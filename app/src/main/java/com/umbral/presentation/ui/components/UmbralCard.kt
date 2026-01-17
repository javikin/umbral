package com.umbral.presentation.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.umbral.presentation.ui.theme.DarkAccentPrimary
import com.umbral.presentation.ui.theme.DarkBackgroundBase
import com.umbral.presentation.ui.theme.DarkBackgroundElevated
import com.umbral.presentation.ui.theme.DarkBackgroundSurface
import com.umbral.presentation.ui.theme.DarkBorderDefault
import com.umbral.presentation.ui.theme.DarkBorderFocus
import com.umbral.presentation.ui.theme.LightBackgroundBase
import com.umbral.presentation.ui.theme.LightBackgroundElevated
import com.umbral.presentation.ui.theme.LightBackgroundSurface
import com.umbral.presentation.ui.theme.LightBorderDefault
import com.umbral.presentation.ui.theme.LightBorderFocus
import com.umbral.presentation.ui.theme.UmbralSpacing
import com.umbral.presentation.ui.theme.UmbralTheme
import com.umbral.presentation.ui.theme.surfaceColorAtElevation
import com.umbral.presentation.ui.theme.SurfaceElevation

// =============================================================================
// LEGACY ELEVATION (Backward Compatibility - Keep for existing code)
// =============================================================================

/**
 * Legacy elevation levels for UmbralCard (Design System 1.0)
 *
 * DEPRECATED: This is kept for backward compatibility only.
 * New code should use CardVariant instead.
 *
 * In light mode, uses shadow-based elevation.
 * In dark mode, uses reduced shadows with tonal elevation (lighter surface = higher).
 */
@Deprecated("Use CardVariant instead for Design System 2.0")
enum class UmbralElevation(
    val lightDefault: Dp,
    val lightPressed: Dp,
    val darkDefault: Dp,
    val darkPressed: Dp,
    val surfaceLevel: SurfaceElevation
) {
    None(0.dp, 0.dp, 0.dp, 0.dp, SurfaceElevation.Level0),
    Subtle(1.dp, 0.dp, 0.dp, 0.dp, SurfaceElevation.Level1),
    Medium(4.dp, 2.dp, 1.dp, 0.dp, SurfaceElevation.Level2),
    High(8.dp, 4.dp, 2.dp, 1.dp, SurfaceElevation.Level3)
}

/**
 * Legacy elevation values for backwards compatibility
 */
@Suppress("unused")
@Deprecated("Use CardVariant instead")
val UmbralElevation.default: Dp
    get() = lightDefault

@Suppress("unused")
@Deprecated("Use CardVariant instead")
val UmbralElevation.pressed: Dp
    get() = lightPressed

// =============================================================================
// DESIGN SYSTEM 2.0 - CARD VARIANT
// =============================================================================

/**
 * Card variant styles for UmbralCard (Design System 2.0)
 *
 * Default: Standard border, no special states
 * Elevated: Uses elevated background color
 * Outlined: More visible border (1.5dp instead of 1dp)
 * Interactive: Adds press and focus states (only when onClick is provided)
 */
enum class CardVariant {
    Default,
    Elevated,
    Outlined,
    Interactive
}

// =============================================================================
// DESIGN SYSTEM 2.0 - UMBRAL CARD (NEW)
// =============================================================================

/**
 * Umbral Design System 2.0 - Card Component
 *
 * Flat design card with border instead of shadow.
 * Supports multiple variants and interactive states.
 *
 * Visual Specs (Design System 2.0):
 * - Dark Theme: Background #1E1E1E, Border 1px 6% white
 * - Light Theme: Background #FFFFFF, Border 1px 4% black
 * - Corner Radius: 16dp
 * - Padding: 16dp
 * - No shadow/elevation
 *
 * Interactive States (when onClick != null):
 * - Pressed: background +4% lighter, scale 0.99
 * - Focused: border changes to borderFocus (30% accent)
 *
 * @param modifier Modifier for customization
 * @param onClick Optional click handler - if provided, card becomes clickable
 * @param variant Card visual variant (Default, Elevated, Outlined, Interactive)
 * @param shape Card corner shape (default: 16dp rounded)
 * @param content Card content (ColumnScope for vertical layout)
 */
