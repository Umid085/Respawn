package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.ui.components.DailyJournalFeedCard
import com.example.ui.components.GamingFreeTrendChart
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanClarity
import com.example.ui.theme.CyberCrimson
import com.example.ui.theme.EmeraldClean
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.JetBrainsMono
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
fun AnalyticsScreen(
    uiState: TrackerUiState,
    onNewJournalEntryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sobriety = uiState.sobrietyTime
    val profile = uiState.profile

    val yearlySavings = profile.monthlySpendDollars * 12f
    val yearlyHoursReclaimed = profile.hoursPerDayGaming * 365f

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SlateDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SlateCardElevated)
                        .border(1.dp, CyanClarity.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Insights,
                        contentDescription = "Analytics",
                        tint = CyanClarity,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "LIFE RECLAMATION ANALYTICS",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Real mathematical proof of your transformation",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
        }

        // 30-Day Gaming-Free Progress Trend Line
        item {
            GamingFreeTrendChart(
                quitTimestamp = profile.quitTimestamp,
                currentStreakDays = sobriety.days
            )
        }

        // 1-Year Long-Term Projection Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("projections_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(listOf(EmeraldClean.copy(alpha = 0.5f), SlateBorder))
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "1-YEAR FREEDOM PROJECTION",
                            style = MaterialTheme.typography.labelSmall,
                            color = EmeraldClean,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = "Projection",
                            tint = EmeraldClean,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SlateCardElevated)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "Annual Hours Saved",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                            Text(
                                text = "${yearlyHoursReclaimed.toInt()} hrs",
                                style = MaterialTheme.typography.titleLarge,
                                fontFamily = JetBrainsMono,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "= ${(yearlyHoursReclaimed / 24f).toInt()} full days awake",
                                style = MaterialTheme.typography.bodySmall,
                                color = AmberGold,
                                fontSize = 11.sp
                            )
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SlateCardElevated)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "Annual Money Saved",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                            Text(
                                text = "$${yearlySavings.toInt()}",
                                style = MaterialTheme.typography.titleLarge,
                                fontFamily = JetBrainsMono,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldClean
                            )
                            Text(
                                text = "Zero microtransactions",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Urge Surfing Defense Statistics
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
                                imageVector = Icons.Default.SelfImprovement,
                                contentDescription = "Craving defense",
                                tint = CyanClarity,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Craving Surfing Log",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Text(
                            text = "${uiState.cravings.size} Surfed",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = CyanClarity
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (uiState.cravings.isEmpty()) {
                        Text(
                            text = "No emergency cravings logged yet. When an urge strikes, use the 'Craving SOS' button on the dashboard to ride it out with box breathing.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    } else {
                        uiState.cravings.take(4).forEach { craving ->
                            val dateStr = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(craving.timestamp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${craving.trigger} (Intensity: ${craving.intensity}/10)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Coped via ${craving.strategyUsed} • $dateStr",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextMuted
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(EmeraldClean.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "SURFED",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = EmeraldClean,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Daily Sobriety Journal & Trigger Identification Feed
        item {
            DailyJournalFeedCard(
                checkIns = uiState.checkIns,
                onNewJournalEntryClick = onNewJournalEntryClick
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
