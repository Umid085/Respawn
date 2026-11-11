package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Badge
import com.example.data.model.BadgeCategory
import com.example.data.model.BadgeTier
import com.example.ui.theme.AmberGold
import com.example.ui.theme.BadgeBronze
import com.example.ui.theme.BadgeDiamond
import com.example.ui.theme.BadgeGold
import com.example.ui.theme.BadgePlatinum
import com.example.ui.theme.BadgeSilver
import com.example.ui.theme.CyanClarity
import com.example.ui.theme.EmeraldClean
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BadgeCard(
    badge: Badge,
    currentValue: Float, // current streak days or hours saved
    strings: com.example.ui.i18n.StringsDefinition = com.example.ui.i18n.AppStrings.Russian,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val isUnlocked = badge.unlockedAt != null

    val tierColor = when (badge.tier) {
        BadgeTier.BRONZE -> BadgeBronze
        BadgeTier.SILVER -> BadgeSilver
        BadgeTier.GOLD -> BadgeGold
        BadgeTier.PLATINUM -> BadgePlatinum
        BadgeTier.DIAMOND -> BadgeDiamond
    }

    val iconVector = when (badge.tier) {
        BadgeTier.DIAMOND -> Icons.Default.Diamond
        BadgeTier.PLATINUM -> Icons.Default.EmojiEvents
        BadgeTier.GOLD -> Icons.Default.Star
        BadgeTier.SILVER -> Icons.Default.Shield
        BadgeTier.BRONZE -> Icons.Default.EmojiEvents
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("badge_card_${badge.id}")
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) SlateCard else SlateCard.copy(alpha = 0.6f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isUnlocked) {
                Brush.linearGradient(
                    listOf(tierColor.copy(alpha = 0.8f), SlateBorder.copy(alpha = 0.4f))
                )
            } else {
                Brush.linearGradient(listOf(SlateBorder.copy(alpha = 0.3f), SlateBorder.copy(alpha = 0.3f)))
            }
        )
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
                // Badge Icon with Tier Halo
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(
                            if (isUnlocked) {
                                Brush.radialGradient(
                                    listOf(tierColor.copy(alpha = 0.25f), SlateCardElevated)
                                )
                            } else {
                                Brush.radialGradient(listOf(SlateCardElevated, SlateCardElevated))
                            }
                        )
                        .border(
                            1.5.dp,
                            if (isUnlocked) tierColor else SlateBorder.copy(alpha = 0.5f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isUnlocked) iconVector else Icons.Default.Lock,
                        contentDescription = badge.title,
                        tint = if (isUnlocked) tierColor else TextMuted,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Title and Tier Pill
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = badge.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isUnlocked) TextPrimary else TextSecondary
                        )

                        // Tier Tag
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(tierColor.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badge.tier.name,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = tierColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = badge.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isUnlocked) TextSecondary else TextMuted,
                        maxLines = if (expanded) Int.MAX_VALUE else 2
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress or Unlock Status
            if (isUnlocked) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Unlocked",
                            tint = EmeraldClean,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        val formattedDate = badge.unlockedAt?.let {
                            SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(it))
                        } ?: "Awarded"
                        val isRu = strings == com.example.ui.i18n.AppStrings.Russian
                        Text(
                            text = if (isRu) "Разблокировано: $formattedDate" else "Unlocked $formattedDate",
                            style = MaterialTheme.typography.labelSmall,
                            color = EmeraldClean,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Text(
                        text = "+${badge.xpReward} XP",
                        style = MaterialTheme.typography.labelSmall,
                        color = AmberGold,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                // Locked progress indicator
                val progress = (currentValue / badge.requiredThreshold).coerceIn(0f, 1f)
                val isRu = strings == com.example.ui.i18n.AppStrings.Russian
                val unitLabel = when (badge.category) {
                    BadgeCategory.STREAK_DAYS -> if (isRu) "дн." else "days"
                    BadgeCategory.HOURS_SAVED -> if (isRu) "ч." else "hrs"
                    BadgeCategory.MONEY_SAVED -> "$"
                    BadgeCategory.CRAVING_SURF -> if (isRu) "побед" else "urges"
                    BadgeCategory.QUESTS -> if (isRu) "квестов" else "quests"
                }

                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${strings.requiredLabel}: ${badge.requiredThreshold.toInt()} $unitLabel",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                        Text(
                            text = String.format("%.1f / %.0f %s", currentValue, badge.requiredThreshold, unitLabel),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = tierColor,
                        trackColor = SlateBorder
                    )
                }
            }

            // Expanded Scientific Benefit & Psychology
            AnimatedVisibility(visible = expanded) {
                val isRu = strings == com.example.ui.i18n.AppStrings.Russian
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SlateCardElevated)
                        .padding(12.dp)
                ) {
                    Text(
                        text = if (isRu) "🧠 Нейробиологический эффект" else "🧠 Neurological Impact & Lore",
                        style = MaterialTheme.typography.labelMedium,
                        color = CyanClarity,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = badge.neuroBenefit,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
