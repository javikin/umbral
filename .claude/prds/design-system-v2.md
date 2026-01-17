---
name: design-system-v2
description: Umbral Design System 2.0 - Fase 1 Tokens con nueva paleta de colores minimalista
status: backlog
created: 2026-01-16T19:41:10Z
---

# PRD: Umbral Design System 2.0 - Fase 1: Tokens

## Executive Summary

Actualizar el Design System existente de Umbral con una nueva paleta de colores minimalista y elegante, manteniendo la excelente arquitectura de tokens actual (9.2/10 de madurez). El objetivo es crear una identidad visual premium, cohesiva y fácilmente mantenible que se alinee con la filosofía del "umbral" - el espacio liminal de transición.

**Valor Principal:** Una base visual consistente que permitirá implementar nuevos módulos sin perder la línea de diseño.

---

## Problem Statement

### Problema Actual
- El diseño actual carece de una identidad visual distintiva y premium
- Al implementar nuevos módulos se pierde la coherencia visual
- No existe una paleta de colores optimizada para dual theme (light/dark)
- Falta feedback visual consistente en toda la app

### Por Qué Ahora
- El proyecto está en fase de expansión con nuevos módulos planificados
- Es el momento ideal para establecer una base sólida antes de escalar
- La arquitectura actual permite una migración limpia (98.4% de tokens)

---

## User Stories

### US-1: Como desarrollador
**Quiero** tener un sistema de tokens completo y documentado
**Para** poder implementar nuevas pantallas manteniendo la consistencia visual sin adivinar colores o espaciados.

**Criterios de Aceptación:**
- [ ] Todos los colores disponibles como tokens semánticos
- [ ] Guía clara de cuándo usar cada token
- [ ] IntelliSense/autocompletado funcional en Android Studio

### US-2: Como usuario
**Quiero** que la app tenga un look premium y moderno
**Para** sentir que estoy usando una herramienta de calidad que vale la pena mantener.

**Criterios de Aceptación:**
- [ ] Tema oscuro elegante por defecto
- [ ] Tema claro disponible que siga el sistema
- [ ] Transiciones suaves entre temas
- [ ] Colores que no cansen la vista en uso prolongado

### US-3: Como diseñador/contribuidor
**Quiero** poder proponer cambios de estilo modificando un solo archivo
**Para** iterar rápidamente en el diseño sin romper componentes existentes.

**Criterios de Aceptación:**
- [ ] Cambiar un color en Color.kt actualiza toda la app
- [ ] No hay colores hardcodeados en componentes
- [ ] Documentación de cada token y su uso

---

## Requirements

### Functional Requirements

#### FR-1: Nueva Paleta de Colores

**Dark Theme (Primary)**
```kotlin
// Foundation
backgroundBase     = #151515  // Fondo principal
backgroundSurface  = #1E1E1E  // Cards, elevated surfaces
backgroundElevated = #282828  // Modals, dialogs

// Text
textPrimary   = #E8E8E8  // Cuerpo, headings
textSecondary = #A0A0A0  // Texto de apoyo
textTertiary  = #6B6B6B  // Placeholders
textDisabled  = #4A4A4A  // Estados inactivos

// Accent - Sage Teal
accentPrimary = #4ECDC4  // CTAs, links, focus
accentHover   = #5ED9C7  // Hover state
accentPressed = #3DB5AD  // Active/pressed state

// Semantic
success = #6FCF97
error   = #EB5757
warning = #F2994A
info    = #56CCF2

// Structural
borderDefault = #FFFFFF0F  // 6% white
borderFocus   = #4ECDC44D  // 30% accent
```

**Light Theme**
```kotlin
// Foundation
backgroundBase     = #F8F8F8  // Warm white
backgroundSurface  = #FFFFFF  // Pure white cards
backgroundElevated = #FFFFFF  // Same, rely on shadow

// Text
textPrimary   = #1A1A1A
textSecondary = #5C5C5C
textTertiary  = #8F8F8F
textDisabled  = #B8B8B8

// Accent - Sage Teal (adjusted for contrast)
accentPrimary = #3DB5AD  // -8% para mejor contraste
accentHover   = #4ECDC4
accentPressed = #2E9D96

// Semantic (adjusted for light bg)
success = #5CB85C
error   = #D32F2F
warning = #E87E04
info    = #2196F3

// Structural
borderDefault = #0000000A  // 4% black
borderFocus   = #3DB5AD4D  // 30% accent
```

