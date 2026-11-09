package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanClarity
import com.example.ui.theme.EmeraldClean
import com.example.ui.theme.JetBrainsMono
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class TrendDayPoint(
    val dayIndex: Int, // 1 to 30
    val labelDate: String, // e.g. "Nov 1"
    val daysCleanAtDate: Float, // cumulative clean days up to that day
    val isClean: Boolean,
    val isMilestone: Boolean,
    val milestoneTitle: String? = null
)

@Composable
fun GamingFreeTrendChart(
    quitTimestamp: Long,
    currentStreakDays: Long,
    modifier: Modifier = Modifier
) {
    val now = System.currentTimeMillis()

    // Generate 30 daily data points covering the last month
    val trendPoints = remember(quitTimestamp, currentStreakDays) {
        val points = mutableListOf<TrendDayPoint>()
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())

        for (i in 29 downTo 0) {
            calendar.timeInMillis = now - (i * 86400000L)
            val dateLabel = dateFormat.format(Date(calendar.timeInMillis))
            val dayNumber = 30 - i

            val elapsedMillisOnDate = (calendar.timeInMillis - quitTimestamp).coerceAtLeast(0L)
            val cleanDaysOnDate = (elapsedMillisOnDate / 86400000f).coerceAtLeast(0f)

            val milestoneTitle = when {
                cleanDaysOnDate >= 30f && (cleanDaysOnDate - 1f) < 30f -> "One Month Free"
                cleanDaysOnDate >= 21f && (cleanDaysOnDate - 1f) < 21f -> "21d Neuroplastic"
                cleanDaysOnDate >= 14f && (cleanDaysOnDate - 1f) < 14f -> "2 Weeks Free"
                cleanDaysOnDate >= 7f && (cleanDaysOnDate - 1f) < 7f -> "First Week Clean"
                else -> null
            }

            points.add(
                TrendDayPoint(
                    dayIndex = dayNumber,
                    labelDate = dateLabel,
                    daysCleanAtDate = cleanDaysOnDate,
                    isClean = calendar.timeInMillis >= quitTimestamp,
                    isMilestone = milestoneTitle != null,
                    milestoneTitle = milestoneTitle
                )
            )
        }
        points
    }

    var selectedPointIndex by remember { mutableIntStateOf(trendPoints.lastIndex) }
    val selectedPoint = trendPoints.getOrNull(selectedPointIndex) ?: trendPoints.last()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("gaming_free_trend_chart_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Brush.horizontalGradient(listOf(EmeraldClean.copy(alpha = 0.6f), SlateBorder))
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(EmeraldClean.copy(alpha = 0.15f))
                            .border(1.dp, EmeraldClean.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShowChart,
                            contentDescription = "Trend Line",
                            tint = EmeraldClean,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "30-DAY PROGRESSION TREND",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldClean,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Gaming-Free Days",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(EmeraldClean.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = "Upward",
                        tint = EmeraldClean,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "100% DISCIPLINE",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        fontWeight = FontWeight.Bold,
                        color = EmeraldClean,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Interactive Point Scrubber Banner
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
                        text = "${selectedPoint.labelDate} (Day ${selectedPoint.dayIndex})",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                    Text(
                        text = String.format(java.util.Locale.US, "%.1f Days Clean", selectedPoint.daysCleanAtDate),
                        style = MaterialTheme.typography.titleMedium.copy(fontFamily = JetBrainsMono),
                        fontWeight = FontWeight.Bold,
                        color = if (selectedPoint.isClean) EmeraldClean else TextMuted
                    )
                }

                if (selectedPoint.milestoneTitle != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AmberGold.copy(alpha = 0.2f))
                            .border(1.dp, AmberGold.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "🏆 ${selectedPoint.milestoneTitle}",
                            style = MaterialTheme.typography.labelSmall,
                            color = AmberGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Text(
                        text = if (selectedPoint.isClean) "Gaming Free ✓" else "Pre-Sobriety",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selectedPoint.isClean) EmeraldClean else TextMuted,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Native Jetpack Compose Trend Line Canvas
            val maxDays = (trendPoints.maxOfOrNull { it.daysCleanAtDate } ?: 30f).coerceAtLeast(30f)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .pointerInput(trendPoints) {
                            detectTapGestures { offset ->
                                val width = size.width
                                val segmentWidth = width / (trendPoints.size - 1).coerceAtLeast(1)
                                val index = (offset.x / segmentWidth).toInt().coerceIn(0, trendPoints.lastIndex)
                                selectedPointIndex = index
                            }
                        }
                ) {
                    val width = size.width
                    val height = size.height
                    val bottomPadding = 20f
                    val topPadding = 20f
                    val chartHeight = height - bottomPadding - topPadding

                    // 1. Draw horizontal grid milestone lines (7d, 14d, 21d, 30d)
                    val milestones = listOf(7f, 14f, 21f, 30f)
                    val dashedEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

                    milestones.forEach { milestoneValue ->
                        if (milestoneValue <= maxDays) {
                            val y = topPadding + chartHeight * (1f - (milestoneValue / maxDays))
                            drawLine(
                                color = SlateBorder.copy(alpha = 0.5f),
                                start = Offset(0f, y),
                                end = Offset(width, y),
                                strokeWidth = 1.dp.toPx(),
                                pathEffect = dashedEffect
                            )
                        }
                    }

                    // 2. Build smooth cubic bezier curve
                    val linePath = Path()
                    val fillPath = Path()

                    val coordinates = trendPoints.mapIndexed { index, point ->
                        val x = (index.toFloat() / (trendPoints.size - 1)) * width
                        val normalizedY = (point.daysCleanAtDate / maxDays).coerceIn(0f, 1f)
                        val y = topPadding + chartHeight * (1f - normalizedY)
                        Offset(x, y)
                    }

                    if (coordinates.isNotEmpty()) {
                        linePath.moveTo(coordinates.first().x, coordinates.first().y)
                        fillPath.moveTo(coordinates.first().x, coordinates.first().y)

                        for (i in 0 until coordinates.size - 1) {
                            val p0 = coordinates[i]
                            val p1 = coordinates[i + 1]
                            val controlPoint1 = Offset(p0.x + (p1.x - p0.x) / 2f, p0.y)
                            val controlPoint2 = Offset(p0.x + (p1.x - p0.x) / 2f, p1.y)
                            linePath.cubicTo(
                                controlPoint1.x, controlPoint1.y,
                                controlPoint2.x, controlPoint2.y,
                                p1.x, p1.y
                            )
                            fillPath.cubicTo(
                                controlPoint1.x, controlPoint1.y,
                                controlPoint2.x, controlPoint2.y,
                                p1.x, p1.y
                            )
                        }

                        // Close fill path down to chart bottom
                        fillPath.lineTo(width, height - bottomPadding)
                        fillPath.lineTo(0f, height - bottomPadding)
                        fillPath.close()

                        // Draw Gradient Fill
                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    EmeraldClean.copy(alpha = 0.35f),
                                    CyanClarity.copy(alpha = 0.15f),
                                    Color.Transparent
                                ),
                                startY = topPadding,
                                endY = height - bottomPadding
                            )
                        )

                        // Draw Line Glow
                        drawPath(
                            path = linePath,
                            brush = Brush.horizontalGradient(
                                listOf(EmeraldClean, CyanClarity, AmberGold)
                            ),
                            style = Stroke(
                                width = 3.dp.toPx(),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )

                        // 3. Draw active/selected indicator point
                        val selectedCoord = coordinates.getOrNull(selectedPointIndex)
                        if (selectedCoord != null) {
                            // Vertical tracking beam
                            drawLine(
                                color = EmeraldClean.copy(alpha = 0.4f),
                                start = Offset(selectedCoord.x, topPadding),
                                end = Offset(selectedCoord.x, height - bottomPadding),
                                strokeWidth = 1.5.dp.toPx(),
                                pathEffect = dashedEffect
                            )

                            // Outer halo
                            drawCircle(
                                color = EmeraldClean.copy(alpha = 0.3f),
                                radius = 10.dp.toPx(),
                                center = selectedCoord
                            )
                            // Inner core
                            drawCircle(
                                color = AmberGold,
                                radius = 5.dp.toPx(),
                                center = selectedCoord
                            )
                            drawCircle(
                                color = SlateCard,
                                radius = 2.dp.toPx(),
                                center = selectedCoord
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // X-Axis Date Labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "30 Days Ago",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = TextMuted
                )
                Text(
                    text = "Day 10",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = TextMuted
                )
                Text(
                    text = "Day 20",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = TextMuted
                )
                Text(
                    text = "Today",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    fontWeight = FontWeight.Bold,
                    color = EmeraldClean
                )
            }
        }
    }
}
