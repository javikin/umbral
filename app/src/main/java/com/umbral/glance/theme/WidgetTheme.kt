package com.umbral.glance.theme

import androidx.compose.ui.graphics.Color
import androidx.glance.unit.ColorProvider
import com.umbral.presentation.ui.theme.*

/**
 * Umbral Widget Theme - Design System 2.0
 *
 * Color Providers for Glance Widgets aligned with Design System 2.0
 * - Uses sage teal accent colors from the new design system
 * - Optimized for light theme (primary use case for home screen widgets)
 * - High contrast ratios for accessibility
 * - Widget will adapt to system theme through Glance Material3
 *
 * Note: Glance 1.1.1 ColorProvider supports single colors only.
 * For theme-aware colors, the widget system uses GlanceTheme.colors.
 */
object WidgetColors {
    // =============================================================================
    // PRIMARY ACCENT - Sage Teal
    // =============================================================================

    /**
     * Primary accent color - sage teal
     * Using light theme variant optimized for home screen visibility
     */
    val primary = ColorProvider(LightAccentPrimary)  // #3DB5AD

    /**
     * Pressed/Active state for accent
     */
    val primaryPressed = ColorProvider(LightAccentPressed)  // #2E9D96

    // =============================================================================
    // SURFACE COLORS
    // =============================================================================

    /**
     * Main widget surface background
     * Pure white for clean appearance on home screen
     */
    val surface = ColorProvider(LightBackgroundSurface)  // #FFFFFF

    /**
     * Secondary surface for nested containers
     * Soft gray for subtle contrast
     */
    val surfaceVariant = ColorProvider(LightBackgroundBase)  // #F8F8F8

    /**
     * Container surface for cards or sections
     */
    val surfaceContainer = ColorProvider(LightBackgroundSurface)  // #FFFFFF

    // =============================================================================
    // BACKGROUND COLORS
    // =============================================================================

    /**
     * Widget background
     */
    val background = ColorProvider(LightBackgroundBase)  // #F8F8F8

    // =============================================================================
    // TEXT COLORS
    // =============================================================================

    /**
     * Primary text - headings and main content
     * Nearly black for high contrast readability
     */
    val onSurface = ColorProvider(LightTextPrimary)  // #1A1A1A

    /**
     * Secondary text - supporting content, subtitles
     * Medium gray for visual hierarchy
     */
    val onSurfaceVariant = ColorProvider(LightTextSecondary)  // #5C5C5C

    /**
     * Text on primary color backgrounds
     * White for maximum contrast on sage teal
     */
    val onPrimary = ColorProvider(Color(0xFFFFFFFF))  // White

    // =============================================================================
    // SEMANTIC COLORS
    // =============================================================================

    /**
     * Success state - active blocking, achievements
     * Green for positive reinforcement
     */
    val success = ColorProvider(LightSuccess)  // #5CB85C

    /**
     * Warning state - needs attention
     * Orange for cautionary states
     */
    val warning = ColorProvider(LightWarning)  // #E87E04

    /**
     * Error state - critical issues
     * Red for errors and destructive actions
     */
    val error = ColorProvider(LightError)  // #D32F2F

    /**
     * Info state - helpful messages
     * Blue for informational content
     */
    val info = ColorProvider(LightInfo)  // #2196F3

    // =============================================================================
    // STREAK/ACHIEVEMENT COLORS
    // =============================================================================

    /**
     * Streak fire effect - orange gradient
     * Preserved from Design System V1 for gamification consistency
     */
    val streak = ColorProvider(StreakFire)  // #F97316

    /**
     * Streak glow effect - softer orange
     * For highlighting streak achievements
     */
    val streakGlow = ColorProvider(StreakFireGlow)  // #FED7AA

    // =============================================================================
    // OUTLINE COLORS
    // =============================================================================

    /**
     * Default outline - subtle borders and dividers
     * Very light for minimal visual weight
     */
    val outline = ColorProvider(LightBorderDefault)  // 4% black

    /**
     * Focus outline - active/focused state borders
     * Sage teal with transparency for focus states
     */
    val outlineVariant = ColorProvider(LightBorderFocus)  // 30% accent
}
