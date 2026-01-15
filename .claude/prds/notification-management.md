---
name: notification-management
description: Interceptar y resumir notificaciones de apps bloqueadas durante sesiones de enfoque
status: backlog
created: 2026-01-07T20:15:49Z
---

# PRD: notification-management

## Executive Summary

Implementar un sistema de gestión de notificaciones que intercepte, almacene y resuma las notificaciones de apps bloqueadas durante sesiones de enfoque activas. Al finalizar la sesión, el usuario verá un resumen de las distracciones evitadas, integrándose con el sistema de gamificación existente.

**Propuesta de valor:** Transformar las notificaciones bloqueadas de una molestia invisible en un logro visible ("Evitaste 23 distracciones hoy").

## Problem Statement

### El Problema
Cuando un usuario activa el bloqueo de apps en Umbral, las notificaciones de esas apps siguen llegando normalmente. Esto causa:
1. **Distracción visual**: Las notificaciones aparecen en la barra de estado
2. **Distracción auditiva**: Sonidos y vibraciones interrumpen
3. **Tentación**: Ver "5 mensajes de Instagram" genera FOMO y ansiedad
4. **Métricas perdidas**: No hay forma de saber cuántas distracciones se evitaron

### Por qué es importante ahora
- El sistema de gamificación (Expedición) ya está implementado
- Los usuarios necesitan feedback positivo sobre su progreso
- Competidores como Opal y One Sec ofrecen esta funcionalidad
- Es un diferenciador clave para el valor percibido de la app

## User Stories

### Persona: María (Estudiante universitaria, 22 años)
> "Cuando estudio, las notificaciones de Instagram y TikTok me distraen aunque no abra las apps. Quiero silenciarlas completamente pero sin perderme nada importante."

### User Stories

**US-1: Silenciar notificaciones durante bloqueo**
```
COMO usuario con una sesión de bloqueo activa
QUIERO que las notificaciones de apps bloqueadas no aparezcan
PARA poder concentrarme sin distracciones visuales ni auditivas
```
**Criterios de aceptación:**
- [ ] Las notificaciones de apps en el perfil activo no se muestran en la barra de estado
- [ ] No hay sonido ni vibración de apps bloqueadas
- [ ] Las notificaciones de apps NO bloqueadas funcionan normalmente
- [ ] Llamadas y SMS siempre pasan (whitelist del sistema)

**US-2: Ver resumen post-sesión**
```
COMO usuario que terminó una sesión de bloqueo
QUIERO ver cuántas notificaciones fueron bloqueadas
PARA sentir satisfacción por las distracciones evitadas
```
**Criterios de aceptación:**
- [ ] Al terminar sesión, mostrar diálogo con resumen
- [ ] Mostrar cantidad por app (ej: "Instagram: 8, Twitter: 3")
- [ ] Opción de ver detalles o descartar
- [ ] Integrar con sistema de logros

**US-3: Revisar notificaciones bloqueadas**
```
COMO usuario curioso sobre lo que me perdí
QUIERO poder ver las notificaciones que fueron bloqueadas
PARA decidir si alguna era importante
```
**Criterios de aceptación:**
- [ ] Lista de notificaciones almacenadas en Umbral
- [ ] Mostrar: app, título, texto, hora
- [ ] Opción de abrir la app original
- [ ] Auto-limpiar después de 24 horas

**US-4: Ganar recompensas por distracciones evitadas**
```
COMO usuario del sistema de Expedición
QUIERO ganar energía extra por notificaciones ignoradas
PARA sentirme recompensado por mi disciplina
```
**Criterios de aceptación:**
- [ ] +1 energía por cada 5 notificaciones bloqueadas
- [ ] Logro especial: "Escudo Mental" (bloquear 100 notificaciones)
- [ ] Stats en pantalla de estadísticas

## Requirements

### Functional Requirements

#### FR-1: NotificationListenerService
- Implementar servicio que intercepte notificaciones del sistema
- Filtrar solo notificaciones de apps en el perfil de bloqueo activo
- Almacenar en Room database: packageName, title, text, timestamp, iconUri
- Cancelar la notificación del sistema (ocultarla)

#### FR-2: Whitelist de Notificaciones Críticas
- Siempre permitir: Llamadas entrantes, SMS, apps de emergencia
- Siempre permitir: Alarmas del sistema, batería baja
- Configurable: Apps bancarias, autenticadores 2FA

#### FR-3: Almacenamiento de Notificaciones
```kotlin
@Entity(tableName = "blocked_notifications")
data class BlockedNotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: String,           // Relacionar con sesión de bloqueo
    val packageName: String,
    val appName: String,
    val title: String?,
    val text: String?,
    val timestamp: Long,
    val iconUri: String?,
    val isRead: Boolean = false
)
```

#### FR-4: UI de Resumen Post-Sesión
- Diálogo al terminar sesión mostrando:
  - Total de notificaciones bloqueadas
  - Breakdown por app (top 5)
  - Energía bonus ganada
- Botones: "Ver todas" / "Descartar"

#### FR-5: Pantalla de Historial
- Nueva sección en la app: "Notificaciones Pausadas"
- Lista agrupada por sesión
- Filtros: Por app, por fecha
- Acciones: Marcar como leída, abrir app, eliminar

