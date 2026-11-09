package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Badge
import com.example.data.model.QuestItem
import com.example.ui.components.DopamineStageCard
import com.example.ui.components.MoneySavedCard
import com.example.ui.components.ReclaimedMetricsGrid
import com.example.ui.components.StreakHeroCard
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanClarity
import com.example.ui.theme.EmeraldClean
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardElevated
import com.example.ui.theme.SlateDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.TrackerUiState

@Composable
fun DashboardScreen(
    uiState: TrackerUiState,
    onUrgeSurfClick: () -> Unit,
    onCheckInClick: () -> Unit,
    onNavigateToBadges: () -> Unit,
    onNavigateToQuests: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onCompleteQuest: (QuestItem) -> Unit,
    onUpdateDailyExpenditure: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SlateDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Top App Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(listOf(AmberGold.copy(alpha = 0.3f), SlateCardElevated))
                            )
                            .border(1.dp, AmberGold.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Logo",
                            tint = AmberGold,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "RESPAWN",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Game-Free Sovereignty",
                            style = MaterialTheme.typography.labelSmall,
                            color = AmberGold
                        )
                    }
                }

                IconButton(
                    onClick = onNavigateToSettings,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(SlateCardElevated)
                        .testTag("settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Hero Streak Section
        item {
            StreakHeroCard(
                sobrietyTime = uiState.sobrietyTime,
                nextBadge = uiState.nextBadge,
                nextBadgeRemainingDays = uiState.nextBadgeDaysRemaining,
                currentLevel = uiState.profile.level,
                currentXp = uiState.profile.totalXp,
                onUrgeSurfClick = onUrgeSurfClick,
                onCheckInClick = onCheckInClick
            )
        }

        // Dedicated Money Saved from Games & Subscriptions
        item {
            MoneySavedCard(
                moneySaved = uiState.sobrietyTime.moneySaved,
                dailyExpenditureSetting = uiState.profile.dailyGamingSpendDollars,
                gamesAvoided = uiState.sobrietyTime.gamesAvoidedEquivalent,
                subscriptionsAvoided = uiState.sobrietyTime.subscriptionsAvoidedEquivalent,
                onUpdateDailyExpenditure = onUpdateDailyExpenditure
            )
        }

        // Motivational Statistics Grid
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "RECLAIMED LIFE STATISTICS",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                ReclaimedMetricsGrid(sobrietyTime = uiState.sobrietyTime)
            }
        }

        // Dopamine Reset Stage Card
        item {
            DopamineStageCard(
                stage = uiState.currentDopamineStage,
                currentDaysClean = uiState.sobrietyTime.days
            )
        }

        // Featured Milestone Badges Preview
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToBadges() }
                    .testTag("milestone_badges_preview_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Trophy Room",
                                tint = AmberGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Milestone Badges",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${uiState.unlockedBadges.size} / ${uiState.allBadges.size} Unlocked",
                                style = MaterialTheme.typography.labelSmall,
                                color = AmberGold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "View all",
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Row of featured badges
                    val featuredBadges = uiState.allBadges.filter {
                        it.id in listOf("badge_7d", "badge_30d", "badge_100d", "badge_24h")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        featuredBadges.forEach { badge ->
                            FeaturedBadgeMini(
                                badge = badge,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Daily Sobriety Journal Card
        item {
            val latestCheckIn = uiState.checkIns.firstOrNull()
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCheckInClick() }
                    .testTag("dashboard_journal_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EditNote,
                                contentDescription = "Journal",
                                tint = CyanClarity,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Daily Sobriety Journal",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Text(
                            text = if (latestCheckIn != null) "Logged Today ✓" else "Log Day ${uiState.sobrietyTime.days}",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (latestCheckIn != null) EmeraldClean else AmberGold,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (latestCheckIn != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(SlateCardElevated)
                                .padding(10.dp)
                        ) {
                            Text(text = latestCheckIn.feelingEmoji, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Feeling: ${latestCheckIn.feeling}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                if (latestCheckIn.identifiedTriggers.isNotBlank()) {
                                    Text(
                                        text = "Triggers: ${latestCheckIn.identifiedTriggers}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary,
                                        maxLines = 1
                                    )
                                }
                                if (latestCheckIn.notes.isNotBlank()) {
                                    Text(
                                        text = "\"${latestCheckIn.notes}\"",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextMuted,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "Take 60 seconds to log how you feel and identify today's triggers to protect your sobriety.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Real-Life Quests (Today's Positive Habits)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TaskAlt,
                                contentDescription = "Real Quests",
                                tint = EmeraldClean,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Today's Real-Life Quests",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { onNavigateToQuests() }
                        ) {
                            Text(
                                text = "All Quests",
                                style = MaterialTheme.typography.labelSmall,
                                color = EmeraldClean
                            )
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "View all",
                                tint = EmeraldClean,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val activeQuests = uiState.quests.take(3)
                    activeQuests.forEach { quest ->
                        QuestRowItem(
                            quest = quest,
                            onComplete = { onCompleteQuest(quest) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        // Manifesto Quote Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SlateCardElevated)
                    .border(1.dp, SlateBorder, RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatQuote,
                        contentDescription = "Manifesto",
                        tint = AmberGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "MY VISION FOR REAL LIFE",
                            style = MaterialTheme.typography.labelSmall,
                            color = AmberGold,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "\"${uiState.profile.whyStatement}\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FeaturedBadgeMini(
    badge: Badge,
    modifier: Modifier = Modifier
) {
    val isUnlocked = badge.unlockedAt != null
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isUnlocked) SlateCardElevated else SlateCardElevated.copy(alpha = 0.4f))
            .border(
                1.dp,
                if (isUnlocked) AmberGold.copy(alpha = 0.5f) else SlateBorder.copy(alpha = 0.3f),
                RoundedCornerShape(12.dp)
            )
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = badge.title,
            tint = if (isUnlocked) AmberGold else TextMuted,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = badge.title,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 12.sp),
            color = if (isUnlocked) TextPrimary else TextMuted,
            maxLines = 2,
            minLines = 2,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            fontWeight = if (isUnlocked) FontWeight.Bold else FontWeight.Normal
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = if (isUnlocked) "EARNED" else "${badge.requiredThreshold.toInt()}d",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = if (isUnlocked) EmeraldClean else TextMuted,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun QuestRowItem(
    quest: QuestItem,
    onComplete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SlateCardElevated)
            .clickable(enabled = !quest.isCompletedToday) { onComplete() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (quest.isCompletedToday) EmeraldClean else SlateBorder)
                    .clickable(enabled = !quest.isCompletedToday) { onComplete() },
                contentAlignment = Alignment.Center
            ) {
                if (quest.isCompletedToday) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Done",
                        tint = SlateDark,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = quest.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (quest.isCompletedToday) TextMuted else TextPrimary
                )
                Text(
                    text = "${quest.category} • +${quest.xpReward} XP",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (quest.isCompletedToday) TextMuted else AmberGold
                )
            }
        }
    }
}
