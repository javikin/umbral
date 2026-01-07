# Achievement System

Complete achievement system with 30 achievements across 3 categories.

## Overview

The achievement system rewards players for various in-game activities:
- **Blocking:** Completing sessions, maintaining streaks, accumulating time
- **Exploration:** Discovering locations, reading lore, completing biomes
- **Companion:** Capturing and evolving companions

Total rewards: **1,060 stars** from 30 achievements

## Architecture

```
domain/
├── model/
│   └── AchievementDefinitions.kt     # 30 achievement definitions
└── usecase/
    ├── CheckAchievementsUseCase.kt   # Progress tracking & auto-unlock
    └── InitializeExpeditionUseCase.kt # First-time setup

presentation/
└── achievements/
    ├── AchievementsViewModel.kt      # Reactive state management
    ├── AchievementsScreen.kt         # Full UI with categories
    └── README.md                     # This file
```

## Achievement Categories

### Blocking (10 achievements - 330 stars)
- `first_step` - First session (5 stars)
- `golden_hour` - 60 min session (10 stars)
- `marathoner` - 120 min session (20 stars)
- `consistent_7` - 7 day streak (15 stars)
- `dedicated_14` - 14 day streak (25 stars)
- `master_30` - 30 day streak (50 stars)
- `centurion` - 100 sessions (30 stars)
- `thousand_min` - 1,000 min total (25 stars)
- `iron_will` - 5,000 min total (50 stars)
- `legend` - 10,000 min total (100 stars)

### Exploration (10 achievements - 315 stars)
- `novice_explorer` - 1 location (5 stars)
- `cartographer` - 5 locations (10 stars)
- `adventurer` - 10 locations (20 stars)
- `master_explorer` - 15 locations (50 stars)
- `lore_reader_5` - 5 stories (10 stars)
- `historian` - All stories (30 stars)
- `biome_complete` - 100% biome (50 stars)
- `collector` - All base companions (40 stars)
- `no_stone` - All secrets (25 stars)
- `speedrunner` - Biome in <14 days (75 stars)

### Companion (10 achievements - 415 stars)
- `first_friend` - 1 companion (5 stars)
- `duo` - 2 companions (10 stars)
- `team` - 4 companions (20 stars)
- `all_together` - 8 companions (40 stars)
- `first_evolution` - 1 evolution (15 stars)
- `evolutionist` - 3 evolutions (25 stars)
- `master_breeder` - 24 evolutions (100 stars)
- `best_friend` - 1,000 energy in one companion (20 stars)
- `eternal_bond` - Max evolution (30 stars)
- `full_sanctuary` - All companions max evo (150 stars)

## Usage

### Checking for Achievements

Call the appropriate check method from `CheckAchievementsUseCase`:

```kotlin
// After blocking session ends
val unlockedAchievements = checkAchievementsUseCase.checkBlockingAchievements(
    sessionMinutes = 65,
    totalMinutes = 1500,
    currentStreak = 10,
    totalSessions = 50
)

// Display unlock notifications
unlockedAchievements.forEach { achievement ->
    showUnlockNotification(achievement.title, achievement.starsEarned)
}
```

### Displaying Achievements Screen

```kotlin
// Navigation
navController.navigate("achievements")

// Or with compose destination
composable("achievements") {
    AchievementsScreen(
        onBack = { navController.popBackStack() }
    )
}
```

### Initialization

Call once during app startup:

```kotlin
class UmbralApplication : Application() {
    @Inject lateinit var initializeExpedition: InitializeExpeditionUseCase

    override fun onCreate() {
        super.onCreate()
        lifecycleScope.launch {
            initializeExpedition()
        }
    }
}
```

## Data Flow

```
User Action
    ↓
CheckAchievementsUseCase
    ↓
ExpeditionRepository.updateAchievementProgress()
    ↓
AchievementDao (Room)
    ↓
Auto-unlock if target reached
    ↓
Award stars to player progress
    ↓
Return UnlockedAchievement list
    ↓
Show notification/animation
```

## Integration Points

### With Blocking System
Hook into session end event:
```kotlin
// In BlockingService or ViewModel
viewModelScope.launch {
    val unlocked = checkAchievementsUseCase.checkBlockingAchievements(...)
    unlocked.forEach { showUnlockAnimation(it) }
}
```

### With Exploration System (Issue #55)
Call after location discovery:
```kotlin
val unlocked = checkAchievementsUseCase.checkExplorationAchievements(
    totalLocationsDiscovered = locationCount,
    loreStoriesRead = loreCount,
    // ...
)
```

### With Companion System (Issue #54)
Call after capture or evolution:
```kotlin
val unlocked = checkAchievementsUseCase.checkCompanionAchievements(
    totalCompanionsCaptured = companionCount,
    totalEvolutions = evolutionCount,
    // ...
)
```

## Testing

```kotlin
@Test
fun `blocking achievement unlocks at correct milestone`() = runTest {
    // Given: Player has 999 minutes
    repository.updateAchievementProgress("thousand_min", 999)

    // When: Check with 1000 minutes
    val unlocked = useCase.checkBlockingAchievements(
        sessionMinutes = 60,
        totalMinutes = 1000,
        currentStreak = 5,
        totalSessions = 20
    )

    // Then: thousand_min achievement unlocks
    assertEquals(1, unlocked.size)
    assertEquals("thousand_min", unlocked[0].id)
    assertEquals(25, unlocked[0].starsEarned)
}
```

## Future Enhancements

1. **Unlock Animations:** Lottie animation when achievement unlocks
2. **Notifications:** Push notification for achievements earned while app closed
3. **Sharing:** Share achievement unlocks to social media
4. **Badges:** Visual badges for rare achievements
5. **Progress Widgets:** Widget showing achievement progress
6. **Leaderboard:** Compare achievements with friends (if backend added)

## Performance Notes

- All achievements use Flow for reactive updates
- Progress only increases (can't regress)
- Auto-unlock happens automatically in repository
- Stars awarded atomically with unlock
- Database queries use indexes on category field

## Accessibility

- All UI elements have proper content descriptions
- Progress indicators announce percentage
- Locked achievements clearly distinguished visually
- High contrast colors for locked vs unlocked states
