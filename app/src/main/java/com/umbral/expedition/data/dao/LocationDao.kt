package com.umbral.expedition.data.dao

import androidx.room.*
import com.umbral.expedition.data.entity.LocationEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for location discovery operations.
 *
 * Provides queries for tracking discovered locations,
 * filtering by biome, and managing lore read status.
 */
@Dao
interface LocationDao {
    @Query("SELECT * FROM discovered_locations ORDER BY discovered_at DESC")
    fun getDiscoveredLocations(): Flow<List<LocationEntity>>

    @Query("SELECT * FROM discovered_locations WHERE biome_id = :biomeId")
    fun getByBiome(biomeId: String): Flow<List<LocationEntity>>

    @Query("SELECT * FROM discovered_locations WHERE id = :id")
    suspend fun getById(id: String): LocationEntity?

    @Query("SELECT COUNT(*) FROM discovered_locations")
    suspend fun getDiscoveryCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: LocationEntity)

    @Update
    suspend fun update(location: LocationEntity)

    @Delete
    suspend fun delete(location: LocationEntity)
}
