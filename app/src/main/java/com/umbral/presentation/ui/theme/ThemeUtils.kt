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
    // THEME-AWARE GRADIENTS (Design System 2.0)
    // ==========================================================================

    // -------------------------------------------------------------------------
    // Background Gradients
    // -------------------------------------------------------------------------

    /**
     * Dark theme background gradient.
     * Creates subtle depth from base to surface level.
     * Usage: Main app background, full-screen backgrounds
     */
    val DarkBackgroundGradient = Brush.verticalGradient(
        colors = listOf(
            DarkBackgroundBase,
            DarkBackgroundSurface
        )
    )

    /**
     * Light theme background gradient.
     * Creates subtle depth from base to surface level.
     * Usage: Main app background, full-screen backgrounds
     */
    val LightBackgroundGradient = Brush.verticalGradient(
        colors = listOf(
            LightBackgroundBase,
            LightBackgroundSurface
        )
    )

    /**
     * Returns theme-aware background gradient.
     * Automatically switches between light and dark variants.
     */
    @Composable
    fun backgroundGradient(): Brush {
        return if (isSystemInDarkTheme()) {
            DarkBackgroundGradient
        } else {
            LightBackgroundGradient
        }
    }

    // -------------------------------------------------------------------------
    // Accent Gradients (Sage Teal)
    // -------------------------------------------------------------------------

    /**
     * Dark theme accent gradient (Sage Teal).
     * Horizontal gradient from primary to hover state.
     * Usage: Buttons, CTAs, blocking screens, focus states
     */
    val DarkAccentGradient = Brush.horizontalGradient(
        colors = listOf(
            DarkAccentPrimary,
            DarkAccentHover
        )
    )

    /**
     * Light theme accent gradient (Sage Teal).
     * Horizontal gradient from primary to hover state.
     * Usage: Buttons, CTAs, blocking screens, focus states
     */
    val LightAccentGradient = Brush.horizontalGradient(
        colors = listOf(
            LightAccentPrimary,
            LightAccentHover
        )
    )

    /**
     * Returns theme-aware accent gradient.
     * Automatically switches between light and dark variants.
     */
    @Composable
    fun accentGradient(): Brush {
        return if (isSystemInDarkTheme()) {
            DarkAccentGradient
        } else {
            LightAccentGradient
        }
    }

    // -------------------------------------------------------------------------
    // Card Gradients
    // -------------------------------------------------------------------------

    /**
     * Dark theme card gradient.
     * Creates subtle elevation from surface to elevated state.
     * Usage: Cards, dialogs, modals, elevated containers
     */
    val DarkCardGradient = Brush.verticalGradient(
        colors = listOf(
            DarkBackgroundSurface,
            DarkBackgroundElevated
        )
    )

    /**
     * Light theme card gradient.
     * Creates subtle elevation from surface to elevated state.
     * Usage: Cards, dialogs, modals, elevated containers
     */
    val LightCardGradient = Brush.verticalGradient(
        colors = listOf(
            LightBackgroundSurface,
            LightBackgroundElevated
        )
    )

    /**
     * Returns theme-aware card gradient.
     * Automatically switches between light and dark variants.
     */
    @Composable
    fun cardGradient(): Brush {
        return if (isSystemInDarkTheme()) {
            DarkCardGradient
        } else {
            LightCardGradient
        }
    }

    // -------------------------------------------------------------------------
    // Legacy Gradients (Preserved for compatibility)
    // -------------------------------------------------------------------------

    /**
     * Returns the primary gradient colors based on current theme.
     * Used for blocking screens, CTAs, and highlights.
     * @deprecated Use accentGradient() instead for Design System 2.0
     */
    @Composable
    @Deprecated("Use accentGradient() instead", ReplaceWith("accentGradient()"))
    fun primaryGradientColors(): List<Color> {
        return if (isSystemInDarkTheme()) {
            listOf(BlockingGradientStartDark, BlockingGradientEndDark)
        } else {
            listOf(BlockingGradientStart, BlockingGradientEnd)
        }
    }

    /**
     * Returns a horizontal primary gradient brush.
     * @deprecated Use accentGradient() instead for Design System 2.0
     */
    @Composable
    @Deprecated("Use accentGradient() instead", ReplaceWith("accentGradient()"))
    fun primaryGradientBrush(): Brush {
        return Brush.horizontalGradient(primaryGradientColors())
    }

    /**
     * Returns a vertical primary gradient brush.
     * @deprecated Use backgroundGradient() or cardGradient() instead
     */
    @Composable
    @Deprecated("Use backgroundGradient() or cardGradient() instead")
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
        val colors = if (isSystemInDarkTheme()) {
            listOf(DarkAccentPrimary, DarkAccentHover)
        } else {
            listOf(LightAccentPrimary, LightAccentHover)
        }
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
     * Uses Design System 2.0 base background colors.
     */
    @Composable
    fun animatedBackground(): Color {
        return animatedThemeColor(
            lightColor = LightBackgroundBase,
            darkColor = DarkBackgroundBase
        )
    }

    /**
     * Animates the surface color on theme change.
     * Uses Design System 2.0 surface colors.
     */
    @Composable
    fun animatedSurface(): Color {
        return animatedThemeColor(
            lightColor = LightBackgroundSurface,
            darkColor = DarkBackgroundSurface
        )
    }
}

