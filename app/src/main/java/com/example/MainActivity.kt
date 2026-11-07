package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.BadgeUnlockDialog
import com.example.ui.components.DailyCheckInSheet
import com.example.ui.components.UrgeSurfingDialog
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.BadgesScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.QuestsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.AmberGold
import com.example.ui.theme.RespawnTheme
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.TrackerViewModel

enum class NavDestination(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    DASHBOARD("Dashboard", Icons.Filled.Home, Icons.Outlined.Home),
    BADGES("Badges", Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents),
    QUESTS("Quests", Icons.Filled.TaskAlt, Icons.Outlined.TaskAlt),
    ANALYTICS("Analytics", Icons.Filled.Insights, Icons.Outlined.Insights)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RespawnTheme {
                val viewModel: TrackerViewModel = viewModel()
                RespawnApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun RespawnApp(viewModel: TrackerViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var currentDestination by remember { mutableStateOf(NavDestination.DASHBOARD) }
    var inSettings by remember { mutableStateOf(false) }

    var showUrgeSurfing by remember { mutableStateOf(false) }
    var showDailyCheckIn by remember { mutableStateOf(false) }

    if (inSettings) {
        BackHandler { inSettings = false }
    } else if (currentDestination != NavDestination.DASHBOARD) {
        BackHandler { currentDestination = NavDestination.DASHBOARD }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = SlateDark,
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            if (!inSettings) {
                NavigationBar(
                    containerColor = SlateCard,
                    contentColor = TextPrimary,
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("bottom_nav_bar")
                ) {
                    NavDestination.values().forEach { destination ->
                        val selected = currentDestination == destination
                        NavigationBarItem(
                            selected = selected,
                            onClick = { currentDestination = destination },
                            icon = {
                                Icon(
                                    imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                                    contentDescription = destination.title,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = destination.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = SlateDark,
                                selectedTextColor = AmberGold,
                                indicatorColor = AmberGold,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextMuted
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AmberGold)
                }
            } else if (inSettings) {
                SettingsScreen(
                    uiState = uiState,
                    onBack = { inSettings = false },
                    onUpdateQuitTimestamp = { viewModel.updateQuitTimestamp(it) },
                    onResetStreak = { viewModel.resetStreak(it) },
                    onUpdateProfile = { hours, dailySpend, monthlySpend, why, genre ->
                        viewModel.updateProfileSettings(hours, dailySpend, monthlySpend, why, genre)
                    }
                )
            } else {
                when (currentDestination) {
                    NavDestination.DASHBOARD -> {
                        DashboardScreen(
                            uiState = uiState,
                            onUrgeSurfClick = { showUrgeSurfing = true },
                            onCheckInClick = { showDailyCheckIn = true },
                            onNavigateToBadges = { currentDestination = NavDestination.BADGES },
                            onNavigateToQuests = { currentDestination = NavDestination.QUESTS },
                            onNavigateToSettings = { inSettings = true },
                            onCompleteQuest = { viewModel.completeQuest(it) },
                            onUpdateDailyExpenditure = { viewModel.updateDailyGamingExpenditure(it) }
                        )
                    }
                    NavDestination.BADGES -> {
                        BadgesScreen(
                            uiState = uiState,
                            onAddReward = { title, days, cost, notes ->
                                viewModel.addPersonalReward(title, days, cost, notes)
                            },
                            onToggleReward = { viewModel.toggleRewardClaimed(it) }
                        )
                    }
                    NavDestination.QUESTS -> {
                        QuestsScreen(
                            uiState = uiState,
                            onCompleteQuest = { viewModel.completeQuest(it) },
                            onAddQuest = { title, cat, xp ->
                                viewModel.addCustomQuest(title, cat, xp)
                            },
                            onUpdateManifesto = { newWhy ->
                                viewModel.updateProfileSettings(
                                    uiState.profile.hoursPerDayGaming,
                                    uiState.profile.dailyGamingSpendDollars,
                                    uiState.profile.monthlySpendDollars,
                                    newWhy,
                                    uiState.profile.primaryGameGenre
                                )
                            }
                        )
                    }
                    NavDestination.ANALYTICS -> {
                        AnalyticsScreen(
                            uiState = uiState,
                            onNewJournalEntryClick = { showDailyCheckIn = true }
                        )
                    }
                }
            }
        }
    }

    // Celebration Dialog when a milestone badge unlocks
    uiState.newlyUnlockedBadge?.let { badge ->
        BadgeUnlockDialog(
            badge = badge,
            onDismiss = { viewModel.dismissUnlockedBadge() }
        )
    }

    // Urge Surfing Emergency Modal
    if (showUrgeSurfing) {
        UrgeSurfingDialog(
            whyStatement = uiState.profile.whyStatement,
            onDismiss = { showUrgeSurfing = false },
            onLogSuccess = { trigger, intensity, strategy, note ->
                viewModel.logCravingSurfed(trigger, intensity, strategy, note)
            }
        )
    }

    // Daily Check-In & Journal Modal
    if (showDailyCheckIn) {
        DailyCheckInSheet(
            streakDays = uiState.sobrietyTime.days,
            onDismiss = { showDailyCheckIn = false },
            onSubmit = { feeling, feelingEmoji, triggers, urge, mood, activities, notes ->
                viewModel.logDailyCheckIn(
                    feeling = feeling,
                    feelingEmoji = feelingEmoji,
                    identifiedTriggers = triggers,
                    urge = urge,
                    mood = mood,
                    activities = activities,
                    notes = notes
                )
            }
        )
    }
}
