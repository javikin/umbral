---
name: advanced-features
description: Widgets y Quick Settings para acceso rápido a Umbral
status: ready
priority: high
created: 2026-01-06T02:29:30Z
updated: 2026-01-06T02:29:30Z
---

# PRD: Advanced Features - Widgets & Quick Settings

## Resumen Ejecutivo

Agregar widgets para la pantalla de inicio y Quick Settings tile para permitir a los usuarios ver el estado de bloqueo y controlarlo sin abrir la app.

---

## Problema

Actualmente, los usuarios deben:
1. Abrir la app Umbral para ver si el bloqueo está activo
2. Navegar dentro de la app para activar/desactivar
3. No hay forma rápida de ver el streak de días

Esto genera **fricción innecesaria** para usuarios que quieren un vistazo rápido al estado.

---

## Solución Propuesta

### 1. Widget de Estado (Home Screen)

**Tamaño:** 2x1 (pequeño) y 2x2 (mediano)

**Información mostrada:**
- Icono de candado (abierto/cerrado)
- Estado: "Bloqueado" / "Sin bloqueo"
- Perfil activo (si hay bloqueo)
- Tap → Abre la app

**Diseño 2x1:**
```
┌─────────────────────────┐
│ 🔒  Bloqueado           │
│     Mi Primer Perfil    │
└─────────────────────────┘
```

**Diseño 2x2:**
```
┌─────────────────────────┐
│                         │
│       🔒                │
│    Bloqueado            │
│  Mi Primer Perfil       │
│    3 apps · Estricto    │
│                         │
└─────────────────────────┘
```

### 2. Widget de Streak (Home Screen)

**Tamaño:** 2x2

**Información mostrada:**
- Número de días de streak
- Icono de fuego animado (si streak > 0)
- Mini calendario de la semana
- Tap → Abre estadísticas

**Diseño:**
```
┌─────────────────────────┐
│     🔥 12               │
│       días              │
│                         │
│  L  M  X  J  V  S  D    │
│  ●  ●  ●  ●  ●  ○  ○    │
└─────────────────────────┘
```

### 3. Quick Settings Tile

**Ubicación:** Panel de Quick Settings (swipe down)

**Estados:**
- **Inactivo:** Icono gris, "Umbral: Off"
- **Activo:** Icono púrpura, "Umbral: [Perfil]"

**Interacción:**
- **Tap:** Toggle bloqueo (si modo no-estricto)
- **Long press:** Abrir app
- **Si modo estricto:** Mostrar toast "Requiere NFC/QR para desbloquear"

**Diseño:**
```
┌─────────┐     ┌─────────┐
│   🛡️    │     │   🛡️    │
│  Off    │ ←→  │ Activo  │
└─────────┘     └─────────┘
```

---

## User Stories

### US1: Ver estado sin abrir app
> Como usuario, quiero ver si mis apps están bloqueadas desde mi pantalla de inicio, para no tener que abrir Umbral cada vez.

**Criterios de aceptación:**
- [ ] Widget muestra estado actual (bloqueado/desbloqueado)
- [ ] Widget se actualiza en tiempo real cuando cambia el estado
- [ ] Widget muestra nombre del perfil activo

### US2: Ver mi racha de días
> Como usuario, quiero ver mi streak de días en la pantalla de inicio, para mantenerme motivado.

**Criterios de aceptación:**
- [ ] Widget muestra número de días consecutivos
- [ ] Widget muestra mini calendario de la semana
- [ ] Widget se actualiza diariamente

### US3: Toggle rápido desde Quick Settings
> Como usuario, quiero activar/desactivar el bloqueo desde Quick Settings, para hacerlo más rápido.

**Criterios de aceptación:**
- [ ] Tile aparece en Quick Settings después de agregarlo
- [ ] Tap activa/desactiva el bloqueo (si no es modo estricto)
- [ ] Muestra estado actual del bloqueo
- [ ] Long press abre la app

### US4: Modo estricto respetado
> Como usuario en modo estricto, quiero que el Quick Settings NO pueda desactivar el bloqueo, para mantener mi compromiso.

**Criterios de aceptación:**
- [ ] En modo estricto, tap muestra mensaje "Requiere NFC/QR"
- [ ] No se puede desactivar desde Quick Settings en modo estricto

---

## Requisitos Técnicos

### Widgets (Jetpack Glance)

**Tecnología:** Jetpack Glance (Compose para widgets)
- Usa Compose-like syntax
- Integración nativa con Android 12+
- Backward compatible con Android 8+

