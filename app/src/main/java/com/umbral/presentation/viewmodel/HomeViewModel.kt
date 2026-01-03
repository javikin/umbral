package com.umbral.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umbral.data.local.dao.BlockingProfileDao
import com.umbral.data.local.entity.BlockingProfileEntity
import com.umbral.data.local.preferences.UmbralPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
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
    private val profileDao: BlockingProfileDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadState()
    }

    private fun loadState() {
        viewModelScope.launch {
            combine(
                preferences.blockingEnabled,
                preferences.currentStreak,
                preferences.onboardingCompleted,
                profileDao.getActiveProfile()
            ) { blockingEnabled, streak, onboardingCompleted, activeProfile ->
                HomeUiState(
                    isLoading = false,
                    isBlockingEnabled = blockingEnabled,
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
            val newState = !_uiState.value.isBlockingEnabled
            preferences.setBlockingEnabled(newState)
        }
    }

    fun selectProfile(profileId: String) {
        viewModelScope.launch {
            profileDao.deactivateAllProfiles()
            profileDao.activateProfile(profileId)
            preferences.setActiveProfileId(profileId)
        }
    }
}
