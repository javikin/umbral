package com.umbral.notifications.domain.usecase

import com.umbral.notifications.domain.repository.NotificationRepository
import javax.inject.Inject

/**
 * UseCase to clear old notifications.
 *
 * Performs automatic cleanup of notification history to:
 * - Save storage space
 * - Comply with privacy best practices (don't keep data indefinitely)
 * - Maintain app performance
 *
 * Default retention is 7 days, but can be configured.
 */
class ClearOldNotificationsUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    /**
     * Clear notifications older than the specified retention period.
     * @param retentionDays Number of days to keep (default: 7 days)
     */
    suspend operator fun invoke(retentionDays: Int = 7) {
        repository.clearOlderThan(retentionDays)
    }
}
