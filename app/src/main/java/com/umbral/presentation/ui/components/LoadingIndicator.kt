package com.umbral.presentation.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.umbral.presentation.ui.theme.UmbralSpacing
import com.umbral.presentation.ui.theme.UmbralTheme

/**
 * Umbral Loading Indicator
 *
 * A customizable loading spinner with the Umbral design style.
 *
 * @param modifier Modifier for customization
 * @param size Size of the indicator
 * @param color Color of the indicator
 * @param strokeWidth Width of the spinner stroke
 */
@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    color: Color = MaterialTheme.colorScheme.primary,
    strokeWidth: Dp = 4.dp
) {
    CircularProgressIndicator(
        modifier = modifier.size(size),
        color = color,
        strokeWidth = strokeWidth
    )
}

/**
 * Pulsing loading indicator with three dots
 */
@Composable
fun PulsingLoadingIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    dotSize: Dp = 12.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulsingDots")

    val scale1 by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1Scale"
    )

    val scale2 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2Scale"
    )

    val scale3 by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3Scale"
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PulsingDot(size = dotSize, scale = scale1, color = color)
        PulsingDot(size = dotSize, scale = scale2, color = color)
        PulsingDot(size = dotSize, scale = scale3, color = color)
    }
}

@Composable
private fun PulsingDot(
    size: Dp,
    scale: Float,
    color: Color
) {
    Box(
        modifier = Modifier
            .size(size)
            .scale(scale)
            .clip(CircleShape)
            .background(color)
    )
}

/**
 * Full screen loading overlay
 */
@Composable
fun LoadingOverlay(
    modifier: Modifier = Modifier,
    message: String? = null
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoadingIndicator(
                size = 64.dp,
                strokeWidth = 5.dp
            )

            message?.let {
                Spacer(modifier = Modifier.height(UmbralSpacing.md))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

/**
 * Inline loading indicator with text
 */
@Composable
fun InlineLoadingIndicator(
    text: String,
    modifier: Modifier = Modifier,
    size: Dp = 20.dp
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(UmbralSpacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LoadingIndicator(
            size = size,
            strokeWidth = 2.dp
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Loading Indicator - Default", showBackground = true)
@Composable
private fun LoadingIndicatorPreview() {
    UmbralTheme {
        LoadingIndicator(
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Loading Indicator - Small", showBackground = true)
@Composable
private fun LoadingIndicatorSmallPreview() {
    UmbralTheme {
        LoadingIndicator(
            size = 24.dp,
            strokeWidth = 2.dp,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Loading Indicator - Large", showBackground = true)
@Composable
private fun LoadingIndicatorLargePreview() {
    UmbralTheme {
        LoadingIndicator(
            size = 72.dp,
            strokeWidth = 6.dp,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Pulsing Loading Indicator", showBackground = true)
@Composable
private fun PulsingLoadingIndicatorPreview() {
    UmbralTheme {
        PulsingLoadingIndicator(
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Inline Loading Indicator", showBackground = true)
@Composable
private fun InlineLoadingIndicatorPreview() {
    UmbralTheme {
        InlineLoadingIndicator(
            text = "Cargando...",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Loading Overlay", showBackground = true)
@Composable
private fun LoadingOverlayPreview() {
    UmbralTheme {
        LoadingOverlay(
            message = "Guardando cambios..."
        )
    }
}

@Preview(name = "Dark Theme Loading", showBackground = true)
@Composable
private fun LoadingIndicatorDarkPreview() {
    UmbralTheme(darkTheme = true) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LoadingIndicator()
            PulsingLoadingIndicator()
            InlineLoadingIndicator(text = "Cargando...")
        }
    }
}
