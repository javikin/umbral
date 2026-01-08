package com.umbral.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umbral.data.local.dao.BlockingProfileDao
import com.umbral.data.local.entity.BlockingProfileEntity
import com.umbral.data.local.preferences.UmbralPreferences
import com.umbral.domain.blocking.BlockingManager
import com.umbral.domain.stats.StatsRepository
import com.umbral.expedition.data.repository.ExpeditionRepository
import com.umbral.expedition.domain.mapper.ProgressMapper
import com.umbral.expedition.domain.model.ActiveCompanionInfo
import com.umbral.expedition.domain.model.CompanionType
import com.umbral.expedition.domain.model.ExpeditionHomeState
import com.umbral.expedition.domain.model.SessionReward
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val isBlockingEnabled: Boolean = false,
    val activeProfile: BlockingProfileEntity? = null,
    val currentStreak: Int = 0,
    val todayBlockedCount: Int = 0,
    val onboardingCompleted: Boolean = true,
    val weeklyStats: List<Float> = emptyList(),
    val hasStatsData: Boolean = false,
    val hasProfiles: Boolean = false,
    val expeditionState: ExpeditionHomeState = ExpeditionHomeState.Loading,
    val showRewardDialog: SessionReward? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val preferences: UmbralPreferences,
    private val profileDao: BlockingProfileDao,
    private val blockingManager: BlockingManager,
    private val statsRepository: StatsRepository,
    private val expeditionRepository: ExpeditionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadState()
        loadExpeditionState()
        observeRewardEvents()
    }

    private fun loadState() {
        viewModelScope.launch {
            combine(
                blockingManager.blockingState,
                preferences.currentStreak,
                preferences.onboardingCompleted,
                profileDao.getActiveProfile(),
                profileDao.getAllProfiles()
            ) { blockingState, streak, onboardingCompleted, activeProfile, allProfiles ->
                // Load weekly stats
                val weeklyStats = loadWeeklyStats()

                HomeUiState(
                    isLoading = false,
                    isBlockingEnabled = blockingState.isActive,
                    activeProfile = activeProfile,
                    currentStreak = streak,
                    onboardingCompleted = onboardingCompleted,
                    weeklyStats = weeklyStats,
                    hasStatsData = weeklyStats.isNotEmpty(),
                    hasProfiles = allProfiles.isNotEmpty()
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    private suspend fun loadWeeklyStats(): List<Float> {
        return try {
            val weeklyData = statsRepository.getWeeklyStats()

            // Convert daily stats to normalized float values (0.0 - 1.0)
            if (weeklyData.dailyStats.isEmpty()) {
                emptyList()
            } else {
                // Get max minutes for normalization
                val maxMinutes = weeklyData.dailyStats.maxOfOrNull { it.minutes } ?: 1

                // Convert to 7-day list with normalized values
                val statsMap = weeklyData.dailyStats.associate { it.day to it.minutes }

                // Generate last 7 days and map to normalized values
                (6 downTo 0).map { daysAgo ->
                    val day = java.time.LocalDate.now().minusDays(daysAgo.toLong()).toString()
                    val minutes = statsMap[day] ?: 0
                    if (maxMinutes > 0) minutes.toFloat() / maxMinutes.toFloat() else 0f
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to load weekly stats")
            emptyList()
        }
    }

    fun toggleBlocking() {
        viewModelScope.launch {
            val activeProfile = _uiState.value.activeProfile

            if (activeProfile != null) {
                // Use BlockingManager to toggle
                val result = blockingManager.toggleBlocking(activeProfile.id)
                result.onFailure { e ->
                    Timber.e(e, "Failed to toggle blocking")
                }
            } else {
                // No active profile, try to get the first available profile
                val profiles = profileDao.getAllProfilesSync()
                if (profiles.isNotEmpty()) {
                    val firstProfile = profiles.first()
                    val result = blockingManager.startBlocking(firstProfile.id)
                    result.onFailure { e ->
                        Timber.e(e, "Failed to start blocking")
                    }
                } else {
                    Timber.w("No profiles available to activate")
                }
            }
        }
    }

    fun selectProfile(profileId: String) {
        viewModelScope.launch {
            val result = blockingManager.startBlocking(profileId)
            result.onFailure { e ->
                Timber.e(e, "Failed to select profile")
            }
        }
    }

    /**
     * Load expedition progress state for display on HomeScreen
     */
    private fun loadExpeditionState() {
        viewModelScope.launch {
            try {
                combine(
                    expeditionRepository.getProgress(),
                    expeditionRepository.getActiveCompanion()
                ) { progress, activeCompanion ->
                    if (progress == null) {
                        ExpeditionHomeState.NotInitialized
                    } else {
                        val domainProgress = ProgressMapper.toDomain(progress)

                        // Map active companion to info
                        val companionInfo = activeCompanion?.let { companion ->
                            val type = CompanionType.fromId(companion.type)
                            if (type != null) {
                                ActiveCompanionInfo(
                                    id = companion.id,
                                    displayName = companion.name ?: type.displayName,
                                    type = type,
                                    evolutionState = companion.evolutionState,
                                    passiveBonusDescription = type.passiveBonus.getDescription()
                                )
                            } else null
                        }

                        ExpeditionHomeState(
                            isLoading = false,
                            isInitialized = true,
                            level = domainProgress.level,
                            currentXp = domainProgress.currentXp,
                            xpForNextLevel = domainProgress.xpForNextLevel,
                            levelProgress = domainProgress.levelProgress,
                            totalEnergy = domainProgress.totalEnergy,
                            currentStreak = domainProgress.currentStreak,
                            streakMultiplier = domainProgress.streakMultiplierText,
                            activeCompanion = companionInfo
                        )
                    }
                }.collect { expeditionState ->
                    _uiState.value = _uiState.value.copy(expeditionState = expeditionState)
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load expedition state")
                _uiState.value = _uiState.value.copy(
                    expeditionState = ExpeditionHomeState.NotInitialized
                )
            }
        }
    }

    /**
     * Observe reward events from BlockingManager
     */
    private fun observeRewardEvents() {
        viewModelScope.launch {
            try {
                blockingManager.rewardEvent.collect { reward ->
                    Timber.d("Received session reward: energy=${reward.energyResult.totalEnergy}")
                    _uiState.value = _uiState.value.copy(showRewardDialog = reward)
                }
            } catch (e: Exception) {
                Timber.e(e, "Error observing reward events")
            }
        }
    }

    /**
     * Dismiss the reward dialog
     */
    fun dismissRewardDialog() {
        _uiState.value = _uiState.value.copy(showRewardDialog = null)
    }
}
