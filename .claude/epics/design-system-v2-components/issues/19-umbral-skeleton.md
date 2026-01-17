---
name: umbral-skeleton
description: Create UmbralSkeleton system with shimmer animation and presets
status: open
priority: 4
sprint: 4
estimate: 0.5 days
created: 2026-01-16T20:02:16Z
github_issue: 106
---

# Issue: Create UmbralSkeleton System

## Overview

Create a skeleton loading system with shimmer animation and preset components for common loading states.

## PRD Reference

Section B4: Skeleton Loaders (B4.1, B4.2)

## Acceptance Criteria

### UmbralSkeleton Base Component
- [ ] Implement API signature:
  - `modifier: Modifier`
  - `shape: Shape` (default RoundedCornerShape(8.dp))
- [ ] Apply visual specifications:
  - Background Dark: `#252525`
  - Background Light: `#E8E8E8`
  - Shimmer Highlight Dark: `#303030`
  - Shimmer Highlight Light: `#F5F5F5`
  - Animation: shimmer left-to-right, 1200ms, infinite

### Skeleton Presets
- [ ] Implement `SkeletonCard`:
  - Card container with image placeholder, title, subtitle
  - Uses UmbralCard dimensions
- [ ] Implement `SkeletonListItem`:
  - List item with avatar, title, subtitle
  - Matches UmbralListItem dimensions
- [ ] Implement `SkeletonText`:
  - `lines: Int` parameter (default 3)
  - Variable widths: 100%, 90%, 60%
  - Spacing between lines
- [ ] Implement `SkeletonProfileCard`:
  - Specific to Umbral's ProfileCard
  - Header, stats, toggle areas

### General
- [ ] Shimmer animation should be performant (not cause battery drain)
- [ ] Respect "Reduce Motion" accessibility setting
- [ ] Create preview composables for all presets
- [ ] Add KDoc documentation

## Visual Reference

```
Base Skeleton:
┌─────────────────────────────────────────┐
│ ░░░░░░░░░░░░░░░░░▓▓▓░░░░░░░░░░░░░░░░░ │  ← shimmer moving
└─────────────────────────────────────────┘

SkeletonCard:
┌─────────────────────────────────────────┐
│ ┌─────────────────────────────────────┐ │
│ │░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░│ │  ← image area
│ └─────────────────────────────────────┘ │
│ ░░░░░░░░░░░░░░░░░░░░░                   │  ← title
│ ░░░░░░░░░░░░░                           │  ← subtitle
└─────────────────────────────────────────┘

SkeletonListItem:
┌─────────────────────────────────────────┐
│  ●    ░░░░░░░░░░░░░░░░░░░              │  ← avatar + title
│       ░░░░░░░░░░░░                      │  ← subtitle
└─────────────────────────────────────────┘

SkeletonText (3 lines):
░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░  100%
░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░     90%
░░░░░░░░░░░░░░░░░░░░                   60%

SkeletonProfileCard:
┌─────────────────────────────────────────┐
│ ░░░░░░░░░░░░░░   ░░░░░░░░░░░           │  ← header
│                                         │
│ ░░░░░   ░░░░░   ░░░░░                  │  ← stats
│                                         │
│ ░░░░░░░░░░░░░░░░░░░░░░░░░░░           │  ← content
│                                         │
│ ░░░░░░░░░░░░░░░░░░░░░  ───            │  ← action + toggle
└─────────────────────────────────────────┘
```

## Shimmer Implementation

```kotlin
// Shimmer effect using gradient and animation
val shimmerColors = listOf(
    backgroundColor,
    highlightColor,
    backgroundColor
)

val transition = rememberInfiniteTransition()
val translateAnim = transition.animateFloat(
    initialValue = 0f,
    targetValue = 1000f,
    animationSpec = infiniteRepeatable(
        tween(1200, easing = LinearEasing),
        RepeatMode.Restart
    )
)

// Apply gradient with animated offset
```

## Performance Notes

- Use `remember` for gradient calculations
- Limit number of simultaneous skeletons if possible
- Consider using `LaunchedEffect` for shared animation state
- Test on lower-end devices

## File Locations

- `app/src/main/java/com/umbral/presentation/ui/components/skeleton/UmbralSkeleton.kt`
- `app/src/main/java/com/umbral/presentation/ui/components/skeleton/SkeletonPresets.kt`

## Dependencies

- Phase 1 color tokens
