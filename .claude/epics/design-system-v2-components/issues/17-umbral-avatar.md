---
name: umbral-avatar
description: Create new UmbralAvatar component with image, initials, and badge support
status: open
priority: 3
sprint: 3
estimate: 0.5 days
created: 2026-01-16T20:02:16Z
github_issue: 104
---

# Issue: Create UmbralAvatar

## Overview

Create an avatar component for displaying user images, initials, or profile icons with optional status badges.

## PRD Reference

Section B3.3: UmbralAvatar

## Acceptance Criteria

- [ ] Implement API signature:
  - `modifier: Modifier`
  - `image: ImageBitmap?`
  - `initials: String?`
  - `size: AvatarSize` (Small, Medium, Large, XLarge)
  - `badge: AvatarBadge?` (Online, Offline, Active, None)
- [ ] Implement AvatarSize enum:
  - Small: 32.dp
  - Medium: 40.dp
  - Large: 56.dp
  - XLarge: 80.dp
- [ ] Implement AvatarBadge enum:
  - Online: green dot
  - Offline: gray dot
  - Active: accent dot with pulse animation
  - None: no badge
- [ ] Apply visual specifications:
  - Shape: Circle
  - Background (no image): `accentPrimary` 15% opacity
  - Initials Color: `accentPrimary`
  - Border: 2px `backgroundBase` (for stacking)
  - Badge Position: bottom-right
  - Badge Size: 25% of avatar size
- [ ] Fallback hierarchy: image > initials > default icon
- [ ] Support image loading with placeholder
- [ ] Active badge should pulse subtly
- [ ] Create preview composables for all combinations
- [ ] Add KDoc documentation

## Visual Reference

```
With Image:        With Initials:      Default:
   ┌───┐              ┌───┐             ┌───┐
   │ 🖼 │             │ JD │            │ 👤 │
   └───┘              └───┘             └───┘

With Badge (Online):
   ┌───┐
   │ 🖼 │
   └───┘●  ← green dot

Sizes:
Small   Medium   Large    XLarge
 32dp    40dp     56dp     80dp
  ○       ○        ○         ○
```

## Use Cases

- Profile pictures
- User list items
- Chat avatars
- Session status indicators

## Implementation Notes

- Use `Modifier.clip(CircleShape)` for clipping
- Badge should use `Box` with offset positioning
- Border helps when stacking avatars (avatar group)
- Consider `AsyncImage` from Coil for loading images

## File Location

`app/src/main/java/com/umbral/presentation/ui/components/display/UmbralAvatar.kt`

## Dependencies

- Motion.kt (01-motion-tokens)
- Phase 1 color tokens
