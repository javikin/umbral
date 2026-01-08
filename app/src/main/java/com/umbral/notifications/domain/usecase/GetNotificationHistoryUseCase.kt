package com.umbral.notifications.domain.usecase

import com.umbral.notifications.domain.model.BlockedNotification
import com.umbral.notifications.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * UseCase to get notification history.
 *
 * Provides different views of notification history:
 * - By session: All notifications from a specific blocking session
 * - Recent: Most recent notifications across all sessions
 */
class GetNotificationHistoryUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    /**
     * Get notifications for a specific session.
     * @param sessionId The session ID to filter by
     * @return Flow of notifications ordered by timestamp (newest first)
     */
    fun getBySession(sessionId: String): Flow<List<BlockedNotification>> {
        return repository.getBySession(sessionId)
    }

    /**
     * Get recent notifications with a limit.
     * @param limit Maximum number of notifications to return
     * @return Flow of recent notifications across all sessions
     */
    fun getRecent(limit: Int = 100): Flow<List<BlockedNotification>> {
        return repository.getRecent(limit)
    }

    /**
     * Get total count of all blocked notifications.
     * @return Total number of notifications ever blocked
     */
    suspend fun getTotalCount(): Int {
        return repository.getTotalBlockedCount()
    }
}
