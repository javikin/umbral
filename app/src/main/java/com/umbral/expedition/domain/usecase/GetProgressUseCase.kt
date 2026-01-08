package com.umbral.expedition.domain.usecase

import com.umbral.expedition.data.repository.ExpeditionRepository
import com.umbral.expedition.domain.mapper.ProgressMapper
import com.umbral.expedition.domain.model.PlayerProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case for getting player progress with all calculated fields.
 *
 * Returns Flow of PlayerProgress domain model with:
 * - XP needed for next level
 * - Level progress percentage
 * - Streak multiplier
 * - All other computed properties
 */
class GetProgressUseCase @Inject constructor(
    private val repository: ExpeditionRepository
) {

    /**
     * Get player progress as Flow
     */
    operator fun invoke(): Flow<PlayerProgress?> {
        return repository.getProgress().map { entity ->
            entity?.let { ProgressMapper.toDomain(it) }
        }
    }

    /**
     * Get player progress once (suspend)
     */
    suspend fun getOnce(): PlayerProgress? {
        return repository.getProgressOnce()?.let { ProgressMapper.toDomain(it) }
    }
}
