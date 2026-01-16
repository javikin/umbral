---
name: umbral-badge
description: Create new UmbralBadge component for notification counts and status indicators
status: open
priority: 3
sprint: 3
estimate: 0.5 days
created: 2026-01-16T20:02:16Z
github_issue: 102
---

# Issue: Create UmbralBadge

## Overview

Create a badge component for displaying notification counts, status indicators, and small labels.

## PRD Reference

Section B3.1: UmbralBadge

## Acceptance Criteria

- [ ] Implement API signature:
  - `content: String`
  - `modifier: Modifier`
  - `variant: BadgeVariant` (Default, Success, Warning, Error, Neutral)
- [ ] Implement BadgeVariant enum with colors:
  - Default: background `accentPrimary`, text dark
  - Success: background `success`, text dark
  - Warning: background `warning`, text dark
  - Error: background `error`, text white
  - Neutral: background `backgroundSurface`, text `textSecondary`
- [ ] Apply visual specifications:
  - Height: 20.dp
  - Horizontal Padding: 8.dp
  - Corner Radius: full (pill)
  - Text Style: `labelSmall`
  - Min Width: 20.dp (for single digit numbers)
- [ ] Support empty badge (dot only, no text)
- [ ] Appear animation: scale + fade with springBouncy
- [ ] Create preview composables for all variants
- [ ] Add KDoc documentation

## Visual Reference

```
With Text:
┌─────┐  ┌───┐  ┌─────┐  ┌───────┐
│  3  │  │ 12│  │ 99+ │  │ Nuevo │
└─────┘  └───┘  └─────┘  └───────┘
 small   medium  max     text badge

Variants:
Default: ████  (accent)
Success: ████  (green)
Warning: ████  (amber)
Error:   ████  (red)
Neutral: ░░░░  (subtle)

Dot Badge:
   •     (just a colored dot, no text)
```

## Use Cases

- Notification counts on bottom bar items
- Status indicators (active sessions)
- New feature badges
- Unread message counts

## Implementation Notes

- Consider `99+` format for counts > 99
- Dot badge should be smaller (8.dp diameter)
- Use `BadgedBox` pattern for positioning on icons
- Animation should play when content changes

## File Location

`app/src/main/java/com/umbral/presentation/ui/components/display/UmbralBadge.kt`

## Dependencies

- Motion.kt (01-motion-tokens)
- Phase 1 color tokens
