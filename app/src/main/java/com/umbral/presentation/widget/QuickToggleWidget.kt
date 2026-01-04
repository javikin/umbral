package com.umbral.presentation.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.umbral.R
import com.umbral.presentation.widget.action.ToggleBlockingAction
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first

/**
 * Widget de toggle rápido (2x2) con botón grande para activar/desactivar bloqueo.
 * Click: Toggle del estado de bloqueo.
 */
class QuickToggleWidget : GlanceAppWidget() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface QuickToggleWidgetEntryPoint {
        fun blockingManager(): com.umbral.domain.blocking.BlockingManager
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            QuickToggleWidgetEntryPoint::class.java
        )

        val blockingManager = entryPoint.blockingManager()
        val currentState = blockingManager.blockingState.first()

        provideContent {
            QuickToggleWidgetContent(
                isActive = currentState.isActive,
                profileName = currentState.activeProfileName
            )
        }
    }

    @Composable
    private fun QuickToggleWidgetContent(
        isActive: Boolean,
        profileName: String?
    ) {
        GlanceTheme {
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(GlanceTheme.colors.surface)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Título
                    Text(
                        text = "Bloqueo",
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurface,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )

                    Spacer(modifier = GlanceModifier.height(12.dp))

                    // Botón de toggle grande
                    Box(
                        modifier = GlanceModifier
                            .size(80.dp)
                            .background(
                                if (isActive) Color(0xFFE53935) // Red
                                else Color(0xFF4CAF50) // Green
                            )
                            .clickable(ToggleBlockingAction.action())
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                provider = ImageProvider(
                                    if (isActive) R.drawable.ic_lock
                                    else R.drawable.ic_lock_open
                                ),
                                contentDescription = if (isActive) "Desactivar" else "Activar",
                                modifier = GlanceModifier.size(36.dp)
                            )
                        }
                    }

                    Spacer(modifier = GlanceModifier.height(8.dp))

                    // Estado actual
                    Text(
                        text = if (isActive) "ON" else "OFF",
                        style = TextStyle(
                            color = ColorProvider(if (isActive) Color(0xFFE53935) else Color(0xFF4CAF50)),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    // Nombre del perfil si está activo
                    if (isActive && profileName != null) {
                        Spacer(modifier = GlanceModifier.height(4.dp))
                        Text(
                            text = profileName,
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
