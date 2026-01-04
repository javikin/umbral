package com.umbral.presentation.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper para actualizar todos los widgets cuando cambie el estado de bloqueo.
 * Se debe llamar desde BlockingManager cuando cambie el estado.
 */
@Singleton
class WidgetUpdater @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Actualiza todos los widgets de Umbral.
     */
    fun updateAllWidgets() {
        scope.launch {
            try {
                val manager = GlanceAppWidgetManager(context)

                // Actualizar StatusWidget
                val statusWidget = StatusWidget()
                manager.getGlanceIds(statusWidget.javaClass).forEach { id ->
                    statusWidget.update(context, id)
                }

                // Actualizar StatsWidget
                val statsWidget = StatsWidget()
                manager.getGlanceIds(statsWidget.javaClass).forEach { id ->
                    statsWidget.update(context, id)
                }

                // Actualizar QuickToggleWidget
                val quickToggleWidget = QuickToggleWidget()
                manager.getGlanceIds(quickToggleWidget.javaClass).forEach { id ->
                    quickToggleWidget.update(context, id)
                }

                Timber.d("All widgets updated successfully")
            } catch (e: Exception) {
                Timber.e(e, "Error updating widgets")
            }
        }
    }

    /**
     * Actualiza solo el widget de estado.
     */
    fun updateStatusWidget() {
        scope.launch {
            try {
                val manager = GlanceAppWidgetManager(context)
                val statusWidget = StatusWidget()
                manager.getGlanceIds(statusWidget.javaClass).forEach { id ->
                    statusWidget.update(context, id)
                }
                Timber.d("Status widget updated")
            } catch (e: Exception) {
                Timber.e(e, "Error updating status widget")
            }
        }
    }

    /**
     * Actualiza solo el widget de estadísticas.
     */
    fun updateStatsWidget() {
        scope.launch {
            try {
                val manager = GlanceAppWidgetManager(context)
                val statsWidget = StatsWidget()
                manager.getGlanceIds(statsWidget.javaClass).forEach { id ->
                    statsWidget.update(context, id)
                }
                Timber.d("Stats widget updated")
            } catch (e: Exception) {
                Timber.e(e, "Error updating stats widget")
            }
        }
    }

    /**
     * Actualiza solo el widget de toggle rápido.
     */
    fun updateQuickToggleWidget() {
        scope.launch {
            try {
                val manager = GlanceAppWidgetManager(context)
                val quickToggleWidget = QuickToggleWidget()
                manager.getGlanceIds(quickToggleWidget.javaClass).forEach { id ->
                    quickToggleWidget.update(context, id)
                }
                Timber.d("Quick toggle widget updated")
            } catch (e: Exception) {
                Timber.e(e, "Error updating quick toggle widget")
            }
        }
    }
}
