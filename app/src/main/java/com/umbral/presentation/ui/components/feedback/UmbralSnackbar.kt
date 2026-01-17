package com.umbral.presentation.ui.components.feedback

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umbral.presentation.ui.theme.DarkBackgroundElevated
import com.umbral.presentation.ui.theme.DarkBorderDefault
import com.umbral.presentation.ui.theme.DarkError
import com.umbral.presentation.ui.theme.DarkSuccess
import com.umbral.presentation.ui.theme.DarkTextPrimary
import com.umbral.presentation.ui.theme.DarkWarning
import com.umbral.presentation.ui.theme.LightBackgroundElevated
import com.umbral.presentation.ui.theme.LightBorderDefault
import com.umbral.presentation.ui.theme.LightError
import com.umbral.presentation.ui.theme.LightSuccess
import com.umbral.presentation.ui.theme.LightTextPrimary
import com.umbral.presentation.ui.theme.LightWarning
import com.umbral.presentation.ui.theme.UmbralMotion
import com.umbral.presentation.ui.theme.UmbralTheme
import kotlinx.coroutines.delay

/**
 * Snackbar variants for UmbralSnackbar
 */
enum class SnackbarVariant {
    Default,  // No icon, surface border
    Success,  // Check icon, green border
    Error,    // X icon, red border
    Warning   // Warning icon, amber border
}

/**
 * Snackbar duration values
 */
enum class SnackbarDuration(val milliseconds: Long) {
    Short(3000),      // 3 seconds
    Medium(5000),     // 5 seconds
    Long(8000),       // 8 seconds
    Indefinite(-1)    // No auto-dismiss
}

/**
 * Snackbar action configuration
 *
 * @param label Action button text
 * @param onClick Action button callback
 */
data class SnackbarAction(
    val label: String,
    val onClick: () -> Unit
)

/**
 * Umbral Design System Snackbar
 *
 * A lightweight notification component that appears at the bottom of the screen
 * with support for different variants, actions, and auto-dismiss durations.
 *
 * Visual Specs:
 * - Background: DarkBackgroundElevated / LightBackgroundElevated
 * - Border: 1px semantic color based on variant
 * - Corner Radius: 12.dp
 * - Padding: 16.dp
 * - Icon Size: 20.dp
 * - Max Width: 400.dp
 * - Position: Bottom, 16.dp margin
 *
 * @param message Snackbar message text
 * @param modifier Modifier for customization
 * @param variant Visual style variant (Default, Success, Error, Warning)
 * @param action Optional action button configuration
 * @param duration Auto-dismiss duration (Short, Medium, Long, Indefinite)
 */
@Composable
fun UmbralSnackbar(
    message: String,
    modifier: Modifier = Modifier,
    variant: SnackbarVariant = SnackbarVariant.Default,
    action: SnackbarAction? = null,
    duration: SnackbarDuration = SnackbarDuration.Medium
) {
    val isDark = MaterialTheme.colorScheme.background == Color(0xFF151515) ||
                 MaterialTheme.colorScheme.background == DarkBackgroundElevated

    // Colors based on variant and theme
    val (borderColor, icon) = when (variant) {
        SnackbarVariant.Default -> {
            val border = if (isDark) DarkBorderDefault else LightBorderDefault
            Pair(border, null)
        }
        SnackbarVariant.Success -> {
            val border = if (isDark) DarkSuccess else LightSuccess
            Pair(border, Icons.Default.Check)
        }
        SnackbarVariant.Error -> {
            val border = if (isDark) DarkError else LightError
            Pair(border, Icons.Default.Close)
        }
        SnackbarVariant.Warning -> {
            val border = if (isDark) DarkWarning else LightWarning
            Pair(border, Icons.Default.Warning)
        }
    }

    val backgroundColor = if (isDark) DarkBackgroundElevated else LightBackgroundElevated
    val textColor = if (isDark) DarkTextPrimary else LightTextPrimary

    Card(
        modifier = modifier
            .widthIn(max = 400.dp)
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .background(backgroundColor)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon for semantic variants
                icon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = borderColor
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }

                // Message text
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )
            }

            // Action button
            action?.let { snackbarAction ->
                Spacer(modifier = Modifier.width(16.dp))
                TextButton(
                    onClick = snackbarAction.onClick,
                    modifier = Modifier
                ) {
                    Text(
                        text = snackbarAction.label,
                        style = MaterialTheme.typography.labelLarge,
                        color = borderColor
                    )
                }
            }
        }
    }
}

/**
 * Snackbar data for queue management
 */
data class SnackbarData(
    val id: Long = System.currentTimeMillis(),
    val message: String,
    val variant: SnackbarVariant = SnackbarVariant.Default,
    val action: SnackbarAction? = null,
    val duration: SnackbarDuration = SnackbarDuration.Medium
)

/**
 * Umbral Snackbar Host
 *
 * Manages a queue of snackbars with automatic dismissal and animations.
 * Place this at the bottom of your screen layout to show snackbars.
 *
 * Usage:
 * ```kotlin
 * var snackbarQueue by remember { mutableStateOf<List<SnackbarData>>(emptyList()) }
 *
 * Box(modifier = Modifier.fillMaxSize()) {
 *     // Your content
 *     UmbralSnackbarHost(
 *         snackbarQueue = snackbarQueue,
 *         onDismiss = { id ->
 *             snackbarQueue = snackbarQueue.filter { it.id != id }
 *         }
 *     )
 * }
 *
 * // To show a snackbar:
 * snackbarQueue = snackbarQueue + SnackbarData(
 *     message = "Perfil guardado",
 *     variant = SnackbarVariant.Success
 * )
 * ```
 *
 * @param snackbarQueue List of snackbars to display
 * @param onDismiss Callback when a snackbar is dismissed
 * @param modifier Modifier for customization
 */
