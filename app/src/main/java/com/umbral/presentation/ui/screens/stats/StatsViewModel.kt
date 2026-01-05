package com.umbral.presentation.ui.screens.stats

import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umbral.data.local.preferences.UmbralPreferences
import com.umbral.domain.stats.StatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val statsRepository: StatsRepository,
    private val preferences: UmbralPreferences,
    private val packageManager: PackageManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    // Default time per attempt in minutes
    private val minutesPerAttempt = 5

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                // Get current streak from preferences
                val currentStreak = preferences.currentStreak.first()

                // Calculate date ranges
                val now = LocalDateTime.now()
                val startOfWeek = now.minusDays(now.dayOfWeek.value.toLong() - 1)
                    .withHour(0).withMinute(0).withSecond(0).withNano(0)
                val startOfLastWeek = startOfWeek.minusWeeks(1)

                // Get this week's attempts count
                val thisWeekAttempts = statsRepository.getAttemptCountSince(startOfWeek)
                val lastWeekAttempts = statsRepository.getAttemptCountSince(startOfLastWeek) - thisWeekAttempts

                // Get daily breakdown for graph (last 7 days)
                val dailyAttempts = (6 downTo 0).map { daysAgo ->
                    val dayStart = now.minusDays(daysAgo.toLong())
                        .withHour(0).withMinute(0).withSecond(0).withNano(0)
                    val dayEnd = dayStart.plusDays(1)

                    val count = statsRepository.getAttemptCountSince(dayStart) -
                            statsRepository.getAttemptCountSince(dayEnd)
                    count.coerceAtLeast(0)
                }

                // Calculate percent change
                val percentChange = if (lastWeekAttempts > 0) {
                    ((thisWeekAttempts - lastWeekAttempts) * 100) / lastWeekAttempts
                } else {
                    0
                }

                // Get top blocked apps
                val topAppsMap = statsRepository.getTopBlockedApps(
                    since = startOfWeek,
                    limit = 5
                )

                val topApps = topAppsMap.map { (packageName, count) ->
                    val appName = try {
                        val appInfo = packageManager.getApplicationInfo(packageName, 0)
                        packageManager.getApplicationLabel(appInfo).toString()
                    } catch (e: PackageManager.NameNotFoundException) {
                        // Extract app name from package name
                        packageName.split(".").lastOrNull()?.replaceFirstChar { it.uppercase() }
                            ?: packageName
                    }

                    BlockedAppStats(
                        packageName = packageName,
                        appName = appName,
                        count = count
                    )
                }.sortedByDescending { it.count }

                // Calculate time saved
                val timeSaved = (thisWeekAttempts * minutesPerAttempt).minutes

                // Determine best streak (for now, use current as best if no history)
                // In a full implementation, you'd track this in preferences
                val bestStreak = maxOf(currentStreak, preferences.currentStreak.first())

                // Check if we have any data
                val isEmpty = thisWeekAttempts == 0 && currentStreak == 0 && topApps.isEmpty()

                _uiState.value = StatsUiState(
                    isLoading = false,
                    isEmpty = isEmpty,
                    streak = StreakStats(
                        current = currentStreak,
                        best = bestStreak,
                        milestones = listOf(7, 14, 21, 30, 60, 90, 100, 365)
                    ),
                    weeklyAttempts = WeeklyAttempts(
                        daily = dailyAttempts,
                        thisWeek = thisWeekAttempts,
                        lastWeek = lastWeekAttempts,
                        percentChange = percentChange
                    ),
                    timeSaved = timeSaved,
                    topApps = topApps
                )
            } catch (e: Exception) {
                // On error, show empty state
                _uiState.value = StatsUiState(
                    isLoading = false,
                    isEmpty = true
                )
            }
        }
    }

    fun refresh() {
        loadStats()
    }
}
