package com.umbral.notifications.domain.model

/**
 * Aggregated notification statistics for the stats screen.
 *
 * @property totalBlocked Total number of notifications blocked all time
 * @property last7Days Number of notifications blocked in the last 7 days
 * @property topApps List of apps with the most blocked notifications
 */
data class NotificationStats(
    val totalBlocked: Int,
    val last7Days: Int,
    val topApps: List<AppNotificationStats>
)

/**
 * Statistics for a single app's blocked notifications.
 *
 * @property packageName Package name of the app
 * @property appName Human-readable app name
 * @property count Number of blocked notifications from this app
 */
data class AppNotificationStats(
    val packageName: String,
    val appName: String,
    val count: Int
)
