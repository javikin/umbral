package com.umbral.presentation.viewmodel

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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TagsUiState(
    val tags: List<NfcTag> = emptyList(),
    val profiles: List<BlockingProfile> = emptyList(),
    val isLoading: Boolean = true,
    val selectedTag: NfcTag? = null,
    val showDeleteDialog: Boolean = false,
    val showLinkProfileDialog: Boolean = false
)

@HiltViewModel
class TagsViewModel @Inject constructor(
    private val nfcRepository: NfcRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _dialogState = MutableStateFlow(DialogState())

    val uiState: StateFlow<TagsUiState> = combine(
        nfcRepository.getAllTags(),
        profileRepository.getAllProfiles(),
        _dialogState
    ) { tags, profiles, dialogState ->
        TagsUiState(
            tags = tags.sortedByDescending { it.createdAt },
            profiles = profiles,
            isLoading = false,
            selectedTag = dialogState.selectedTag,
            showDeleteDialog = dialogState.showDeleteDialog,
            showLinkProfileDialog = dialogState.showLinkProfileDialog
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TagsUiState()
    )

    fun showDeleteDialog(tag: NfcTag) {
        _dialogState.update {
            it.copy(selectedTag = tag, showDeleteDialog = true)
        }
    }

    fun hideDeleteDialog() {
        _dialogState.update {
            it.copy(selectedTag = null, showDeleteDialog = false)
        }
    }

    fun deleteTag() {
        val tag = _dialogState.value.selectedTag ?: return
        viewModelScope.launch {
            nfcRepository.deleteTag(tag.id)
            hideDeleteDialog()
        }
    }

    fun showLinkProfileDialog(tag: NfcTag) {
        _dialogState.update {
            it.copy(selectedTag = tag, showLinkProfileDialog = true)
        }
    }

    fun hideLinkProfileDialog() {
        _dialogState.update {
            it.copy(selectedTag = null, showLinkProfileDialog = false)
        }
    }

    fun linkTagToProfile(profileId: String) {
        val tag = _dialogState.value.selectedTag ?: return
        viewModelScope.launch {
            nfcRepository.linkTagToProfile(tag.id, profileId)
            hideLinkProfileDialog()
        }
    }

    fun unlinkTagFromProfile(tagId: String) {
        viewModelScope.launch {
            nfcRepository.unlinkTagFromProfile(tagId)
        }
    }

    fun getProfileName(profileId: String?): String? {
        if (profileId == null) return null
        return uiState.value.profiles.find { it.id == profileId }?.name
    }

    private data class DialogState(
        val selectedTag: NfcTag? = null,
        val showDeleteDialog: Boolean = false,
        val showLinkProfileDialog: Boolean = false
    )
}
