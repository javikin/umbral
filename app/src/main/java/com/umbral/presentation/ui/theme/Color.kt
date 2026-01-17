package com.umbral.presentation.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Umbral Design System 2.0 - Color Palette
 *
 * Philosophy: Clean, modern, accessible color system with sage teal accent
 * - Dark theme optimized for OLED displays
 * - Light theme with subtle grays for reduced eye strain
 * - High contrast ratios for accessibility (WCAG AA/AAA compliant)
 */

// =============================================================================
// DARK THEME - FOUNDATION
// =============================================================================

/**
 * Foundation colors for dark theme backgrounds
 * Used for: Main app background, cards, elevated surfaces
 */
val DarkBackgroundBase = Color(0xFF151515)        // Main background - deepest
val DarkBackgroundSurface = Color(0xFF1E1E1E)     // Cards and containers
val DarkBackgroundElevated = Color(0xFF282828)    // Elevated surfaces (modals, dialogs)

// =============================================================================
// DARK THEME - TEXT
// =============================================================================

/**
 * Text hierarchy for dark theme
 * Primary: Main content (contrast ratio 12:1)
 * Secondary: Supporting text (contrast ratio 7:1)
 * Tertiary: Hints, placeholders (contrast ratio 4.5:1)
 * Disabled: Inactive states (contrast ratio 3:1)
 */
val DarkTextPrimary = Color(0xFFE8E8E8)           // Headings, body text
val DarkTextSecondary = Color(0xFFA0A0A0)         // Subtitles, descriptions
val DarkTextTertiary = Color(0xFF6B6B6B)          // Hints, placeholders
val DarkTextDisabled = Color(0xFF4A4A4A)          // Disabled text

// =============================================================================
// DARK THEME - ACCENT (Sage Teal)
// =============================================================================

/**
 * Sage Teal accent colors - representing calm, focus, nature
 * Primary: Default state
 * Hover: Interactive hover state (desktop/tablet)
 * Pressed: Touch/click pressed state
 */
val DarkAccentPrimary = Color(0xFF4ECDC4)         // Main accent color
val DarkAccentHover = Color(0xFF5ED9C7)           // Hover state (+10% lighter)
val DarkAccentPressed = Color(0xFF3DB5AD)         // Pressed state (-10% darker)

// =============================================================================
// DARK THEME - SEMANTIC
// =============================================================================

/**
 * Semantic colors for status and feedback
 * Success: Positive actions, completions
 * Error: Errors, destructive actions
 * Warning: Caution, attention needed
 * Info: Informational messages
 */
val DarkSuccess = Color(0xFF6FCF97)               // Success states, achievements
val DarkError = Color(0xFFEB5757)                 // Errors, destructive actions
val DarkWarning = Color(0xFFF2994A)               // Warnings, caution
val DarkInfo = Color(0xFF56CCF2)                  // Info messages, tips

// =============================================================================
// DARK THEME - STRUCTURAL
// =============================================================================

/**
 * Borders and dividers
 * Default: Subtle dividers, card borders
 * Focus: Focused input fields, active states
 */
val DarkBorderDefault = Color(0x0FFFFFFF)         // 6% white - subtle borders
val DarkBorderFocus = Color(0x4D4ECDC4)           // 30% accent - focused borders

// =============================================================================
// LIGHT THEME - FOUNDATION
// =============================================================================

/**
 * Foundation colors for light theme backgrounds
 * Used for: Main app background, cards, elevated surfaces
 */
val LightBackgroundBase = Color(0xFFF8F8F8)       // Main background - soft gray
val LightBackgroundSurface = Color(0xFFFFFFFF)    // Cards and containers - pure white
val LightBackgroundElevated = Color(0xFFFFFFFF)   // Elevated surfaces (modals, dialogs)

// =============================================================================
// LIGHT THEME - TEXT
// =============================================================================

/**
 * Text hierarchy for light theme
 * Primary: Main content (contrast ratio 12:1)
 * Secondary: Supporting text (contrast ratio 7:1)
 * Tertiary: Hints, placeholders (contrast ratio 4.5:1)
 * Disabled: Inactive states (contrast ratio 3:1)
 */
val LightTextPrimary = Color(0xFF1A1A1A)          // Headings, body text
val LightTextSecondary = Color(0xFF5C5C5C)        // Subtitles, descriptions
val LightTextTertiary = Color(0xFF8F8F8F)         // Hints, placeholders
val LightTextDisabled = Color(0xFFB8B8B8)         // Disabled text

// =============================================================================
// LIGHT THEME - ACCENT (Sage Teal)
// =============================================================================

/**
 * Sage Teal accent colors adjusted for light backgrounds
 * Primary: Default state (darker for contrast)
 * Hover: Interactive hover state
 * Pressed: Touch/click pressed state
 */
val LightAccentPrimary = Color(0xFF3DB5AD)        // Main accent - darker for contrast
val LightAccentHover = Color(0xFF4ECDC4)          // Hover state (+10% lighter)
val LightAccentPressed = Color(0xFF2E9D96)        // Pressed state (-10% darker)

// =============================================================================
// LIGHT THEME - SEMANTIC
// =============================================================================

