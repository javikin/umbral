package com.umbral.glance.util

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Helper object for updating Glance widgets across the app
 *
 * Provides centralized methods to trigger widget updates when:
 * - Blocking state changes
 * - Profile is activated/deactivated
 * - Stats are updated
 * - Timer completes
 */
object WidgetUpdateHelper {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Update all widgets of a specific type
     *
     * This is a template method - actual widget classes will be added in subsequent tasks
     */
    private suspend fun <T : androidx.glance.appwidget.GlanceAppWidget> updateWidgets(
        context: Context,
        widget: T
    ) {
        try {
            widget.updateAll(context)
            Timber.d("Updated all ${widget::class.simpleName} widgets")
        } catch (e: Exception) {
            Timber.e(e, "Failed to update ${widget::class.simpleName} widgets")
        }
    }

    /**
     * Update all active widgets when blocking state changes
     *
     * Should be called when:
     * - User activates a profile
     * - User deactivates blocking
     * - Timer auto-unblocks
     */
    fun updateOnBlockingStateChange(context: Context) {
        scope.launch {
            try {
                // TODO: Add actual widget updates in Task 2 (Status Widget)
                // updateWidgets(context, StatusWidget())
                Timber.d("Blocking state changed - widget updates triggered")
            } catch (e: Exception) {
                Timber.e(e, "Failed to update widgets on blocking state change")
            }
        }
    }

    /**
     * Update all widgets when stats are updated
     *
     * Should be called when:
     * - Daily stats are recorded
     * - Streak is updated
     * - New achievement is unlocked
     */
    fun updateOnStatsChange(context: Context) {
        scope.launch {
            try {
                // TODO: Add actual widget updates in Task 3 (Streak Widget)
                // updateWidgets(context, StreakWidget())
                Timber.d("Stats changed - widget updates triggered")
            } catch (e: Exception) {
                Timber.e(e, "Failed to update widgets on stats change")
            }
        }
    }

    /**
     * Update all widgets (force refresh)
     *
     * Use sparingly - only for manual refresh actions
     */
    fun updateAllWidgets(context: Context) {
        scope.launch {
            try {
                updateOnBlockingStateChange(context)
                updateOnStatsChange(context)
                Timber.d("All widgets updated")
            } catch (e: Exception) {
                Timber.e(e, "Failed to update all widgets")
            }
        }
    }

    /**
     * Get count of active widget instances for a specific widget type
     */
    suspend fun <T : androidx.glance.appwidget.GlanceAppWidget> getWidgetCount(
        context: Context,
        widget: T
    ): Int {
        return try {
            val manager = GlanceAppWidgetManager(context)
            val glanceIds = manager.getGlanceIds(widget::class.java)
            glanceIds.size
        } catch (e: Exception) {
            Timber.e(e, "Failed to get widget count for ${widget::class.simpleName}")
            0
        }
    }
}
