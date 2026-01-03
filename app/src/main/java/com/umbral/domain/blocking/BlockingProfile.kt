package com.umbral.domain.blocking

import java.time.LocalDateTime

/**
 * Domain model for a blocking profile.
 */
data class BlockingProfile(
    val id: String,
    val name: String,
    val iconName: String = "shield",
    val colorHex: String = "#6650A4",
    val isActive: Boolean = false,
    val isStrictMode: Boolean = false,
    val blockedApps: List<String> = emptyList(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
