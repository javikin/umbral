package com.umbral.presentation.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umbral.data.local.dao.AppAttemptCount
import com.umbral.data.local.dao.DailyStats
import com.umbral.domain.stats.StatsRepository
import com.umbral.notifications.domain.model.AppNotificationStats
import com.umbral.notifications.domain.model.NotificationStats
import com.umbral.notifications.domain.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val statsRepository: StatsRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        loadStats()
    }

    fun loadStats() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                // Load blocking stats and notification stats in parallel
                val todayStatsDeferred = async { statsRepository.getTodayStats() }
                val weeklyStatsDeferred = async { statsRepository.getWeeklyStats() }
                val notificationStatsDeferred = async { notificationRepository.getNotificationStats() }

                val todayStats = todayStatsDeferred.await()
                val weeklyStats = weeklyStatsDeferred.await()
                val notificationStats = notificationStatsDeferred.await()

                // Calculate percentage change
                val percentageChange = calculatePercentageChange(
                    current = weeklyStats.totalMinutes,
                    previous = weeklyStats.previousWeekMinutes
                )

                _uiState.value = StatsUiState(
                    isLoading = false,
                    error = null,
                    todayBlockedMinutes = todayStats.blockedMinutes,
                    todayAttempts = todayStats.attemptCount,
                    weeklyBlockedMinutes = weeklyStats.totalMinutes,
                    previousWeekMinutes = weeklyStats.previousWeekMinutes,
                    percentageChange = percentageChange,
                    dailyStats = weeklyStats.dailyStats,
                    topApps = weeklyStats.topApps,
                    notificationStats = notificationStats
                )

                Timber.d("Stats loaded successfully: Today=${todayStats.blockedMinutes}min, Week=${weeklyStats.totalMinutes}min, Notifications=${notificationStats.totalBlocked}")
            } catch (e: Exception) {
                Timber.e(e, "Error loading stats")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error al cargar estadisticas"
                )
            }
        }
    }

    private fun calculatePercentageChange(current: Int, previous: Int): Int {
        if (previous == 0) {
            return if (current > 0) 100 else 0
        }
        return ((current - previous) * 100) / previous
    }
}

/**
 * UI state for the stats screen.
 */
data class StatsUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val todayBlockedMinutes: Int = 0,
    val todayAttempts: Int = 0,
    val weeklyBlockedMinutes: Int = 0,
    val previousWeekMinutes: Int = 0,
    val percentageChange: Int = 0,
    val dailyStats: List<DailyStats> = emptyList(),
    val topApps: List<AppAttemptCount> = emptyList(),
    val notificationStats: NotificationStats? = null
)
