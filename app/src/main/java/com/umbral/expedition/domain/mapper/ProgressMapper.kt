package com.umbral.expedition.domain.mapper

import com.umbral.expedition.data.entity.ProgressEntity
import com.umbral.expedition.domain.model.PlayerProgress

/**
 * Mapper for converting between ProgressEntity (Room) and PlayerProgress (Domain).
 */
object ProgressMapper {

    /**
     * Convert Room entity to domain model
     */
    fun toDomain(entity: ProgressEntity): PlayerProgress {
        return PlayerProgress(
            id = entity.id,
            level = entity.level,
            currentXp = entity.currentXp,
            totalEnergy = entity.totalEnergy,
            stars = entity.stars,
            currentStreak = entity.currentStreak,
            longestStreak = entity.longestStreak,
            totalBlockingMinutes = entity.totalBlockingMinutes
        )
    }

    /**
     * Convert domain model to Room entity
     */
    fun toEntity(progress: PlayerProgress): ProgressEntity {
        return ProgressEntity(
            id = progress.id,
            level = progress.level,
            currentXp = progress.currentXp,
            totalEnergy = progress.totalEnergy,
            stars = progress.stars,
            currentStreak = progress.currentStreak,
            longestStreak = progress.longestStreak,
            totalBlockingMinutes = progress.totalBlockingMinutes
        )
    }
}
