---
name: umbral-topbar
description: Redesign UmbralTopBar with seamless look and UmbralTabRow
status: open
priority: 2
sprint: 2
estimate: 0.5 days
created: 2026-01-16T20:02:16Z
github_issue: 98
---

# Issue: Redesign UmbralTopBar

## Overview

Redesign the top app bar with a seamless, zero-elevation look and create the UmbralTabRow component for tabbed navigation.

## PRD Reference

Section A4.2: UmbralTopBar and A4.3: UmbralTabRow

## Acceptance Criteria

### UmbralTopBar
- [ ] Implement API signature:
  - `title: String`
  - `modifier: Modifier`
  - `navigationIcon: @Composable (() -> Unit)?`
  - `actions: @Composable RowScope.() -> Unit`
- [ ] Apply visual specifications:
  - Background: `backgroundBase`
  - Height: 64.dp
  - Title Style: `titleLarge`
  - Bottom Border: None (seamless)
  - Elevation: 0
- [ ] Support navigation icon (back arrow typically)
- [ ] Support action icons in trailing position
- [ ] Create preview composables

### UmbralTabRow
- [ ] Implement API signature:
  - `tabs: List<String>`
  - `selectedIndex: Int`
  - `onTabSelected: (Int) -> Unit`
  - `modifier: Modifier`
- [ ] Apply visual specifications:
  - Indicator: pill shape with `accentPrimary` 15% opacity
  - Text unselected: `textSecondary`
  - Text selected: `textPrimary`
  - Tab height: 48.dp
- [ ] Implement indicator animation:
  - Indicator slides with spring animation
  - Smooth width transition between tabs
- [ ] Create preview composables

### General
- [ ] Add KDoc documentation for both components

## Visual Reference

```
UmbralTopBar:
┌─────────────────────────────────────────┐
│  ←   Configuracion              ⚙️  🔔  │
└─────────────────────────────────────────┘
      ↑                           ↑
   nav icon                    actions

UmbralTabRow:
┌─────────────────────────────────────────┐
│   [Perfiles]     Apps       Estadisticas │
│   ~~~~~~~~~~                             │ ← pill indicator
└─────────────────────────────────────────┘
```

## Implementation Notes

- TopBar should integrate with Scaffold
- Consider transparent status bar integration
- TabRow indicator should be a rounded rectangle (pill)
- Use `HorizontalPager` integration if needed

## File Locations

- `app/src/main/java/com/umbral/presentation/ui/components/navigation/UmbralTopBar.kt`
- `app/src/main/java/com/umbral/presentation/ui/components/navigation/UmbralTabRow.kt`

## Dependencies

- Motion.kt (01-motion-tokens)
- Phase 1 color and typography tokens
