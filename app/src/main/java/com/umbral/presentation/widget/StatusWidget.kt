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
import com.umbral.R
import com.umbral.presentation.widget.action.OpenAppAction
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first

/**
 * Widget de estado pequeño (2x1) que muestra si el bloqueo está activo o inactivo.
 * Click: Abre la app.
 */
class StatusWidget : GlanceAppWidget() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface StatusWidgetEntryPoint {
        fun blockingManager(): com.umbral.domain.blocking.BlockingManager
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Obtener estado actual
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            StatusWidgetEntryPoint::class.java
        )

        val blockingManager = entryPoint.blockingManager()
        val currentState = blockingManager.blockingState.first()

        provideContent {
            StatusWidgetContent(
                isActive = currentState.isActive,
                profileName = currentState.activeProfileName
            )
        }
    }

    @Composable
    private fun StatusWidgetContent(
        isActive: Boolean,
        profileName: String?
    ) {
        GlanceTheme {
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(
                        if (isActive) Color(0xFFE53935) // BlockingActive red
                        else Color(0xFF4CAF50) // BlockingInactive green
                    )
                    .clickable(OpenAppAction.action())
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icono
                    Image(
                        provider = ImageProvider(
                            if (isActive) R.drawable.ic_lock
                            else R.drawable.ic_lock_open
                        ),
                        contentDescription = if (isActive) "Bloqueado" else "Desbloqueado",
                        modifier = GlanceModifier.size(28.dp)
                    )

                    Spacer(modifier = GlanceModifier.height(4.dp))

                    // Texto de estado
                    Text(
                        text = if (isActive) "Activo" else "Inactivo",
                        style = TextStyle(
                            color = GlanceTheme.colors.onPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    // Nombre del perfil si está activo
                    if (isActive && profileName != null) {
                        Text(
                            text = profileName,
                            style = TextStyle(
                                color = GlanceTheme.colors.onPrimary,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
