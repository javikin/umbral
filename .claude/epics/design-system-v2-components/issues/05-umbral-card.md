---
name: umbral-card
description: Redesign UmbralCard with flat style, variants, and UmbralSurface/UmbralDivider
status: open
priority: 1
sprint: 1
estimate: 1 day
created: 2026-01-16T20:02:16Z
github_issue: 92
---

# Issue: Redesign UmbralCard with Flat Style

## Overview

Redesign the card component following the flat design philosophy with subtle borders instead of shadows. Also create UmbralSurface and UmbralDivider utility components.

## PRD Reference

Section A2: Cards y Contenedores (A2.1, A2.2, A2.3)

## Acceptance Criteria

### UmbralCard
- [ ] Implement new API signature:
  - `modifier: Modifier`
  - `onClick: (() -> Unit)?` (null = not clickable)
  - `variant: CardVariant` (Default, Elevated, Outlined, Interactive)
  - `content: @Composable ColumnScope.() -> Unit`
- [ ] Apply visual specifications:
  - Background Dark: `backgroundSurface` (#1E1E1E)
  - Background Light: `backgroundSurface` (#FFFFFF)
  - Border: 1px `borderDefault` (6% white dark / 4% black light)
  - Corner Radius: 16.dp (UmbralShape.lg)
  - Padding: 16.dp (UmbralSpacing.md)
  - No shadow
- [ ] Implement Interactive variant states:
  - Pressed: background +4% lighter, scale 0.99
  - Focused: border `borderFocus` (30% accent)
- [ ] Maintain backward compatibility with existing API

### UmbralSurface
- [ ] Implement API signature:
  - `modifier: Modifier`
  - `elevation: SurfaceElevation` (Level0-Level3)
  - `shape: Shape`
  - `content: @Composable () -> Unit`
- [ ] Implement SurfaceElevation enum:
  - Level0: backgroundBase
  - Level1: backgroundSurface
  - Level2: backgroundElevated
  - Level3: backgroundElevated + 2% overlay

### UmbralDivider
- [ ] Implement API signature:
  - `modifier: Modifier`
  - `variant: DividerVariant` (Full, Inset, Middle)
- [ ] Implement DividerVariant enum:
  - Full: full width
  - Inset: horizontal padding 16.dp
  - Middle: horizontal padding 72.dp (for lists with icons)
- [ ] Specs: height 1.dp, color `borderDefault`

### General
- [ ] Create preview composables for all components and variants
- [ ] Add KDoc documentation

## Visual Reference

```
Card Flat con Borde:
┌─────────────────────────────────────────┐
│ ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░ │ ← 1px border
│ ░                                     ░ │
│ ░   Title                             ░ │
│ ░   Subtitle text here                ░ │
│ ░                                     ░ │
│ ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░ │
└─────────────────────────────────────────┘

Divider Variants:
Full:    ─────────────────────────────────
Inset:      ─────────────────────────
Middle:          ─────────────────
```

## File Locations

- `app/src/main/java/com/umbral/presentation/ui/components/cards/UmbralCard.kt`
- `app/src/main/java/com/umbral/presentation/ui/components/cards/UmbralSurface.kt`
- `app/src/main/java/com/umbral/presentation/ui/components/cards/UmbralDivider.kt`

## Dependencies

- Motion.kt (01-motion-tokens)
- Phase 1 color and shape tokens
