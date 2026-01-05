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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.umbral.presentation.ui.theme.UmbralSpacing
import com.umbral.presentation.ui.theme.UmbralTheme

/**
 * Shimmer loading effect for placeholder content
 *
 * Creates an animated shimmer effect that indicates loading state.
 */
@Composable
fun ShimmerLoading(
    modifier: Modifier = Modifier,
    baseColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    highlightColor: Color = MaterialTheme.colorScheme.surface
) {
    val transition = rememberInfiniteTransition(label = "shimmer")

    val translateX by transition.animateFloat(
        initialValue = -500f,
        targetValue = 1500f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            baseColor,
            highlightColor,
            baseColor
        ),
        start = Offset(translateX, 0f),
        end = Offset(translateX + 500f, 0f)
    )

    Box(
        modifier = modifier.background(brush = shimmerBrush)
    )
}

/**
 * Shimmer box with rounded corners
 */
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    height: Dp = 16.dp,
    cornerRadius: Dp = 4.dp,
    baseColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    highlightColor: Color = MaterialTheme.colorScheme.surface
) {
    ShimmerLoading(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(cornerRadius)),
        baseColor = baseColor,
        highlightColor = highlightColor
    )
}

/**
 * Shimmer circle for avatar placeholders
 */
@Composable
fun ShimmerCircle(
    size: Dp,
    modifier: Modifier = Modifier,
    baseColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    highlightColor: Color = MaterialTheme.colorScheme.surface
) {
    ShimmerLoading(
        modifier = modifier
            .size(size)
            .clip(CircleShape),
        baseColor = baseColor,
        highlightColor = highlightColor
    )
}

/**
 * Shimmer placeholder for a typical list item with icon and text
 */
@Composable
fun ShimmerListItem(
    modifier: Modifier = Modifier,
    hasIcon: Boolean = true,
    lines: Int = 2
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(UmbralSpacing.cardPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (hasIcon) {
            ShimmerCircle(size = 40.dp)
            Spacer(modifier = Modifier.width(UmbralSpacing.md))
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(lines) { index ->
                ShimmerBox(
                    modifier = Modifier.fillMaxWidth(
                        if (index == lines - 1) 0.6f else 1f
                    ),
                    height = if (index == 0) 16.dp else 12.dp
                )
            }
        }
    }
}

/**
 * Shimmer placeholder for a card with title and content
 */
@Composable
fun ShimmerCard(
    modifier: Modifier = Modifier,
    hasHeader: Boolean = true,
    contentLines: Int = 3
) {
    UmbralCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(UmbralSpacing.sm)
        ) {
            if (hasHeader) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ShimmerCircle(size = 48.dp)
                    Spacer(modifier = Modifier.width(UmbralSpacing.md))
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ShimmerBox(
                            modifier = Modifier.fillMaxWidth(0.7f),
                            height = 18.dp
                        )
                        ShimmerBox(
                            modifier = Modifier.fillMaxWidth(0.5f),
                            height = 12.dp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(UmbralSpacing.md))
            }

            repeat(contentLines) { index ->
                ShimmerBox(
                    modifier = Modifier.fillMaxWidth(
                        when {
                            index == contentLines - 1 -> 0.5f
                            index % 2 == 0 -> 1f
                            else -> 0.85f
                        }
                    ),
                    height = 14.dp
                )
                if (index < contentLines - 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

/**
 * Shimmer placeholder for stats display
 */
@Composable
fun ShimmerStatsItem(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ShimmerBox(
            modifier = Modifier.width(60.dp),
            height = 32.dp,
            cornerRadius = 8.dp
        )
        ShimmerBox(
            modifier = Modifier.width(80.dp),
            height = 12.dp
        )
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Shimmer Box", showBackground = true)
@Composable
private fun ShimmerBoxPreview() {
    UmbralTheme {
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            height = 20.dp
        )
    }
}

@Preview(name = "Shimmer Circle", showBackground = true)
@Composable
private fun ShimmerCirclePreview() {
    UmbralTheme {
        ShimmerCircle(
            size = 60.dp,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Shimmer List Item", showBackground = true)
@Composable
private fun ShimmerListItemPreview() {
    UmbralTheme {
        ShimmerListItem(
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(name = "Shimmer Card", showBackground = true)
@Composable
private fun ShimmerCardPreview() {
    UmbralTheme {
        ShimmerCard(
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Shimmer Stats", showBackground = true)
@Composable
private fun ShimmerStatsPreview() {
    UmbralTheme {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ShimmerStatsItem()
            ShimmerStatsItem()
            ShimmerStatsItem()
        }
    }
}

@Preview(name = "Shimmer List - Multiple Items", showBackground = true)
@Composable
private fun ShimmerListPreview() {
    UmbralTheme {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(3) {
                ShimmerListItem()
            }
        }
    }
}

@Preview(name = "Dark Theme Shimmer", showBackground = true)
@Composable
private fun ShimmerDarkPreview() {
    UmbralTheme(darkTheme = true) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ShimmerListItem()
            ShimmerCard(contentLines = 2)
        }
    }
}