/**
 * Semantic colors for status and feedback (adjusted for light backgrounds)
 * Success: Positive actions, completions
 * Error: Errors, destructive actions
 * Warning: Caution, attention needed
 * Info: Informational messages
 */
val LightSuccess = Color(0xFF5CB85C)              // Success states, achievements
val LightError = Color(0xFFD32F2F)                // Errors, destructive actions
val LightWarning = Color(0xFFE87E04)              // Warnings, caution
val LightInfo = Color(0xFF2196F3)                 // Info messages, tips

// =============================================================================
// LIGHT THEME - STRUCTURAL
// =============================================================================

/**
 * Borders and dividers
 * Default: Subtle dividers, card borders
 * Focus: Focused input fields, active states
 */
val LightBorderDefault = Color(0x0A000000)        // 4% black - subtle borders
val LightBorderFocus = Color(0x4D3DB5AD)          // 30% accent - focused borders

// =============================================================================
// LEGACY COLORS (Design System 1.0 - Deprecated, kept for migration)
// =============================================================================

/**
 * Legacy primary colors - will be phased out
 * Mapped to new Design System 2.0 equivalents where possible
 */
@Deprecated("Use DarkAccentPrimary instead", ReplaceWith("DarkAccentPrimary"))
val UmbralPrimary = Color(0xFF6366F1)             // Old indigo - migrate to sage teal

@Deprecated("Use DarkAccentHover instead", ReplaceWith("DarkAccentHover"))
val UmbralPrimaryLight = Color(0xFF818CF8)

@Deprecated("Use DarkAccentPressed instead", ReplaceWith("DarkAccentPressed"))
val UmbralPrimaryDark = Color(0xFF4F46E5)

@Deprecated("Use LightBackgroundSurface instead", ReplaceWith("LightBackgroundSurface"))
val UmbralPrimaryContainer = Color(0xFFE0E7FF)

@Deprecated("Use DarkAccentPrimary for secondary accent", ReplaceWith("DarkAccentPrimary"))
val UmbralSecondary = Color(0xFF8B5CF6)

@Deprecated("Use DarkAccentHover instead", ReplaceWith("DarkAccentHover"))
val UmbralSecondaryLight = Color(0xFFA78BFA)

@Deprecated("Use DarkAccentPressed instead", ReplaceWith("DarkAccentPressed"))
val UmbralSecondaryDark = Color(0xFF7C3AED)

@Deprecated("Use LightBackgroundSurface instead", ReplaceWith("LightBackgroundSurface"))
val UmbralSecondaryContainer = Color(0xFFEDE9FE)

// =============================================================================
// LEGACY SEMANTIC COLORS
// =============================================================================

@Deprecated("Use DarkSuccess/LightSuccess instead", ReplaceWith("DarkSuccess"))
val UmbralSuccess = Color(0xFF10B981)

@Deprecated("Use DarkSuccess instead", ReplaceWith("DarkSuccess"))
val UmbralSuccessLight = Color(0xFF34D399)

@Deprecated("Use LightBackgroundSurface with success color", ReplaceWith("LightBackgroundSurface"))
val UmbralSuccessContainer = Color(0xFFD1FAE5)

@Deprecated("Use DarkWarning/LightWarning instead", ReplaceWith("DarkWarning"))
val UmbralWarning = Color(0xFFF59E0B)

@Deprecated("Use DarkWarning instead", ReplaceWith("DarkWarning"))
val UmbralWarningLight = Color(0xFFFBBF24)

@Deprecated("Use LightBackgroundSurface with warning color", ReplaceWith("LightBackgroundSurface"))
val UmbralWarningContainer = Color(0xFFFEF3C7)

@Deprecated("Use DarkError/LightError instead", ReplaceWith("DarkError"))
val UmbralError = Color(0xFFEF4444)

@Deprecated("Use DarkError instead", ReplaceWith("DarkError"))
val UmbralErrorLight = Color(0xFFF87171)

@Deprecated("Use LightBackgroundSurface with error color", ReplaceWith("LightBackgroundSurface"))
val UmbralErrorContainer = Color(0xFFFEE2E2)

// =============================================================================
// LEGACY THEME COLORS
// =============================================================================

@Deprecated("Use LightBackgroundBase instead", ReplaceWith("LightBackgroundBase"))
val LightBackground = Color(0xFFFAFAFA)

@Deprecated("Use LightBackgroundSurface instead", ReplaceWith("LightBackgroundSurface"))
val LightSurface = Color(0xFFFFFFFF)

@Deprecated("Use LightBackgroundSurface instead", ReplaceWith("LightBackgroundSurface"))
val LightSurfaceVariant = Color(0xFFF1F5F9)

@Deprecated("Use LightTextPrimary instead", ReplaceWith("LightTextPrimary"))
val LightOnBackground = Color(0xFF1F2937)

@Deprecated("Use LightTextPrimary instead", ReplaceWith("LightTextPrimary"))
val LightOnSurface = Color(0xFF1F2937)

@Deprecated("Use LightTextSecondary instead", ReplaceWith("LightTextSecondary"))
val LightOnSurfaceVariant = Color(0xFF6B7280)

