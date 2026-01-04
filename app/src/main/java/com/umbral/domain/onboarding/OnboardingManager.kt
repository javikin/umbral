package com.umbral.domain.onboarding

import com.umbral.domain.model.OnboardingState
import com.umbral.domain.model.RequiredPermission
import kotlinx.coroutines.flow.StateFlow

interface OnboardingManager {

    /**
     * Estado actual del onboarding
     */
    val state: StateFlow<OnboardingState>

    /**
     * Verifica si el onboarding está completo
     */
    suspend fun isOnboardingComplete(): Boolean

    /**
     * Avanza al siguiente paso
     */
    fun nextStep()

    /**
     * Retrocede al paso anterior
     */
    fun previousStep()

    /**
     * Salta un paso opcional
     */
    fun skipStep()

    /**
     * Actualiza las apps seleccionadas
     */
    fun updateSelectedApps(apps: List<String>)

    /**
     * Alterna la selección de una app
     */
    fun toggleAppSelection(packageName: String)

    /**
     * Actualiza el nombre del perfil
     */
    fun updateProfileName(name: String)

    /**
     * Actualiza los estados de permisos
     */
    fun refreshPermissions()

    /**
     * Crea el perfil inicial y completa el onboarding
     */
    suspend fun completeOnboarding(): Result<String>  // Returns profile ID

    /**
     * Resetea el onboarding (para testing)
     */
    suspend fun resetOnboarding()
}
