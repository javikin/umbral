package com.umbral.expedition.presentation.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umbral.expedition.data.entity.AchievementEntity
import com.umbral.expedition.data.repository.ExpeditionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Achievements screen
 */
@HiltViewModel
class AchievementsViewModel @Inject constructor(
    private val repository: ExpeditionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AchievementsUiState())
    val uiState: StateFlow<AchievementsUiState> = _uiState.asStateFlow()

    init {
        loadAchievements()
    }

    private fun loadAchievements() {
        viewModelScope.launch {
            combine(
                repository.getAchievements(),
                repository.getUnlockedAchievements()
            ) { allAchievements, unlockedAchievements ->
                AchievementsUiState(
                    achievements = allAchievements,
                    unlockedCount = unlockedAchievements.size,
                    totalCount = allAchievements.size,
                    totalStars = unlockedAchievements.sumOf { it.starsReward },
                    isLoading = false
                )
            }.collectLatest { state ->
                _uiState.value = state
            }
        }
    }
}

/**
 * UI state for Achievements screen
 */
data class AchievementsUiState(
    val achievements: List<AchievementEntity> = emptyList(),
    val unlockedCount: Int = 0,
    val totalCount: Int = 0,
    val totalStars: Int = 0,
    val isLoading: Boolean = true
)
