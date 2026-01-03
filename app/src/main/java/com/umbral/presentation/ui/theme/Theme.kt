package com.umbral.presentation.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    onPrimary = Purple20,
    primaryContainer = Purple40,
    onPrimaryContainer = Purple80,
    secondary = Teal80,
    onSecondary = Teal20,
    secondaryContainer = Teal40,
    onSecondaryContainer = Teal80,
    tertiary = Amber80,
    background = SurfaceDark,
    surface = SurfaceDark,
    onBackground = OnSurfaceDark,
    onSurface = OnSurfaceDark,
    error = Error
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    onPrimary = Surface,
    primaryContainer = Purple80,
    onPrimaryContainer = Purple20,
    secondary = Teal40,
    onSecondary = Surface,
    secondaryContainer = Teal80,
    onSecondaryContainer = Teal20,
    tertiary = Amber40,
    background = Surface,
    surface = Surface,
    onBackground = OnSurface,
    onSurface = OnSurface,
    error = Error
)

@Composable
fun UmbralTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
