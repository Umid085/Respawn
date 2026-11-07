package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.model.DailyCheckIn
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanClarity
import com.example.ui.theme.CyberCrimson
import com.example.ui.theme.EmeraldClean
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DailyJournalFeedCard(
    checkIns: List<DailyCheckIn>,
    onNewJournalEntryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedTriggers by remember { mutableStateOf(false) }

    // Aggregate identified triggers across all journal entries
    val triggerFrequency = remember(checkIns) {
        val map = mutableMapOf<String, Int>()
        checkIns.forEach { entry ->
            if (entry.identifiedTriggers.isNotBlank()) {
                entry.identifiedTriggers.split(",").map { it.trim() }.forEach { trigger ->
                    if (trigger.isNotBlank() && !trigger.startsWith("No Triggers")) {
                        map[trigger] = map.getOrDefault(trigger, 0) + 1
                    }
                }
            }
        }
        map.toList().sortedByDescending { it.second }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("daily_journal_feed_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Brush.horizontalGradient(listOf(CyanClarity.copy(alpha = 0.5f), SlateBorder))
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header with Log New Entry Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(CyanClarity.copy(alpha = 0.15f))
                            .border(1.dp, CyanClarity.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = "Journal",
                            tint = CyanClarity,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "SOBRIETY JOURNAL & TRIGGERS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = CyanClarity,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Daily Emotional Log",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }

                Button(
                    onClick = onNewJournalEntryClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanClarity),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("new_journal_entry_btn")
                ) {
                    Text(
                        text = "Write Entry",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = SlateCard
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Identified Triggers Intelligence Block
            if (triggerFrequency.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SlateCardElevated)
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = "Triggers",
                                tint = CyberCrimson,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Identified Triggers Pattern",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Text(
                            text = "${triggerFrequency.size} Distinct Triggers",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberCrimson
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        triggerFrequency.take(5).forEach { (trigger, count) ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CyberCrimson.copy(alpha = 0.15f))
                                    .border(1.dp, CyberCrimson.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "$trigger ($count×)",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            }
                        }
                    }

                    val topTrigger = triggerFrequency.firstOrNull()?.first
                    if (topTrigger != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "💡 Recovery Strategy for $topTrigger: Prepare an alternative physical routine before entering high-risk hours to bypass habitual cravings.",
                            style = MaterialTheme.typography.bodySmall,
                            color = AmberGold,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Journal Entries Feed
            Text(
                text = "RECENT REFLECTIONS (${checkIns.size} ENTRIES)",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (checkIns.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SlateCardElevated)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No journal entries yet",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap 'Write Entry' to log how you feel today and begin identifying triggers.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                checkIns.take(4).forEach { entry ->
                    JournalEntryItem(entry = entry)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun JournalEntryItem(entry: DailyCheckIn) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SlateCardElevated)
            .padding(12.dp)
    ) {
        // Date & Emotion Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = entry.feelingEmoji,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = entry.feeling,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Text(
                text = "${entry.dateIso} • Day ${entry.dayNumber}",
                style = MaterialTheme.typography.labelSmall,
                color = AmberGold
            )
        }

        // Identified Triggers tags
        if (entry.identifiedTriggers.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Triggers:",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = TextMuted
                )
                Text(
                    text = entry.identifiedTriggers,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    fontWeight = FontWeight.Medium,
                    color = if (entry.identifiedTriggers.startsWith("No Triggers")) EmeraldClean else CyberCrimson
                )
            }
        }

        // Journal reflection notes
        if (entry.notes.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "\"${entry.notes}\"",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 16.sp
            )
        }

        // Replacement activities tag
        if (entry.activities.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "✓ Activities: ${entry.activities}",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = EmeraldClean
            )
        }
    }
}
