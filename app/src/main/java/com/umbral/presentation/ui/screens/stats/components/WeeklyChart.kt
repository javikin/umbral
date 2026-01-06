package com.umbral.presentation.ui.screens.stats.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umbral.data.local.dao.DailyStats
import com.umbral.presentation.ui.components.UmbralCard
import com.umbral.presentation.ui.components.UmbralElevation
import com.umbral.presentation.ui.theme.UmbralSpacing
import com.umbral.presentation.ui.theme.UmbralTheme
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

/**
 * WeeklyChartCard - Bar chart showing the last 7 days of blocking statistics
 *
 * Displays a bar chart with:
 * - Day labels in Spanish (L, M, X, J, V, S, D)
 * - Highlight current day with primary color
 * - Handle empty data gracefully
 * - Calculate height based on max value
 *
 * @param dailyStats List of daily statistics from database
 * @param modifier Modifier for customization
 */
@Composable
fun WeeklyChartCard(
    dailyStats: List<DailyStats>,
    modifier: Modifier = Modifier
) {
    UmbralCard(
        modifier = modifier.fillMaxWidth(),
        elevation = UmbralElevation.Subtle
    ) {
        Text(
            text = "Últimos 7 días",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(UmbralSpacing.md))

        if (dailyStats.isEmpty()) {
            // Empty state
            Text(
                text = "Sin datos para mostrar",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = UmbralSpacing.lg),
                textAlign = TextAlign.Center
            )
        } else {
            WeeklyBarChart(
                dailyStats = dailyStats,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * WeeklyBarChart - The actual bar chart visualization
 *
 * @param dailyStats List of daily statistics
 * @param modifier Modifier for customization
 */
@Composable
private fun WeeklyBarChart(
    dailyStats: List<DailyStats>,
    modifier: Modifier = Modifier
) {
    // Prepare data for last 7 days
    val last7Days = prepareLast7DaysData(dailyStats)
    val today = LocalDate.now()
    val maxMinutes = last7Days.maxOfOrNull { it.minutes } ?: 1

    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    Column(modifier = modifier) {
        // Bar chart
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            val barCount = 7
            val spacing = 8.dp.toPx()
            val totalSpacing = spacing * (barCount - 1)
            val barWidth = (size.width - totalSpacing) / barCount
            val maxHeight = size.height

            last7Days.forEachIndexed { index, dayData ->
                val barHeight = if (maxMinutes > 0) {
                    (dayData.minutes.toFloat() / maxMinutes) * maxHeight * 0.9f
                } else {
                    0f
                }

                val x = index * (barWidth + spacing)
                val y = maxHeight - barHeight

                // Determine if this is today
                val isToday = dayData.day == today.toString()

                // Draw bar
                drawRoundRect(
                    color = if (isToday) primaryColor else surfaceVariant,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(4.dp.toPx())
                )
            }
        }

        Spacer(modifier = Modifier.height(UmbralSpacing.sm))

        // Day labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            last7Days.forEach { dayData ->
                val date = LocalDate.parse(dayData.day)
                val isToday = dayData.day == today.toString()

                Text(
                    text = getDayLabel(date),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                    color = if (isToday) primaryColor else onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Prepare data for the last 7 days, filling in missing days with 0 minutes
 */
private fun prepareLast7DaysData(dailyStats: List<DailyStats>): List<DailyStats> {
    val today = LocalDate.now()
    val statsMap = dailyStats.associateBy { it.day }

    return (6 downTo 0).map { daysAgo ->
        val date = today.minusDays(daysAgo.toLong())
        val dateString = date.toString()
        statsMap[dateString] ?: DailyStats(dateString, 0)
    }
}

/**
 * Get Spanish day label (L, M, X, J, V, S, D)
 */
private fun getDayLabel(date: LocalDate): String {
    return when (date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("es"))) {
        "lun." -> "L"
        "mar." -> "M"
        "mié." -> "X"
        "jue." -> "J"
        "vie." -> "V"
        "sáb." -> "S"
        "dom." -> "D"
        else -> date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("es")).first().toString()
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Weekly Chart - With Data", showBackground = true)
@Composable
private fun WeeklyChartCardPreview() {
    UmbralTheme {
        WeeklyChartCard(
            dailyStats = listOf(
                DailyStats("2026-01-01", 90),
                DailyStats("2026-01-02", 120),
                DailyStats("2026-01-03", 100),
                DailyStats("2026-01-04", 80),
                DailyStats("2026-01-05", 150)
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Weekly Chart - Empty", showBackground = true)
@Composable
private fun WeeklyChartCardEmptyPreview() {
    UmbralTheme {
        WeeklyChartCard(
            dailyStats = emptyList(),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Weekly Chart - Dark", showBackground = true)
@Composable
private fun WeeklyChartCardDarkPreview() {
    UmbralTheme(darkTheme = true) {
        WeeklyChartCard(
            dailyStats = listOf(
                DailyStats("2026-01-01", 60),
                DailyStats("2026-01-03", 90),
                DailyStats("2026-01-05", 120)
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
