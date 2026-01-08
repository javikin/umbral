package com.umbral.notifications.domain.model

import kotlin.time.Duration

/**
 * Summary of notifications for a blocking session.
 * Used for post-session reports and statistics.
 *
 * @property sessionId ID of the blocking session
 * @property totalCount Total number of notifications blocked
 * @property byApp Breakdown of notifications by app
 * @property sessionDuration Duration of the blocking session
 */
data class NotificationSummary(
    val sessionId: String,
    val totalCount: Int,
    val byApp: List<AppCount>,
    val sessionDuration: Duration
) {
    /**
     * Count of notifications for a specific app.
     *
     * @property packageName Package name of the app
     * @property appName Human-readable app name
     * @property count Number of notifications from this app
     */
    data class AppCount(
        val packageName: String,
        val appName: String,
        val count: Int
    )
}
