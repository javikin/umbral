---
name: gamification
description: Sistema de gamificación "Expedición" - exploración y compañeros para motivar el uso consciente del móvil
status: backlog
created: 2026-01-07T04:36:05Z
scope: mvp-plus
premium_prd: gamification-premium (pendiente)
---

# PRD: Sistema de Gamificación - Expedición

## Executive Summary

**Expedición** es un sistema de gamificación que transforma el tiempo de bloqueo en una aventura de exploración y descubrimiento. Los usuarios exploran un mapa de biomas naturales, descubren locaciones, capturan compañeros y construyen su santuario personal mientras practican hábitos digitales saludables.

### Propuesta de Valor
- **Motivación intrínseca**: El progreso visible en el mapa crea sensación de logro
- **Conexión emocional**: Los compañeros evolucionan con el usuario
- **Recompensa tangible**: Cada minuto de bloqueo se traduce en recursos y descubrimientos
- **Comunidad**: Compartir logros y competir amistosamente (roadmap)

### Inspiración
- **Forest App**: Mecánica de semillas/árboles que crecen con el tiempo
- **Habitica**: RPG de hábitos con avatares y mascotas
- **Pokémon GO**: Exploración y colección de criaturas

---

## Problem Statement

### El Problema
Los usuarios de Umbral completan sus sesiones de bloqueo pero carecen de motivación a largo plazo. Una vez que la "novedad" del bloqueo pasa, el engagement decrece y los usuarios abandonan la app o reducen su uso.

### Por Qué Ahora
- Umbral V1 está feature-complete con bloqueo robusto
- Los competidores (Forest, Brick) demuestran que la gamificación aumenta retención 3-5x
- Las herramientas de IA permiten crear assets visuales de alta calidad rápidamente
- El ecosistema Lottie ofrece animaciones profesionales accesibles

### Hipótesis
Si transformamos el tiempo de bloqueo en progreso tangible (exploración + compañeros), los usuarios mantendrán rachas más largas y usarán la app consistentemente por más tiempo.

---

## User Stories

### Persona Principal: "El Explorador Consciente"
**María, 28 años, diseñadora**
- Usa Umbral para enfocarse durante trabajo
- Le gustan los juegos casuales y coleccionables
- Motivada por completar colecciones y ver progreso visual
- Comparte logros con amigos en redes sociales

### Persona Secundaria: "El Competidor Social"
**Carlos, 24 años, estudiante**
- Usa Umbral para estudiar sin distracciones
- Motivado por rankings y comparación con amigos
- Le gustan los retos y logros difíciles
- Quiere demostrar disciplina a su grupo

---

## User Journey: MVP+

### Journey 1: Primer Contacto
```
Usuario completa onboarding
    ↓
Ve introducción a Expedición (3 pantallas)
    ↓
Recibe primer compañero "Lumina" (starter)
    ↓
Ve mapa con primer bioma parcialmente revelado
    ↓
Inicia primera sesión de bloqueo
    ↓
Al completar: gana Energía, revela parte del mapa
    ↓
Descubre primera locación
```

### Journey 2: Sesión Diaria
```
Usuario abre app
    ↓
Ve progreso del día (Energía acumulada, racha)
    ↓
Compañero le saluda con animación
    ↓
Inicia sesión de bloqueo
    ↓
Durante bloqueo: puede ver mapa (opcional)
    ↓
Al completar:
    - +Energía basada en duración
    - Progreso en mapa
    - Posible: nuevo compañero/evolución/logro
    ↓
Celebración animada
    ↓
Revisa nuevo descubrimiento
```

### Journey 3: Evolución de Compañero
```
Usuario acumula suficiente Energía
    ↓
Notificación: "Lumina está lista para evolucionar"
    ↓
Abre pantalla de compañero
    ↓
Animación de evolución (Lottie)
    ↓
Nuevo diseño + habilidad desbloqueada
    ↓
Logro: "Primera Evolución"
    ↓
Comparte en redes (opcional)
```

