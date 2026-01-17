---
name: umbral-checkbox
description: Create new UmbralCheckbox component with path drawing animation
status: open
priority: 2
sprint: 2
estimate: 0.5 days
created: 2026-01-16T20:02:16Z
github_issue: 95
---

# Issue: Create UmbralCheckbox

## Overview

Create a checkbox component with smooth check animation (path drawing) and optional label support.

## PRD Reference

Section A3.3: UmbralCheckbox

## Acceptance Criteria

- [ ] Implement API signature:
  - `checked: Boolean`
  - `onCheckedChange: (Boolean) -> Unit`
  - `modifier: Modifier`
  - `enabled: Boolean`
  - `label: String?`
- [ ] Apply visual specifications:
  - Unchecked: border 2px `borderDefault`, background transparent
  - Checked: background `accentPrimary`, checkmark white
  - Indeterminate: background `accentPrimary`, dash white
  - Size: 24.dp x 24.dp
  - Corner radius: 6.dp
- [ ] Implement check animation:
  - Path drawing animation 200ms
  - Scale bounce on check: spring(dampingRatio=0.5)
- [ ] Label should be clickable to toggle checkbox
- [ ] Support indeterminate state (optional enhancement)
- [ ] Create preview composables for all states
- [ ] Add KDoc documentation

## Visual Reference

```
Unchecked:      Checked:        Indeterminate:
┌────┐          ┌────┐          ┌────┐
│    │          │ ✓  │          │ ─  │
└────┘          └────┘          └────┘
border only     filled          filled + dash

With Label:
┌────┐
│ ✓  │  Aceptar terminos y condiciones
└────┘
```

## Animation Sequence

1. User taps checkbox
2. Background fills with accent color (tween 100ms)
3. Checkmark path draws from start to end (200ms)
4. Slight scale bounce (springBouncy)

## Implementation Notes

- Use `Canvas` for custom drawing
- Animate path using `PathMeasure` and `drawPath` with limited length
- Combine with `animateFloatAsState` for smooth transitions
- Ensure minimum touch target 48dp

## File Location

`app/src/main/java/com/umbral/presentation/ui/components/inputs/UmbralCheckbox.kt`

## Dependencies

- Motion.kt (01-motion-tokens)
- Phase 1 color tokens