@Composable
fun UmbralCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    variant: CardVariant = CardVariant.Default,
    shape: Shape = MaterialTheme.shapes.large,
    content: @Composable ColumnScope.() -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()

    // Background color based on variant and theme
    val backgroundColor = when (variant) {
        CardVariant.Default -> if (isDarkTheme) DarkBackgroundSurface else LightBackgroundSurface
        CardVariant.Elevated -> if (isDarkTheme) DarkBackgroundElevated else LightBackgroundElevated
        CardVariant.Outlined -> if (isDarkTheme) DarkBackgroundSurface else LightBackgroundSurface
        CardVariant.Interactive -> if (isDarkTheme) DarkBackgroundSurface else LightBackgroundSurface
    }

    // Apply +4% overlay when pressed (for interactive cards only)
    val pressedOverlay = if (isDarkTheme) Color.White.copy(alpha = 0.04f) else Color.Black.copy(alpha = 0.04f)

    // Border configuration
    val borderWidth = when (variant) {
        CardVariant.Outlined -> 1.5.dp
        else -> 1.dp
    }

    val borderColor = when {
        isFocused && onClick != null -> if (isDarkTheme) DarkBorderFocus else LightBorderFocus
        variant == CardVariant.Outlined -> if (isDarkTheme) DarkBorderDefault.copy(alpha = 1.5f) else LightBorderDefault.copy(alpha = 1.5f)
        else -> if (isDarkTheme) DarkBorderDefault else LightBorderDefault
    }

    // Scale animation for pressed state
    val scale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.99f else 1f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 400f
        ),
        label = "cardScale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(shape)
            .background(backgroundColor)
            .border(borderWidth, borderColor, shape)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null, // No ripple, we handle press state manually
                        onClick = onClick
                    )
                } else {
                    Modifier
                }
            )
    ) {
        // Add pressed overlay
        if (isPressed && onClick != null) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(pressedOverlay, shape)
            )
        }

        Column(
            modifier = Modifier.padding(UmbralSpacing.cardPadding),
            content = content
        )
    }
}

// =============================================================================
// LEGACY OVERLOADS (Backward Compatibility)
// =============================================================================

/**
 * Legacy UmbralCard with elevation parameter (Design System 1.0)
 *
 * DEPRECATED: This is kept for backward compatibility only.
 * Existing code using elevation parameter will continue to work.
 * New code should use the variant parameter instead.
 *
 * @param modifier Modifier for customization
 * @param elevation Elevation level (shadow-based, deprecated)
 * @param shape Card corner shape
 * @param onClick Optional click handler
 * @param content Card content
 */
@Deprecated(
    "Use UmbralCard with variant parameter instead for Design System 2.0",
    ReplaceWith("UmbralCard(modifier, onClick, CardVariant.Default, shape, content)")
)
@Composable
fun UmbralCard(
    modifier: Modifier = Modifier,
    elevation: UmbralElevation = UmbralElevation.Subtle,
    shape: Shape = MaterialTheme.shapes.large,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isDarkTheme = isSystemInDarkTheme()

    // Use different elevation values for light and dark themes
    val defaultElevation = if (isDarkTheme) elevation.darkDefault else elevation.lightDefault
    val pressedElevation = if (isDarkTheme) elevation.darkPressed else elevation.lightPressed

    val animatedElevation by animateDpAsState(
        targetValue = if (isPressed && onClick != null) pressedElevation else defaultElevation,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 400f
        ),
        label = "cardElevation"
    )

    // In dark mode, use tonal surface color for elevation instead of shadows
    val containerColor = if (isDarkTheme) {
        surfaceColorAtElevation(elevation.surfaceLevel)
    } else {
        MaterialTheme.colorScheme.surface
    }

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            colors = CardDefaults.cardColors(
                containerColor = containerColor
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = animatedElevation,
                pressedElevation = pressedElevation
            ),
            interactionSource = interactionSource
        ) {
            Column(
                modifier = Modifier.padding(UmbralSpacing.cardPadding),
                content = content
            )
        }
    } else {
        Card(
            modifier = modifier,
            shape = shape,
            colors = CardDefaults.cardColors(
                containerColor = containerColor
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = defaultElevation
            )
        ) {
            Column(
                modifier = Modifier.padding(UmbralSpacing.cardPadding),
                content = content
            )
        }
    }
}

/**
 * Legacy UmbralOutlinedCard (Design System 1.0)
 *
 * DEPRECATED: Use UmbralCard with variant = CardVariant.Outlined instead.
 *
 * @param modifier Modifier for customization
 * @param shape Card corner shape
 * @param onClick Optional click handler
 * @param content Card content
 */
