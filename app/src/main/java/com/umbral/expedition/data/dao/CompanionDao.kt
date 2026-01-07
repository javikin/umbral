package com.umbral.expedition.data.dao

import androidx.room.*
import com.umbral.expedition.data.entity.CompanionEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for companion persistence operations.
 *
 * Provides queries for CRUD operations, companion management,
 * and evolution tracking.
 */
@Dao
interface CompanionDao {
    @Query("SELECT * FROM companions ORDER BY captured_at DESC")
    fun getAllCompanions(): Flow<List<CompanionEntity>>

    @Query("SELECT * FROM companions WHERE is_active = 1 LIMIT 1")
    fun getActiveCompanion(): Flow<CompanionEntity?>

    @Query("SELECT * FROM companions WHERE type = :type")
    suspend fun getByType(type: String): CompanionEntity?

    @Query("SELECT COUNT(*) FROM companions")
    suspend fun getCompanionCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(companion: CompanionEntity)

    @Update
    suspend fun update(companion: CompanionEntity)

    @Query("UPDATE companions SET is_active = 0")
    suspend fun deactivateAll()

    @Query("UPDATE companions SET is_active = 1 WHERE id = :id")
    suspend fun setActive(id: String)

    @Query("UPDATE companions SET evolution_state = :state, energy_invested = :energy WHERE id = :id")
    suspend fun evolve(id: String, state: Int, energy: Int)
}
