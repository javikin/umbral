---
name: update-theme-utils-kt
description: Actualizar gradientes y utilidades de tema con los nuevos colores
status: open
priority: 1
estimate: 1h
created: 2026-01-16T20:02:16Z
updated: 2026-01-16T20:11:00Z
github_issue: 83
---

# Issue: Update ThemeUtils.kt Gradients

## Description

Actualizar el archivo `ThemeUtils.kt` para usar los nuevos colores de la paleta en gradientes y cualquier otra utilidad de tema. Esto asegura que efectos visuales como fondos con gradiente mantengan la coherencia con el nuevo design system.

## Technical Details

**File:** `app/src/main/java/com/umbral/presentation/ui/theme/ThemeUtils.kt`

### Gradientes a Actualizar

```kotlin
// Dark Theme Gradients
val DarkBackgroundGradient = Brush.verticalGradient(
    colors = listOf(
        DarkBackgroundBase,
        DarkBackgroundSurface
    )
)

val DarkAccentGradient = Brush.horizontalGradient(
    colors = listOf(
        DarkAccentPrimary,
        DarkAccentHover
    )
)

val DarkCardGradient = Brush.verticalGradient(
    colors = listOf(
        DarkBackgroundSurface,
        DarkBackgroundElevated
    )
)

// Light Theme Gradients
val LightBackgroundGradient = Brush.verticalGradient(
    colors = listOf(
        LightBackgroundBase,
        LightBackgroundSurface
    )
)

val LightAccentGradient = Brush.horizontalGradient(
    colors = listOf(
        LightAccentPrimary,
        LightAccentHover
    )
)

val LightCardGradient = Brush.verticalGradient(
    colors = listOf(
        LightBackgroundSurface,
        LightBackgroundElevated
    )
)
```

### Utilidades Composable

```kotlin
@Composable
fun backgroundGradient(): Brush {
    return if (isSystemInDarkTheme()) {
        DarkBackgroundGradient
    } else {
        LightBackgroundGradient
    }
}

@Composable
fun accentGradient(): Brush {
    return if (isSystemInDarkTheme()) {
        DarkAccentGradient
    } else {
        LightAccentGradient
    }
}

@Composable
fun cardGradient(): Brush {
    return if (isSystemInDarkTheme()) {
        DarkCardGradient
    } else {
        LightCardGradient
    }
}
```

## Acceptance Criteria

- [ ] Gradientes de fondo actualizados para dark y light theme
- [ ] Gradientes de acento usando nuevos colores Sage Teal
- [ ] Gradientes de card para superficies elevadas
- [ ] Funciones @Composable para obtener gradiente segun tema actual
- [ ] Comentarios documentando uso de cada gradiente
- [ ] Build exitoso sin errores
- [ ] Gradientes visualmente coherentes con la nueva paleta

## Dependencies

- Issue #01: Update Color.kt with new palette (debe completarse primero)

## Notes

- Los gradientes deben ser sutiles, no llamativos
- Evitar transiciones bruscas entre colores
- Considerar que los gradientes de fondo cubren pantalla completa
- Los gradientes de card son mas sutiles para no distraer

## Testing

1. Compilar proyecto sin errores
2. Verificar gradientes en pantallas que los usen
3. Probar en ambos temas (light/dark)
4. Confirmar transiciones de color suaves
5. Revisar que no hay artefactos visuales en gradientes
