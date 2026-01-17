package com.umbral.presentation.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umbral.presentation.ui.theme.UmbralTheme

/**
 * Umbral Design System Toggle Switch
 *
 * A custom toggle switch with smooth slide animation and bounce effect.
 *
 * @param checked Whether the toggle is on
 * @param onCheckedChange Callback when toggle state changes
 * @param modifier Modifier for customization
 * @param enabled Whether the toggle is enabled
 *
 * @deprecated Use UmbralSwitch instead. This component uses Design System 1.0 and will be removed.
 * Migration: Replace UmbralToggle with UmbralSwitch - the API is identical.
 */
@Deprecated(
    message = "Use UmbralSwitch instead. This component uses Design System 1.0.",
    replaceWith = ReplaceWith("UmbralSwitch(checked, onCheckedChange, modifier, enabled)", "com.umbral.presentation.ui.components.UmbralSwitch"),
    level = DeprecationLevel.WARNING
)
@Composable
fun UmbralToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }

    // Track dimensions
    val trackWidth = 52.dp
    val trackHeight = 32.dp
    val thumbSize = 28.dp
    val thumbPadding = 2.dp
    val thumbTravel = trackWidth - thumbSize - (thumbPadding * 2)

    // Animated thumb position with bounce
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) thumbTravel else 0.dp,
        animationSpec = spring(
            dampingRatio = 0.5f,
            stiffness = 400f
        ),
        label = "thumbOffset"
    )

    // Animated colors
    val trackColor by animateColorAsState(
        targetValue = when {
            !enabled -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f)
            checked -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.surfaceVariant
        },
        label = "trackColor"
    )

    val thumbColor by animateColorAsState(
        targetValue = when {
            !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            checked -> MaterialTheme.colorScheme.onPrimary
            else -> MaterialTheme.colorScheme.outline
        },
        label = "thumbColor"
    )

    Box(
        modifier = modifier
            .size(width = trackWidth, height = trackHeight)
            .clip(CircleShape)
            .background(trackColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Switch,
                onClick = { onCheckedChange(!checked) }
            )
            .padding(thumbPadding),
        contentAlignment = Alignment.CenterStart
    ) {
        // Thumb
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(thumbSize)
                .clip(CircleShape)
                .background(thumbColor)
        )
    }
}

/**
 * Toggle with label
 *
 * @deprecated Use UmbralSwitch(label = "...", ...) instead. This component uses Design System 1.0.
 * Migration: Replace UmbralLabeledToggle with UmbralSwitch and pass the label parameter.
 */
@Deprecated(
    message = "Use UmbralSwitch with label parameter instead. This component uses Design System 1.0.",
    replaceWith = ReplaceWith("UmbralSwitch(checked, onCheckedChange, modifier, enabled, label)", "com.umbral.presentation.ui.components.UmbralSwitch"),
    level = DeprecationLevel.WARNING
)
@Composable
fun UmbralLabeledToggle(
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
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.weight(1f)
            ) {
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
        UmbralToggle(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Toggle - Off", showBackground = true)
@Composable
private fun UmbralToggleOffPreview() {
    UmbralTheme {
        UmbralToggle(
            checked = false,
            onCheckedChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Toggle - On", showBackground = true)
@Composable
private fun UmbralToggleOnPreview() {
    UmbralTheme {
        UmbralToggle(
            checked = true,
            onCheckedChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Toggle - Disabled Off", showBackground = true)
@Composable
private fun UmbralToggleDisabledOffPreview() {
    UmbralTheme {
        UmbralToggle(
            checked = false,
            onCheckedChange = {},
            enabled = false,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Toggle - Disabled On", showBackground = true)
@Composable
private fun UmbralToggleDisabledOnPreview() {
    UmbralTheme {
        UmbralToggle(
            checked = true,
            onCheckedChange = {},
            enabled = false,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Labeled Toggle", showBackground = true)
@Composable
private fun UmbralLabeledTogglePreview() {
    UmbralTheme {
        UmbralLabeledToggle(
            label = "Modo oscuro",
            description = "Activa el tema oscuro automáticamente",
            checked = true,
            onCheckedChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Dark Theme Toggle", showBackground = true)
@Composable
private fun UmbralToggleDarkPreview() {
    UmbralTheme(darkTheme = true) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            UmbralToggle(
                checked = false,
                onCheckedChange = {}
            )
            UmbralToggle(
                checked = true,
                onCheckedChange = {}
            )
        }
    }
}
