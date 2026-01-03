package com.umbral.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umbral.data.local.dao.NfcTagDao
import com.umbral.data.local.entity.NfcTagEntity
import com.umbral.data.local.preferences.UmbralPreferences
import com.umbral.domain.nfc.NfcError
import com.umbral.domain.nfc.NfcManager
import com.umbral.domain.nfc.NfcResult
import com.umbral.domain.nfc.NfcState
import com.umbral.domain.nfc.NfcTag
import com.umbral.domain.nfc.TagEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class NfcScanUiState(
    val nfcState: NfcState = NfcState.Enabled,
    val scanState: ScanState = ScanState.Idle,
    val lastEvent: TagEvent? = null,
    val tagName: String = "",
    val showRegisterDialog: Boolean = false
)

sealed class ScanState {
    data object Idle : ScanState()
    data object Scanning : ScanState()
    data object Success : ScanState()
    data class Error(val error: NfcError) : ScanState()
}

@HiltViewModel
class NfcScanViewModel @Inject constructor(
    private val nfcManager: NfcManager,
    private val nfcTagDao: NfcTagDao,
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
        when (event) {
            is TagEvent.KnownTag -> {
                _uiState.update {
                    it.copy(
                        scanState = ScanState.Success,
                        lastEvent = event,
                        showRegisterDialog = false
                    )
                }
                // Toggle blocking when known tag is scanned
                toggleBlocking()
            }
            is TagEvent.UnknownTag -> {
                _uiState.update {
                    it.copy(
                        scanState = ScanState.Idle,
                        lastEvent = event,
                        showRegisterDialog = true
                    )
                }
            }
            is TagEvent.InvalidTag -> {
                _uiState.update {
                    it.copy(
                        scanState = ScanState.Error(event.error),
                        lastEvent = event
                    )
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

    fun updateTagName(name: String) {
        _uiState.update { it.copy(tagName = name) }
    }

    fun registerTag() {
        val event = _uiState.value.lastEvent
        if (event !is TagEvent.UnknownTag) return

        val name = _uiState.value.tagName.ifBlank { "Tag NFC" }

        viewModelScope.launch {
            val tagEntity = NfcTagEntity(
                id = UUID.randomUUID().toString(),
                uid = event.uid,
                name = name,
                createdAt = System.currentTimeMillis()
            )

            nfcTagDao.insertTag(tagEntity)

            // Write Umbral payload to tag
            val writeResult = nfcManager.writeTag(event.androidTag, tagEntity.id)

            when (writeResult) {
                is NfcResult.Success -> {
                    _uiState.update {
                        it.copy(
                            scanState = ScanState.Success,
                            showRegisterDialog = false,
                            tagName = ""
                        )
                    }
                    // Toggle blocking after registering
                    toggleBlocking()
                }
                is NfcResult.Error -> {
                    // Delete the tag if write failed
                    nfcTagDao.deleteTagById(tagEntity.id)
                    _uiState.update {
                        it.copy(
                            scanState = ScanState.Error(writeResult.error),
                            showRegisterDialog = false
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

    private fun toggleBlocking() {
        viewModelScope.launch {
            preferences.blockingEnabled.collect { isEnabled ->
                preferences.setBlockingEnabled(!isEnabled)
                return@collect
            }
        }
    }

    fun isNfcAvailable(): Boolean = nfcManager.isNfcAvailable()
    fun isNfcEnabled(): Boolean = nfcManager.isNfcEnabled()
}
