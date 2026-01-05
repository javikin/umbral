package com.umbral.presentation.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Umbral Theme Utilities
 *
 * Provides theme-aware colors, gradients, and utilities for consistent
 * styling across light and dark modes.
 */
object UmbralThemeUtils {

    // ==========================================================================
    // THEME-AWARE GRADIENTS
    // ==========================================================================

    /**
     * Returns the primary gradient colors based on current theme.
     * Used for blocking screens, CTAs, and highlights.
     */
    @Composable
    fun primaryGradientColors(): List<Color> {
        return if (isSystemInDarkTheme()) {
            listOf(BlockingGradientStartDark, BlockingGradientEndDark)
        } else {
            listOf(BlockingGradientStart, BlockingGradientEnd)
        }
    }

    /**
     * Returns a horizontal primary gradient brush.
     */
    @Composable
    fun primaryGradientBrush(): Brush {
        return Brush.horizontalGradient(primaryGradientColors())
    }

    /**
     * Returns a vertical primary gradient brush.
     */
    @Composable
    fun primaryVerticalGradientBrush(): Brush {
        return Brush.verticalGradient(primaryGradientColors())
    }

    /**
     * Returns the success gradient colors based on current theme.
     * Used for achievements, streaks, and positive feedback.
     */
    @Composable
    fun successGradientColors(): List<Color> {
        return if (isSystemInDarkTheme()) {
            listOf(SuccessGradientStartDark, SuccessGradientEndDark)
        } else {
            listOf(SuccessGradientStart, SuccessGradientEnd)
        }
    }

    /**
     * Returns a horizontal success gradient brush.
     */
    @Composable
    fun successGradientBrush(): Brush {
        return Brush.horizontalGradient(successGradientColors())
    }

    /**
     * Returns the achievement gradient colors based on current theme.
     * Used for badges, milestones, and special achievements.
     */
    @Composable
    fun achievementGradientColors(): List<Color> {
        return if (isSystemInDarkTheme()) {
            listOf(AchievementGradientStartDark, AchievementGradientEndDark)
        } else {
            listOf(AchievementGradientStart, AchievementGradientEnd)
        }
    }

    /**
     * Returns a radial gradient brush for spotlight effects.
     */
    @Composable
    fun spotlightGradientBrush(
        center: Offset = Offset.Unspecified,
        radius: Float = Float.POSITIVE_INFINITY
    ): Brush {
        val colors = primaryGradientColors()
        return Brush.radialGradient(
            colors = listOf(colors[0].copy(alpha = 0.3f), Color.Transparent),
            center = center,
            radius = radius
        )
    }

    // ==========================================================================
    // STREAK COLORS
    // ==========================================================================

    /**
     * Returns the streak fire color based on current theme.
     */
    @Composable
    fun streakFireColor(): Color {
        return if (isSystemInDarkTheme()) StreakFireDark else StreakFire
    }

    /**
     * Returns the streak fire glow color based on current theme.
     */
    @Composable
    fun streakFireGlowColor(): Color {
        return if (isSystemInDarkTheme()) StreakFireGlowDark else StreakFireGlow
    }

    // ==========================================================================
    // ANIMATED COLORS
    // ==========================================================================

    /**
     * Animates a color change based on theme transition.
     * Use this for elements that should smoothly transition between themes.
     */
    @Composable
    fun animatedThemeColor(
        lightColor: Color,
        darkColor: Color,
        durationMillis: Int = 300
    ): Color {
        val isDark = isSystemInDarkTheme()
        val animatedColor by animateColorAsState(
            targetValue = if (isDark) darkColor else lightColor,
            animationSpec = tween(durationMillis),
            label = "themeColorAnimation"
        )
        return animatedColor
    }

    /**
     * Animates the background color on theme change.
     */
    @Composable
    fun animatedBackground(): Color {
        return animatedThemeColor(
            lightColor = LightBackground,
            darkColor = DarkBackground
        )
    }

