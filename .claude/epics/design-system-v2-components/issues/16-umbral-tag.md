---
name: umbral-tag
description: Create new UmbralTag component for labels and categories
status: open
priority: 3
sprint: 3
estimate: 0.5 days
created: 2026-01-16T20:02:16Z
github_issue: 103
---

# Issue: Create UmbralTag

## Overview

Create a tag component (similar to Chip but more compact) for labels, categories, and removable items.

## PRD Reference

Section B3.2: UmbralTag

## Acceptance Criteria

- [ ] Implement API signature:
  - `text: String`
  - `modifier: Modifier`
  - `icon: ImageVector?`
  - `onRemove: (() -> Unit)?` (shows X if not null)
- [ ] Apply visual specifications:
  - Height: 28.dp
  - Background: `accentPrimary` 10% opacity
  - Text Color: `accentPrimary`
  - Border: None
  - Corner Radius: 6.dp
  - Padding: 8.dp horizontal
- [ ] Support optional leading icon
- [ ] Support removable variant with X button
- [ ] Remove button should have proper touch target
- [ ] Press state: slightly darker background
- [ ] Create preview composables
- [ ] Add KDoc documentation

## Visual Reference

```
Simple Tag:
┌──────────────┐
│   Trabajo    │
└──────────────┘

With Icon:
┌────────────────┐
│  📱 Redes     │
└────────────────┘

Removable:
┌────────────────┐
│  Instagram  ✕ │
└────────────────┘
```

## Use Cases

- Category labels on profiles
- Selected apps in multi-select
- Filters applied
- Tags/labels on items

## Comparison with UmbralBadge

| Feature | Tag | Badge |
|---------|-----|-------|
| Height | 28.dp | 20.dp |
| Removable | Yes | No |
| Icon support | Yes | No |
| Purpose | Categories | Counts/Status |

## Implementation Notes

- Use `InputChip` as reference but simplify
- Remove animation when onRemove is called
- Icon should be tinted with text color
- X button should be 16.dp icon

## File Location

`app/src/main/java/com/umbral/presentation/ui/components/display/UmbralTag.kt`

## Dependencies

- Motion.kt (01-motion-tokens)
- Phase 1 color tokens
