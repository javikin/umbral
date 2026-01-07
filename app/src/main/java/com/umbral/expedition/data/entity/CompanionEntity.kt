package com.umbral.expedition.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a companion creature captured during expeditions.
 *
 * Companions can evolve through 3 states by investing energy:
 * - State 1: Basic form (0-499 energy)
 * - State 2: Intermediate (500-1499 energy)
 * - State 3: Final form (1500+ energy)
 */
@Entity(tableName = "companions")
data class CompanionEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "name")
    val name: String? = null,

    @ColumnInfo(name = "evolution_state")
    val evolutionState: Int = 1,

    @ColumnInfo(name = "energy_invested")
    val energyInvested: Int = 0,

    @ColumnInfo(name = "captured_at")
    val capturedAt: Long,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = false
)
