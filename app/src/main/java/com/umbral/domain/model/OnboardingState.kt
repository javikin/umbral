package com.umbral.domain.model

/**
 * Estado completo del onboarding
 */
data class OnboardingState(
    val currentStep: OnboardingStep,
    val completedSteps: Set<OnboardingStep>,
    val permissionStates: PermissionStates,
    val selectedApps: List<String>,
    val profileName: String,
    val isComplete: Boolean
) {
    companion object {
        fun initial() = OnboardingState(
            currentStep = OnboardingStep.WELCOME,
            completedSteps = emptySet(),
            permissionStates = PermissionStates.initial(),
            selectedApps = emptyList(),
            profileName = "Mi Primer Perfil",
            isComplete = false
        )
    }
}

enum class OnboardingStep {
    WELCOME,
    HOW_IT_WORKS,
    PERMISSIONS,
    SELECT_APPS,
    SUCCESS
}

data class PermissionStates(
    val usageStats: PermissionStatus,
    val overlay: PermissionStatus,
    val notifications: PermissionStatus,
    val nfc: NfcStatus
) {
    companion object {
        fun initial() = PermissionStates(
            usageStats = PermissionStatus.NOT_REQUESTED,
            overlay = PermissionStatus.NOT_REQUESTED,
            notifications = PermissionStatus.NOT_REQUESTED,
            nfc = NfcStatus.NOT_AVAILABLE
        )
    }
}

enum class PermissionStatus {
    NOT_REQUESTED,
    GRANTED,
    DENIED,
    PERMANENTLY_DENIED
}

enum class NfcStatus {
    NOT_AVAILABLE,
    DISABLED,
    ENABLED
}

/**
 * Preferencias persistidas del onboarding
 */
data class OnboardingPreferences(
    val hasCompletedOnboarding: Boolean,
    val completedAt: Long?,
    val skippedPermissions: Set<String>,
    val firstProfileId: String?
)
