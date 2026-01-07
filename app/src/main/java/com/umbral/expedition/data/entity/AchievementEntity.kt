package com.umbral.expedition.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for storing achievement progress and unlock state.
 * Linked to definitions in AchievementDefinitions.kt
 */
@Entity(
    tableName = "achievements",
    indices = [Index(value = ["category"])]
)
data class AchievementEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "progress")
    val progress: Int = 0,

    @ColumnInfo(name = "target")
    val target: Int,

    @ColumnInfo(name = "unlocked_at")
    val unlockedAt: Long? = null,

    @ColumnInfo(name = "stars_reward")
    val starsReward: Int
)