@Deprecated(
    "Use UmbralCard with variant = CardVariant.Outlined instead",
    ReplaceWith("UmbralCard(modifier, onClick, CardVariant.Outlined, shape, content)")
)
@Composable
fun UmbralOutlinedCard(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.large,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isDarkTheme = isSystemInDarkTheme()

    // In dark mode, use a slightly elevated surface for better contrast
    val containerColor = if (isDarkTheme) {
        surfaceColorAtElevation(SurfaceElevation.Level1)
    } else {
        MaterialTheme.colorScheme.surface
    }

    if (onClick != null) {
        androidx.compose.material3.OutlinedCard(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            colors = CardDefaults.outlinedCardColors(
                containerColor = containerColor
            ),
            interactionSource = interactionSource
        ) {
            Column(
                modifier = Modifier.padding(UmbralSpacing.cardPadding),
                content = content
            )
        }
    } else {
        androidx.compose.material3.OutlinedCard(
            modifier = modifier,
            shape = shape,
            colors = CardDefaults.outlinedCardColors(
                containerColor = containerColor
            )
        ) {
            Column(
                modifier = Modifier.padding(UmbralSpacing.cardPadding),
                content = content
            )
        }
    }
}

// =============================================================================
// PREVIEWS - Design System 2.0
// =============================================================================

@Preview(name = "Card - Default Variant", showBackground = true)
@Composable
private fun UmbralCardDefaultPreview() {
    UmbralTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            UmbralCard(
                modifier = Modifier.fillMaxWidth(),
                variant = CardVariant.Default
            ) {
                Text(
                    text = "Default Card",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Standard border, flat design",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(name = "Card - Elevated Variant", showBackground = true)
@Composable
private fun UmbralCardElevatedPreview() {
    UmbralTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            UmbralCard(
                modifier = Modifier.fillMaxWidth(),
                variant = CardVariant.Elevated
            ) {
                Text(
                    text = "Elevated Card",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Uses elevated background color",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(name = "Card - Outlined Variant", showBackground = true)
@Composable
private fun UmbralCardOutlinedPreview() {
    UmbralTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            UmbralCard(
                modifier = Modifier.fillMaxWidth(),
                variant = CardVariant.Outlined
            ) {
                Text(
                    text = "Outlined Card",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "More visible border (1.5dp)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(name = "Card - Interactive Variant", showBackground = true)
@Composable
private fun UmbralCardInteractivePreview() {
    UmbralTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            UmbralCard(
                modifier = Modifier.fillMaxWidth(),
                variant = CardVariant.Interactive,
                onClick = {}
            ) {
                Text(
                    text = "Interactive Card",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Tap to see press state (scale + overlay)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(name = "Card - Clickable Default", showBackground = true)
@Composable
private fun UmbralCardClickablePreview() {
    UmbralTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            UmbralCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = {},
                variant = CardVariant.Default // Explicitly specify variant to avoid ambiguity
            ) {
                Text(
                    text = "Clickable Card",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Default variant with onClick handler",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(name = "Dark Theme - All Variants", showBackground = true)
@Composable
private fun UmbralCardDarkPreview() {
    UmbralTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkBackgroundBase)
                .padding(16.dp)
        ) {
            Column {
                UmbralCard(
                    modifier = Modifier.fillMaxWidth(),
                    variant = CardVariant.Default
                ) {
                    Text(
                        text = "Dark Theme - Default",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Background: #1E1E1E, Border: 6% white",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(modifier = Modifier.padding(vertical = 8.dp))

                UmbralCard(
                    modifier = Modifier.fillMaxWidth(),
                    variant = CardVariant.Elevated
                ) {
                    Text(
                        text = "Dark Theme - Elevated",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Background: #282828",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(modifier = Modifier.padding(vertical = 8.dp))

                UmbralCard(
                    modifier = Modifier.fillMaxWidth(),
                    variant = CardVariant.Interactive,
                    onClick = {}
                ) {
                    Text(
                        text = "Dark Theme - Interactive",
                        style = MaterialTheme.typography.titleMedium,
                        color = DarkAccentPrimary
                    )
                    Text(
                        text = "Tap to see press states",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// =============================================================================
// LEGACY PREVIEWS (Design System 1.0)
// =============================================================================

@Preview(name = "Legacy - Subtle Elevation", showBackground = true)
@Composable
private fun UmbralCardSubtlePreview() {
    UmbralTheme {
        UmbralCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = UmbralElevation.Subtle
        ) {
            Text(
                text = "Legacy Card (Subtle)",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Using deprecated elevation parameter",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(name = "Legacy - Outlined", showBackground = true)
@Composable
private fun UmbralOutlinedCardPreview() {
    UmbralTheme {
        UmbralOutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Legacy Outlined Card",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Using deprecated UmbralOutlinedCard",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
