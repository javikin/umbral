---
name: umbral-icon-button
description: Redesign UmbralIconButton with size and variant options
status: open
priority: 1
sprint: 1
estimate: 0.5 days
created: 2026-01-16T20:02:16Z
github_issue: 91
---

# Issue: Redesign UmbralIconButton

## Overview

Redesign the icon button component with multiple size options and visual variants (Ghost, Filled, Tonal) for different use cases.

## PRD Reference

Section A1.3: UmbralIconButton

## Acceptance Criteria

- [ ] Implement new API signature:
  - `icon: ImageVector`
  - `onClick: () -> Unit`
  - `modifier: Modifier`
  - `contentDescription: String`
  - `size: IconButtonSize` (Small, Medium, Large)
  - `variant: IconButtonVariant` (Ghost, Filled, Tonal)
- [ ] Implement IconButtonSize enum:
  - Small: 32.dp button, 18.dp icon
  - Medium: 40.dp button, 24.dp icon
  - Large: 48.dp button, 28.dp icon
- [ ] Implement IconButtonVariant enum:
  - Ghost: no background
  - Filled: background accentPrimary
  - Tonal: background accentPrimary 10% opacity
- [ ] Apply press animations:
  - Scale 0.95 with springSnappy
  - Background alpha change for Ghost variant
- [ ] Ensure contentDescription is required for accessibility
- [ ] Create preview composables for all combinations
- [ ] Add KDoc documentation

## Visual Reference

```
Ghost:          Filled:         Tonal:
  ⚙️              [⚙️]           (⚙️)
no bg           accent bg       subtle bg

Sizes:
Small   Medium    Large
 ⚙️       ⚙️         ⚙️
32dp    40dp      48dp
```

## Use Cases

- Ghost: Navigation icons, action bar icons
- Filled: Primary icon actions (FAB-style)
- Tonal: Secondary icon actions, toggles

## Implementation Notes

- Use `IconButton` composable as base
- Ensure ripple is correctly bounded to shape
- Ghost variant should show subtle background on press
- All sizes should meet minimum 48dp touch target (add padding if needed)

## File Location

`app/src/main/java/com/umbral/presentation/ui/components/buttons/UmbralIconButton.kt`

## Dependencies

- Motion.kt (01-motion-tokens)
- Phase 1 color tokens
