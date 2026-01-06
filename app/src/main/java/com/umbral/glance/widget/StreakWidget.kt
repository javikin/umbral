package com.umbral.glance.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.umbral.R
import com.umbral.glance.theme.WidgetColors
import com.umbral.presentation.widget.action.OpenStatsScreenAction
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Widget de racha (2x2) que muestra la racha actual de días usando Umbral
 * con un mini calendario semanal.
 *
 * Diseño:
 * - Icono de fuego + número de días de racha
 * - Label "días"
 * - Mini calendario semanal (L M X J V S D)
 * - Puntos para cada día (llenos si activo, vacíos si no)
 * - Día actual resaltado
 */
class StreakWidget : GlanceAppWidget() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface StreakWidgetEntryPoint {
        fun statsRepository(): com.umbral.domain.stats.StatsRepository
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            StreakWidgetEntryPoint::class.java
        )

        val statsRepository = entryPoint.statsRepository()

        // Obtener estadísticas semanales para calcular la racha
        val weeklyStats = try {
            statsRepository.getWeeklyStats()
        } catch (e: Exception) {
            null
        }

        // Calcular racha actual basada en días consecutivos con actividad
        val currentStreak = calculateStreak(weeklyStats?.dailyStats ?: emptyList())

        // Obtener actividad de cada día de la semana
        val weeklyActivity = getWeeklyActivity(weeklyStats?.dailyStats ?: emptyList())

        provideContent {
            StreakWidgetContent(
                streakDays = currentStreak,
                weeklyActivity = weeklyActivity
            )
        }
    }

    @Composable
    private fun StreakWidgetContent(
        streakDays: Int,
        weeklyActivity: List<Boolean>
    ) {
        GlanceTheme {
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(WidgetColors.surface)
                    .cornerRadius(16.dp)
                    .clickable(OpenStatsScreenAction.action())
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Icono de fuego + número de racha
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            provider = ImageProvider(R.drawable.ic_fire),
                            contentDescription = "Racha",
                            modifier = GlanceModifier.size(32.dp)
                        )
                        Spacer(modifier = GlanceModifier.width(4.dp))
                        Text(
                            text = streakDays.toString(),
                            style = TextStyle(
                                color = WidgetColors.streak,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(modifier = GlanceModifier.height(2.dp))

                    // Label "días"
                    Text(
                        text = "días",
                        style = TextStyle(
                            color = WidgetColors.onSurfaceVariant,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )
                    )

                    Spacer(modifier = GlanceModifier.height(12.dp))

                    // Mini calendario semanal
                    WeeklyCalendar(weeklyActivity = weeklyActivity)
                }
            }
        }
    }

    @Composable
    private fun WeeklyCalendar(weeklyActivity: List<Boolean>) {
        val dayLabels = listOf("L", "M", "X", "J", "V", "S", "D")
        val today = LocalDate.now().dayOfWeek.value // 1 = Lunes, 7 = Domingo

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Etiquetas de días
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                dayLabels.forEachIndexed { index, label ->
                    val isToday = index + 1 == today
                    Text(
                        text = label,
                        style = TextStyle(
                            color = if (isToday) WidgetColors.primary else WidgetColors.onSurfaceVariant,
                            fontSize = 9.sp,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                        ),
                        modifier = GlanceModifier.padding(horizontal = 2.dp)
                    )
                }
            }

            Spacer(modifier = GlanceModifier.height(4.dp))

            // Puntos de actividad
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                weeklyActivity.forEachIndexed { index, isActive ->
                    val isToday = index + 1 == today
                    Box(
                        modifier = GlanceModifier
                            .size(8.dp)
                            .padding(horizontal = 2.dp)
                            .cornerRadius(4.dp)
                            .background(
                                when {
                                    isActive && isToday -> WidgetColors.primary
                                    isActive -> WidgetColors.streak
                                    isToday -> WidgetColors.outline
                                    else -> WidgetColors.surfaceVariant
                                }
                            ),
                        contentAlignment = Alignment.Center,
                        content = {}
                    )
                }
            }
        }
    }

    /**
     * Calcula la racha actual de días consecutivos con actividad.
     * Una "racha" se cuenta como días donde hubo minutos de bloqueo.
     */
    private fun calculateStreak(dailyStats: List<com.umbral.data.local.dao.DailyStats>): Int {
        if (dailyStats.isEmpty()) return 0

        // Crear mapa de día -> minutos
        val statsMap = dailyStats.associate { stat ->
            LocalDate.parse(stat.day) to stat.minutes
        }

        var streak = 0
        var currentDate = LocalDate.now()

        // Contar días consecutivos hacia atrás desde hoy
        while (true) {
            val minutes = statsMap[currentDate] ?: 0
            if (minutes > 0) {
                streak++
                currentDate = currentDate.minus(1, ChronoUnit.DAYS)
            } else {
                break
            }

            // Límite de seguridad (máximo 365 días)
            if (streak >= 365) break
        }

        return streak
    }

    /**
     * Obtiene la actividad de cada día de la semana actual.
     * Retorna lista de 7 booleanos (L-D) indicando si hubo actividad.
     */
    private fun getWeeklyActivity(dailyStats: List<com.umbral.data.local.dao.DailyStats>): List<Boolean> {
        if (dailyStats.isEmpty()) {
            return List(7) { false }
        }

        val statsMap = dailyStats.associate { stat ->
            LocalDate.parse(stat.day) to stat.minutes
        }

        // Obtener el lunes de esta semana
        val today = LocalDate.now()
        val monday = today.with(DayOfWeek.MONDAY)

        // Crear lista de actividad para cada día de la semana
        return (0..6).map { dayOffset ->
            val date = monday.plusDays(dayOffset.toLong())
            val minutes = statsMap[date] ?: 0
            minutes > 0
        }
    }
}
