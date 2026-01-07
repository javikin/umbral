package com.umbral.expedition.domain.mapper

import com.umbral.expedition.data.entity.CompanionEntity
import com.umbral.expedition.domain.model.Companion
import com.umbral.expedition.domain.model.CompanionType

/**
 * Mapper for converting between CompanionEntity (Room) and Companion (Domain).
 */
object CompanionMapper {

    /**
     * Convert Room entity to domain model
     */
    fun toDomain(entity: CompanionEntity): Companion {
        val companionType = CompanionType.fromId(entity.type)
            ?: throw IllegalArgumentException("Unknown companion type: ${entity.type}")

        return Companion(
            id = entity.id,
            type = companionType,
            name = entity.name,
            evolutionState = entity.evolutionState,
            energyInvested = entity.energyInvested,
            capturedAt = entity.capturedAt,
            isActive = entity.isActive
        )
    }

    /**
     * Convert domain model to Room entity
     */
    fun toEntity(companion: Companion): CompanionEntity {
        return CompanionEntity(
            id = companion.id,
            type = companion.type.id,
            name = companion.name,
            evolutionState = companion.evolutionState,
            energyInvested = companion.energyInvested,
            capturedAt = companion.capturedAt,
            isActive = companion.isActive
        )
    }

    /**
     * Convert list of entities to domain models
     */
    fun toDomainList(entities: List<CompanionEntity>): List<Companion> {
        return entities.map { toDomain(it) }
    }

    /**
     * Convert list of domain models to entities
     */
    fun toEntityList(companions: List<Companion>): List<CompanionEntity> {
        return companions.map { toEntity(it) }
    }
}
