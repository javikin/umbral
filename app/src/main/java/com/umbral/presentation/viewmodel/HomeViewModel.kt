package com.umbral.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umbral.data.local.dao.BlockingProfileDao
import com.umbral.data.local.entity.BlockingProfileEntity
import com.umbral.data.local.preferences.UmbralPreferences
import com.umbral.domain.blocking.BlockingManager
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
    val onboardingCompleted: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val preferences: UmbralPreferences,
    private val profileDao: BlockingProfileDao,
    private val blockingManager: BlockingManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadState()
    }

    private fun loadState() {
        viewModelScope.launch {
            combine(
                blockingManager.blockingState,
                preferences.currentStreak,
                preferences.onboardingCompleted,
                profileDao.getActiveProfile()
            ) { blockingState, streak, onboardingCompleted, activeProfile ->
                HomeUiState(
                    isLoading = false,
                    isBlockingEnabled = blockingState.isActive,
                    activeProfile = activeProfile,
                    currentStreak = streak,
                    onboardingCompleted = onboardingCompleted
                )
            }.collect { state ->
                _uiState.value = state
            }
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
}
