package com.umbral.expedition.presentation.animation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.umbral.expedition.domain.model.CompanionType

/**
 * Composable that displays a Lottie animation for a companion.
 *
 * Features:
 * - Loads appropriate animation based on companion type and state
 * - IDLE state loops forever
 * - HAPPY and EVOLVING states play once
 * - Falls back to static star icon if animation fails to load
 * - Provides colored gradient background based on companion element
 *
 * @param companionType The type of companion to animate
 * @param animationState The current animation state (IDLE, HAPPY, EVOLVING)
 * @param backgroundColor Background color (from companion element)
 * @param height Height of the animation container
 * @param onAnimationComplete Callback when non-looping animation completes (HAPPY, EVOLVING)
 */
@Composable
fun CompanionAnimation(
    companionType: CompanionType,
    animationState: CompanionAnimationState,
    backgroundColor: Color,
    height: Dp = 250.dp,
    onAnimationComplete: (() -> Unit)? = null
) {
    // Get the animation path
    val animationPath = LottieAnimations.getAnimation(companionType, animationState)

    // Load the Lottie composition
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset(animationPath)
    )

    // Determine if this should loop
    val iterations = when (animationState) {
        CompanionAnimationState.IDLE -> LottieConstants.IterateForever
        CompanionAnimationState.HAPPY,
        CompanionAnimationState.EVOLVING -> 1
    }

    // Animate the composition
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = iterations,
        isPlaying = true,
        restartOnPlay = true
    )

    // Notify when animation completes (for non-looping animations)
    LaunchedEffect(progress) {
        if (progress == 1f && animationState != CompanionAnimationState.IDLE) {
            onAnimationComplete?.invoke()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        backgroundColor.copy(alpha = 0.6f),
                        backgroundColor.copy(alpha = 0.2f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        if (composition != null) {
            // Show Lottie animation
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height * 0.8f)
            )
        } else {
            // Fallback to static star icon
            FallbackCompanionIcon(
                evolutionState = 1, // Default to state 1 for fallback
                color = backgroundColor
            )
        }
    }
}

/**
 * Fallback static icon when Lottie animation fails to load.
 * Shows stars based on evolution state.
 */
@Composable
private fun FallbackCompanionIcon(
    evolutionState: Int,
    color: Color
) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        repeat(evolutionState.coerceIn(1, 3)) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(64.dp)
            )
        }
    }
}
