package com.umbral.presentation.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.umbral.presentation.ui.theme.DarkAccentPrimary
import com.umbral.presentation.ui.theme.DarkBorderDefault
import com.umbral.presentation.ui.theme.DarkTextSecondary
import com.umbral.presentation.ui.theme.LightAccentPrimary
import com.umbral.presentation.ui.theme.LightBorderDefault
import com.umbral.presentation.ui.theme.LightTextSecondary
import com.umbral.presentation.ui.theme.UmbralTheme
import com.umbral.presentation.ui.theme.isUmbralDarkTheme

/**
 * Umbral Design System 2.0 - Switch Component
 *
 * A modern toggle switch with smooth animations following Design System 2.0 specs.
 *
 * Visual Specs:
 * - Track: 52dp x 32dp
 * - Thumb: 28dp diameter
 * - Off state: borderDefault (12% opacity) track, textSecondary thumb
 * - On state: accentPrimary track, #151515 thumb
 *
 * Animations:
 * - Thumb position: spring (dampingRatio=0.6, stiffness=400)
 * - Track color: tween 200ms
 *
 * @param checked Whether the switch is on (true) or off (false)
 * @param onCheckedChange Callback when switch state changes
 * @param modifier Modifier for customization
 * @param enabled Whether the switch is enabled for user interaction
 * @param label Optional text label displayed next to the switch
 */
@Composable
fun UmbralSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String? = null
) {
    if (label != null) {
        // Switch with label
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
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = if (enabled) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            )
            UmbralSwitchCore(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled
            )
        }
    } else {
        // Switch without label
        UmbralSwitchCore(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = modifier,
            enabled = enabled
        )
    }
}

/**
 * Core switch component without label
 */
@Composable
private fun UmbralSwitchCore(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val isDark = isUmbralDarkTheme()
    val interactionSource = remember { MutableInteractionSource() }

    // Design System 2.0 specs
    val trackWidth = 52.dp
    val trackHeight = 32.dp
    val thumbSize = 28.dp
    val thumbPadding = 2.dp
    val thumbTravel = trackWidth - thumbSize - (thumbPadding * 2)

    // Animated thumb position with spring animation (dampingRatio=0.6, stiffness=400)
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) thumbTravel else 0.dp,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 400f
        ),
        label = "thumbOffset"
    )

    // Track color with tween 200ms animation
    val trackColor by animateColorAsState(
        targetValue = when {
            !enabled -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f)
            checked -> if (isDark) DarkAccentPrimary else LightAccentPrimary
            else -> {
                // Off state: borderDefault with 12% opacity
                val borderColor = if (isDark) DarkBorderDefault else LightBorderDefault
                borderColor.copy(alpha = 0.12f)
            }
        },
        animationSpec = tween(durationMillis = 200),
        label = "trackColor"
    )

    // Thumb color
    val thumbColor by animateColorAsState(
        targetValue = when {
            !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            checked -> Color(0xFF151515) // Dark color for thumb when on
            else -> if (isDark) DarkTextSecondary else LightTextSecondary
        },
        animationSpec = tween(durationMillis = 200),
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

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Switch - Off (Light)", showBackground = true)
@Composable
private fun UmbralSwitchOffLightPreview() {
    UmbralTheme(darkTheme = false) {
        UmbralSwitch(
            checked = false,
            onCheckedChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Switch - On (Light)", showBackground = true)
@Composable
private fun UmbralSwitchOnLightPreview() {
    UmbralTheme(darkTheme = false) {
        UmbralSwitch(
            checked = true,
            onCheckedChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Switch - Off (Dark)", showBackground = true)
@Composable
private fun UmbralSwitchOffDarkPreview() {
    UmbralTheme(darkTheme = true) {
        UmbralSwitch(
            checked = false,
            onCheckedChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Switch - On (Dark)", showBackground = true)
@Composable
private fun UmbralSwitchOnDarkPreview() {
    UmbralTheme(darkTheme = true) {
        UmbralSwitch(
            checked = true,
            onCheckedChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Switch - With Label", showBackground = true)
@Composable
private fun UmbralSwitchWithLabelPreview() {
    UmbralTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            UmbralSwitch(
                label = "Modo oscuro",
                checked = false,
                onCheckedChange = {}
            )
            UmbralSwitch(
                label = "Notificaciones",
                checked = true,
                onCheckedChange = {}
            )
        }
    }
}

@Preview(name = "Switch - Disabled Off", showBackground = true)
@Composable
private fun UmbralSwitchDisabledOffPreview() {
    UmbralTheme {
        UmbralSwitch(
            checked = false,
            onCheckedChange = {},
            enabled = false,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Switch - Disabled On", showBackground = true)
@Composable
private fun UmbralSwitchDisabledOnPreview() {
    UmbralTheme {
        UmbralSwitch(
            checked = true,
            onCheckedChange = {},
            enabled = false,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Switch - All States", showBackground = true)
@Composable
private fun UmbralSwitchAllStatesPreview() {
    UmbralTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Light Theme", style = MaterialTheme.typography.titleSmall)
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                UmbralSwitch(checked = false, onCheckedChange = {})
                UmbralSwitch(checked = true, onCheckedChange = {})
            }

            Text("With Labels", style = MaterialTheme.typography.titleSmall)
            UmbralSwitch(
                label = "Activado",
                checked = true,
                onCheckedChange = {}
            )
            UmbralSwitch(
                label = "Desactivado",
                checked = false,
                onCheckedChange = {}
            )

            Text("Disabled", style = MaterialTheme.typography.titleSmall)
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                UmbralSwitch(checked = false, onCheckedChange = {}, enabled = false)
                UmbralSwitch(checked = true, onCheckedChange = {}, enabled = false)
            }
        }
    }
}

@Preview(name = "Switch - Dark Theme All States", showBackground = true)
@Composable
private fun UmbralSwitchDarkAllStatesPreview() {
    UmbralTheme(darkTheme = true) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Dark Theme", style = MaterialTheme.typography.titleSmall)
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                UmbralSwitch(checked = false, onCheckedChange = {})
                UmbralSwitch(checked = true, onCheckedChange = {})
            }

            Text("With Labels", style = MaterialTheme.typography.titleSmall)
            UmbralSwitch(
                label = "Activado",
                checked = true,
                onCheckedChange = {}
            )
            UmbralSwitch(
                label = "Desactivado",
                checked = false,
                onCheckedChange = {}
            )

            Text("Disabled", style = MaterialTheme.typography.titleSmall)
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                UmbralSwitch(checked = false, onCheckedChange = {}, enabled = false)
                UmbralSwitch(checked = true, onCheckedChange = {}, enabled = false)
            }
        }
    }
}
