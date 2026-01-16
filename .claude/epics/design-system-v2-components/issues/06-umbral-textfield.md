---
name: umbral-textfield
description: Redesign UmbralTextField with floating label, error states, and animations
status: open
priority: 2
sprint: 2
estimate: 0.5 days
created: 2026-01-16T20:02:16Z
github_issue: 93
---

# Issue: Redesign UmbralTextField

## Overview

Redesign the text field component with floating label animation, proper error handling with shake animation, and consistent visual styling.

## PRD Reference

Section A3.1: UmbralTextField

## Acceptance Criteria

- [ ] Implement new API signature:
  - `value: String`
  - `onValueChange: (String) -> Unit`
  - `modifier: Modifier`
  - `label: String?`
  - `placeholder: String?`
  - `leadingIcon: ImageVector?`
  - `trailingIcon: ImageVector?`
  - `error: String?`
  - `enabled: Boolean`
  - `singleLine: Boolean`
  - `keyboardOptions: KeyboardOptions`
- [ ] Apply visual specifications:
  - Background: `backgroundSurface`
  - Border (default): 1px `borderDefault`
  - Border (focused): 2px `accentPrimary`
  - Border (error): 2px `error`
  - Corner Radius: 12.dp (UmbralShape.md)
  - Height: 56.dp
  - Horizontal Padding: 16.dp
  - Label: `textSecondary`, animates up on focus
  - Placeholder: `textTertiary`
  - Input Text: `textPrimary`
- [ ] Implement animations:
  - Label float: tween(150ms, easeOut)
  - Border color: tween(200ms)
  - Error shake: spring with 3 oscillations
- [ ] Display error message below field when error is not null
- [ ] Create preview composables for all states
- [ ] Add KDoc documentation

## Visual Reference

```
Unfocused (empty):
┌──────────────────────────────┐
│  Nombre del perfil           │  ← label/placeholder inside
└──────────────────────────────┘

Focused (with label floating):
   Nombre del perfil              ← label floated up
┌──────────────────────────────┐
│  |                           │  ← cursor, accent border
└──────────────────────────────┘

With Value:
   Nombre del perfil
┌──────────────────────────────┐
│  Mi perfil de trabajo        │
└──────────────────────────────┘

Error State:
   Nombre del perfil
┌──────────────────────────────┐
│  ab                          │  ← red border, shakes
└──────────────────────────────┘
  Minimo 3 caracteres           ← error message
```

## Implementation Notes

- Use `OutlinedTextField` as base or build custom
- Label should smoothly animate position and size
- Error shake should use spring animation for natural feel
- Consider using `derivedStateOf` for efficient recomposition

## File Location

`app/src/main/java/com/umbral/presentation/ui/components/inputs/UmbralTextField.kt`

## Dependencies

- Motion.kt (01-motion-tokens)
- Phase 1 color and typography tokens