#### FR-6: Integración con Gamificación
- Nuevo logro: "Escudo Mental" - Bloquear 100 notificaciones
- Nuevo logro: "Fortaleza" - Bloquear 500 notificaciones
- Nuevo logro: "Inmune" - Bloquear 1000 notificaciones
- Bonus de energía: +1 por cada 5 notificaciones bloqueadas en una sesión

### Non-Functional Requirements

#### NFR-1: Performance
- El servicio de notificaciones no debe consumir más de 2% de batería adicional
- Latencia de interceptación < 100ms
- Base de datos de notificaciones limitada a 1000 registros (FIFO)

#### NFR-2: Privacidad
- Las notificaciones se almacenan solo localmente
- Auto-eliminación después de 7 días
- Opción de desactivar almacenamiento (solo contar)
- No se capturan notificaciones de apps de mensajería E2E por defecto

#### NFR-3: Permisos
- Requiere: `BIND_NOTIFICATION_LISTENER_SERVICE`
- El usuario debe habilitar manualmente en Configuración > Acceso a notificaciones
- Mostrar guía clara de por qué se necesita este permiso

#### NFR-4: Reliability
- El servicio debe sobrevivir a reinicios del sistema
- Recuperación graceful si el servicio es terminado por el sistema
- Sincronizar estado con BlockingManager existente

## Technical Approach

### Arquitectura

```
┌─────────────────────────────────────────────────────────────┐
│                    NotificationService                       │
│  (NotificationListenerService)                               │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  onNotificationPosted() ──► shouldBlock() ──► storeAndHide()│
│                                    │                         │
│                                    ▼                         │
│                          BlockingManager                     │
│                          (isBlocking? activeProfile?)        │
│                                    │                         │
│                                    ▼                         │
│                    BlockedNotificationRepository             │
│                                    │                         │
│                                    ▼                         │
│                         Room Database                        │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

### Flujo Principal

1. **Notificación llega al sistema**
2. `UmbralNotificationService.onNotificationPosted()` se dispara
3. Verificar si bloqueo está activo via `BlockingManager.isBlocking`
4. Verificar si la app está en el perfil activo
5. Si debe bloquear:
   - Guardar en `BlockedNotificationEntity`
   - Llamar `cancelNotification(key)`
6. Si no debe bloquear:
   - No hacer nada, notificación pasa normal

### Integración con Módulos Existentes

- **BlockingManager**: Consultar estado de bloqueo activo
- **BlockingProfile**: Obtener lista de apps bloqueadas
- **ExpeditionRepository**: Otorgar energía bonus
- **AchievementSystem**: Desbloquear logros relacionados

## Success Criteria

| Métrica | Target | Cómo medir |
|---------|--------|------------|
| Adopción del permiso | >70% de usuarios lo activan | Analytics |
| Notificaciones bloqueadas/sesión | >5 promedio | Stats internas |
| Retención D7 | +10% vs sin feature | Cohorte A/B |
| Satisfacción | >4.2 estrellas en reviews mencionando feature | Play Store |

## Constraints & Assumptions

### Constraints
- **Android only**: NotificationListenerService no existe en iOS
- **Permiso manual**: El usuario debe ir a Settings > Apps > Special access
- **Battery optimization**: Algunos OEMs (Xiaomi, Huawei) pueden matar el servicio
- **Android 13+**: Cambios en cómo se manejan notificaciones

### Assumptions
- Los usuarios están dispuestos a dar el permiso de notificaciones
- El overhead de batería será aceptable
- Las notificaciones almacenadas no contendrán información ultra-sensible

## Out of Scope (V1)

- ❌ Responder a notificaciones desde Umbral
- ❌ Sincronización cloud de notificaciones bloqueadas
- ❌ Notificaciones de apps de trabajo (perfil de trabajo Android)
- ❌ Filtrado inteligente por contenido de notificación
- ❌ Modo "VIP" que deja pasar notificaciones de contactos específicos
- ❌ Widget mostrando notificaciones pausadas

## Dependencies

### Internas
- BlockingManager (existente) - Para saber estado de bloqueo
- BlockingProfile (existente) - Para lista de apps bloqueadas
- ExpeditionRepository (existente) - Para otorgar energía
- AchievementSystem (existente) - Para nuevos logros

### Externas
- Android NotificationListenerService API
- Room Database (ya integrado)
- Hilt DI (ya integrado)

## Risks & Mitigations

| Riesgo | Probabilidad | Impacto | Mitigación |
|--------|--------------|---------|------------|
| Usuario no da permiso | Alta | Alto | UX clara explicando beneficio, recordatorios |
| OEM mata el servicio | Media | Alto | Guías por fabricante, detección de kill |
| Batería excesiva | Baja | Alto | Profiling, optimización, toggle para desactivar |
| Privacidad concerns | Media | Medio | Opción de solo contar sin almacenar contenido |

## Timeline Estimate

| Fase | Duración | Entregables |
|------|----------|-------------|
| Setup + Service | 3 días | NotificationListenerService básico |
| Storage + UI | 3 días | Room entities, pantalla de historial |
| Integration | 2 días | Conexión con BlockingManager, gamificación |
| Polish + Testing | 2 días | Edge cases, battery testing, OEM testing |
| **Total** | **10 días** | Feature completo |

---

**Creado:** 2026-01-07
**Autor:** Product Manager
**Stakeholders:** Desarrollo Android, UX, QA
