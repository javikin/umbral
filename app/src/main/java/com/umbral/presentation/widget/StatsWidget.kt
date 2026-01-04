package com.umbral.presentation.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.umbral.presentation.widget.action.OpenStatsScreenAction
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first

/**
 * Widget de estadísticas (4x2) que muestra apps bloqueadas hoy, racha actual y tiempo ahorrado.
 * Click: Abre la pantalla de estadísticas.
 */
class StatsWidget : GlanceAppWidget() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface StatsWidgetEntryPoint {
        fun blockingManager(): com.umbral.domain.blocking.BlockingManager
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Obtener datos (placeholder - en producción vendría de StatsRepository)
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            StatsWidgetEntryPoint::class.java
        )

        val blockingManager = entryPoint.blockingManager()
        val currentState = blockingManager.blockingState.first()

        // TODO: Obtener stats reales desde StatsRepository cuando esté implementado
        val todayBlocked = 0 // Placeholder
        val currentStreak = 0 // Placeholder
        val timeSaved = 0L // Placeholder en minutos

        provideContent {
            StatsWidgetContent(
                todayBlocked = todayBlocked,
                currentStreak = currentStreak,
                timeSavedMinutes = timeSaved
            )
        }
    }

    @Composable
    private fun StatsWidgetContent(
        todayBlocked: Int,
        currentStreak: Int,
        timeSavedMinutes: Long
    ) {
        GlanceTheme {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(GlanceTheme.colors.surface)
                    .clickable(OpenStatsScreenAction.action())
                    .padding(16.dp)
            ) {
                // Título
                Text(
                    text = "Estadísticas",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = GlanceModifier.height(12.dp))

                // Fila de estadísticas
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Apps bloqueadas hoy
                    StatItem(
                        label = "Bloqueadas hoy",
                        value = todayBlocked.toString(),
                        modifier = GlanceModifier.defaultWeight()
                    )

                    Spacer(modifier = GlanceModifier.width(8.dp))

                    // Racha actual
                    StatItem(
                        label = "Racha actual",
                        value = "$currentStreak días",
                        modifier = GlanceModifier.defaultWeight()
                    )
                }

                Spacer(modifier = GlanceModifier.height(12.dp))

                // Tiempo ahorrado
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    StatItem(
                        label = "Tiempo ahorrado",
                        value = formatTime(timeSavedMinutes),
                        modifier = GlanceModifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    @Composable
    private fun StatItem(
        label: String,
        value: String,
        modifier: GlanceModifier = GlanceModifier
    ) {
        Column(
            modifier = modifier.padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = TextStyle(
                    color = GlanceTheme.colors.primary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = GlanceModifier.height(4.dp))
            Text(
                text = label,
                style = TextStyle(
                    color = GlanceTheme.colors.onSurfaceVariant,
                    fontSize = 12.sp
                )
            )
        }
    }

    private fun formatTime(minutes: Long): String {
        return when {
            minutes < 60 -> "${minutes}m"
            minutes < 1440 -> "${minutes / 60}h ${minutes % 60}m"
            else -> "${minutes / 1440}d ${(minutes % 1440) / 60}h"
        }
    }
}
