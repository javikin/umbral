package com.umbral.presentation.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Umbral Design System - Theme
 *
 * Light theme: Clean, minimal, warm whites
 * Dark theme: Deep blue-gray, easy on eyes
 *
 * No dynamic color - we want consistent brand identity
 */

// =============================================================================
// LIGHT COLOR SCHEME
// =============================================================================
private val LightColorScheme = lightColorScheme(
    // Primary
    primary = UmbralPrimary,
    onPrimary = LightSurface,
    primaryContainer = UmbralPrimaryContainer,
    onPrimaryContainer = UmbralPrimaryDark,

    // Secondary
    secondary = UmbralSecondary,
    onSecondary = LightSurface,
    secondaryContainer = UmbralSecondaryContainer,
    onSecondaryContainer = UmbralSecondaryDark,

    // Tertiary (using success color)
    tertiary = UmbralSuccess,
    onTertiary = LightSurface,
    tertiaryContainer = UmbralSuccessContainer,
    onTertiaryContainer = UmbralSuccess,

    // Error
    error = UmbralError,
    onError = LightSurface,
    errorContainer = UmbralErrorContainer,
    onErrorContainer = UmbralError,

    // Background & Surface
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,

    // Surface containers (Material 3 elevation system)
    surfaceContainer = LightSurfaceContainer,
    surfaceContainerLow = LightSurfaceContainerLow,
    surfaceContainerHigh = LightSurfaceContainerHigh,
    surfaceContainerHighest = LightSurfaceContainerHighest,
    surfaceBright = LightSurface,
    surfaceDim = LightSurfaceVariant,

    // Outline
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,

    // Scrim
    scrim = ScrimLight,

    // Inverse (for snackbars, etc.)
    inverseSurface = DarkSurface,
    inverseOnSurface = DarkOnSurface,
    inversePrimary = UmbralPrimaryLight
)

// =============================================================================
// DARK COLOR SCHEME
// =============================================================================
private val DarkColorScheme = darkColorScheme(
    // Primary
    primary = UmbralPrimaryLight,
    onPrimary = DarkBackground,
    primaryContainer = UmbralPrimaryDark,
    onPrimaryContainer = UmbralPrimaryLight,

    // Secondary
    secondary = UmbralSecondaryLight,
    onSecondary = DarkBackground,
    secondaryContainer = UmbralSecondaryDark,
    onSecondaryContainer = UmbralSecondaryLight,

    // Tertiary (using success color)
    tertiary = UmbralSuccessLight,
    onTertiary = DarkBackground,
    tertiaryContainer = UmbralSuccess,
    onTertiaryContainer = UmbralSuccessLight,

    // Error
    error = UmbralErrorLight,
    onError = DarkBackground,
    errorContainer = UmbralError,
    onErrorContainer = UmbralErrorLight,

    // Background & Surface
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,

    // Surface containers (Material 3 tonal elevation - brighter = higher)
    surfaceContainer = DarkSurfaceContainer,
    surfaceContainerLow = DarkSurfaceContainerLow,
    surfaceContainerHigh = DarkSurfaceContainerHigh,
    surfaceContainerHighest = DarkSurfaceContainerHighest,
    surfaceBright = DarkSurfaceContainerHighest,
    surfaceDim = DarkBackground,

    // Outline
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,

    // Scrim
    scrim = ScrimDark,

    // Inverse
    inverseSurface = LightSurface,
    inverseOnSurface = LightOnSurface,
    inversePrimary = UmbralPrimary
)

// =============================================================================
// THEME COMPOSABLE
// =============================================================================

/**
 * Umbral app theme.
 *
 * @param darkTheme Whether to use dark theme. Defaults to system setting.
 * @param content The composable content to theme.
 */
@Composable
fun UmbralTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // Update system bars color
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // Status bar color
            window.statusBarColor = if (darkTheme) {
                DarkBackground.toArgb()
            } else {
                LightBackground.toArgb()
            }

            // Navigation bar color
            window.navigationBarColor = if (darkTheme) {
                DarkBackground.toArgb()
            } else {
                LightBackground.toArgb()
            }

            // Icon colors (light icons on dark bg, dark icons on light bg)
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    // Provide spacing through CompositionLocal
    CompositionLocalProvider(
        LocalUmbralSpacing provides UmbralSpacingValues()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = UmbralTypography,
            shapes = UmbralShapes,
            content = content
        )
    }
}

// =============================================================================
// THEME EXTENSIONS
// =============================================================================

/**
 * Check if current theme is dark
 */
@Composable
fun isUmbralDarkTheme(): Boolean = isSystemInDarkTheme()

/**
 * Get current color scheme based on theme
 */
@Composable
fun umbralColorScheme() = if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme
