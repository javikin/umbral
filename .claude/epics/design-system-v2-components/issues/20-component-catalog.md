---
name: component-catalog
description: Create ComponentCatalogScreen for debug builds showing all components
status: open
priority: 4
sprint: 4
estimate: 1 day
created: 2026-01-16T20:02:16Z
github_issue: 107
---

# Issue: Create ComponentCatalogScreen

## Overview

Create a comprehensive component catalog screen accessible in debug builds that displays all design system components in their various states.

## PRD Reference

Section C1: ComponentCatalogScreen

## Acceptance Criteria

### Access & Navigation
- [ ] Only visible in debug builds
- [ ] Accessible from Settings via long press on version number
- [ ] Or add "Component Catalog" menu item in Settings (debug only)
- [ ] Support back navigation

### Theme Toggle
- [ ] Dark/Light theme toggle at top of screen
- [ ] Instant theme switch without restart
- [ ] Current theme indicated visually

### Component Sections
- [ ] **Buttons Section:**
  - UmbralButton (all sizes: Small, Medium, Large)
  - UmbralTextButton (normal, destructive)
  - UmbralIconButton (all variants: Ghost, Filled, Tonal)
  - State demos (enabled, disabled, loading, pressed)
- [ ] **Cards Section:**
  - UmbralCard (Default, Elevated, Outlined, Interactive)
  - UmbralSurface (all elevation levels)
  - UmbralDivider (Full, Inset, Middle)
- [ ] **Inputs Section:**
  - UmbralTextField (empty, filled, focused, error)
  - UmbralSearchField (empty, with text)
  - UmbralCheckbox (unchecked, checked, indeterminate)
  - UmbralSwitch (off, on, disabled)
- [ ] **Navigation Section:**
  - UmbralBottomBar (with selection)
  - UmbralTopBar
  - UmbralTabRow
- [ ] **Feedback Section:**
  - UmbralSnackbar (all variants)
  - UmbralToast
  - UmbralProgressIndicator (all variants)
- [ ] **Data Display Section:**
  - UmbralBadge (all variants)
  - UmbralTag (simple, with icon, removable)
  - UmbralAvatar (all sizes, with/without badge)
  - UmbralListItem (single line, two line)
- [ ] **Empty States Section:**
  - All EmptyStateIllustration variants
- [ ] **Skeletons Section:**
  - All skeleton presets

### Features
- [ ] Lazy loading for performance
- [ ] Each component shows all its states
- [ ] Section headers for organization
- [ ] Expandable/collapsible sections (optional)
- [ ] Copy-to-clipboard for component usage code (optional)
- [ ] Spacing overlay toggle (optional)

### General
- [ ] Add navigation route to MainNavigation (debug only)
- [ ] Create preview composables
- [ ] Add KDoc documentation

## Visual Reference

```
Component Catalog
─────────────────────────────────────────
[🌙 Dark] [☀️ Light]      ← Theme Toggle
─────────────────────────────────────────

▼ Buttons
  ┌─────────────────────────────────────┐
  │ UmbralButton                        │
  │                                     │
  │ [ Small ] [ Medium ] [ Large ]      │
  │                                     │
  │ States: [Enabled][Disabled][Loading]│
  └─────────────────────────────────────┘

  ┌─────────────────────────────────────┐
  │ UmbralTextButton                    │
  │                                     │
  │   Cancel   │   Delete (destructive) │
  └─────────────────────────────────────┘

  ...

▼ Cards
  ┌─────────────────────────────────────┐
  │ CardVariant.Default                 │
  │ ┌───────────────────────────────┐   │
  │ │ Card content here             │   │
  │ └───────────────────────────────┘   │
  └─────────────────────────────────────┘

  ...

▼ Empty States
  ┌─────────────────────────────────────┐
  │ NoProfiles   NoApps    NoStats      │
  │    📄         📱        📊         │
  │                                     │
  │ NoNfc     SearchEmpty  Success      │
  │   📡          🔍         ✓          │
  └─────────────────────────────────────┘

...
```

## Implementation Notes

- Use `LazyColumn` with sticky headers for sections
- Consider using `AnimatedVisibility` for expand/collapse
- Theme toggle should use `LocalDensity` and `CompositionLocalProvider`
- Each section can be a separate composable for organization
- Use `BuildConfig.DEBUG` for visibility check

## File Location

`app/src/main/java/com/umbral/presentation/ui/components/catalog/ComponentCatalogScreen.kt`

## Navigation

Add to MainNavigation.kt (debug only):
```kotlin
if (BuildConfig.DEBUG) {
    composable("component_catalog") {
        ComponentCatalogScreen(onBack = { navController.popBackStack() })
    }
}
```

## Dependencies

- All component issues (01-19) should be completed
- Phase 1 tokens
