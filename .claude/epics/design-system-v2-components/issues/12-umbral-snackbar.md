---
name: umbral-snackbar
description: Create new UmbralSnackbar component with variants and enter/exit animations
status: open
priority: 3
sprint: 3
estimate: 0.5 days
created: 2026-01-16T20:02:16Z
github_issue: 99
---

# Issue: Create UmbralSnackbar

## Overview

Create a snackbar component for displaying temporary messages with semantic variants (success, error, warning) and smooth animations.

## PRD Reference

Section B2.1: UmbralSnackbar

## Acceptance Criteria

- [ ] Implement API signature:
  - `message: String`
  - `modifier: Modifier`
  - `variant: SnackbarVariant` (Default, Success, Error, Warning)
  - `action: SnackbarAction?`
  - `duration: SnackbarDuration` (Short, Medium, Long, Indefinite)
- [ ] Implement SnackbarVariant enum:
  - Default: background surface
  - Success: icon check, border success color
  - Error: icon X, border error color
  - Warning: icon !, border warning color
- [ ] Implement SnackbarDuration enum:
  - Short: 3 seconds
  - Medium: 5 seconds
  - Long: 8 seconds
  - Indefinite: no auto-dismiss
- [ ] Implement SnackbarAction data class:
  - `label: String`
  - `onClick: () -> Unit`
- [ ] Apply visual specifications:
  - Background: `backgroundElevated`
  - Border: 1px of semantic color
  - Corner Radius: 12.dp
  - Padding: 16.dp
  - Icon Size: 20.dp
  - Max Width: 400.dp
  - Position: Bottom, 16.dp margin
- [ ] Implement animations:
  - Enter: slideInVertically + fadeIn, 250ms
  - Exit: slideOutVertically + fadeOut, 200ms
- [ ] Create SnackbarHost composable for managing multiple snackbars
- [ ] Create preview composables for all variants
- [ ] Add KDoc documentation

## Visual Reference

```
Default:
┌──────────────────────────────────────────┐
│  Perfil guardado correctamente    [Undo] │
└──────────────────────────────────────────┘

Success:
┌──────────────────────────────────────────┐
│ ✓  Sesion de bloqueo iniciada           │ ← green border
└──────────────────────────────────────────┘

Error:
┌──────────────────────────────────────────┐
│ ✕  No se pudo conectar con NFC          │ ← red border
└──────────────────────────────────────────┘

Warning:
┌──────────────────────────────────────────┐
│ ⚠  La bateria esta baja                 │ ← amber border
└──────────────────────────────────────────┘
```

## Implementation Notes

- Use `SnackbarHostState` pattern from Material
- Consider queue management for multiple snackbars
- Auto-dismiss using `LaunchedEffect` with delay
- Swipe to dismiss gesture (optional enhancement)

## File Location

`app/src/main/java/com/umbral/presentation/ui/components/feedback/UmbralSnackbar.kt`

## Dependencies

- Motion.kt (01-motion-tokens)
- Phase 1 color tokens
