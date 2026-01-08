package com.umbral.expedition.domain.model

import androidx.compose.ui.geometry.Offset

/**
 * Static data defining the Forest biome map layout.
 * Contains all 15 locations with their positions on the map canvas.
 */
object ForestBiomeData {

    /**
     * Map dimensions in canvas units (normalized 0-1000 for easier positioning)
     */
    const val MAP_WIDTH = 1000f
    const val MAP_HEIGHT = 1000f

    /**
     * Location definition with map position
     */
    data class LocationDefinition(
        val id: String,
        val position: Offset,
        val radius: Float = 30f, // Hit detection radius
        val adjacentTo: List<String> = emptyList() // Which locations this unlocks when discovered
    )

    /**
     * All 15 forest locations positioned on the map
     * Layout forms a path-like structure encouraging exploration
     */
    val locations = listOf(
        // Starting location (center-left)
        LocationDefinition(
            id = "forest_01",
            position = Offset(200f, 500f),
            adjacentTo = listOf("forest_02", "forest_03")
        ),
        // Path 1: Upper branch
        LocationDefinition(
            id = "forest_02",
            position = Offset(300f, 300f),
            adjacentTo = listOf("forest_01", "forest_04", "forest_06")
        ),
        LocationDefinition(
            id = "forest_03",
            position = Offset(350f, 600f),
            adjacentTo = listOf("forest_01", "forest_05")
        ),
        LocationDefinition(
            id = "forest_04",
            position = Offset(450f, 200f),
            adjacentTo = listOf("forest_02", "forest_07")
        ),
        LocationDefinition(
            id = "forest_05",
            position = Offset(500f, 700f),
            adjacentTo = listOf("forest_03", "forest_08")
        ),
        // Path 2: Middle convergence
        LocationDefinition(
            id = "forest_06",
            position = Offset(500f, 400f),
            adjacentTo = listOf("forest_02", "forest_09")
        ),
        LocationDefinition(
            id = "forest_07",
            position = Offset(600f, 150f),
            adjacentTo = listOf("forest_04", "forest_10")
        ),
        LocationDefinition(
            id = "forest_08",
            position = Offset(650f, 750f),
            adjacentTo = listOf("forest_05", "forest_11")
        ),
        // Path 3: Towards center
        LocationDefinition(
            id = "forest_09",
            position = Offset(650f, 450f),
            adjacentTo = listOf("forest_06", "forest_12")
        ),
        LocationDefinition(
            id = "forest_10",
            position = Offset(750f, 250f),
            adjacentTo = listOf("forest_07", "forest_13")
        ),
        LocationDefinition(
            id = "forest_11",
            position = Offset(750f, 650f),
            adjacentTo = listOf("forest_08", "forest_14")
        ),
        // Final convergence to heart
        LocationDefinition(
            id = "forest_12",
            position = Offset(800f, 500f),
            adjacentTo = listOf("forest_09", "forest_15")
        ),
        LocationDefinition(
            id = "forest_13",
            position = Offset(850f, 350f),
            adjacentTo = listOf("forest_10", "forest_15")
        ),
        LocationDefinition(
            id = "forest_14",
            position = Offset(850f, 600f),
            adjacentTo = listOf("forest_11", "forest_15")
        ),
        // Heart of the forest (final location)
        LocationDefinition(
            id = "forest_15",
            position = Offset(900f, 475f),
            adjacentTo = emptyList()
        )
    )

    /**
     * Get location definition by ID
     */
    fun getLocation(id: String): LocationDefinition? {
        return locations.find { it.id == id }
    }

    /**
     * Check if a location is adjacent to any discovered location
     * Used to determine which locations should be visible on the map
     */
    fun isAdjacentToAny(locationId: String, discoveredIds: Set<String>): Boolean {
        // The location itself is discovered
        if (locationId in discoveredIds) return true

        // Check if any discovered location lists this as adjacent
        return locations.any { location ->
            location.id in discoveredIds && locationId in location.adjacentTo
        }
    }

    /**
     * Get all location IDs that should be visible (discovered or adjacent to discovered)
     */
    fun getVisibleLocationIds(discoveredIds: Set<String>): Set<String> {
        // Always show forest_01 as starting point
        if (discoveredIds.isEmpty()) {
            return setOf("forest_01")
        }

        // Show all discovered + their adjacent locations
        val visible = mutableSetOf<String>()
        visible.addAll(discoveredIds)

        discoveredIds.forEach { discoveredId ->
            val location = getLocation(discoveredId)
            location?.adjacentTo?.let { adjacent ->
                visible.addAll(adjacent)
            }
        }

        return visible
    }
}
