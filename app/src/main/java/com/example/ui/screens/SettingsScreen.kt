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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyberCrimson
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsScreen(
    uiState: TrackerUiState,
    onBack: () -> Unit,
    onUpdateQuitTimestamp: (Long) -> Unit,
    onResetStreak: (String) -> Unit,
    onUpdateProfile: (hoursPerDay: Float, dailySpend: Float, monthlySpend: Float, why: String, genre: String) -> Unit,
    onToggleDailyCheckInNotification: (Boolean) -> Unit = {},
    onToggleAccomplishmentsNotification: (Boolean) -> Unit = {},
    onSendTestDailyCheckIn: () -> Unit = {},
    onSendTestAccomplishment: () -> Unit = {},
    onCheckForUpdates: () -> Unit = {},
    onUpdateTargetGitHubRepo: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var hoursPerDay by remember(uiState.profile.hoursPerDayGaming) {
        mutableFloatStateOf(uiState.profile.hoursPerDayGaming)
    }
    var dailySpend by remember(uiState.profile.dailyGamingSpendDollars) {
        mutableFloatStateOf(uiState.profile.dailyGamingSpendDollars)
    }
    var monthlySpend by remember(uiState.profile.monthlySpendDollars) {
        mutableFloatStateOf(uiState.profile.monthlySpendDollars)
    }
    var genre by remember(uiState.profile.primaryGameGenre) {
        mutableStateOf(uiState.profile.primaryGameGenre)
    }

    var showResetDialog by remember { mutableStateOf(false) }
    var showAdjustDateDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SlateDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SlateCardElevated)
                        .testTag("settings_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "SETTINGS & PROFILE",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Fine-tune calculations and quit date",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
        }

        // Quit Date Card
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
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Date",
                                tint = AmberGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Quit Date & Time",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        OutlinedButton(
                            onClick = { showAdjustDateDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AmberGold),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("Adjust Date", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val formattedQuitDate = SimpleDateFormat(
                        "MMMM d, yyyy 'at' h:mm a",
                        Locale.getDefault()
                    ).format(Date(uiState.profile.quitTimestamp))

                    Text(
                        text = formattedQuitDate,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = AmberGold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Your clean streak counter started counting continuously from this moment.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }
        }

        // Habit Calibration Card
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
                    Text(
                        text = "Gaming Habit Calibration",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Used to compute hours reclaimed and money saved",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Hours per day slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Daily Gaming Average",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Text(
                            text = String.format("%.1f hrs / day", hoursPerDay),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = FlameOrange
                        )
                    }
                    Slider(
                        value = hoursPerDay,
                        onValueChange = {
                            hoursPerDay = it
                            onUpdateProfile(hoursPerDay, dailySpend, monthlySpend, uiState.profile.whyStatement, genre)
                        },
                        valueRange = 1f..12f,
                        steps = 21,
                        colors = SliderDefaults.colors(
                            thumbColor = FlameOrange,
                            activeTrackColor = FlameOrange,
                            inactiveTrackColor = SlateBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Daily Gaming Expenditure Setting
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Daily Gaming Expenditure",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Games & subscriptions avoided",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = String.format("$%.2f / day", dailySpend),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldClean
                            )
                            Text(
                                text = String.format("~ $%.0f/mo", dailySpend * 30f),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }
                    Slider(
                        value = dailySpend,
                        onValueChange = {
                            dailySpend = it
                            monthlySpend = it * 30f
                            onUpdateProfile(hoursPerDay, dailySpend, monthlySpend, uiState.profile.whyStatement, genre)
                        },
                        valueRange = 0.50f..25.0f,
                        steps = 48,
                        colors = SliderDefaults.colors(
                            thumbColor = EmeraldClean,
                            activeTrackColor = EmeraldClean,
                            inactiveTrackColor = SlateBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = genre,
                        onValueChange = {
                            genre = it
                            onUpdateProfile(hoursPerDay, dailySpend, monthlySpend, uiState.profile.whyStatement, genre)
                        },
                        label = { Text("Addictive Game Genres") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SlateCardElevated,
                            unfocusedContainerColor = SlateCardElevated,
                            focusedBorderColor = AmberGold,
                            unfocusedBorderColor = SlateBorder
                        )
                    )
                }
            }
        }

        // Notifications & Reminders Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("notifications_settings_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldClean.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(EmeraldClean.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Notifications",
                                tint = EmeraldClean,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "NOTIFICATIONS & REMINDERS",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldClean,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Daily Check-Ins & Accomplishments",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 1. Daily Check-in reminder toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SlateCardElevated)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Daily Check-In Reminder (8:00 PM)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Evening prompt to log emotional state, cravings, and identify triggers.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                lineHeight = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Switch(
                            checked = uiState.dailyCheckInNotificationEnabled,
                            onCheckedChange = { onToggleDailyCheckInNotification(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = EmeraldClean,
                                checkedTrackColor = EmeraldClean.copy(alpha = 0.3f),
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = SlateBorder
                            ),
                            modifier = Modifier.testTag("toggle_daily_checkin_notification")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 2. Accomplishments & Milestones toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SlateCardElevated)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Milestones & Accomplishments",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Celebration alerts when unlocking 7-day, 30-day, or 100-day clean streak badges.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                lineHeight = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Switch(
                            checked = uiState.accomplishmentsNotificationEnabled,
                            onCheckedChange = { onToggleAccomplishmentsNotification(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = AmberGold,
                                checkedTrackColor = AmberGold.copy(alpha = 0.3f),
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = SlateBorder
                            ),
                            modifier = Modifier.testTag("toggle_accomplishments_notification")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Test Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onSendTestDailyCheckIn,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldClean)
                        ) {
                            Text("Test Check-In", fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }

                        OutlinedButton(
                            onClick = onSendTestAccomplishment,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AmberGold)
                        ) {
                            Text("Test Trophy", fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }
                    }
                }
            }
        }

        // In-App Updates & Auto-Renewal Card
        item {
            var repoInput by remember(uiState.targetGitHubRepo) { mutableStateOf(uiState.targetGitHubRepo) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("app_updates_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, AmberGold.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(AmberGold.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SystemUpdate,
                                contentDescription = "App Updates",
                                tint = AmberGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "APP UPDATES & AUTO-RENEWAL",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = AmberGold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "In-App Version Checker",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SlateCardElevated)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Installed Version",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                            Text(
                                text = "v1.0 (Build 1)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldClean
                            )
                        }

                        Button(
                            onClick = onCheckForUpdates,
                            enabled = !uiState.isCheckingForUpdate,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AmberGold)
                        ) {
                            if (uiState.isCheckingForUpdate) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = SlateDark
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Checking...", color = SlateDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Text("Check for Updates", color = SlateDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (uiState.updateCheckStatusMessage != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(SlateCardElevated)
                                .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = uiState.updateCheckStatusMessage ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (uiState.availableUpdate != null) EmeraldClean else TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "GitHub Release Repository:",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = repoInput,
                        onValueChange = {
                            repoInput = it
                            onUpdateTargetGitHubRepo(it)
                        },
                        placeholder = { Text("owner/repository") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SlateCardElevated,
                            unfocusedContainerColor = SlateCardElevated,
                            focusedBorderColor = AmberGold,
                            unfocusedBorderColor = SlateBorder
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "When you publish a new APK release on GitHub, the app detects it automatically and prompts you to update in one tap.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Reset Streak Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCrimson.copy(alpha = 0.4f))
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
                                imageVector = Icons.Default.RestartAlt,
                                contentDescription = "Reset",
                                tint = CyberCrimson,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Streak Reset",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = CyberCrimson
                            )
                        }

                        Button(
                            onClick = { showResetDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCrimson.copy(alpha = 0.2f)),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("Reset Counter", color = CyberCrimson, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "If you slipped up, do not despair. Relapse is not the end of your journey—it is data on where your boundary broke. Resetting restarts your streak from 0 while keeping your XP and previous trophies intact.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // About & Science
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "About",
                            tint = AmberGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Respawn Philosophy",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Respawn is engineered based on dopamine fasting and cognitive behavioral habit disruption. Games hijack your evolutionary achievement drive. By turning your discipline and reclaimed hours into tangible real-world leveling, you win in reality.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Adjust Date Dialog
    if (showAdjustDateDialog) {
        AdjustDateDialog(
            currentTimestamp = uiState.profile.quitTimestamp,
            onDismiss = { showAdjustDateDialog = false },
            onConfirm = { timestamp ->
                onUpdateQuitTimestamp(timestamp)
                showAdjustDateDialog = false
            }
        )
    }

    // Reset Streak Dialog
    if (showResetDialog) {
        var reason by remember { mutableStateOf("") }
        Dialog(onDismissRequest = { showResetDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCrimson)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Reset Clean Streak to Zero?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CyberCrimson
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Honesty with yourself is the supreme virtue of recovery. What led to this relapse?",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        label = { Text("Trigger / Lesson Learned") },
                        placeholder = { Text("e.g. Played with friends because of loneliness...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SlateCardElevated,
                            unfocusedContainerColor = SlateCardElevated,
                            focusedBorderColor = CyberCrimson,
                            unfocusedBorderColor = SlateBorder
                        ),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { showResetDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = SlateCardElevated),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Cancel", color = TextSecondary)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                onResetStreak(reason)
                                showResetDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCrimson),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Reset Streak", color = TextPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdjustDateDialog(
    currentTimestamp: Long,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit
) {
    val now = System.currentTimeMillis()
    var selectedPresetDays by remember { mutableStateOf<Int?>(null) }
    var customDaysInput by remember { mutableStateOf("") }

    val presets = listOf(
        "Just Now (0d)" to 0,
        "1 Day Ago" to 1,
        "3 Days Ago" to 3,
        "7 Days (1 Week)" to 7,
        "14 Days (2 Weeks)" to 14,
        "30 Days (1 Month)" to 30,
        "100 Days Strong" to 100
    )

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
                    text = "Set When You Quit Gaming",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Select when your last gaming session occurred to accurately synchronize your streak counter and unlock your corresponding achievement badges.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Presets list
                presets.forEach { (label, days) ->
                    val isSelected = selectedPresetDays == days
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) AmberGold.copy(alpha = 0.2f) else SlateCardElevated)
                            .border(
                                1.dp,
                                if (isSelected) AmberGold else SlateBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                selectedPresetDays = days
                                customDaysInput = ""
                            }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) AmberGold else TextPrimary
                        )
                        if (days == 7 || days == 30 || days == 100) {
                            Text(
                                text = "🏆 Milestone",
                                style = MaterialTheme.typography.labelSmall,
                                color = AmberGold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = customDaysInput,
                    onValueChange = {
                        customDaysInput = it
                        selectedPresetDays = null
                    },
                    label = { Text("Or enter exact custom days ago") },
                    placeholder = { Text("e.g. 15") },
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
                            val days = when {
                                selectedPresetDays != null -> selectedPresetDays!!
                                customDaysInput.isNotBlank() -> customDaysInput.toIntOrNull() ?: 0
                                else -> 0
                            }
                            val targetTimestamp = now - (days * 86400000L)
                            onConfirm(targetTimestamp)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Apply & Sync Badges", color = SlateDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
