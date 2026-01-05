---
name: testing-qa
prd: testing-qa
description: Completar testing y QA para Umbral V1 antes del lanzamiento
status: open
priority: high
github: https://github.com/javikin/umbral/issues/7
created: 2026-01-04T03:41:28Z
updated: 2026-01-04T06:30:00Z
progress: 83%
---

# Epic: Testing & QA

## Objetivo

Completar la fase de testing y QA para garantizar la calidad de Umbral V1 antes del lanzamiento en Play Store.

## Contexto Técnico

### Estado Actual
- **Unit Tests:** 170 tests pasando
- **Archivos de test:** 8
- **Cobertura:** No medida
- **CI/CD:** No configurado

### Stack de Testing
- **Unit Tests:** JUnit5 + MockK + Turbine
- **Integration Tests:** Robolectric + Room in-memory
- **UI Tests:** Compose Testing + Espresso
- **Coverage:** JaCoCo
- **CI/CD:** GitHub Actions

## Análisis Técnico

### Módulos a Testear (por prioridad)

| Módulo | Archivos | Cobertura Actual | Target |
|--------|----------|------------------|--------|
| domain/blocking | 5 | ~60% | 90% |
| domain/nfc | 3 | ~50% | 85% |
| data/blocking | 4 | ~40% | 80% |
| data/nfc | 3 | ~30% | 80% |
| presentation/viewmodel | 8 | ~70% | 85% |
| presentation/ui | 15 | 0% | 60% |

### Áreas Críticas

1. **BlockingManager** - Core del bloqueo de apps
2. **NfcTagManager** - Lectura/escritura de tags
3. **ProfileRepository** - CRUD de perfiles
4. **UsageStatsManager** - Detección de apps

### Tests de Integración Necesarios

1. **Database Flow:** Profile → Room → Query → UI
2. **Blocking Flow:** NFC Scan → Profile Load → Block Apps
3. **Stats Flow:** App Usage → Aggregation → Charts

### Tests de UI Necesarios

1. **ProfilesScreen** - Lista, crear, editar, eliminar
2. **NfcScanScreen** - Estados de escaneo
3. **SelectAppsScreen** - Selección de apps
4. **OnboardingScreen** - Flujo completo

## Tasks Identificados

### Fase 1: Configuración (Paralelo)
1. **Configurar JaCoCo** - Medir cobertura actual
2. **Configurar GitHub Actions** - CI para tests

### Fase 2: Unit Tests (Paralelo)
3. **Tests BlockingManager** - Cobertura completa
4. **Tests NfcTagManager** - Cobertura completa
5. **Tests Repositories** - Room + DataStore

### Fase 3: Integration Tests (Secuencial)
6. **Tests de Database** - Room in-memory
7. **Tests de Flujos** - End-to-end con Robolectric

### Fase 4: UI Tests (Paralelo)
8. **Tests ProfilesScreen** - CRUD de perfiles
9. **Tests OnboardingScreen** - Flujo onboarding
10. **Tests Compose Components** - Componentes reutilizables

### Fase 5: QA Manual (Secuencial)
11. **Testing NFC Real** - Con tags físicos
12. **Testing Dispositivos** - Matriz de compatibilidad

## Dependencias entre Tasks

```
[1: JaCoCo] ──┬──> [3: BlockingManager]
              │
[2: CI/CD] ───┼──> [4: NfcTagManager]
              │
              └──> [5: Repositories]
                        │
                        v
                   [6: Database Tests]
                        │
                        v
                   [7: Flow Tests]
                        │
              ┌─────────┼─────────┐
              v         v         v
         [8: Profiles] [9: Onboarding] [10: Components]
                        │
                        v
                   [11: NFC Real]
                        │
                        v
                   [12: Dispositivos]
```

## Criterio de Éxito

- [ ] Cobertura >80% en módulos core
- [ ] Cobertura >70% general
- [ ] GitHub Actions pipeline verde
- [ ] 0 crashes en testing manual
- [ ] NFC probado con 3 tipos de tags
- [ ] Probado en min 3 dispositivos

## Riesgos

| Riesgo | Probabilidad | Impacto | Mitigación |
|--------|--------------|---------|------------|
| Robolectric lento | Media | Bajo | Separar test suites |
| NFC no disponible | Baja | Alto | Usar mocks + testing real posterior |
| Compose tests flaky | Media | Medio | Retry policy en CI |

## Estimación

- **Total:** ~40 horas
- **Duración:** 1 semana
- **Paralelismo:** Tasks 1-2, 3-5, 8-10 pueden ser paralelos

## Entregables

1. `jacoco.gradle` configurado
2. `.github/workflows/test.yml`
3. Tests de integración en `androidTest/`
4. Tests de UI en `androidTest/`
5. `TESTING_REPORT.md` con resultados

## Tasks Created

- [x] #8 - configure-jacoco (parallel: true) ✅
- [x] #9 - configure-github-actions (parallel: true) ✅
- [x] #10 - tests-blocking-manager (parallel: true, depends: 8) ✅
- [x] #11 - tests-nfc-manager (parallel: true, depends: 8) ✅
- [x] #12 - tests-repositories (parallel: true, depends: 8) ✅
- [x] #13 - tests-database-integration (parallel: false, depends: 12) ✅
- [x] #14 - tests-flow-integration (parallel: false, depends: 13) ✅
- [x] #15 - tests-ui-profiles-screen (parallel: true, depends: 14) ✅
- [x] #16 - tests-ui-onboarding-screen (parallel: true, depends: 14) ✅
- [x] #17 - tests-ui-components (parallel: true, depends: 14) ✅
- [ ] #18 - testing-nfc-real (parallel: false, depends: 15,16,17)
- [ ] #19 - testing-devices-matrix (parallel: false, depends: 18)

Total tasks: 12
Parallel tasks: 7
Sequential tasks: 5

Synced: 2026-01-04T03:45:00Z
