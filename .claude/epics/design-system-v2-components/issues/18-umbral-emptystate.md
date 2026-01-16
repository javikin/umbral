---
name: umbral-emptystate
description: Create UmbralEmptyState component with line art illustrations
status: open
priority: 4
sprint: 4
estimate: 1 day
created: 2026-01-16T20:02:16Z
github_issue: 105
---

# Issue: Create UmbralEmptyState with Illustrations

## Overview

Create an empty state component with minimalist line art illustrations for various empty/error states throughout the app.

## PRD Reference

Section B1: Empty States (B1.1, B1.2)

## Acceptance Criteria

### UmbralEmptyState Component
- [ ] Implement API signature:
  - `illustration: EmptyStateIllustration`
  - `title: String`
  - `description: String`
  - `modifier: Modifier`
  - `action: EmptyStateAction?`
- [ ] Implement EmptyStateIllustration enum:
  - NoProfiles: profile with dotted lines
  - NoApps: empty app grid
  - NoStats: empty graph
  - NoNfc: NFC tag with question mark
  - SearchEmpty: magnifier with X
  - Success: checkmark
  - Error: alert triangle
  - Offline: cloud with X
- [ ] Implement EmptyStateAction data class:
  - `label: String`
  - `onClick: () -> Unit`
- [ ] Apply visual specifications:
  - Illustration: 120x120.dp
  - Illustration Color: `textTertiary` with `accentPrimary` details
  - Title: `titleMedium`, `textPrimary`, centered
  - Description: `bodyMedium`, `textSecondary`, centered, max 2 lines
  - Action: UmbralButton size Small, margin top 24.dp
  - Vertical Spacing: 16.dp between elements

### EmptyStateIllustrations
- [ ] Create all 8 illustrations as Composables using Canvas/Path
- [ ] Illustrations should be line art (monocromatic with accent)
- [ ] Implement `EmptyStateIllustration.Render()` extension function:
  - `modifier: Modifier`
  - `primaryColor: Color` (default textTertiary)
  - `accentColor: Color` (default accentPrimary)

### General
- [ ] Create preview composables for all illustrations
- [ ] Add KDoc documentation

## Visual Reference

```
Empty State Layout:

         ┌─────────────┐
         │             │
         │    ┌───┐    │
         │    │ ? │    │     ← Illustration (120x120)
         │    └───┘    │
         │             │
         └─────────────┘

    No hay perfiles aun          ← Title

  Crea tu primer perfil para     ← Description
  empezar a bloquear apps

      [ + Crear perfil ]         ← Action Button


Illustration Examples (Line Art):

NoProfiles:            NoStats:           SearchEmpty:
    ┌─────────────┐         │                   ◯
    │  ┌─────┐    │         │                  /
    │  │  ?  │    │       ──┼─────────        ✕
    │  └─────┘    │         │
    │ - - - - - - │
    │ - - - - - - │
    └─────────────┘

NoNfc:                 Error:             Success:
    ╭───────────╮         ▲                  ✓
    │  ╭───╮    │        ╱ ╲
    │  │ ? │    │       ╱ ! ╲
    │  ╰───╯    │      ▔▔▔▔▔▔
    ╰───────────╯
```

## Implementation Notes

- Use `Canvas` with `drawPath` for illustrations
- Keep illustrations simple (line art style)
- Accent color for small details/highlights
- Consider using `rememberVectorPainter` for complex paths
- Test illustrations in both dark and light themes

## File Locations

- `app/src/main/java/com/umbral/presentation/ui/components/empty/UmbralEmptyState.kt`
- `app/src/main/java/com/umbral/presentation/ui/components/empty/EmptyStateIllustrations.kt`

## Dependencies

- UmbralButton (02-umbral-button)
- Phase 1 color and typography tokens
