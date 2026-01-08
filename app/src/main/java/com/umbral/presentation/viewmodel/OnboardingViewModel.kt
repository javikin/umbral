package com.umbral.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umbral.domain.apps.InstalledApp
import com.umbral.domain.model.OnboardingState
import com.umbral.domain.model.RequiredPermission
import com.umbral.domain.onboarding.OnboardingManager
import com.umbral.domain.onboarding.PermissionHelper
import com.umbral.domain.apps.InstalledAppsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingManager: OnboardingManager,
    private val permissionHelper: PermissionHelper,
    private val installedAppsRepository: InstalledAppsRepository
) : ViewModel() {

    val state: StateFlow<OnboardingState> = onboardingManager.state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = OnboardingState.initial()
        )

    private val _installedApps = MutableStateFlow<List<InstalledApp>>(emptyList())
    val installedApps: StateFlow<List<InstalledApp>> = _installedApps.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadInstalledApps()
    }

    fun nextStep() {
        onboardingManager.nextStep()
    }

    fun previousStep() {
        onboardingManager.previousStep()
    }

    fun skipStep() {
        onboardingManager.skipStep()
    }

    fun toggleAppSelection(packageName: String) {
        onboardingManager.toggleAppSelection(packageName)
    }

    fun updateProfileName(name: String) {
        onboardingManager.updateProfileName(name)
    }

    fun refreshPermissions() {
        onboardingManager.refreshPermissions()
    }

    fun openPermissionSettings(permission: RequiredPermission) {
        permissionHelper.openPermissionSettings(permission)
    }

    fun openNfcSettings() {
        permissionHelper.openNfcSettings()
    }

    fun selectPreset(preset: AppPreset) {
        val presetPackages = when (preset) {
            AppPreset.SOCIAL -> listOf(
                "com.instagram.android",
                "com.facebook.katana",
                "com.twitter.android",
                "com.snapchat.android",
                "com.zhiliaoapp.musically", // TikTok
                "com.whatsapp",
                "com.facebook.orca" // Messenger
            )
            AppPreset.GAMES -> listOf(
                "com.supercell.clashofclans",
                "com.king.candycrushsaga",
                "com.mojang.minecraftpe",
                "com.ea.gp.fifamobile",
                "com.supercell.brawlstars",
                "com.roblox.client"
            )
            AppPreset.ENTERTAINMENT -> listOf(
                "com.netflix.mediaclient",
                "com.google.android.youtube",
                "com.spotify.music",
                "com.amazon.avod.thirdpartyclient", // Prime Video
                "com.hbo.hbonow",
                "com.disney.disneyplus"
            )
        }

        // Add apps from preset that are actually installed
        val currentSelected = state.value.selectedApps.toMutableList()
        val installedPackages = _installedApps.value.map { it.packageName }

        presetPackages.forEach { pkg ->
            if (pkg in installedPackages && pkg !in currentSelected) {
                currentSelected.add(pkg)
            }
        }

        onboardingManager.updateSelectedApps(currentSelected)
    }

    fun completeOnboarding(onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = onboardingManager.completeOnboarding()

            result.fold(
                onSuccess = { profileId ->
                    _isLoading.value = false
                    onSuccess(profileId)
                },
                onFailure = { exception ->
                    _isLoading.value = false
                    val errorMessage = exception.message ?: "Error desconocido"
                    _error.value = errorMessage
                    onError(errorMessage)
                }
            )
        }
    }

    fun completeOnboardingSimple() {
        viewModelScope.launch {
            onboardingManager.completeOnboardingSimple()
        }
    }

    private fun loadInstalledApps() {
        viewModelScope.launch {
            _isLoading.value = true
            var refreshAttempts = 0
            val maxRefreshAttempts = 3

            try {
                installedAppsRepository.getInstalledApps().collect { apps ->
                    _installedApps.value = apps.sortedBy { it.name.lowercase() }

                    if (apps.isNotEmpty()) {
                        // Apps loaded successfully
                        _isLoading.value = false
                        refreshAttempts = maxRefreshAttempts // Stop any more refresh attempts
                    } else if (refreshAttempts < maxRefreshAttempts) {
                        // List is empty, try refreshing (with delay between retries)
                        refreshAttempts++
                        kotlinx.coroutines.delay(500L * refreshAttempts)
                        installedAppsRepository.refreshApps()
                    } else {
                        // Max attempts reached, stop loading
                        _isLoading.value = false
                    }
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar apps: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun refreshInstalledApps() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                installedAppsRepository.refreshApps()
                // Small delay to allow the flow to emit
                kotlinx.coroutines.delay(500)
                if (_installedApps.value.isEmpty()) {
                    _error.value = "No se encontraron apps. Verifica los permisos."
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Error al cargar apps: ${e.message}"
                _isLoading.value = false
            }
        }
    }
}

enum class AppPreset {
    SOCIAL,
    GAMES,
    ENTERTAINMENT
}
