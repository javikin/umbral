package com.umbral.presentation.viewmodel

import android.net.Uri
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umbral.domain.blocking.BlockingManager
import com.umbral.domain.blocking.BlockingProfile
import com.umbral.domain.qr.QrAction
import com.umbral.domain.qr.QrScanResult
import com.umbral.domain.qr.QrScanner
import com.umbral.domain.qr.QrScannerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QrScanUiState(
    val scannerState: QrScannerState = QrScannerState.Idle,
    val isFlashlightOn: Boolean = false,
    val scannedProfile: BlockingProfile? = null,
    val error: String? = null
)

@HiltViewModel
class QrScanViewModel @Inject constructor(
    private val qrScanner: QrScanner,
    private val blockingManager: BlockingManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(QrScanUiState())
    val uiState: StateFlow<QrScanUiState> = _uiState.asStateFlow()

    init {
        observeScannerState()
        observeScanResults()
    }

    private fun observeScannerState() {
        viewModelScope.launch {
            qrScanner.scannerState.collect { state ->
                _uiState.update { it.copy(scannerState = state) }
            }
        }
    }

    private fun observeScanResults() {
        viewModelScope.launch {
            qrScanner.lastResult.collect { result ->
                handleScanResult(result)
            }
        }
    }

    fun startScanning(lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
        qrScanner.startScanning(lifecycleOwner, previewView)
    }

    fun stopScanning() {
        qrScanner.stopScanning()
    }

    fun toggleFlashlight() {
        qrScanner.toggleFlashlight()
        viewModelScope.launch {
            qrScanner.isFlashlightOn.collect { isOn ->
                _uiState.update { it.copy(isFlashlightOn = isOn) }
            }
        }
    }

    fun scanFromGallery() {
        // TODO: Open gallery picker and scan image
        // This would require activity result contract integration
    }

    private fun handleScanResult(result: QrScanResult) {
        when (result) {
            is QrScanResult.Success -> {
                executeQrAction(result)
                _uiState.update {
                    it.copy(
                        scannedProfile = result.profile,
                        error = null
                    )
                }
            }

            is QrScanResult.InvalidFormat -> {
                _uiState.update {
                    it.copy(error = "Codigo QR no valido")
                }
            }

            is QrScanResult.ExpiredQr -> {
                _uiState.update {
                    it.copy(error = "Este codigo QR ha expirado")
                }
            }

            is QrScanResult.ProfileNotFound -> {
                _uiState.update {
                    it.copy(error = "Perfil no encontrado")
                }
            }

            is QrScanResult.InvalidSignature -> {
                _uiState.update {
                    it.copy(error = "Codigo QR alterado o invalido")
                }
            }

            is QrScanResult.NotUmbralQr -> {
                // Ignore non-Umbral QR codes silently
            }
        }
    }

    private fun executeQrAction(result: QrScanResult.Success) {
        viewModelScope.launch {
            try {
                val profileId = result.profile.id
                when (result.payload.action) {
                    QrAction.ACTIVATE -> {
                        blockingManager.startBlocking(profileId)
                    }

                    QrAction.DEACTIVATE -> {
                        blockingManager.stopBlocking()
                    }

                    QrAction.TOGGLE -> {
                        // Check if this profile is currently active
                        val currentState = blockingManager.blockingState.value
                        if (currentState.isActive && currentState.activeProfileId == profileId) {
                            blockingManager.stopBlocking()
                        } else {
                            blockingManager.startBlocking(profileId)
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Error al ejecutar accion: ${e.message}")
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    override fun onCleared() {
        super.onCleared()
        stopScanning()
    }
}
