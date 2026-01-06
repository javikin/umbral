package com.umbral.glance.theme

import androidx.compose.ui.graphics.Color
import androidx.glance.unit.ColorProvider

/**
 * Umbral Widget Theme - Color Providers for Glance Widgets
 *
 * Uses ColorProvider with single colors for simplicity.
 * Widget will automatically adapt to system theme through Glance Material3.
 * Colors are aligned with the main app theme for consistency.
 */
object WidgetColors {
    // Primary brand colors (using light theme versions)
    val primary = ColorProvider(Color(0xFF6366F1))  // UmbralPrimary
    val secondary = ColorProvider(Color(0xFF8B5CF6))  // UmbralSecondary

    // Surface colors
    val surface = ColorProvider(Color(0xFFFFFFFF))  // Will be overridden by Material3 theme
    val surfaceVariant = ColorProvider(Color(0xFFF1F5F9))
    val surfaceContainer = ColorProvider(Color(0xFFF5F5F5))

    // Background colors
    val background = ColorProvider(Color(0xFFFAFAFA))

    // Text colors
    val onSurface = ColorProvider(Color(0xFF1F2937))
    val onSurfaceVariant = ColorProvider(Color(0xFF6B7280))
    val onPrimary = ColorProvider(Color(0xFFFFFFFF))

    // Semantic colors
    val success = ColorProvider(Color(0xFF10B981))  // UmbralSuccess
    val warning = ColorProvider(Color(0xFFF59E0B))  // UmbralWarning
    val error = ColorProvider(Color(0xFFEF4444))  // UmbralError

    // Streak/Achievement colors
    val streak = ColorProvider(Color(0xFFF97316))  // StreakFire
    val streakGlow = ColorProvider(Color(0xFFFED7AA))  // StreakFireGlow

    // Outline colors
    val outline = ColorProvider(Color(0xFFE5E7EB))
    val outlineVariant = ColorProvider(Color(0xFFD1D5DB))
}
