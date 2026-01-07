package com.umbral.expedition.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a decoration purchased for the sanctuary.
 *
 * Decorations can be placed at custom positions (x, y) in the sanctuary view.
 * They are purchased with stars earned from achievements.
 */
@Entity(tableName = "sanctuary_decorations")
data class DecorationEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "position_x")
    val positionX: Float? = null,

    @ColumnInfo(name = "position_y")
    val positionY: Float? = null,

    @ColumnInfo(name = "purchased_at")
    val purchasedAt: Long
)
