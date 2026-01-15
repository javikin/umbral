package com.umbral.notifications.domain.usecase

import com.umbral.notifications.domain.model.NotificationSummary
import com.umbral.notifications.domain.repository.NotificationRepository
import javax.inject.Inject

/**
 * UseCase to get notification summary for a blocking session.
 *
 * Used to generate post-session reports showing how many notifications
 * were blocked and which apps were most active.
 */
class GetNotificationSummaryUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    /**
     * Get notification summary for a session.
     * @param sessionId The session ID to get summary for
     * @return Summary with total count and breakdown by app
     */
    suspend operator fun invoke(sessionId: String): NotificationSummary {
        return repository.getSummaryForSession(sessionId)
    }
}
