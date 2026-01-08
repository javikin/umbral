package com.umbral.notifications.data.repository

import com.umbral.notifications.data.local.BlockedNotificationDao
import com.umbral.notifications.data.mapper.toDomain
import com.umbral.notifications.data.mapper.toEntity
import com.umbral.notifications.domain.model.AppNotificationStats
import com.umbral.notifications.domain.model.BlockedNotification
import com.umbral.notifications.domain.model.NotificationStats
import com.umbral.notifications.domain.model.NotificationSummary
import com.umbral.notifications.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration

/**
 * Implementation of NotificationRepository.
 *
 * Manages data access for blocked notifications through the DAO layer.
 * Converts between database entities and domain models.
 */
@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val dao: BlockedNotificationDao
) : NotificationRepository {

    override suspend fun save(notification: BlockedNotification): Long {
        return dao.insert(notification.toEntity())
    }

    override fun getBySession(sessionId: String): Flow<List<BlockedNotification>> {
        return dao.getBySession(sessionId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getRecent(limit: Int): Flow<List<BlockedNotification>> {
        return dao.getRecent(limit).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getSummaryForSession(sessionId: String): NotificationSummary {
        val totalCount = dao.getCountForSession(sessionId)
        val byApp = dao.getCountByAppForSession(sessionId)

        return NotificationSummary(
            sessionId = sessionId,
            totalCount = totalCount,
            byApp = byApp.map { appCount ->
                NotificationSummary.AppCount(
                    packageName = appCount.packageName,
                    appName = appCount.appName,
                    count = appCount.count
                )
            },
            sessionDuration = Duration.ZERO // Will be calculated with session data in future
        )
    }

    override suspend fun getTotalBlockedCount(): Int {
        return dao.getTotalCount()
    }

    override suspend fun markAsRead(id: Long) {
        dao.markAsRead(id)
    }

    override suspend fun delete(id: Long) {
        dao.delete(id)
    }

    override suspend fun clearOlderThan(days: Int) {
        val cutoffMillis = System.currentTimeMillis() - (days * 24 * 60 * 60 * 1000L)
        dao.deleteOlderThan(cutoffMillis)
    }

    override suspend fun trimToLimit(maxCount: Int) {
        dao.trimToLimit(maxCount)
    }

    // ========== Gamification Methods ==========

    override fun getTotalBlockedCountFlow(): Flow<Int> {
        return dao.getTotalCountFlow()
    }

    override suspend fun getCountForSession(sessionId: String): Int {
        return dao.getCountForSession(sessionId)
    }

    override suspend fun getTopBlockedApps(limit: Int): List<AppNotificationStats> {
        return dao.getTopBlockedApps(limit).map { appCount ->
            AppNotificationStats(
                packageName = appCount.packageName,
                appName = appCount.appName,
                count = appCount.count
            )
        }
    }

    override suspend fun getNotificationStats(): NotificationStats {
        val totalBlocked = dao.getTotalCount()
        val last7DaysCutoff = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
        val last7Days = dao.getCountSince(last7DaysCutoff)
        val topApps = dao.getTopBlockedApps(5).map { appCount ->
            AppNotificationStats(
                packageName = appCount.packageName,
                appName = appCount.appName,
                count = appCount.count
            )
        }

        return NotificationStats(
            totalBlocked = totalBlocked,
            last7Days = last7Days,
            topApps = topApps
        )
    }

    override suspend fun getCountForLastDays(days: Int): Int {
        val cutoffMillis = System.currentTimeMillis() - (days * 24 * 60 * 60 * 1000L)
        return dao.getCountSince(cutoffMillis)
    }
}
