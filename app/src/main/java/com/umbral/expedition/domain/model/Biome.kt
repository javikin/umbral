package com.umbral.expedition.domain.model

/**
 * Represents different biomes in the expedition map.
 * V1 only includes FOREST, future updates will add more biomes.
 */
enum class Biome(
    val id: String,
    val displayName: String,
    val description: String,
    val totalLocations: Int,
    val primaryColor: String // Hex color for UI theming
) {
    FOREST(
        id = "forest",
        displayName = "Bosque Místico",
        description = "Un denso bosque lleno de criaturas mágicas y secretos antiguos",
        totalLocations = 15,
        primaryColor = "#2E7D32" // Dark green
    );

    companion object {
        /**
         * Get biome by ID
         */
        fun fromId(id: String): Biome? = values().find { it.id == id }

        /**
         * Get default starting biome
         */
        fun getStartingBiome(): Biome = FOREST
    }
}
