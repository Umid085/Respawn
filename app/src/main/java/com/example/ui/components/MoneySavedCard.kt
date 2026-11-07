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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.AmberGold
import com.example.ui.theme.EmeraldClean
import com.example.ui.theme.JetBrainsMono
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardElevated
import com.example.ui.theme.SlateDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MoneySavedCard(
    moneySaved: Float,
    dailyExpenditureSetting: Float,
    gamesAvoided: Float,
    subscriptionsAvoided: Float,
    onUpdateDailyExpenditure: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("money_saved_dashboard_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            Brush.horizontalGradient(
                listOf(
                    EmeraldClean.copy(alpha = 0.7f),
                    AmberGold.copy(alpha = 0.4f),
                    SlateBorder.copy(alpha = 0.5f)
                )
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header with edit icon
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
                            .background(
                                Brush.radialGradient(
                                    listOf(EmeraldClean.copy(alpha = 0.25f), SlateCardElevated)
                                )
                            )
                            .border(1.dp, EmeraldClean.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Savings,
                            contentDescription = "Money Saved",
                            tint = EmeraldClean,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "MONEY SAVED",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldClean,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Zero Games & Subscriptions",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }

                // Edit daily expenditure button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(SlateCardElevated)
                        .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
                        .clickable { showEditDialog = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("edit_daily_spend_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Spend",
                        tint = AmberGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format("$%.2f/day", dailyExpenditureSetting),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = AmberGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Prominent Money Saved Figure
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = String.format("$%.2f", moneySaved),
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontFamily = JetBrainsMono,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 36.sp
                        ),
                        color = EmeraldClean,
                        modifier = Modifier.testTag("total_money_saved_text")
                    )
                    Text(
                        text = "Kept in your bank account, not spent on pixels",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Breakdown Tiles: Games Avoided & Subscriptions Avoided
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Games Avoided Tile
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SlateCardElevated)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = "Games",
                        tint = AmberGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = String.format("%.1f Games", gamesAvoided),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "@ $70 / release",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = TextMuted
                        )
                    }
                }

                // Subscriptions Avoided Tile
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SlateCardElevated)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Subscriptions,
                        contentDescription = "Subscriptions",
                        tint = EmeraldClean,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = String.format("%.1f Months", subscriptionsAvoided),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "@ $15 / sub",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = TextMuted
                        )
                    }
                }
            }
        }
    }

    if (showEditDialog) {
        EditDailyExpenditureDialog(
            currentSetting = dailyExpenditureSetting,
            onDismiss = { showEditDialog = false },
            onSave = {
                onUpdateDailyExpenditure(it)
                showEditDialog = false
            }
        )
    }
}

@Composable
fun EditDailyExpenditureDialog(
    currentSetting: Float,
    onDismiss: () -> Unit,
    onSave: (Float) -> Unit
) {
    var spendValue by remember { mutableFloatStateOf(currentSetting) }

    val presets = listOf(
        "Casual ($1.50/day)" to 1.50f,
        "Moderate ($3.50/day)" to 3.50f,
        "Passes & Subs ($6.00/day)" to 6.00f,
        "Heavy Gamer ($10.00/day)" to 10.00f
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("edit_daily_expenditure_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldClean.copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AttachMoney,
                        contentDescription = "Dollar",
                        tint = EmeraldClean,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Daily Gaming Expenditure",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Estimate how much you averaged per day on new video games, battle passes, cosmetics, and recurring online subscriptions (e.g. Game Pass, PlayStation Plus, WoW, Final Fantasy, Discord Nitro).",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Current selected figure
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SlateCardElevated)
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Daily Expenditure Setting",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                        Text(
                            text = String.format("$%.2f / day", spendValue),
                            style = MaterialTheme.typography.headlineMedium.copy(fontFamily = JetBrainsMono),
                            fontWeight = FontWeight.Bold,
                            color = EmeraldClean
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Monthly equivalent",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                        Text(
                            text = String.format("$%.0f / month", spendValue * 30f),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AmberGold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Slider
                Slider(
                    value = spendValue,
                    onValueChange = { spendValue = it },
                    valueRange = 0.50f..25.0f,
                    steps = 48,
                    colors = SliderDefaults.colors(
                        thumbColor = EmeraldClean,
                        activeTrackColor = EmeraldClean,
                        inactiveTrackColor = SlateBorder
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Presets
                Text(
                    text = "Quick Presets:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(6.dp))

                presets.forEach { (label, value) ->
                    val isSelected = Math.abs(spendValue - value) < 0.2f
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) EmeraldClean.copy(alpha = 0.2f) else SlateCardElevated)
                            .border(
                                1.dp,
                                if (isSelected) EmeraldClean else SlateBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { spendValue = value }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isSelected) TextPrimary else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                        Text(
                            text = String.format("$%.2f", value),
                            style = MaterialTheme.typography.labelSmall,
                            color = EmeraldClean
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

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
                        onClick = { onSave(spendValue) },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldClean),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Save Setting", color = SlateDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
