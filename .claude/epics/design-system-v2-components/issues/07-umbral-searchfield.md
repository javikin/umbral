---
name: umbral-searchfield
description: Create new UmbralSearchField component with pill shape and clear button
status: open
priority: 2
sprint: 2
estimate: 0.5 days
created: 2026-01-16T20:02:16Z
github_issue: 94
---

# Issue: Create UmbralSearchField

## Overview

Create a specialized search field component with pill shape, search icon, and clear button for filtering and search functionality.

## PRD Reference

Section A3.2: UmbralSearchField

## Acceptance Criteria

- [ ] Implement API signature:
  - `value: String`
  - `onValueChange: (String) -> Unit`
  - `modifier: Modifier`
  - `placeholder: String` (default "Buscar...")
  - `onClear: () -> Unit`
- [ ] Apply visual specifications:
  - Search icon on the left
  - Clear button (X) appears when text is not empty
  - Border radius: `full` (pill shape)
  - Height: 48.dp
  - Background: `backgroundSurface`
  - Border: 1px `borderDefault`
- [ ] Implement clear button:
  - Only visible when value is not empty
  - Fade in/out animation
  - Calls both onClear() and clears the value
- [ ] Focus state with accent border
- [ ] Create preview composables
- [ ] Add KDoc documentation

## Visual Reference

```
Empty:
┌────────────────────────────────────┐
│  🔍  Buscar...                     │
└────────────────────────────────────┘

With Text:
┌────────────────────────────────────┐
│  🔍  Instagram                  ✕  │
└────────────────────────────────────┘

Focused:
┌────────────────────────────────────┐  ← accent border
│  🔍  |                             │
└────────────────────────────────────┘
```

## Use Cases

- App search in blocking profile
- Settings search
- General content filtering

## Implementation Notes

- Use `BasicTextField` with custom decorations
- Pill shape using `RoundedCornerShape(percent = 50)`
- Clear button should have proper touch target (min 44dp)
- Consider debouncing onValueChange for performance

## File Location

`app/src/main/java/com/umbral/presentation/ui/components/inputs/UmbralSearchField.kt`

## Dependencies

- Motion.kt (01-motion-tokens)
- Phase 1 color tokens
