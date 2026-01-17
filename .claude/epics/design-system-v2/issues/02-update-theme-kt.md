---
name: update-theme-kt
description: Actualizar mappings de ColorScheme en Theme.kt con la nueva paleta de colores
status: open
priority: 1
estimate: 1h
created: 2026-01-16T20:02:16Z
updated: 2026-01-16T20:11:00Z
github_issue: 82
---

# Issue: Update Theme.kt ColorScheme Mappings

## Description

Actualizar el archivo `Theme.kt` para mapear los nuevos colores definidos en `Color.kt` al sistema `ColorScheme` de Material 3. Esto asegura que todos los componentes que usan `MaterialTheme.colorScheme` reciban los colores correctos automaticamente.

## Technical Details

**File:** `app/src/main/java/com/umbral/presentation/ui/theme/Theme.kt`

### Dark Color Scheme Mapping

```kotlin
private val DarkColorScheme = darkColorScheme(
    // Primary colors
    primary = DarkAccentPrimary,
    onPrimary = DarkBackgroundBase,
    primaryContainer = DarkAccentPressed,
    onPrimaryContainer = DarkTextPrimary,

    // Secondary colors (using accent variants)
    secondary = DarkAccentHover,
    onSecondary = DarkBackgroundBase,
    secondaryContainer = DarkBackgroundElevated,
    onSecondaryContainer = DarkTextPrimary,

    // Tertiary (info color)
    tertiary = DarkInfo,
    onTertiary = DarkBackgroundBase,
    tertiaryContainer = DarkBackgroundElevated,
    onTertiaryContainer = DarkInfo,

    // Background & Surface
    background = DarkBackgroundBase,
    onBackground = DarkTextPrimary,
    surface = DarkBackgroundSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkBackgroundElevated,
    onSurfaceVariant = DarkTextSecondary,

    // Outline
    outline = DarkBorderDefault,
    outlineVariant = DarkBorderFocus,

    // Error
    error = DarkError,
    onError = DarkBackgroundBase,
    errorContainer = DarkError.copy(alpha = 0.12f),
    onErrorContainer = DarkError,

    // Inverse
    inverseSurface = LightBackgroundSurface,
    inverseOnSurface = LightTextPrimary,
    inversePrimary = LightAccentPrimary,

    // Scrim
    scrim = Color.Black.copy(alpha = 0.32f),
)
```

### Light Color Scheme Mapping

```kotlin
private val LightColorScheme = lightColorScheme(
    // Primary colors
    primary = LightAccentPrimary,
    onPrimary = Color.White,
    primaryContainer = LightAccentHover,
    onPrimaryContainer = LightTextPrimary,

    // Secondary colors
    secondary = LightAccentHover,
    onSecondary = LightTextPrimary,
    secondaryContainer = LightBackgroundSurface,
    onSecondaryContainer = LightTextPrimary,

    // Tertiary (info color)
    tertiary = LightInfo,
    onTertiary = Color.White,
    tertiaryContainer = LightBackgroundSurface,
    onTertiaryContainer = LightInfo,

    // Background & Surface
    background = LightBackgroundBase,
    onBackground = LightTextPrimary,
    surface = LightBackgroundSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightBackgroundElevated,
    onSurfaceVariant = LightTextSecondary,

    // Outline
    outline = LightBorderDefault,
    outlineVariant = LightBorderFocus,

    // Error
    error = LightError,
    onError = Color.White,
    errorContainer = LightError.copy(alpha = 0.12f),
    onErrorContainer = LightError,

    // Inverse
    inverseSurface = DarkBackgroundSurface,
    inverseOnSurface = DarkTextPrimary,
    inversePrimary = DarkAccentPrimary,

    // Scrim
    scrim = Color.Black.copy(alpha = 0.32f),
)
```

## Acceptance Criteria

- [ ] DarkColorScheme actualizado con todos los nuevos colores
- [ ] LightColorScheme actualizado con todos los nuevos colores
- [ ] Mappings semanticos correctos (primary, secondary, error, etc.)
- [ ] Colores de superficie y fondo correctamente asignados
- [ ] Colores de texto (onBackground, onSurface, etc.) correctos
- [ ] Outline y outlineVariant usando colores de borde
- [ ] Scrim con transparencia apropiada
- [ ] Build exitoso sin errores
- [ ] Tema cambia correctamente entre light/dark

## Dependencies

- Issue #01: Update Color.kt with new palette (debe completarse primero)

## Notes

- Los mappings deben seguir las convenciones de Material 3
- Considerar que `surfaceVariant` se usa para elementos ligeramente elevados
- `outline` es para bordes default, `outlineVariant` para estados de focus

## Testing

1. Compilar proyecto y verificar sin errores
2. Probar cambio de tema (light/dark) en dispositivo/emulador
3. Verificar que componentes Material reciben colores correctos
4. Confirmar que cards, buttons, text fields usan la nueva paleta
