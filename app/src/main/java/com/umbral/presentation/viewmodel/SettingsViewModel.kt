package com.umbral.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umbral.domain.permission.PermissionManager
import com.umbral.domain.permission.PermissionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val permissionState: PermissionState = PermissionState(),
    val isLoading: Boolean = true,
    val appVersion: String = "1.0.0"
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val permissionManager: PermissionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observePermissions()
    }

    private fun observePermissions() {
        viewModelScope.launch {
            permissionManager.permissionState.collect { state ->
                _uiState.update { current ->
                    current.copy(
                        permissionState = state,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun requestUsageStatsPermission() {
        permissionManager.requestUsageStatsPermission()
    }

    fun requestOverlayPermission() {
        permissionManager.requestOverlayPermission()
    }

    fun refreshPermissions() {
        viewModelScope.launch {
            permissionManager.refreshPermissions()
        }
    }
}
