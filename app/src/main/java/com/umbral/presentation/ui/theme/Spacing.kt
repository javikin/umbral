package com.umbral.presentation.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Umbral Design System - Spacing Tokens
 *
 * Based on 4dp base unit for consistency.
 * Use these tokens instead of hardcoded dp values.
 */
data class UmbralSpacingValues(
    // ==========================================================================
    // BASE SPACING SCALE
    // ==========================================================================
    val xs: Dp = 4.dp,      // Extra small: tight spacing
    val sm: Dp = 8.dp,      // Small: compact elements
    val md: Dp = 16.dp,     // Medium: default spacing
    val lg: Dp = 24.dp,     // Large: section spacing
    val xl: Dp = 32.dp,     // Extra large: major sections
    val xxl: Dp = 48.dp,    // 2XL: screen-level spacing

    // ==========================================================================
    // SCREEN LAYOUT
    // ==========================================================================
    val screenHorizontal: Dp = 20.dp,   // Horizontal screen padding
    val screenVertical: Dp = 16.dp,     // Vertical screen padding
    val screenTop: Dp = 24.dp,          // Top padding after app bar

    // ==========================================================================
    // COMPONENT SPACING
    // ==========================================================================
    val cardPadding: Dp = 16.dp,        // Internal card padding
    val cardSpacing: Dp = 12.dp,        // Space between cards
    val listItemSpacing: Dp = 8.dp,     // Space between list items
    val chipSpacing: Dp = 8.dp,         // Space between chips
    val buttonSpacing: Dp = 12.dp,      // Space between buttons

    // ==========================================================================
    // ICON SPACING
    // ==========================================================================
    val iconTextSpacing: Dp = 8.dp,     // Space between icon and text
    val iconPadding: Dp = 12.dp,        // Padding around icon buttons

    // ==========================================================================
    // COMPONENT SIZES
    // ==========================================================================
    val buttonHeight: Dp = 56.dp,       // Standard button height
    val buttonHeightSmall: Dp = 40.dp,  // Small button height
    val chipHeight: Dp = 32.dp,         // Chip height
    val iconSizeSmall: Dp = 18.dp,
    val iconSizeMedium: Dp = 24.dp,
    val iconSizeLarge: Dp = 32.dp,
    val iconSizeXLarge: Dp = 48.dp,

    // ==========================================================================
    // TOUCH TARGETS
    // ==========================================================================
    val minTouchTarget: Dp = 48.dp      // Minimum touch target size (accessibility)
)

/**
 * Object accessor for spacing values.
 * Use: UmbralSpacing.md, UmbralSpacing.screenHorizontal, etc.
 */
object UmbralSpacing {
    private val values = UmbralSpacingValues()

    // Base scale
    val xs: Dp get() = values.xs
    val sm: Dp get() = values.sm
    val md: Dp get() = values.md
    val lg: Dp get() = values.lg
    val xl: Dp get() = values.xl
    val xxl: Dp get() = values.xxl

    // Screen layout
    val screenHorizontal: Dp get() = values.screenHorizontal
    val screenVertical: Dp get() = values.screenVertical
    val screenTop: Dp get() = values.screenTop

    // Components
    val cardPadding: Dp get() = values.cardPadding
    val cardSpacing: Dp get() = values.cardSpacing
    val listItemSpacing: Dp get() = values.listItemSpacing
    val chipSpacing: Dp get() = values.chipSpacing
    val buttonSpacing: Dp get() = values.buttonSpacing

    // Icons
    val iconTextSpacing: Dp get() = values.iconTextSpacing
    val iconPadding: Dp get() = values.iconPadding

    // Sizes
    val buttonHeight: Dp get() = values.buttonHeight
    val buttonHeightSmall: Dp get() = values.buttonHeightSmall
    val chipHeight: Dp get() = values.chipHeight
    val iconSizeSmall: Dp get() = values.iconSizeSmall
    val iconSizeMedium: Dp get() = values.iconSizeMedium
    val iconSizeLarge: Dp get() = values.iconSizeLarge
    val iconSizeXLarge: Dp get() = values.iconSizeXLarge

    // Touch
    val minTouchTarget: Dp get() = values.minTouchTarget
}

/**
 * CompositionLocal for spacing (optional, for theme-based access)
 */
val LocalUmbralSpacing = staticCompositionLocalOf { UmbralSpacingValues() }

/**
 * Extension to access spacing from MaterialTheme-style accessor
 */
object UmbralTheme {
    val spacing: UmbralSpacingValues
        @Composable
        @ReadOnlyComposable
        get() = LocalUmbralSpacing.current
}
