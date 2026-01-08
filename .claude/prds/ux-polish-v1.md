---
name: ux-polish-v1
description: Correcciones de UX identificadas en testing inicial - Stats card, FAB duplicado, onboarding simplificado
status: backlog
created: 2026-01-06T16:55:41Z
---

# PRD: UX Polish V1

## Executive Summary

Conjunto de 3 correcciones de UX identificadas durante el primer testing en dispositivo real de Umbral. Estos issues afectan la primera impresión del usuario y la claridad de la interfaz. Resolverlos es crítico antes de cualquier release público.

**Valor:** Mejorar la experiencia de primer uso y eliminar confusiones en la UI.

---

## Problem Statement

### ¿Qué problema estamos resolviendo?

Durante el testing inicial en dispositivo, se identificaron 3 problemas de UX que degradan la experiencia del usuario:

1. **Mock Data Engañoso**: El card de estadísticas muestra datos falsos cuando no hay datos reales, confundiendo al usuario sobre el estado real de la app.

2. **UI Redundante**: La pantalla de Tags muestra dos botones para la misma acción (agregar tag), creando confusión visual.

3. **Onboarding Largo**: El proceso de onboarding incluye selección de apps que duplica funcionalidad del flujo de creación de perfil, haciendo el primer uso innecesariamente largo.

### ¿Por qué es importante ahora?

- Son issues de primera impresión - afectan a todos los usuarios nuevos
- Crean confusión y desconfianza (datos falsos)
- Aumentan fricción innecesaria en el primer uso
- Deben resolverse antes de cualquier release o testing con usuarios externos

---

## User Stories

### US-1: Usuario ve estadísticas reales
**Como** usuario nuevo de Umbral
**Quiero** ver el estado real de mis estadísticas (vacío si no hay datos)
**Para** entender honestamente mi progreso y no sentirme engañado por datos falsos

**Acceptance Criteria:**
- [ ] Si no hay datos, el card muestra "Sin datos todavía" con mensaje informativo
- [ ] Si hay datos, muestra métricas reales (tiempo bloqueado, apps, racha)
- [ ] Click en el card navega a pantalla de estadísticas
- [ ] No existe código hardcodeado de datos mock

### US-2: Usuario agrega primer tag sin confusión
**Como** usuario que quiere agregar su primer tag NFC
**Quiero** ver un único botón claro para agregar
**Para** no confundirme con múltiples botones que hacen lo mismo

**Acceptance Criteria:**
- [ ] Empty state muestra solo botón central "Agregar primer tag"
- [ ] FAB no aparece cuando no hay tags
- [ ] Cuando hay 1+ tags, aparece el FAB
- [ ] Transición visual suave entre estados

### US-3: Usuario completa onboarding rápido
**Como** usuario nuevo
**Quiero** completar el onboarding rápidamente
**Para** empezar a usar la app sin configuración redundante

**Acceptance Criteria:**
- [ ] Onboarding no incluye paso de selección de apps
- [ ] Onboarding: Bienvenida → Permisos → Completado
- [ ] Home muestra card prominente "Crea tu primer perfil" si no hay perfiles
- [ ] Card desaparece después de crear primer perfil
- [ ] Única selección de apps ocurre en creación de perfil

---

## Requirements

### Functional Requirements

#### FR-1: Stats Card con Empty State
| ID | Requirement | Priority |
|----|-------------|----------|
| FR-1.1 | Detectar si existen sesiones de bloqueo completadas | Must |
| FR-1.2 | Mostrar empty state cuando no hay datos | Must |
| FR-1.3 | Mostrar datos reales cuando existen | Must |
| FR-1.4 | Navegar a StatsScreen al hacer click | Must |
| FR-1.5 | Eliminar cualquier dato mock/hardcodeado | Must |

**Empty State Design:**
```
┌─────────────────────────────┐
│ 📊 Estadísticas             │
│                             │
│   Sin datos todavía         │
│   Activa un perfil para     │
│   comenzar a trackear       │
│                             │
└─────────────────────────────┘
```

**Con Datos:**
```
┌─────────────────────────────┐
│ 📊 Estadísticas         →   │
│                             │
│ Hoy: 1h 30m bloqueado       │
│ Racha: 3 días               │
│                             │
└─────────────────────────────┘
```

#### FR-2: FAB Condicional en Tags
| ID | Requirement | Priority |
|----|-------------|----------|
| FR-2.1 | Ocultar FAB cuando tags.isEmpty() | Must |
| FR-2.2 | Mostrar FAB cuando tags.isNotEmpty() | Must |
| FR-2.3 | Empty state mantiene su botón central | Must |

**Lógica:**
```kotlin
val showFab = tags.isNotEmpty()

Scaffold(
    floatingActionButton = {
        if (showFab) {
            FloatingActionButton(...) { ... }
        }
    }
) { ... }
```

