package com.umbral.expedition.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing the player's overall progress in the expedition system.
 *
 * This is a singleton entity (id=1) that tracks:
 * - Level and XP progression
 * - Total energy earned
 * - Stars currency for purchasing decorations
 * - Daily streak tracking
 * - Total blocking time contribution
 */
@Entity(tableName = "player_progress")
data class ProgressEntity(
    @PrimaryKey
    val id: Int = 1,

    @ColumnInfo(name = "level")
    val level: Int = 1,

    @ColumnInfo(name = "current_xp")
    val currentXp: Int = 0,

    @ColumnInfo(name = "total_energy")
    val totalEnergy: Int = 0,

    @ColumnInfo(name = "stars")
    val stars: Int = 0,

    @ColumnInfo(name = "current_streak")
    val currentStreak: Int = 0,

    @ColumnInfo(name = "longest_streak")
    val longestStreak: Int = 0,

    @ColumnInfo(name = "total_blocking_minutes")
    val totalBlockingMinutes: Int = 0
)
