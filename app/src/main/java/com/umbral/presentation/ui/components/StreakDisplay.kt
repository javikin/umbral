package com.umbral.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umbral.presentation.ui.theme.UmbralSpacing
import com.umbral.presentation.ui.theme.UmbralTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

/**
 * Umbral Streak Display Component
 *
 * Shows the current streak with an animated fire icon and optional mini calendar
 * showing the last 7 days.
 *
 * @param currentStreak Number of consecutive days
 * @param modifier Modifier for customization
 * @param showMiniCalendar Whether to show the 7-day calendar
 * @param completedDays Set of days that were completed (for calendar)
 */
@Composable
fun StreakDisplay(
    currentStreak: Int,
    modifier: Modifier = Modifier,
    showMiniCalendar: Boolean = true,
    completedDays: Set<LocalDate> = emptySet()
) {
    var isVisible by remember { mutableStateOf(false) }
    val isMilestone = currentStreak in listOf(7, 14, 21, 30, 60, 90, 100, 365)

    LaunchedEffect(Unit) {
        isVisible = true
    }

    val streakScale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.5f,
        animationSpec = spring(
            dampingRatio = 0.5f,
            stiffness = 300f
        ),
        label = "streakScale"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Main streak display
        Row(
            modifier = Modifier.scale(streakScale),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Fire icon with animation
            PulsingIcon(
                icon = Icons.Outlined.LocalFireDepartment,
                modifier = Modifier.size(UmbralSpacing.iconSizeXLarge),
                tint = if (currentStreak > 0) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline
                }
            )

            Spacer(modifier = Modifier.width(UmbralSpacing.sm))

            Column {
                Text(
                    text = "$currentStreak",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (currentStreak == 1) "día" else "días",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Milestone celebration
        AnimatedVisibility(
            visible = isMilestone && isVisible,
            enter = fadeIn() + scaleIn()
        ) {
            Text(
                text = getMilestoneMessage(currentStreak),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = UmbralSpacing.sm)
            )
        }

        // Mini calendar
        if (showMiniCalendar) {
            Spacer(modifier = Modifier.height(UmbralSpacing.md))
            MiniCalendar(
                completedDays = completedDays.ifEmpty {
                    // Default: last N days are completed based on streak
                    (0 until minOf(currentStreak, 7)).map {
                        LocalDate.now().minusDays(it.toLong())
                    }.toSet()
                }
            )
        }
    }
}

@Composable
private fun MiniCalendar(
    completedDays: Set<LocalDate>,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val last7Days = (6 downTo 0).map { today.minusDays(it.toLong()) }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(UmbralSpacing.xs)
    ) {
        last7Days.forEach { date ->
            DayDot(
                dayOfWeek = date.dayOfWeek,
                isCompleted = completedDays.contains(date),
                isToday = date == today
            )
        }
    }
}

@Composable
private fun DayDot(
    dayOfWeek: DayOfWeek,
    isCompleted: Boolean,
    isToday: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Day label
        Text(
            text = dayOfWeek.getDisplayName(TextStyle.NARROW, Locale("es")).uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = if (isToday) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Completion dot
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isCompleted -> MaterialTheme.colorScheme.primary
                        isToday -> MaterialTheme.colorScheme.primaryContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onPrimary)
                )
            }
        }
    }
}

private fun getMilestoneMessage(streak: Int): String {
    return when (streak) {
        7 -> "🎉 ¡Una semana!"
        14 -> "🔥 ¡Dos semanas!"
        21 -> "💪 ¡Tres semanas!"
        30 -> "🏆 ¡Un mes!"
        60 -> "⭐ ¡Dos meses!"
        90 -> "🌟 ¡Tres meses!"
        100 -> "💯 ¡100 días!"
        365 -> "🎊 ¡Un año!"
        else -> ""
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Streak - Day 1", showBackground = true)
@Composable
private fun StreakDisplayDay1Preview() {
    UmbralTheme {
        StreakDisplay(
            currentStreak = 1,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Streak - Week", showBackground = true)
@Composable
private fun StreakDisplayWeekPreview() {
    UmbralTheme {
        StreakDisplay(
            currentStreak = 7,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Streak - Month", showBackground = true)
@Composable
private fun StreakDisplayMonthPreview() {
    UmbralTheme {
        StreakDisplay(
            currentStreak = 30,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Streak - Zero", showBackground = true)
@Composable
private fun StreakDisplayZeroPreview() {
    UmbralTheme {
        StreakDisplay(
            currentStreak = 0,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Streak - No Calendar", showBackground = true)
@Composable
private fun StreakDisplayNoCalendarPreview() {
    UmbralTheme {
        StreakDisplay(
            currentStreak = 15,
            showMiniCalendar = false,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Dark Theme Streak", showBackground = true)
@Composable
private fun StreakDisplayDarkPreview() {
    UmbralTheme(darkTheme = true) {
        StreakDisplay(
            currentStreak = 14,
            modifier = Modifier.padding(16.dp)
        )
    }
}
