package com.umbral.presentation.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umbral.presentation.ui.theme.UmbralSpacing
import com.umbral.presentation.ui.theme.UmbralTheme

/**
 * Umbral Design System - Text/Ghost Button
 *
 * A transparent button with text-only styling for secondary actions.
 * Follows the "Filled + Ghost" button philosophy.
 *
 * Visual specs:
 * - Background: transparent (subtle background on press)
 * - Text color: accentPrimary (or error if destructive)
 * - Corner radius: 8dp
 * - Padding: 16dp horizontal, 12dp vertical
 * - Height: 48dp (matches Medium button)
 * - Border: None
 *
 * States:
 * - Default: Text in accent color, no background
 * - Pressed: Background accentPrimary at 10% opacity
 * - Disabled: 40% opacity
 * - Destructive: Text in error color
 *
 * @param text Button label text
 * @param onClick Click callback
 * @param modifier Modifier for customization
 * @param enabled Whether the button is enabled
 * @param icon Optional icon before text
 * @param destructive Use error color for destructive actions (delete, cancel)
 */
@Composable
fun UmbralTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    destructive: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Scale animation on press
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 500f
        ),
        label = "textButtonScale"
    )

    // Text color based on destructive flag
    val textColor = when {
        !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        destructive -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.primary
    }

    // Background color on press
    val backgroundColor = when {
        !enabled -> Color.Transparent
        isPressed && destructive -> MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
        isPressed -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        else -> Color.Transparent
    }

    TextButton(
        onClick = onClick,
        modifier = modifier
            .scale(scale)
            .height(UmbralSpacing.minTouchTarget)
            .background(
                color = backgroundColor,
                shape = MaterialTheme.shapes.small
            ),
        enabled = enabled,
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.textButtonColors(
            contentColor = textColor,
            disabledContentColor = textColor
        ),
        interactionSource = interactionSource,
        contentPadding = PaddingValues(
            horizontal = UmbralSpacing.md,
            vertical = UmbralSpacing.sm
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let { iconVector ->
                AnimatedIcon(
                    icon = iconVector,
                    modifier = Modifier.size(UmbralSpacing.iconSizeMedium),
                    tint = textColor
                )
                Spacer(modifier = Modifier.width(UmbralSpacing.iconTextSpacing))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Default Text Button", showBackground = true)
@Composable
private fun UmbralTextButtonDefaultPreview() {
    UmbralTheme {
        UmbralTextButton(
            text = "Cancelar",
            onClick = {}
        )
    }
}

@Preview(name = "Destructive Text Button", showBackground = true)
@Composable
private fun UmbralTextButtonDestructivePreview() {
    UmbralTheme {
        UmbralTextButton(
            text = "Eliminar",
            onClick = {},
            destructive = true
        )
    }
}

@Preview(name = "Text Button with Icon", showBackground = true)
@Composable
private fun UmbralTextButtonIconPreview() {
    UmbralTheme {
        UmbralTextButton(
            text = "Volver",
            onClick = {},
            icon = Icons.Default.ArrowBack
        )
    }
}

@Preview(name = "Destructive with Icon", showBackground = true)
@Composable
private fun UmbralTextButtonDestructiveIconPreview() {
    UmbralTheme {
        UmbralTextButton(
            text = "Eliminar perfil",
            onClick = {},
            icon = Icons.Default.Delete,
            destructive = true
        )
    }
}

@Preview(name = "Disabled Text Button", showBackground = true)
@Composable
private fun UmbralTextButtonDisabledPreview() {
    UmbralTheme {
        UmbralTextButton(
            text = "No disponible",
            onClick = {},
            enabled = false
        )
    }
}

@Preview(name = "Dark Theme Text Button", showBackground = true)
@Composable
private fun UmbralTextButtonDarkPreview() {
    UmbralTheme(darkTheme = true) {
        UmbralTextButton(
            text = "Cancelar",
            onClick = {}
        )
    }
}

@Preview(name = "Dark Theme Destructive", showBackground = true)
@Composable
private fun UmbralTextButtonDarkDestructivePreview() {
    UmbralTheme(darkTheme = true) {
        UmbralTextButton(
            text = "Eliminar",
            onClick = {},
            destructive = true
        )
    }
}
