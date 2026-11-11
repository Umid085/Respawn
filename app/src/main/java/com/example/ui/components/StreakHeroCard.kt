package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Badge
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyberCrimson
import com.example.ui.theme.EmeraldClean
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.JetBrainsMono
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.SobrietyTime

@Composable
fun StreakHeroCard(
    sobrietyTime: SobrietyTime,
    nextBadge: Badge?,
    nextBadgeRemainingDays: Float,
    currentLevel: Int,
    currentXp: Int,
    onUrgeSurfClick: () -> Unit,
    onCheckInClick: () -> Unit,
    strings: com.example.ui.i18n.StringsDefinition = com.example.ui.i18n.AppStrings.Russian,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("streak_hero_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Brush.verticalGradient(
                listOf(
                    AmberGold.copy(alpha = glowAlpha),
                    SlateBorder.copy(alpha = 0.5f)
                )
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Level & Status Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Level Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SlateCardElevated)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "XP Level",
                        tint = AmberGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${strings.levelPrefix} $currentLevel",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$currentXp ${strings.xpLabel}",
                        style = MaterialTheme.typography.labelSmall,
                        color = AmberGold
                    )
                }

                // Clean Badge Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(EmeraldClean.copy(alpha = 0.15f))
                        .border(1.dp, EmeraldClean.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(EmeraldClean)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = strings.daysFree,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldClean,
                        letterSpacing = 1.sp,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main Streak Days Display
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(170.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                AmberGold.copy(alpha = 0.18f),
                                SlateCardElevated.copy(alpha = 0.6f),
                                Color.Transparent
                            )
                        )
                    )
                    .border(
                        2.5.dp,
                        Brush.sweepGradient(
                            listOf(
                                AmberGold,
                                FlameOrange,
                                EmeraldClean,
                                AmberGold
                            )
                        ),
                        CircleShape
                    )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Streak flame",
                        tint = FlameOrange,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "${sobrietyTime.days}",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontFamily = JetBrainsMono,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = TextPrimary,
                        modifier = Modifier.testTag("streak_days_count")
                    )
                    Text(
                        text = strings.daysFree,
                        style = MaterialTheme.typography.labelLarge.copy(
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = AmberGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Live ticking precision timer
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(SlateCardElevated)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                TimerSegment(value = sobrietyTime.days, label = "d")
                TimerColon()
                TimerSegment(value = sobrietyTime.hours, label = "h")
                TimerColon()
                TimerSegment(value = sobrietyTime.minutes, label = "m")
                TimerColon()
                TimerSegment(value = sobrietyTime.seconds, label = "s", isAccented = true)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Next milestone target
            if (nextBadge != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SlateCardElevated.copy(alpha = 0.5f))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f, fill = false),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Next badge",
                                tint = AmberGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${strings.nextBadgePrefix}${nextBadge.title}",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = String.format(java.util.Locale.US, "%.1f %s", nextBadgeRemainingDays, strings.daysLeftSuffix),
                            style = MaterialTheme.typography.labelSmall,
                            color = AmberGold,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    val progress = ((nextBadge.requiredThreshold - nextBadgeRemainingDays) / nextBadge.requiredThreshold)
                        .coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = AmberGold,
                        trackColor = SlateBorder
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dual Action Buttons: Daily Check-In & Urge SOS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onUrgeSurfClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("urge_sos_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = CyberCrimson
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, CyberCrimson.copy(alpha = 0.6f))
                ) {
                    Icon(
                        imageVector = Icons.Default.WarningAmber,
                        contentDescription = "Urge SOS",
                        modifier = Modifier.size(18.dp),
                        tint = CyberCrimson
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = strings.cravingSosButton,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                FilledTonalButton(
                    onClick = onCheckInClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("daily_checkin_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = EmeraldClean.copy(alpha = 0.2f),
                        contentColor = EmeraldClean
                    )
                ) {
                    Text(
                        text = strings.dailyCheckInButton,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TimerSegment(value: Long, label: String, isAccented: Boolean = false) {
    Row(verticalAlignment = Alignment.Bottom) {
        Text(
            text = String.format("%02d", value),
            fontFamily = JetBrainsMono,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = if (isAccented) AmberGold else TextPrimary
        )
        Text(
            text = label,
            fontFamily = JetBrainsMono,
            fontSize = 12.sp,
            color = TextMuted,
            modifier = Modifier.padding(start = 1.dp, bottom = 2.dp)
        )
    }
}

@Composable
private fun TimerColon() {
    Text(
        text = " : ",
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = TextMuted,
        modifier = Modifier.padding(horizontal = 2.dp)
    )
}
