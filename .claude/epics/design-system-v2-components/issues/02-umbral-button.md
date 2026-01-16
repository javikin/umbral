---
name: umbral-button
description: Redesign UmbralButton (Primary) with new visual specs and animations
status: open
priority: 1
sprint: 1
estimate: 0.5 days
created: 2026-01-16T20:02:16Z
github_issue: 89
---

# Issue: Redesign UmbralButton

## Overview

Redesign the primary button component with new visual specifications, size variants, loading states, and animations following the filled + ghost philosophy.

## PRD Reference

Section A1.1: UmbralButton (Primary)

## Acceptance Criteria

- [ ] Implement new API signature with all parameters:
  - `text: String`
  - `onClick: () -> Unit`
  - `modifier: Modifier`
  - `enabled: Boolean`
  - `loading: Boolean`
  - `icon: ImageVector?`
  - `size: ButtonSize` (Small, Medium, Large)
- [ ] Implement ButtonSize enum with correct dimensions:
  - Small: height 36.dp, text labelMedium
  - Medium: height 48.dp, text labelLarge
  - Large: height 56.dp, text titleSmall
- [ ] Apply visual specifications:
  - Background: `accentPrimary` (#4ECDC4 dark / #3DB5AD light)
  - Text Color: `#151515` (always dark for contrast)
  - Corner Radius: 8.dp (UmbralShape.sm)
  - Horizontal Padding: 24.dp
  - No border, no shadow (flat design)
- [ ] Implement all states with animations:
  - Default: as specified
  - Pressed: `accentPressed`, scale 0.98, spring(0.6, 500)
  - Hover: `accentHover`, tween(150ms, easeOut)
  - Disabled: 40% opacity
  - Loading: spinner replaces text, crossfade(200ms)
- [ ] Support optional leading icon
- [ ] Maintain backward compatibility with existing API if possible
- [ ] Create preview composables for all sizes and states
- [ ] Add KDoc documentation

## Visual Reference

```
┌──────────────────────┐
│     Guardar          │  ← Medium (default)
└──────────────────────┘

┌────────────────┐
│   Cancelar     │  ← Small
└────────────────┘

┌──────────────────────────┐
│      Continuar           │  ← Large
└──────────────────────────┘

┌──────────────────────┐
│   🔄  (spinner)      │  ← Loading state
└──────────────────────┘
```

## Implementation Notes

- Use `animateColorAsState` for color transitions
- Use `graphicsLayer` for scale animations
- Loading spinner should be same height as text
- Ensure touch target minimum 48x48.dp even for Small size

## File Location

`app/src/main/java/com/umbral/presentation/ui/components/buttons/UmbralButton.kt`

## Dependencies

- Motion.kt (01-motion-tokens)
- Phase 1 color tokens
