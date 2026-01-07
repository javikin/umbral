package com.umbral.notifications.domain.repository

import com.umbral.notifications.domain.model.AppNotificationStats
import com.umbral.notifications.domain.model.BlockedNotification
import com.umbral.notifications.domain.model.NotificationSummary
import com.umbral.notifications.domain.model.NotificationStats
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for notification data operations.
 *
 * Provides abstraction over data access for blocked notifications.
 * All operations use domain models, not database entities.
 */
interface NotificationRepository {

    /**
     * Save a blocked notification.
     * @param notification The notification to save
     * @return The row ID of the inserted notification
     */
    suspend fun save(notification: BlockedNotification): Long

    /**
     * Get all notifications for a specific blocking session.
     * @param sessionId The session ID to filter by
     * @return Flow of notifications ordered by timestamp (newest first)
     */
    fun getBySession(sessionId: String): Flow<List<BlockedNotification>>

    /**
     * Get recent notifications with a limit.
     * @param limit Maximum number of notifications to return (default: 100)
     * @return Flow of recent notifications
     */
    fun getRecent(limit: Int = 100): Flow<List<BlockedNotification>>

    /**
     * Get notification summary for a session.
     * Includes total count and breakdown by app.
     * @param sessionId The session ID to get summary for
     * @return Summary with counts and statistics
     */
    suspend fun getSummaryForSession(sessionId: String): NotificationSummary

    /**
     * Get total count of all blocked notifications.
     * @return Total number of notifications in database
     */
    suspend fun getTotalBlockedCount(): Int

    /**
     * Mark a notification as read.
     * @param id The notification ID to mark
     */
    suspend fun markAsRead(id: Long)

    /**
     * Delete a specific notification.
     * @param id The notification ID to delete
     */
    suspend fun delete(id: Long)

    /**
     * Delete notifications older than a certain number of days.
     * Used for automatic cleanup of old data.
     * @param days Number of days to keep (delete older than this)
     */
    suspend fun clearOlderThan(days: Int)

    /**
     * Trim notifications to keep only the most recent ones.
     * Implements FIFO cleanup to maintain storage limits.
     * @param maxCount Maximum number of notifications to keep (default: 1000)
     */
    suspend fun trimToLimit(maxCount: Int = 1000)

    // ========== Gamification Methods ==========

    /**
     * Get total blocked count as a Flow for reactive updates.
     * Used for achievement progress tracking.
     * @return Flow emitting the total blocked count
     */
    fun getTotalBlockedCountFlow(): Flow<Int>

    /**
     * Get count of notifications blocked for a specific session.
     * Used for calculating energy bonus at session end.
     * @param sessionId The session ID to count for
     * @return Number of notifications blocked in the session
     */
    suspend fun getCountForSession(sessionId: String): Int

    /**
     * Get top apps by blocked notification count.
     * @param limit Maximum number of apps to return (default: 5)
     * @return List of apps with their notification counts, ordered by count descending
     */
    suspend fun getTopBlockedApps(limit: Int = 5): List<AppNotificationStats>

    /**
     * Get notification statistics for the stats screen.
     * Includes total count, last 7 days count, and top apps.
     * @return Aggregated notification statistics
     */
    suspend fun getNotificationStats(): NotificationStats

    /**
     * Get count of notifications blocked in the last N days.
     * @param days Number of days to look back
     * @return Number of notifications blocked in the period
     */
    suspend fun getCountForLastDays(days: Int): Int
}
