package com.umbral.presentation.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * Umbral Theme - Material Design 3
 *
 * Paleta de colores inspirada en naturaleza y transiciones (metaxy).
 * Soporte completo para Material You dynamic colors.
 */

// ========================================
// Light Color Palette
// ========================================

private val FocusSky = Color(0xFFE8F4F8)          // Background suave
private val DeepFocus = Color(0xFF0A4D68)         // Primary oscuro
private val FocusLeaf = Color(0xFF4CAF50)         // Success/tertiary
private val FocusAmber = Color(0xFFFFA726)        // Warning
private val FocusSurface = Color(0xFFFFFBFE)      // Surface
private val FocusSurfaceVariant = Color(0xFFE7F2F5) // Surface variant

private val LightColorScheme = lightColorScheme(
    primary = DeepFocus,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF7FB3D5),
    onPrimaryContainer = Color(0xFF00344D),

    secondary = Color(0xFF52606D),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD6E4F0),
    onSecondaryContainer = Color(0xFF0F1D27),

    tertiary = FocusLeaf,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFC8E6C9),
    onTertiaryContainer = Color(0xFF1B5E20),

    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    background = FocusSky,
    onBackground = Color(0xFF191C1D),

    surface = FocusSurface,
    onSurface = Color(0xFF191C1D),
    surfaceVariant = FocusSurfaceVariant,
    onSurfaceVariant = Color(0xFF40484C),

    outline = Color(0xFF70787D),
    outlineVariant = Color(0xFFC0C8CD),

    scrim = Color.Black,

    inverseSurface = Color(0xFF2E3133),
    inverseOnSurface = Color(0xFFEFF1F2),
    inversePrimary = Color(0xFF7FB3D5)
)

// ========================================
// Dark Color Palette
// ========================================

private val NightSky = Color(0xFF0D1B2A)          // Background oscuro
private val MoonGlow = Color(0xFF415A77)          // Primary claro
private val NightLeaf = Color(0xFF66BB6A)         // Success
private val StarLight = Color(0xFFE0E1DD)         // Texto

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF7FB3D5),
    onPrimary = Color(0xFF00344D),
    primaryContainer = MoonGlow,
    onPrimaryContainer = Color(0xFFCAE6FF),

    secondary = Color(0xFFBAC8DA),
    onSecondary = Color(0xFF24323F),
    secondaryContainer = Color(0xFF3B4856),
    onSecondaryContainer = Color(0xFFD6E4F0),

    tertiary = NightLeaf,
    onTertiary = Color(0xFF1B5E20),
    tertiaryContainer = Color(0xFF2E7D32),
    onTertiaryContainer = Color(0xFFC8E6C9),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    background = NightSky,
    onBackground = StarLight,

    surface = Color(0xFF1B263B),
    onSurface = StarLight,
    surfaceVariant = Color(0xFF415A77),
    onSurfaceVariant = Color(0xFFC0C8CD),

    outline = Color(0xFF8A9297),
    outlineVariant = Color(0xFF40484C),

    scrim = Color.Black,

    inverseSurface = Color(0xFFE0E3E3),
    inverseOnSurface = Color(0xFF2E3133),
    inversePrimary = DeepFocus
)

// ========================================
// Typography
// ========================================

private val UmbralTypography = Typography(
    // Display
    displayLarge = Typography().displayLarge.copy(
        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
    ),
    displayMedium = Typography().displayMedium.copy(
        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
    ),
    displaySmall = Typography().displaySmall.copy(
        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
    ),

    // Headline
    headlineLarge = Typography().headlineLarge.copy(
        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
    ),
    headlineMedium = Typography().headlineMedium.copy(
        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
    ),
    headlineSmall = Typography().headlineSmall.copy(
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
    ),

    // Title
    titleLarge = Typography().titleLarge.copy(
        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
    ),
    titleMedium = Typography().titleMedium.copy(
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
    ),
    titleSmall = Typography().titleSmall.copy(
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
    ),

    // Body
    bodyLarge = Typography().bodyLarge.copy(
        fontWeight = androidx.compose.ui.text.font.FontWeight.Normal
    ),
    bodyMedium = Typography().bodyMedium.copy(
        fontWeight = androidx.compose.ui.text.font.FontWeight.Normal
    ),
    bodySmall = Typography().bodySmall.copy(
        fontWeight = androidx.compose.ui.text.font.FontWeight.Normal
    ),

    // Label
    labelLarge = Typography().labelLarge.copy(
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
    ),
    labelMedium = Typography().labelMedium.copy(
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
    ),
    labelSmall = Typography().labelSmall.copy(
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
    )
)

// ========================================
// Shapes
// ========================================

private val UmbralShapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
    small = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
)

// ========================================
// Theme Composable
// ========================================

@Composable
fun UmbralTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true, // Material You support
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Android 12+ con dynamic colors
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        // Fallback a colores estáticos
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = UmbralTypography,
        shapes = UmbralShapes,
        content = content
    )
}

// ========================================
// Preview Helpers
// ========================================

@Composable
fun UmbralThemePreview(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    UmbralTheme(
        darkTheme = darkTheme,
        dynamicColor = dynamicColor,
        content = content
    )
}
