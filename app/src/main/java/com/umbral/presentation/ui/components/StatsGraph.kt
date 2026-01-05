package com.umbral.presentation.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umbral.presentation.ui.theme.UmbralTheme
import kotlin.math.abs

/**
 * Umbral Stats Graph Component
 *
 * A minimalist line graph with animated drawing and touch interaction.
 *
 * @param data List of values to display (normalized 0-1 recommended)
 * @param modifier Modifier for customization
 * @param lineColor Color for the line
 * @param showLabels Whether to show value labels
 * @param labels Optional labels for x-axis (e.g., days of week)
 */
@Composable
fun StatsGraph(
    data: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    showLabels: Boolean = true,
    labels: List<String>? = null
) {
    if (data.isEmpty()) return

    val animationProgress = remember { Animatable(0f) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var touchX by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(data) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800)
        )
    }

    val gradientColors = listOf(
        lineColor.copy(alpha = 0.3f),
        lineColor.copy(alpha = 0.0f)
    )

    Column(modifier = modifier) {
        // Selected value display
        selectedIndex?.let { index ->
            if (index in data.indices) {
                Text(
                    text = "${(data[index] * 100).toInt()}%",
                    style = MaterialTheme.typography.titleLarge,
                    color = lineColor,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .pointerInput(data) {
                        detectTapGestures { offset ->
                            touchX = offset.x
                            val segmentWidth = size.width.toFloat() / (data.size - 1)
                            selectedIndex = ((offset.x / segmentWidth).toInt())
                                .coerceIn(0, data.size - 1)
                        }
                    }
            ) {
                val width = size.width
                val height = size.height
                val padding = 16.dp.toPx()

                val maxValue = data.maxOrNull() ?: 1f
                val minValue = data.minOrNull() ?: 0f
                val range = (maxValue - minValue).takeIf { it > 0 } ?: 1f

                // Calculate points
                val points = data.mapIndexed { index, value ->
                    val x = padding + (index.toFloat() / (data.size - 1)) * (width - 2 * padding)
                    val normalizedValue = (value - minValue) / range
                    val y = height - padding - (normalizedValue * (height - 2 * padding))
                    Offset(x, y)
                }

                // Limit points based on animation progress
                val animatedPointCount = (points.size * animationProgress.value).toInt()
                    .coerceAtLeast(1)
                val animatedPoints = points.take(animatedPointCount)

                // Draw gradient fill under line
                if (animatedPoints.size > 1) {
                    val fillPath = Path().apply {
                        moveTo(animatedPoints.first().x, height - padding)
                        animatedPoints.forEach { point ->
                            lineTo(point.x, point.y)
                        }
                        lineTo(animatedPoints.last().x, height - padding)
                        close()
                    }

                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(gradientColors)
                    )
                }

                // Draw the line
                if (animatedPoints.size > 1) {
                    val linePath = Path().apply {
                        moveTo(animatedPoints.first().x, animatedPoints.first().y)
                        for (i in 1 until animatedPoints.size) {
                            lineTo(animatedPoints[i].x, animatedPoints[i].y)
                        }
                    }

                    drawPath(
                        path = linePath,
                        color = lineColor,
                        style = Stroke(
                            width = 3.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }

                // Draw dots at data points
                animatedPoints.forEachIndexed { index, point ->
                    val isSelected = index == selectedIndex
                    val radius = if (isSelected) 8.dp.toPx() else 4.dp.toPx()

                    drawCircle(
                        color = lineColor,
                        radius = radius,
                        center = point
                    )

                    if (!isSelected) {
                        drawCircle(
                            color = Color.White,
                            radius = 2.dp.toPx(),
                            center = point
                        )
                    }
                }
            }
        }

        // X-axis labels
        if (showLabels && labels != null) {
            androidx.compose.foundation.layout.Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
            ) {
                labels.take(data.size).forEach { label ->
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Stats Graph", showBackground = true)
@Composable
private fun StatsGraphPreview() {
    UmbralTheme {
        StatsGraph(
            data = listOf(0.3f, 0.5f, 0.4f, 0.8f, 0.6f, 0.9f, 0.7f),
            labels = listOf("L", "M", "X", "J", "V", "S", "D"),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Stats Graph - No Labels", showBackground = true)
@Composable
private fun StatsGraphNoLabelsPreview() {
    UmbralTheme {
        StatsGraph(
            data = listOf(0.2f, 0.6f, 0.4f, 0.9f, 0.5f),
            showLabels = false,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Stats Graph - Flat", showBackground = true)
@Composable
private fun StatsGraphFlatPreview() {
    UmbralTheme {
        StatsGraph(
            data = listOf(0.5f, 0.5f, 0.5f, 0.5f, 0.5f),
            labels = listOf("L", "M", "X", "J", "V"),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Stats Graph - Upward Trend", showBackground = true)
@Composable
private fun StatsGraphUpwardPreview() {
    UmbralTheme {
        StatsGraph(
            data = listOf(0.1f, 0.2f, 0.35f, 0.5f, 0.7f, 0.85f, 1.0f),
            labels = listOf("L", "M", "X", "J", "V", "S", "D"),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Dark Theme Stats Graph", showBackground = true)
@Composable
private fun StatsGraphDarkPreview() {
    UmbralTheme(darkTheme = true) {
        StatsGraph(
            data = listOf(0.3f, 0.5f, 0.4f, 0.8f, 0.6f, 0.9f, 0.7f),
            labels = listOf("L", "M", "X", "J", "V", "S", "D"),
            modifier = Modifier.padding(16.dp)
        )
    }
}
