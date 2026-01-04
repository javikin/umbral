package com.umbral.presentation.widget.action

import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.action.Action
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import com.umbral.presentation.MainActivity
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first

/**
 * Acción para abrir la app principal.
 */
object OpenAppAction {
    fun action(): Action = actionStartActivity<MainActivity>()
}

/**
 * Acción para abrir la pantalla de estadísticas.
 */
object OpenStatsScreenAction {
    fun action(): Action = actionStartActivity<MainActivity>(
        actionParametersOf(destinationKey to "stats")
    )

    private val destinationKey = ActionParameters.Key<String>("destination")
}

/**
 * Acción para hacer toggle del bloqueo.
 */
object ToggleBlockingAction {
    fun action(): Action = actionRunCallback<ToggleBlockingCallback>()
}

/**
 * Callback para toggle de bloqueo desde widget.
 */
class ToggleBlockingCallback : ActionCallback {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ToggleBlockingCallbackEntryPoint {
        fun blockingManager(): com.umbral.domain.blocking.BlockingManager
    }

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            ToggleBlockingCallbackEntryPoint::class.java
        )

        val blockingManager = entryPoint.blockingManager()
        val currentState = blockingManager.blockingState.first()

        // Toggle: Si está activo, detener. Si está inactivo, activar con el último perfil usado.
        if (currentState.isActive) {
            blockingManager.stopBlocking()
        } else {
            // TODO: Activar con el último perfil usado o el perfil por defecto
            // Por ahora, solo cambiamos el estado (necesitamos ProfileRepository para obtener profileId)
            // En una implementación completa, esto debería:
            // 1. Obtener el último perfil usado desde preferences
            // 2. Si no hay perfil previo, mostrar un toast indicando que debe seleccionar un perfil
            // 3. Llamar a blockingManager.startBlocking(profileId)

            // Placeholder - necesita implementación completa
            timber.log.Timber.w("Toggle widget clicked but no profile selected. Open app to select a profile.")
        }

        // Actualizar todos los widgets
        updateAllWidgets(context)
    }

    private suspend fun updateAllWidgets(context: Context) {
        val manager = GlanceAppWidgetManager(context)

        // Actualizar StatusWidget
        val statusWidget = com.umbral.presentation.widget.StatusWidget()
        manager.getGlanceIds(statusWidget.javaClass).forEach { id ->
            statusWidget.update(context, id)
        }

        // Actualizar StatsWidget
        val statsWidget = com.umbral.presentation.widget.StatsWidget()
        manager.getGlanceIds(statsWidget.javaClass).forEach { id ->
            statsWidget.update(context, id)
        }

        // Actualizar QuickToggleWidget
        val quickToggleWidget = com.umbral.presentation.widget.QuickToggleWidget()
        manager.getGlanceIds(quickToggleWidget.javaClass).forEach { id ->
            quickToggleWidget.update(context, id)
        }
    }
}
