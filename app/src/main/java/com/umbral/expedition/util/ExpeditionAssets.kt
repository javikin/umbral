package com.umbral.expedition.util

import androidx.annotation.DrawableRes
import com.umbral.R
import com.umbral.expedition.domain.model.CompanionType
import com.umbral.expedition.presentation.animation.CompanionAnimationState

/**
 * Central asset registry for all expedition-related visual resources.
 *
 * This object provides:
 * - Consistent asset path references
 * - Type-safe asset lookup
 * - Fallback assets when specific resources don't exist
 * - Easy migration path when adding real assets
 */
object ExpeditionAssets {

    // ========== Companion Images (Static) ==========

    /**
     * Get static companion image drawable resource.
     *
     * @param type The companion type
     * @param evolutionState Evolution level (0 = base, 1 = evolved, 2 = final)
     * @return Drawable resource ID for the companion image
     */
    @DrawableRes
    fun companionImage(type: CompanionType, evolutionState: Int): Int {
        // For now, return placeholder for all companions
        // In production, this would map to specific drawable resources
        // Example:
        // return when (type) {
        //     CompanionType.LEAF_SPRITE -> when (evolutionState) {
        //         0 -> R.drawable.companion_leaf_sprite_base
        //         1 -> R.drawable.companion_leaf_sprite_evolved
        //         2 -> R.drawable.companion_leaf_sprite_final
        //         else -> R.drawable.companion_placeholder
        //     }
        //     CompanionType.EMBER_FOX -> ...
        //     else -> R.drawable.companion_placeholder
        // }

        return R.drawable.companion_placeholder
    }

    /**
     * Get element-themed placeholder for companion.
     * Useful for showing locked companions with element hints.
     */
    @DrawableRes
    fun companionElementPlaceholder(type: CompanionType): Int {
        // Could return element-specific placeholders in the future
        return R.drawable.companion_placeholder
    }

    // ========== Location Images ==========

    /**
     * Get location background image for a specific location ID.
     *
     * @param locationId The unique location identifier (e.g., "forest_01")
     * @return Drawable resource ID for the location background
     */
    @DrawableRes
    fun locationImage(locationId: String): Int {
        // For now, return single placeholder for all locations
        // In production, this would map to location-specific images
        // Example:
        // return when (locationId) {
        //     "forest_01" -> R.drawable.location_forest_01
        //     "forest_02" -> R.drawable.location_forest_02
        //     ...
        //     else -> R.drawable.location_placeholder
        // }

        return R.drawable.location_placeholder
    }

    /**
     * Get biome-themed placeholder for undiscovered locations.
     * Shows a fog/mystery version of the location.
     */
    @DrawableRes
    fun locationFoggedImage(): Int {
        return R.drawable.location_placeholder
    }

    // ========== Lottie Animations ==========

    /**
     * Get Lottie animation file path for companion animations.
     *
     * @param type The companion type
     * @param state The animation state (idle, happy, evolving)
     * @return Path to Lottie JSON file in assets folder
     */
    fun companionLottie(type: CompanionType, state: CompanionAnimationState): String {
        return when (state) {
            CompanionAnimationState.IDLE -> getIdleLottie(type)
            CompanionAnimationState.HAPPY -> getHappyLottie(type)
            CompanionAnimationState.EVOLVING -> getEvolvingLottie(type)
        }
    }

    private fun getIdleLottie(type: CompanionType): String {
        // Map each companion to its idle animation
        // For now, return generic placeholders
        // In production, return type-specific animations:
        // "lottie/companion_${type.id}_idle.json"

        return when (type) {
            CompanionType.LEAF_SPRITE -> "lottie/companion_leaf_sprite_idle.json"
            CompanionType.EMBER_FOX -> "lottie/companion_ember_fox_idle.json"
            CompanionType.AQUA_TURTLE -> "lottie/companion_aqua_turtle_idle.json"
            CompanionType.SKY_BIRD -> "lottie/companion_sky_bird_idle.json"
            CompanionType.STONE_GOLEM -> "lottie/companion_stone_golem_idle.json"
            CompanionType.THUNDER_WOLF -> "lottie/companion_thunder_wolf_idle.json"
            CompanionType.SHADOW_CAT -> "lottie/companion_shadow_cat_idle.json"
            CompanionType.CRYSTAL_DEER -> "lottie/companion_crystal_deer_idle.json"
        }
    }

    private fun getHappyLottie(type: CompanionType): String {
        return when (type) {
            CompanionType.LEAF_SPRITE -> "lottie/companion_leaf_sprite_happy.json"
            CompanionType.EMBER_FOX -> "lottie/companion_ember_fox_happy.json"
            CompanionType.AQUA_TURTLE -> "lottie/companion_aqua_turtle_happy.json"
            CompanionType.SKY_BIRD -> "lottie/companion_sky_bird_happy.json"
            CompanionType.STONE_GOLEM -> "lottie/companion_stone_golem_happy.json"
            CompanionType.THUNDER_WOLF -> "lottie/companion_thunder_wolf_happy.json"
            CompanionType.SHADOW_CAT -> "lottie/companion_shadow_cat_happy.json"
            CompanionType.CRYSTAL_DEER -> "lottie/companion_crystal_deer_happy.json"
        }
    }

    private fun getEvolvingLottie(type: CompanionType): String {
        return when (type) {
            CompanionType.LEAF_SPRITE -> "lottie/companion_leaf_sprite_evolve.json"
            CompanionType.EMBER_FOX -> "lottie/companion_ember_fox_evolve.json"
            CompanionType.AQUA_TURTLE -> "lottie/companion_aqua_turtle_evolve.json"
            CompanionType.SKY_BIRD -> "lottie/companion_sky_bird_evolve.json"
            CompanionType.STONE_GOLEM -> "lottie/companion_stone_golem_evolve.json"
            CompanionType.THUNDER_WOLF -> "lottie/companion_thunder_wolf_evolve.json"
            CompanionType.SHADOW_CAT -> "lottie/companion_shadow_cat_evolve.json"
            CompanionType.CRYSTAL_DEER -> "lottie/companion_crystal_deer_evolve.json"
        }
    }

    /**
     * Generic Lottie animations (not companion-specific)
     */
    object Generic {
        const val CAPTURE_SUCCESS = "lottie/capture_success.json"
        const val ENERGY_FLOW = "lottie/energy_flow.json"
        const val LEVEL_UP = "lottie/level_up.json"
        const val ACHIEVEMENT_UNLOCK = "lottie/achievement_unlock.json"
    }

    // ========== Asset Migration Helper ==========

    /**
     * Check if a real asset exists for a given companion.
     * Useful for development when not all assets are ready.
     *
     * In production, this could check if the file exists in drawable
     * or assets folder and return true/false.
     *
     * For now, always returns false (use placeholders).
     */
    fun hasRealAsset(type: CompanionType): Boolean {
        // Return false to always use placeholders during development
        // When real assets are added, implement actual checking:
        // val resourceId = companionImage(type, 0)
        // return resourceId != R.drawable.companion_placeholder
        return false
    }

    /**
     * Check if a real Lottie animation exists.
     */
    fun hasRealAnimation(type: CompanionType, state: CompanionAnimationState): Boolean {
        // Return false to use fallback static images
        // When Lottie files are added, return true
        return false
    }
}
