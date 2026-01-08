---
name: gamification
status: backlog
created: 2026-01-07T04:45:38Z
updated: 2026-01-07T05:13:26Z
progress: 0%
prd: .claude/prds/gamification.md
github: https://github.com/javikin/umbral/issues/52
---

# Epic: Sistema de Gamificación "Expedición"

## Overview

Implementación del sistema de gamificación MVP+ que transforma el tiempo de bloqueo en una experiencia de exploración. El usuario gana Energía por cada minuto bloqueado, que usa para revelar un mapa, descubrir locaciones, capturar compañeros y desbloquear logros.

**Scope:** MVP+ (4-5 semanas)
**Complejidad:** Alta (nuevo feature module completo)
**Dependencias:** BlockingManager existente, StatsRepository

---

## Architecture Decisions

### AD-1: Feature Module Independiente
- **Decisión:** Crear módulo `expedition` separado bajo `com.umbral.expedition`
- **Rationale:** Aislamiento de código, fácil de desactivar/activar, testing independiente
- **Impacto:** Nueva estructura de packages, DI module separado

### AD-2: Canvas para Mapa
- **Decisión:** Usar Jetpack Compose Canvas para renderizar el mapa
- **Rationale:** Control total sobre rendering, mejor performance que ImageViews anidados
- **Alternativa descartada:** Google Maps SDK (overkill, requiere API key)
- **Impacto:** Implementación custom de gestures (pan, zoom)

### AD-3: Lottie para Animaciones
- **Decisión:** Usar Lottie Compose para todas las animaciones
- **Rationale:** Assets ligeros (~50KB c/u), 60fps garantizado, amplia biblioteca disponible
- **Alternativa descartada:** GIFs (pesados), Rive (más complejo para MVP)
- **Impacto:** Dependencia `lottie-compose`, assets en `/raw/`

### AD-4: Room para Persistencia
- **Decisión:** Extender Room DB existente con nuevas tablas
- **Rationale:** Consistencia con arquitectura existente, migrations automáticas
- **Impacto:** Nueva migration, 5 tablas adicionales

### AD-5: Hook en BlockingManager
- **Decisión:** Agregar callback `onSessionComplete` en BlockingManager
- **Rationale:** Punto único de integración, no modifica lógica de bloqueo
- **Impacto:** Modificación menor en BlockingManager existente

### AD-6: Assets Bundled
- **Decisión:** Incluir todos los assets en el APK (no descarga dinámica)
- **Rationale:** Funcionalidad offline garantizada, UX sin esperas
- **Trade-off:** +15MB en APK size
- **Impacto:** Assets en `/res/raw/` y `/res/drawable/`

---

## Technical Approach

### Database Schema

```sql
-- Migration: add_expedition_tables

CREATE TABLE companions (
    id TEXT PRIMARY KEY,
    type TEXT NOT NULL,
    name TEXT,
    evolution_state INTEGER DEFAULT 1,
    energy_invested INTEGER DEFAULT 0,
    captured_at INTEGER NOT NULL,
    is_active INTEGER DEFAULT 0,
    UNIQUE(type)
);

CREATE TABLE discovered_locations (
    id TEXT PRIMARY KEY,
    biome_id TEXT NOT NULL,
    discovered_at INTEGER NOT NULL,
    energy_spent INTEGER NOT NULL,
    lore_read INTEGER DEFAULT 0
);

CREATE TABLE player_progress (
    id INTEGER PRIMARY KEY DEFAULT 1,
    level INTEGER DEFAULT 1,
    current_xp INTEGER DEFAULT 0,
    total_energy INTEGER DEFAULT 0,
    stars INTEGER DEFAULT 0,
    current_streak INTEGER DEFAULT 0,
    longest_streak INTEGER DEFAULT 0,
    total_blocking_minutes INTEGER DEFAULT 0
);

CREATE TABLE achievements (
    id TEXT PRIMARY KEY,
    category TEXT NOT NULL,
    progress INTEGER DEFAULT 0,
    target INTEGER NOT NULL,
    unlocked_at INTEGER,
    stars_reward INTEGER NOT NULL
);

CREATE TABLE sanctuary_decorations (
    id TEXT PRIMARY KEY,
    type TEXT NOT NULL,
    position_x REAL,
    position_y REAL,
    purchased_at INTEGER NOT NULL
);

CREATE INDEX idx_achievements_category ON achievements(category);
CREATE INDEX idx_locations_biome ON discovered_locations(biome_id);
```

### Module Structure

