---
name: notification-filtering
status: backlog
created: 2026-01-16T04:00:25Z
updated: 2026-01-16T04:12:31Z
progress: 0%
prd: .claude/prds/notification-filtering.md
github: https://github.com/javikin/umbral/issues/72
---

# Epic: notification-filtering

## Overview

Investigación y corrección del sistema de filtrado de notificaciones que no está funcionando. El `UmbralNotificationService` (NotificationListenerService) está implementado pero las notificaciones de apps bloqueadas siguen apareciendo. Este epic se enfoca en diagnosticar la causa raíz y aplicar el fix necesario con mínimas modificaciones.

## Architecture Decisions

### AD-1: Mantener arquitectura existente
- **Decisión:** No reescribir el servicio, solo corregir el bug
- **Razón:** El código está bien estructurado, solo necesita debugging

### AD-2: Logging primero, código después
- **Decisión:** Agregar logging extensivo antes de hacer cambios
- **Razón:** Necesitamos entender exactamente dónde falla antes de arreglar

### AD-3: Hilt EntryPointAccessors como fallback
- **Decisión:** Si `@AndroidEntryPoint` no funciona con NLS, usar inyección manual
- **Razón:** NotificationListenerService tiene ciclo de vida especial que puede no ser compatible con Hilt

## Technical Approach

### Investigación (Fase 0)
1. Revisar implementaciones de NotificationListenerService en GitHub
2. Comparar con proyectos como Foqos, Brick, Digital Wellbeing apps
3. Identificar patrones comunes que podrían estar faltando
4. Documentar diferencias encontradas

### Diagnóstico (Fase 1)
1. Agregar logging exhaustivo en `UmbralNotificationService`
2. Verificar en Logcat si el servicio está conectado
3. Verificar si `blockedApps` contiene las apps correctas
4. Identificar el punto exacto de falla

### Corrección (Fase 2)
Según la hipótesis confirmada:
- **Si Hilt falla:** Usar `EntryPointAccessors` para inyección manual
- **Si timing issue:** Agregar buffer inicial o verificación lazy
- **Si package mismatch:** Normalizar comparación de package names

### Verificación (Fase 3)
- Probar que notificaciones de apps bloqueadas NO aparecen
- Probar que notificaciones de apps NO bloqueadas SÍ aparecen
- Verificar resumen agrupado al final de sesión

## Implementation Strategy

### Enfoque minimalista
Este es un **bug fix**, no una nueva feature. El objetivo es:
1. Encontrar por qué no funciona
2. Arreglar con el cambio mínimo necesario
3. Verificar que funciona
4. Documentar la causa raíz

### Testing
- Test manual con dispositivo real
- Logcat para verificar flujo
- No se requieren unit tests nuevos (el código existente ya tiene tests)

## Task Breakdown Preview

- [ ] **Issue #1:** Investigar implementaciones de NotificationListenerService en proyectos open source similares (Foqos, Brick, etc.) para identificar patrones faltantes
- [ ] **Issue #2:** Agregar logging diagnóstico al servicio de notificaciones
- [ ] **Issue #3:** Verificar y corregir inyección de dependencias (Hilt)
- [ ] **Issue #4:** Corregir filtrado de notificaciones según causa raíz identificada
- [ ] **Issue #5:** Verificar y mejorar resumen agrupado al finalizar sesión

**Total: 5 issues** (manteniéndolo simple y enfocado)

## Dependencies

### Archivos clave
- `app/src/main/java/com/umbral/notifications/service/UmbralNotificationService.kt`
- `app/src/main/java/com/umbral/data/blocking/BlockingManagerImpl.kt`
- `app/src/main/AndroidManifest.xml`

### Conocimiento previo
- Entender cómo funciona `NotificationListenerService` en Android
- Conocer limitaciones de Hilt con servicios del sistema

## Success Criteria (Technical)

| Criterio | Métrica |
|----------|---------|
| Notificaciones filtradas | 100% de apps en perfil activo |
| Sin falsos positivos | 0% de apps no bloqueadas afectadas |
| Resumen funcional | Dialog aparece al terminar sesión |
| Logs útiles | Suficiente info para debug futuro |

## Estimated Effort

- **Diagnóstico:** 1-2 horas (agregar logs + probar)
- **Fix:** 1-3 horas (según complejidad de la causa)
- **Verificación:** 1 hora (testing manual)
- **Total estimado:** 3-6 horas de trabajo

## Risks

| Riesgo | Mitigación |
|--------|------------|
| Causa raíz compleja | Probar hipótesis una por una |
| OEM mata servicio | Documentar, fuera de scope para este epic |
| Hilt incompatible con NLS | Usar EntryPointAccessors manual |

## Tasks Created

- [ ] #73 - Investigar implementaciones de NotificationListenerService en GitHub (parallel: true)
- [ ] #74 - Agregar logging diagnóstico al servicio de notificaciones (parallel: false)
- [ ] #75 - Verificar y corregir inyección de dependencias (Hilt) (parallel: false)
- [ ] #76 - Corregir filtrado de notificaciones según causa raíz (parallel: false)
- [ ] #77 - Verificar y mejorar resumen agrupado al finalizar sesión (parallel: false)

**Total tasks:** 5
**Parallel tasks:** 1 (solo el primero puede iniciar independientemente)
**Sequential tasks:** 4
**Estimated total effort:** 4-10 horas
