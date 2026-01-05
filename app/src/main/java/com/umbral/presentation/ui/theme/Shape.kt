package com.umbral.presentation.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Umbral Design System - Shapes
 *
 * Border radius scale:
 * - Small: 8dp (chips, buttons, small elements)
 * - Medium: 12dp (cards pequeñas, inputs)
 * - Large: 16dp (cards principales)
 * - Extra Large: 24dp (modals, bottom sheets)
 */
val UmbralShapes = Shapes(
    // Small: chips, buttons, tags
    small = RoundedCornerShape(8.dp),

    // Medium: small cards, inputs, text fields
    medium = RoundedCornerShape(12.dp),

    // Large: main cards, containers
    large = RoundedCornerShape(16.dp),

    // Extra Large: modals, bottom sheets, dialogs
    extraLarge = RoundedCornerShape(24.dp)
)

/**
 * Additional shape tokens for specific use cases
 */
object UmbralCornerRadius {
    val none = 0.dp
    val xs = 4.dp       // Very subtle rounding
    val sm = 8.dp       // Small elements (chips, buttons)
    val md = 12.dp      // Medium elements (small cards)
    val lg = 16.dp      // Large elements (main cards)
    val xl = 24.dp      // Extra large (modals, sheets)
    val full = 999.dp   // Fully rounded (pills, circles)
}

/**
 * Pre-built shapes for common use cases
 */
object UmbralShapeTokens {
    // Buttons
    val button = RoundedCornerShape(UmbralCornerRadius.sm)
    val buttonSmall = RoundedCornerShape(UmbralCornerRadius.xs)
    val buttonPill = RoundedCornerShape(UmbralCornerRadius.full)

    // Chips & Tags
    val chip = RoundedCornerShape(UmbralCornerRadius.sm)

    // Cards
    val cardSmall = RoundedCornerShape(UmbralCornerRadius.md)
    val card = RoundedCornerShape(UmbralCornerRadius.lg)
    val cardLarge = RoundedCornerShape(UmbralCornerRadius.xl)

    // Inputs
    val textField = RoundedCornerShape(UmbralCornerRadius.md)

    // Modals & Sheets
    val bottomSheet = RoundedCornerShape(
        topStart = UmbralCornerRadius.xl,
        topEnd = UmbralCornerRadius.xl,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )
    val dialog = RoundedCornerShape(UmbralCornerRadius.xl)

    // Special
    val circle = RoundedCornerShape(UmbralCornerRadius.full)
    val avatar = RoundedCornerShape(UmbralCornerRadius.full)
}
