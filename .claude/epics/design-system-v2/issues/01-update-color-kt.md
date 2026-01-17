---
name: update-color-kt
description: Reemplazar paleta de colores completa en Color.kt con nueva identidad visual Sage Teal
status: open
priority: 1
estimate: 2h
created: 2026-01-16T20:02:16Z
updated: 2026-01-16T20:11:00Z
github_issue: 81
---

# Issue: Update Color.kt with New Palette

## Description

Actualizar el archivo `Color.kt` con la nueva paleta de colores minimalista y elegante que define la identidad visual de Umbral 2.0. Esta es la tarea mas critica del epic ya que todos los demas archivos dependen de estos tokens.

## Technical Details

**File:** `app/src/main/java/com/umbral/presentation/ui/theme/Color.kt`

### Dark Theme Colors

```kotlin
// Foundation
val DarkBackgroundBase     = Color(0xFF151515)  // Fondo principal
val DarkBackgroundSurface  = Color(0xFF1E1E1E)  // Cards, elevated surfaces
val DarkBackgroundElevated = Color(0xFF282828)  // Modals, dialogs

// Text
val DarkTextPrimary   = Color(0xFFE8E8E8)  // Cuerpo, headings
val DarkTextSecondary = Color(0xFFA0A0A0)  // Texto de apoyo
val DarkTextTertiary  = Color(0xFF6B6B6B)  // Placeholders
val DarkTextDisabled  = Color(0xFF4A4A4A)  // Estados inactivos

// Accent - Sage Teal
val DarkAccentPrimary = Color(0xFF4ECDC4)  // CTAs, links, focus
val DarkAccentHover   = Color(0xFF5ED9C7)  // Hover state
val DarkAccentPressed = Color(0xFF3DB5AD)  // Active/pressed state

// Semantic
val DarkSuccess = Color(0xFF6FCF97)
val DarkError   = Color(0xFFEB5757)
val DarkWarning = Color(0xFFF2994A)
val DarkInfo    = Color(0xFF56CCF2)

// Structural
val DarkBorderDefault = Color(0x0FFFFFFF)  // 6% white
val DarkBorderFocus   = Color(0x4D4ECDC4)  // 30% accent
```

### Light Theme Colors

```kotlin
// Foundation
val LightBackgroundBase     = Color(0xFFF8F8F8)  // Warm white
val LightBackgroundSurface  = Color(0xFFFFFFFF)  // Pure white cards
val LightBackgroundElevated = Color(0xFFFFFFFF)  // Same, rely on shadow

// Text
val LightTextPrimary   = Color(0xFF1A1A1A)
val LightTextSecondary = Color(0xFF5C5C5C)
val LightTextTertiary  = Color(0xFF8F8F8F)
val LightTextDisabled  = Color(0xFFB8B8B8)

// Accent - Sage Teal (adjusted for contrast)
val LightAccentPrimary = Color(0xFF3DB5AD)  // -8% para mejor contraste
val LightAccentHover   = Color(0xFF4ECDC4)
val LightAccentPressed = Color(0xFF2E9D96)

// Semantic (adjusted for light bg)
val LightSuccess = Color(0xFF5CB85C)
val LightError   = Color(0xFFD32F2F)
val LightWarning = Color(0xFFE87E04)
val LightInfo    = Color(0xFF2196F3)

// Structural
val LightBorderDefault = Color(0x0A000000)  // 4% black
val LightBorderFocus   = Color(0x4D3DB5AD)  // 30% accent
```

## Acceptance Criteria

- [ ] Todos los colores Dark Theme definidos con naming consistente
- [ ] Todos los colores Light Theme definidos con naming consistente
- [ ] Comentarios documentando el uso de cada grupo de colores
- [ ] Colores semanticos (success, error, warning, info) para ambos temas
- [ ] Colores de borde con transparencia correcta
- [ ] Naming convention: `{Theme}{Category}{Variant}` (ej: `DarkTextPrimary`)
- [ ] Remover colores obsoletos que ya no se usen
- [ ] Build exitoso sin errores de compilacion

## Dependencies

- Ninguna - este es el primer issue a completar

## Notes

- Mantener compatibilidad con cualquier uso existente de colores
- Si hay colores en uso que no estan en la nueva paleta, mapearlos al color mas cercano
- Documentar cualquier decision de mapeo en los comentarios

## Testing

1. Verificar que el proyecto compila sin errores
2. Revisar que IntelliSense muestra los nuevos colores
3. Confirmar que los colores son accesibles desde Theme.kt
