---
name: umbral-bottombar
description: Redesign UmbralBottomBar with icon-only style and animated indicator
status: open
priority: 2
sprint: 2
estimate: 0.5 days
created: 2026-01-16T20:02:16Z
github_issue: 97
---

# Issue: Redesign UmbralBottomBar

## Overview

Redesign the bottom navigation bar with a minimalist icon-only approach and animated indicator line.

## PRD Reference

Section A4.1: UmbralBottomBar (Tab Bar Minimalista)

## Acceptance Criteria

- [ ] Implement new API signature:
  - `items: List<BottomBarItem>`
  - `selectedIndex: Int`
  - `onItemSelected: (Int) -> Unit`
  - `modifier: Modifier`
- [ ] Implement BottomBarItem data class:
  - `icon: ImageVector`
  - `selectedIcon: ImageVector` (defaults to icon)
  - `label: String` (for accessibility, not displayed)
  - `badge: Int?` (for notification count)
- [ ] Apply visual specifications:
  - Background: `backgroundBase`
  - Top Border: 1px `borderDefault`
  - Height: 64.dp
  - Icon Size: 28.dp
  - Labels: Hidden (icon-only design)
  - Indicator: 3px line below active icon
- [ ] Implement states:
  - Unselected: icon `textSecondary`
  - Selected: icon `accentPrimary` + indicator line
  - Pressed: icon `accentPrimary`, subtle ripple
- [ ] Implement indicator animation:
  - Position: spring(dampingRatio=0.8, stiffness=300)
  - Width expand/contract: tween(200ms)
- [ ] Badge support for notification counts
- [ ] Create preview composables
- [ ] Add KDoc documentation

## Visual Reference

```
Bottom Bar Minimalista:
┌─────────────────────────────────────────┐
│─────────────────────────────────────────│ ← 1px top border
│                                         │
│    🏠        📊        ⚙️              │
│    ───                                  │ ← indicator line
│                                         │
└─────────────────────────────────────────┘

With Badge:
    🏠        📊 ●      ⚙️
              2
```

## Implementation Notes

- Use `Row` with equal weights for items
- Indicator should animate smoothly between items
- Calculate indicator position based on selectedIndex
- Consider using `Modifier.drawBehind` for indicator
- Badge should use UmbralBadge component when available

## File Location

`app/src/main/java/com/umbral/presentation/ui/components/navigation/UmbralBottomBar.kt`

## Dependencies

- Motion.kt (01-motion-tokens)
- Phase 1 color tokens