---

## Requirements

### Functional Requirements

#### FR-1: Sistema de Progresión
| ID | Requirement | Priority |
|----|-------------|----------|
| FR-1.1 | Ganar Energía proporcional al tiempo de bloqueo | P0 |
| FR-1.2 | Sistema de niveles (1-50) con XP | P0 |
| FR-1.3 | Multiplicadores por racha (1.0x → 2.5x) | P1 |
| FR-1.4 | Logros desbloqueables (30 en MVP+) | P1 |
| FR-1.5 | Títulos/badges visibles en perfil | P2 |

#### FR-2: Mapa de Exploración
| ID | Requirement | Priority |
|----|-------------|----------|
| FR-2.1 | Mapa scrollable de 1 bioma (Bosque Inicial) | P0 |
| FR-2.2 | 15 locaciones descubribles | P0 |
| FR-2.3 | Fog of war que se revela con Energía | P0 |
| FR-2.4 | Animaciones de descubrimiento | P1 |
| FR-2.5 | Locaciones con lore/historia corta | P2 |

#### FR-3: Sistema de Compañeros
| ID | Requirement | Priority |
|----|-------------|----------|
| FR-3.1 | 8 compañeros base capturables | P0 |
| FR-3.2 | Compañero starter (Lumina) al iniciar | P0 |
| FR-3.3 | 2 estados de evolución por compañero | P1 |
| FR-3.4 | Animaciones idle/happy para cada uno | P1 |
| FR-3.5 | Habilidades pasivas por compañero | P2 |

#### FR-4: Economía Virtual
| ID | Requirement | Priority |
|----|-------------|----------|
| FR-4.1 | Moneda: Energía (ganada por bloqueo) | P0 |
| FR-4.2 | Moneda secundaria: Estrellas (logros) | P1 |
| FR-4.3 | Tienda básica (decoraciones santuario) | P2 |
| FR-4.4 | Sin monetización real en MVP+ | P0 |

#### FR-5: Santuario Personal
| ID | Requirement | Priority |
|----|-------------|----------|
| FR-5.1 | Vista de santuario con compañeros | P1 |
| FR-5.2 | Compañero activo visible en Home | P0 |
| FR-5.3 | Decoraciones básicas (5 items) | P2 |
| FR-5.4 | Animaciones ambientales (día/noche) | P2 |

### Non-Functional Requirements

#### NFR-1: Performance
| ID | Requirement | Target |
|----|-------------|--------|
| NFR-1.1 | Carga del mapa | < 500ms |
| NFR-1.2 | Animaciones Lottie | 60fps |
| NFR-1.3 | Memoria adicional | < 50MB |
| NFR-1.4 | Tamaño APK adicional | < 15MB |

#### NFR-2: Offline Support
| ID | Requirement | Target |
|----|-------------|--------|
| NFR-2.1 | 100% funcional offline | Required |
| NFR-2.2 | Sync cuando hay conexión | Future |
| NFR-2.3 | Assets cacheados localmente | Required |

#### NFR-3: Accessibility
| ID | Requirement | Target |
|----|-------------|--------|
| NFR-3.1 | Animaciones desactivables | Required |
| NFR-3.2 | Alto contraste disponible | P2 |
| NFR-3.3 | Screen reader compatible | P1 |

---

## Technical Architecture: MVP+

### Stack Tecnológico

