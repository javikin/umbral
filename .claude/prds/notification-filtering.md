---
name: notification-filtering
description: Investigar y corregir el sistema de filtrado de notificaciones que no está funcionando
status: backlog
created: 2026-01-16T03:44:50Z
---

# PRD: notification-filtering

## Executive Summary

El sistema de gestión de notificaciones implementado en `notification-management` no está funcionando correctamente. Las notificaciones de apps bloqueadas siguen llegando a la barra de estado del sistema a pesar de que:
1. El permiso de acceso a notificaciones está habilitado
2. El `UmbralNotificationService` está implementado
3. El bloqueo de apps sí funciona correctamente

**Objetivo:** Diagnosticar y corregir el sistema para que las notificaciones de apps bloqueadas se intercepten, almacenen y cancelen durante sesiones de bloqueo activas.

## Problem Statement

### El Problema
El `UmbralNotificationService` (NotificationListenerService) no está interceptando/cancelando las notificaciones de apps bloqueadas. El usuario reporta:
- Las notificaciones de apps bloqueadas **siguen apareciendo** en la barra de estado
- El bloqueo de apps **sí funciona** (las apps se bloquean correctamente)
- El permiso de acceso a notificaciones **está habilitado**

### Impacto
- UX degradada: el usuario sigue viendo distracciones
- Promesa incumplida: la funcionalidad no hace lo que dice hacer
- Métricas incorrectas: no se puede medir "distracciones evitadas"

### Por qué es crítico ahora
- El código está implementado pero no funciona
- El PRD `notification-management` ya definió esta funcionalidad
- Los usuarios esperan que funcione según lo descrito

## Root Cause Analysis (Hipótesis)

### Hipótesis 1: Servicio no conectado
El `NotificationListenerService` podría no estar conectado al sistema correctamente.
- **Verificar:** Logs de `onListenerConnected()` / `onListenerDisconnected()`
- **Test:** Agregar logs más verbosos al iniciar el servicio

### Hipótesis 2: BlockingState no propagado
El flujo `blockingManager.blockingState` podría no estar emitiendo correctamente al servicio.
- **Verificar:** ¿`blockedApps` contiene las apps correctas cuando hay bloqueo activo?
- **Test:** Log del contenido de `blockedApps` en cada notificación

### Hipótesis 3: Timing issue
El servicio podría recibir notificaciones antes de que el estado de bloqueo se actualice.
- **Verificar:** Orden de inicialización
- **Test:** Comparar timestamps de bloqueo vs notificaciones

### Hipótesis 4: Package name mismatch
El `packageName` de la notificación podría no coincidir con los guardados en el perfil.
- **Verificar:** Comparar exactamente los package names
- **Test:** Log de comparación `packageName in blockedApps`

### Hipótesis 5: Hilt injection fallando
`@AndroidEntryPoint` en un `NotificationListenerService` puede tener problemas de inyección.
- **Verificar:** ¿Las dependencias se inyectan correctamente?
- **Test:** Verificar que `blockingManager` no es null

## User Stories

### US-1: Debug del sistema de notificaciones
```
COMO desarrollador
QUIERO herramientas de diagnóstico para el sistema de notificaciones
PARA identificar por qué no se están filtrando
```
**Criterios de aceptación:**
- [ ] Logs detallados de cada notificación recibida
- [ ] Log del estado de bloqueo en cada evento
- [ ] Log de la decisión (filtrar/permitir) y razón
- [ ] Pantalla de debug accesible desde settings (dev mode)

### US-2: Corrección del filtrado
```
COMO usuario con sesión de bloqueo activa
QUIERO que las notificaciones de apps bloqueadas NO aparezcan
PARA poder concentrarme sin distracciones
```
**Criterios de aceptación:**
- [ ] Notificaciones de apps en el perfil activo NO aparecen en barra de estado
- [ ] Notificaciones se guardan en Room para historial
- [ ] `cancelNotification(key)` se ejecuta correctamente
- [ ] Apps no bloqueadas siguen recibiendo notificaciones normalmente

### US-3: Resumen agrupado al final
```
COMO usuario que terminó una sesión de bloqueo
QUIERO ver un resumen agrupado de las notificaciones bloqueadas
PARA saber qué me perdí sin tener que revisar una por una
```
**Criterios de aceptación:**
- [ ] Al terminar sesión, mostrar dialog con resumen
- [ ] Agrupar por app (ej: "Instagram: 5, WhatsApp: 3")
- [ ] Mostrar total de distracciones evitadas
- [ ] Opción de ver detalles o descartar

## Requirements

### Functional Requirements

#### FR-1: Diagnóstico y Debugging
```kotlin
// Agregar logging extensivo en UmbralNotificationService
override fun onNotificationPosted(sbn: StatusBarNotification?) {
    sbn ?: return

    Timber.d("""
        📬 NOTIFICATION RECEIVED:
        - Package: ${sbn.packageName}
        - ID: ${sbn.id}
        - Key: ${sbn.key}
        - Current session: $currentSessionId
        - Blocked apps count: ${blockedApps.size}
        - Blocked apps: $blockedApps
        - Is in blocked list: ${sbn.packageName in blockedApps}
    """.trimIndent())

    // ... resto del código
}
```