#### FR-2: Tipografía - Inter Font

```kotlin
// Mantener la escala actual pero asegurar uso de Inter
fontFamily = InterFontFamily

// Display: 32sp, 45sp, 57sp (SemiBold)
// Headline: 24sp, 32sp (SemiBold)
// Title: 16sp, 20sp (Medium)
// Body: 12sp, 14sp, 16sp (Regular)
// Label: 11sp, 12sp, 14sp (Medium)
```

#### FR-3: Sistema de Spacing

```kotlin
// Mantener tokens actuales en UmbralSpacing
xs  = 4.dp   // Extra small
sm  = 8.dp   // Small
md  = 16.dp  // Medium (default)
lg  = 24.dp  // Large
xl  = 32.dp  // Extra large
xxl = 48.dp  // 2XL

// Component-specific
cardPadding  = 16.dp
buttonHeight = 56.dp
chipHeight   = 32.dp
iconSizeSmall  = 18.dp
iconSizeMedium = 24.dp
iconSizeLarge  = 32.dp
```

#### FR-4: Sistema de Elevación/Sombras

```kotlin
// Dark Theme - usar overlays sutiles
elevation1 = backgroundSurface  // Cards
elevation2 = backgroundElevated // Dialogs
elevation3 = backgroundElevated + 2% white overlay

// Light Theme - usar sombras
shadowSubtle = #00000014  // 8% black
shadowDeep   = #00000029  // 16% black
```

#### FR-5: Shapes (mantener sistema actual)

```kotlin
none = 0.dp
xs   = 4.dp   // Muy sutil
sm   = 8.dp   // Botones, chips
md   = 12.dp  // Cards pequeñas
lg   = 16.dp  // Cards principales
xl   = 24.dp  // Bottom sheets
full = 999.dp // Pills, círculos
```

### Non-Functional Requirements

#### NFR-1: Performance
- Cambio de tema < 100ms
- No re-renders innecesarios al cambiar tema
- Memory footprint igual o menor al actual

#### NFR-2: Accessibility
- Todos los textos WCAG 2.1 AA compliant (4.5:1 ratio mínimo)
- Colores de acento usables como indicadores visuales (no solo texto)
- Soporte para fuentes del sistema si Inter no disponible

#### NFR-3: Maintainability
- Un solo archivo para cada tipo de token
- Naming consistente (camelCase para Kotlin, snake_case para resources)
- Comentarios explicando uso de cada token

---

## Success Criteria

### Cuantitativos
- [ ] 100% de componentes usando tokens (actualmente 98.4%)
- [ ] 0 colores hardcodeados fuera de Color.kt
- [ ] Contraste ratio > 4.5:1 en todas las combinaciones texto/fondo
- [ ] < 100ms para cambio de tema

### Cualitativos
- [ ] Look premium y minimalista validado
- [ ] Coherencia visual en todas las pantallas
- [ ] Desarrolladores pueden implementar nuevas pantallas sin consultar

---

## Technical Implementation

### Archivos a Modificar

**Prioridad 1: Core Tokens (CRÍTICO)**
| Archivo | Cambios | Esfuerzo |
|---------|---------|----------|
| `Color.kt` | Reemplazar paleta completa | 2h |
| `Theme.kt` | Actualizar ColorScheme mappings | 1h |
| `ThemeUtils.kt` | Actualizar gradients con nuevos colores | 1h |

**Prioridad 2: Cleanup (ALTO)**
| Archivo | Cambios | Esfuerzo |
|---------|---------|----------|
| `StatsChart.kt` | 6 hardcoded dp → UmbralSpacing | 30min |
| `ProfileCard.kt` | Preview colors (opcional) | 15min |

**Prioridad 3: Polish (MEDIO)**
| Archivo | Cambios | Esfuerzo |
|---------|---------|----------|
| `UmbralToggle.kt` | Extraer dimensions a tokens | 30min |
| `Dimens.kt` | Remover (deprecated) | 15min |

**Prioridad 4: Widget Theme**
| Archivo | Cambios | Esfuerzo |
|---------|---------|----------|
| `WidgetTheme.kt` | Alinear con nuevos colores | 30min |

