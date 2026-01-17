package com.umbral.presentation.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.umbral.presentation.ui.theme.DarkBackgroundBase
import com.umbral.presentation.ui.theme.UmbralSpacing
import com.umbral.presentation.ui.theme.UmbralTheme

/**
 * Size variants for UmbralIconButton
 */
enum class IconButtonSize(
    val containerSize: Dp,
    val iconSize: Dp
) {
    Small(containerSize = 32.dp, iconSize = 18.dp),
    Medium(containerSize = 40.dp, iconSize = 24.dp),
    Large(containerSize = 48.dp, iconSize = 28.dp)
}

/**
 * Visual style variants for UmbralIconButton
 */
enum class IconButtonVariant {
    Ghost,   // No background (default)
    Filled,  // Accent background
    Tonal    // Accent at 10% opacity background
}

/**
 * Umbral Design System Icon Button
 *
 * A circular icon button component with support for different sizes and visual styles.
 * Follows Design System 2.0 specifications with proper accessibility support.
 *
 * @param icon ImageVector icon to display
 * @param onClick Click callback
 * @param modifier Modifier for customization
 * @param contentDescription Accessibility description (required)
 * @param size Size variant (Small, Medium, Large)
 * @param variant Visual style variant (Ghost, Filled, Tonal)
 * @param enabled Whether the button is enabled
 */
