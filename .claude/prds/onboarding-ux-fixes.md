---
name: onboarding-ux-fixes
description: Corregir bugs de UX encontrados en QA del onboarding y selector de apps
status: draft
priority: critical
created: 2026-01-04T23:34:58Z
updated: 2026-01-04T23:34:58Z
---

# PRD: Onboarding & UX Fixes

## Problema

Durante el QA manual en Pixel 8 Pro (Android 16), se encontraron múltiples problemas de UX que bloquean el uso básico de la app:

### Bugs Críticos Encontrados

| # | Bug | Severidad | Impacto |
|---|-----|-----------|---------|
| 1 | **Lista de apps vacía** en selector | 🔴 Crítico | Usuario no puede seleccionar apps para bloquear |
| 2 | **Navegación rota** entre onboarding/permisos/selector | 🔴 Crítico | Flujo confuso, puede volver al onboarding |
| 3 | **Falta paso en onboarding** explicando cómo desbloquear | 🟡 Alto | Usuario no sabe cómo reactivar apps |
| 4 | **Tags de categorías** no se entienden | 🟡 Alto | UX confusa en selector de apps |

---

## Contexto

### Flujo Actual (Roto)
```
Onboarding → Permisos → [puede volver atrás] → Selector Apps (vacío)
```

### Flujo Esperado
```
Onboarding (con paso de desbloqueo) → Permisos → Selector Apps (con lista) → Home
                                          ↑
                                    (sin retroceso)
```

---

## User Stories

### US1: Como usuario nuevo, quiero ver todas mis apps instaladas para poder seleccionar cuáles bloquear.

**Criterios de Aceptación:**
- [ ] La lista de apps muestra todas las apps instaladas del usuario
- [ ] Las apps del sistema están separadas/ocultas por defecto
- [ ] Hay indicador de carga mientras se obtienen las apps
- [ ] Si hay error, se muestra mensaje claro con opción de reintentar

### US2: Como usuario nuevo, quiero entender cómo desbloquear mis apps antes de activar el bloqueo.

**Criterios de Aceptación:**
- [ ] Onboarding incluye paso que explica NFC/QR para desbloquear
- [ ] Iconografía clara de "tap NFC" o "escanear QR"
- [ ] Opción de ver tutorial después si lo omite

### US3: Como usuario, quiero un flujo lineal sin poder retroceder a pasos completados.

**Criterios de Aceptación:**
- [ ] Una vez otorgados permisos, no puede volver al onboarding
- [ ] Navegación usa `popUpTo` para limpiar backstack
- [ ] Botón físico "atrás" sale de la app, no retrocede el flujo

### US4: Como usuario, quiero entender las categorías de apps para filtrar fácilmente.

**Criterios de Aceptación:**
- [ ] Tags usan nombres claros en español: "Redes Sociales", "Juegos", "Productividad"
- [ ] Iconos acompañan cada categoría
- [ ] Filtro funciona correctamente

---

## Análisis Técnico

### Bug 1: Lista de Apps Vacía

**Causa Probable:**
- Permiso `QUERY_ALL_PACKAGES` no declarado en manifest (Android 11+)
- O `PackageManager.getInstalledApplications()` no funciona en API 36

**Solución:**
```xml
<!-- AndroidManifest.xml -->
<uses-permission android:name="android.permission.QUERY_ALL_PACKAGES" />
```

O usar `<queries>` con intents específicos:
```xml
<queries>
    <intent>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent>
</queries>
```

### Bug 2: Navegación Rota

**Causa Probable:**
- `NavController` no limpia backstack después de permisos
- Falta `popUpTo` en navegación

**Solución:**
```kotlin
navController.navigate("app_selector") {
    popUpTo("onboarding") { inclusive = true }
}
```

### Bug 3: Paso Faltante en Onboarding

**Solución:**
- Agregar nueva pantalla `HowToUnblockScreen` entre permisos y éxito
- Contenido: "Para desbloquear, escanea tu tag NFC o código QR"

### Bug 4: Tags Confusos

**Solución:**
- Revisar strings en `strings.xml`
- Usar nombres descriptivos en español
- Agregar iconos de Material Design para cada categoría

---

## Alcance

### En Scope ✅
- Fix lista de apps vacía
- Fix navegación entre pantallas
- Agregar paso de "cómo desbloquear" en onboarding
- Mejorar UX de categorías/tags

### Fuera de Scope ❌
- Rediseño completo del onboarding
- Nuevas features de categorización
- Cambios en lógica de bloqueo

---

## Criterio de Éxito

| Métrica | Target |
|---------|--------|
| Apps se muestran en selector | 100% dispositivos |
| Flujo onboarding sin retroceso | Verificado |
| Usuario entiende cómo desbloquear | Paso visible |
| Categorías claras en español | UX review passed |

---

## Riesgos

| Riesgo | Mitigación |
|--------|------------|
| `QUERY_ALL_PACKAGES` rechazado en Play Store | Usar `<queries>` como alternativa |
| Onboarding muy largo | Mantener conciso, máx 4 pasos |

---

## Prioridad

**CRÍTICO** - Bloquea el uso básico de la app. Debe resolverse antes de cualquier release.

---

**Creado:** 2026-01-04
**Autor:** QA Team
