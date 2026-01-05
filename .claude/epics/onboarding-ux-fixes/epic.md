---
name: onboarding-ux-fixes
prd: onboarding-ux-fixes
description: Corregir bugs críticos de UX en onboarding y selector de apps
status: completed
priority: critical
github: https://github.com/javikin/umbral/issues/20
created: 2026-01-04T23:34:58Z
updated: 2026-01-05T05:30:00Z
progress: 100%
---

# Epic: Onboarding & UX Fixes

## Objetivo

Corregir los 4 bugs críticos encontrados durante QA que bloquean el uso básico de Umbral.

## Bugs a Resolver

| # | Bug | Archivo Principal |
|---|-----|-------------------|
| 1 | Lista de apps vacía | `AndroidManifest.xml`, `InstalledAppsRepository` |
| 2 | Navegación rota (puede volver atrás) | `NavGraph.kt`, `OnboardingNavigation` |
| 3 | Falta paso "cómo desbloquear" | `OnboardingScreen.kt` |
| 4 | Tags/categorías confusos | `strings.xml`, `AppCategoryChip` |

## Análisis Técnico

### Bug 1: Apps No Aparecen

**Diagnóstico:**
En Android 11+ se requiere declarar `QUERY_ALL_PACKAGES` o usar `<queries>` en manifest.

**Archivos a modificar:**
- `app/src/main/AndroidManifest.xml`
- `app/src/main/java/com/umbral/data/apps/InstalledAppsRepositoryImpl.kt`

**Solución:**
```xml
<queries>
    <intent>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent>
</queries>
```

### Bug 2: Navegación Permite Retroceso

**Diagnóstico:**
`NavController` no limpia el backstack al navegar entre secciones.

**Archivos a modificar:**
- `app/src/main/java/com/umbral/presentation/navigation/NavGraph.kt`
- `app/src/main/java/com/umbral/presentation/ui/screens/onboarding/`

**Solución:**
```kotlin
navController.navigate(Screen.AppSelector.route) {
    popUpTo(Screen.Onboarding.route) { inclusive = true }
}
```

### Bug 3: Falta Paso de Desbloqueo

**Diagnóstico:**
El onboarding no explica cómo el usuario puede desactivar el bloqueo.

**Archivos a modificar:**
- `app/src/main/java/com/umbral/presentation/ui/screens/onboarding/OnboardingScreen.kt`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/drawable/` (nuevo icono si necesario)

**Solución:**
Agregar `HowToUnblockStep` entre permisos y éxito.

### Bug 4: Tags Confusos

**Diagnóstico:**
Los chips de categoría no tienen texto claro en español.

**Archivos a modificar:**
- `app/src/main/res/values/strings.xml`
- `app/src/main/java/com/umbral/presentation/ui/screens/apps/AppSelectorScreen.kt`

---

## Tasks

### Task 1: Fix Lista de Apps Vacía
**Prioridad:** 🔴 Crítica
**Tipo:** Bug fix
**Estimación:** 1-2 horas

- Agregar `<queries>` al AndroidManifest
- Verificar que `getInstalledApplications()` funcione en API 36
- Agregar logs para debugging
- Testear en dispositivo real

### Task 2: Fix Navegación Onboarding
**Prioridad:** 🔴 Crítica
**Tipo:** Bug fix
**Estimación:** 1-2 horas

- Revisar `NavGraph.kt`
- Agregar `popUpTo` con `inclusive = true`
- Deshabilitar gesto de swipe back en onboarding
- Manejar botón físico atrás correctamente

### Task 3: Agregar Paso "Cómo Desbloquear"
**Prioridad:** 🟡 Alta
**Tipo:** Feature
**Estimación:** 2-3 horas

- Crear nueva pantalla `HowToUnblockScreen`
- Agregar strings en español
- Diseñar iconografía (NFC tap + QR scan)
- Integrar en flujo de onboarding

### Task 4: Mejorar UX Categorías
**Prioridad:** 🟡 Alta
**Tipo:** UX improvement
**Estimación:** 1-2 horas

- Actualizar strings de categorías a español claro
- Agregar iconos a cada categoría
- Verificar que filtros funcionen

---

## Dependencias

```
[Task 1: Fix Apps] ──────┐
                         ├──> [QA Final]
[Task 2: Fix Nav] ───────┤
                         │
[Task 3: Unlock Step] ───┤
                         │
[Task 4: Categorías] ────┘
```

Todas las tasks son paralelas excepto QA final.

---

## Criterio de Done

- [ ] Lista de apps muestra apps instaladas
- [ ] No se puede volver al onboarding después de permisos
- [ ] Onboarding explica cómo desbloquear
- [ ] Categorías tienen nombres claros en español
- [ ] Probado en Pixel 8 Pro (Android 16)
- [ ] 0 crashes

---

## Estimación

- **Total:** 6-9 horas
- **Paralelismo:** Tasks 1-4 pueden ser paralelas

---

## Tasks Created

- [x] #23 - fix-apps-list-empty (parallel: true) 🔴 Crítico ✅
- [x] #24 - fix-navigation-backstack (parallel: true) 🔴 Crítico ✅
- [x] #21 - add-unlock-step-onboarding (parallel: true) ✅
- [x] #22 - improve-category-ux (parallel: true) ✅

Total tasks: 4
Completed tasks: 4
Parallel tasks: 4
Sequential tasks: 0

Completed: 2026-01-05T05:30:00Z
