package com.umbral.domain.stats

import java.time.LocalDateTime

/**
 * Domain model for a blocked app attempt.
 */
data class BlockedAttempt(
    val id: Long = 0,
    val packageName: String,
    val appName: String,
    val profileId: String,
    val timestamp: LocalDateTime,
    val wasUnlocked: Boolean = false,
    val unlockMethod: String? = null
)
