package com.umbral.presentation.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Umbral Design System - Color Palette
 *
 * Primary: Indigo - Confianza, calma, profesional
 * Secondary: Violeta - Premium, único
 * Success: Verde menta - Logro, positivo
 * Warning: Ámbar - Atención suave
 * Error: Rojo - Solo para errores críticos
 */

// =============================================================================
// PRIMARY COLORS
// =============================================================================

val UmbralPrimary = Color(0xFF6366F1)          // Indigo - main brand color
val UmbralPrimaryLight = Color(0xFF818CF8)     // Lighter for dark theme
val UmbralPrimaryDark = Color(0xFF4F46E5)      // Darker for pressed states
val UmbralPrimaryContainer = Color(0xFFE0E7FF) // Light container

val UmbralSecondary = Color(0xFF8B5CF6)        // Violeta - accent
val UmbralSecondaryLight = Color(0xFFA78BFA)   // Lighter for dark theme
val UmbralSecondaryDark = Color(0xFF7C3AED)    // Darker for pressed states
val UmbralSecondaryContainer = Color(0xFFEDE9FE) // Light container

// =============================================================================
// SEMANTIC COLORS
// =============================================================================

val UmbralSuccess = Color(0xFF10B981)          // Verde menta - achievements
val UmbralSuccessLight = Color(0xFF34D399)
val UmbralSuccessContainer = Color(0xFFD1FAE5)

val UmbralWarning = Color(0xFFF59E0B)          // Ámbar - attention
val UmbralWarningLight = Color(0xFFFBBF24)
val UmbralWarningContainer = Color(0xFFFEF3C7)

val UmbralError = Color(0xFFEF4444)            // Rojo - errors only
val UmbralErrorLight = Color(0xFFF87171)
val UmbralErrorContainer = Color(0xFFFEE2E2)

// =============================================================================
// LIGHT THEME COLORS
// =============================================================================

val LightBackground = Color(0xFFFAFAFA)        // Casi blanco
val LightSurface = Color(0xFFFFFFFF)           // Blanco puro
val LightSurfaceVariant = Color(0xFFF1F5F9)    // Gris muy claro
val LightOnBackground = Color(0xFF1F2937)      // Gris oscuro para texto
val LightOnSurface = Color(0xFF1F2937)         // Gris oscuro para texto
val LightOnSurfaceVariant = Color(0xFF6B7280)  // Gris medio para texto secundario
val LightOutline = Color(0xFFE5E7EB)           // Bordes sutiles
val LightOutlineVariant = Color(0xFFD1D5DB)    // Bordes más visibles

// =============================================================================
// DARK THEME COLORS
// =============================================================================

val DarkBackground = Color(0xFF0F172A)         // Azul muy oscuro
val DarkSurface = Color(0xFF1E293B)            // Azul oscuro
val DarkSurfaceVariant = Color(0xFF334155)     // Gris azulado
val DarkOnBackground = Color(0xFFF8FAFC)       // Casi blanco
val DarkOnSurface = Color(0xFFF8FAFC)          // Casi blanco
val DarkOnSurfaceVariant = Color(0xFF94A3B8)   // Gris claro
val DarkOutline = Color(0xFF475569)            // Bordes sutiles
val DarkOutlineVariant = Color(0xFF334155)     // Bordes más visibles

// =============================================================================
// SURFACE CONTAINER COLORS (Material 3 elevation system)
// =============================================================================

// Light theme surface containers (subtle elevation differentiation)
val LightSurfaceContainer = Color(0xFFF5F5F5)      // Slightly elevated
val LightSurfaceContainerLow = Color(0xFFFAFAFA)   // Minimal elevation
val LightSurfaceContainerHigh = Color(0xFFEEEEEE)  // Higher elevation
val LightSurfaceContainerHighest = Color(0xFFE8E8E8)

// Dark theme surface containers (tonal elevation - brighter = higher)
val DarkSurfaceContainer = Color(0xFF243447)       // Slightly elevated
val DarkSurfaceContainerLow = Color(0xFF1A2A3D)    // Minimal elevation
val DarkSurfaceContainerHigh = Color(0xFF2D3F52)   // Higher elevation
val DarkSurfaceContainerHighest = Color(0xFF364A5F)

// =============================================================================
// SPECIAL COLORS (for specific use cases)
// =============================================================================

// Blocking screen - Light theme gradients
val BlockingGradientStart = Color(0xFF6366F1)  // Primary
val BlockingGradientEnd = Color(0xFF8B5CF6)    // Secondary

// Blocking screen - Dark theme gradients (deeper, less saturated)
val BlockingGradientStartDark = Color(0xFF4F46E5)  // Darker primary
val BlockingGradientEndDark = Color(0xFF7C3AED)    // Darker secondary

// Success gradient
val SuccessGradientStart = Color(0xFF10B981)
val SuccessGradientEnd = Color(0xFF34D399)
val SuccessGradientStartDark = Color(0xFF059669)
val SuccessGradientEndDark = Color(0xFF10B981)

// Stats/Achievement gradients
val AchievementGradientStart = Color(0xFFF59E0B)
val AchievementGradientEnd = Color(0xFFF97316)
val AchievementGradientStartDark = Color(0xFFD97706)
val AchievementGradientEndDark = Color(0xFFEA580C)

// Streak fire
val StreakFire = Color(0xFFF97316)             // Orange for fire emoji effect
val StreakFireGlow = Color(0xFFFED7AA)         // Glow effect
val StreakFireDark = Color(0xFFEA580C)
val StreakFireGlowDark = Color(0xFFFDBA74)

// Scrim colors (for overlays and modals)
val ScrimLight = Color(0x80000000)             // 50% black
val ScrimDark = Color(0x99000000)              // 60% black (darker scrim for dark mode)

// Legacy compatibility (gradual migration)
@Deprecated("Use UmbralPrimary instead", ReplaceWith("UmbralPrimary"))
val Purple40 = Color(0xFF6650A4)
@Deprecated("Use UmbralPrimaryLight instead", ReplaceWith("UmbralPrimaryLight"))
val Purple80 = Color(0xFFD0BCFF)
