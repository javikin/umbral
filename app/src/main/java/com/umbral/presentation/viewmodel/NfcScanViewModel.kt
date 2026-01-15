package com.umbral.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umbral.data.local.preferences.UmbralPreferences
import com.umbral.domain.blocking.ProfileRepository
import com.umbral.domain.nfc.NfcError
import com.umbral.domain.nfc.NfcManager
import com.umbral.domain.nfc.NfcRepository
import com.umbral.domain.nfc.NfcResult
import com.umbral.domain.nfc.NfcState
import com.umbral.domain.nfc.NfcTag
import com.umbral.domain.nfc.TagEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

data class NfcScanUiState(
    val nfcState: NfcState = NfcState.Enabled,
    val scanState: ScanState = ScanState.Idle,
    val lastEvent: TagEvent? = null,
    val tagName: String = "",
    val tagLocation: String = "",
    val showRegisterDialog: Boolean = false,
    val showRegisterForm: Boolean = false,
    val isWriting: Boolean = false,
    val lastScannedTag: NfcTag? = null,
    val pendingTagData: PendingTagData? = null
)

data class PendingTagData(
    val name: String,
    val location: String?
)

sealed class ScanState {
    data object Idle : ScanState()
    data object Scanning : ScanState()
    data object WaitingForTag : ScanState()
    data object Writing : ScanState()
    data object Success : ScanState()
    data class TagRegistered(val tag: NfcTag) : ScanState()
    data class Error(val error: NfcError) : ScanState()
}

