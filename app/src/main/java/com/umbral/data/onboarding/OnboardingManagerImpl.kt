package com.umbral.data.onboarding

import com.umbral.data.local.preferences.UmbralPreferences
import com.umbral.domain.blocking.BlockingProfile
import com.umbral.domain.blocking.ProfileRepository
import com.umbral.domain.model.OnboardingState
import com.umbral.domain.model.OnboardingStep
import com.umbral.domain.onboarding.OnboardingManager
import com.umbral.domain.onboarding.PermissionHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnboardingManagerImpl @Inject constructor(
    private val permissionHelper: PermissionHelper,
    private val profileRepository: ProfileRepository,
    private val preferences: UmbralPreferences
) : OnboardingManager {

    private val _state = MutableStateFlow(OnboardingState.initial())
    override val state: StateFlow<OnboardingState> = _state.asStateFlow()

    init {
        refreshPermissions()
    }

    override suspend fun isOnboardingComplete(): Boolean {
        return preferences.onboardingCompleted.first()
    }

    override fun nextStep() {
        _state.update { current ->
            val nextStep = when (current.currentStep) {
                OnboardingStep.WELCOME -> OnboardingStep.HOW_IT_WORKS
                OnboardingStep.HOW_IT_WORKS -> OnboardingStep.PERMISSIONS
                OnboardingStep.PERMISSIONS -> OnboardingStep.SELECT_APPS
                OnboardingStep.SELECT_APPS -> OnboardingStep.SUCCESS
                OnboardingStep.SUCCESS -> OnboardingStep.SUCCESS // Stay on success
            }
            current.copy(
                currentStep = nextStep,
                completedSteps = current.completedSteps + current.currentStep
            )
        }
    }

    override fun previousStep() {
        _state.update { current ->
            val previousStep = when (current.currentStep) {
                OnboardingStep.WELCOME -> OnboardingStep.WELCOME // Stay on first
                OnboardingStep.HOW_IT_WORKS -> OnboardingStep.WELCOME
                OnboardingStep.PERMISSIONS -> OnboardingStep.HOW_IT_WORKS
                OnboardingStep.SELECT_APPS -> OnboardingStep.PERMISSIONS
                OnboardingStep.SUCCESS -> OnboardingStep.SELECT_APPS
            }
            current.copy(currentStep = previousStep)
        }
    }

    override fun skipStep() {
        // For now, just go to next step
        nextStep()
    }

    override fun updateSelectedApps(apps: List<String>) {
        _state.update { current ->
            current.copy(selectedApps = apps)
        }
    }

    override fun toggleAppSelection(packageName: String) {
        _state.update { current ->
            val newSelection = if (current.selectedApps.contains(packageName)) {
                current.selectedApps - packageName
            } else {
                current.selectedApps + packageName
            }
            current.copy(selectedApps = newSelection)
        }
    }

    override fun updateProfileName(name: String) {
        _state.update { current ->
            current.copy(profileName = name)
        }
    }

    override fun refreshPermissions() {
        _state.update { current ->
            current.copy(
                permissionStates = permissionHelper.checkAllPermissions()
            )
        }
    }

    override suspend fun completeOnboarding(): Result<String> {
        return try {
            val currentState = _state.value

            // Validate minimum requirements
            if (currentState.selectedApps.isEmpty()) {
                return Result.failure(IllegalStateException("No apps selected"))
            }

            if (!permissionHelper.hasMinimumPermissions()) {
                return Result.failure(IllegalStateException("Missing required permissions"))
            }

            // Create the first profile
            val profileId = UUID.randomUUID().toString()
            val profile = BlockingProfile(
                id = profileId,
                name = currentState.profileName,
                iconName = "shield",
                colorHex = "#6650A4",
                isActive = false,
                isStrictMode = false,
                blockedApps = currentState.selectedApps,
                createdAt = java.time.LocalDateTime.now(),
                updatedAt = java.time.LocalDateTime.now()
            )

            // Save profile
            profileRepository.saveProfile(profile)

            // Mark onboarding as complete
            preferences.setOnboardingCompleted(true)

            // Update state
            _state.update { current ->
                current.copy(isComplete = true)
            }

            Result.success(profileId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resetOnboarding() {
        preferences.setOnboardingCompleted(false)
        _state.value = OnboardingState.initial()
        refreshPermissions()
    }
}
