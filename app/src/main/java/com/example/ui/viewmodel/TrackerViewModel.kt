package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.Badge
import com.example.data.model.BadgeCategory
import com.example.data.model.QuestItem
import android.content.Context
import com.example.data.model.UserProfile
import com.example.data.repository.TrackerRepository
import com.example.notification.NotificationHelper
import com.example.updater.AppUpdateManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TrackerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TrackerRepository

    private val _uiState = MutableStateFlow(TrackerUiState())
    val uiState: StateFlow<TrackerUiState> = _uiState.asStateFlow()

    init {
        val database = AppDatabase.getInstance(application)
        repository = TrackerRepository(database)

        NotificationHelper.createNotificationChannels(application)
        val dailyEnabled = NotificationHelper.isDailyCheckInEnabled(application)
        val accomEnabled = NotificationHelper.isAccomplishmentsEnabled(application)
        val repo = AppUpdateManager.getTargetRepository(application)
        val prefs = application.getSharedPreferences("respawn_settings", Context.MODE_PRIVATE)
        val savedLangCode = prefs.getString("app_language", "ru") ?: "ru"
        val initialLang = com.example.ui.i18n.AppLanguage.values().firstOrNull { it.code == savedLangCode }
            ?: com.example.ui.i18n.AppLanguage.RUSSIAN

        _uiState.update {
            it.copy(
                dailyCheckInNotificationEnabled = dailyEnabled,
                accomplishmentsNotificationEnabled = accomEnabled,
                targetGitHubRepo = repo,
                appLanguage = initialLang
            )
        }
        if (dailyEnabled) {
            NotificationHelper.scheduleDailyReminder(application)
        }

        viewModelScope.launch {
            repository.initializeIfNeeded()
            checkForUpdates(silent = true)
        }

        // Collect repository flows
        viewModelScope.launch {
            repository.userProfile.collect { profile ->
                val currentProfile = profile ?: UserProfile()
                _uiState.update { current ->
                    val (sobriety, stage, nextBadge, remainingDays) = calculateMetrics(currentProfile, current.allBadges, current.appLanguage)
                    current.copy(
                        isLoading = false,
                        profile = currentProfile,
                        sobrietyTime = sobriety,
                        currentDopamineStage = stage,
                        nextBadge = nextBadge,
                        nextBadgeDaysRemaining = remainingDays
                    )
                }
            }
        }

        viewModelScope.launch {
            repository.allBadges.collect { badges ->
                _uiState.update { current ->
                    val (sobriety, stage, nextBadge, remainingDays) = calculateMetrics(current.profile, badges, current.appLanguage)
                    current.copy(
                        allBadges = badges,
                        nextBadge = nextBadge,
                        nextBadgeDaysRemaining = remainingDays
                    )
                }
            }
        }

        viewModelScope.launch {
            repository.unlockedBadges.collect { unlocked ->
                _uiState.update { it.copy(unlockedBadges = unlocked) }
            }
        }

        viewModelScope.launch {
            repository.allCheckIns.collect { checkIns ->
                _uiState.update { it.copy(checkIns = checkIns) }
            }
        }

        viewModelScope.launch {
            repository.allCravings.collect { cravings ->
                _uiState.update { it.copy(cravings = cravings) }
            }
        }

        viewModelScope.launch {
            repository.allQuests.collect { quests ->
                _uiState.update { it.copy(quests = quests) }
            }
        }

        viewModelScope.launch {
            repository.allRewards.collect { rewards ->
                _uiState.update { it.copy(rewards = rewards) }
            }
        }

        // Collect newly unlocked badge events for celebration modal & notification
        viewModelScope.launch {
            repository.badgeUnlockedEvent.collect { badge ->
                _uiState.update { it.copy(newlyUnlockedBadge = badge) }
                NotificationHelper.showAccomplishmentNotification(
                    context = getApplication(),
                    badgeTitle = badge.title,
                    badgeDescription = badge.description
                )
            }
        }

        // Live 1-second ticking timer for real-time second updates
        viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                val currentProfile = _uiState.value.profile
                val badges = _uiState.value.allBadges
                if (!_uiState.value.isLoading) {
                    val lang = _uiState.value.appLanguage
                    val (sobriety, stage, nextBadge, remainingDays) = calculateMetrics(currentProfile, badges, lang)
                    _uiState.update {
                        it.copy(
                            sobrietyTime = sobriety,
                            currentDopamineStage = stage,
                            nextBadge = nextBadge,
                            nextBadgeDaysRemaining = remainingDays
                        )
                    }
                    // Periodically check milestones every minute
                    if (sobriety.seconds == 0L) {
                        repository.checkAndAwardMilestones()
                    }
                }
            }
        }
    }

    private fun calculateMetrics(
        profile: UserProfile,
        badges: List<Badge>,
        language: com.example.ui.i18n.AppLanguage
    ): Quadruple<SobrietyTime, DopamineStage, Badge?, Float> {
        val now = System.currentTimeMillis()
        val elapsedMillis = (now - profile.quitTimestamp).coerceAtLeast(0L)
        val totalSeconds = elapsedMillis / 1000L

        val days = totalSeconds / 86400L
        val hours = (totalSeconds % 86400L) / 3600L
        val minutes = (totalSeconds % 3600L) / 60L
        val seconds = totalSeconds % 60L

        val totalHoursElapsed = totalSeconds / 3600.0f
        val hoursReclaimed = (totalHoursElapsed / 24.0f) * profile.hoursPerDayGaming
        val daysReclaimed = totalSeconds / 86400.0f
        val dailySpend = profile.dailyGamingSpendDollars
        val moneySaved = daysReclaimed * dailySpend
        val gamesAvoidedEquivalent = moneySaved / 70.0f
        val subscriptionsAvoidedEquivalent = moneySaved / 15.0f

        val booksEquivalent = hoursReclaimed / 6.0f // ~6 hours per book
        val workoutsEquivalent = hoursReclaimed.toInt() // 1 hr per workout

        val sobriety = SobrietyTime(
            totalSeconds = totalSeconds,
            days = days,
            hours = hours,
            minutes = minutes,
            seconds = seconds,
            hoursReclaimed = hoursReclaimed,
            moneySaved = moneySaved,
            dailyGamingSpend = dailySpend,
            gamesAvoidedEquivalent = gamesAvoidedEquivalent,
            subscriptionsAvoidedEquivalent = subscriptionsAvoidedEquivalent,
            booksEquivalent = booksEquivalent,
            workoutsEquivalent = workoutsEquivalent
        )

        val stage = calculateDopamineStage(days.toInt(), language)

        // Find next locked streak badge
        val lockedStreakBadges = badges
            .filter { it.category == BadgeCategory.STREAK_DAYS && it.unlockedAt == null }
            .sortedBy { it.requiredThreshold }

        val nextBadge = lockedStreakBadges.firstOrNull()
        val remainingDays = if (nextBadge != null) {
            (nextBadge.requiredThreshold - (totalSeconds / 86400.0f)).coerceAtLeast(0f)
        } else 0f

        return Quadruple(sobriety, stage, nextBadge, remainingDays)
    }

    private fun calculateDopamineStage(days: Int, language: com.example.ui.i18n.AppLanguage): DopamineStage {
        val isRu = language == com.example.ui.i18n.AppLanguage.RUSSIAN || 
            (language == com.example.ui.i18n.AppLanguage.SYSTEM && java.util.Locale.getDefault().language.lowercase().let { it == "ru" || it == "be" || it == "uk" || it == "kk" })

        return when {
            days < 3 -> DopamineStage(
                stageNumber = 1,
                title = if (isRu) "Острая ломка" else "The Detox Shock",
                dayRange = if (isRu) "Дни 1–3" else "Days 1–3",
                status = if (isRu) "Острая фаза" else "Acute Phase",
                progress = (days / 3f).coerceIn(0.1f, 1f),
                neurobiologyNote = if (isRu) "Пик беспокойства и тяги. Дофаминовые рецепторы фиксируют резкий спад сверхстимуляции. Пейте воду, дышите и держитесь." else "Peak restlessness & phantom urges. Dopamine receptors notice the sudden drop in hyperstimulation. Stay hydrated and breathe."
            )
            days < 7 -> DopamineStage(
                stageNumber = 2,
                title = if (isRu) "Дофаминовый сдвиг" else "Dopamine Shift",
                dayRange = if (isRu) "Дни 4–7" else "Days 4–7",
                status = if (isRu) "Стабилизация" else "Stabilizing",
                progress = ((days - 3) / 4f).coerceIn(0.1f, 1f),
                neurobiologyNote = if (isRu) "Фаза быстрого сна (REM) восстанавливается, туман в голове рассеивается. Физическая суета сменяется спокойной энергией." else "REM sleep rebounds and morning brain fog starts clearing. Physical fidgeting subsides into grounded energy."
            )
            days < 21 -> DopamineStage(
                stageNumber = 3,
                title = if (isRu) "Нейропластическое обновление" else "Neuroplastic Rewiring",
                dayRange = if (isRu) "Дни 8–21" else "Days 8–21",
                status = if (isRu) "Восстановление" else "Rebuilding",
                progress = ((days - 7) / 14f).coerceIn(0.1f, 1f),
                neurobiologyNote = if (isRu) "Префронтальная кора укрепляет самоконтроль. Простые ежедневные радости (прогулки, чтение) начинают приносить удовольствие." else "Prefrontal cortex strengthens executive control. Subtle daily pleasures like walking and reading begin to feel rewarding."
            )
            days < 60 -> DopamineStage(
                stageNumber = 4,
                title = if (isRu) "Естественная чувствительность" else "Natural Sensitization",
                dayRange = if (isRu) "Дни 22–60" else "Days 22–60",
                status = if (isRu) "Расцвет" else "Flourishing",
                progress = ((days - 21) / 39f).coerceIn(0.1f, 1f),
                neurobiologyNote = if (isRu) "Плотность рецепторов D2 восстанавливается. Возвращается глубокая концентрация, терпение и искренний интерес к реальным проектам." else "D2 receptor density recovers significantly. Deep focus, patience, and genuine motivation for real-world projects return."
            )
            days < 100 -> DopamineStage(
                stageNumber = 5,
                title = if (isRu) "Когнитивное мастерство" else "Cognitive Mastery",
                dayRange = if (isRu) "Дни 61–100" else "Days 61–100",
                status = if (isRu) "Мастерство" else "Mastery",
                progress = ((days - 60) / 40f).coerceIn(0.1f, 1f),
                neurobiologyNote = if (isRu) "Тяга к играм становится редкой и легко преодолимой. Эмоциональная стойкость и удовлетворение жизнью достигают пика." else "Urges are now rare and easily dismissed. Emotional resilience and real-life satisfaction reach peak levels."
            )
            else -> DopamineStage(
                stageNumber = 6,
                title = if (isRu) "Полное перерождение" else "Total Sovereign Rebirth",
                dayRange = if (isRu) "100+ Дней" else "100+ Days",
                status = if (isRu) "Свобода" else "Transcended",
                progress = 1.0f,
                neurobiologyNote = if (isRu) "Полный гомеостаз мозга. Вы больше не ищете спасения в пикселях — реальный мир стал вашей главной ареной побед." else "Complete brain homeostasis. You no longer view video games as an escape; your real life is your playground."
            )
        }
    }

    fun dismissUnlockedBadge() {
        _uiState.update { it.copy(newlyUnlockedBadge = null) }
    }

    fun logDailyCheckIn(
        feeling: String,
        feelingEmoji: String,
        identifiedTriggers: List<String>,
        urge: Int,
        mood: Int,
        activities: List<String>,
        notes: String
    ) {
        viewModelScope.launch {
            repository.logDailyCheckIn(
                feeling = feeling,
                feelingEmoji = feelingEmoji,
                identifiedTriggers = identifiedTriggers,
                urgeLevel = urge,
                moodLevel = mood,
                activities = activities,
                notes = notes
            )
        }
    }

    fun logCravingSurfed(trigger: String, intensity: Int, strategy: String, note: String) {
        viewModelScope.launch {
            repository.logCravingSurfed(trigger, intensity, strategy, note)
        }
    }

    fun completeQuest(quest: QuestItem) {
        viewModelScope.launch {
            repository.completeQuest(quest)
        }
    }

    fun addCustomQuest(title: String, category: String, xp: Int) {
        viewModelScope.launch {
            repository.addCustomQuest(title, category, xp)
        }
    }

    fun addPersonalReward(title: String, days: Int, cost: Float, notes: String) {
        viewModelScope.launch {
            repository.addPersonalReward(title, days, cost, notes)
        }
    }

    fun toggleRewardClaimed(reward: com.example.data.model.PersonalReward) {
        viewModelScope.launch {
            repository.toggleRewardClaimed(reward)
        }
    }

    fun updateQuitTimestamp(timestamp: Long) {
        viewModelScope.launch {
            repository.updateQuitTimestamp(timestamp)
        }
    }

    fun resetStreak(reason: String) {
        viewModelScope.launch {
            repository.resetStreak(reason)
        }
    }

    fun updateDailyGamingExpenditure(dailySpend: Float) {
        viewModelScope.launch {
            val current = _uiState.value.profile
            val updated = current.copy(
                dailyGamingSpendDollars = dailySpend,
                monthlySpendDollars = dailySpend * 30f
            )
            repository.updateUserProfile(updated)
        }
    }

    fun updateProfileSettings(
        hoursPerDay: Float,
        dailySpend: Float,
        monthlySpend: Float,
        whyStatement: String,
        genre: String
    ) {
        viewModelScope.launch {
            val current = _uiState.value.profile
            val updated = current.copy(
                hoursPerDayGaming = hoursPerDay,
                dailyGamingSpendDollars = dailySpend,
                monthlySpendDollars = monthlySpend,
                whyStatement = whyStatement,
                primaryGameGenre = genre
            )
            repository.updateUserProfile(updated)
        }
    }

    fun setDailyCheckInNotification(enabled: Boolean) {
        NotificationHelper.setDailyCheckInEnabled(getApplication(), enabled)
        _uiState.update { it.copy(dailyCheckInNotificationEnabled = enabled) }
    }

    fun setAccomplishmentsNotification(enabled: Boolean) {
        NotificationHelper.setAccomplishmentsEnabled(getApplication(), enabled)
        _uiState.update { it.copy(accomplishmentsNotificationEnabled = enabled) }
    }

    fun sendTestDailyCheckInNotification() {
        val days = _uiState.value.sobrietyTime.days.coerceAtLeast(1L)
        NotificationHelper.showDailyCheckInNotification(getApplication(), days)
    }

    fun sendTestAccomplishmentNotification() {
        val featured = _uiState.value.allBadges.firstOrNull { it.id == "badge_7d" }
            ?: _uiState.value.allBadges.firstOrNull()
        val title = featured?.title ?: "First Week Clean"
        val desc = featured?.description ?: "7 days gaming-free! Real neurotransmitter reset underway."
        NotificationHelper.showAccomplishmentNotification(getApplication(), title, desc)
    }

    fun checkForUpdates(silent: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCheckingForUpdate = true, updateCheckStatusMessage = null) }
            val result = AppUpdateManager.checkForUpdates(getApplication(), currentVersionCode = 1)
            result.onSuccess { releaseInfo ->
                _uiState.update {
                    it.copy(
                        isCheckingForUpdate = false,
                        availableUpdate = if (releaseInfo.hasUpdate) releaseInfo else null,
                        updateCheckStatusMessage = if (releaseInfo.hasUpdate) {
                            "Update available: ${releaseInfo.latestVersionName}"
                        } else {
                            if (!silent) "You're on the newest version (v1.0)." else null
                        }
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isCheckingForUpdate = false,
                        updateCheckStatusMessage = if (!silent) "Could not check updates: ${error.localizedMessage ?: "Network error"}" else null
                    )
                }
            }
        }
    }

    fun downloadAndInstallUpdate(context: Context) {
        val update = _uiState.value.availableUpdate ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isDownloadingUpdate = true, updateDownloadProgress = 0) }
            val downloadResult = AppUpdateManager.downloadApk(context, update.downloadUrl) { progress ->
                _uiState.update { it.copy(updateDownloadProgress = progress) }
            }
            _uiState.update { it.copy(isDownloadingUpdate = false) }
            downloadResult.onSuccess { apkFile ->
                val installed = AppUpdateManager.installApk(context, apkFile)
                if (installed) {
                    _uiState.update { it.copy(availableUpdate = null) }
                }
            }.onFailure { error ->
                _uiState.update { it.copy(updateCheckStatusMessage = "Download failed: ${error.localizedMessage}") }
            }
        }
    }

    fun dismissUpdateDialog() {
        _uiState.update { it.copy(availableUpdate = null) }
    }

    fun setTargetGitHubRepo(repo: String) {
        AppUpdateManager.setTargetRepository(getApplication(), repo)
        _uiState.update { it.copy(targetGitHubRepo = repo) }
    }

    fun setAppLanguage(lang: com.example.ui.i18n.AppLanguage) {
        val prefs = getApplication<Application>().getSharedPreferences("respawn_settings", Context.MODE_PRIVATE)
        prefs.edit().putString("app_language", lang.code).apply()
        _uiState.update { current ->
            val (sobriety, stage, nextBadge, remainingDays) = calculateMetrics(current.profile, current.allBadges, lang)
            current.copy(
                appLanguage = lang,
                currentDopamineStage = stage,
                sobrietyTime = sobriety,
                nextBadge = nextBadge,
                nextBadgeDaysRemaining = remainingDays
            )
        }
    }
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