#### FR-2: Verificar conexión del servicio
```kotlin
override fun onListenerConnected() {
    super.onListenerConnected()
    Timber.i("🔌 NotificationListener CONNECTED")
    // Broadcast para UI que confirme conexión
}

override fun onListenerDisconnected() {
    super.onListenerDisconnected()
    Timber.w("🔌 NotificationListener DISCONNECTED")
    // Intentar reconexión
}
```

#### FR-3: Verificar inyección de dependencias
- Confirmar que `@AndroidEntryPoint` funciona con `NotificationListenerService`
- Si hay problemas, usar `EntryPointAccessors` manual
- Alternativa: usar singleton sin Hilt para el servicio

#### FR-4: Resumen agrupado mejorado
```kotlin
data class NotificationSummary(
    val totalBlocked: Int,
    val byApp: Map<String, Int>,  // appName -> count
    val topApps: List<Pair<String, Int>>,  // Top 5 apps
    val sessionDuration: Long
)
```

### Non-Functional Requirements

#### NFR-1: Logging
- Todos los logs deben usar Timber
- Nivel DEBUG para desarrollo
- Nivel INFO para producción (eventos importantes)
- Nivel ERROR para fallos

#### NFR-2: Testabilidad
- Unit tests para la lógica de filtrado
- Integration test con mock de NotificationListenerService

## Technical Investigation Steps

### Paso 1: Agregar logging exhaustivo
1. Modificar `UmbralNotificationService` con logs detallados
2. Log en `onListenerConnected` y `onListenerDisconnected`
3. Log del estado completo en cada `onNotificationPosted`

### Paso 2: Probar manualmente
1. Compilar y ejecutar la app
2. Activar un perfil de bloqueo
3. Enviar notificación desde app bloqueada
4. Revisar Logcat filtrando por "UmbralNotification"

### Paso 3: Identificar punto de falla
- ¿El servicio recibe la notificación? (log en `onNotificationPosted`)
- ¿El estado de bloqueo es correcto? (log de `currentSessionId` y `blockedApps`)
- ¿La comparación funciona? (log de `packageName in blockedApps`)
- ¿`cancelNotification` se ejecuta? (log después del cancel)

### Paso 4: Aplicar fix
- Según lo encontrado, aplicar la corrección
- Puede ser: timing, injection, package name format, etc.

### Paso 5: Verificar y documentar
- Confirmar que las notificaciones se filtran
- Documentar la causa raíz encontrada
- Actualizar el código con la solución

## Success Criteria

| Métrica | Target | Cómo medir |
|---------|--------|------------|
| Notificaciones filtradas | 100% de apps bloqueadas | Test manual |
| Resumen mostrado | Al finalizar cada sesión | Test manual |
| Agrupación correcta | Por app y sesión | UI verification |
| Sin regresiones | Apps no bloqueadas funcionan | Test manual |

## Constraints & Assumptions

### Constraints
- El servicio ya está implementado - no reescribir desde cero
- Mantener compatibilidad con el PRD `notification-management`
- No cambiar la arquitectura existente a menos que sea necesario

### Assumptions
- El permiso está correctamente configurado en AndroidManifest
- La declaración del servicio en el manifest es correcta
- Room y el repositorio de notificaciones funcionan correctamente

## Out of Scope

- ❌ Nuevas funcionalidades no relacionadas con el bug
- ❌ Cambios en la UI de historial (ya funciona según PRD anterior)
- ❌ Integración con gamificación (ya implementada)
- ❌ Whitelist de notificaciones críticas (ya implementada)

## Dependencies

### Archivos a investigar
1. `app/src/main/java/com/umbral/notifications/service/UmbralNotificationService.kt`
2. `app/src/main/java/com/umbral/data/blocking/BlockingManagerImpl.kt`
3. `app/src/main/java/com/umbral/di/NotificationModule.kt` (si existe)
4. `app/src/main/AndroidManifest.xml` (declaración del servicio)

### PRDs relacionados
- `notification-management` - PRD original de la funcionalidad

## Risks & Mitigations

| Riesgo | Probabilidad | Impacto | Mitigación |
|--------|--------------|---------|------------|
| Hilt no funciona con NLS | Alta | Alto | Usar EntryPointAccessors manual |
| OEM mata el servicio | Media | Alto | Documentar workarounds por fabricante |
| Timing race condition | Media | Medio | Usar buffer o delay inicial |

## Implementation Checklist

- [ ] Agregar logging extensivo al servicio
- [ ] Verificar conexión del servicio en Logcat
- [ ] Verificar inyección de dependencias
- [ ] Identificar causa raíz del problema
- [ ] Aplicar fix
- [ ] Probar filtrado de notificaciones
- [ ] Probar resumen al finalizar sesión
- [ ] Verificar agrupación por app
- [ ] Documentar solución

---

**Creado:** 2026-01-16
**Tipo:** Bug Fix / Investigation
**Prioridad:** Alta (funcionalidad core no funciona)
**Relacionado:** PRD notification-management
