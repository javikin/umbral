package com.umbral.notifications.presentation.summary

import com.umbral.notifications.domain.model.NotificationSummary

/**
 * UI state for Session Summary dialog.
 *
 * @property summary Notification summary data from the session
 * @property bonusEnergy Bonus energy earned (+1 per 5 notifications)
 * @property isLoading Whether the summary is still loading
 */
data class SessionSummaryState(
    val summary: NotificationSummary? = null,
    val bonusEnergy: Int = 0,
    val isLoading: Boolean = true
)
