---
name: motion-tokens
description: Create Motion.kt with animation tokens, durations, springs, and easings
status: open
priority: 1
sprint: 1
estimate: 0.5 days
created: 2026-01-16T20:02:16Z
github_issue: 88
---

# Issue: Create Motion.kt Animation Tokens

## Overview

Create the foundational animation token system that will be used by all components. This establishes consistent animation behavior across the entire design system.

## PRD Reference

Section D: Especificaciones de Animacion (D1-D4)

## Acceptance Criteria

- [ ] Create `Motion.kt` in `presentation/ui/theme/`
- [ ] Implement duration tokens:
  - `instant` = 0ms
  - `quick` = 100ms (micro-interactions)
  - `fast` = 150ms (hover, color changes)
  - `normal` = 250ms (standard transitions)
  - `slow` = 400ms (page transitions)
  - `slower` = 600ms (complex animations)
- [ ] Implement spring configurations:
  - `springSnappy` (dampingRatio=0.7, stiffness=500)
  - `springBouncy` (dampingRatio=0.5, stiffness=400)
  - `springGentle` (dampingRatio=1.0, stiffness=200)
- [ ] Implement easing curves:
  - `easeOut` = CubicBezierEasing(0.0, 0.0, 0.2, 1.0)
  - `easeIn` = CubicBezierEasing(0.4, 0.0, 1.0, 1.0)
  - `easeInOut` = CubicBezierEasing(0.4, 0.0, 0.2, 1.0)
  - `emphasis` = CubicBezierEasing(0.2, 0.0, 0.0, 1.0)
- [ ] Include screen transition presets (enterTransition, exitTransition)
- [ ] Add KDoc documentation for all tokens
- [ ] Create preview composables demonstrating animations

## Implementation Details

```kotlin
object UmbralMotion {
    // Durations
    val instant = 0.ms
    val quick = 100.ms
    val fast = 150.ms
    val normal = 250.ms
    val slow = 400.ms
    val slower = 600.ms

    // Springs
    val springSnappy = spring<Float>(
        dampingRatio = 0.7f,
        stiffness = 500f
    )
    val springBouncy = spring<Float>(
        dampingRatio = 0.5f,
        stiffness = 400f
    )
    val springGentle = spring<Float>(
        dampingRatio = 1f,
        stiffness = 200f
    )

    // Easings
    val easeOut = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)
    val easeIn = CubicBezierEasing(0.4f, 0.0f, 1.0f, 1.0f)
    val easeInOut = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
    val emphasis = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
}
```

## File Location

`app/src/main/java/com/umbral/presentation/ui/theme/Motion.kt`

## Dependencies

- Requires Phase 1 tokens (design-system-v2) to be completed
- Uses Compose Animation APIs

## Notes

- All animation tokens should respect system "Reduce Motion" accessibility setting
- Consider providing reduced-motion alternatives for each animation
