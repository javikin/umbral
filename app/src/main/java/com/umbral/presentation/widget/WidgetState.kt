package com.umbral.presentation.widget

import com.umbral.domain.blocking.BlockingState

/**
 * Estado compartido para todos los widgets de Umbral.
 * Sincronizado desde BlockingManager y ProfileRepository.
 */
data class WidgetState(
    val isActive: Boolean = false,
    val profileName: String? = null,
    val blockedAppsCount: Int = 0,
    val todayBlockedAttempts: Int = 0,
    val currentStreak: Int = 0,
    val timeSaved: Long = 0L // en minutos
)

/**
 * Convierte BlockingState del dominio a estado de widget.
 */
fun BlockingState.toWidgetState(
    todayBlockedAttempts: Int = 0,
    currentStreak: Int = 0,
    timeSaved: Long = 0L
): WidgetState {
    return WidgetState(
        isActive = this.isActive,
        profileName = this.activeProfileName,
        blockedAppsCount = this.blockedApps.size,
        todayBlockedAttempts = todayBlockedAttempts,
        currentStreak = currentStreak,
        timeSaved = timeSaved
    )
}
