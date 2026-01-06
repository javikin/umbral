package com.umbral.domain.stats

import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Repository for statistics and session tracking.
 */
interface StatsRepository {

    // Blocked Attempts
    suspend fun recordBlockedAttempt(attempt: BlockedAttempt): Result<Unit>

    fun getRecentAttempts(limit: Int = 50): Flow<List<BlockedAttempt>>

    suspend fun getAttemptCountSince(since: LocalDateTime): Int

    suspend fun getTopBlockedApps(since: LocalDateTime, limit: Int = 5): Map<String, Int>

    // Blocking Sessions
    suspend fun startSession(profileId: String): Result<Long>

    suspend fun endSession(
        sessionId: Long,
        endTime: LocalDateTime,
        attempts: Int,
        unlockMethod: String?
    ): Result<Unit>

    suspend fun getActiveSession(): BlockingSession?

    suspend fun getTotalBlockedSince(since: LocalDateTime): Int

    // Cleanup
    suspend fun deleteOldAttempts(before: LocalDateTime): Result<Unit>

    // === New Unified Event Tracking ===

    /**
     * Record that a blocking session started.
     * @param profileId The profile that started blocking
     */
    suspend fun recordBlockStarted(profileId: String): Result<Unit>

    /**
     * Record that a blocking session ended.
     * @param profileId The profile that was blocking
     * @param durationMinutes How long the session lasted
     */
    suspend fun recordBlockEnded(profileId: String, durationMinutes: Int): Result<Unit>

    /**
     * Record that a user attempted to open a blocked app.
     * @param packageName The package name of the blocked app
     */
    suspend fun recordAppAttempt(packageName: String): Result<Unit>

    /**
     * Get today's blocking statistics.
     * @return Statistics for today
     */
    suspend fun getTodayStats(): TodayStats

    /**
     * Get weekly blocking statistics.
     * @return Statistics for the past 7 days
     */
    suspend fun getWeeklyStats(): WeeklyStats
}
