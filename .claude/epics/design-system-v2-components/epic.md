---
name: design-system-v2-components
description: Umbral Design System 2.0 - Fase 2 Componentes con rediseño completo y nuevos elementos UI
status: backlog
created: 2026-01-16T20:02:16Z
updated: 2026-01-16T20:02:16Z
prd: design-system-v2-components
depends_on: design-system-v2
total_issues: 21
estimated_days: 12-15
github_issue: 80
---

# Epic: Umbral Design System 2.0 - Fase 2: Componentes

## Overview

Rediseno completo de la biblioteca de componentes de Umbral siguiendo los tokens establecidos en Fase 1. Incluye mejora de componentes existentes (botones, cards, inputs, navegacion) y creacion de nuevos componentes esenciales (empty states, feedback visual, data display, skeletons).

**Objetivo:** Lograr una experiencia premium, minimalista y cohesiva en toda la app.

**Dependencia Critica:** Este epic requiere la implementacion previa de `design-system-v2` (Fase 1: Tokens).

---

## Problem Statement

### Problema Actual
- Los componentes actuales fueron creados incrementalmente sin una vision unificada
- Falta de componentes esenciales para UX completa (empty states, skeletons)
- No existe un catalogo visual para verificar consistencia
- Las micro-interacciones no estan estandarizadas

### Por Que Ahora
- Fase 1 (Tokens) establece la base visual necesaria
- Momento ideal para refactorizar antes de nuevos modulos
- Inversion que pagara dividendos en velocidad de desarrollo futura

---

## Sprint Breakdown

### Sprint 1: Foundation (3-4 dias)
| Issue | Descripcion | Prioridad |
|-------|-------------|-----------|
| 01-motion-tokens | Create Motion.kt animation tokens | 1 |
| 02-umbral-button | Redesign UmbralButton | 1 |
| 03-umbral-text-button | Create UmbralTextButton | 1 |
| 04-umbral-icon-button | Redesign UmbralIconButton | 1 |
| 05-umbral-card | Redesign UmbralCard with flat style | 1 |

### Sprint 2: Inputs & Navigation (3-4 dias)
| Issue | Descripcion | Prioridad |
|-------|-------------|-----------|
| 06-umbral-textfield | Redesign UmbralTextField | 2 |
| 07-umbral-searchfield | Create UmbralSearchField | 2 |
| 08-umbral-checkbox | Create UmbralCheckbox | 2 |
| 09-umbral-switch | Redesign UmbralSwitch (from Toggle) | 2 |
| 10-umbral-bottombar | Redesign UmbralBottomBar | 2 |
| 11-umbral-topbar | Redesign UmbralTopBar | 2 |

### Sprint 3: New Components (3-4 dias)
| Issue | Descripcion | Prioridad |
|-------|-------------|-----------|
| 12-umbral-snackbar | Create UmbralSnackbar | 3 |
| 13-umbral-toast | Create UmbralToast | 3 |
| 14-umbral-progress | Improve UmbralProgressIndicator | 3 |
| 15-umbral-badge | Create UmbralBadge | 3 |
| 16-umbral-tag | Create UmbralTag | 3 |
| 17-umbral-avatar | Create UmbralAvatar | 3 |

### Sprint 4: Polish (2-3 dias)
| Issue | Descripcion | Prioridad |
|-------|-------------|-----------|
| 18-umbral-emptystate | Create UmbralEmptyState with illustrations | 4 |
| 19-umbral-skeleton | Create UmbralSkeleton system | 4 |
| 20-component-catalog | Create ComponentCatalogScreen | 4 |
| 21-integration-testing | Integration testing and adjustments | 4 |

---

## File Structure

```
presentation/ui/components/
├── buttons/
│   ├── UmbralButton.kt
│   ├── UmbralTextButton.kt
│   └── UmbralIconButton.kt
├── cards/
│   ├── UmbralCard.kt
│   ├── UmbralSurface.kt
│   └── UmbralDivider.kt
├── inputs/
│   ├── UmbralTextField.kt
│   ├── UmbralSearchField.kt
│   ├── UmbralCheckbox.kt
│   └── UmbralSwitch.kt
├── navigation/
│   ├── UmbralBottomBar.kt
│   ├── UmbralTopBar.kt
│   └── UmbralTabRow.kt
├── feedback/
│   ├── UmbralSnackbar.kt
│   ├── UmbralToast.kt
│   └── UmbralProgressIndicator.kt
├── display/
│   ├── UmbralBadge.kt
│   ├── UmbralTag.kt
│   ├── UmbralAvatar.kt
│   └── UmbralListItem.kt
├── empty/
│   ├── UmbralEmptyState.kt
│   └── EmptyStateIllustrations.kt
├── skeleton/
│   ├── UmbralSkeleton.kt
│   └── SkeletonPresets.kt
└── catalog/
    └── ComponentCatalogScreen.kt

presentation/ui/theme/
├── Motion.kt  // New animation tokens
└── [existing files]
```

---

## Success Criteria

### Quantitative
- [ ] 100% componentes con preview funcional
- [ ] 0 animaciones que causen frame drops
- [ ] Catalogo muestra 100% de componentes
- [ ] Touch targets >= 48dp en todos los interactivos

### Qualitative
- [ ] Look premium y cohesivo validado
- [ ] Animaciones se sienten naturales
- [ ] Desarrolladores pueden usar componentes sin documentacion externa

---

## Non-Functional Requirements

### NFR-1: Performance
- Animaciones a 60fps minimo en dispositivos mid-range
- Skeleton shimmer no debe causar battery drain
- Lazy loading para ComponentCatalog

### NFR-2: Accessibility
- Todos los componentes soportan TalkBack
- Respetar "Reduce Motion" del sistema
- Focus indicators visibles (2px accent border)
- Touch targets minimo 48x48.dp

### NFR-3: Maintainability
- Un archivo por componente
- Previews para cada estado
- Documentacion KDoc completa

---

## Issues Directory

All issues are located in `./issues/` with naming pattern `{number}-{component-name}.md`
