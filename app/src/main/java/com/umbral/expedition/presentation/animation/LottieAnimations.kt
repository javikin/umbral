package com.umbral.expedition.presentation.animation

import com.umbral.expedition.domain.model.CompanionType

/**
 * Central registry for all Lottie animation file paths.
 *
 * This object provides:
 * - Consistent animation path references
 * - Type-safe animation lookup
 * - Fallback paths when specific animations don't exist
 */
object LottieAnimations {

    // ========== Companion-specific animations ==========

    /**
     * Get idle animation path for a specific companion type.
     * Falls back to generic idle animation if type-specific one doesn't exist.
     */
    fun getIdleAnimation(companionType: CompanionType): String {
        return when (companionType) {
            CompanionType.LEAF_SPRITE -> "lottie/companion_leaf_sprite_idle.json"
            CompanionType.EMBER_FOX -> "lottie/companion_ember_fox_idle.json"
            CompanionType.AQUA_TURTLE -> "lottie/companion_aqua_turtle_idle.json"
            CompanionType.SKY_BIRD -> "lottie/companion_sky_bird_idle.json"
            CompanionType.STONE_GOLEM -> "lottie/companion_stone_golem_idle.json"
            CompanionType.THUNDER_WOLF -> "lottie/companion_thunder_wolf_idle.json"
            CompanionType.SHADOW_CAT -> "lottie/companion_shadow_cat_idle.json"
            CompanionType.CRYSTAL_DEER -> "lottie/companion_crystal_deer_idle.json"
        }.let { path ->
            // In development, all paths are valid placeholders
            // In production, check if file exists and fallback to generic
            path
        }
    }

    /**
     * Get happy/celebration animation for a companion type.
     * Falls back to generic celebration animation.
     */
    fun getHappyAnimation(companionType: CompanionType): String {
        return when (companionType) {
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

    /**
     * Get evolution animation for a companion type.
     * Falls back to generic evolution animation.
     */
    fun getEvolutionAnimation(companionType: CompanionType): String {
        return when (companionType) {
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

    // ========== Generic animations ==========

    /**
     * Generic idle animation (fallback)
     */
    const val GENERIC_IDLE = "lottie/generic_companion_idle.json"

    /**
     * Generic celebration/happy animation
     */
    const val GENERIC_CELEBRATION = "lottie/generic_celebration.json"

    /**
     * Generic evolution animation with sparkles/glow
     */
    const val GENERIC_EVOLUTION = "lottie/generic_evolution.json"

    /**
     * Capture success animation (stars, confetti, etc.)
     */
    const val CAPTURE_SUCCESS = "lottie/capture_success.json"

    /**
     * Energy investment animation (flowing energy particles)
     */
    const val ENERGY_FLOW = "lottie/energy_flow.json"

    // ========== Helper functions ==========

    /**
     * Get the appropriate animation path based on companion and state.
     * This is the main entry point for animation selection.
     */
    fun getAnimation(
        companionType: CompanionType,
        state: CompanionAnimationState
    ): String {
        return when (state) {
            CompanionAnimationState.IDLE -> getIdleAnimation(companionType)
            CompanionAnimationState.HAPPY -> getHappyAnimation(companionType)
            CompanionAnimationState.EVOLVING -> getEvolutionAnimation(companionType)
        }
    }

    /**
     * Check if we should use a fallback static image instead of Lottie.
     * In development, we'll use placeholders for all animations.
     */
    fun shouldUseFallback(animationPath: String): Boolean {
        // For now, always try to load animation
        // In production, this could check if file exists in assets
        return false
    }
}
