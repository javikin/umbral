---
name: umbral-toast
description: Create new UmbralToast component for subtle, non-intrusive feedback
status: open
priority: 3
sprint: 3
estimate: 0.5 days
created: 2026-01-16T20:02:16Z
github_issue: 100
---

# Issue: Create UmbralToast

## Overview

Create a toast component for subtle, informational messages that are more compact than snackbars and auto-dismiss quickly.

## PRD Reference

Section B2.2: UmbralToast (mas sutil que Snackbar)

## Acceptance Criteria

- [ ] Implement API signature:
  - `message: String`
  - `icon: ImageVector?`
- [ ] Apply visual specifications:
  - More compact than Snackbar
  - No action button (informational only)
  - Appears at top-center of screen
  - Auto-dismiss after 2 seconds
  - Background with blur effect (if performance allows)
- [ ] Implement visual styling:
  - Background: `backgroundElevated` with 90% opacity
  - Corner Radius: pill shape (full rounded)
  - Padding: 12.dp horizontal, 8.dp vertical
  - Height: auto, approximately 36-40.dp
  - Text: `labelMedium`
- [ ] Implement animations:
  - Enter: fadeIn + scaleIn from 0.8
  - Exit: fadeOut + scaleOut to 0.8
  - Duration: 200ms each
- [ ] Create ToastHost composable for managing toasts
- [ ] Create preview composables
- [ ] Add KDoc documentation

## Visual Reference

```
Centered at top:

        ┌─────────────────────┐
        │  ✓  Copiado         │
        └─────────────────────┘

With just text:

        ┌─────────────────────┐
        │  Guardando...       │
        └─────────────────────┘
```

## Use Cases

- Copy confirmation
- Quick status updates
- Non-critical information
- Action acknowledgments

## Implementation Notes

- Toast should float above content
- Use `Popup` or `Box` with `align(Alignment.TopCenter)`
- Blur effect using `Modifier.blur()` if API level supports
- Fallback to semi-transparent background if blur not available
- Consider using coroutines for timing

## File Location

`app/src/main/java/com/umbral/presentation/ui/components/feedback/UmbralToast.kt`

## Dependencies

- Motion.kt (01-motion-tokens)
- Phase 1 color tokens
