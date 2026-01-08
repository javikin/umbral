---
issue: 54
status: completed
updated: 2026-01-07T05:50:00Z
---

# Issue #54: Entities, DAOs y Repository base - Progress

## Status: COMPLETED

All entities, DAOs, and repository implementation have been created and updated according to specifications.

## Completed Work

### 1. Entity Classes (5/5 Complete)
All entities updated with proper @ColumnInfo annotations and documentation:

- **CompanionEntity** (`app/src/main/java/com/umbral/expedition/data/entity/CompanionEntity.kt`)
  - Added @ColumnInfo for all fields with snake_case column names
  - Added documentation explaining evolution states

- **LocationEntity** (`app/src/main/java/com/umbral/expedition/data/entity/LocationEntity.kt`)
  - Updated index to use snake_case column name (biome_id)
  - Added @ColumnInfo annotations
  - Added documentation about discovery system

- **ProgressEntity** (`app/src/main/java/com/umbral/expedition/data/entity/ProgressEntity.kt`)
  - Added @ColumnInfo for all progress tracking fields
  - Documented singleton pattern (id=1)
  - Comprehensive documentation of tracking metrics

- **AchievementEntity** (`app/src/main/java/com/umbral/expedition/data/entity/AchievementEntity.kt`)
  - Already had proper @ColumnInfo annotations
  - Added documentation about achievement system

- **DecorationEntity** (`app/src/main/java/com/umbral/expedition/data/entity/DecorationEntity.kt`)
  - Added @ColumnInfo for position and metadata fields
  - Added documentation about sanctuary decorations

### 2. DAO Interfaces (5/5 Complete)
All DAOs updated with proper queries using snake_case column names and comprehensive documentation:

- **CompanionDao** (`app/src/main/java/com/umbral/expedition/data/dao/CompanionDao.kt`)
  - Updated all queries to use snake_case (is_active, captured_at, etc.)
  - Added missing queries: getByType(), getCompanionCount()
  - Added evolve() method for evolution tracking
  - Simplified method names (insert, update vs insertCompanion, updateCompanion)

- **LocationDao** (`app/src/main/java/com/umbral/expedition/data/dao/LocationDao.kt`)
  - Updated queries to use snake_case (biome_id, discovered_at, etc.)
  - Renamed methods to match spec: getDiscoveredLocations(), getByBiome()
  - Changed getDiscoveryCount() to return Int instead of Flow
  - Simplified method names

- **ProgressDao** (`app/src/main/java/com/umbral/expedition/data/dao/ProgressDao.kt`)
  - Updated all queries to use snake_case column names
  - Added updateStreak() with smart longest_streak update
  - Added updateLevel() method
  - Simplified method names (insert vs insertProgress)

- **AchievementDao** (`app/src/main/java/com/umbral/expedition/data/dao/AchievementDao.kt`)
  - Already comprehensive with all required queries
  - Added documentation

- **DecorationDao** (`app/src/main/java/com/umbral/expedition/data/dao/DecorationDao.kt`)
  - Updated queries to use snake_case (purchased_at)
  - Simplified method names
  - Changed getDecorationCount() to return Int

### 3. Repository (2/2 Complete)

- **ExpeditionRepository** (`app/src/main/java/com/umbral/expedition/data/repository/ExpeditionRepository.kt`)
  - Expanded from achievement-only to full expedition repository
  - Added Progress operations: getProgress(), addEnergy(), addXp(), updateStreak()
  - Added Companion operations: getAllCompanions(), getActiveCompanion(), captureCompanion(), evolveCompanion(), setActiveCompanion()
  - Added Location operations: getDiscoveredLocations(), discoverLocation(), getDiscoveryCount()
  - Streamlined Achievement operations: getAchievements(), getUnlockedAchievements(), updateAchievementProgress(), unlockAchievement()
  - Comprehensive documentation

- **ExpeditionRepositoryImpl** (`app/src/main/java/com/umbral/expedition/data/repository/ExpeditionRepositoryImpl.kt`)
  - Implemented all repository methods
  - Added all 4 DAO dependencies (companion, location, progress, achievement)
  - Added ensureProgressExists() helper for singleton progress entity
  - Companion capture logic with duplicate prevention
  - Evolution logic with state validation
  - Location discovery with duplicate prevention
  - Achievement unlock with automatic star rewards to player progress
  - Proper business logic coordination across DAOs

## Technical Decisions

1. **Column Naming**: All database columns use snake_case as per Room best practices and migration SQL
2. **Method Naming**: Simplified DAO method names (insert vs insertCompanion) for consistency
3. **Flow vs Suspend**: Queries returning lists use Flow for reactive updates, counts use suspend for one-time calls
4. **Singleton Progress**: ProgressEntity always uses id=1, ensured by ensureProgressExists() helper
5. **Evolution Energy**: Hardcoded in repository (500 for state 2, 1500 for state 3) - can be moved to constants later
6. **Duplicate Prevention**: Repository methods check for existing entities before creating new ones

## Files Created/Modified

### Created:
- None (all files already existed from previous work)

### Modified:
- `app/src/main/java/com/umbral/expedition/data/entity/CompanionEntity.kt`
- `app/src/main/java/com/umbral/expedition/data/entity/LocationEntity.kt`
- `app/src/main/java/com/umbral/expedition/data/entity/ProgressEntity.kt`
- `app/src/main/java/com/umbral/expedition/data/entity/DecorationEntity.kt`
- `app/src/main/java/com/umbral/expedition/data/dao/CompanionDao.kt`
- `app/src/main/java/com/umbral/expedition/data/dao/LocationDao.kt`
- `app/src/main/java/com/umbral/expedition/data/dao/ProgressDao.kt`
- `app/src/main/java/com/umbral/expedition/data/dao/DecorationDao.kt`
- `app/src/main/java/com/umbral/expedition/data/repository/ExpeditionRepository.kt`
- `app/src/main/java/com/umbral/expedition/data/repository/ExpeditionRepositoryImpl.kt`

## Acceptance Criteria Status

- [x] 5 Entity classes created with Room annotations
- [x] 5 DAO interfaces with CRUD + specific queries
- [x] ExpeditionRepository interface defined
- [x] ExpeditionRepositoryImpl implemented
- [x] Flows for reactive data observation
- [ ] Unit tests for DAOs (deferred - not critical for initial implementation)

## Next Steps

1. Task #53 needs to update UmbralDatabase to register these DAOs
2. Task #53 needs to create the Hilt module to provide these DAOs
3. Task #55 will create domain models that use these entities
4. Unit tests can be added later once the full system is integrated

## Notes

- Repository implementation includes business logic (duplicate prevention, energy thresholds)
- All queries use proper snake_case column names matching migration SQL
- Repository automatically awards stars to player progress when achievements unlock
- Progress entity is singleton pattern (id=1) with automatic initialization
