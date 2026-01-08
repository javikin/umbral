package com.umbral.expedition.data.dao

import androidx.room.*
import com.umbral.expedition.data.entity.DecorationEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for sanctuary decoration operations.
 *
 * Provides queries for managing purchased decorations
 * and their positions in the sanctuary view.
 */
@Dao
interface DecorationDao {
    @Query("SELECT * FROM sanctuary_decorations ORDER BY purchased_at DESC")
    fun getAllDecorations(): Flow<List<DecorationEntity>>

    @Query("SELECT * FROM sanctuary_decorations WHERE type = :type")
    fun getByType(type: String): Flow<List<DecorationEntity>>

    @Query("SELECT * FROM sanctuary_decorations WHERE id = :id")
    suspend fun getById(id: String): DecorationEntity?

    @Query("SELECT COUNT(*) FROM sanctuary_decorations")
    suspend fun getDecorationCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(decoration: DecorationEntity)

    @Update
    suspend fun update(decoration: DecorationEntity)

    @Delete
    suspend fun delete(decoration: DecorationEntity)
}
