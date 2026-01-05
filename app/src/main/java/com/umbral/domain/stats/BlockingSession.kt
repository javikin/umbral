package com.umbral.domain.stats

import java.time.LocalDateTime

/**
 * Domain model for a blocking session.
 */
data class BlockingSession(
    val id: Long = 0,
    val profileId: String,
    val startedAt: LocalDateTime,
    val endedAt: LocalDateTime? = null,
    val blockedAttempts: Int = 0,
    val unlockMethod: String? = null
) {
    val durationMinutes: Long?
        get() = endedAt?.let {
            java.time.Duration.between(startedAt, it).toMinutes()
        }

    val isActive: Boolean
        get() = endedAt == null
}
