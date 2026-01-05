---
name: testing-qa
description: Completar testing y QA para Umbral V1 antes del lanzamiento
status: backlog
created: 2026-01-04T03:40:38Z
updated: 2026-01-04T03:40:38Z
parent: umbral-v1
---

# Testing & QA - Umbral V1

## Resumen

Completar la fase de testing y aseguramiento de calidad para Umbral V1, incluyendo tests unitarios adicionales, tests de integración, tests de UI, y QA manual en dispositivos reales.

## Problema

Actualmente el proyecto tiene:
- ✅ 170 unit tests pasando
- ❌ Cobertura de código no medida
- ❌ Sin tests de integración
- ❌ Sin tests de UI (Compose)
- ❌ Sin testing en dispositivos reales
- ❌ Sin testing de NFC real

Para un lanzamiento exitoso en Play Store, necesitamos garantizar calidad y estabilidad.

## User Stories

### Como Desarrollador
- Quiero tener >80% de cobertura de código para detectar regresiones
- Quiero tests de integración para validar flujos completos
- Quiero CI/CD que ejecute tests automáticamente

### Como QA
- Quiero una matriz de testing para dispositivos Android
- Quiero casos de prueba documentados para testing manual
- Quiero validar NFC con tags físicos reales

### Como Usuario Final
- Quiero una app que no crashee
- Quiero que el bloqueo de apps funcione consistentemente
- Quiero que NFC responda rápido y sin errores

## Requisitos Funcionales

### 1. Cobertura de Tests Unitarios
- [ ] Medir cobertura actual con JaCoCo
- [ ] Alcanzar >80% cobertura en módulos core
- [ ] Alcanzar >70% cobertura general

### 2. Tests de Integración
- [ ] Tests de Room Database (in-memory)
- [ ] Tests de DataStore preferences
- [ ] Tests de flujos Repository → UseCase → ViewModel

### 3. Tests de UI (Compose)
- [ ] Tests de navegación entre screens
- [ ] Tests de componentes críticos
- [ ] Tests de estados de UI (loading, error, success)

### 4. QA Manual
- [ ] Matriz de dispositivos (min 3 dispositivos)
- [ ] Testing de NFC con tags reales
- [ ] Testing de Accessibility Service
- [ ] Testing de widgets
- [ ] Testing de permisos

### 5. CI/CD
- [ ] GitHub Actions para tests en PR
- [ ] Build automático de APK
- [ ] Reporte de cobertura

## Requisitos No Funcionales

### Performance
- App debe iniciar en <2 segundos
- NFC scan debe responder en <500ms
- UI debe mantener 60fps

### Estabilidad
- 0 crashes en sesión de 1 hora
- Manejo graceful de errores de permisos
- Recovery de estados inconsistentes

### Compatibilidad
- Android 8.0+ (API 26+)
- Diferentes resoluciones de pantalla
- Modo oscuro/claro

## Criterios de Éxito

1. **Cobertura:** >80% en módulos core, >70% general
2. **CI/CD:** Pipeline verde en main
3. **Crashes:** 0 crashes en testing manual
4. **Dispositivos:** Probado en min 3 dispositivos diferentes
5. **NFC:** Funciona con NTAG213, NTAG215, NTAG216

## Dependencias

- `fix-compilation` epic completado ✅
- Acceso a dispositivos Android físicos
- Tags NFC para testing (NTAG213/215/216)

## Estimación

- **Duración:** 1 semana (Semana 13)
- **Esfuerzo:** ~40 horas

## Entregables

1. Suite de tests de integración
2. Suite de tests de UI
3. Reporte de cobertura JaCoCo
4. Documento de testing manual completado
5. GitHub Actions workflow
6. APK testeado y estable

## Referencias

- [Android Testing Guide](https://developer.android.com/training/testing)
- [Compose Testing](https://developer.android.com/jetpack/compose/testing)
- [JaCoCo](https://www.jacoco.org/jacoco/)
