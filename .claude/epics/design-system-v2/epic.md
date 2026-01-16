---
name: design-system-v2
description: Umbral Design System 2.0 - Fase 1 Tokens con nueva paleta de colores minimalista
status: backlog
created: 2026-01-16T20:02:16Z
updated: 2026-01-16T20:11:00Z
prd: design-system-v2
progress: 0%
github_issue: 79
---

# Epic: Design System 2.0 - Fase 1: Tokens

## Overview

Actualizar el Design System existente de Umbral con una nueva paleta de colores minimalista y elegante, manteniendo la excelente arquitectura de tokens actual (9.2/10 de madurez). El objetivo es crear una identidad visual premium, cohesiva y facilmente mantenible que se alinee con la filosofia del "umbral" - el espacio liminal de transicion.

**Valor Principal:** Una base visual consistente que permitira implementar nuevos modulos sin perder la linea de diseno.

## Goals

- [ ] 100% de componentes usando tokens (actualmente 98.4%)
- [ ] 0 colores hardcodeados fuera de Color.kt
- [ ] Contraste ratio > 4.5:1 en todas las combinaciones texto/fondo
- [ ] < 100ms para cambio de tema
- [ ] Look premium y minimalista validado
- [ ] Coherencia visual en todas las pantallas

## Nueva Paleta de Colores

### Dark Theme (Primary)
- **Backgrounds:** #151515 (base), #1E1E1E (surface), #282828 (elevated)
- **Text:** #E8E8E8 (primary), #A0A0A0 (secondary), #6B6B6B (tertiary), #4A4A4A (disabled)
- **Accent (Sage Teal):** #4ECDC4 (primary), #5ED9C7 (hover), #3DB5AD (pressed)
- **Semantic:** #6FCF97 (success), #EB5757 (error), #F2994A (warning), #56CCF2 (info)

### Light Theme
- **Backgrounds:** #F8F8F8 (base), #FFFFFF (surface/elevated)
- **Text:** #1A1A1A (primary), #5C5C5C (secondary), #8F8F8F (tertiary), #B8B8B8 (disabled)
- **Accent (Sage Teal):** #3DB5AD (primary), #4ECDC4 (hover), #2E9D96 (pressed)
- **Semantic:** #5CB85C (success), #D32F2F (error), #E87E04 (warning), #2196F3 (info)

## Issues

| # | Issue | Priority | Estimate | Status |
|---|-------|----------|----------|--------|
| 1 | [Update Color.kt with new palette](issues/01-update-color-kt.md) | P1 | 2h | open |
| 2 | [Update Theme.kt ColorScheme mappings](issues/02-update-theme-kt.md) | P1 | 1h | open |
| 3 | [Update ThemeUtils.kt gradients](issues/03-update-theme-utils-kt.md) | P1 | 1h | open |
| 4 | [Fix StatsChart.kt hardcoded values](issues/04-fix-stats-chart-kt.md) | P2 | 30min | open |
| 5 | [Update WidgetTheme.kt colors](issues/05-update-widget-theme-kt.md) | P4 | 30min | open |
| 6 | [Remove deprecated Dimens.kt](issues/06-remove-dimens-kt.md) | P3 | 15min | open |
| 7 | [Validate accessibility contrast ratios](issues/07-validate-accessibility.md) | QA | 30min | open |

**Total Estimated Effort:** ~6 hours

## Technical Context

### Files to Modify

**Priority 1: Core Tokens (CRITICAL)**
- `presentation/ui/theme/Color.kt` - Replace complete palette
- `presentation/ui/theme/Theme.kt` - Update ColorScheme mappings
- `presentation/ui/theme/ThemeUtils.kt` - Update gradients with new colors

**Priority 2: Cleanup (HIGH)**
- `StatsChart.kt` - 6 hardcoded dp to UmbralSpacing

**Priority 3: Polish (MEDIUM)**
- `Dimens.kt` - Remove (deprecated)

**Priority 4: Widget Theme**
- `glance/theme/WidgetTheme.kt` - Align with new colors

### Migration Strategy

1. Create branch `feature/design-system-v2`
2. Update Color.kt with new palette
3. Update Theme.kt with new ColorScheme
4. Update ThemeUtils.kt gradients
5. Fix StatsChart.kt hardcoded values
6. Test visual on all screens
7. Validate accessibility with scanner
8. Update WidgetTheme.kt
9. Remove Dimens.kt (deprecated)

## Constraints

- No cambiar la estructura de componentes existentes
- Mantener compatibilidad con Android API 26+
- No agregar dependencias nuevas
- Tiempo estimado: 1-2 dias de desarrollo

## Assumptions

- Inter font ya esta disponible en el proyecto
- El sistema de tema actual (isSystemInDarkTheme) funciona correctamente
- Los componentes custom ya usan MaterialTheme.colorScheme

## Out of Scope (Fase 2)

- Rediseno de componentes individuales
- Nuevos componentes (date picker, time picker, etc.)
- Animaciones de transicion entre pantallas
- Iconografia personalizada
- Design tokens en Figma
- Storybook/Preview catalog

---

**PRD:** `.claude/prds/design-system-v2.md`
**Branch:** `feature/design-system-v2`
