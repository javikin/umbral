package com.umbral.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umbral.domain.blocking.BlockingProfile
import com.umbral.domain.blocking.ProfileRepository
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

data class ProfilesUiState(
    val profiles: List<BlockingProfile> = emptyList(),
    val isLoading: Boolean = true,
    val selectedProfile: BlockingProfile? = null,
    val showDeleteDialog: Boolean = false
)

@HiltViewModel
class ProfilesViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _dialogState = MutableStateFlow(DialogState())

    val uiState: StateFlow<ProfilesUiState> = combine(
        profileRepository.getAllProfiles(),
        _dialogState
    ) { profiles, dialogState ->
        ProfilesUiState(
            profiles = profiles,
            isLoading = false,
            selectedProfile = dialogState.selectedProfile,
            showDeleteDialog = dialogState.showDeleteDialog
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfilesUiState()
    )

    fun showDeleteDialog(profile: BlockingProfile) {
        _dialogState.update {
            it.copy(selectedProfile = profile, showDeleteDialog = true)
        }
    }

    fun hideDeleteDialog() {
        _dialogState.update {
            it.copy(selectedProfile = null, showDeleteDialog = false)
        }
    }

    fun deleteProfile() {
        val profile = _dialogState.value.selectedProfile ?: return
        viewModelScope.launch {
            profileRepository.deleteProfile(profile.id)
            hideDeleteDialog()
        }
    }

    fun activateProfile(profileId: String) {
        viewModelScope.launch {
            profileRepository.activateProfile(profileId)
        }
    }

    fun deactivateProfile(profileId: String) {
        viewModelScope.launch {
            profileRepository.deactivateAllProfiles()
        }
    }

    fun createDefaultProfile() {
        viewModelScope.launch {
            val profile = BlockingProfile(
                id = UUID.randomUUID().toString(),
                name = "Mi Perfil",
                iconName = "shield",
                colorHex = "#6650A4",
                blockedApps = emptyList(),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
            profileRepository.saveProfile(profile)
        }
    }

    private data class DialogState(
        val selectedProfile: BlockingProfile? = null,
        val showDeleteDialog: Boolean = false
    )
}