@Composable
fun UmbralIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String,
    size: IconButtonSize = IconButtonSize.Medium,
    variant: IconButtonVariant = IconButtonVariant.Ghost,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()

    // Press animation - scale to 0.95 when pressed
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 500f
        ),
        label = "iconButtonScale"
    )

    // Calculate colors based on variant
    val backgroundColor = when {
        !enabled -> Color.Transparent
        else -> when (variant) {
            IconButtonVariant.Ghost -> Color.Transparent
            IconButtonVariant.Filled -> MaterialTheme.colorScheme.primary
            IconButtonVariant.Tonal -> if (isPressed) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.20f)
            } else {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
            }
        }
    }

    val iconColor = when {
        !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.40f)
        else -> when (variant) {
            IconButtonVariant.Ghost -> if (isPressed) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            }
            IconButtonVariant.Filled -> Color(0xFF151515) // Dark color for filled variant
            IconButtonVariant.Tonal -> MaterialTheme.colorScheme.primary
        }
    }

    // Calculate padding to ensure minimum 48dp touch target
    val minTouchTarget = UmbralSpacing.minTouchTarget
    val touchTargetPadding = ((minTouchTarget - size.containerSize) / 2).coerceAtLeast(0.dp)

    Box(
        modifier = modifier
            .padding(touchTargetPadding)
            .size(size.containerSize)
            .scale(scale)
            .clip(RoundedCornerShape(50)) // Circular shape
            .background(backgroundColor)
            .border(
                width = if (isFocused && enabled) 2.dp else 0.dp,
                color = if (isFocused && enabled) {
                    MaterialTheme.colorScheme.primary
                } else {
                    Color.Transparent
                },
                shape = RoundedCornerShape(50)
            )
            .clickable(
                enabled = enabled,
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null // We handle press state manually
            )
            .semantics {
                this.contentDescription = contentDescription
                this.role = Role.Button
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null, // Already set in parent semantics
            modifier = Modifier.size(size.iconSize),
            tint = iconColor
        )
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Ghost Variant - All Sizes", showBackground = true)
@Composable
private fun UmbralIconButtonGhostPreview() {
    UmbralTheme {
        Box(
            modifier = Modifier
                .background(DarkBackgroundBase)
                .padding(16.dp)
        ) {
            Row {
                UmbralIconButton(
                    icon = Icons.Default.Menu,
                    onClick = {},
                    contentDescription = "Menu",
                    size = IconButtonSize.Small,
                    variant = IconButtonVariant.Ghost
                )
                Spacer(modifier = Modifier.width(8.dp))
                UmbralIconButton(
                    icon = Icons.Default.Menu,
                    onClick = {},
                    contentDescription = "Menu",
                    size = IconButtonSize.Medium,
                    variant = IconButtonVariant.Ghost
                )
                Spacer(modifier = Modifier.width(8.dp))
                UmbralIconButton(
                    icon = Icons.Default.Menu,
                    onClick = {},
                    contentDescription = "Menu",
                    size = IconButtonSize.Large,
                    variant = IconButtonVariant.Ghost
                )
            }
        }
    }
}

@Preview(name = "Filled Variant - All Sizes", showBackground = true)
@Composable
private fun UmbralIconButtonFilledPreview() {
    UmbralTheme {
        Box(
            modifier = Modifier
                .background(DarkBackgroundBase)
                .padding(16.dp)
        ) {
            Row {
                UmbralIconButton(
                    icon = Icons.Default.Favorite,
                    onClick = {},
                    contentDescription = "Favorito",
                    size = IconButtonSize.Small,
                    variant = IconButtonVariant.Filled
                )
                Spacer(modifier = Modifier.width(8.dp))
                UmbralIconButton(
                    icon = Icons.Default.Favorite,
                    onClick = {},
                    contentDescription = "Favorito",
                    size = IconButtonSize.Medium,
                    variant = IconButtonVariant.Filled
                )
                Spacer(modifier = Modifier.width(8.dp))
                UmbralIconButton(
                    icon = Icons.Default.Favorite,
                    onClick = {},
                    contentDescription = "Favorito",
                    size = IconButtonSize.Large,
                    variant = IconButtonVariant.Filled
                )
            }
        }
    }
}

@Preview(name = "Tonal Variant - All Sizes", showBackground = true)
@Composable
private fun UmbralIconButtonTonalPreview() {
    UmbralTheme {
        Box(
            modifier = Modifier
                .background(DarkBackgroundBase)
                .padding(16.dp)
        ) {
            Row {
                UmbralIconButton(
                    icon = Icons.Default.Settings,
                    onClick = {},
                    contentDescription = "Configuración",
                    size = IconButtonSize.Small,
                    variant = IconButtonVariant.Tonal
                )
                Spacer(modifier = Modifier.width(8.dp))
                UmbralIconButton(
                    icon = Icons.Default.Settings,
                    onClick = {},
                    contentDescription = "Configuración",
                    size = IconButtonSize.Medium,
                    variant = IconButtonVariant.Tonal
                )
                Spacer(modifier = Modifier.width(8.dp))
                UmbralIconButton(
                    icon = Icons.Default.Settings,
                    onClick = {},
                    contentDescription = "Configuración",
                    size = IconButtonSize.Large,
                    variant = IconButtonVariant.Tonal
                )
            }
        }
    }
}

@Preview(name = "Disabled States", showBackground = true)
@Composable
private fun UmbralIconButtonDisabledPreview() {
    UmbralTheme {
        Box(
            modifier = Modifier
                .background(DarkBackgroundBase)
                .padding(16.dp)
        ) {
            Row {
                UmbralIconButton(
                    icon = Icons.Default.Close,
                    onClick = {},
                    contentDescription = "Cerrar",
                    variant = IconButtonVariant.Ghost,
                    enabled = false
                )
                Spacer(modifier = Modifier.width(8.dp))
                UmbralIconButton(
                    icon = Icons.Default.Close,
                    onClick = {},
                    contentDescription = "Cerrar",
                    variant = IconButtonVariant.Filled,
                    enabled = false
                )
                Spacer(modifier = Modifier.width(8.dp))
                UmbralIconButton(
                    icon = Icons.Default.Close,
                    onClick = {},
                    contentDescription = "Cerrar",
                    variant = IconButtonVariant.Tonal,
                    enabled = false
                )
            }
        }
    }
}

@Preview(name = "All Variants Comparison", showBackground = true)
@Composable
private fun UmbralIconButtonAllVariantsPreview() {
    UmbralTheme {
        Box(
            modifier = Modifier
                .background(DarkBackgroundBase)
                .padding(16.dp)
        ) {
            Row {
                // Ghost
                UmbralIconButton(
                    icon = Icons.Default.Menu,
                    onClick = {},
                    contentDescription = "Menu",
                    variant = IconButtonVariant.Ghost
                )
                Spacer(modifier = Modifier.width(12.dp))
                // Filled
                UmbralIconButton(
                    icon = Icons.Default.Favorite,
                    onClick = {},
                    contentDescription = "Favorito",
                    variant = IconButtonVariant.Filled
                )
                Spacer(modifier = Modifier.width(12.dp))
                // Tonal
                UmbralIconButton(
                    icon = Icons.Default.Settings,
                    onClick = {},
                    contentDescription = "Configuración",
                    variant = IconButtonVariant.Tonal
                )
            }
        }
    }
}