@HiltViewModel
class NfcScanViewModel @Inject constructor(
    private val nfcManager: NfcManager,
    private val nfcRepository: NfcRepository,
    private val profileRepository: ProfileRepository,
    private val preferences: UmbralPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(NfcScanUiState())
    val uiState: StateFlow<NfcScanUiState> = _uiState.asStateFlow()

    init {
        observeNfcState()
        observeTagEvents()
    }

    private fun observeNfcState() {
        viewModelScope.launch {
            nfcManager.nfcState.collect { state ->
                _uiState.update { it.copy(nfcState = state) }
            }
        }
    }

    private fun observeTagEvents() {
        viewModelScope.launch {
            nfcManager.tagEvents.collect { event ->
                handleTagEvent(event)
            }
        }
    }

    private fun handleTagEvent(event: TagEvent) {
        val currentState = _uiState.value

        when (event) {
            is TagEvent.KnownTag -> {
                _uiState.update {
                    it.copy(
                        scanState = ScanState.Success,
                        lastEvent = event,
                        lastScannedTag = event.tag,
                        showRegisterDialog = false,
                        showRegisterForm = false
                    )
                }
                // Update last used and toggle profile/blocking
                viewModelScope.launch {
                    nfcRepository.updateLastUsed(event.tag.uid)
                    toggleProfileOrBlocking(event.tag.profileId)
                }
            }
            is TagEvent.UnknownTag -> {
                // If we're waiting for a tag with pending data, register immediately
                if (currentState.scanState == ScanState.WaitingForTag && currentState.pendingTagData != null) {
                    registerTagWithPendingData(event, currentState.pendingTagData)
                } else {
                    // Old flow: show dialog (kept for compatibility)
                    _uiState.update {
                        it.copy(
                            scanState = ScanState.Idle,
                            lastEvent = event,
                            showRegisterDialog = true,
                            tagName = "",
                            tagLocation = ""
                        )
                    }
                }
            }
            is TagEvent.InvalidTag -> {
                _uiState.update {
                    it.copy(
                        scanState = ScanState.Error(event.error),
                        lastEvent = event,
                        pendingTagData = null
                    )
                }
            }
        }
    }

    private fun registerTagWithPendingData(event: TagEvent.UnknownTag, pendingData: PendingTagData) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    scanState = ScanState.Writing,
                    isWriting = true,
                    lastEvent = event
                )
            }

            val newTag = NfcTag(
                id = UUID.randomUUID().toString(),
                uid = event.uid,
                name = pendingData.name.ifBlank { "Tag NFC" },
                location = pendingData.location,
                profileId = null, // Profile is linked later from profile detail
                createdAt = Instant.now()
            )

            // Insert tag first
            val insertResult = nfcRepository.insertTag(newTag)

            if (insertResult.isFailure) {
                _uiState.update {
                    it.copy(
                        scanState = ScanState.Error(NfcError.UNKNOWN_ERROR),
                        isWriting = false,
                        pendingTagData = null,
                        showRegisterForm = false
                    )
                }
                return@launch
            }

            // Write Umbral payload to tag
            val writeResult = nfcManager.writeTag(event.androidTag, newTag.id)

            when (writeResult) {
                is NfcResult.Success -> {
                    _uiState.update {
                        it.copy(
                            scanState = ScanState.TagRegistered(newTag),
                            lastScannedTag = newTag,
                            isWriting = false,
                            pendingTagData = null,
                            showRegisterForm = false,
                            tagName = "",
                            tagLocation = ""
                        )
                    }
                    // Don't toggle blocking - tag has no profile linked yet
                }
                is NfcResult.Error -> {
                    nfcRepository.deleteTag(newTag.id)
                    _uiState.update {
                        it.copy(
                            scanState = ScanState.Error(writeResult.error),
                            isWriting = false,
                            pendingTagData = null
                        )
                    }
                }
            }
        }
    }

    fun startScanning() {
        _uiState.update { it.copy(scanState = ScanState.Scanning) }
    }

    fun stopScanning() {
        _uiState.update { it.copy(scanState = ScanState.Idle) }
    }

    fun showRegisterForm() {
        _uiState.update {
            it.copy(
                showRegisterForm = true,
                tagName = "",
                tagLocation = ""
            )
        }
    }

    fun hideRegisterForm() {
        _uiState.update {
            it.copy(
                showRegisterForm = false,
                pendingTagData = null,
                scanState = ScanState.Idle
            )
        }
    }

    fun startWaitingForTag() {
        val currentState = _uiState.value
        val name = currentState.tagName
        val location = currentState.tagLocation.ifBlank { null }

        _uiState.update {
            it.copy(
                scanState = ScanState.WaitingForTag,
                pendingTagData = PendingTagData(name, location)
            )
        }
    }

    fun cancelWaitingForTag() {
        _uiState.update {
            it.copy(
                scanState = ScanState.Idle,
                pendingTagData = null,
                showRegisterForm = true
            )
        }
    }

    fun updateTagName(name: String) {
        _uiState.update { it.copy(tagName = name) }
    }

    fun updateTagLocation(location: String) {
        _uiState.update { it.copy(tagLocation = location) }
    }

    fun registerTag() {
        val event = _uiState.value.lastEvent
        if (event !is TagEvent.UnknownTag) return

        val name = _uiState.value.tagName.ifBlank { "Tag NFC" }
        val location = _uiState.value.tagLocation.ifBlank { null }

        viewModelScope.launch {
            // Show writing state
            _uiState.update {
                it.copy(
                    scanState = ScanState.Writing,
                    isWriting = true,
                    showRegisterDialog = false
                )
            }

            val newTag = NfcTag(
                id = UUID.randomUUID().toString(),
                uid = event.uid,
                name = name,
                location = location,
                createdAt = Instant.now()
            )

            // Insert tag first
            val insertResult = nfcRepository.insertTag(newTag)

            if (insertResult.isFailure) {
                _uiState.update {
                    it.copy(
                        scanState = ScanState.Error(NfcError.UNKNOWN_ERROR),
                        isWriting = false,
                        tagName = "",
                        tagLocation = ""
                    )
                }
                return@launch
            }

            // Write Umbral payload to tag
            val writeResult = nfcManager.writeTag(event.androidTag, newTag.id)

            when (writeResult) {
                is NfcResult.Success -> {
                    _uiState.update {
                        it.copy(
                            scanState = ScanState.TagRegistered(newTag),
                            lastScannedTag = newTag,
                            isWriting = false,
                            tagName = "",
                            tagLocation = ""
                        )
                    }
                    // Toggle blocking after registering
                    toggleProfileOrBlocking(newTag.profileId)
                }
                is NfcResult.Error -> {
                    // Delete the tag if write failed
                    nfcRepository.deleteTag(newTag.id)
                    _uiState.update {
                        it.copy(
                            scanState = ScanState.Error(writeResult.error),
                            isWriting = false,
                            tagName = "",
                            tagLocation = ""
                        )
                    }
                }
            }
        }
    }

    fun dismissDialog() {
        _uiState.update {
            it.copy(
                showRegisterDialog = false,
                tagName = ""
            )
        }
    }

    fun resetState() {
        _uiState.update {
            it.copy(
                scanState = ScanState.Idle,
                lastEvent = null
            )
        }
    }

    fun openNfcSettings(context: android.content.Context) {
        nfcManager.openNfcSettings(context)
    }

    private suspend fun toggleProfileOrBlocking(profileId: String?) {
        if (profileId != null) {
            // Toggle the associated profile
            val profile = profileRepository.getProfileById(profileId)
            if (profile != null) {
                if (profile.isActive) {
                    // Profile is active, deactivate it
                    profileRepository.deactivateAllProfiles()
                    preferences.setBlockingEnabled(false)
                } else {
                    // Profile is not active, activate it
                    profileRepository.activateProfile(profileId)
                    preferences.setBlockingEnabled(true)
                }
            } else {
                // Profile not found, fallback to toggle blocking
                toggleBlocking()
            }
        } else {
            // No profile associated, just toggle blocking
            toggleBlocking()
        }
    }

    private suspend fun toggleBlocking() {
        val isEnabled = preferences.blockingEnabled.first()
        preferences.setBlockingEnabled(!isEnabled)
    }

    fun isNfcAvailable(): Boolean = nfcManager.isNfcAvailable()
    fun isNfcEnabled(): Boolean = nfcManager.isNfcEnabled()
}
