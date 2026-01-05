package com.umbral.presentation.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.umbral.presentation.ui.theme.UmbralSpacing
import com.umbral.presentation.ui.theme.UmbralTheme
import com.umbral.presentation.ui.theme.surfaceColorAtElevation
import com.umbral.presentation.ui.theme.SurfaceElevation

/**
 * Elevation levels for UmbralCard
 *
 * In light mode, uses shadow-based elevation.
 * In dark mode, uses reduced shadows with tonal elevation (lighter surface = higher).
 */
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
val UmbralElevation.default: Dp
    get() = lightDefault

@Suppress("unused")
val UmbralElevation.pressed: Dp
    get() = lightPressed

/**
 * Umbral Design System Card
 *
 * A versatile card component with customizable elevation and optional click behavior.
 * Includes press state animation for interactive cards.
 *
 * @param modifier Modifier for customization
 * @param elevation Elevation level (affects shadow depth)
 * @param shape Card corner shape
 * @param onClick Optional click handler - if provided, card becomes clickable
 * @param content Card content
 */
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
 * Outlined variant of UmbralCard
 *
 * Uses theme-aware border and container colors.
 */
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
// PREVIEWS
// =============================================================================

@Preview(name = "Card - Subtle Elevation", showBackground = true)
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
                text = "Card Title",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Card content goes here",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(name = "Card - Medium Elevation", showBackground = true)
@Composable
private fun UmbralCardMediumPreview() {
    UmbralTheme {
        UmbralCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = UmbralElevation.Medium
        ) {
            Text(
                text = "Medium Elevation Card",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Preview(name = "Clickable Card", showBackground = true)
@Composable
private fun UmbralCardClickablePreview() {
    UmbralTheme {
        UmbralCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            onClick = {}
        ) {
            Text(
                text = "Tap Me",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "This card is clickable",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(name = "Outlined Card", showBackground = true)
@Composable
private fun UmbralOutlinedCardPreview() {
    UmbralTheme {
        UmbralOutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Outlined Card",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Preview(name = "Dark Theme Card", showBackground = true)
@Composable
private fun UmbralCardDarkPreview() {
    UmbralTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            UmbralCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = UmbralElevation.Medium
            ) {
                Text(
                    text = "Dark Theme Card",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Content in dark mode - uses tonal elevation",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