```
com.umbral.expedition/
├── data/
│   ├── dao/
│   │   ├── CompanionDao.kt
│   │   ├── LocationDao.kt
│   │   ├── ProgressDao.kt
│   │   ├── AchievementDao.kt
│   │   └── DecorationDao.kt
│   ├── entity/
│   │   ├── CompanionEntity.kt
│   │   ├── LocationEntity.kt
│   │   ├── ProgressEntity.kt
│   │   ├── AchievementEntity.kt
│   │   └── DecorationEntity.kt
│   └── repository/
│       ├── ExpeditionRepository.kt
│       └── ExpeditionRepositoryImpl.kt
├── domain/
│   ├── model/
│   │   ├── Companion.kt
│   │   ├── CompanionType.kt
│   │   ├── Location.kt
│   │   ├── Biome.kt
│   │   ├── Achievement.kt
│   │   ├── AchievementCategory.kt
│   │   └── PlayerProgress.kt
│   └── usecase/
│       ├── GainEnergyUseCase.kt
│       ├── DiscoverLocationUseCase.kt
│       ├── CaptureCompanionUseCase.kt
│       ├── EvolveCompanionUseCase.kt
│       ├── CheckAchievementsUseCase.kt
│       └── GetProgressUseCase.kt
├── presentation/
│   ├── map/
│   │   ├── ExpeditionMapScreen.kt
│   │   ├── ExpeditionMapViewModel.kt
│   │   └── components/
│   │       ├── BiomeMapCanvas.kt
│   │       ├── LocationMarker.kt
│   │       ├── FogOfWar.kt
│   │       └── MapControls.kt
│   ├── companion/
│   │   ├── CompanionDetailScreen.kt
│   │   ├── CompanionListScreen.kt
│   │   ├── CompanionViewModel.kt
│   │   └── components/
│   │       ├── CompanionCard.kt
│   │       ├── CompanionAnimation.kt
│   │       ├── EvolutionDialog.kt
│   │       └── CompanionStats.kt
│   ├── sanctuary/
│   │   ├── SanctuaryScreen.kt
│   │   ├── SanctuaryViewModel.kt
│   │   └── components/
│   │       ├── SanctuaryView.kt
│   │       └── ActiveCompanion.kt
│   ├── achievements/
│   │   ├── AchievementsScreen.kt
│   │   ├── AchievementsViewModel.kt
│   │   └── components/
│   │       ├── AchievementCard.kt
│   │       └── AchievementUnlockDialog.kt
│   └── components/
│       ├── EnergyDisplay.kt
│       ├── LevelProgress.kt
│       └── StreakIndicator.kt
└── di/
    └── ExpeditionModule.kt
```

### Integration Points

```kotlin
// 1. BlockingManager hook
class BlockingManager @Inject constructor(
    private val expeditionRepository: ExpeditionRepository,
    // ... existing deps
) {
    private suspend fun onBlockingSessionComplete(
        profileId: String,
        durationMinutes: Int
    ) {
        // Award energy
        val energy = calculateEnergy(durationMinutes)
        expeditionRepository.addEnergy(energy)

        // Check achievements
        expeditionRepository.checkBlockingAchievements(durationMinutes)
    }
}

// 2. HomeScreen integration
@Composable
fun HomeScreenContent(
    // ... existing params
    expeditionState: ExpeditionState,
    onExpeditionClick: () -> Unit
) {
    // Show active companion with animation
    ActiveCompanionCard(
        companion = expeditionState.activeCompanion,
        onClick = onExpeditionClick
    )

    // Show energy/level in header
    EnergyDisplay(energy = expeditionState.energy)
}

// 3. Navigation
sealed class Screen {
    // ... existing screens
    object ExpeditionMap : Screen()
    object CompanionList : Screen()
    object CompanionDetail : Screen()
    object Sanctuary : Screen()
    object Achievements : Screen()
}
```

### Fórmulas de Progresión

```kotlin
object ExpeditionFormulas {
    // Energía por minuto de bloqueo
    const val BASE_ENERGY_PER_MINUTE = 10

    // Multiplicador por racha
    fun getStreakMultiplier(streak: Int): Float = when {
        streak < 3 -> 1.0f
        streak < 7 -> 1.25f
        streak < 14 -> 1.5f
        streak < 30 -> 2.0f
        else -> 2.5f
    }

    // XP necesario para siguiente nivel
    fun xpForLevel(level: Int): Int = (100 * level * 1.2).toInt()

    // Costo de revelar locación
    fun locationRevealCost(index: Int): Int = 200 + (index * 50)

    // Energía para evolucionar compañero
    fun evolutionCost(currentState: Int): Int = when (currentState) {
        1 -> 1000   // Estado 1 → 2
        2 -> 3000   // Estado 2 → 3
        else -> Int.MAX_VALUE
    }
}
```

---

## Task Breakdown

