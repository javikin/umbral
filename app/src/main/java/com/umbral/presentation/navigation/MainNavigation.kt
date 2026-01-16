package com.umbral.presentation.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.umbral.data.local.preferences.UmbralPreferences
import com.umbral.domain.blocking.BlockingManager
import com.umbral.domain.blocking.SessionEndedEvent
import com.umbral.domain.blocking.SessionStartedEvent
import com.umbral.notifications.domain.model.NotificationSummary
import com.umbral.notifications.domain.usecase.GetNotificationSummaryUseCase
import com.umbral.notifications.presentation.summary.SessionStartedDialog
import com.umbral.notifications.presentation.summary.SessionSummaryDialog
import com.umbral.presentation.ui.screens.onboarding.OnboardingNavHost
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Data for showing session summary dialog.
 */
data class SessionSummaryData(
    val summary: NotificationSummary,
    val bonusEnergy: Int
)

@HiltViewModel
class MainNavigationViewModel @Inject constructor(
    val preferences: UmbralPreferences,
    private val blockingManager: BlockingManager,
    private val getNotificationSummaryUseCase: GetNotificationSummaryUseCase
) : ViewModel() {

    private val _sessionSummaryEvent = MutableSharedFlow<SessionSummaryData>(extraBufferCapacity = 1)
    val sessionSummaryEvent: SharedFlow<SessionSummaryData> = _sessionSummaryEvent.asSharedFlow()

    private val _sessionStartedEvent = MutableSharedFlow<SessionStartedEvent>(extraBufferCapacity = 1)
    val sessionStartedEvent: SharedFlow<SessionStartedEvent> = _sessionStartedEvent.asSharedFlow()

    init {
        // Listen for session started events
        viewModelScope.launch {
            blockingManager.sessionStartedEvent.collect { event ->
                Timber.d("Session started event received: sessionId=${event.sessionId}")
                _sessionStartedEvent.tryEmit(event)
            }
        }

        // Listen for session ended events
        viewModelScope.launch {
            blockingManager.sessionEndedEvent.collect { event ->
                Timber.d("Session ended event received: sessionId=${event.sessionId}")
                loadAndEmitSummary(event)
            }
        }
    }

    private suspend fun loadAndEmitSummary(event: SessionEndedEvent) {
        try {
            val summary = getNotificationSummaryUseCase(event.sessionId)
            val bonusEnergy = summary.totalCount / 5
            _sessionSummaryEvent.tryEmit(SessionSummaryData(summary, bonusEnergy))
            Timber.d("Session summary emitted: ${summary.totalCount} notifications blocked, duration: ${summary.sessionDuration}")
        } catch (e: Exception) {
            Timber.e(e, "Failed to load session summary")
        }
    }
}

@Composable
fun MainNavigation(
    viewModel: MainNavigationViewModel = hiltViewModel()
) {
    val onboardingCompleted by viewModel.preferences.onboardingCompleted
        .collectAsStateWithLifecycle(initialValue = false)

    // Session started dialog state
    var showStartedDialog by remember { mutableStateOf<SessionStartedEvent?>(null) }

    // Session summary dialog state
    var showSummaryDialog by remember { mutableStateOf<SessionSummaryData?>(null) }

    // Navigation callback for when user wants to view all notifications
    var navigateToHistory by remember { mutableStateOf(false) }

    // Collect session started events
    LaunchedEffect(Unit) {
        viewModel.sessionStartedEvent.collect { event ->
            showStartedDialog = event
        }
    }

    // Collect session summary events
    LaunchedEffect(Unit) {
        viewModel.sessionSummaryEvent.collect { summaryData ->
            showSummaryDialog = summaryData
        }
    }

    LaunchedEffect(onboardingCompleted) {
        Log.d("MainNavigation", "onboardingCompleted = $onboardingCompleted")
    }

    Box {
        if (!onboardingCompleted) {
            // Show onboarding flow
            OnboardingNavHost(
                onOnboardingComplete = {
                    // Preferences update will trigger recomposition
                }
            )
        } else {
            // Show main app
            UmbralNavHost(
                navigateToNotificationHistory = navigateToHistory,
                onNavigatedToHistory = { navigateToHistory = false }
            )
        }

        // Session Started Dialog
        showStartedDialog?.let { event ->
            SessionStartedDialog(
                event = event,
                onDismiss = {
                    showStartedDialog = null
                }
            )
        }

        // Session Summary Dialog
        showSummaryDialog?.let { summaryData ->
            SessionSummaryDialog(
                summary = summaryData.summary,
                bonusEnergy = summaryData.bonusEnergy,
                onViewAll = {
                    showSummaryDialog = null
                    navigateToHistory = true
                },
                onDismiss = {
                    showSummaryDialog = null
                }
            )
        }
    }
}
