package com.umbral.presentation.ui.components.feedback

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import com.umbral.presentation.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Umbral Design System 2.0 - Toast Component
 *
 * Subtle, non-intrusive feedback for quick informational messages.
 *
 * ## Usage
 * ```kotlin
 * val toastState = rememberToastState()
 *
 * UmbralToastHost(toastState = toastState)
 *
 * Button(onClick = { toastState.show("Cambios guardados") }) {
 *     Text("Guardar")
 * }
 * ```
 *
 * ## Features
 * - Auto-dismiss after 2 seconds
 * - Compact, pill-shaped design
 * - Optional icon support
 * - Positioned at top-center
 * - Smooth fade + scale animations
 *
 * ## Design Specs
 * - Background: DarkBackgroundElevated / LightBackgroundElevated (90% opacity)
 * - Corner Radius: Full (pill shape)
 * - Padding: 12.dp horizontal, 8.dp vertical
 * - Height: auto (~36-40.dp)
 * - Text Style: labelMedium
 * - Animation: fadeIn/Out + scaleIn/Out (200ms)
 */

/**
 * Toast state manager - controls visibility and message content
 */
class ToastState {
    var message by mutableStateOf("")
        private set
    var icon by mutableStateOf<ImageVector?>(null)
        private set
    var isVisible by mutableStateOf(false)
        private set

    /**
     * Show a toast message
     *
     * @param message Text to display
     * @param icon Optional icon to show before the message
     */
    suspend fun show(message: String, icon: ImageVector? = null) {
        this.message = message
        this.icon = icon
        this.isVisible = true

        // Auto-dismiss after 2 seconds
        delay(2000)
        hide()
    }

    /**
     * Hide the toast immediately
     */
    fun hide() {
        isVisible = false
    }
}

/**
 * Remember a toast state across recompositions
 */
@Composable
fun rememberToastState(): ToastState {
    return remember { ToastState() }
}

/**
 * Toast host - manages toast display lifecycle
 *
 * Place this at the root of your screen to enable toast messages.
 *
 * @param toastState State controlling toast visibility and content
 * @param modifier Optional modifier
 */
@Composable
fun UmbralToastHost(
    toastState: ToastState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        AnimatedVisibility(
            visible = toastState.isVisible,
            enter = fadeIn(
                animationSpec = tween(
                    durationMillis = 200,
                    easing = UmbralMotion.easeOut
                )
            ) + scaleIn(
                initialScale = 0.8f,
                animationSpec = tween(
                    durationMillis = 200,
                    easing = UmbralMotion.easeOut
                )
            ),
            exit = fadeOut(
                animationSpec = tween(
                    durationMillis = 200,
                    easing = UmbralMotion.easeIn
                )
            ) + scaleOut(
                targetScale = 0.8f,
                animationSpec = tween(
                    durationMillis = 200,
                    easing = UmbralMotion.easeIn
                )
            )
        ) {
            UmbralToast(
                message = toastState.message,
                icon = toastState.icon,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

/**
 * Toast component - compact, informational feedback
 *
 * @param message Text to display
 * @param icon Optional icon to show before the message
 * @param modifier Optional modifier
 */
@Composable
fun UmbralToast(
    message: String,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    val isDark = !MaterialTheme.colorScheme.surface.equals(LightBackgroundSurface)

    val backgroundColor = if (isDark) {
        DarkBackgroundElevated.copy(alpha = 0.9f)
    } else {
        LightBackgroundElevated.copy(alpha = 0.9f)
    }

    val textColor = if (isDark) {
        DarkTextPrimary
    } else {
        LightTextPrimary
    }

    Row(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(50) // Full pill shape
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Optional icon
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        // Message text
        Text(
            text = message,
            style = MaterialTheme.typography.labelMedium,
            color = textColor
        )
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Toast - Simple (Light)", showBackground = true)
@Composable
private fun PreviewToastSimpleLight() {
    UmbralTheme(darkTheme = false) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(LightBackgroundBase)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            UmbralToast(message = "Cambios guardados")
        }
    }
}

@Preview(name = "Toast - Simple (Dark)", showBackground = true)
@Composable
private fun PreviewToastSimpleDark() {
    UmbralTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkBackgroundBase)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            UmbralToast(message = "Cambios guardados")
        }
    }
}

@Preview(name = "Toast - With Success Icon (Light)", showBackground = true)
@Composable
private fun PreviewToastSuccessLight() {
    UmbralTheme(darkTheme = false) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(LightBackgroundBase)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            UmbralToast(
                message = "Perfil activado",
                icon = Icons.Default.CheckCircle
            )
        }
    }
}

@Preview(name = "Toast - With Success Icon (Dark)", showBackground = true)
@Composable
private fun PreviewToastSuccessDark() {
    UmbralTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkBackgroundBase)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            UmbralToast(
                message = "Perfil activado",
                icon = Icons.Default.CheckCircle
            )
        }
    }
}

@Preview(name = "Toast - With Info Icon (Light)", showBackground = true)
@Composable
private fun PreviewToastInfoLight() {
    UmbralTheme(darkTheme = false) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(LightBackgroundBase)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            UmbralToast(
                message = "Toca el tag NFC para continuar",
                icon = Icons.Default.Info
            )
        }
    }
}

@Preview(name = "Toast - With Warning Icon (Dark)", showBackground = true)
@Composable
private fun PreviewToastWarningDark() {
    UmbralTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkBackgroundBase)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            UmbralToast(
                message = "Batería baja",
                icon = Icons.Default.Warning
            )
        }
    }
}

@Preview(name = "Toast - Long Message (Light)", showBackground = true)
@Composable
private fun PreviewToastLongLight() {
    UmbralTheme(darkTheme = false) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(LightBackgroundBase)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            UmbralToast(
                message = "Configuración actualizada correctamente",
                icon = Icons.Default.CheckCircle
            )
        }
    }
}

@Preview(name = "Toast Host - Interactive Demo", showBackground = true)
@Composable
private fun PreviewToastHost() {
    val toastState = rememberToastState()
    val scope = rememberCoroutineScope()

    UmbralTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackgroundBase)
        ) {
            // Toast host
            UmbralToastHost(toastState = toastState)

            // Demo buttons
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                androidx.compose.material3.Button(
                    onClick = {
                        scope.launch {
                            toastState.show("Cambios guardados")
                        }
                    }
                ) {
                    Text("Mostrar Toast Simple")
                }

                Spacer(modifier = Modifier.height(16.dp))

                androidx.compose.material3.Button(
                    onClick = {
                        scope.launch {
                            toastState.show(
                                message = "Perfil activado",
                                icon = Icons.Default.CheckCircle
                            )
                        }
                    }
                ) {
                    Text("Mostrar Toast con Ícono")
                }
            }
        }
    }
}
