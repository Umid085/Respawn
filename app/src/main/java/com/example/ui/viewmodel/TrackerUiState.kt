package com.example.ui.viewmodel

import com.example.data.model.Badge
import com.example.data.model.CravingEvent
import com.example.data.model.DailyCheckIn
import com.example.data.model.PersonalReward
import com.example.data.model.QuestItem
import com.example.data.model.UserProfile

data class SobrietyTime(
    val totalSeconds: Long = 0,
    val days: Long = 0,
    val hours: Long = 0,
    val minutes: Long = 0,
    val seconds: Long = 0,
    val hoursReclaimed: Float = 0f,
    val moneySaved: Float = 0f,
    val dailyGamingSpend: Float = 3.50f,
    val gamesAvoidedEquivalent: Float = 0f,
    val subscriptionsAvoidedEquivalent: Float = 0f,
    val booksEquivalent: Float = 0f,
    val workoutsEquivalent: Int = 0
)

data class DopamineStage(
    val stageNumber: Int,
    val title: String,
    val dayRange: String,
    val status: String,
    val progress: Float, // 0.0 to 1.0
    val neurobiologyNote: String
)

data class TrackerUiState(
    val isLoading: Boolean = true,
    val profile: UserProfile = UserProfile(),
    val sobrietyTime: SobrietyTime = SobrietyTime(),
    val currentDopamineStage: DopamineStage = DopamineStage(
        stageNumber = 1,
        title = "Acute Detox",
        dayRange = "Days 1–3",
        status = "Active",
        progress = 0.3f,
        neurobiologyNote = "Peak restlessness. Dopamine receptors signaling for rapid hyperstimulation."
    ),
    val nextBadge: Badge? = null,
    val nextBadgeDaysRemaining: Float = 0f,
    val allBadges: List<Badge> = emptyList(),
    val unlockedBadges: List<Badge> = emptyList(),
    val checkIns: List<DailyCheckIn> = emptyList(),
    val cravings: List<CravingEvent> = emptyList(),
    val quests: List<QuestItem> = emptyList(),
    val rewards: List<PersonalReward> = emptyList(),
    val newlyUnlockedBadge: Badge? = null, // For pop-up celebration
    val dailyCheckInNotificationEnabled: Boolean = true,
    val accomplishmentsNotificationEnabled: Boolean = true,
    val availableUpdate: com.example.updater.AppReleaseInfo? = null,
    val isCheckingForUpdate: Boolean = false,
    val isDownloadingUpdate: Boolean = false,
    val updateDownloadProgress: Int = 0,
    val updateCheckStatusMessage: String? = null,
    val targetGitHubRepo: String = "peersalexandr/respawn"
)
