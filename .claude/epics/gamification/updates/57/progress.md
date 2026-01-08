---
issue: 57
title: ExpeditionMapScreen con Canvas
started: 2026-01-07T06:06:55Z
completed: 2026-01-07T06:06:55Z
status: completed
---

# Issue #57 Progress: ExpeditionMapScreen con Canvas

## Summary
Implemented the main expedition map screen with Jetpack Compose Canvas rendering, fog of war system, and interactive location discovery.

## Files Created

### 1. Domain Model
- **ForestBiomeData.kt** (`domain/model/`)
  - Defines 15 forest locations with map positions (Offset coordinates)
  - Each location has adjacency list for path connections
  - Helper functions for visibility logic (discovered vs available locations)
  - Map dimensions: 1000x1000 canvas units

### 2. ViewModel
- **ExpeditionMapViewModel.kt** (`presentation/map/`)
  - Manages UI state: discoveredLocationIds, visibleLocationIds, currentEnergy
  - Combines repository flows with local selection state
  - Methods: selectLocation(), discoverSelectedLocation(), clearSelection()
  - Uses DiscoverLocationUseCase for discovery flow
  - Emits DiscoveryResult for snackbar messages

### 3. Canvas Component
- **BiomeMapCanvas.kt** (`presentation/map/components/`)
  - Custom Canvas with pan/zoom gestures using rememberTransformableState
  - Scale range: 0.5x to 3x
  - Renders in layers:
    1. Forest green background with texture patches
    2. Connection paths between discovered locations
    3. Fog of war (70% opacity black, cleared around discovered locations)
    4. Location markers (green=discovered, orange=available)
  - Tap gesture detection with coordinate transformation
  - Hit detection using distance calculation (30f radius)

### 4. UI Components
- **EnergyChip.kt** (`presentation/map/components/`)
  - Displays current energy with gold star icon
  - Used in TopAppBar

- **LocationDetailSheet.kt** (`presentation/map/components/`)
  - ModalBottomSheet for location details
  - Discovered: Shows name, lore, and discovery stats
  - Undiscovered: Shows lock icon, energy cost, and "Descubrir" button
  - Button disabled when insufficient energy
  - Error message shows energy shortage

### 5. Main Screen
- **ExpeditionMapScreen.kt** (`presentation/map/`)
  - Scaffold with TopAppBar showing biome name and energy
  - Full-screen BiomeMapCanvas
  - Conditional LocationDetailSheet when location selected
  - Snackbar for discovery results
  - Spanish UI text throughout

## Technical Highlights

### Canvas Performance
- Transform state for pan/zoom (no heavy allocations in draw scope)
- Efficient fog of war using BlendMode.Clear for discovered areas
- Connection paths only drawn between discovered locations
- Hit detection optimized with early returns

### Gesture Handling
- Pan: Direct offset modification
- Zoom: Coerced to 0.5f-3f range
- Tap: Coordinate transformation accounting for scale and offset
- Centering offset to keep map centered on canvas

### Fog of War Implementation
```kotlin
// Draw full fog layer
drawRect(color = fogColor, alpha = 0.7f)

// Clear around discovered locations
drawCircle(
    color = Color.Transparent,
    radius = 100f,
    blendMode = BlendMode.Clear
)
```

### Visibility Logic
- Starting location (forest_01) always visible
- Discovered locations always visible
- Locations adjacent to discovered locations become visible
- Adjacency defined in ForestBiomeData

## Testing Notes

### Manual Testing Required
1. Pan gesture works smoothly
2. Pinch-to-zoom functions (0.5x to 3x)
3. Tapping locations opens detail sheet
4. Discovery button disabled when energy insufficient
5. Fog of war clears around discovered locations
6. Connection paths appear between discovered locations
7. Snackbar shows appropriate messages
8. Spanish text displays correctly

### Edge Cases Handled
- Empty discovered set (shows only forest_01)
- Tap outside any location (no action)
- Discovery with insufficient energy (error message)
- Already discovered location (snackbar message)
- Transform bounds (scale clamped, pan unrestricted)

## Integration Points

### Dependencies Used
- DiscoverLocationUseCase
- ExpeditionRepository (via ViewModel)
- LocationMapper, ProgressMapper
- ForestBiomeData (new)

### UI/UX Features
- Material 3 components (ModalBottomSheet, TopAppBar)
- Forest theme color (#1B5E20 dark green)
- Gold star for energy (#FFD700)
- Green for discovered (#4CAF50)
- Orange for available (#FF9800)

## Performance Considerations
- Canvas redraw optimized (only visible locations drawn)
- Transform calculations outside draw scope
- Flow combination in ViewModel (efficient state updates)
- StateFlow with WhileSubscribed(5000) for lifecycle awareness

## Future Enhancements (Not in Scope)
- Animated fog reveal on discovery
- Parallax background layers
- Path animation when discovering new locations
- Zoom buttons (optional MapControls.kt)
- Mini-map overview
- Location icons/images instead of circles

## Status
✅ All required files created
✅ Canvas rendering with fog of war
✅ Pan and zoom gestures
✅ Location tap detection
✅ Discovery flow with energy cost
✅ Spanish UI text
✅ 60fps target architecture (no heavy allocations in draw)

Ready for integration testing with ExpeditionRepository and navigation.
