---
name: umbral-v1
description: App Android open-source para bloqueo automático de apps mediante NFC tags
status: in-progress
created: 2026-01-04T01:47:21Z
updated: 2026-01-04T01:47:21Z
---

# Umbral V1 - App de Bloqueo NFC

## Resumen

App Android open-source que permite bloquear aplicaciones automáticamente mediante tags NFC físicos. El usuario coloca su teléfono sobre un tag NFC para activar/desactivar perfiles de bloqueo.

## Stack Tecnológico

- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose + Material Design 3
- **Arquitectura:** Clean Architecture + MVVM
- **DI:** Hilt
- **Database:** Room
- **Persistencia:** DataStore
- **NFC:** Android NFC APIs (NTAG213/215/216)
- **QR:** ZXing + ML Kit

## Módulos

| Módulo | Spec | Estado |
|--------|------|--------|
| Foundation | - | ✅ Completo |
| NFC | nfc-module.md | ✅ Completo |
| Blocking | blocking-module.md | ✅ Completo |
| Profiles | profiles-module.md | ✅ Completo |
| QR | qr-module.md | ✅ Completo |
| Stats | stats-module.md | ✅ Completo |
| Onboarding | onboarding-module.md | ✅ Completo |
| Widgets | ui-module.md | ✅ Completo |
| Testing | - | 🔄 En progreso |
| Launch | - | ⏳ Pendiente |

## Progreso Actual

- **Semana actual:** 11-12 (Feature Complete)
- **Archivos Kotlin:** 106
- **Tests implementados:** 8 archivos (~150 casos)
- **Estado de compilación:** ❌ Errores pendientes

### Errores de Compilación Conocidos

1. `domain.apps.InstalledApp` - modelo no existe
2. `domain.qr.Profile` - debería ser `domain.blocking.BlockingProfile`
3. `QrScanViewModel` - métodos inexistentes
4. Screens de UI - tipos incorrectos

## Documentación Existente

- `docs/reference/technical-decisions.md` - Arquitectura completa
- `docs/reference/competitive-analysis.md` - Análisis de Foqos, Brick, Unpluq
- `docs/reference/implementation-plan.md` - Plan de 14 semanas
- `docs/reference/modules/*.md` - Specs de cada módulo
- `docs/reference/user-personas.md` - Personas definidas
- `docs/reference/user-stories.md` - User stories

## Épicos

1. **fix-compilation** - Corregir errores de compilación
2. **testing-qa** - Completar testing y QA (Semana 13)
3. **launch-prep** - Preparación para Play Store (Semana 14)

## Enlaces

- [Implementation Plan](../../docs/reference/implementation-plan.md)
- [Technical Decisions](../../docs/reference/technical-decisions.md)
