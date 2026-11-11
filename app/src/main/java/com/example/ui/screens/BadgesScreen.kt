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
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Badge
import com.example.data.model.BadgeCategory
import com.example.data.model.PersonalReward
import com.example.ui.components.BadgeCard
import com.example.ui.theme.AmberGold
import com.example.ui.theme.EmeraldClean
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardElevated
import com.example.ui.theme.SlateDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.TrackerUiState

@Composable
fun BadgesScreen(
    uiState: TrackerUiState,
    onAddReward: (title: String, days: Int, cost: Float, notes: String) -> Unit,
    onToggleReward: (PersonalReward) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddRewardDialog by remember { mutableStateOf(false) }

    val strings = uiState.strings
    val isRu = uiState.appLanguage == com.example.ui.i18n.AppLanguage.RUSSIAN ||
        (uiState.appLanguage == com.example.ui.i18n.AppLanguage.SYSTEM && java.util.Locale.getDefault().language.lowercase().let { it == "ru" || it == "be" || it == "uk" || it == "kk" })

    val tabs = listOf(
        if (isRu) "Все награды" else "All Badges",
        strings.tabStreak,
        strings.tabHours,
        strings.tabRewards
    )

    val streakDays = uiState.sobrietyTime.days.toFloat()
    val hoursSaved = uiState.sobrietyTime.hoursReclaimed

    val filteredBadges = when (selectedTab) {
        1 -> uiState.allBadges.filter { it.category == BadgeCategory.STREAK_DAYS }
        2 -> uiState.allBadges.filter { it.category == BadgeCategory.HOURS_SAVED }
        else -> uiState.allBadges
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SlateDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Header summary banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("trophy_room_header"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(listOf(AmberGold.copy(alpha = 0.5f), SlateBorder))
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = strings.badgesScreenTitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = AmberGold,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${uiState.unlockedBadges.size} / ${uiState.allBadges.size} ${strings.totalBadgesUnlocked}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = strings.badgesScreenSubtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(listOf(AmberGold.copy(alpha = 0.25f), SlateCardElevated))
                            )
                            .border(1.5.dp, AmberGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Trophy",
                            tint = AmberGold,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        // Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = SlateDark,
                contentColor = AmberGold,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = AmberGold
                    )
                },
                divider = { Box(modifier = Modifier.height(1.dp).background(SlateBorder)) }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) AmberGold else TextSecondary
                            )
                        }
                    )
                }
            }
        }

        // Rewards Tab Content
        if (selectedTab == 3) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = strings.personalRewardsTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (isRu) "Финансируются из сэкономленных на играх денег" else "Funded by money & time saved from gaming",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }

                    Button(
                        onClick = { showAddRewardDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = SlateDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = strings.addCustomRewardButton,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = SlateDark
                        )
                    }
                }
            }

            items(uiState.rewards, key = { it.id }) { reward ->
                PersonalRewardRow(
                    reward = reward,
                    currentDaysClean = streakDays.toInt(),
                    onToggleClaimed = { onToggleReward(reward) }
                )
            }
        } else {
            // Badges List
            items(filteredBadges, key = { it.id }) { badge ->
                val currentValue = when (badge.category) {
                    BadgeCategory.STREAK_DAYS -> streakDays
                    BadgeCategory.HOURS_SAVED -> hoursSaved
                    BadgeCategory.CRAVING_SURF -> uiState.cravings.size.toFloat()
                    else -> 0f
                }
                BadgeCard(
                    badge = badge,
                    currentValue = currentValue
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showAddRewardDialog) {
        AddRewardDialog(
            onDismiss = { showAddRewardDialog = false },
            onAdd = { title, days, cost, notes ->
                onAddReward(title, days, cost, notes)
                showAddRewardDialog = false
            }
        )
    }
}

@Composable
private fun PersonalRewardRow(
    reward: PersonalReward,
    currentDaysClean: Int,
    onToggleClaimed: () -> Unit
) {
    val isEligible = currentDaysClean >= reward.targetDays

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (reward.isClaimed) SlateCard.copy(alpha = 0.5f) else SlateCard
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (reward.isClaimed) EmeraldClean.copy(alpha = 0.4f)
            else if (isEligible) AmberGold.copy(alpha = 0.6f)
            else SlateBorder.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (reward.isClaimed) EmeraldClean.copy(alpha = 0.15f)
                        else if (isEligible) AmberGold.copy(alpha = 0.15f)
                        else SlateCardElevated
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CardGiftcard,
                    contentDescription = "Reward",
                    tint = if (reward.isClaimed) EmeraldClean else if (isEligible) AmberGold else TextMuted,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reward.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (reward.isClaimed) TextMuted else TextPrimary
                )
                Text(
                    text = "Unlock at ${reward.targetDays} Days Clean • Est. $${reward.estimatedCost.toInt()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isEligible) AmberGold else TextMuted
                )
                if (reward.notes.isNotEmpty()) {
                    Text(
                        text = reward.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }
            }

            Checkbox(
                checked = reward.isClaimed,
                onCheckedChange = { onToggleClaimed() },
                colors = CheckboxDefaults.colors(
                    checkedColor = EmeraldClean,
                    uncheckedColor = SlateBorder
                )
            )
        }
    }
}

@Composable
private fun AddRewardDialog(
    onDismiss: () -> Unit,
    onAdd: (title: String, days: Int, cost: Float, notes: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var targetDays by remember { mutableStateOf("30") }
    var cost by remember { mutableStateOf("50") }
    var notes by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, AmberGold.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Add Real-Life Milestone Reward",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Reward Name") },
                    placeholder = { Text("e.g. New Running Shoes, Camping Trip") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SlateCardElevated,
                        unfocusedContainerColor = SlateCardElevated,
                        focusedBorderColor = AmberGold,
                        unfocusedBorderColor = SlateBorder
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = targetDays,
                        onValueChange = { targetDays = it },
                        label = { Text("Target Days") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SlateCardElevated,
                            unfocusedContainerColor = SlateCardElevated,
                            focusedBorderColor = AmberGold,
                            unfocusedBorderColor = SlateBorder
                        )
                    )

                    OutlinedTextField(
                        value = cost,
                        onValueChange = { cost = it },
                        label = { Text("Cost ($)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SlateCardElevated,
                            unfocusedContainerColor = SlateCardElevated,
                            focusedBorderColor = AmberGold,
                            unfocusedBorderColor = SlateBorder
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Why this reward matters") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SlateCardElevated,
                        unfocusedContainerColor = SlateCardElevated,
                        focusedBorderColor = AmberGold,
                        unfocusedBorderColor = SlateBorder
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = SlateCardElevated),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cancel", color = TextSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val daysInt = targetDays.toIntOrNull() ?: 30
                            val costFloat = cost.toFloatOrNull() ?: 0f
                            if (title.isNotBlank()) {
                                onAdd(title, daysInt, costFloat, notes)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Save Reward", color = SlateDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
