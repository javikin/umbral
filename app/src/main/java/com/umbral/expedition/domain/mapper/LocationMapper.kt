package com.umbral.expedition.domain.mapper

import com.umbral.expedition.data.entity.LocationEntity
import com.umbral.expedition.domain.model.Biome
import com.umbral.expedition.domain.model.Location

/**
 * Mapper for converting between LocationEntity (Room) and Location (Domain).
 */
object LocationMapper {

    /**
     * Convert Room entity to domain model
     */
    fun toDomain(entity: LocationEntity): Location {
        val biome = Biome.fromId(entity.biomeId)
            ?: throw IllegalArgumentException("Unknown biome: ${entity.biomeId}")

        return Location(
            id = entity.id,
            biome = biome,
            discoveredAt = entity.discoveredAt,
            energySpent = entity.energySpent,
            loreRead = entity.loreRead
        )
    }

    /**
     * Convert domain model to Room entity
     */
    fun toEntity(location: Location): LocationEntity {
        return LocationEntity(
            id = location.id,
            biomeId = location.biome.id,
            discoveredAt = location.discoveredAt,
            energySpent = location.energySpent,
            loreRead = location.loreRead
        )
    }

    /**
     * Convert list of entities to domain models
     */
    fun toDomainList(entities: List<LocationEntity>): List<Location> {
        return entities.map { toDomain(it) }
    }

    /**
     * Convert list of domain models to entities
     */
    fun toEntityList(locations: List<Location>): List<LocationEntity> {
        return locations.map { toEntity(it) }
    }
}