    /**
     * Animates the surface color on theme change.
     */
    @Composable
    fun animatedSurface(): Color {
        return animatedThemeColor(
            lightColor = LightSurface,
            darkColor = DarkSurface
        )
    }
}

// =============================================================================
// EXTENSION FUNCTIONS
// =============================================================================

/**
 * Returns a surface color with appropriate elevation for the current theme.
 * In light mode, uses shadows. In dark mode, uses tonal elevation.
 */
@Composable
fun surfaceColorAtElevation(elevation: SurfaceElevation): Color {
    val isDark = isSystemInDarkTheme()
    return when (elevation) {
        SurfaceElevation.Level0 -> MaterialTheme.colorScheme.surface
        SurfaceElevation.Level1 -> if (isDark) DarkSurfaceContainerLow else MaterialTheme.colorScheme.surface
        SurfaceElevation.Level2 -> if (isDark) DarkSurfaceContainer else MaterialTheme.colorScheme.surfaceContainer
        SurfaceElevation.Level3 -> if (isDark) DarkSurfaceContainerHigh else MaterialTheme.colorScheme.surfaceContainerHigh
        SurfaceElevation.Level4 -> if (isDark) DarkSurfaceContainerHighest else MaterialTheme.colorScheme.surfaceContainerHighest
    }
}

/**
 * Surface elevation levels for Material 3 tonal elevation system.
 */
enum class SurfaceElevation {
    Level0,  // Base surface
    Level1,  // 1dp equivalent
    Level2,  // 3dp equivalent
    Level3,  // 6dp equivalent
    Level4   // 8dp equivalent
}

/**
 * Returns the appropriate text color for a surface at the given elevation.
 * Ensures WCAG AA contrast compliance.
 */
@Composable
fun contentColorFor(surfaceElevation: SurfaceElevation): Color {
    return MaterialTheme.colorScheme.onSurface
}

/**
 * Returns a dimmed version of the primary color for subtle highlights.
 */
@Composable
fun primaryDimmed(): Color {
    return if (isSystemInDarkTheme()) {
        UmbralPrimaryLight.copy(alpha = 0.12f)
    } else {
        UmbralPrimary.copy(alpha = 0.08f)
    }
}

/**
 * Returns a color suitable for dividers based on theme.
 */
@Composable
fun dividerColor(): Color {
    return if (isSystemInDarkTheme()) {
        DarkOutlineVariant.copy(alpha = 0.5f)
    } else {
        LightOutlineVariant.copy(alpha = 0.5f)
    }
}

/**
 * Returns a color suitable for disabled content.
 */
@Composable
fun disabledContentColor(): Color {
    return MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
}

/**
 * Returns a color suitable for disabled containers.
 */
@Composable
fun disabledContainerColor(): Color {
    return MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
}

// =============================================================================
// COMPOSABLE HELPERS
// =============================================================================

/**
 * Provides theme-aware gradient for backgrounds.
 * Creates a subtle gradient effect that works in both themes.
 */
@Composable
fun backgroundGradientBrush(): Brush {
    val isDark = isSystemInDarkTheme()
    return Brush.verticalGradient(
        colors = if (isDark) {
            listOf(
                DarkBackground,
                DarkSurfaceContainerLow
            )
        } else {
            listOf(
                LightBackground,
                LightSurfaceContainerLow
            )
        }
    )
}

/**
 * Returns a shimmer effect brush adapted for the current theme.
 */
@Composable
fun shimmerBrush(translateX: Float): Brush {
    val isDark = isSystemInDarkTheme()
    val baseColor = if (isDark) DarkSurfaceVariant else LightSurfaceVariant
    val highlightColor = if (isDark) DarkSurfaceContainerHigh else LightSurface

    return Brush.linearGradient(
        colors = listOf(baseColor, highlightColor, baseColor),
        start = Offset(translateX, 0f),
        end = Offset(translateX + 500f, 0f)
    )
}
