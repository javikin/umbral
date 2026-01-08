package com.umbral.notifications.presentation.history

import com.umbral.notifications.domain.model.BlockedNotification

/**
 * UI state for Notification History screen.
 *
 * @property notifications All notifications (filtered or not)
 * @property groupedNotifications Notifications grouped by session ID
 * @property availableApps List of unique app names for filter dropdown
 * @property selectedApp Currently selected app filter (null = all apps)
 * @property selectedPeriod Currently selected time period filter
 * @property isLoading Whether data is being loaded
 */
data class NotificationHistoryState(
    val notifications: List<BlockedNotification> = emptyList(),
    val groupedNotifications: Map<String, List<BlockedNotification>> = emptyMap(),
    val availableApps: List<String> = emptyList(),
    val selectedApp: String? = null,
    val selectedPeriod: FilterPeriod = FilterPeriod.ALL,
    val isLoading: Boolean = true
)

/**
 * Time period filter options.
 */
enum class FilterPeriod(val displayName: String) {
    ALL("Todas"),
    TODAY("Hoy"),
    YESTERDAY("Ayer"),
    WEEK("Semana")
}