// =============================================================================
// EXTENSION FUNCTIONS
// =============================================================================

/**
 * Returns a surface color with appropriate elevation for the current theme.
 * In light mode, uses shadows. In dark mode, uses tonal elevation.
 * Updated for Design System 2.0.
 */
@Composable
fun surfaceColorAtElevation(elevation: SurfaceElevation): Color {
    val isDark = isSystemInDarkTheme()
    return when (elevation) {
        SurfaceElevation.Level0 -> if (isDark) DarkBackgroundBase else LightBackgroundBase
        SurfaceElevation.Level1 -> if (isDark) DarkBackgroundSurface else LightBackgroundSurface
        SurfaceElevation.Level2 -> if (isDark) DarkBackgroundElevated else LightBackgroundElevated
        SurfaceElevation.Level3 -> if (isDark) DarkBackgroundElevated else LightBackgroundElevated
        SurfaceElevation.Level4 -> if (isDark) DarkBackgroundElevated else LightBackgroundElevated
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
 * Returns a dimmed version of the accent color for subtle highlights.
 * Updated for Design System 2.0 (Sage Teal).
 */
@Composable
fun accentDimmed(): Color {
    return if (isSystemInDarkTheme()) {
        DarkAccentPrimary.copy(alpha = 0.12f)
    } else {
        LightAccentPrimary.copy(alpha = 0.08f)
    }
}

/**
 * Legacy function - use accentDimmed() instead.
 * @deprecated Use accentDimmed() instead for Design System 2.0
 */
@Composable
@Deprecated("Use accentDimmed() instead", ReplaceWith("accentDimmed()"))
fun primaryDimmed(): Color {
    return accentDimmed()
}

/**
 * Returns a color suitable for dividers based on theme.
 * Updated for Design System 2.0.
 */
@Composable
fun dividerColor(): Color {
    return if (isSystemInDarkTheme()) {
        DarkBorderDefault.copy(alpha = 0.8f)
    } else {
        LightBorderDefault.copy(alpha = 0.8f)
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
 * @deprecated Use UmbralThemeUtils.backgroundGradient() instead
 */
@Composable
@Deprecated("Use UmbralThemeUtils.backgroundGradient() instead", ReplaceWith("UmbralThemeUtils.backgroundGradient()"))
fun backgroundGradientBrush(): Brush {
    return UmbralThemeUtils.backgroundGradient()
}

/**
 * Returns a shimmer effect brush adapted for the current theme.
 * Updated for Design System 2.0.
 */
@Composable
fun shimmerBrush(translateX: Float): Brush {
    val isDark = isSystemInDarkTheme()
    val baseColor = if (isDark) DarkBackgroundSurface else LightBackgroundSurface
    val highlightColor = if (isDark) DarkBackgroundElevated else LightBackgroundElevated

    return Brush.linearGradient(
        colors = listOf(baseColor, highlightColor, baseColor),
        start = Offset(translateX, 0f),
        end = Offset(translateX + 500f, 0f)
    )
}
