---
name: umbral-progress
description: Improve UmbralProgressIndicator with multiple variants (Circular, Dots, Pulse)
status: open
priority: 3
sprint: 3
estimate: 0.5 days
created: 2026-01-16T20:02:16Z
github_issue: 101
---

# Issue: Improve UmbralProgressIndicator

## Overview

Enhance the progress indicator component with multiple animation variants and create a determinate progress bar component.

## PRD Reference

Section B2.3: UmbralProgressIndicator

## Acceptance Criteria

### UmbralProgressIndicator (Indeterminate)
- [ ] Implement API signature:
  - `modifier: Modifier`
  - `variant: ProgressVariant` (Circular, Dots, Pulse)
- [ ] Implement ProgressVariant enum:
  - Circular: standard spinner
  - Dots: 3 animated dots
  - Pulse: pulsing circle
- [ ] Implement Circular variant:
  - Sizes: 24.dp (small), 40.dp (medium), 56.dp (large)
  - Stroke: 3.dp
  - Color: `accentPrimary`
  - Animation: rotate 360 degrees in 1000ms, easeInOut
- [ ] Implement Dots variant:
  - 3 circles of 8.dp
  - Spacing: 8.dp
  - Animation: scale staggered, 600ms total cycle
- [ ] Implement Pulse variant:
  - Circle that scales from 0.8 to 1.2
  - Opacity pulses with scale
  - 800ms cycle

### UmbralProgressBar (Determinate)
- [ ] Implement API signature:
  - `progress: Float` (0f to 1f)
  - `modifier: Modifier`
- [ ] Apply visual specifications:
  - Height: 4.dp
  - Background: `borderDefault`
  - Fill: `accentPrimary`
  - Corner Radius: full (pill)
- [ ] Implement smooth width transition animation
- [ ] Support animated progress changes

### General
- [ ] Create preview composables for all variants
- [ ] Add KDoc documentation

## Visual Reference

```
Circular:
    ◠
   ◞ ◟    (spinning)
    ◡

Dots:
   •  •  •   (scaling staggered)
   ↑  ↑  ↑
   each scales up then down in sequence

Pulse:
   ( • )     (expanding and contracting)

Progress Bar:
   ████████░░░░░░░░░░░░   40%
```

## Implementation Notes

- Use `InfiniteTransition` for continuous animations
- Dots should have delay between each (staggered effect)
- Progress bar should use `animateFloatAsState` for smooth changes
- Consider performance on lower-end devices

## File Location

`app/src/main/java/com/umbral/presentation/ui/components/feedback/UmbralProgressIndicator.kt`

## Dependencies

- Motion.kt (01-motion-tokens)
- Phase 1 color tokens