@Composable
fun UmbralSnackbarHost(
    snackbarQueue: List<SnackbarData>,
    onDismiss: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Only show the most recent snackbar
        snackbarQueue.lastOrNull()?.let { snackbar ->
            var isVisible by remember(snackbar.id) { mutableStateOf(false) }

            // Auto-dismiss if duration is not indefinite
            LaunchedEffect(snackbar.id) {
                isVisible = true
                if (snackbar.duration != SnackbarDuration.Indefinite) {
                    delay(snackbar.duration.milliseconds)
                    isVisible = false
                    delay(200) // Wait for exit animation
                    onDismiss(snackbar.id)
                }
            }

            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(
                        durationMillis = UmbralMotion.normal,
                        easing = UmbralMotion.easeOut
                    )
                ) + fadeIn(
                    animationSpec = tween(
                        durationMillis = UmbralMotion.normal,
                        easing = UmbralMotion.easeOut
                    )
                ),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(
                        durationMillis = 200,
                        easing = UmbralMotion.easeIn
                    )
                ) + fadeOut(
                    animationSpec = tween(
                        durationMillis = 200,
                        easing = UmbralMotion.easeIn
                    )
                )
            ) {
                UmbralSnackbar(
                    message = snackbar.message,
                    modifier = Modifier.padding(bottom = 16.dp),
                    variant = snackbar.variant,
                    action = snackbar.action,
                    duration = snackbar.duration
                )
            }
        }
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Default Snackbar", showBackground = true)
@Composable
private fun UmbralSnackbarDefaultPreview() {
    UmbralTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                UmbralSnackbar(
                    message = "Cambios guardados correctamente",
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    }
}

@Preview(name = "Success Snackbar", showBackground = true)
@Composable
private fun UmbralSnackbarSuccessPreview() {
    UmbralTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                UmbralSnackbar(
                    message = "Perfil creado exitosamente",
                    variant = SnackbarVariant.Success,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    }
}

@Preview(name = "Error Snackbar", showBackground = true)
@Composable
private fun UmbralSnackbarErrorPreview() {
    UmbralTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                UmbralSnackbar(
                    message = "Error al guardar los cambios",
                    variant = SnackbarVariant.Error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    }
}

@Preview(name = "Warning Snackbar", showBackground = true)
@Composable
private fun UmbralSnackbarWarningPreview() {
    UmbralTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                UmbralSnackbar(
                    message = "La batería está baja, conecta tu dispositivo",
                    variant = SnackbarVariant.Warning,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    }
}

@Preview(name = "Snackbar with Action", showBackground = true)
@Composable
private fun UmbralSnackbarWithActionPreview() {
    UmbralTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                UmbralSnackbar(
                    message = "Perfil eliminado",
                    variant = SnackbarVariant.Default,
                    action = SnackbarAction(
                        label = "DESHACER",
                        onClick = {}
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    }
}

@Preview(name = "Success with Action", showBackground = true)
@Composable
private fun UmbralSnackbarSuccessWithActionPreview() {
    UmbralTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                UmbralSnackbar(
                    message = "Elemento agregado a favoritos",
                    variant = SnackbarVariant.Success,
                    action = SnackbarAction(
                        label = "VER",
                        onClick = {}
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    }
}

@Preview(name = "Dark Theme - All Variants", showBackground = true)
@Composable
private fun UmbralSnackbarDarkThemePreview() {
    UmbralTheme(darkTheme = true) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Bottom)
            ) {
                UmbralSnackbar(
                    message = "Mensaje predeterminado",
                    variant = SnackbarVariant.Default
                )
                UmbralSnackbar(
                    message = "Operación exitosa",
                    variant = SnackbarVariant.Success
                )
                UmbralSnackbar(
                    message = "Ocurrió un error",
                    variant = SnackbarVariant.Error
                )
                UmbralSnackbar(
                    message = "Advertencia importante",
                    variant = SnackbarVariant.Warning
                )
            }
        }
    }
}

@Preview(name = "Snackbar Host Demo", showBackground = true)
@Composable
private fun UmbralSnackbarHostPreview() {
    var snackbarQueue by remember {
        mutableStateOf(
            listOf(
                SnackbarData(
                    message = "Cambios guardados correctamente",
                    variant = SnackbarVariant.Success,
                    duration = SnackbarDuration.Long
                )
            )
        )
    }

    UmbralTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize()) {
                UmbralSnackbarHost(
                    snackbarQueue = snackbarQueue,
                    onDismiss = { id ->
                        snackbarQueue = snackbarQueue.filter { it.id != id }
                    }
                )
            }
        }
    }
}

@Preview(name = "Long Message Snackbar", showBackground = true)
@Composable
private fun UmbralSnackbarLongMessagePreview() {
    UmbralTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                UmbralSnackbar(
                    message = "Este es un mensaje muy largo que demuestra cómo se comporta el snackbar con contenido extenso",
                    variant = SnackbarVariant.Warning,
                    action = SnackbarAction(
                        label = "ACCIÓN",
                        onClick = {}
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    }
}
