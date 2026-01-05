package com.umbral.presentation.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umbral.presentation.ui.theme.UmbralSpacing
import com.umbral.presentation.ui.theme.UmbralTheme

/**
 * An icon that animates with a subtle scale effect when it appears.
 *
 * @param icon The ImageVector to display
 * @param modifier Modifier for customization
 * @param tint Color for the icon
 * @param animate Whether to animate the icon on appear
 * @param contentDescription Accessibility description
 */
@Composable
fun AnimatedIcon(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    animate: Boolean = true,
    contentDescription: String? = null
) {
    var isVisible by remember { mutableStateOf(!animate) }

    LaunchedEffect(Unit) {
        if (animate) {
            isVisible = true
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.5f,
        animationSpec = spring(
            dampingRatio = 0.5f,
            stiffness = 400f
        ),
        label = "iconScale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "iconAlpha"
    )

    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        modifier = modifier.scale(scale),
        tint = tint.copy(alpha = alpha)
    )
}

/**
 * Pulsing animated icon for emphasis
 */
@Composable
fun PulsingIcon(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    contentDescription: String? = null
) {
    var isPulsing by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (true) {
            isPulsing = !isPulsing
            kotlinx.coroutines.delay(800)
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (isPulsing) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = 0.4f,
            stiffness = 200f
        ),
        label = "pulseScale"
    )

    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        modifier = modifier.scale(scale),
        tint = tint
    )
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Animated Icon", showBackground = true)
@Composable
private fun AnimatedIconPreview() {
    UmbralTheme {
        AnimatedIcon(
            icon = Icons.Default.Lock,
            modifier = Modifier.size(UmbralSpacing.iconSizeLarge),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview(name = "Static Icon", showBackground = true)
@Composable
private fun StaticIconPreview() {
    UmbralTheme {
        AnimatedIcon(
            icon = Icons.Default.Lock,
            modifier = Modifier.size(UmbralSpacing.iconSizeLarge),
            tint = MaterialTheme.colorScheme.primary,
            animate = false
        )
    }
}

@Preview(name = "Pulsing Icon", showBackground = true)
@Composable
private fun PulsingIconPreview() {
    UmbralTheme {
        PulsingIcon(
            icon = Icons.Default.Star,
            modifier = Modifier.size(UmbralSpacing.iconSizeXLarge),
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}

@Preview(name = "Dark Theme Icon", showBackground = true)
@Composable
private fun AnimatedIconDarkPreview() {
    UmbralTheme(darkTheme = true) {
        AnimatedIcon(
            icon = Icons.Default.Lock,
            modifier = Modifier.size(UmbralSpacing.iconSizeLarge),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}
