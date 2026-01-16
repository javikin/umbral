---
name: umbral-text-button
description: Create new UmbralTextButton (Ghost/Secondary) component
status: open
priority: 1
sprint: 1
estimate: 0.5 days
created: 2026-01-16T20:02:16Z
github_issue: 90
---

# Issue: Create UmbralTextButton

## Overview

Create a new ghost/secondary button component for less prominent actions. Follows the filled + ghost design philosophy where primary actions use filled buttons and secondary actions use text buttons.

## PRD Reference

Section A1.2: UmbralTextButton (Ghost/Secondary)

## Acceptance Criteria

- [ ] Implement API signature:
  - `text: String`
  - `onClick: () -> Unit`
  - `modifier: Modifier`
  - `enabled: Boolean`
  - `icon: ImageVector?`
  - `destructive: Boolean` (for delete actions)
- [ ] Apply visual specifications:
  - Background: `transparent`
  - Text Color: `accentPrimary` (or `error` if destructive)
  - Corner Radius: 8.dp
  - Horizontal Padding: 16.dp
  - No border
- [ ] Implement all states:
  - Default: text only in accent color
  - Pressed: background `accentPrimary` 10% opacity
  - Disabled: 40% opacity
- [ ] Support optional leading icon
- [ ] Create preview composables for all variants
- [ ] Add KDoc documentation

## Visual Reference

```
Normal:
   Cancelar     ← accent color text

Destructive:
   Eliminar     ← error color text

With Icon:
   + Agregar    ← icon + text

Pressed State:
┌──────────────┐
│   Cancelar   │  ← subtle background appears
└──────────────┘
```

## Use Cases

- Cancel actions in dialogs
- Secondary actions in forms
- Destructive actions (delete, remove)
- Navigation links that should look like buttons

## Implementation Notes

- Use `Indication` for ripple/press feedback
- Ensure touch target minimum 48.dp height
- Text should be `labelLarge` style
- Icon size should match text height

## File Location

`app/src/main/java/com/umbral/presentation/ui/components/buttons/UmbralTextButton.kt`

## Dependencies

- Motion.kt (01-motion-tokens)
- Phase 1 color tokens