**Archivos a crear:**
```
app/src/main/java/com/umbral/
├── glance/
│   ├── StatusWidget.kt
│   ├── StatusWidgetReceiver.kt
│   ├── StreakWidget.kt
│   ├── StreakWidgetReceiver.kt
│   └── theme/
│       └── WidgetTheme.kt
```

**Configuración:**
```xml
<!-- AndroidManifest.xml -->
<receiver android:name=".glance.StatusWidgetReceiver"
    android:exported="true">
    <intent-filter>
        <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
    </intent-filter>
    <meta-data
        android:name="android.appwidget.provider"
        android:resource="@xml/status_widget_info" />
</receiver>
```

### Quick Settings Tile

**Tecnología:** TileService (API 24+)
- Extiende `TileService`
- Requiere permiso en manifest

**Archivos a crear:**
```
app/src/main/java/com/umbral/
├── service/
│   └── UmbralTileService.kt
```

**Configuración:**
```xml
<!-- AndroidManifest.xml -->
<service
    android:name=".service.UmbralTileService"
    android:icon="@drawable/ic_tile"
    android:label="@string/tile_label"
    android:permission="android.permission.BIND_QUICK_SETTINGS_TILE"
    android:exported="true">
    <intent-filter>
        <action android:name="android.service.quicksettings.action.QS_TILE" />
    </intent-filter>
</service>
```

---

## Dependencias

```kotlin
// build.gradle.kts
dependencies {
    // Glance for Widgets
    implementation("androidx.glance:glance-appwidget:1.1.0")
    implementation("androidx.glance:glance-material3:1.1.0")
}
```

---

## Diseño Visual

### Colores (siguiendo tema Umbral)
- **Fondo widget:** `#1A1D26` (dark surface)
- **Texto primario:** `#FFFFFF`
- **Texto secundario:** `#9CA3AF`
- **Acento (activo):** `#8B5CF6` (púrpura)
- **Acento (streak):** `#F59E0B` (naranja/fuego)

### Iconos
- Usar Material Icons existentes
- Candado: `Icons.Default.Lock` / `Icons.Default.LockOpen`
- Fuego: `Icons.Default.LocalFireDepartment`
- Escudo: `Icons.Default.Shield`

---

## Scope y Estimación

| Feature | Complejidad | Estimación |
|---------|-------------|------------|
| Widget Estado 2x1 | Media | 4h |
| Widget Estado 2x2 | Media | 2h (incremental) |
| Widget Streak 2x2 | Media | 4h |
| Quick Settings Tile | Media | 4h |
| Testing & Polish | - | 4h |
| **Total** | | **~18h (2-3 días)** |

---

## Criterios de Éxito

1. **Funcionalidad:**
   - [ ] Ambos widgets se pueden agregar desde el picker
   - [ ] Quick Settings tile funciona correctamente
   - [ ] Estados se actualizan en tiempo real

2. **UX:**
   - [ ] Widgets se ven consistentes con el tema de la app
   - [ ] Interacciones son responsivas (< 100ms)
   - [ ] Textos en español

3. **Técnico:**
   - [ ] Compatible con Android 8+ (API 26+)
   - [ ] Sin crashes
   - [ ] Bajo consumo de batería

---

## 4. Módulo de Estadísticas Mejoradas

### Pantalla Principal de Estadísticas

**Información mostrada:**
- Tiempo total bloqueado (hoy, semana, mes)
- Intentos de abrir apps bloqueadas
- Apps más bloqueadas (top 5)
- Gráfica de actividad semanal/mensual
- Comparativa con semana anterior

**Diseño:**
```
┌─────────────────────────────────────┐
│ Estadísticas                        │
├─────────────────────────────────────┤
│                                     │
│  Tiempo bloqueado hoy               │
│  ████████████░░░░░  4h 32m          │
│                                     │
│  Esta semana        vs anterior     │
│  28h 15m            +12% ↑          │
│                                     │
│  ┌─────────────────────────────┐    │
│  │     📊 Gráfica semanal      │    │
│  │   ▁ ▃ ▅ ▇ █ ▃ ▁             │    │
│  │   L M X J V S D             │    │
│  └─────────────────────────────┘    │
│                                     │
│  Intentos bloqueados: 47            │
│                                     │
│  Top apps bloqueadas:               │
│  1. Instagram     - 23 intentos     │
│  2. TikTok        - 12 intentos     │
│  3. Twitter       - 8 intentos      │
│                                     │
└─────────────────────────────────────┘
```

### User Stories Estadísticas

### US5: Ver tiempo bloqueado
> Como usuario, quiero ver cuánto tiempo he tenido apps bloqueadas, para medir mi progreso.

