package com.umbral.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.umbral.data.local.entity.BlockedAttemptEntity
import com.umbral.data.local.entity.BlockingSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StatsDao {

    // Blocked Attempts
    @Insert
    suspend fun insertBlockedAttempt(attempt: BlockedAttemptEntity)

    @Query("SELECT * FROM blocked_attempts ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentAttempts(limit: Int = 50): Flow<List<BlockedAttemptEntity>>

    @Query("SELECT COUNT(*) FROM blocked_attempts WHERE timestamp >= :since")
    suspend fun getAttemptCountSince(since: Long): Int

    @Query("""
        SELECT packageName, COUNT(*) as count
        FROM blocked_attempts
        WHERE timestamp >= :since
        GROUP BY packageName
        ORDER BY count DESC
        LIMIT :limit
    """)
    suspend fun getTopBlockedApps(since: Long, limit: Int = 5): List<AppBlockCount>

    // Blocking Sessions
    @Insert
    suspend fun insertSession(session: BlockingSessionEntity): Long

    @Query("UPDATE blocking_sessions SET endedAt = :endTime, blockedAttempts = :attempts, unlockMethod = :method WHERE id = :sessionId")
    suspend fun endSession(sessionId: Long, endTime: Long, attempts: Int, method: String?)

    @Query("SELECT * FROM blocking_sessions WHERE endedAt IS NULL LIMIT 1")
    suspend fun getActiveSession(): BlockingSessionEntity?

    @Query("SELECT SUM(blockedAttempts) FROM blocking_sessions WHERE startedAt >= :since")
    suspend fun getTotalBlockedSince(since: Long): Int?

    // Cleanup
    @Query("DELETE FROM blocked_attempts WHERE timestamp < :before")
    suspend fun deleteOldAttempts(before: Long)
}

data class AppBlockCount(
    val packageName: String,
    val count: Int
)
