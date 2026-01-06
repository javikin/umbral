package com.umbral.data.stats

import com.umbral.data.local.dao.BlockingEventDao
import com.umbral.data.local.dao.StatsDao
import com.umbral.data.local.entity.BlockedAttemptEntity
import com.umbral.data.local.entity.BlockingEventEntity
import com.umbral.data.local.entity.BlockingSessionEntity
import com.umbral.data.local.entity.EventType
import com.umbral.domain.stats.BlockedAttempt
import com.umbral.domain.stats.BlockingSession
import com.umbral.domain.stats.StatsRepository
import com.umbral.domain.stats.TodayStats
import com.umbral.domain.stats.WeeklyStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatsRepositoryImpl @Inject constructor(
    private val statsDao: StatsDao,
    private val blockingEventDao: BlockingEventDao
) : StatsRepository {

    override suspend fun recordBlockedAttempt(attempt: BlockedAttempt): Result<Unit> {
        return try {
            val entity = attempt.toEntity()
            statsDao.insertBlockedAttempt(entity)
            Timber.d("Blocked attempt recorded: ${attempt.packageName}")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error recording blocked attempt")
            Result.failure(e)
        }
    }

    override fun getRecentAttempts(limit: Int): Flow<List<BlockedAttempt>> {
        return statsDao.getRecentAttempts(limit).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getAttemptCountSince(since: LocalDateTime): Int {
        return try {
            val sinceTimestamp = since.toEpochSecond(ZoneOffset.UTC)
            statsDao.getAttemptCountSince(sinceTimestamp)
        } catch (e: Exception) {
            Timber.e(e, "Error getting attempt count")
            0
        }
    }

    override suspend fun getTopBlockedApps(since: LocalDateTime, limit: Int): Map<String, Int> {
        return try {
            val sinceTimestamp = since.toEpochSecond(ZoneOffset.UTC)
            val results = statsDao.getTopBlockedApps(sinceTimestamp, limit)
            results.associate { it.packageName to it.count }
        } catch (e: Exception) {
            Timber.e(e, "Error getting top blocked apps")
            emptyMap()
        }
    }

    override suspend fun startSession(profileId: String): Result<Long> {
        return try {
            val session = BlockingSessionEntity(
                profileId = profileId,
                startedAt = LocalDateTime.now()
            )
            val sessionId = statsDao.insertSession(session)
            Timber.d("Session started: $sessionId for profile: $profileId")
            Result.success(sessionId)
        } catch (e: Exception) {
            Timber.e(e, "Error starting session")
            Result.failure(e)
        }
    }

    override suspend fun endSession(
        sessionId: Long,
        endTime: LocalDateTime,
        attempts: Int,
        unlockMethod: String?
    ): Result<Unit> {
        return try {
            val endTimestamp = endTime.toEpochSecond(ZoneOffset.UTC)
            statsDao.endSession(sessionId, endTimestamp, attempts, unlockMethod)
            Timber.d("Session ended: $sessionId with $attempts attempts")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error ending session")
            Result.failure(e)
        }
    }

    override suspend fun getActiveSession(): BlockingSession? {
        return try {
            statsDao.getActiveSession()?.toDomain()
        } catch (e: Exception) {
            Timber.e(e, "Error getting active session")
            null
        }
    }

    override suspend fun getTotalBlockedSince(since: LocalDateTime): Int {
        return try {
            val sinceTimestamp = since.toEpochSecond(ZoneOffset.UTC)
            statsDao.getTotalBlockedSince(sinceTimestamp) ?: 0
        } catch (e: Exception) {
            Timber.e(e, "Error getting total blocked")
            0
        }
    }

    override suspend fun deleteOldAttempts(before: LocalDateTime): Result<Unit> {
        return try {
            val beforeTimestamp = before.toEpochSecond(ZoneOffset.UTC)
            statsDao.deleteOldAttempts(beforeTimestamp)
            Timber.d("Old attempts deleted before: $before")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error deleting old attempts")
            Result.failure(e)
        }
    }

    // === New Unified Event Tracking Implementation ===

    override suspend fun recordBlockStarted(profileId: String): Result<Unit> {
        return try {
            blockingEventDao.insert(
                BlockingEventEntity(
                    eventType = EventType.BLOCK_STARTED,
                    profileId = profileId
                )
            )
            Timber.d("Block started event recorded for profile: $profileId")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error recording block started event")
            Result.failure(e)
        }
    }

    override suspend fun recordBlockEnded(profileId: String, durationMinutes: Int): Result<Unit> {
        return try {
            blockingEventDao.insert(
                BlockingEventEntity(
                    eventType = EventType.BLOCK_ENDED,
                    profileId = profileId,
                    durationMinutes = durationMinutes
                )
            )
            Timber.d("Block ended event recorded: $durationMinutes minutes for profile: $profileId")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error recording block ended event")
            Result.failure(e)
        }
    }

    override suspend fun recordAppAttempt(packageName: String): Result<Unit> {
        return try {
            blockingEventDao.insert(
                BlockingEventEntity(
                    eventType = EventType.APP_ATTEMPT,
                    packageName = packageName
                )
            )
            Timber.d("App attempt event recorded: $packageName")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error recording app attempt event")
            Result.failure(e)
        }
    }

    override suspend fun getTodayStats(): TodayStats {
        return try {
            val startOfDay = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            TodayStats(
                blockedMinutes = blockingEventDao.getTotalBlockedMinutes(startOfDay),
                attemptCount = blockingEventDao.getAttemptCount(startOfDay)
            )
        } catch (e: Exception) {
            Timber.e(e, "Error getting today's stats")
            TodayStats(blockedMinutes = 0, attemptCount = 0)
        }
    }

    override suspend fun getWeeklyStats(): WeeklyStats {
        return try {
            val startOfWeek = LocalDate.now()
                .minusDays(6)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            val startOfPreviousWeek = LocalDate.now()
                .minusDays(13)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            val thisWeekMinutes = blockingEventDao.getTotalBlockedMinutes(startOfWeek)
            val previousWeekMinutes = blockingEventDao.getBlockedMinutesBetween(
                startOfPreviousWeek,
                startOfWeek
            )

            WeeklyStats(
                totalMinutes = thisWeekMinutes,
                previousWeekMinutes = previousWeekMinutes,
                dailyStats = blockingEventDao.getDailyBlockedMinutes(startOfWeek),
                topApps = blockingEventDao.getTopAttemptedApps(startOfWeek)
            )
        } catch (e: Exception) {
            Timber.e(e, "Error getting weekly stats")
            WeeklyStats(
                totalMinutes = 0,
                previousWeekMinutes = 0,
                dailyStats = emptyList(),
                topApps = emptyList()
            )
        }
    }
}

// Extension functions for mapping
private fun BlockedAttemptEntity.toDomain(): BlockedAttempt {
    return BlockedAttempt(
        id = id,
        packageName = packageName,
        appName = appName,
        profileId = profileId,
        timestamp = timestamp,
        wasUnlocked = wasUnlocked,
        unlockMethod = unlockMethod
    )
}

private fun BlockedAttempt.toEntity(): BlockedAttemptEntity {
    return BlockedAttemptEntity(
        id = if (id == 0L) 0 else id,
        packageName = packageName,
        appName = appName,
        profileId = profileId,
        timestamp = timestamp,
        wasUnlocked = wasUnlocked,
        unlockMethod = unlockMethod
    )
}

private fun BlockingSessionEntity.toDomain(): BlockingSession {
    return BlockingSession(
        id = id,
        profileId = profileId,
        startedAt = startedAt,
        endedAt = endedAt,
        blockedAttempts = blockedAttempts,
        unlockMethod = unlockMethod
    )
}
