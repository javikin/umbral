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
}
