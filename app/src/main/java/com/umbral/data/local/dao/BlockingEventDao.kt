package com.umbral.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.umbral.data.local.entity.BlockingEventEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for accessing blocking events for statistics and analytics.
 */
@Dao
interface BlockingEventDao {

    @Insert
    suspend fun insert(event: BlockingEventEntity)

    // === Estadísticas de tiempo bloqueado ===

    /**
     * Get total blocked minutes since a timestamp.
     * @param startTimestamp Unix timestamp in milliseconds
     * @return Total minutes blocked
     */
    @Query("""
        SELECT COALESCE(SUM(durationMinutes), 0)
        FROM blocking_events
        WHERE eventType = 'BLOCK_ENDED'
        AND timestamp >= :startTimestamp
    """)
    suspend fun getTotalBlockedMinutes(startTimestamp: Long): Int

    /**
     * Get blocked minutes between two timestamps.
     * @param startTimestamp Unix timestamp in milliseconds
     * @param endTimestamp Unix timestamp in milliseconds
     * @return Total minutes blocked in the range
     */
    @Query("""
        SELECT COALESCE(SUM(durationMinutes), 0)
        FROM blocking_events
        WHERE eventType = 'BLOCK_ENDED'
        AND timestamp >= :startTimestamp
        AND timestamp < :endTimestamp
    """)
    suspend fun getBlockedMinutesBetween(startTimestamp: Long, endTimestamp: Long): Int

    // === Estadísticas de intentos ===

    /**
     * Get count of app attempt events since a timestamp.
     * @param startTimestamp Unix timestamp in milliseconds
     * @return Number of attempts
     */
    @Query("""
        SELECT COUNT(*)
        FROM blocking_events
        WHERE eventType = 'APP_ATTEMPT'
        AND timestamp >= :startTimestamp
    """)
    suspend fun getAttemptCount(startTimestamp: Long): Int

    /**
     * Get most attempted apps since a timestamp.
     * @param startTimestamp Unix timestamp in milliseconds
     * @param limit Maximum number of apps to return
     * @return List of apps with attempt counts
     */
    @Query("""
        SELECT packageName, COUNT(*) as count
        FROM blocking_events
        WHERE eventType = 'APP_ATTEMPT'
        AND timestamp >= :startTimestamp
        AND packageName IS NOT NULL
        GROUP BY packageName
        ORDER BY count DESC
        LIMIT :limit
    """)
    suspend fun getTopAttemptedApps(startTimestamp: Long, limit: Int = 5): List<AppAttemptCount>

    // === Estadísticas por día (para gráficas) ===

    /**
     * Get daily blocked minutes for chart display.
     * @param startTimestamp Unix timestamp in milliseconds
     * @return List of daily statistics
     */
    @Query("""
        SELECT
            date(timestamp / 1000, 'unixepoch', 'localtime') as day,
            SUM(durationMinutes) as minutes
        FROM blocking_events
        WHERE eventType = 'BLOCK_ENDED'
        AND timestamp >= :startTimestamp
        GROUP BY day
        ORDER BY day
    """)
    suspend fun getDailyBlockedMinutes(startTimestamp: Long): List<DailyStats>

    // === Intentos por hora (para patrones) ===

    /**
     * Get hourly attempt counts to identify usage patterns.
     * @param startTimestamp Unix timestamp in milliseconds
     * @return List of hourly statistics
     */
    @Query("""
        SELECT
            strftime('%H', timestamp / 1000, 'unixepoch', 'localtime') as hour,
            COUNT(*) as count
        FROM blocking_events
        WHERE eventType = 'APP_ATTEMPT'
        AND timestamp >= :startTimestamp
        GROUP BY hour
        ORDER BY hour
    """)
    suspend fun getHourlyAttempts(startTimestamp: Long): List<HourlyStats>

    // === Flow para observar cambios ===

    /**
     * Observe event count changes for reactive UI.
     * @param startTimestamp Unix timestamp in milliseconds
     * @return Flow of event counts
     */
    @Query("SELECT COUNT(*) FROM blocking_events WHERE timestamp >= :startTimestamp")
    fun observeEventCount(startTimestamp: Long): Flow<Int>

    // === Cleanup ===

    /**
     * Delete old events to manage database size.
     * @param beforeTimestamp Unix timestamp in milliseconds
     */
    @Query("DELETE FROM blocking_events WHERE timestamp < :beforeTimestamp")
    suspend fun deleteOldEvents(beforeTimestamp: Long)
}

/**
 * Data class for app attempt counts.
 */
data class AppAttemptCount(
    val packageName: String,
    val count: Int
)

/**
 * Data class for daily statistics.
 */
data class DailyStats(
    val day: String,
    val minutes: Int
)

/**
 * Data class for hourly statistics.
 */
data class HourlyStats(
    val hour: String,
    val count: Int
)
