package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.Badge
import com.example.data.model.BadgeCategory
import com.example.data.model.QuestItem
import com.example.data.model.UserProfile
import com.example.data.repository.TrackerRepository
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

        viewModelScope.launch {
            repository.initializeIfNeeded()
        }

        // Collect repository flows
        viewModelScope.launch {
            repository.userProfile.collect { profile ->
                val currentProfile = profile ?: UserProfile()
                _uiState.update { current ->
                    val (sobriety, stage, nextBadge, remainingDays) = calculateMetrics(currentProfile, current.allBadges)
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
                    val (sobriety, stage, nextBadge, remainingDays) = calculateMetrics(current.profile, badges)
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

        // Collect newly unlocked badge events for celebration modal
        viewModelScope.launch {
            repository.badgeUnlockedEvent.collect { badge ->
                _uiState.update { it.copy(newlyUnlockedBadge = badge) }
            }
        }

        // Live 1-second ticking timer for real-time second updates
        viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                val currentProfile = _uiState.value.profile
                val badges = _uiState.value.allBadges
                if (!_uiState.value.isLoading) {
                    val (sobriety, stage, nextBadge, remainingDays) = calculateMetrics(currentProfile, badges)
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
        badges: List<Badge>
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

        val stage = calculateDopamineStage(days.toInt())

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

    private fun calculateDopamineStage(days: Int): DopamineStage {
        return when {
            days < 3 -> DopamineStage(
                stageNumber = 1,
                title = "The Detox Shock",
                dayRange = "Days 1–3",
                status = "Acute Phase",
                progress = (days / 3f).coerceIn(0.1f, 1f),
                neurobiologyNote = "Peak restlessness & phantom urges. Dopamine receptors notice the sudden drop in hyperstimulation. Stay hydrated and breathe."
            )
            days < 7 -> DopamineStage(
                stageNumber = 2,
                title = "Dopamine Shift",
                dayRange = "Days 4–7",
                status = "Stabilizing",
                progress = ((days - 3) / 4f).coerceIn(0.1f, 1f),
                neurobiologyNote = "REM sleep rebounds and morning brain fog starts clearing. Physical fidgeting subsides into grounded energy."
            )
            days < 21 -> DopamineStage(
                stageNumber = 3,
                title = "Neuroplastic Rewiring",
                dayRange = "Days 8–21",
                status = "Rebuilding",
                progress = ((days - 7) / 14f).coerceIn(0.1f, 1f),
                neurobiologyNote = "Prefrontal cortex strengthens executive control. Subtle daily pleasures like walking and reading begin to feel rewarding."
            )
            days < 60 -> DopamineStage(
                stageNumber = 4,
                title = "Natural Sensitization",
                dayRange = "Days 22–60",
                status = "Flourishing",
                progress = ((days - 21) / 39f).coerceIn(0.1f, 1f),
                neurobiologyNote = "D2 receptor density recovers significantly. Deep focus, patience, and genuine motivation for real-world projects return."
            )
            days < 100 -> DopamineStage(
                stageNumber = 5,
                title = "Cognitive Mastery",
                dayRange = "Days 61–100",
                status = "Mastery",
                progress = ((days - 60) / 40f).coerceIn(0.1f, 1f),
                neurobiologyNote = "Urges are now rare and easily dismissed. Emotional resilience and real-life satisfaction reach peak levels."
            )
            else -> DopamineStage(
                stageNumber = 6,
                title = "Total Sovereign Rebirth",
                dayRange = "100+ Days",
                status = "Transcended",
                progress = 1.0f,
                neurobiologyNote = "Complete brain homeostasis. You no longer view video games as an escape; your real life is your playground."
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
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
