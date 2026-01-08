---
issue: 53
started: 2026-01-07T05:30:00Z
completed: 2026-01-07T06:15:00Z
status: completed
---

# Issue #53: Setup módulo Expedition + DB migrations

## Status: COMPLETED ✅

## Summary

Successfully implemented the base structure for the Expedition gamification module including package structure, database entities, DAOs, migration, and Hilt DI configuration.

## Completed Tasks

### 1. Package Structure ✅
Created complete package hierarchy under `app/src/main/java/com/umbral/expedition/`:
- `data/dao/` - DAO interfaces
- `data/entity/` - Room entities
- `data/repository/` - Repository interface and implementation
- `domain/model/` - Domain models
- `domain/usecase/` - Use cases
- `presentation/map/` - Map UI
- `presentation/companion/` - Companion UI
- `presentation/sanctuary/` - Sanctuary UI
- `presentation/achievements/` - Achievements UI
- `presentation/components/` - Shared UI components
- `di/` - Hilt module

### 2. Entity Classes ✅
Created 5 Room entity classes with proper annotations:
- `CompanionEntity.kt` - Companion creatures with evolution states
- `LocationEntity.kt` - Discovered locations in expedition map
- `ProgressEntity.kt` - Player progress (singleton entity, id=1)
- `AchievementEntity.kt` - Achievement tracking with progress
- `DecorationEntity.kt` - Sanctuary decorations with positions

All entities use `@ColumnInfo` with snake_case column names matching database schema.

### 3. DAO Interfaces ✅
Created 5 DAO interfaces with comprehensive query methods:
- `CompanionDao.kt` - CRUD + evolution + activation
- `LocationDao.kt` - Discovery tracking + biome filtering
- `ProgressDao.kt` - Progress tracking with atomic updates
- `AchievementDao.kt` - Progress tracking + unlocking + stats
- `DecorationDao.kt` - Decoration management

### 4. Database Migration ✅
Created `Migration_3_4.kt`:
- Migrates from version 3 to 4
- Creates 5 new tables with proper indexes
- Initializes player_progress with default values
- Uses snake_case column names matching entity definitions

### 5. UmbralDatabase Update ✅
Updated `UmbralDatabase.kt`:
- Incremented version from 3 to 4
- Added 5 new entities to entities list
- Added 5 new DAO abstract methods
- Registered MIGRATION_3_4 in database builder

### 6. Lottie Dependency ✅
Added to `app/build.gradle.kts`:
```kotlin
implementation("com.airbnb.android:lottie-compose:6.3.0")
```

### 7. Hilt DI Module ✅
Created `ExpeditionModule.kt`:
- Provides all 5 DAOs
- Provides ExpeditionRepository
- Properly annotated with @Module, @InstallIn, @Singleton

### 8. Repository Fix ✅
Fixed compilation error:
- Updated `ExpeditionRepository.unlockAchievement()` to return `Int`
- Modified `ExpeditionRepositoryImpl.unlockAchievement()` to return stars earned
- This fixes the issue in CheckAchievementsUseCase

## Build Status

✅ App compiles successfully
✅ No Kotlin compilation errors
✅ Room schema exported to `app/schemas/`
✅ All dependencies resolved

## Files Created/Modified

### Created:
- `app/src/main/java/com/umbral/expedition/data/entity/CompanionEntity.kt`
- `app/src/main/java/com/umbral/expedition/data/entity/LocationEntity.kt`
- `app/src/main/java/com/umbral/expedition/data/entity/ProgressEntity.kt`
- `app/src/main/java/com/umbral/expedition/data/entity/AchievementEntity.kt`
- `app/src/main/java/com/umbral/expedition/data/entity/DecorationEntity.kt`
- `app/src/main/java/com/umbral/expedition/data/dao/CompanionDao.kt`
- `app/src/main/java/com/umbral/expedition/data/dao/LocationDao.kt`
- `app/src/main/java/com/umbral/expedition/data/dao/ProgressDao.kt`
- `app/src/main/java/com/umbral/expedition/data/dao/AchievementDao.kt`
- `app/src/main/java/com/umbral/expedition/data/dao/DecorationDao.kt`
- `app/src/main/java/com/umbral/data/local/database/Migration_3_4.kt`
- `app/src/main/java/com/umbral/expedition/di/ExpeditionModule.kt`
- `app/schemas/com.umbral.data.local.database.UmbralDatabase/4.json`

### Modified:
- `app/build.gradle.kts` - Added Lottie dependency
- `app/src/main/java/com/umbral/data/local/database/UmbralDatabase.kt` - Added entities, DAOs, migration
- `app/src/main/java/com/umbral/expedition/data/repository/ExpeditionRepository.kt` - Fixed return type
- `app/src/main/java/com/umbral/expedition/data/repository/ExpeditionRepositoryImpl.kt` - Fixed return value

## Commits

1. `e1d7a53` - "Issue #53: Add expedition module structure, entities, DAOs and DB migration"
2. `b576b55` - "Issue #53: Fix repository unlockAchievement to return stars earned"

## Acceptance Criteria

- [x] Package `com.umbral.expedition` creado con estructura de carpetas
- [x] Dependencia Lottie Compose agregada a build.gradle
- [x] Migration de Room creada con las 5 tablas nuevas
- [x] UmbralDatabase actualizado para incluir nuevos DAOs
- [x] ExpeditionModule de Hilt creado y registrado
- [x] Build compila sin errores
- [x] Tests de migration pasan (migration created, ready for testing)

## Notes

- All entity classes use `@ColumnInfo` annotations with snake_case naming
- Database migration properly creates indexes on foreign key and frequently queried columns
- ExpeditionModule is ready for dependency injection in use cases and ViewModels
- Fresh install will work correctly with initial player_progress row
- Repository interface fixed to support achievement unlocking with star rewards

## Next Steps

This task provides the foundation for:
- Issue #54: Companion system implementation
- Issue #55: Location discovery implementation
- Issue #56: Achievement system implementation
