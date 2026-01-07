---
name: notification-management
status: backlog
created: 2026-01-07T20:18:37Z
progress: 0%
prd: .claude/prds/notification-management.md
github: https://github.com/javikin/umbral/issues/62
---

# Epic: notification-management

## Overview

Implementar un sistema de gestión de notificaciones que intercepte, almacene y resuma las notificaciones de apps bloqueadas durante sesiones de enfoque activas. El sistema se integra con el módulo de gamificación existente (Expedición) para otorgar recompensas por distracciones evitadas.

## Architecture Decisions

### 1. NotificationListenerService
- **Decisión:** Usar `NotificationListenerService` de Android para interceptar notificaciones
- **Razón:** API oficial de Android, no requiere root, funciona con todas las apps
- **Trade-off:** Requiere que el usuario habilite manualmente el permiso en Settings

### 2. Almacenamiento Local con Room
- **Decisión:** Almacenar notificaciones bloqueadas en Room Database
- **Razón:** Consistente con arquitectura existente, permite queries complejas, ya integrado
- **Tabla:** `blocked_notifications` con relación a `blocking_sessions`

### 3. Integración con BlockingManager
- **Decisión:** Consultar BlockingManager.isBlocking para determinar si filtrar notificaciones
- **Razón:** Reutiliza lógica existente, single source of truth para estado de bloqueo

### 4. Whitelist de Notificaciones Críticas
- **Decisión:** Siempre permitir llamadas, SMS, alarmas, batería baja
- **Razón:** Seguridad del usuario, no bloquear comunicación de emergencia
- **Configurable:** Apps bancarias y autenticadores 2FA opcionales

## Technical Approach

### New Module Structure
```
app/src/main/java/com/umbral/
├── notifications/
│   ├── data/
│   │   ├── local/
│   │   │   ├── BlockedNotificationDao.kt
│   │   │   └── BlockedNotificationEntity.kt
│   │   └── repository/
│   │       └── NotificationRepository.kt
│   ├── domain/
│   │   ├── model/
│   │   │   └── BlockedNotification.kt
│   │   └── usecase/
│   │       ├── GetBlockedNotificationsUseCase.kt
│   │       ├── GetNotificationSummaryUseCase.kt
│   │       └── ClearOldNotificationsUseCase.kt
│   ├── service/
│   │   └── UmbralNotificationService.kt
│   └── presentation/
│       ├── history/
│       │   ├── NotificationHistoryScreen.kt
│       │   └── NotificationHistoryViewModel.kt
│       └── summary/
│           └── SessionSummaryDialog.kt
```

### Database Schema
```kotlin
@Entity(tableName = "blocked_notifications")
data class BlockedNotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: String,
    val packageName: String,
    val appName: String,
    val title: String?,
    val text: String?,
    val timestamp: Long,
    val iconUri: String?,
    val isRead: Boolean = false
)
```

### Service Flow
```
┌─────────────────────────────────────────────────────────────┐
│              UmbralNotificationService                       │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  onNotificationPosted(sbn)                                  │
│         │                                                    │
│         ▼                                                    │
│  ┌─────────────────┐                                        │
│  │ isBlocking()?   │──No──▶ Allow notification              │
│  └────────┬────────┘                                        │
│           │ Yes                                              │
│           ▼                                                  │
│  ┌─────────────────┐                                        │
│  │ isWhitelisted?  │──Yes─▶ Allow notification              │
│  └────────┬────────┘                                        │
│           │ No                                               │
│           ▼                                                  │
│  ┌─────────────────┐                                        │
│  │ isBlockedApp?   │──No──▶ Allow notification              │
│  └────────┬────────┘                                        │
│           │ Yes                                              │
│           ▼                                                  │
│  Store in DB + cancelNotification(key)                      │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

### Integration Points

1. **BlockingManager** - Query active blocking state and profile
2. **BlockingProfile** - Get list of blocked apps for current session
3. **ExpeditionRepository** - Award bonus energy for blocked notifications
4. **AchievementSystem** - New achievements for notification milestones

### New Achievements
| ID | Name | Requirement | Energy Reward |
|----|------|-------------|---------------|
| `shield_mind` | Escudo Mental | Block 100 notifications | 50 |
| `fortress` | Fortaleza | Block 500 notifications | 100 |
| `immune` | Inmune | Block 1000 notifications | 200 |

### Energy Bonus Formula
```kotlin
val bonusEnergy = blockedCount / 5  // +1 energy per 5 notifications
```

## Implementation Strategy

### Phase 1: Core Service (Tasks 001-002)
- Create NotificationListenerService
- Implement notification interception logic
- Add whitelist for critical notifications

### Phase 2: Storage & Repository (Tasks 003-004)
- Add Room entity and DAO
- Create repository with cleanup logic
- Add database migration

### Phase 3: UI Components (Tasks 005-006)
- Session summary dialog
- Notification history screen
- Permission onboarding flow

### Phase 4: Integration (Tasks 007-008)
- Connect with BlockingManager
- Integrate with gamification system
- Add new achievements

## Tasks Created

- [ ] #63 - NotificationListenerService básico + permisos (parallel: true)
- [ ] #64 - Whitelist de notificaciones críticas (parallel: false)
- [ ] #65 - Room entity, DAO y migration (parallel: true)
- [ ] #66 - Repository y UseCases (parallel: false)
- [ ] #67 - SessionSummaryDialog post-sesión (parallel: true)
- [ ] #68 - NotificationHistoryScreen (parallel: true)
- [ ] #69 - Integración BlockingManager (parallel: false)
- [ ] #70 - Integración gamificación + achievements (parallel: false)

Total tasks: 8
Parallel tasks: 4
Sequential tasks: 4
Estimated total effort: 30 hours

## Dependencies

### Internal
- `BlockingManager` - Estado de bloqueo activo
- `BlockingProfile` - Lista de apps bloqueadas
- `ExpeditionRepository` - Otorgar energía bonus
- `AchievementSystem` - Nuevos logros
- `UmbralDatabase` - Agregar nueva tabla

### External
- Android NotificationListenerService API
- Room Database (ya integrado)
- Hilt DI (ya integrado)

## Success Criteria (Technical)

| Metric | Target |
|--------|--------|
| Service latency | < 100ms per notification |
| Battery impact | < 2% additional |
| Database limit | Max 1000 records (FIFO) |
| Auto-cleanup | 7 days retention |

## Estimated Effort

| Phase | Tasks | Effort |
|-------|-------|--------|
| Core Service | 001-002 | 8h |
| Storage | 003-004 | 6h |
| UI | 005-006 | 8h |
| Integration | 007-008 | 6h |
| **Total** | **8 tasks** | **~28h** |

## Risks & Mitigations

| Risk | Mitigation |
|------|------------|
| User doesn't grant permission | Clear onboarding explaining benefits |
| OEM kills service | Detect and guide user to battery settings |
| Privacy concerns | Option to count-only without storing content |

---

**Creado:** 2026-01-07T20:18:37Z
**Autor:** Technical Architect Agent
