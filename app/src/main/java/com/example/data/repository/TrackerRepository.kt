package com.example.data.repository

import com.example.data.database.AppDatabase
import com.example.data.model.Badge
import com.example.data.model.BadgeCategory
import com.example.data.model.CravingEvent
import com.example.data.model.DailyCheckIn
import com.example.data.model.PersonalReward
import com.example.data.model.QuestItem
import com.example.data.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TrackerRepository(private val db: AppDatabase) {

    private val userProfileDao = db.userProfileDao()
    private val badgeDao = db.badgeDao()
    private val checkInDao = db.dailyCheckInDao()
    private val cravingDao = db.cravingDao()
    private val questDao = db.questDao()
    private val rewardDao = db.personalRewardDao()

    // Shared flow to broadcast celebratory newly unlocked badges to UI
    private val _badgeUnlockedEvent = MutableSharedFlow<Badge>(extraBufferCapacity = 5)
    val badgeUnlockedEvent = _badgeUnlockedEvent.asSharedFlow()

    val userProfile: Flow<UserProfile?> = userProfileDao.getUserProfile()
    val allBadges: Flow<List<Badge>> = badgeDao.getAllBadges()
    val unlockedBadges: Flow<List<Badge>> = badgeDao.getUnlockedBadges()
    val allCheckIns: Flow<List<DailyCheckIn>> = checkInDao.getAllCheckIns()
    val allCravings: Flow<List<CravingEvent>> = cravingDao.getAllCravings()
    val allQuests: Flow<List<QuestItem>> = questDao.getAllQuests()
    val allRewards: Flow<List<PersonalReward>> = rewardDao.getAllRewards()

    suspend fun initializeIfNeeded() = withContext(Dispatchers.IO) {
        val existingProfile = userProfileDao.getUserProfileOnce()
        if (existingProfile == null) {
            // Default quit timestamp: set to exactly when user started (or 0 days)
            userProfileDao.insertOrUpdate(
                UserProfile(
                    id = 1,
                    quitTimestamp = System.currentTimeMillis() - (1000L * 60 * 60 * 2), // 2 hours ago by default so counter immediately ticks
                    hoursPerDayGaming = 4.0f,
                    dailyGamingSpendDollars = 3.50f,
                    monthlySpendDollars = 105.0f
                )
            )
        }
        badgeDao.insertBadges(AppDatabase.getInitialBadges())
        questDao.insertQuests(AppDatabase.getInitialQuests())

        // Seed rewards if empty
        val currentRewards = rewardDao.getAllRewards()
        // We insert seed rewards
        AppDatabase.getInitialRewards().forEach {
            rewardDao.insertReward(it)
        }

        // Run an initial badge milestone check
        checkAndAwardMilestones()
    }

    suspend fun updateUserProfile(profile: UserProfile) = withContext(Dispatchers.IO) {
        userProfileDao.insertOrUpdate(profile)
        checkAndAwardMilestones()
    }

    suspend fun updateQuitTimestamp(timestamp: Long) = withContext(Dispatchers.IO) {
        val current = userProfileDao.getUserProfileOnce() ?: UserProfile()
        userProfileDao.insertOrUpdate(current.copy(quitTimestamp = timestamp))
        checkAndAwardMilestones()
    }

    suspend fun resetStreak(reason: String = "") = withContext(Dispatchers.IO) {
        val current = userProfileDao.getUserProfileOnce() ?: UserProfile()
        userProfileDao.insertOrUpdate(current.copy(quitTimestamp = System.currentTimeMillis()))
    }

    suspend fun checkAndAwardMilestones() = withContext(Dispatchers.IO) {
        val profile = userProfileDao.getUserProfileOnce() ?: return@withContext
        val now = System.currentTimeMillis()
        val diffMillis = (now - profile.quitTimestamp).coerceAtLeast(0L)
        val streakDays = (diffMillis / (1000.0 * 60 * 60 * 24)).toFloat()
        val streakHours = (diffMillis / (1000.0 * 60 * 60)).toFloat()
        val hoursSaved = (streakHours / 24.0f) * profile.hoursPerDayGaming
        val moneySaved = streakDays * profile.dailyGamingSpendDollars

        // For count, we check badges
        val initialBadges = AppDatabase.getInitialBadges()
        for (badgeTemplate in initialBadges) {
            val existing = badgeDao.getBadgeById(badgeTemplate.id)
            if (existing != null && existing.unlockedAt == null) {
                var shouldUnlock = false
                when (existing.category) {
                    BadgeCategory.STREAK_DAYS -> {
                        if (streakDays >= existing.requiredThreshold) {
                            shouldUnlock = true
                        }
                    }
                    BadgeCategory.HOURS_SAVED -> {
                        if (hoursSaved >= existing.requiredThreshold) {
                            shouldUnlock = true
                        }
                    }
                    BadgeCategory.MONEY_SAVED -> {
                        if (moneySaved >= existing.requiredThreshold) {
                            shouldUnlock = true
                        }
                    }
                    else -> {}
                }

                if (shouldUnlock) {
                    val updated = existing.copy(unlockedAt = now)
                    badgeDao.updateBadge(updated)
                    addXp(updated.xpReward)
                    _badgeUnlockedEvent.tryEmit(updated)
                }
            }
        }
    }

    suspend fun logDailyCheckIn(
        feeling: String = "Balanced",
        feelingEmoji: String = "🙂",
        identifiedTriggers: List<String> = emptyList(),
        urgeLevel: Int = 1,
        moodLevel: Int = 3,
        activities: List<String> = emptyList(),
        notes: String = ""
    ) = withContext(Dispatchers.IO) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val profile = userProfileDao.getUserProfileOnce()
        val now = System.currentTimeMillis()
        val streakDays = if (profile != null) {
            ((now - profile.quitTimestamp).coerceAtLeast(0L) / 86400000L) + 1
        } else 1L

        val checkIn = DailyCheckIn(
            dateIso = today,
            timestamp = now,
            dayNumber = streakDays,
            feeling = feeling,
            feelingEmoji = feelingEmoji,
            identifiedTriggers = identifiedTriggers.joinToString(", "),
            urgeLevel = urgeLevel,
            moodLevel = moodLevel,
            activities = activities.joinToString(", "),
            notes = notes
        )
        checkInDao.insertCheckIn(checkIn)
        addXp(60) // Check-in reward XP
        checkAndAwardMilestones()
    }

    suspend fun logCravingSurfed(
        trigger: String,
        intensity: Int,
        strategy: String,
        note: String
    ) = withContext(Dispatchers.IO) {
        val event = CravingEvent(
            timestamp = System.currentTimeMillis(),
            trigger = trigger,
            intensity = intensity,
            strategyUsed = strategy,
            successful = true,
            note = note
        )
        cravingDao.insertCraving(event)
        addXp(75) // Urge surfed reward XP

        // Check Craving Badges
        val firstUrge = badgeDao.getBadgeById("urge_first")
        if (firstUrge != null && firstUrge.unlockedAt == null) {
            val updated = firstUrge.copy(unlockedAt = System.currentTimeMillis())
            badgeDao.updateBadge(updated)
            addXp(updated.xpReward)
            _badgeUnlockedEvent.tryEmit(updated)
        }
    }

    suspend fun completeQuest(quest: QuestItem) = withContext(Dispatchers.IO) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val updated = quest.copy(
            isCompletedToday = true,
            lastCompletedDate = today,
            completionCount = quest.completionCount + 1
        )
        questDao.updateQuest(updated)
        addXp(quest.xpReward)
    }

    suspend fun resetQuestsIfNewDay() = withContext(Dispatchers.IO) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        // In real app, uncheck if lastCompletedDate != today
    }

    suspend fun addCustomQuest(title: String, category: String, xp: Int) = withContext(Dispatchers.IO) {
        questDao.insertQuest(
            QuestItem(
                title = title,
                category = category,
                xpReward = xp,
                isCustom = true
            )
        )
    }

    suspend fun addPersonalReward(title: String, targetDays: Int, cost: Float, notes: String) = withContext(Dispatchers.IO) {
        rewardDao.insertReward(
            PersonalReward(
                title = title,
                targetDays = targetDays,
                estimatedCost = cost,
                notes = notes
            )
        )
    }

    suspend fun toggleRewardClaimed(reward: PersonalReward) = withContext(Dispatchers.IO) {
        rewardDao.updateReward(reward.copy(isClaimed = !reward.isClaimed))
    }

    private suspend fun addXp(amount: Int) {
        val profile = userProfileDao.getUserProfileOnce() ?: return
        val newXp = profile.totalXp + amount
        val newLevel = calculateLevel(newXp)
        userProfileDao.insertOrUpdate(profile.copy(totalXp = newXp, level = newLevel))
    }

    private fun calculateLevel(xp: Int): Int {
        // Level curve: Level 1: 0, Level 2: 200, Level 3: 500, Level 4: 900, Level 5: 1400...
        var level = 1
        var threshold = 200
        var currentReq = 200
        while (xp >= threshold) {
            level++
            currentReq += 150
            threshold += currentReq
        }
        return level
    }
}
