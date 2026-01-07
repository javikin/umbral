package com.umbral.notifications.data.local

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO for accessing blocked notifications.
 * Provides CRUD operations and queries for notification management.
 */
@Dao
interface BlockedNotificationDao {

    /**
     * Insert a new blocked notification.
     * @param notification The notification to insert
     * @return The row ID of the inserted notification
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: BlockedNotificationEntity): Long

    /**
     * Get all notifications for a specific blocking session.
     * @param sessionId The session ID to filter by
     * @return Flow of notifications ordered by timestamp (newest first)
     */
    @Query("SELECT * FROM blocked_notifications WHERE session_id = :sessionId ORDER BY timestamp DESC")
    fun getBySession(sessionId: String): Flow<List<BlockedNotificationEntity>>

    /**
     * Get all blocked notifications.
     * @return Flow of all notifications ordered by timestamp (newest first)
     */
    @Query("SELECT * FROM blocked_notifications ORDER BY timestamp DESC")
    fun getAll(): Flow<List<BlockedNotificationEntity>>

    /**
     * Get recent notifications with a limit.
     * @param limit Maximum number of notifications to return
     * @return Flow of recent notifications
     */
    @Query("SELECT * FROM blocked_notifications ORDER BY timestamp DESC LIMIT :limit")
    fun getRecent(limit: Int = 100): Flow<List<BlockedNotificationEntity>>

    /**
     * Get notification count by app for a specific session.
     * Groups notifications by package name and counts them.
     * @param sessionId The session ID to filter by
     * @return List of notification counts per app
     */
    @Query("""
        SELECT package_name, app_name, COUNT(*) as count
        FROM blocked_notifications
        WHERE session_id = :sessionId
        GROUP BY package_name
        ORDER BY count DESC
    """)
    suspend fun getCountByAppForSession(sessionId: String): List<AppNotificationCount>

    /**
     * Get total count of notifications for a session.
     * @param sessionId The session ID to count for
     * @return Number of notifications in the session
     */
    @Query("SELECT COUNT(*) FROM blocked_notifications WHERE session_id = :sessionId")
    suspend fun getCountForSession(sessionId: String): Int

    /**
     * Get total count of all blocked notifications.
     * @return Total number of notifications in database
     */
    @Query("SELECT COUNT(*) FROM blocked_notifications")
    suspend fun getTotalCount(): Int

    /**
     * Mark a notification as read.
     * @param id The notification ID to mark
     */
    @Query("UPDATE blocked_notifications SET is_read = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    /**
     * Delete a specific notification.
     * @param id The notification ID to delete
     */
    @Query("DELETE FROM blocked_notifications WHERE id = :id")
    suspend fun delete(id: Long)

    /**
     * Delete notifications older than a timestamp.
     * Used for cleanup of old data.
     * @param olderThan Unix timestamp in milliseconds
     */
    @Query("DELETE FROM blocked_notifications WHERE timestamp < :olderThan")
    suspend fun deleteOlderThan(olderThan: Long)

    /**
     * Delete all notifications.
     * Used for clearing all notification history.
     */
    @Query("DELETE FROM blocked_notifications")
    suspend fun deleteAll()

    /**
     * Trim notifications to keep only the most recent ones.
     * Implements FIFO cleanup to maintain a maximum of keepCount notifications.
     * @param keepCount Maximum number of notifications to keep (default: 1000)
     */
    @Query("""
        DELETE FROM blocked_notifications
        WHERE id NOT IN (
            SELECT id FROM blocked_notifications
            ORDER BY timestamp DESC
            LIMIT :keepCount
        )
    """)
    suspend fun trimToLimit(keepCount: Int = 1000)

    // ========== Gamification Queries ==========

    /**
     * Get total count of blocked notifications as Flow (for reactive updates).
     * Used for achievement progress tracking.
     * @return Flow emitting the total count
     */
    @Query("SELECT COUNT(*) FROM blocked_notifications")
    fun getTotalCountFlow(): Flow<Int>

    /**
     * Get blocked notifications count for last N days.
     * @param since Timestamp in milliseconds
     * @return Number of notifications blocked since the timestamp
     */
    @Query("SELECT COUNT(*) FROM blocked_notifications WHERE timestamp >= :since")
    suspend fun getCountSince(since: Long): Int

    /**
     * Get top blocked apps with counts, ordered by count descending.
     * @param limit Maximum number of apps to return
     * @return List of apps with their blocked notification counts
     */
    @Query("""
        SELECT package_name, app_name, COUNT(*) as count
        FROM blocked_notifications
        GROUP BY package_name
        ORDER BY count DESC
        LIMIT :limit
    """)
    suspend fun getTopBlockedApps(limit: Int): List<AppNotificationCount>

    /**
     * Get top blocked apps since a timestamp.
     * @param since Timestamp in milliseconds
     * @param limit Maximum number of apps to return
     * @return List of apps with their blocked notification counts since the timestamp
     */
    @Query("""
        SELECT package_name, app_name, COUNT(*) as count
        FROM blocked_notifications
        WHERE timestamp >= :since
        GROUP BY package_name
        ORDER BY count DESC
        LIMIT :limit
    """)
    suspend fun getTopBlockedAppsSince(since: Long, limit: Int): List<AppNotificationCount>

    /**
     * Get daily blocked notification counts for the last N days.
     * Used for charts and statistics.
     * @param since Timestamp in milliseconds
     * @return List of daily counts with date strings
     */
    @Query("""
        SELECT DATE(timestamp/1000, 'unixepoch', 'localtime') as date_string,
               COUNT(*) as count
        FROM blocked_notifications
        WHERE timestamp >= :since
        GROUP BY date_string
        ORDER BY date_string ASC
    """)
    suspend fun getDailyBlockedCounts(since: Long): List<DailyNotificationCount>
}

/**
 * Data class for notification counts grouped by app.
 * Used in queries that aggregate notification counts.
 *
 * @property packageName The package name of the app
 * @property appName The human-readable app name
 * @property count Number of notifications from this app
 */
data class AppNotificationCount(
    @ColumnInfo(name = "package_name") val packageName: String,
    @ColumnInfo(name = "app_name") val appName: String,
    @ColumnInfo(name = "count") val count: Int
)

/**
 * Data class for daily notification counts.
 * Used in queries that aggregate counts by date for charts.
 *
 * @property dateString The date in YYYY-MM-DD format
 * @property count Number of notifications blocked on this date
 */
data class DailyNotificationCount(
    @ColumnInfo(name = "date_string") val dateString: String,
    @ColumnInfo(name = "count") val count: Int
)