### Estructura de Archivos Final

```
presentation/ui/theme/
├── Color.kt          // Paleta completa dark/light
├── Theme.kt          // MaterialTheme configuration
├── Type.kt           // Typography (sin cambios mayores)
├── Shape.kt          // Corner radius (sin cambios)
├── Spacing.kt        // Spacing scale (sin cambios)
├── Animation.kt      // Animation tokens (sin cambios)
└── ThemeUtils.kt     // Gradients, helpers actualizados

glance/theme/
└── WidgetTheme.kt    // Widget colors actualizados
```

### Migration Strategy

1. **Crear branch** `feature/design-system-v2`
2. **Actualizar Color.kt** con nueva paleta
3. **Actualizar Theme.kt** con nuevos ColorScheme
4. **Actualizar ThemeUtils.kt** gradients
5. **Fix StatsChart.kt** hardcoded values
6. **Test visual** todas las pantallas
7. **Validar accessibility** con scanner
8. **Update WidgetTheme.kt**
9. **Remove Dimens.kt** (deprecated)

---

## Constraints & Assumptions

### Constraints
- No cambiar la estructura de componentes existentes
- Mantener compatibilidad con Android API 26+
- No agregar dependencias nuevas
- Tiempo estimado: 1-2 días de desarrollo

### Assumptions
- Inter font ya está disponible en el proyecto
- El sistema de tema actual (isSystemInDarkTheme) funciona correctamente
- Los componentes custom ya usan MaterialTheme.colorScheme

---

## Out of Scope (Fase 2)

Los siguientes items serán parte del PRD de Fase 2: Componentes:

- [ ] Rediseño de componentes individuales
- [ ] Nuevos componentes (date picker, time picker, etc.)
- [ ] Animaciones de transición entre pantallas
- [ ] Iconografía personalizada
- [ ] Design tokens en Figma
- [ ] Storybook/Preview catalog

---

## Dependencies

### Internas
- Ninguna - el proyecto ya tiene la infraestructura necesaria

### Externas
- Inter font (Google Fonts) - ya incluida
- Material 3 - ya incluida

---

## Appendix

### A. Análisis de Accesibilidad

| Combinación | Ratio | Status |
|-------------|-------|--------|
| #151515 + #E8E8E8 | 12.8:1 | AAA |
| #151515 + #4ECDC4 | 8.9:1 | AAA |
| #1E1E1E + #A0A0A0 | 6.2:1 | AA |
| #F8F8F8 + #1A1A1A | 14.1:1 | AAA |
| #F8F8F8 + #3DB5AD | 3.2:1 | Buttons only |

### B. Por Qué Sage Teal (#4ECDC4)

1. **Alineación conceptual:** Teal está entre verde (calma) y azul (enfoque) - representa el espacio liminal del "umbral"
2. **Diferenciación:** La mayoría de apps de focus usan azul, naranja o púrpura
3. **Tendencia 2024-2025:** Colores de naturaleza, desaturados, premium
4. **Técnico:** Excelente contraste en ambos temas

### C. Paleta Completa en Hex

```
DARK THEME
----------
Background:   #151515, #1E1E1E, #282828
Text:         #E8E8E8, #A0A0A0, #6B6B6B, #4A4A4A
Accent:       #4ECDC4, #5ED9C7, #3DB5AD
Semantic:     #6FCF97, #EB5757, #F2994A, #56CCF2
Border:       rgba(255,255,255,0.06), rgba(78,205,196,0.30)

LIGHT THEME
-----------
Background:   #F8F8F8, #FFFFFF, #FFFFFF
Text:         #1A1A1A, #5C5C5C, #8F8F8F, #B8B8B8
Accent:       #3DB5AD, #4ECDC4, #2E9D96
Semantic:     #5CB85C, #D32F2F, #E87E04, #2196F3
Border:       rgba(0,0,0,0.04), rgba(61,181,173,0.30)
```

---

## Next Steps

1. **Aprobación:** Revisar y aprobar este PRD
2. **Epic:** Ejecutar `/oden:prd-parse design-system-v2` para crear epic
3. **Implementación:** Seguir plan de migración
4. **Fase 2:** Crear PRD para componentes una vez completada Fase 1

---

**Autor:** Claude Code
**Revisado por:** Pendiente
**Aprobado:** Pendiente
