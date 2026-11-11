package com.example.updater

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

data class AppReleaseInfo(
    val hasUpdate: Boolean,
    val latestVersionCode: Int,
    val latestVersionName: String,
    val downloadUrl: String,
    val releaseNotes: String
)

sealed class UpdateDownloadProgress {
    object Idle : UpdateDownloadProgress()
    data class Downloading(val progressPercent: Int) : UpdateDownloadProgress()
    data class ReadyToInstall(val apkFile: File) : UpdateDownloadProgress()
    data class Error(val message: String) : UpdateDownloadProgress()
}

object AppUpdateManager {

    private const val PREFS_NAME = "respawn_updater_prefs"
    private const val KEY_GITHUB_REPO = "github_repo_owner_name"
    const val DEFAULT_GITHUB_REPO = "peersalexandr/respawn"

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    fun getTargetRepository(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_GITHUB_REPO, DEFAULT_GITHUB_REPO) ?: DEFAULT_GITHUB_REPO
    }

    fun setTargetRepository(context: Context, repo: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_GITHUB_REPO, repo.trim()).apply()
    }

    /**
     * Checks GitHub latest release for the target repository.
     */
    suspend fun checkForUpdates(
        context: Context,
        currentVersionCode: Int = 1
    ): Result<AppReleaseInfo> = withContext(Dispatchers.IO) {
        try {
            val repo = getTargetRepository(context)
            val apiUrl = "https://api.github.com/repos/$repo/releases/latest"

            val request = Request.Builder()
                .url(apiUrl)
                .header("Accept", "application/vnd.github.v3+json")
                .header("User-Agent", "Respawn-App-Updater")
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                // If repo not yet public or has no releases yet, provide clean response
                return@withContext Result.success(
                    AppReleaseInfo(
                        hasUpdate = false,
                        latestVersionCode = currentVersionCode,
                        latestVersionName = "v1.0",
                        downloadUrl = "",
                        releaseNotes = "You are currently running the newest build (Version 1.0)."
                    )
                )
            }

            val bodyString = response.body?.string() ?: ""
            val json = JSONObject(bodyString)

            val tagName = json.optString("tag_name", "v1.0")
            val releaseNotes = json.optString("body", "Bug fixes and performance improvements.")

            // Parse version number from tag (e.g., "v1.1" -> 2, "v2.0" -> 3)
            val parsedVersionCode = parseVersionCodeFromTag(tagName)

            // Look for APK in assets
            var apkDownloadUrl = ""
            val assets = json.optJSONArray("assets")
            if (assets != null) {
                for (i in 0 until assets.length()) {
                    val asset = assets.getJSONObject(i)
                    val name = asset.optString("name", "")
                    if (name.endsWith(".apk", ignoreCase = true)) {
                        apkDownloadUrl = asset.optString("browser_download_url", "")
                        break
                    }
                }
            }

            val hasUpdate = parsedVersionCode > currentVersionCode && apkDownloadUrl.isNotBlank()

            Result.success(
                AppReleaseInfo(
                    hasUpdate = hasUpdate,
                    latestVersionCode = parsedVersionCode,
                    latestVersionName = tagName,
                    downloadUrl = apkDownloadUrl,
                    releaseNotes = releaseNotes
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseVersionCodeFromTag(tag: String): Int {
        val clean = tag.removePrefix("v").removePrefix("V").trim()
        val parts = clean.split(".")
        return try {
            if (parts.size >= 2) {
                val major = parts[0].toIntOrNull() ?: 1
                val minor = parts[1].toIntOrNull() ?: 0
                (major * 10) + minor
            } else {
                clean.toIntOrNull() ?: 1
            }
        } catch (_: Exception) {
            1
        }
    }

    /**
     * Downloads APK with live progress tracking and triggers package installation.
     */
    suspend fun downloadApk(
        context: Context,
        downloadUrl: String,
        onProgress: (Int) -> Unit
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(downloadUrl)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Download failed with HTTP ${response.code}"))
            }

            val body = response.body ?: return@withContext Result.failure(Exception("Empty response body"))
            val contentLength = body.contentLength()

            val downloadDir = File(context.cacheDir, "updates").apply { mkdirs() }
            val outputFile = File(downloadDir, "respawn_latest.apk")
            if (outputFile.exists()) outputFile.delete()

            body.byteStream().use { input ->
                FileOutputStream(outputFile).use { output ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    var totalBytesRead = 0L

                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        totalBytesRead += bytesRead
                        if (contentLength > 0) {
                            val percent = ((totalBytesRead * 100) / contentLength).toInt().coerceIn(0, 100)
                            onProgress(percent)
                        }
                    }
                    output.flush()
                }
            }

            Result.success(outputFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Prompts the Android OS package installer to install the downloaded APK.
     */
    fun installApk(context: Context, apkFile: File): Boolean {
        if (!apkFile.exists()) return false

        // Check if unknown app sources permission is granted on Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!context.packageManager.canRequestPackageInstalls()) {
                val settingsIntent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                    data = Uri.parse("package:${context.packageName}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(settingsIntent)
                return false
            }
        }

        val apkUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apkFile
        )

        val installIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        return try {
            context.startActivity(installIntent)
            true
        } catch (_: Exception) {
            false
        }
    }
}