```
┌─────────────────────────────────────────────────────────┐
│                    PRESENTATION                         │
├─────────────────────────────────────────────────────────┤
│  Jetpack Compose UI                                     │
│  ├── ExpeditionMapScreen (Canvas + gestures)           │
│  ├── CompanionScreen (Lottie animations)               │
│  ├── SanctuaryScreen (Compose + Lottie)                │
│  ├── AchievementsScreen (LazyGrid)                     │
│  └── Components: CompanionCard, LocationCard, etc.     │
├─────────────────────────────────────────────────────────┤
│  Lottie Compose                                         │
│  ├── Companion animations (idle, happy, evolve)        │
│  ├── Discovery animations                               │
│  ├── Achievement unlock animations                      │
│  └── Ambient effects (particles, glow)                 │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│                     DOMAIN                              │
├─────────────────────────────────────────────────────────┤
│  Use Cases                                              │
│  ├── GainEnergyUseCase                                 │
│  ├── DiscoverLocationUseCase                           │
│  ├── EvolveCompanionUseCase                            │
│  ├── UnlockAchievementUseCase                          │
│  └── CalculateProgressUseCase                          │
├─────────────────────────────────────────────────────────┤
│  Entities                                               │
│  ├── Companion (id, type, evolutionState, energy)      │
│  ├── Location (id, biome, discovered, lore)            │
│  ├── Achievement (id, type, progress, unlocked)        │
│  ├── PlayerProgress (level, xp, energy, stars)         │
│  └── Biome (id, locations, companions, unlocked)       │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│                      DATA                               │
├─────────────────────────────────────────────────────────┤
│  Room Database                                          │
│  ├── companions (user's collection)                    │
│  ├── discovered_locations                              │
│  ├── achievements                                       │
│  ├── player_progress                                   │
│  └── sanctuary_decorations                             │
├─────────────────────────────────────────────────────────┤
│  DataStore Preferences                                  │
│  ├── active_companion_id                               │
│  ├── expedition_intro_seen                             │
│  ├── animation_settings                                │
│  └── last_daily_reward                                 │
├─────────────────────────────────────────────────────────┤
│  Assets (bundled)                                       │
│  ├── /raw/lottie/*.json (~50 animations)              │
│  ├── /drawable/companions/*.webp                       │
│  ├── /drawable/locations/*.webp                        │
│  └── /drawable/map/*.webp                              │
└─────────────────────────────────────────────────────────┘
```

### Módulos de Feature

```
app/
├── src/main/java/com/umbral/
│   ├── expedition/
│   │   ├── data/
│   │   │   ├── dao/
│   │   │   │   ├── CompanionDao.kt
│   │   │   │   ├── LocationDao.kt
│   │   │   │   └── ProgressDao.kt
│   │   │   ├── entity/
│   │   │   │   ├── CompanionEntity.kt
│   │   │   │   ├── LocationEntity.kt
│   │   │   │   └── ProgressEntity.kt
│   │   │   └── repository/
│   │   │       ├── ExpeditionRepository.kt
│   │   │       └── ExpeditionRepositoryImpl.kt
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   │   ├── Companion.kt
│   │   │   │   ├── Location.kt
│   │   │   │   ├── Biome.kt
│   │   │   │   └── PlayerProgress.kt
│   │   │   └── usecase/
│   │   │       ├── GainEnergyUseCase.kt
│   │   │       ├── DiscoverLocationUseCase.kt
│   │   │       └── EvolveCompanionUseCase.kt
│   │   └── presentation/
│   │       ├── map/
│   │       │   ├── ExpeditionMapScreen.kt
│   │       │   ├── ExpeditionMapViewModel.kt
│   │       │   └── components/
│   │       │       ├── BiomeMap.kt
│   │       │       ├── LocationMarker.kt
│   │       │       └── FogOfWar.kt
│   │       ├── companion/
│   │       │   ├── CompanionScreen.kt
│   │       │   ├── CompanionViewModel.kt
│   │       │   └── components/
│   │       │       ├── CompanionAnimation.kt
│   │       │       └── EvolutionDialog.kt
│   │       └── sanctuary/
│   │           ├── SanctuaryScreen.kt
│   │           └── SanctuaryViewModel.kt
│   ├── achievements/
│   │   ├── data/
│   │   ├── domain/
│   │   └── presentation/
│   └── economy/
│       ├── data/
│       ├── domain/
│       └── presentation/
```

### Integración con Sistema Existente

