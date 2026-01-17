package com.umbral.presentation.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

/**
 * Umbral Design System 2.0 - Animation Tokens
 *
 * Centralized animation specifications for consistent motion across the app.
 *
 * ## Duration Guidelines
 * - instant: No animation (0ms) - for immediate state changes
 * - quick: Micro-interactions (100ms) - press feedback, ripples
 * - fast: Hover states (150ms) - color changes, simple transitions
 * - normal: Standard transitions (250ms) - most UI changes
 * - slow: Page transitions (400ms) - screen navigation
 * - slower: Complex animations (600ms) - multi-step transitions
 *
 * ## Spring Guidelines
 * - springSnappy: Responsive feedback (dampingRatio=0.7f, stiffness=500f)
 * - springBouncy: Playful interactions (dampingRatio=0.5f, stiffness=400f)
 * - springGentle: Smooth movement (dampingRatio=1f, stiffness=200f)
 *
 * ## Easing Guidelines
 * - easeOut: Most common - fast start, slow end (entrances, expansions)
 * - easeIn: Fast end - used for exits, collapses
 * - easeInOut: Symmetric - used for reciprocal movements
 * - emphasis: Dramatic - attention-grabbing animations
 */
object UmbralMotion {

    // ==================== DURATIONS ====================

    /** Instant - no animation (0ms) */
    const val instant = 0

    /** Quick - micro-interactions like press feedback (100ms) */
    const val quick = 100

    /** Fast - hover states, color changes (150ms) */
    const val fast = 150

    /** Normal - standard transitions (250ms) */
    const val normal = 250

    /** Slow - page transitions (400ms) */
    const val slow = 400

    /** Slower - complex animations (600ms) */
    const val slower = 600

    // ==================== SPRINGS ====================

    /** Snappy spring - responsive UI feedback */
    fun <T> springSnappy(): SpringSpec<T> = spring(
        dampingRatio = 0.7f,
        stiffness = 500f
    )

    /** Bouncy spring - playful interactions */
    fun <T> springBouncy(): SpringSpec<T> = spring(
        dampingRatio = 0.5f,
        stiffness = 400f
    )

    /** Gentle spring - smooth, natural movement */
    fun <T> springGentle(): SpringSpec<T> = spring(
        dampingRatio = 1f,
        stiffness = 200f
    )

    // ==================== EASINGS ====================

    /** Ease out - starts fast, ends slow (most common) */
    val easeOut = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)

    /** Ease in - starts slow, ends fast (exits) */
    val easeIn = CubicBezierEasing(0.4f, 0.0f, 1.0f, 1.0f)

    /** Ease in-out - slow start and end (symmetric) */
    val easeInOut = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)

    /** Emphasis - dramatic, attention-grabbing */
    val emphasis = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)

    // ==================== TWEEN SPECS ====================

    fun tweenQuick(easing: Easing = easeOut): TweenSpec<Float> =
        tween(durationMillis = quick, easing = easing)

    fun tweenFast(easing: Easing = easeOut): TweenSpec<Float> =
        tween(durationMillis = fast, easing = easing)

    fun tweenNormal(easing: Easing = easeOut): TweenSpec<Float> =
        tween(durationMillis = normal, easing = easing)

    fun tweenSlow(easing: Easing = easeOut): TweenSpec<Float> =
        tween(durationMillis = slow, easing = easing)

    // ==================== SCREEN TRANSITIONS ====================

    /** Enter transition - fade in + slide from right */
    val enterTransition = fadeIn(
        animationSpec = tween(normal, easing = easeOut)
    ) + slideInHorizontally(
        initialOffsetX = { it / 4 },
        animationSpec = tween(normal, easing = easeOut)
    )

    /** Exit transition - fade out + slide to left */
    val exitTransition = fadeOut(
        animationSpec = tween(fast, easing = easeIn)
    ) + slideOutHorizontally(
        targetOffsetX = { it / 4 },
        animationSpec = tween(fast, easing = easeIn)
    )
}
