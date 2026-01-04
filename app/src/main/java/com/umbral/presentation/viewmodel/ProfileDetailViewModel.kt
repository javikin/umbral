package com.umbral.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umbral.domain.blocking.BlockingProfile
import com.umbral.domain.blocking.ProfileRepository
import com.umbral.domain.nfc.NfcRepository
import com.umbral.domain.nfc.NfcTag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

data class ProfileDetailUiState(
    val isLoading: Boolean = true,
    val isNewProfile: Boolean = true,
    val profileId: String = "",
    val name: String = "",
    val colorHex: String = "#6650A4",
    val iconName: String = "shield",
    val isStrictMode: Boolean = false,
    val blockedApps: List<String> = emptyList(),
    val linkedTags: List<NfcTag> = emptyList(),
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ProfileDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val profileRepository: ProfileRepository,
    private val nfcRepository: NfcRepository
) : ViewModel() {

    private val profileId: String = savedStateHandle.get<String>("profileId") ?: "new"

    private val _formState = MutableStateFlow(FormState())

    val uiState: StateFlow<ProfileDetailUiState> = combine(
        _formState,
        nfcRepository.getAllTags()
    ) { formState, allTags ->
        val linkedTags = allTags.filter { it.profileId == formState.profileId }

        ProfileDetailUiState(
            isLoading = formState.isLoading,
            isNewProfile = formState.isNewProfile,
            profileId = formState.profileId,
            name = formState.name,
            colorHex = formState.colorHex,
            iconName = formState.iconName,
            isStrictMode = formState.isStrictMode,
            blockedApps = formState.blockedApps,
            linkedTags = linkedTags,
            isSaving = formState.isSaving,
            saveSuccess = formState.saveSuccess,
            error = formState.error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileDetailUiState()
    )

    init {
        loadProfile()
    }

    private fun loadProfile() {
        if (profileId == "new") {
            _formState.update {
                it.copy(
                    isLoading = false,
                    isNewProfile = true,
                    profileId = UUID.randomUUID().toString(),
                    name = "",
                    colorHex = AVAILABLE_COLORS.first(),
                    iconName = "shield"
                )
            }
        } else {
            viewModelScope.launch {
                val profile = profileRepository.getProfileById(profileId)
                if (profile != null) {
                    _formState.update {
                        it.copy(
                            isLoading = false,
                            isNewProfile = false,
                            profileId = profile.id,
                            name = profile.name,
                            colorHex = profile.colorHex,
                            iconName = profile.iconName,
                            isStrictMode = profile.isStrictMode,
                            blockedApps = profile.blockedApps
                        )
                    }
                } else {
                    _formState.update {
                        it.copy(
                            isLoading = false,
                            error = "Perfil no encontrado"
                        )
                    }
                }
            }
        }
    }

    fun updateName(name: String) {
        _formState.update { it.copy(name = name, error = null) }
    }

    fun updateColor(colorHex: String) {
        _formState.update { it.copy(colorHex = colorHex) }
    }

    fun updateIcon(iconName: String) {
        _formState.update { it.copy(iconName = iconName) }
    }

    fun toggleStrictMode() {
        _formState.update { it.copy(isStrictMode = !it.isStrictMode) }
    }

    fun addBlockedApp(packageName: String) {
        _formState.update { state ->
            if (packageName !in state.blockedApps) {
                state.copy(blockedApps = state.blockedApps + packageName)
            } else {
                state
            }
        }
    }

    fun removeBlockedApp(packageName: String) {
        _formState.update { state ->
            state.copy(blockedApps = state.blockedApps - packageName)
        }
    }

    fun setBlockedApps(apps: List<String>) {
        _formState.update { state ->
            state.copy(blockedApps = apps)
        }
    }

    fun unlinkTag(tagId: String) {
        viewModelScope.launch {
            nfcRepository.unlinkTagFromProfile(tagId)
        }
    }

    fun saveProfile(onSuccess: () -> Unit) {
        val state = _formState.value

        if (state.name.isBlank()) {
            _formState.update { it.copy(error = "El nombre es requerido") }
            return
        }

        viewModelScope.launch {
            _formState.update { it.copy(isSaving = true, error = null) }

            val profile = BlockingProfile(
                id = state.profileId,
                name = state.name.trim(),
                iconName = state.iconName,
                colorHex = state.colorHex,
                isActive = false,
                isStrictMode = state.isStrictMode,
                blockedApps = state.blockedApps,
                createdAt = if (state.isNewProfile) LocalDateTime.now() else LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )

            val result = profileRepository.saveProfile(profile)

            if (result.isSuccess) {
                _formState.update { it.copy(isSaving = false, saveSuccess = true) }
                onSuccess()
            } else {
                _formState.update {
                    it.copy(
                        isSaving = false,
                        error = "Error al guardar el perfil"
                    )
                }
            }
        }
    }

    private data class FormState(
        val isLoading: Boolean = true,
        val isNewProfile: Boolean = true,
        val profileId: String = "",
        val name: String = "",
        val colorHex: String = "#6650A4",
        val iconName: String = "shield",
        val isStrictMode: Boolean = false,
        val blockedApps: List<String> = emptyList(),
        val isSaving: Boolean = false,
        val saveSuccess: Boolean = false,
        val error: String? = null
    )

    companion object {
        val AVAILABLE_COLORS = listOf(
            "#6650A4", // Purple (default)
            "#D0BCFF", // Light purple
            "#4CAF50", // Green
            "#2196F3", // Blue
            "#FF9800", // Orange
            "#F44336", // Red
            "#E91E63", // Pink
            "#009688", // Teal
            "#795548", // Brown
            "#607D8B"  // Blue grey
        )

        val AVAILABLE_ICONS = listOf(
            "shield",
            "lock",
            "work",
            "home",
            "night",
            "focus",
            "fitness",
            "study"
        )
    }
}