```kotlin
// BlockingManager integration
class BlockingManager {
    // Existing code...

    // NEW: Hook for expedition rewards
    private fun onBlockingSessionComplete(
        profileId: String,
        durationMinutes: Int
    ) {
        // Calculate energy gained
        val baseEnergy = durationMinutes * ENERGY_PER_MINUTE
        val streakMultiplier = expeditionRepository.getStreakMultiplier()
        val totalEnergy = (baseEnergy * streakMultiplier).toInt()

        // Award energy
        expeditionRepository.addEnergy(totalEnergy)

        // Check for discoveries
        expeditionRepository.checkForDiscoveries(totalEnergy)

        // Check achievements
        achievementManager.checkBlockingAchievements(durationMinutes)
    }
}
```

### Esquema de Base de Datos

```sql
-- Tabla: companions (compañeros del usuario)
CREATE TABLE companions (
    id TEXT PRIMARY KEY,
    type TEXT NOT NULL,           -- "lumina", "terra", "aqua", etc.
    name TEXT,                    -- nombre personalizado (opcional)
    evolution_state INTEGER DEFAULT 1,  -- 1, 2, 3
    energy_invested INTEGER DEFAULT 0,
    captured_at INTEGER NOT NULL,
    is_active INTEGER DEFAULT 0,
    UNIQUE(type)                  -- solo uno de cada tipo
);

-- Tabla: discovered_locations
CREATE TABLE discovered_locations (
    id TEXT PRIMARY KEY,
    biome_id TEXT NOT NULL,
    discovered_at INTEGER NOT NULL,
    energy_spent INTEGER NOT NULL,
    lore_read INTEGER DEFAULT 0
);

-- Tabla: player_progress
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

-- Tabla: achievements
CREATE TABLE achievements (
    id TEXT PRIMARY KEY,
    category TEXT NOT NULL,       -- "blocking", "exploration", "companion", "social"
    progress INTEGER DEFAULT 0,
    target INTEGER NOT NULL,
    unlocked_at INTEGER,
    stars_reward INTEGER NOT NULL
);

-- Tabla: sanctuary_decorations
CREATE TABLE sanctuary_decorations (
    id TEXT PRIMARY KEY,
    type TEXT NOT NULL,
    position_x REAL,
    position_y REAL,
    purchased_at INTEGER NOT NULL
);
```

---

## Assets Required (MVP+)

### Compañeros (~24 ilustraciones)
| Compañero | Base | Evo 1 | Evo 2 | Total |
|-----------|------|-------|-------|-------|
| Lumina (luz/starter) | 1 | 1 | 1 | 3 |
| Terra (tierra) | 1 | 1 | 1 | 3 |
| Aqua (agua) | 1 | 1 | 1 | 3 |
| Ventus (aire) | 1 | 1 | 1 | 3 |
| Flora (plantas) | 1 | 1 | 1 | 3 |
| Ignis (fuego) | 1 | 1 | 1 | 3 |
| Noctis (noche) | 1 | 1 | 1 | 3 |
| Aurora (amanecer) | 1 | 1 | 1 | 3 |

### Animaciones Lottie (~35)
| Categoría | Cantidad | Descripción |
|-----------|----------|-------------|
| Companion idle | 8 | Animación de espera por compañero |
| Companion happy | 8 | Celebración al completar sesión |
| Evolution | 3 | Transición genérica de evolución |
| Discovery | 5 | Revelar nueva locación |
| Achievement | 3 | Desbloqueo de logro |
| Ambient | 5 | Partículas, brillos, efectos |
| UI | 3 | Botones, transiciones |

### Mapa y Locaciones (~20 ilustraciones)
| Asset | Cantidad |
|-------|----------|
| Bioma Bosque (base map) | 1 |
| Locaciones únicas | 15 |
| Iconos de locación | 15 |
| UI del mapa | 4 |

### Estilo Visual
- **Paleta**: Colores naturales, verdes, azules, tierras
- **Estilo**: Ghibli-inspired / Acuarela digital
- **Herramientas**: Midjourney v6, DALL-E 3, Illustrator touch-ups
- **Formato**: WebP para ilustraciones, JSON para Lottie