#### FR-3: Onboarding Simplificado
| ID | Requirement | Priority |
|----|-------------|----------|
| FR-3.1 | Remover paso de selección de apps del onboarding | Must |
| FR-3.2 | Flujo: Welcome → Permissions → Complete | Must |
| FR-3.3 | Crear componente FirstProfilePromptCard | Must |
| FR-3.4 | Mostrar prompt en Home si profiles.isEmpty() | Must |
| FR-3.5 | Ocultar prompt cuando profiles.isNotEmpty() | Must |
| FR-3.6 | Click en prompt navega a crear perfil | Must |

**First Profile Card Design:**
```
┌─────────────────────────────────────┐
│ 🎯 Crea tu primer perfil            │
│                                     │
│ Define qué apps bloquear y cómo     │
│ desbloquearlas con NFC o timer      │
│                                     │
│           [ Crear perfil ]          │
└─────────────────────────────────────┘
```

### Non-Functional Requirements

| ID | Requirement | Target |
|----|-------------|--------|
| NFR-1 | Transiciones entre estados deben ser animadas | 300ms fade |
| NFR-2 | No regresiones en funcionalidad existente | 0 bugs |
| NFR-3 | Código debe seguir patrones existentes | Consistencia |

---

## Success Criteria

| Metric | Target | Measurement |
|--------|--------|-------------|
| Mock data eliminado | 100% | Code review - no hardcoded values |
| UI duplicada eliminada | 0 elementos duplicados | Visual inspection |
| Onboarding steps | ≤3 pasos | Flow count |
| First profile conversion | Card → Profile creation | User testing |

---

## Constraints & Assumptions

### Constraints
- Mantener compatibilidad con código existente
- No cambiar estructura de base de datos
- Cambios solo en capa de presentación (UI)

### Assumptions
- El repositorio de estadísticas ya existe y funciona
- La navegación a StatsScreen ya está implementada
- El flujo de creación de perfil está completo y funcional

---

## Out of Scope

- Rediseño completo de pantallas
- Nuevas funcionalidades de estadísticas
- Cambios en el flujo de creación de perfil
- Animaciones complejas
- Tests automatizados (se harán en fase posterior)

---

## Dependencies

### Internas
- `StatsRepository` - Para obtener datos reales de estadísticas
- `ProfileRepository` - Para verificar si existen perfiles
- `TagRepository` - Para verificar si existen tags
- Navegación existente a StatsScreen y CreateProfileScreen

### Externas
- Ninguna

---

## Implementation Order

1. **Issue 2: FAB Duplicado** (~30 min)
   - Cambio más simple y aislado
   - Permite validar proceso de testing

2. **Issue 1: Stats Card** (~1 hora)
   - Requiere conectar con repositorio real
   - Agregar navegación

3. **Issue 3: Onboarding** (~2 horas)
   - Cambio más extenso
   - Afecta múltiples archivos
   - Crear nuevo componente

---

## Files to Modify

### Issue 1: Stats Card
- `app/src/main/java/com/umbral/presentation/ui/home/HomeScreen.kt`
- `app/src/main/java/com/umbral/presentation/ui/home/HomeViewModel.kt`
- `app/src/main/java/com/umbral/presentation/ui/home/components/StatsCard.kt`

### Issue 2: FAB Duplicado
- `app/src/main/java/com/umbral/presentation/ui/tags/TagsScreen.kt`

### Issue 3: Onboarding
- `app/src/main/java/com/umbral/presentation/ui/onboarding/OnboardingScreen.kt`
- `app/src/main/java/com/umbral/presentation/ui/onboarding/OnboardingViewModel.kt`
- `app/src/main/java/com/umbral/presentation/ui/home/HomeScreen.kt`
- `app/src/main/java/com/umbral/presentation/ui/home/HomeViewModel.kt`
- `app/src/main/java/com/umbral/presentation/ui/home/components/FirstProfilePromptCard.kt` (nuevo)

---

## Testing Checklist

### Fresh Install Flow
- [ ] Instalar app limpia
- [ ] Completar onboarding (solo permisos, sin selección apps)
- [ ] Ver Home con card "Crear primer perfil"
- [ ] Stats card muestra empty state
- [ ] Tags screen muestra empty state sin FAB

### After First Profile
- [ ] Crear perfil desde card
- [ ] Card de primer perfil desaparece
- [ ] Stats card sigue en empty state (sin sesiones aún)

### After First Session
- [ ] Activar perfil de bloqueo
- [ ] Completar sesión
- [ ] Stats card muestra datos reales
- [ ] Click navega a StatsScreen

### Tags Flow
- [ ] Agregar primer tag
- [ ] FAB aparece
- [ ] Agregar segundo tag funciona desde FAB

---

**Creado:** 2026-01-06T16:55:41Z
**Autor:** Product Manager
