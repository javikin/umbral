package com.umbral.presentation.ui.components.empty

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.umbral.presentation.ui.theme.DarkAccentPrimary
import com.umbral.presentation.ui.theme.DarkTextTertiary
import com.umbral.presentation.ui.theme.LightAccentPrimary
import com.umbral.presentation.ui.theme.LightTextTertiary

/**
 * Illustration types for empty states
 */
enum class EmptyStateIllustration {
    NoProfiles,    // Profile icon with dotted lines
    NoApps,        // Empty app grid
    NoStats,       // Empty graph/chart
    NoNfc,         // NFC tag with question mark
    SearchEmpty,   // Magnifying glass with X
    Success,       // Checkmark
    Error,         // Alert triangle
    Offline        // Cloud with X
}

/**
 * Renders the appropriate illustration based on the type
 */
@Composable
fun EmptyStateIllustrationView(
    type: EmptyStateIllustration,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val baseColor = if (isDark) DarkTextTertiary else LightTextTertiary
    val accentColor = if (isDark) DarkAccentPrimary else LightAccentPrimary

    Canvas(modifier = modifier.size(120.dp)) {
        when (type) {
            EmptyStateIllustration.NoProfiles -> drawNoProfiles(baseColor, accentColor)
            EmptyStateIllustration.NoApps -> drawNoApps(baseColor, accentColor)
            EmptyStateIllustration.NoStats -> drawNoStats(baseColor, accentColor)
            EmptyStateIllustration.NoNfc -> drawNoNfc(baseColor, accentColor)
            EmptyStateIllustration.SearchEmpty -> drawSearchEmpty(baseColor, accentColor)
            EmptyStateIllustration.Success -> drawSuccess(baseColor, accentColor)
            EmptyStateIllustration.Error -> drawError(baseColor, accentColor)
            EmptyStateIllustration.Offline -> drawOffline(baseColor, accentColor)
        }
    }
}

// =============================================================================
// ILLUSTRATION IMPLEMENTATIONS
// =============================================================================

/**
 * No Profiles: Profile icon with dotted outline
 */
private fun DrawScope.drawNoProfiles(baseColor: Color, accentColor: Color) {
    val centerX = size.width / 2
    val centerY = size.height / 2

    // Profile head (circle)
    drawCircle(
        color = baseColor,
        radius = 18f,
        center = Offset(centerX, centerY - 15f),
        style = Stroke(width = 2.5f)
    )

    // Profile body (arc)
    val bodyPath = Path().apply {
        addArc(
            oval = Rect(
                left = centerX - 35f,
                top = centerY,
                right = centerX + 35f,
                bottom = centerY + 70f
            ),
            startAngleDegrees = 0f,
            sweepAngleDegrees = 180f
        )
    }
    drawPath(
        path = bodyPath,
        color = baseColor,
        style = Stroke(width = 2.5f)
    )

    // Dotted frame around profile
    val frameStroke = Stroke(
        width = 2f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
    )
    drawRoundRect(
        color = accentColor,
        topLeft = Offset(centerX - 50f, centerY - 60f),
        size = androidx.compose.ui.geometry.Size(100f, 120f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f),
        style = frameStroke
    )
}

/**
 * No Apps: Empty grid layout
 */
private fun DrawScope.drawNoApps(baseColor: Color, accentColor: Color) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    val gridSize = 22f
    val spacing = 8f
    val startX = centerX - (gridSize * 1.5f + spacing)
    val startY = centerY - (gridSize * 1.5f + spacing)

    // Draw 3x3 grid of rounded squares
    for (row in 0..2) {
        for (col in 0..2) {
            val x = startX + (col * (gridSize + spacing))
            val y = startY + (row * (gridSize + spacing))

            // Middle square highlighted with accent
            val color = if (row == 1 && col == 1) accentColor else baseColor
            val strokeWidth = if (row == 1 && col == 1) 2.5f else 2f

            drawRoundRect(
                color = color,
                topLeft = Offset(x, y),
                size = androidx.compose.ui.geometry.Size(gridSize, gridSize),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f),
                style = Stroke(width = strokeWidth)
            )
        }
    }
}

/**
 * No Stats: Empty bar chart
 */
private fun DrawScope.drawNoStats(baseColor: Color, accentColor: Color) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    val barWidth = 16f
    val spacing = 12f
    val baselineY = centerY + 40f

    // Draw 5 bars with varying heights
    val barHeights = listOf(30f, 50f, 25f, 45f, 35f)
    val startX = centerX - (barHeights.size * (barWidth + spacing) / 2)

    barHeights.forEachIndexed { index, height ->
        val x = startX + (index * (barWidth + spacing))
        val color = if (index == 2) accentColor else baseColor

        drawRoundRect(
            color = color,
            topLeft = Offset(x, baselineY - height),
            size = androidx.compose.ui.geometry.Size(barWidth, height),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f),
            style = Stroke(width = 2f)
        )
    }

    // Baseline
    drawLine(
        color = baseColor,
        start = Offset(startX - 10f, baselineY),
        end = Offset(startX + (barHeights.size * (barWidth + spacing)), baselineY),
        strokeWidth = 2f,
        cap = StrokeCap.Round
    )
}

/**
 * No NFC: NFC tag with question mark
 */
