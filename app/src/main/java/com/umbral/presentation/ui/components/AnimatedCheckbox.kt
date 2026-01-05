package com.umbral.presentation.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.umbral.presentation.ui.theme.UmbralSpacing
import com.umbral.presentation.ui.theme.UmbralTheme

/**
 * Animated checkbox with a smooth check mark drawing animation.
 *
 * @param checked Whether the checkbox is checked
 * @param onCheckedChange Callback when check state changes
 * @param modifier Modifier for customization
 * @param enabled Whether the checkbox is enabled
 * @param size Size of the checkbox
 * @param checkedColor Background color when checked
 * @param uncheckedColor Border/background color when unchecked
 * @param checkmarkColor Color of the check mark
 */
@Composable
fun AnimatedCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: Dp = 24.dp,
    checkedColor: Color = MaterialTheme.colorScheme.primary,
    uncheckedColor: Color = MaterialTheme.colorScheme.outline,
    checkmarkColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    val interactionSource = remember { MutableInteractionSource() }

    // Animate the check progress
    val checkProgress by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 300f
        ),
        label = "checkProgress"
    )

    // Animate scale for bounce effect
    val scale by animateFloatAsState(
        targetValue = if (checked) 1f else 0.95f,
        animationSpec = spring(
            dampingRatio = 0.5f,
            stiffness = 400f
        ),
        label = "checkScale"
    )

    // Animate background color
    val backgroundColor by animateColorAsState(
        targetValue = if (checked) {
            if (enabled) checkedColor else checkedColor.copy(alpha = 0.38f)
        } else {
            Color.Transparent
        },
        animationSpec = tween(150),
        label = "backgroundColor"
    )

    // Animate border color
    val borderColor by animateColorAsState(
        targetValue = if (checked) {
            Color.Transparent
        } else {
            if (enabled) uncheckedColor else uncheckedColor.copy(alpha = 0.38f)
        },
        animationSpec = tween(150),
        label = "borderColor"
    )

    Box(
        modifier = modifier
            .size(size)
            .scale(scale)
            .clip(RoundedCornerShape(4.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Checkbox,
                onClick = { onCheckedChange(!checked) }
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.size(size)
        ) {
            val strokeWidth = 2.dp.toPx()
            val cornerRadius = CornerRadius(4.dp.toPx())

            // Draw background/border
            if (checked) {
                // Filled background when checked
                drawRoundRect(
                    color = backgroundColor,
                    size = this.size,
                    cornerRadius = cornerRadius
                )
            } else {
                // Border when unchecked
                drawRoundRect(
                    color = borderColor,
                    size = Size(
                        this.size.width - strokeWidth,
                        this.size.height - strokeWidth
                    ),
                    topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                    cornerRadius = cornerRadius,
                    style = Stroke(width = strokeWidth)
                )
            }

            // Draw checkmark with progress animation
            if (checkProgress > 0f) {
                val checkPath = Path().apply {
                    // Start point (left)
                    moveTo(this@Canvas.size.width * 0.22f, this@Canvas.size.height * 0.52f)
                    // Middle point (bottom)
                    lineTo(this@Canvas.size.width * 0.42f, this@Canvas.size.height * 0.72f)
                    // End point (top right)
                    lineTo(this@Canvas.size.width * 0.78f, this@Canvas.size.height * 0.32f)
                }

                val pathMeasure = PathMeasure()
                pathMeasure.setPath(checkPath, false)
                val pathLength = pathMeasure.length

                val animatedPath = Path()
                pathMeasure.getSegment(
                    startDistance = 0f,
                    stopDistance = pathLength * checkProgress,
                    destination = animatedPath,
                    startWithMoveTo = true
                )

                drawPath(
                    path = animatedPath,
                    color = if (enabled) checkmarkColor else checkmarkColor.copy(alpha = 0.7f),
                    style = Stroke(
                        width = 2.5.dp.toPx(),
                        cap = androidx.compose.ui.graphics.StrokeCap.Round,
                        join = androidx.compose.ui.graphics.StrokeJoin.Round
                    )
                )
            }
        }
    }
}

/**
 * Checkbox with label
 */
@Composable
fun LabeledCheckbox(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    description: String? = null
) {
    Row(
        modifier = modifier
            .clickable(
                enabled = enabled,
                onClick = { onCheckedChange(!checked) },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedCheckbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )

        Spacer(modifier = Modifier.width(UmbralSpacing.md))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = if (enabled) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                }
            )
            description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (enabled) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                    }
                )
            }
        }
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Checkbox - Unchecked", showBackground = true)
@Composable
private fun AnimatedCheckboxUncheckedPreview() {
    UmbralTheme {
        AnimatedCheckbox(
            checked = false,
            onCheckedChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Checkbox - Checked", showBackground = true)
@Composable
private fun AnimatedCheckboxCheckedPreview() {
    UmbralTheme {
        AnimatedCheckbox(
            checked = true,
            onCheckedChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Checkbox - Disabled", showBackground = true)
@Composable
private fun AnimatedCheckboxDisabledPreview() {
    UmbralTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AnimatedCheckbox(
                checked = false,
                onCheckedChange = {},
                enabled = false
            )
            AnimatedCheckbox(
                checked = true,
                onCheckedChange = {},
                enabled = false
            )
        }
    }
}

@Preview(name = "Checkbox - Large", showBackground = true)
@Composable
private fun AnimatedCheckboxLargePreview() {
    UmbralTheme {
        AnimatedCheckbox(
            checked = true,
            onCheckedChange = {},
            size = 32.dp,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Labeled Checkbox", showBackground = true)
@Composable
private fun LabeledCheckboxPreview() {
    UmbralTheme {
        var checked by remember { mutableStateOf(true) }
        LabeledCheckbox(
            label = "Recordar contraseña",
            description = "Mantener la sesión iniciada",
            checked = checked,
            onCheckedChange = { checked = it },
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Multiple Checkboxes", showBackground = true)
@Composable
private fun MultipleCheckboxesPreview() {
    UmbralTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            var option1 by remember { mutableStateOf(true) }
            var option2 by remember { mutableStateOf(false) }
            var option3 by remember { mutableStateOf(true) }

            LabeledCheckbox(
                label = "Opción 1",
                checked = option1,
                onCheckedChange = { option1 = it }
            )
            LabeledCheckbox(
                label = "Opción 2",
                checked = option2,
                onCheckedChange = { option2 = it }
            )
            LabeledCheckbox(
                label = "Opción 3 (deshabilitada)",
                checked = option3,
                onCheckedChange = { option3 = it },
                enabled = false
            )
        }
    }
}

@Preview(name = "Dark Theme Checkbox", showBackground = true)
@Composable
private fun AnimatedCheckboxDarkPreview() {
    UmbralTheme(darkTheme = true) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AnimatedCheckbox(
                checked = false,
                onCheckedChange = {}
            )
            AnimatedCheckbox(
                checked = true,
                onCheckedChange = {}
            )
        }
    }
}