---

## Success Criteria

### Métricas Primarias (MVP+)
| Métrica | Target | Medición |
|---------|--------|----------|
| Retención D7 | +25% vs actual | Analytics |
| Sesiones/semana | +40% | Analytics |
| Duración promedio sesión | +15% | Analytics |
| Racha promedio | >5 días | Room DB |

### Métricas Secundarias
| Métrica | Target | Medición |
|---------|--------|----------|
| Compañeros capturados avg | >3 | Room DB |
| Locaciones descubiertas avg | >8 | Room DB |
| Logros desbloqueados avg | >10 | Room DB |
| NPS Score | >50 | Survey |

### Criterios de Aceptación
- [ ] Usuario puede ganar Energía por tiempo de bloqueo
- [ ] Mapa muestra progreso de exploración
- [ ] Al menos 1 compañero evoluciona correctamente
- [ ] 30 logros desbloqueables funcionan
- [ ] Performance: <500ms carga, 60fps animaciones
- [ ] 100% funcional offline
- [ ] Assets de calidad profesional

---

## Constraints & Assumptions

### Constraints
1. **Sin backend**: MVP+ es 100% local, sin sync
2. **Tamaño APK**: Máximo +15MB adicionales
3. **Timeline**: 4-5 semanas de desarrollo
4. **Assets**: Generados con AI + recursos gratuitos/comprados

### Assumptions
1. Usuarios responden positivamente a gamificación tipo Forest/Habitica
2. AI puede generar assets de calidad suficiente
3. Lottie animations no impactan significativamente batería
4. El concepto "Expedición" resuena mejor que alternativas

### Risks
| Risk | Probability | Impact | Mitigation |
|------|-------------|--------|------------|
| Assets AI no suficiente calidad | Media | Alto | Presupuesto para assets profesionales |
| Performance con muchas animaciones | Baja | Alto | Lazy loading, animaciones opcionales |
| Feature creep durante desarrollo | Alta | Medio | Scope estricto, P0 primero |
| Usuarios no enganchan con mecánica | Media | Alto | A/B testing, feedback temprano |

---

## Out of Scope (MVP+)

### Explícitamente NO incluido:
- ❌ Múltiples biomas (solo Bosque Inicial)
- ❌ Sistema social/amigos
- ❌ Leaderboards
- ❌ Trading de compañeros
- ❌ Monetización (compras in-app)
- ❌ Backend/cloud sync
- ❌ Animaciones 3D
- ❌ Efectos de clima/estaciones
- ❌ Eventos especiales/temporadas
- ❌ Notificaciones push de expedición

---

## Dependencies

### Internas
- Sistema de bloqueo funcional (completado)
- StatsRepository para métricas (completado)
- Integración con Home screen (modificación menor)

### Externas
- Lottie Compose library
- Assets visuales (ilustraciones, animaciones)
- Room migrations para nuevas tablas

### Equipo
- 1 desarrollador Android (4-5 semanas)
- Assets: AI generation + curation (~1 semana paralelo)

---

## Roadmap a Premium

### Versión Premium (PRD separado: `gamification-premium`)

La versión Premium expandirá significativamente el sistema con:

#### Contenido Expandido
- 5 biomas adicionales (Montaña, Costa, Desierto, Tundra, Volcán)
- 40+ compañeros totales (8 por bioma)
- 100+ locaciones
- 100+ logros

#### Características Avanzadas
- **Sistema Social**: Amigos, leaderboards, trading, challenges grupales
- **Backend Supabase**: Cloud sync, profiles, realtime updates
- **Animaciones Premium**: Rive interactivo, efectos AGSL
- **Santuario 3D**: Visualización con Filament/SceneView
- **Eventos Temporales**: Estaciones, eventos especiales, limited items
- **Monetización**: Battle pass, cosmetics (no P2W)

