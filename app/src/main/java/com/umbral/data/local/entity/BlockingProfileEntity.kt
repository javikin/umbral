package com.umbral.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "blocking_profiles")
data class BlockingProfileEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val iconName: String = "shield",
    val colorHex: String = "#6650A4",
    val isActive: Boolean = false,
    val isStrictMode: Boolean = false,
    val blockNotifications: Boolean = true,
    val blockedApps: List<String> = emptyList(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
