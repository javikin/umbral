---
name: advanced-features
prd: advanced-features
description: Widgets, Quick Settings y Estadísticas mejoradas para Umbral
status: completed
priority: high
github: https://github.com/javikin/umbral/issues/39
created: 2026-01-06T02:54:04Z
updated: 2026-01-06T04:03:42Z
completed: 2026-01-06T04:03:42Z
progress: 100%
---

# Epic: Advanced Features - Widgets, Quick Settings & Stats

## Objetivo

Agregar funcionalidades avanzadas que permitan a los usuarios:
1. Ver el estado de bloqueo desde la pantalla de inicio (widgets)
2. Toggle rápido desde Quick Settings
3. Estadísticas detalladas de uso y bloqueo

---

## Análisis Técnico

### Tecnologías Requeridas

| Componente | Tecnología | Min API |
|------------|------------|---------|
| Widgets | Jetpack Glance 1.1.0 | API 26 |
| Quick Settings | TileService | API 24 |
| Stats DB | Room (existente) | API 21 |
| Charts | Compose Canvas | API 21 |

### Dependencias a Agregar

```kotlin
// build.gradle.kts
dependencies {
    // Glance for Widgets
    implementation("androidx.glance:glance-appwidget:1.1.0")
    implementation("androidx.glance:glance-material3:1.1.0")
}
```

### Arquitectura

```
┌─────────────────────────────────────────────────────────────┐
│                      PRESENTATION                           │
├─────────────────────────────────────────────────────────────┤
│  Glance Widgets    │  TileService    │  Stats Screen       │
│  - StatusWidget    │  - UmbralTile   │  - StatsViewModel   │
│  - StreakWidget    │                 │  - WeeklyChart      │
└─────────┬──────────┴────────┬────────┴──────────┬──────────┘
          │                   │                    │
          ▼                   ▼                    ▼
┌─────────────────────────────────────────────────────────────┐
│                        DOMAIN                               │
├─────────────────────────────────────────────────────────────┤
│  BlockingStateUseCase  │  StreakUseCase  │  StatsUseCase   │
└─────────┬──────────────┴────────┬────────┴──────────┬──────┘
          │                       │                    │
          ▼                       ▼                    ▼
┌─────────────────────────────────────────────────────────────┐
│                         DATA                                │
├─────────────────────────────────────────────────────────────┤
│  BlockingRepository    │  StreakRepository  │  StatsRepo   │
│  (existente)          │  (existente)       │  (nuevo)     │
└─────────────────────────────────────────────────────────────┘
```

---

## Tasks

### Task 1: Setup Glance Dependencies
**Prioridad:** Alta
**Tipo:** Setup
**Paralelo:** true

- Agregar dependencias de Glance al build.gradle.kts
- Crear estructura de carpetas para widgets
- Configurar WidgetTheme con colores de Umbral

### Task 2: Widget de Estado
**Prioridad:** Alta
**Tipo:** Feature
**Paralelo:** true (después de Task 1)

- Crear StatusWidget.kt (Glance)
- Crear StatusWidgetReceiver.kt
- Crear status_widget_info.xml
- Registrar en AndroidManifest.xml
- Implementar tamaños 2x1 y 2x2
- Conectar con BlockingRepository

### Task 3: Widget de Streak
**Prioridad:** Alta
**Tipo:** Feature
**Paralelo:** true (después de Task 1)

- Crear StreakWidget.kt (Glance)
- Crear StreakWidgetReceiver.kt
- Crear streak_widget_info.xml
- Registrar en AndroidManifest.xml
- Implementar mini calendario semanal
- Conectar con StreakRepository

### Task 4: Quick Settings Tile
**Prioridad:** Alta
**Tipo:** Feature
**Paralelo:** true

- Crear UmbralTileService.kt
- Registrar en AndroidManifest.xml
- Implementar estados (activo/inactivo)
- Manejar tap (toggle si no estricto)
- Manejar long press (abrir app)
- Respetar modo estricto

### Task 5: Stats Database Schema
**Prioridad:** Alta
**Tipo:** Database
**Paralelo:** true

- Crear BlockingEvent entity
- Crear BlockingEventDao
- Agregar migración a UmbralDatabase
- Crear StatsRepository

### Task 6: Stats UI - Pantalla Principal
**Prioridad:** Media
**Tipo:** Feature
**Depende de:** Task 5

- Crear StatsScreen.kt
- Crear StatsViewModel.kt
- Implementar tiempo bloqueado (hoy/semana)
- Implementar comparativa semanal

### Task 7: Stats UI - Gráfica y Top Apps
**Prioridad:** Media
**Tipo:** Feature
**Depende de:** Task 5, Task 6

- Crear WeeklyChart.kt (Compose Canvas)
- Crear TopAppsCard.kt
- Implementar contador de intentos
- Conectar navegación desde widget streak

---

## Dependencias

```
[Task 1: Setup] ──────────┬──────────────────────────────────┐
                          │                                   │
                          ▼                                   │
[Task 2: Widget Estado] ──┼──────────────────┐               │
                          │                   │               │
                          ▼                   │               │
[Task 3: Widget Streak] ──┼───────────────────┼───────────────┤
                          │                   │               │
[Task 4: Quick Settings] ─┘                   │               │
                                              │               │
[Task 5: Stats DB] ───────────────────────────┼───────────────┘
                                              │
                                              ▼
                          [Task 6: Stats Screen]
                                              │
                                              ▼
                          [Task 7: Charts & Top Apps]
```

**Paralelismo posible:**
- Tasks 1-4-5 pueden ejecutarse en paralelo
- Tasks 2-3 requieren Task 1 completada
- Tasks 6-7 requieren Task 5 completada

---

## Criterio de Done

### Por Feature
- [ ] Widgets aparecen en picker de Android
- [ ] Quick Settings tile funciona correctamente
- [ ] Estadísticas muestran datos reales
- [ ] UI consistente con tema Umbral
- [ ] Textos en español
- [ ] Sin crashes

### Testing
- [ ] Probado en Android 12+ (Glance)
- [ ] Probado en Android 8+ (compatibilidad)
- [ ] Probado toggle modo estricto vs normal
- [ ] Probado actualización de widgets en tiempo real

---

## Estimación

| Task | Estimación |
|------|------------|
| Task 1: Setup | 2h |
| Task 2: Widget Estado | 6h |
| Task 3: Widget Streak | 4h |
| Task 4: Quick Settings | 4h |
| Task 5: Stats DB | 3h |
| Task 6: Stats Screen | 4h |
| Task 7: Charts | 4h |
| Testing & Polish | 3h |
| **Total** | **~30h (4-5 días)** |

---

## Tasks Created

- [ ] #40 - Setup Glance Dependencies (parallel: true)
- [ ] #41 - Widget de Estado (parallel: true, depends: #40)
- [ ] #42 - Widget de Streak (parallel: true, depends: #40)
- [ ] #43 - Quick Settings Tile (parallel: true)
- [ ] #44 - Stats Database Schema (parallel: true)
- [ ] #45 - Stats Screen UI (parallel: false, depends: #44)
- [ ] #46 - Stats Charts & Top Apps (parallel: false, depends: #44, #45)

Total tasks: 7
Parallel tasks: 5 (can start immediately: #40, #43, #44)
Sequential tasks: 2 (#45, #46)
