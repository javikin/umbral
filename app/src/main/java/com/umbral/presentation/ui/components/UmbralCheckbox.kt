package com.umbral.presentation.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umbral.presentation.ui.theme.UmbralSpacing
import com.umbral.presentation.ui.theme.UmbralTheme

/**
 * Umbral Design System Checkbox
 *
 * A customizable checkbox component with smooth animations and optional label support.
 *
 * Visual specifications:
 * - Size: 24x24dp
 * - Corner radius: 6dp
 * - Border width: 2dp
 * - Checkmark: white, 2dp stroke
 *
 * Animations:
 * - Check animation: path drawing with 200ms tween
 * - Scale bounce on check: spring animation (dampingRatio=0.5)
 *
 * @param checked Current checked state
 * @param onCheckedChange Callback when state changes
 * @param modifier Modifier for customization
 * @param enabled Whether the checkbox is enabled
 * @param label Optional text label displayed to the right of checkbox
 */
@Composable
fun UmbralCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Scale animation on press
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 500f
        ),
        label = "checkboxPressScale"
    )

    // Scale bounce animation on check
    val checkScale by animateFloatAsState(
        targetValue = if (checked) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = 0.5f,
            stiffness = 400f
        ),
        label = "checkboxCheckScale"
    )

    // Color animation
    val backgroundColor by animateColorAsState(
        targetValue = when {
            !enabled && checked -> MaterialTheme.colorScheme.primary.copy(alpha = 0.38f)
            !enabled -> Color.Transparent
            checked -> MaterialTheme.colorScheme.primary
            else -> Color.Transparent
        },
        animationSpec = tween(durationMillis = 150),
        label = "checkboxBackgroundColor"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            !enabled -> MaterialTheme.colorScheme.outline.copy(alpha = 0.38f)
            checked -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.outline
        },
        animationSpec = tween(durationMillis = 150),
        label = "checkboxBorderColor"
    )

    // Checkmark path animation (0f to 1f)
    val checkProgress by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "checkmarkProgress"
    )

    Row(
        modifier = modifier
            .clickable(
                enabled = enabled,
                onClick = { onCheckedChange(!checked) },
                role = Role.Checkbox,
                interactionSource = interactionSource,
                indication = null
            ),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Checkbox canvas
        Canvas(
            modifier = Modifier
                .size(24.dp)
                .scale(scale * checkScale)
        ) {
            val canvasSize = size.minDimension
            val cornerRadius = 6.dp.toPx()
            val strokeWidth = 2.dp.toPx()

            // Draw checkbox background
            drawRoundRect(
                color = backgroundColor,
                size = Size(canvasSize, canvasSize),
                cornerRadius = CornerRadius(cornerRadius)
            )

            // Draw checkbox border
            drawRoundRect(
                color = borderColor,
                size = Size(canvasSize, canvasSize),
                cornerRadius = CornerRadius(cornerRadius),
                style = Stroke(width = strokeWidth)
            )

            // Draw checkmark if checked
            if (checkProgress > 0f) {
                val checkmarkColor = if (enabled) Color.White else Color.White.copy(alpha = 0.6f)
                val checkmarkStrokeWidth = 2.dp.toPx()

                // Checkmark path (approximate coordinates for 24x24 box)
                val checkmarkPath = Path().apply {
                    // Start point (left side of checkmark)
                    moveTo(canvasSize * 0.25f, canvasSize * 0.5f)
                    // Middle point (bottom of checkmark)
                    lineTo(canvasSize * 0.42f, canvasSize * 0.68f)
                    // End point (top right of checkmark)
                    lineTo(canvasSize * 0.75f, canvasSize * 0.32f)
                }

                // Draw only the portion of the path based on animation progress
                val pathMeasure = androidx.compose.ui.graphics.PathMeasure().apply {
                    setPath(checkmarkPath, false)
                }
                val animatedPath = Path()
                pathMeasure.getSegment(
                    startDistance = 0f,
                    stopDistance = pathMeasure.length * checkProgress,
                    destination = animatedPath,
                    startWithMoveTo = true
                )

                drawPath(
                    path = animatedPath,
                    color = checkmarkColor,
                    style = Stroke(
                        width = checkmarkStrokeWidth,
                        cap = StrokeCap.Round
                    )
                )
            }
        }

        // Optional label
        label?.let { text ->
            Spacer(modifier = Modifier.width(UmbralSpacing.iconTextSpacing))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = if (enabled) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                }
            )
        }
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Unchecked", showBackground = true)
@Composable
private fun UmbralCheckboxUncheckedPreview() {
    UmbralTheme {
        UmbralCheckbox(
            checked = false,
            onCheckedChange = {}
        )
    }
}

@Preview(name = "Checked", showBackground = true)
@Composable
private fun UmbralCheckboxCheckedPreview() {
    UmbralTheme {
        UmbralCheckbox(
            checked = true,
            onCheckedChange = {}
        )
    }
}

@Preview(name = "With Label Unchecked", showBackground = true)
@Composable
private fun UmbralCheckboxWithLabelUncheckedPreview() {
    UmbralTheme {
        UmbralCheckbox(
            checked = false,
            onCheckedChange = {},
            label = "Recordar mi preferencia"
        )
    }
}

@Preview(name = "With Label Checked", showBackground = true)
@Composable
private fun UmbralCheckboxWithLabelCheckedPreview() {
    UmbralTheme {
        UmbralCheckbox(
            checked = true,
            onCheckedChange = {},
            label = "Activar notificaciones"
        )
    }
}

@Preview(name = "Disabled Unchecked", showBackground = true)
@Composable
private fun UmbralCheckboxDisabledUncheckedPreview() {
    UmbralTheme {
        UmbralCheckbox(
            checked = false,
            onCheckedChange = {},
            enabled = false,
            label = "Opción deshabilitada"
        )
    }
}

@Preview(name = "Disabled Checked", showBackground = true)
@Composable
private fun UmbralCheckboxDisabledCheckedPreview() {
    UmbralTheme {
        UmbralCheckbox(
            checked = true,
            onCheckedChange = {},
            enabled = false,
            label = "Opción deshabilitada"
        )
    }
}

@Preview(name = "Dark Theme Unchecked", showBackground = true)
@Composable
private fun UmbralCheckboxDarkUncheckedPreview() {
    UmbralTheme(darkTheme = true) {
        UmbralCheckbox(
            checked = false,
            onCheckedChange = {},
            label = "Modo oscuro"
        )
    }
}

@Preview(name = "Dark Theme Checked", showBackground = true)
@Composable
private fun UmbralCheckboxDarkCheckedPreview() {
    UmbralTheme(darkTheme = true) {
        UmbralCheckbox(
            checked = true,
            onCheckedChange = {},
            label = "Modo oscuro"
        )
    }
}
