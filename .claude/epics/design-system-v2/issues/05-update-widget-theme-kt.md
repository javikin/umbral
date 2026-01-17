---
name: update-widget-theme-kt
description: Alinear colores de WidgetTheme.kt con la nueva paleta del design system
status: open
priority: 4
estimate: 30min
created: 2026-01-16T20:02:16Z
updated: 2026-01-16T20:11:00Z
github_issue: 85
---

# Issue: Update WidgetTheme.kt Colors

## Description

Actualizar el archivo `WidgetTheme.kt` para que los widgets de Glance usen la misma paleta de colores que la app principal. Esto asegura coherencia visual entre la app y los widgets del home screen.

## Technical Details

**File:** `app/src/main/java/com/umbral/glance/theme/WidgetTheme.kt` (o ubicacion similar)

### Colores para Widgets (Dark Theme)

```kotlin
object WidgetColors {
    // Dark Theme
    val darkBackground = androidx.glance.color.ColorProvider(
        day = Color(0xFFF8F8F8),  // Light
        night = Color(0xFF151515)  // Dark - BackgroundBase
    )

    val darkSurface = androidx.glance.color.ColorProvider(
        day = Color(0xFFFFFFFF),
        night = Color(0xFF1E1E1E)  // Dark - BackgroundSurface
    )

    val darkTextPrimary = androidx.glance.color.ColorProvider(
        day = Color(0xFF1A1A1A),
        night = Color(0xFFE8E8E8)
    )

    val darkTextSecondary = androidx.glance.color.ColorProvider(
        day = Color(0xFF5C5C5C),
        night = Color(0xFFA0A0A0)
    )

    val accentPrimary = androidx.glance.color.ColorProvider(
        day = Color(0xFF3DB5AD),  // Light accent
        night = Color(0xFF4ECDC4)  // Dark accent - Sage Teal
    )

    val success = androidx.glance.color.ColorProvider(
        day = Color(0xFF5CB85C),
        night = Color(0xFF6FCF97)
    )

    val error = androidx.glance.color.ColorProvider(
        day = Color(0xFFD32F2F),
        night = Color(0xFFEB5757)
    )
}
```

### Widget Theme Object

```kotlin
object WidgetTheme {
    val colors = GlanceTheme.colors.copy(
        primary = WidgetColors.accentPrimary,
        background = WidgetColors.darkBackground,
        surface = WidgetColors.darkSurface,
        onPrimary = WidgetColors.darkTextPrimary,
        onBackground = WidgetColors.darkTextPrimary,
        onSurface = WidgetColors.darkTextPrimary,
        error = WidgetColors.error,
    )
}
```

## Acceptance Criteria

- [ ] WidgetColors object actualizado con nueva paleta
- [ ] ColorProvider configurado para day/night correctamente
- [ ] Colores de fondo alineados con DarkBackgroundBase/Surface
- [ ] Colores de texto alineados con DarkTextPrimary/Secondary
- [ ] Accent color usando Sage Teal (#4ECDC4)
- [ ] Colores semanticos (success, error) actualizados
- [ ] Build exitoso sin errores
- [ ] Widgets visualmente coherentes con la app

## Dependencies

- Issue #01: Update Color.kt with new palette (referencia de colores)

## Notes

- Glance usa su propio sistema de colores (ColorProvider)
- ColorProvider maneja automaticamente light/dark mode
- Los widgets tienen limitaciones visuales comparados con Compose
- Verificar que los colores funcionan bien en diferentes launchers

## Testing

1. Compilar proyecto sin errores
2. Agregar widget al home screen
3. Verificar colores en light mode
4. Verificar colores en dark mode
5. Comparar visualmente con la app principal
6. Probar en diferentes launchers si es posible
