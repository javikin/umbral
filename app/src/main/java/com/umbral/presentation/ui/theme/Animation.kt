package com.umbral.presentation.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically

/**
 * Umbral Design System Animation Constants
 *
 * Consistent animation durations and easings used throughout the app.
 */
object UmbralAnimation {
    // ==========================================================================
    // DURATIONS (in milliseconds)
    // ==========================================================================

    /** Quick animations for immediate feedback (hover, press states) */
    const val Quick = 150

    /** Normal animations for transitions and state changes */
    const val Normal = 300

    /** Slow animations for page transitions and major state changes */
    const val Slow = 500

    /** Stagger delay between items in a list */
    const val StaggerDelay = 50

    // ==========================================================================
    // EASINGS
    // ==========================================================================

    /** Standard easing - for elements entering/exiting screen */
    val StandardEasing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1f)

    /** Decelerate easing - for elements entering the screen */
    val DecelerateEasing = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1f)

    /** Accelerate easing - for elements leaving the screen */
    val AccelerateEasing = CubicBezierEasing(0.4f, 0.0f, 1f, 1f)

    /** Emphasis easing - for attention-grabbing animations */
    val EmphasisEasing = CubicBezierEasing(0.2f, 0.0f, 0f, 1f)

    // ==========================================================================
    // SPRING CONFIGURATIONS
    // ==========================================================================

    /** Bouncy spring for playful interactions */
    object SpringBouncy {
        const val DampingRatio = Spring.DampingRatioMediumBouncy
        const val Stiffness = Spring.StiffnessLow
    }

    /** Snappy spring for responsive interactions */
    object SpringSnappy {
        const val DampingRatio = 0.6f
        const val Stiffness = Spring.StiffnessMedium
    }

    /** Gentle spring for subtle animations */
    object SpringGentle {
        const val DampingRatio = Spring.DampingRatioNoBouncy
        const val Stiffness = Spring.StiffnessLow
    }

    // ==========================================================================
    // ANIMATION SPECS
    // ==========================================================================

    /** Standard fade animation spec */
    val FadeSpec = tween<Float>(
        durationMillis = Normal,
        easing = StandardEasing
    )

    /** Quick fade animation spec */
    val QuickFadeSpec = tween<Float>(
        durationMillis = Quick,
        easing = StandardEasing
    )

    /** Bouncy spring animation spec */
    fun <T> bouncySpring() = spring<T>(
        dampingRatio = SpringBouncy.DampingRatio,
        stiffness = SpringBouncy.Stiffness
    )

    /** Snappy spring animation spec */
    fun <T> snappySpring() = spring<T>(
        dampingRatio = SpringSnappy.DampingRatio,
        stiffness = SpringSnappy.Stiffness
    )

    /** Gentle spring animation spec */
    fun <T> gentleSpring() = spring<T>(
        dampingRatio = SpringGentle.DampingRatio,
        stiffness = SpringGentle.Stiffness
    )

    // ==========================================================================
    // NAVIGATION TRANSITIONS
    // ==========================================================================

    /** Standard enter transition for navigation */
    val NavEnterTransition = fadeIn(
        animationSpec = tween(Normal)
    ) + slideInHorizontally(
        animationSpec = tween(Normal, easing = DecelerateEasing),
        initialOffsetX = { it / 4 }
    )

    /** Standard exit transition for navigation */
    val NavExitTransition = fadeOut(
        animationSpec = tween(Normal)
    ) + slideOutHorizontally(
        animationSpec = tween(Normal, easing = AccelerateEasing),
        targetOffsetX = { -it / 4 }
    )

    /** Pop enter transition (going back) */
    val NavPopEnterTransition = fadeIn(
        animationSpec = tween(Normal)
    ) + slideInHorizontally(
        animationSpec = tween(Normal, easing = DecelerateEasing),
        initialOffsetX = { -it / 4 }
    )

    /** Pop exit transition (going back) */
    val NavPopExitTransition = fadeOut(
        animationSpec = tween(Normal)
    ) + slideOutHorizontally(
        animationSpec = tween(Normal, easing = AccelerateEasing),
        targetOffsetX = { it / 4 }
    )

    // ==========================================================================
    // STAGGERED LIST ANIMATIONS
    // ==========================================================================

    /** Create staggered enter animation for list items */
    fun staggeredEnter(index: Int) = fadeIn(
        animationSpec = tween(
            durationMillis = Normal,
            delayMillis = index * StaggerDelay
        )
    ) + slideInVertically(
        animationSpec = spring(
            dampingRatio = SpringBouncy.DampingRatio,
            stiffness = SpringBouncy.Stiffness
        ),
        initialOffsetY = { 50 }
    )

    /** Create staggered exit animation for list items */
    fun staggeredExit(index: Int) = fadeOut(
        animationSpec = tween(
            durationMillis = Quick,
            delayMillis = index * (StaggerDelay / 2)
        )
    ) + slideOutVertically(
        animationSpec = tween(Quick),
        targetOffsetY = { -30 }
    )
}
