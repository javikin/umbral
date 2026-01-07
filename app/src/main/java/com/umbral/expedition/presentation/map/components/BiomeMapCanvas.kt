package com.umbral.expedition.presentation.map.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.umbral.expedition.domain.model.ForestBiomeData
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Canvas component that renders the biome map with fog of war and interactive locations.
 *
 * Features:
 * - Pan gesture to move around the map
 * - Pinch-to-zoom for closer inspection
 * - Fog of war on undiscovered areas
 * - Visual distinction between discovered and available locations
 * - Tap detection for location selection
 */
@Composable
fun BiomeMapCanvas(
    discoveredLocationIds: Set<String>,
    visibleLocationIds: Set<String>,
    onLocationTap: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Transform state for pan and zoom
    var offset by remember { mutableStateOf(Offset.Zero) }
    var scale by remember { mutableFloatStateOf(1f) }

    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(0.5f, 3f)

        // Apply pan with scale consideration
        offset += panChange
    }

    // Location tap detection
    var tappedLocation by remember { mutableStateOf<String?>(null) }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .transformable(state = transformState)
            .pointerInput(Unit) {
                detectTapGestures { tapOffset ->
                    // Convert screen tap to map coordinates
                    val canvasWidth = size.width.toFloat()
                    val canvasHeight = size.height.toFloat()

                    // Account for centering offset
                    val centerOffsetX = (canvasWidth - ForestBiomeData.MAP_WIDTH * scale) / 2f
                    val centerOffsetY = (canvasHeight - ForestBiomeData.MAP_HEIGHT * scale) / 2f

                    val mapX = (tapOffset.x - offset.x - centerOffsetX) / scale
                    val mapY = (tapOffset.y - offset.y - centerOffsetY) / scale

                    // Find which location was tapped
                    val tapped = findLocationAtPosition(
                        position = Offset(mapX, mapY),
                        visibleIds = visibleLocationIds
                    )

                    tapped?.let { locationId ->
                        onLocationTap(locationId)
                    }
                }
            }
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Center the map on canvas
        val centerOffsetX = (canvasWidth - ForestBiomeData.MAP_WIDTH * scale) / 2f
        val centerOffsetY = (canvasHeight - ForestBiomeData.MAP_HEIGHT * scale) / 2f

        translate(offset.x + centerOffsetX, offset.y + centerOffsetY) {
            scale(scale, scale, Offset.Zero) {
                // 1. Draw map background
                drawMapBackground()

                // 2. Draw connection paths between discovered locations
                drawConnectionPaths(discoveredLocationIds)

                // 3. Draw fog of war over undiscovered areas
                drawFogOfWar(discoveredLocationIds)

                // 4. Draw location markers
                drawLocationMarkers(discoveredLocationIds, visibleLocationIds)
            }
        }
    }
}

/**
 * Draw the forest biome background
 */
private fun DrawScope.drawMapBackground() {
    // Forest green gradient background
    drawRect(
        color = Color(0xFF1B5E20), // Dark forest green
        size = androidx.compose.ui.geometry.Size(
            ForestBiomeData.MAP_WIDTH,
            ForestBiomeData.MAP_HEIGHT
        )
    )

    // Add some texture (lighter green patches)
    drawCircle(
        color = Color(0xFF2E7D32),
        radius = 150f,
        center = Offset(300f, 300f),
        alpha = 0.3f
    )
    drawCircle(
        color = Color(0xFF2E7D32),
        radius = 180f,
        center = Offset(700f, 600f),
        alpha = 0.3f
    )
    drawCircle(
        color = Color(0xFF2E7D32),
        radius = 120f,
        center = Offset(500f, 700f),
        alpha = 0.3f
    )
}

/**
 * Draw connection paths between adjacent discovered locations
 */
private fun DrawScope.drawConnectionPaths(discoveredIds: Set<String>) {
    discoveredIds.forEach { locationId ->
        val location = ForestBiomeData.getLocation(locationId) ?: return@forEach

        location.adjacentTo.forEach { adjacentId ->
            // Only draw if both locations are discovered
            if (adjacentId in discoveredIds) {
                val adjacentLocation = ForestBiomeData.getLocation(adjacentId) ?: return@forEach

                // Draw connecting line
                drawLine(
                    color = Color(0xFF81C784), // Light green
                    start = location.position,
                    end = adjacentLocation.position,
                    strokeWidth = 3.dp.toPx(),
                    alpha = 0.6f
                )
            }
        }
    }
}

/**
 * Draw fog of war over undiscovered regions
 */
private fun DrawScope.drawFogOfWar(discoveredIds: Set<String>) {
    // Create fog layer
    val fogColor = Color(0xFF000000)

    // Draw fog over entire map
    drawRect(
        color = fogColor,
        size = androidx.compose.ui.geometry.Size(
            ForestBiomeData.MAP_WIDTH,
            ForestBiomeData.MAP_HEIGHT
        ),
        alpha = 0.7f
    )

    // Clear fog around discovered locations
    discoveredIds.forEach { locationId ->
        val location = ForestBiomeData.getLocation(locationId) ?: return@forEach

        // Draw a clear circle around discovered location
        drawCircle(
            color = Color.Transparent,
            radius = 100f, // Fog-free radius
            center = location.position,
            blendMode = androidx.compose.ui.graphics.BlendMode.Clear
        )
    }
}

/**
 * Draw location markers
 */
private fun DrawScope.drawLocationMarkers(
    discoveredIds: Set<String>,
    visibleIds: Set<String>
) {
    visibleIds.forEach { locationId ->
        val location = ForestBiomeData.getLocation(locationId) ?: return@forEach
        val isDiscovered = locationId in discoveredIds

        if (isDiscovered) {
            // Discovered location: green filled circle
            drawCircle(
                color = Color(0xFF4CAF50), // Green
                radius = 20f,
                center = location.position
            )
            drawCircle(
                color = Color.White,
                radius = 20f,
                center = location.position,
                style = Stroke(width = 3f)
            )
        } else {
            // Available but not discovered: orange outline
            drawCircle(
                color = Color(0xFFFF9800), // Orange
                radius = 18f,
                center = location.position,
                style = Stroke(width = 4f)
            )
            // Pulsing inner circle
            drawCircle(
                color = Color(0xFFFF9800),
                radius = 8f,
                center = location.position,
                alpha = 0.5f
            )
        }
    }
}

/**
 * Find which location (if any) was tapped
 */
private fun findLocationAtPosition(
    position: Offset,
    visibleIds: Set<String>
): String? {
    return ForestBiomeData.locations
        .filter { it.id in visibleIds }
        .find { location ->
            val distance = sqrt(
                (position.x - location.position.x).pow(2) +
                (position.y - location.position.y).pow(2)
            )
            distance <= location.radius
        }?.id
}