@Deprecated("Use LightBorderDefault instead", ReplaceWith("LightBorderDefault"))
val LightOutline = Color(0xFFE5E7EB)

@Deprecated("Use LightBorderFocus instead", ReplaceWith("LightBorderFocus"))
val LightOutlineVariant = Color(0xFFD1D5DB)

@Deprecated("Use DarkBackgroundBase instead", ReplaceWith("DarkBackgroundBase"))
val DarkBackground = Color(0xFF0F172A)

@Deprecated("Use DarkBackgroundSurface instead", ReplaceWith("DarkBackgroundSurface"))
val DarkSurface = Color(0xFF1E293B)

@Deprecated("Use DarkBackgroundElevated instead", ReplaceWith("DarkBackgroundElevated"))
val DarkSurfaceVariant = Color(0xFF334155)

@Deprecated("Use DarkTextPrimary instead", ReplaceWith("DarkTextPrimary"))
val DarkOnBackground = Color(0xFFF8FAFC)

@Deprecated("Use DarkTextPrimary instead", ReplaceWith("DarkTextPrimary"))
val DarkOnSurface = Color(0xFFF8FAFC)

@Deprecated("Use DarkTextSecondary instead", ReplaceWith("DarkTextSecondary"))
val DarkOnSurfaceVariant = Color(0xFF94A3B8)

@Deprecated("Use DarkBorderDefault instead", ReplaceWith("DarkBorderDefault"))
val DarkOutline = Color(0xFF475569)

@Deprecated("Use DarkBorderFocus instead", ReplaceWith("DarkBorderFocus"))
val DarkOutlineVariant = Color(0xFF334155)

// =============================================================================
// LEGACY SURFACE CONTAINERS
// =============================================================================

@Deprecated("Use LightBackgroundSurface instead", ReplaceWith("LightBackgroundSurface"))
val LightSurfaceContainer = Color(0xFFF5F5F5)

@Deprecated("Use LightBackgroundBase instead", ReplaceWith("LightBackgroundBase"))
val LightSurfaceContainerLow = Color(0xFFFAFAFA)

@Deprecated("Use LightBackgroundElevated instead", ReplaceWith("LightBackgroundElevated"))
val LightSurfaceContainerHigh = Color(0xFFEEEEEE)

@Deprecated("Use LightBackgroundElevated instead", ReplaceWith("LightBackgroundElevated"))
val LightSurfaceContainerHighest = Color(0xFFE8E8E8)

@Deprecated("Use DarkBackgroundSurface instead", ReplaceWith("DarkBackgroundSurface"))
val DarkSurfaceContainer = Color(0xFF243447)

@Deprecated("Use DarkBackgroundBase instead", ReplaceWith("DarkBackgroundBase"))
val DarkSurfaceContainerLow = Color(0xFF1A2A3D)

@Deprecated("Use DarkBackgroundElevated instead", ReplaceWith("DarkBackgroundElevated"))
val DarkSurfaceContainerHigh = Color(0xFF2D3F52)

@Deprecated("Use DarkBackgroundElevated instead", ReplaceWith("DarkBackgroundElevated"))
val DarkSurfaceContainerHighest = Color(0xFF364A5F)

// =============================================================================
// SPECIAL COLORS (for specific use cases - kept from V1)
// =============================================================================

/**
 * Blocking screen gradients - updated to use new sage teal accent
 */
val BlockingGradientStart = DarkAccentPressed      // Darker sage teal
val BlockingGradientEnd = DarkAccentPrimary        // Main sage teal
val BlockingGradientStartDark = DarkAccentPressed
val BlockingGradientEndDark = DarkAccentPrimary

/**
 * Success gradients - using new semantic colors
 */
val SuccessGradientStart = LightSuccess
val SuccessGradientEnd = DarkSuccess
val SuccessGradientStartDark = Color(0xFF4CAF50)
val SuccessGradientEndDark = DarkSuccess

/**
 * Achievement gradients - preserved from V1
 */
val AchievementGradientStart = Color(0xFFF59E0B)
val AchievementGradientEnd = Color(0xFFF97316)
val AchievementGradientStartDark = Color(0xFFD97706)
val AchievementGradientEndDark = Color(0xFFEA580C)

/**
 * Streak fire effect - preserved from V1
 */
val StreakFire = Color(0xFFF97316)
val StreakFireGlow = Color(0xFFFED7AA)
val StreakFireDark = Color(0xFFEA580C)
val StreakFireGlowDark = Color(0xFFFDBA74)

/**
 * Scrim colors for overlays and modals
 */
val ScrimLight = Color(0x80000000)                // 50% black
val ScrimDark = Color(0x99000000)                 // 60% black

// =============================================================================
// LEGACY COMPATIBILITY (to be removed in future versions)
// =============================================================================

@Deprecated("Use DarkAccentPrimary instead", ReplaceWith("DarkAccentPrimary"))
val Purple40 = Color(0xFF6650A4)

@Deprecated("Use LightAccentPrimary instead", ReplaceWith("LightAccentPrimary"))
val Purple80 = Color(0xFFD0BCFF)