private fun DrawScope.drawNoNfc(baseColor: Color, accentColor: Color) {
    val centerX = size.width / 2
    val centerY = size.height / 2

    // NFC tag (rounded rectangle)
    drawRoundRect(
        color = baseColor,
        topLeft = Offset(centerX - 35f, centerY - 45f),
        size = androidx.compose.ui.geometry.Size(70f, 90f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f),
        style = Stroke(width = 2.5f)
    )

    // NFC waves (top right)
    val waveStroke = Stroke(width = 2f, cap = StrokeCap.Round)
    for (i in 1..2) {
        val offset = i * 8f
        val arc = Path().apply {
            moveTo(centerX + 15f, centerY - 30f)
            cubicTo(
                centerX + 15f + offset, centerY - 30f - offset,
                centerX + 15f + offset, centerY - 30f + offset,
                centerX + 15f, centerY - 30f + (offset * 2)
            )
        }
        drawPath(arc, accentColor, style = waveStroke)
    }

    // Question mark in center
    val qPath = Path().apply {
        // Top curve
        addArc(
            oval = Rect(
                left = centerX - 10f,
                top = centerY - 10f,
                right = centerX + 10f,
                bottom = centerY + 10f
            ),
            startAngleDegrees = 180f,
            sweepAngleDegrees = 180f
        )
        // Vertical line
        lineTo(centerX, centerY + 15f)
    }
    drawPath(qPath, accentColor, style = Stroke(width = 2.5f, cap = StrokeCap.Round))

    // Dot
    drawCircle(
        color = accentColor,
        radius = 2.5f,
        center = Offset(centerX, centerY + 25f)
    )
}

/**
 * Search Empty: Magnifying glass with X
 */
private fun DrawScope.drawSearchEmpty(baseColor: Color, accentColor: Color) {
    val centerX = size.width / 2
    val centerY = size.height / 2

    // Magnifying glass circle
    drawCircle(
        color = baseColor,
        radius = 30f,
        center = Offset(centerX - 10f, centerY - 10f),
        style = Stroke(width = 2.5f)
    )

    // Handle
    drawLine(
        color = baseColor,
        start = Offset(centerX + 15f, centerY + 15f),
        end = Offset(centerX + 35f, centerY + 35f),
        strokeWidth = 2.5f,
        cap = StrokeCap.Round
    )

    // X mark inside circle
    val xSize = 12f
    drawLine(
        color = accentColor,
        start = Offset(centerX - 10f - xSize, centerY - 10f - xSize),
        end = Offset(centerX - 10f + xSize, centerY - 10f + xSize),
        strokeWidth = 2.5f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = accentColor,
        start = Offset(centerX - 10f + xSize, centerY - 10f - xSize),
        end = Offset(centerX - 10f - xSize, centerY - 10f + xSize),
        strokeWidth = 2.5f,
        cap = StrokeCap.Round
    )
}

/**
 * Success: Checkmark in circle
 */
private fun DrawScope.drawSuccess(baseColor: Color, accentColor: Color) {
    val centerX = size.width / 2
    val centerY = size.height / 2

    // Circle
    drawCircle(
        color = accentColor,
        radius = 40f,
        center = Offset(centerX, centerY),
        style = Stroke(width = 3f)
    )

    // Checkmark
    val checkPath = Path().apply {
        moveTo(centerX - 18f, centerY)
        lineTo(centerX - 5f, centerY + 13f)
        lineTo(centerX + 18f, centerY - 13f)
    }
    drawPath(
        path = checkPath,
        color = accentColor,
        style = Stroke(width = 3f, cap = StrokeCap.Round)
    )
}

/**
 * Error: Alert triangle
 */
private fun DrawScope.drawError(baseColor: Color, accentColor: Color) {
    val centerX = size.width / 2
    val centerY = size.height / 2

    // Triangle
    val trianglePath = Path().apply {
        moveTo(centerX, centerY - 40f)
        lineTo(centerX - 40f, centerY + 30f)
        lineTo(centerX + 40f, centerY + 30f)
        close()
    }
    drawPath(
        path = trianglePath,
        color = accentColor,
        style = Stroke(width = 3f)
    )

    // Exclamation mark
    drawLine(
        color = accentColor,
        start = Offset(centerX, centerY - 15f),
        end = Offset(centerX, centerY + 5f),
        strokeWidth = 3f,
        cap = StrokeCap.Round
    )
    drawCircle(
        color = accentColor,
        radius = 3f,
        center = Offset(centerX, centerY + 15f)
    )
}

/**
 * Offline: Cloud with X
 */
private fun DrawScope.drawOffline(baseColor: Color, accentColor: Color) {
    val centerX = size.width / 2
    val centerY = size.height / 2

    // Cloud shape
    val cloudPath = Path().apply {
        // Left bump
        addArc(
            oval = Rect(
                left = centerX - 40f,
                top = centerY - 20f,
                right = centerX - 10f,
                bottom = centerY + 10f
            ),
            startAngleDegrees = 90f,
            sweepAngleDegrees = 180f
        )
        // Top bump
        addArc(
            oval = Rect(
                left = centerX - 20f,
                top = centerY - 35f,
                right = centerX + 20f,
                bottom = centerY - 5f
            ),
            startAngleDegrees = 180f,
            sweepAngleDegrees = 180f
        )
        // Right bump
        addArc(
            oval = Rect(
                left = centerX + 10f,
                top = centerY - 20f,
                right = centerX + 40f,
                bottom = centerY + 10f
            ),
            startAngleDegrees = 270f,
            sweepAngleDegrees = 180f
        )
        // Bottom line
        lineTo(centerX - 40f, centerY + 10f)
        close()
    }
    drawPath(
        path = cloudPath,
        color = baseColor,
        style = Stroke(width = 2.5f)
    )

    // X mark
    val xSize = 15f
    drawLine(
        color = accentColor,
        start = Offset(centerX - xSize, centerY - xSize / 2),
        end = Offset(centerX + xSize, centerY + xSize / 2),
        strokeWidth = 3f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = accentColor,
        start = Offset(centerX + xSize, centerY - xSize / 2),
        end = Offset(centerX - xSize, centerY + xSize / 2),
        strokeWidth = 3f,
        cap = StrokeCap.Round
    )
}
