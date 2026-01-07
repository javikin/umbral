package com.umbral.expedition.domain.usecase

import com.umbral.expedition.data.repository.ExpeditionRepository
import com.umbral.expedition.domain.mapper.LocationMapper
import com.umbral.expedition.domain.mapper.ProgressMapper
import com.umbral.expedition.domain.model.Biome
import com.umbral.expedition.domain.model.DiscoveryResult
import com.umbral.expedition.domain.model.ExpeditionFormulas
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case for discovering a new location on the expedition map.
 *
 * This use case:
 * 1. Checks if player has enough energy
 * 2. Checks if location was already discovered
 * 3. Spends energy
 * 4. Records discovery
 * 5. Returns result
 */
class DiscoverLocationUseCase @Inject constructor(
    private val repository: ExpeditionRepository
) {

    /**
     * Attempt to discover a location
     *
     * @param locationId ID of location to discover (e.g., "forest_01")
     * @param biome Biome that location belongs to
     * @return DiscoveryResult with success or failure details
     */
    suspend operator fun invoke(locationId: String, biome: Biome = Biome.FOREST): DiscoveryResult {
        // Get current progress
        val progressEntity = repository.getProgress().first()
            ?: throw IllegalStateException("Progress not initialized")

        val progress = ProgressMapper.toDomain(progressEntity)

        // Check if already discovered
        val discoveredLocations = repository.getDiscoveredLocations().first()
        if (discoveredLocations.any { it.id == locationId }) {
            return DiscoveryResult.AlreadyDiscovered(locationId)
        }

        // Calculate energy cost (based on discovery index)
        val discoveryCount = discoveredLocations.size
        val energyCost = ExpeditionFormulas.locationRevealCost(discoveryCount)

        // Check if player has enough energy
        if (progress.totalEnergy < energyCost) {
            return DiscoveryResult.InsufficientEnergy(
                required = energyCost,
                available = progress.totalEnergy,
                shortage = energyCost - progress.totalEnergy
            )
        }

        // Spend energy
        repository.spendEnergy(energyCost)

        // Discover location
        repository.discoverLocation(locationId, biome.id, energyCost)

        // Get the newly discovered location
        val locationEntity = repository.getLocationById(locationId)
            ?: throw IllegalStateException("Location not found after discovery")

        val location = LocationMapper.toDomain(locationEntity)

        return DiscoveryResult.Success(
            location = location,
            energyRemaining = progress.totalEnergy - energyCost
        )
    }
}
