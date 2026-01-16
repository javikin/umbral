---
name: umbral-switch
description: Redesign UmbralSwitch (formerly UmbralToggle) with new specs and animations
status: open
priority: 2
sprint: 2
estimate: 0.5 days
created: 2026-01-16T20:02:16Z
github_issue: 96
---

# Issue: Redesign UmbralSwitch

## Overview

Redesign the toggle component (renamed to UmbralSwitch) with updated visual specifications and smooth spring animations.

## PRD Reference

Section A3.4: UmbralSwitch (rediseno de UmbralToggle)

## Acceptance Criteria

- [ ] Rename component from UmbralToggle to UmbralSwitch
- [ ] Implement API signature:
  - `checked: Boolean`
  - `onCheckedChange: (Boolean) -> Unit`
  - `modifier: Modifier`
  - `enabled: Boolean`
  - `label: String?`
- [ ] Apply visual specifications:
  - Track Width: 52.dp
  - Track Height: 32.dp
  - Track BG (off): `borderDefault` (12% opacity)
  - Track BG (on): `accentPrimary`
  - Thumb Color (off): `textSecondary`
  - Thumb Color (on): `#151515`
  - Thumb Size: 28.dp
- [ ] Implement animations:
  - Thumb position: spring(dampingRatio=0.6, stiffness=400)
  - Track color: tween(200ms)
- [ ] Label should be clickable to toggle switch
- [ ] Disabled state: 40% opacity
- [ ] Create preview composables for all states
- [ ] Add KDoc documentation
- [ ] Deprecate UmbralToggle with alias to UmbralSwitch

## Visual Reference

```
Off:                          On:
┌──────────────────────┐      ┌──────────────────────┐
│ ●                    │      │                    ● │
└──────────────────────┘      └──────────────────────┘
 subtle track                  accent track

With Label:
┌──────────────────────┐
│                    ● │  Notificaciones activas
└──────────────────────┘
```

## Animation Details

- Thumb slides with spring physics for natural feel
- Track color crossfades smoothly
- Combined with slight thumb scale on press (0.95)

## Migration Notes

- UmbralToggle should remain as deprecated alias
- Update all usages in codebase to use UmbralSwitch
- Maintain same functionality, only visual changes

## File Location

`app/src/main/java/com/umbral/presentation/ui/components/inputs/UmbralSwitch.kt`

## Dependencies

- Motion.kt (01-motion-tokens)
- Phase 1 color tokens