| # | Task | Files Principales | Effort | Parallel |
|---|------|-------------------|--------|----------|
| 1 | Setup módulo Expedition + DB migrations | `ExpeditionModule.kt`, migrations | 4h | true |
| 2 | Entities, DAOs y Repository | `*Entity.kt`, `*Dao.kt`, `ExpeditionRepository.kt` | 6h | true |
| 3 | Domain models y UseCases | `model/*.kt`, `usecase/*.kt` | 4h | false (deps: 2) |
| 4 | Sistema de progresión (Energía, XP, Niveles) | `GainEnergyUseCase.kt`, `ProgressEntity.kt` | 4h | false (deps: 3) |
| 5 | Sistema de Logros | `Achievement*.kt`, `CheckAchievementsUseCase.kt` | 6h | true |
| 6 | ExpeditionMapScreen + Canvas rendering | `ExpeditionMapScreen.kt`, `BiomeMapCanvas.kt` | 8h | false (deps: 4) |
| 7 | Sistema de Compañeros (captura, evolución) | `Companion*.kt`, `CaptureCompanionUseCase.kt` | 6h | false (deps: 4) |
| 8 | UI Compañeros + Animaciones Lottie | `CompanionAnimation.kt`, `CompanionDetailScreen.kt` | 6h | false (deps: 7) |
| 9 | Integración BlockingManager + HomeScreen | `BlockingManager.kt`, `HomeScreen.kt` | 4h | false (deps: 4) |
| 10 | Assets + Polish + Testing | Assets integration, bug fixes | 8h | false (deps: all) |

**Total estimado:** ~56 horas (4-5 semanas a ritmo normal)

---

## Dependencies

### Internas
- `BlockingManager` - Hook para recompensar sesiones
- `StatsRepository` - Datos de tiempo bloqueado existente
- `UmbralDatabase` - Agregar nuevas tablas
- `MainNavigation` - Agregar rutas de Expedition

### Externas (nuevas)
```kotlin
// build.gradle.kts
implementation("com.airbnb.android:lottie-compose:6.3.0")
```

### Assets Requeridos
- 24 ilustraciones de compañeros (8 base × 3 estados)
- 15 ilustraciones de locaciones
- 1 mapa base del bioma Bosque
- ~35 animaciones Lottie

---

## Success Criteria (Technical)

| Criteria | Measurement |
|----------|-------------|
| Energía se gana correctamente | Test: 60 min = 600+ energía base |
| Mapa renderiza sin lag | Profiler: <16ms frame time |
| Animaciones 60fps | Profiler: no frame drops |
| DB migrations exitosas | Test: fresh install + upgrade paths |
| Offline 100% funcional | Test: airplane mode full flow |
| APK size delta | < 15MB adicionales |
| Memory overhead | < 50MB adicionales en runtime |

---

## Risk Mitigation

| Risk | Mitigation |
|------|------------|
| Canvas performance con zoom | Implementar viewport culling, solo renderizar visible |
| Assets muy pesados | Usar WebP, comprimir Lottie, lazy loading |
| Migrations complejas | Test migrations en cada PR, backup strategy |
| Scope creep | Priorizar P0, features P2 van a backlog |

---

## Files Summary

### Crear (nuevos)
- `app/src/main/java/com/umbral/expedition/**` (~40 archivos)
- `app/src/main/res/raw/lottie/**` (~35 animaciones)
- `app/src/main/res/drawable/expedition/**` (~40 ilustraciones)

### Modificar (existentes)
- `app/src/main/java/com/umbral/data/local/UmbralDatabase.kt` - Agregar DAOs
- `app/src/main/java/com/umbral/domain/blocking/BlockingManager.kt` - Hook callback
- `app/src/main/java/com/umbral/presentation/ui/screens/home/HomeScreen.kt` - Companion card
- `app/src/main/java/com/umbral/presentation/navigation/MainNavigation.kt` - Rutas
- `app/src/main/java/com/umbral/di/AppModule.kt` - Include ExpeditionModule

---

## Tasks Created

- [ ] #53 - Setup módulo Expedition + DB migrations (parallel: true, ~4h)
- [ ] #54 - Entities, DAOs y Repository base (parallel: true, ~6h)
- [ ] #55 - Domain models y UseCases (parallel: false, depends: #54, ~4h)
- [ ] #56 - Sistema de Logros completo (parallel: true, ~6h)
- [ ] #57 - ExpeditionMapScreen con Canvas (parallel: false, depends: #55, ~8h)
- [ ] #58 - Sistema de Compañeros completo (parallel: false, depends: #55, ~6h)
- [ ] #59 - UI Compañeros + Animaciones Lottie (parallel: false, depends: #58, ~6h)
- [ ] #60 - Integración BlockingManager + HomeScreen (parallel: false, depends: #55, ~4h)
- [ ] #61 - Assets + Polish + Testing (parallel: false, depends: all, ~8h)

**Summary:**
- Total tasks: 9
- Parallel tasks: 3 (#53, #54, #56)
- Sequential tasks: 6
- Estimated total effort: ~52 hours (4-5 semanas)

---

**Creado:** 2026-01-07T04:45:38Z
**Actualizado:** 2026-01-07T05:13:26Z
**Autor:** Technical Lead Agent
