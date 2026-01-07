package com.umbral.notifications.presentation.summary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umbral.notifications.domain.usecase.GetNotificationSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for SessionSummaryDialog.
 * Loads notification summary and calculates bonus energy.
 */
@HiltViewModel
class SessionSummaryViewModel @Inject constructor(
    private val getSummaryUseCase: GetNotificationSummaryUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(SessionSummaryState())
    val state: StateFlow<SessionSummaryState> = _state.asStateFlow()

    /**
     * Load summary for a specific session.
     * Calculates bonus energy as +1 per 5 notifications blocked.
     *
     * @param sessionId ID of the blocking session
     */
    fun loadSummary(sessionId: String) {
        viewModelScope.launch {
            try {
                val summary = getSummaryUseCase(sessionId)
                val bonusEnergy = summary.totalCount / 5

                _state.update {
                    it.copy(
                        summary = summary,
                        bonusEnergy = bonusEnergy,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                // On error, just mark as not loading with empty state
                _state.update {
                    it.copy(
                        summary = null,
                        bonusEnergy = 0,
                        isLoading = false
                    )
                }
            }
        }
    }
}
