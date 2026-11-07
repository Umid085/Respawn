package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SelfImprovement
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.CyanClarity
import com.example.ui.theme.CyberCrimson
import com.example.ui.theme.EmeraldClean
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UrgeSurfingDialog(
    whyStatement: String,
    onDismiss: () -> Unit,
    onLogSuccess: (trigger: String, intensity: Int, strategy: String, note: String) -> Unit
) {
    var selectedTrigger by remember { mutableStateOf("Boredom") }
    var selectedStrategy by remember { mutableStateOf("Box Breathing") }
    var intensity by remember { mutableIntStateOf(7) }
    var note by remember { mutableStateOf("") }

    // Breathing guide state
    var breathPhase by remember { mutableStateOf("Inhale") }
    var breathSeconds by remember { mutableIntStateOf(4) }

    LaunchedEffect(Unit) {
        val phases = listOf("Inhale (4s)", "Hold (4s)", "Exhale (4s)", "Hold (4s)")
        var index = 0
        while (true) {
            breathPhase = phases[index % 4]
            for (sec in 4 downTo 1) {
                breathSeconds = sec
                delay(1000L)
            }
            index++
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "breathScale")
    val circleScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circleScale"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 24.dp)
                .testTag("urge_surfing_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, CyberCrimson.copy(alpha = 0.5f))
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
                        Icon(
                            imageVector = Icons.Default.SelfImprovement,
                            contentDescription = "Urge Surfer",
                            tint = CyanClarity,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "URGE SURFER SOS",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
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

                Spacer(modifier = Modifier.height(14.dp))

                // Guided Box Breathing Animation
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(CyanClarity.copy(alpha = 0.25f), SlateCardElevated)
                            )
                        )
                        .border(2.dp, CyanClarity.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = breathPhase,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CyanClarity
                        )
                        Text(
                            text = "$breathSeconds",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Why I Quit Reminder Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SlateCardElevated)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "YOUR CORE REASON:",
                        style = MaterialTheme.typography.labelSmall,
                        color = EmeraldClean,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "\"$whyStatement\"",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Trigger Picker
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "What triggered this urge?",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val triggers = listOf("Boredom", "Stress", "Fatigue", "Gaming Ad / Friends", "Loneliness", "Anger")
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        triggers.forEach { trigger ->
                            val isSelected = selectedTrigger == trigger
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) CyberCrimson.copy(alpha = 0.25f) else SlateCardElevated)
                                    .border(
                                        1.dp,
                                        if (isSelected) CyberCrimson else SlateBorder,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedTrigger = trigger }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
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

                Spacer(modifier = Modifier.height(14.dp))

                // Urge Intensity Slider
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Intensity Level",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextSecondary
                        )
                        Text(
                            text = "$intensity / 10",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CyberCrimson
                        )
                    }
                    Slider(
                        value = intensity.toFloat(),
                        onValueChange = { intensity = it.toInt() },
                        valueRange = 1f..10f,
                        steps = 8,
                        colors = SliderDefaults.colors(
                            thumbColor = CyberCrimson,
                            activeTrackColor = CyberCrimson,
                            inactiveTrackColor = SlateBorder
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Coping Action Used
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "How did you interrupt it?",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val strategies = listOf("Box Breathing", "Cold Water Splash", "10-Min Walk", "Pushups", "Called Friend")
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        strategies.forEach { strategy ->
                            val isSelected = selectedStrategy == strategy
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) EmeraldClean.copy(alpha = 0.25f) else SlateCardElevated)
                                    .border(
                                        1.dp,
                                        if (isSelected) EmeraldClean else SlateBorder,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedStrategy = strategy }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = strategy,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) TextPrimary else TextSecondary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Reflection Note (optional)") },
                    placeholder = { Text("What did you learn from surfing this wave?") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SlateCardElevated,
                        unfocusedContainerColor = SlateCardElevated,
                        focusedBorderColor = CyanClarity,
                        unfocusedBorderColor = SlateBorder
                    ),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        onLogSuccess(selectedTrigger, intensity, selectedStrategy, note)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("surf_complete_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldClean)
                ) {
                    Text(
                        text = "I Conquered This Urge (+75 XP)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SlateCard
                    )
                }
            }
        }
    }
}
