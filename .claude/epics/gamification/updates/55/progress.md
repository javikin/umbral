# Issue #55: Domain Models and Use Cases - Completed

## Summary

Successfully created all domain models, mappers, and use cases for the Expedition gamification module. This establishes a clean separation between the data layer (Room entities) and the domain layer (business logic).

## Files Created

### Domain Models (`app/src/main/java/com/umbral/expedition/domain/model/`)

1. **ExpeditionFormulas.kt** - Game formulas and constants
   - Energy calculation: `BASE_ENERGY_PER_MINUTE = 10`
   - Streak multipliers (1.0x to 2.5x based on days)
   - XP leveling formula: `level^2 * 100`
   - Location reveal costs: `(index + 1) * 50`
   - Evolution thresholds: State 2 at 500, State 3 at 1500 energy

2. **CaptureRequirement.kt** - Sealed class for companion unlock conditions
   - AlwaysAvailable
   - MinimumLevel
   - LocationsDiscovered
   - SpecificLocation
   - AchievementUnlocked
   - MinimumStreak
   - TotalBlockingMinutes
   - Includes `isMet()` validation and Spanish descriptions

3. **Biome.kt** - Biome enum (V1: FOREST only)
   - Display name: "Bosque Místico"
   - 15 total locations
   - Primary color for theming

4. **CompanionType.kt** - 8 companion types with metadata
   - Elements: Nature, Fire, Water, Air, Earth, Electric, Dark, Light
   - Passive bonuses: EnergyBoost, XpBoost, LocationDiscount, StreakProtection
   - 2 starters (Leaf Sprite, Ember Fox)
   - 6 unlockables with specific requirements

5. **Companion.kt** - Clean domain model with computed properties
   - `canEvolve`, `evolutionCost`, `evolutionProgress`
   - `displayName` (custom name or type + roman numeral)
   - Energy tracking for evolution

6. **PlayerProgress.kt** - Player progression tracking
   - `xpForNextLevel`, `levelProgress`, `streakMultiplier`
   - Helper methods: `withEnergyAndXp()`, `withEnergySpent()`, etc.

7. **Location.kt** - Discovered location domain model
   - 15 forest locations with Spanish names
   - Lore text for each location
   - `daysSinceDiscovery` calculated property

8. **UseCaseResults.kt** - Result types for all use cases
   - EnergyGainResult
   - DiscoveryResult (sealed class)
   - EvolutionResult (sealed class)
   - CaptureResult (sealed class)
   - InvestEnergyResult (sealed class)

### Mappers (`app/src/main/java/com/umbral/expedition/domain/mapper/`)

1. **CompanionMapper.kt** - Entity ↔ Domain for companions
2. **ProgressMapper.kt** - Entity ↔ Domain for player progress
3. **LocationMapper.kt** - Entity ↔ Domain for locations

### Use Cases (`app/src/main/java/com/umbral/expedition/domain/usecase/`)

1. **GainEnergyUseCase.kt**
   - Calculates energy from blocking session with streak multiplier
   - Awards XP (10% of energy)
   - Checks for level up
   - Adds blocking minutes to total

2. **GetProgressUseCase.kt**
   - Returns PlayerProgress with all computed fields
   - Provides both Flow and suspend variants

3. **DiscoverLocationUseCase.kt**
   - Validates energy requirement
   - Checks if already discovered
   - Spends energy and records discovery

4. **EvolveCompanionUseCase.kt**
   - Validates evolution requirements
   - Performs evolution to next state
   - Returns updated companion

5. **CaptureCompanionUseCase.kt**
   - Checks capture requirements (level, locations, achievements, etc.)
   - Validates not already captured
   - Creates new companion instance

6. **InvestEnergyUseCase.kt**
   - Spends player energy to invest in companion
   - Tracks total energy invested for evolution
   - Returns whether companion can now evolve

## Files Updated

### Repository Interface
- **ExpeditionRepository.kt** - Added missing methods:
  - `getProgressOnce()`, `spendEnergy()`, `updateLevel()`, `addBlockingMinutes()`
  - `getCompanionById()`, `getCompanionByType()`, `investEnergyInCompanion()`
  - `getLocationById()`

### Repository Implementation
- **ExpeditionRepositoryImpl.kt** - Implemented all new repository methods

### DAO
- **CompanionDao.kt** - Added `getById(id: String)` query method

## Design Decisions

### 1. Clean Architecture
- Domain models have **zero Room dependencies**
- All database operations go through repository
- Mappers provide clear separation between layers

### 2. Computed Properties
- Domain models calculate derived values on-the-fly
- Examples: `canEvolve`, `levelProgress`, `streakMultiplier`
- Eliminates need to store calculated values in DB

### 3. Spanish UI Text
- All display names and descriptions in Spanish
- Code (variables, enums) remains in English
- Follows project naming conventions

### 4. Type-Safe Results
- Sealed classes for use case results
- Provides success/failure variants with details
- Example: `DiscoveryResult.InsufficientEnergy(required, available, shortage)`

### 5. Formula Centralization
- `ExpeditionFormulas` object contains all game balance
- Easy to tweak without searching through code
- Clear documentation of progression curves

## Game Balance Summary

### Energy System
- Base: 10 energy/minute blocking
- Streak multipliers:
  - 1-2 days: 1.0x
  - 3-6 days: 1.2x
  - 7-13 days: 1.5x
  - 14-29 days: 2.0x
  - 30+ days: 2.5x

### XP & Leveling
- Formula: `level² × 100`
- Level 2: 400 XP
- Level 10: 10,000 XP
- Level 20: 40,000 XP

### Location Discovery
- Cost increases with each discovery: `(index + 1) × 50`
- Location 1: 50 energy
- Location 15: 750 energy

### Companion Evolution
- State 1→2: 500 energy
- State 2→3: 1,500 energy
- 3 states total (basic, intermediate, final)

## Testing Notes

All use cases are ready for unit testing:
- Each has a single responsibility
- Dependencies injected via constructor
- Results are testable sealed classes
- Mock repository for isolated testing

## Next Steps

Recommended follow-up tasks:
1. Create ViewModel for expedition screen
2. Implement UI screens using these use cases
3. Write unit tests for use cases
4. Add integration tests for repository
5. Create lore content system (currently hardcoded in Location.kt)

## Files Summary

**Created:** 17 new files
**Updated:** 3 existing files

All acceptance criteria met:
- ✅ Domain models with computed properties
- ✅ CompanionType enum with 8 types and Spanish names
- ✅ ExpeditionFormulas with all game balance
- ✅ 6 use cases implemented
- ✅ Mappers for Entity ↔ Domain conversion
- ✅ Result types for all use cases
- ✅ Repository methods extended as needed
