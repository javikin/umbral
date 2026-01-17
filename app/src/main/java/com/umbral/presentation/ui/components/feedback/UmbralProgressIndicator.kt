package com.umbral.presentation.ui.components.feedback

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.umbral.presentation.ui.theme.UmbralMotion
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Umbral Design System 2.0 - Progress Indicator
 *
 * Indeterminate progress indicator with multiple animation variants.
 *
 * ## Variants
 * - Circular: Classic rotating spinner
 * - Dots: Three animated dots with staggered scale
 * - Pulse: Single circle pulsing in size and opacity
 *
 * ## Usage
 * ```kotlin
 * // Default circular indicator
 * UmbralProgressIndicator()
 *
 * // Dots variant
 * UmbralProgressIndicator(variant = ProgressVariant.Dots)
 *
 * // Large pulse variant
 * UmbralProgressIndicator(
 *     variant = ProgressVariant.Pulse,
 *     size = ProgressSize.Large
 * )
 * ```
 *
 * @param modifier Modifier for the indicator
 * @param variant Visual style of the indicator
 * @param size Size preset for the indicator
 */
@Composable
fun UmbralProgressIndicator(
    modifier: Modifier = Modifier,
    variant: ProgressVariant = ProgressVariant.Circular,
    size: ProgressSize = ProgressSize.Medium
) {
    when (variant) {
        ProgressVariant.Circular -> CircularProgress(modifier, size)
        ProgressVariant.Dots -> DotsProgress(modifier, size)
        ProgressVariant.Pulse -> PulseProgress(modifier, size)
    }
}

/**
 * Determinate progress bar showing completion percentage.
 *
 * ## Features
 * - Smooth spring-based width animation
 * - Pill-shaped design
 * - Semantic accent color
 *
 * ## Usage
 * ```kotlin
 * var progress by remember { mutableStateOf(0f) }
 *
 * UmbralProgressBar(progress = progress)
 *
 * // Update progress 0f to 1f
 * LaunchedEffect(Unit) {
 *     progress = 0.75f
 * }
 * ```
 *
 * @param progress Current progress value from 0f to 1f
 * @param modifier Modifier for the progress bar
 */
@Composable
fun UmbralProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val accentColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)

    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = UmbralMotion.springGentle(),
        label = "ProgressBarAnimation"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(4.dp)
            .clip(RoundedCornerShape(percent = 50))
    ) {
        // Background track
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRoundRect(
                color = backgroundColor,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.height / 2)
            )
        }

        // Progress fill
        Canvas(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
        ) {
            drawRoundRect(
                color = accentColor,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.height / 2)
            )
        }
    }
}

// =============================================================================
// VARIANTS IMPLEMENTATION
// =============================================================================

@Composable
private fun CircularProgress(
    modifier: Modifier = Modifier,
    size: ProgressSize
) {
    val diameter = size.toDp()
    val strokeWidth = 3.dp
    val accentColor = MaterialTheme.colorScheme.primary

    val infiniteTransition = rememberInfiniteTransition(label = "CircularProgressRotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = UmbralMotion.easeInOut
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "CircularProgressRotation"
    )

    Canvas(
        modifier = modifier.size(diameter)
    ) {
        drawCircularProgress(
            color = accentColor,
            strokeWidth = strokeWidth,
            rotation = rotation
        )
    }
}

@Composable
private fun DotsProgress(
    modifier: Modifier = Modifier,
    size: ProgressSize
) {
    val dotDiameter = 8.dp
    val spacing = 8.dp
    val accentColor = MaterialTheme.colorScheme.primary

    val infiniteTransition = rememberInfiniteTransition(label = "DotsProgressAnimation")

    // Create staggered animations for 3 dots
    val scales = (0..2).map { index ->
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 600
                    1f at 0
                    1.4f at (200 + index * 100)
                    1f at (400 + index * 100)
                },
                repeatMode = RepeatMode.Restart
            ),
            label = "DotScale$index"
        )
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        scales.forEach { scale ->
            val scaleValue by scale
            Canvas(modifier = Modifier.size(dotDiameter)) {
                drawCircle(
                    color = accentColor,
                    radius = (size.toPx() / 2) * scaleValue
                )
            }
        }
    }
}

@Composable
private fun PulseProgress(
    modifier: Modifier = Modifier,
    size: ProgressSize
) {
    val diameter = size.toDp()
    val accentColor = MaterialTheme.colorScheme.primary

    val infiniteTransition = rememberInfiniteTransition(label = "PulseProgressAnimation")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 800,
                easing = UmbralMotion.easeInOut
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 800,
                easing = UmbralMotion.easeInOut
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseAlpha"
    )

    Canvas(
        modifier = modifier.size(diameter)
    ) {
        drawCircle(
            color = accentColor.copy(alpha = alpha),
            radius = (size.toPx() / 2) * scale
        )
    }
}

// =============================================================================
// DRAWING UTILITIES
// =============================================================================

private fun DrawScope.drawCircularProgress(
    color: Color,
    strokeWidth: Dp,
    rotation: Float
) {
    val stroke = Stroke(
        width = strokeWidth.toPx(),
        cap = StrokeCap.Round
    )

    val sweepAngle = 270f
    val startAngle = rotation - 90f

    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        useCenter = false,
        style = stroke
    )
}

// =============================================================================
// ENUMS & SIZES
// =============================================================================

/**
 * Visual variant for progress indicator.
 */
enum class ProgressVariant {
    /** Classic rotating circular spinner */
    Circular,

    /** Three animated dots with staggered scale */
    Dots,

    /** Single pulsing circle */
    Pulse
}

/**
 * Size presets for progress indicators.
 */
enum class ProgressSize {
    /** 24dp - compact UI elements */
    Small,

    /** 40dp - standard size */
    Medium,

    /** 56dp - prominent loading states */
    Large;

    fun toDp(): Dp = when (this) {
        Small -> 24.dp
        Medium -> 40.dp
        Large -> 56.dp
    }
}
