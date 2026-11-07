package com.example.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.window.DialogProperties
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
fun DailyCheckInSheet(
    streakDays: Long,
    onDismiss: () -> Unit,
    onSubmit: (
        feeling: String,
        feelingEmoji: String,
        triggers: List<String>,
        urge: Int,
        mood: Int,
        activities: List<String>,
        notes: String
    ) -> Unit
) {
    // Feelings state
    val feelingOptions = listOf(
        Pair("Calm & Grounded", "🧘"),
        Pair("Restless & Bored", "🥱"),
        Pair("Energized & Sharp", "⚡"),
        Pair("Stressed / Anxious", "😫"),
        Pair("Vulnerable / Tempted", "⚠️"),
        Pair("Lonely / Isolated", "👤"),
        Pair("Focused & Driven", "🎯"),
        Pair("Grateful & Peace", "🌿")
    )
    var selectedFeeling by remember { mutableStateOf(feelingOptions[0]) }

    // Triggers identified state
    val triggerOptions = listOf(
        "Unstructured Free Time / Boredom",
        "Late Night Fatigue",
        "Work or Study Stress",
        "Saw Friends Playing / Game Stream",
        "Emotional Escapism (Avoiding Pain)",
        "Social Isolation",
        "Frustration / Bad Mood",
        "No Triggers (Felt Strong) ✓"
    )
    val selectedTriggers = remember { mutableStateListOf<String>("No Triggers (Felt Strong) ✓") }

    var urgeLevel by remember { mutableIntStateOf(1) }
    var moodLevel by remember { mutableIntStateOf(4) }
    val selectedActivities = remember { mutableStateListOf<String>("Walk / Outdoors") }
    var notes by remember { mutableStateOf("") }

    val activityOptions = listOf(
        "Exercise & Gym",
        "Reading Book",
        "Deep Work & Career",
        "Walk / Outdoors",
        "Home Cooking",
        "Social / Friends",
        "Creative Hobby",
        "Meditation"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 20.dp)
                .testTag("daily_journal_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldClean.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(EmeraldClean.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EditNote,
                                contentDescription = "Journal",
                                tint = EmeraldClean,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "DAILY SOBRIETY JOURNAL",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldClean,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Day $streakDays of Sobriety",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 1. HOW DO YOU FEEL TODAY?
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "1. How are you feeling today?",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${selectedFeeling.second} ${selectedFeeling.first}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = AmberGold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        feelingOptions.forEach { feelingPair ->
                            val isSelected = selectedFeeling == feelingPair
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) AmberGold.copy(alpha = 0.2f) else SlateCardElevated)
                                    .border(
                                        1.dp,
                                        if (isSelected) AmberGold else SlateBorder,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedFeeling = feelingPair }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${feelingPair.second} ${feelingPair.first}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) TextPrimary else TextSecondary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 2. IDENTIFY TRIGGERS ENCOUNTERED
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "Triggers",
                            tint = CyberCrimson,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "2. Identify Triggers Encountered",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = "Naming triggers robs them of their subconscious power.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        triggerOptions.forEach { trigger ->
                            val isSelected = selectedTriggers.contains(trigger)
                            val isNone = trigger.startsWith("No Triggers")
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) {
                                            if (isNone) EmeraldClean.copy(alpha = 0.2f)
                                            else CyberCrimson.copy(alpha = 0.2f)
                                        } else SlateCardElevated
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) {
                                            if (isNone) EmeraldClean else CyberCrimson
                                        } else SlateBorder,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        if (isNone) {
                                            selectedTriggers.clear()
                                            selectedTriggers.add(trigger)
                                        } else {
                                            selectedTriggers.remove("No Triggers (Felt Strong) ✓")
                                            if (isSelected) {
                                                selectedTriggers.remove(trigger)
                                                if (selectedTriggers.isEmpty()) {
                                                    selectedTriggers.add("No Triggers (Felt Strong) ✓")
                                                }
                                            } else {
                                                selectedTriggers.add(trigger)
                                            }
                                        }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = trigger,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) TextPrimary else TextSecondary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 3. TEMPTATION & CRAVING LEVEL
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "3. Gaming Craving Intensity",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        val urgeLabel = when (urgeLevel) {
                            1 -> "Zero Cravings 🟢"
                            2 -> "Mild Fleeting 🟢"
                            3 -> "Noticeable 🟡"
                            4 -> "Challenging 🟠"
                            else -> "Severe Wave 🔴"
                        }
                        Text(
                            text = urgeLabel,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (urgeLevel <= 2) EmeraldClean else if (urgeLevel <= 3) AmberGold else CyberCrimson
                        )
                    }
                    Slider(
                        value = urgeLevel.toFloat(),
                        onValueChange = { urgeLevel = it.toInt() },
                        valueRange = 1f..5f,
                        steps = 3,
                        colors = SliderDefaults.colors(
                            thumbColor = FlameOrange,
                            activeTrackColor = FlameOrange,
                            inactiveTrackColor = SlateBorder
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 4. POSITIVE ACTIVITIES TODAY
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "4. Replacement Activities Done",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        activityOptions.forEach { activity ->
                            val isSelected = selectedActivities.contains(activity)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) CyanClarity.copy(alpha = 0.2f) else SlateCardElevated)
                                    .border(
                                        1.dp,
                                        if (isSelected) CyanClarity else SlateBorder,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        if (isSelected) selectedActivities.remove(activity)
                                        else selectedActivities.add(activity)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = activity,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) TextPrimary else TextSecondary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 5. FREE-FORM JOURNAL ENTRY
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Reflect",
                            tint = AmberGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "5. Daily Journal Reflection",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = "How did your mind respond today? What triggered you, or what victories did you achieve?",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        placeholder = { Text("Today I felt tempted around 8 PM due to boredom, but I went for a run instead. Felt much clearer afterwards...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SlateCardElevated,
                            unfocusedContainerColor = SlateCardElevated,
                            focusedBorderColor = EmeraldClean,
                            unfocusedBorderColor = SlateBorder
                        ),
                        minLines = 3,
                        maxLines = 5
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        onSubmit(
                            selectedFeeling.first,
                            selectedFeeling.second,
                            selectedTriggers.toList(),
                            urgeLevel,
                            moodLevel,
                            selectedActivities.toList(),
                            notes
                        )
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_journal_entry_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldClean)
                ) {
                    Text(
                        text = "Save Journal Entry (+60 XP)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SlateCard
                    )
                }
            }
        }
    }
}
