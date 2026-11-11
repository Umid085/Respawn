package com.example.notification

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import java.util.Calendar

object NotificationHelper {

    const val CHANNEL_DAILY_CHECKIN = "channel_daily_checkin"
    const val CHANNEL_ACCOMPLISHMENTS = "channel_accomplishments"

    const val ACTION_DAILY_CHECKIN_ALARM = "com.aistudio.respawn.ACTION_DAILY_CHECKIN"
    const val ACTION_OPEN_CHECKIN = "com.aistudio.respawn.ACTION_OPEN_CHECKIN"
    const val ACTION_OPEN_BADGES = "com.aistudio.respawn.ACTION_OPEN_BADGES"

    private const val NOTIFICATION_ID_CHECKIN = 1001
    private const val NOTIFICATION_ID_ACCOMPLISHMENT = 2001
    private const val PREFS_NAME = "respawn_notification_prefs"
    private const val KEY_DAILY_CHECKIN_ENABLED = "daily_checkin_enabled"
    private const val KEY_ACCOMPLISHMENTS_ENABLED = "accomplishments_enabled"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // 1. Daily Check-In Channel
            val checkInChannel = NotificationChannel(
                CHANNEL_DAILY_CHECKIN,
                "Daily Recovery Check-In",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Daily evening reminder to reflect, log feelings, and identify cravings/triggers."
                enableVibration(true)
                setShowBadge(true)
            }

            // 2. Accomplishments & Milestones Channel
            val accomplishmentChannel = NotificationChannel(
                CHANNEL_ACCOMPLISHMENTS,
                "Milestones & Accomplishments",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Celebrations and trophy alerts when reaching clean sobriety milestones."
                enableVibration(true)
                setShowBadge(true)
            }

            notificationManager.createNotificationChannel(checkInChannel)
            notificationManager.createNotificationChannel(accomplishmentChannel)
        }
    }

    fun isDailyCheckInEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_DAILY_CHECKIN_ENABLED, true)
    }

    fun setDailyCheckInEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_DAILY_CHECKIN_ENABLED, enabled).apply()
        if (enabled) {
            scheduleDailyReminder(context)
        } else {
            cancelDailyReminder(context)
        }
    }

    fun isAccomplishmentsEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_ACCOMPLISHMENTS_ENABLED, true)
    }

    fun setAccomplishmentsEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_ACCOMPLISHMENTS_ENABLED, enabled).apply()
    }

    private fun isRussian(context: Context): Boolean {
        val prefs = context.getSharedPreferences("respawn_settings", Context.MODE_PRIVATE)
        val code = prefs.getString("app_language", "ru") ?: "ru"
        return if (code == "system") {
            java.util.Locale.getDefault().language.lowercase().let { it == "ru" || it == "be" || it == "uk" || it == "kk" }
        } else {
            code == "ru"
        }
    }

    fun showDailyCheckInNotification(context: Context, dayCount: Long = 1) {
        if (!isDailyCheckInEnabled(context)) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            action = ACTION_OPEN_CHECKIN
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val isRu = isRussian(context)
        val title = if (isRu) "⚔️ Дневник трезвости: День $dayCount" else "⚔️ Day $dayCount Check-In Ready"
        val message = if (isRu) "Уделите минуту, чтобы зафиксировать состояние, победить тягу и укрепить трезвость." else "Take 60 seconds to log how you feel and identify today's triggers to protect your sobriety."

        val notification = NotificationCompat.Builder(context, CHANNEL_DAILY_CHECKIN)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setColor(0xFF10B981.toInt()) // EmeraldClean
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_CHECKIN, notification)
    }

    fun showAccomplishmentNotification(context: Context, badgeTitle: String, badgeDescription: String) {
        if (!isAccomplishmentsEnabled(context)) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            action = ACTION_OPEN_BADGES
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            badgeTitle.hashCode(),
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val isRu = isRussian(context)
        val title = if (isRu) "🏆 Достижение: $badgeTitle" else "🏆 Accomplishment: $badgeTitle"
        val message = if (isRu) "$badgeDescription Вы возвращаете контроль над своей реальной жизнью!" else "$badgeDescription You are taking back control of your real life!"

        val notification = NotificationCompat.Builder(context, CHANNEL_ACCOMPLISHMENTS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setColor(0xFFF59E0B.toInt()) // AmberGold
            .build()

        val notificationId = NOTIFICATION_ID_ACCOMPLISHMENT + (badgeTitle.hashCode() % 1000)
        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }

    fun scheduleDailyReminder(context: Context, hour: Int = 20, minute: Int = 0) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = ACTION_DAILY_CHECKIN_ALARM
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            101,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        } catch (_: SecurityException) {
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    fun cancelDailyReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = ACTION_DAILY_CHECKIN_ALARM
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            101,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