**Criterios de aceptación:**
- [ ] Muestra tiempo bloqueado hoy
- [ ] Muestra tiempo bloqueado esta semana
- [ ] Muestra comparativa con semana anterior (% cambio)

### US6: Ver intentos de acceso
> Como usuario, quiero ver cuántas veces intenté abrir apps bloqueadas, para ser consciente de mis hábitos.

**Criterios de aceptación:**
- [ ] Contador de intentos por día
- [ ] Lista de top 5 apps más intentadas
- [ ] Gráfica de intentos por hora del día

### US7: Gráfica de actividad
> Como usuario, quiero ver una gráfica de mi actividad semanal/mensual, para ver patrones.

**Criterios de aceptación:**
- [ ] Gráfica de barras por día de la semana
- [ ] Toggle entre vista semanal y mensual
- [ ] Indicador del día actual

---

### Requisitos Técnicos - Estadísticas

**Base de datos (Room):**
```kotlin
@Entity(tableName = "blocking_events")
data class BlockingEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val eventType: EventType, // BLOCK_STARTED, BLOCK_ENDED, APP_ATTEMPT
    val profileId: Long?,
    val packageName: String?, // Para APP_ATTEMPT
    val durationMinutes: Int? // Para BLOCK_ENDED
)

enum class EventType {
    BLOCK_STARTED,
    BLOCK_ENDED,
    APP_ATTEMPT
}
```

**Queries necesarios:**
```kotlin
@Dao
interface BlockingEventDao {
    @Query("SELECT SUM(durationMinutes) FROM blocking_events WHERE eventType = 'BLOCK_ENDED' AND timestamp >= :startOfDay")
    suspend fun getTodayBlockedMinutes(startOfDay: Long): Int?

    @Query("SELECT COUNT(*) FROM blocking_events WHERE eventType = 'APP_ATTEMPT' AND timestamp >= :startOfDay")
    suspend fun getTodayAttempts(startOfDay: Long): Int

    @Query("SELECT packageName, COUNT(*) as count FROM blocking_events WHERE eventType = 'APP_ATTEMPT' AND timestamp >= :startOfWeek GROUP BY packageName ORDER BY count DESC LIMIT 5")
    suspend fun getTopBlockedApps(startOfWeek: Long): List<AppAttemptCount>
}
```

**Archivos a crear:**
```
app/src/main/java/com/umbral/
├── data/
│   ├── local/
│   │   ├── entity/BlockingEvent.kt
│   │   └── dao/BlockingEventDao.kt
│   └── repository/StatsRepository.kt
├── domain/
│   └── usecase/
│       ├── GetTodayStatsUseCase.kt
│       └── GetWeeklyStatsUseCase.kt
└── presentation/
    └── ui/
        └── screens/
            └── stats/
                ├── StatsScreen.kt
                ├── StatsViewModel.kt
                └── components/
                    ├── WeeklyChart.kt
                    └── TopAppsCard.kt
```

---

### Actualización de Scope

| Feature | Complejidad | Estimación |
|---------|-------------|------------|
| Widget Estado 2x1 | Media | 4h |
| Widget Estado 2x2 | Media | 2h |
| Widget Streak 2x2 | Media | 4h |
| Quick Settings Tile | Media | 4h |
| DB Schema Eventos | Baja | 2h |
| Stats Repository | Media | 3h |
| Stats Screen UI | Media | 4h |
| Weekly Chart | Media | 3h |
| Testing & Polish | - | 4h |
| **Total** | | **~30h (4-5 días)** |

---

## Out of Scope (V1)

- Widget configurable (elegir qué mostrar)
- Múltiples tamaños adicionales
- Widget de shortcuts a perfiles específicos
- Complications para Wear OS
- Exportar estadísticas a CSV
- Estadísticas por perfil individual

---

## Riesgos

| Riesgo | Probabilidad | Impacto | Mitigación |
|--------|--------------|---------|------------|
| Glance bugs en Android 12 | Media | Medio | Testing en múltiples dispositivos |
| Quick Settings no visible | Baja | Bajo | Documentar cómo agregar tile |
| Performance widgets | Baja | Alto | Usar WorkManager para updates |

---

## Referencias

- [Jetpack Glance Documentation](https://developer.android.com/jetpack/compose/glance)
- [TileService Guide](https://developer.android.com/develop/ui/views/quicksettings-tiles)
- [Material Design Widgets](https://m3.material.io/components/widgets)

---

**Creado:** 2026-01-06
**Autor:** Oden Forge
**Estado:** Draft - Pendiente revisión
