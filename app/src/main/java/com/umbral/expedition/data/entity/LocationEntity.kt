package com.umbral.expedition.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a discovered location in the expedition map.
 *
 * Locations belong to different biomes and cost energy to discover.
 * Players can read lore text to learn about each location.
 */
@Entity(
    tableName = "discovered_locations",
    indices = [Index(value = ["biome_id"])]
)
data class LocationEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "biome_id")
    val biomeId: String,

    @ColumnInfo(name = "discovered_at")
    val discoveredAt: Long,

    @ColumnInfo(name = "energy_spent")
    val energySpent: Int,

    @ColumnInfo(name = "lore_read")
    val loreRead: Boolean = false
)