#### Stack Premium
```
MVP+ Stack (Compose + Lottie + Room)
    +
├── Rive Android (interactive animations)
├── Filament/SceneView (3D sanctuary)
├── AGSL Shaders (weather effects)
├── Supabase SDK (backend)
│   ├── Auth (accounts)
│   ├── Realtime (social)
│   ├── Storage (user content)
│   └── Edge Functions (leaderboards)
└── Firebase (analytics, push)
```

#### Timeline Premium
- **Estimado**: 14-18 semanas adicionales
- **Prerequisito**: MVP+ completado y validado
- **PRD**: Crear después de validar MVP+

---

## Implementation Plan Preview

### Semana 1: Foundation
- Setup módulos expedition, achievements, economy
- Room migrations para nuevas tablas
- Entidades y DAOs básicos
- Integración hook en BlockingManager

### Semana 2: Core Systems
- Sistema de Energía completo
- Sistema de niveles/XP
- Achievement framework
- Unit tests

### Semana 3: Map & Exploration
- ExpeditionMapScreen con Canvas
- Fog of war system
- Location discovery logic
- Mapa del Bioma Bosque

### Semana 4: Companions
- Companion system completo
- Evolución
- Animaciones Lottie integradas
- SanctuaryScreen básico

### Semana 5: Polish & Assets
- Integración final de assets
- Animations tuning
- Bug fixes
- Testing en dispositivos
- Release

---

## Appendix

### A. Compañeros Detallados

| Compañero | Elemento | Habilidad Pasiva | Requisito Captura |
|-----------|----------|------------------|-------------------|
| Lumina | Luz | +5% XP ganado | Starter (gratis) |
| Terra | Tierra | +5% Energía | 500 Energía total |
| Aqua | Agua | -10% tiempo evolución | Descubrir Lago |
| Ventus | Aire | +1 día racha perdón | 7 días racha |
| Flora | Planta | Decoraciones -20% | 10 locaciones |
| Ignis | Fuego | +10% Energía en racha 5+ | 14 días racha |
| Noctis | Noche | 2x Energía sesiones nocturnas | 10 sesiones nocturnas |
| Aurora | Amanecer | Bonus diario +50 | 30 días total uso |

### B. Logros MVP+ (30 total)

**Bloqueo (10)**
1. Primer Paso - Completar primera sesión
2. Hora Dorada - 60 min en una sesión
3. Maratonista - 120 min en una sesión
4. Consistente - 7 días seguidos
5. Dedicado - 14 días seguidos
6. Maestro - 30 días seguidos
7. Centurión - 100 sesiones totales
8. Mil Minutos - 1000 min bloqueados
9. Iron Will - 5000 min bloqueados
10. Leyenda - 10000 min bloqueados

**Exploración (10)**
1. Explorador Novato - Primera locación
2. Cartógrafo - 5 locaciones
3. Aventurero - 10 locaciones
4. Maestro Explorador - 15 locaciones (todas)
5. Lector de Lore - Leer 5 historias
6. Historiador - Leer todas las historias
7. Bioma Completo - 100% del Bosque
8. Coleccionista - Todos los compañeros base
9. Sin Piedra Sin Voltear - Todos los secretos
10. Speedrunner - Completar bioma en <14 días

**Compañeros (10)**
1. Primer Amigo - Capturar primer compañero
2. Dúo - 2 compañeros
3. Equipo - 4 compañeros
4. Todos Juntos - 8 compañeros
5. Primera Evolución - Evolucionar por primera vez
6. Evolucionista - 3 evoluciones
7. Maestro Criador - Todas las evoluciones
8. Mejor Amigo - 1000 Energía en un compañero
9. Vínculo Eterno - Evolución máxima
10. Santuario Lleno - Todos los compañeros max evo

---

**Creado:** 2026-01-07T04:36:05Z
**Autor:** Product Manager Agent
**Scope:** MVP+ (4-5 semanas)
**Premium PRD:** Pendiente de crear post-validación MVP+
