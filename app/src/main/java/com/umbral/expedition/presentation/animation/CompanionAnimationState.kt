package com.umbral.expedition.presentation.animation

/**
 * Represents the different animation states a companion can be in.
 */
enum class CompanionAnimationState {
    /**
     * Companion is idle - loops forever
     */
    IDLE,

    /**
     * Companion is happy/celebrating - plays once
     */
    HAPPY,

    /**
     * Companion is evolving - plays once
     */
    EVOLVING
}
