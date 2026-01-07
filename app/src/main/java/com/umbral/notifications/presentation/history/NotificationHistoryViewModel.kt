package com.umbral.notifications.presentation.history

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umbral.notifications.domain.repository.NotificationRepository
import com.umbral.notifications.domain.usecase.GetNotificationHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

/**
 * ViewModel for Notification History screen.
 *
 * Manages notification history data, filtering, and user actions like
 * marking as read, opening apps, and deleting notifications.
 */
@HiltViewModel
class NotificationHistoryViewModel @Inject constructor(
    private val getHistoryUseCase: GetNotificationHistoryUseCase,
    private val repository: NotificationRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationHistoryState())
    val state: StateFlow<NotificationHistoryState> = _state.asStateFlow()

    // Keep unfiltered notifications for filtering
    private var allNotifications: List<com.umbral.notifications.domain.model.BlockedNotification> = emptyList()

    init {
        loadNotifications()
    }

    /**
     * Load all notifications from repository.
     */
    private fun loadNotifications() {
        viewModelScope.launch {
            getHistoryUseCase.getRecent(limit = 1000)
                .collectLatest { notifications ->
                    allNotifications = notifications
                    _state.update {
                        it.copy(
                            notifications = notifications,
                            groupedNotifications = groupBySession(notifications),
                            availableApps = notifications.map { n -> n.appName }.distinct().sorted(),
                            isLoading = false
                        )
                    }
                    applyFilters()
                }
        }
    }

    /**
     * Group notifications by session ID for organized display.
     */
    private fun groupBySession(
        notifications: List<com.umbral.notifications.domain.model.BlockedNotification>
    ): Map<String, List<com.umbral.notifications.domain.model.BlockedNotification>> {
        return notifications.groupBy { it.sessionId }
    }

    /**
     * Set app filter.
     * @param app App name to filter by, or null for all apps
     */
    fun setAppFilter(app: String?) {
        _state.update { it.copy(selectedApp = app) }
        applyFilters()
    }

    /**
     * Set time period filter.
     * @param period The time period to filter by
     */
    fun setPeriodFilter(period: FilterPeriod) {
        _state.update { it.copy(selectedPeriod = period) }
        applyFilters()
    }

    /**
     * Apply current filters to notification list.
     */
    private fun applyFilters() {
        var filtered = allNotifications

        // Apply app filter
        _state.value.selectedApp?.let { app ->
            filtered = filtered.filter { it.appName == app }
        }

        // Apply period filter
        filtered = when (_state.value.selectedPeriod) {
            FilterPeriod.TODAY -> filtered.filter { it.timestamp.isToday() }
            FilterPeriod.YESTERDAY -> filtered.filter { it.timestamp.isYesterday() }
            FilterPeriod.WEEK -> filtered.filter { it.timestamp.isWithinLastWeek() }
            FilterPeriod.ALL -> filtered
        }

        _state.update {
            it.copy(
                notifications = filtered,
                groupedNotifications = groupBySession(filtered)
            )
        }
    }

    /**
     * Mark a notification as read.
     * @param id Notification ID
     */
    fun markAsRead(id: Long) {
        viewModelScope.launch {
            repository.markAsRead(id)
        }
    }

    /**
     * Open the app that sent the notification.
     * @param packageName Package name of the app
     */
    fun openApp(packageName: String) {
        try {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            // App not found, silently fail
        } catch (e: Exception) {
            // Handle other exceptions
        }
    }

    /**
     * Delete a notification.
     * @param id Notification ID
     */
    fun deleteNotification(id: Long) {
        viewModelScope.launch {
            repository.delete(id)
        }
    }

    /**
     * Refresh notification list (for pull-to-refresh).
     */
    fun refresh() {
        loadNotifications()
    }
}

/**
 * Extension functions for Instant date filtering.
 */
private fun Instant.isToday(): Boolean {
    val today = LocalDate.now()
    val date = this.atZone(ZoneId.systemDefault()).toLocalDate()
    return date == today
}

private fun Instant.isYesterday(): Boolean {
    val yesterday = LocalDate.now().minusDays(1)
    val date = this.atZone(ZoneId.systemDefault()).toLocalDate()
    return date == yesterday
}

private fun Instant.isWithinLastWeek(): Boolean {
    val weekAgo = LocalDate.now().minusDays(7)
    val date = this.atZone(ZoneId.systemDefault()).toLocalDate()
    return date >= weekAgo
}
