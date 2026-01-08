---
name: ux-polish-v1
status: completed
created: 2026-01-06T16:59:10Z
updated: 2026-01-06T18:07:04Z
progress: 100%
prd: .claude/prds/ux-polish-v1.md
github: https://github.com/javikin/umbral/issues/47
---

# Epic: UX Polish V1

## Overview

Correcciones de UX identificadas en testing real de dispositivo. Este epic aborda 3 issues que afectan la primera impresión del usuario:

1. **Stats Card Mock Data** - Eliminar datos hardcodeados, mostrar estado real
2. **FAB Duplicado en Tags** - Mostrar FAB solo cuando hay tags existentes
3. **Onboarding Simplificado** - Remover selección de apps, agregar prompt en Home

**Esfuerzo total estimado:** ~3.5 horas

---

## Architecture Decisions

### AD-1: Datos reales vs Mock Data
- **Decisión:** Conectar `StatsPreviewCard` al `HomeViewModel` para obtener datos reales
- **Rationale:** Los datos mock engañan al usuario y degradan la confianza
- **Impacto:** Modificar `HomeViewModel` para exponer estadísticas semanales

### AD-2: FAB Condicional
- **Decisión:** Mostrar FAB basado en `uiState.tags.isNotEmpty()`
- **Rationale:** Evitar redundancia UI (empty state ya tiene botón)
- **Impacto:** Cambio mínimo en `TagsScreen.kt`

### AD-3: Flujo de Onboarding
- **Decisión:** Remover paso `select_apps`, agregar `FirstProfilePromptCard` en Home
- **Rationale:** Reducir fricción inicial, único punto de selección de apps
- **Impacto:** Modificar navegación de onboarding, crear nuevo componente

---

## Technical Approach

### Issue 1: Stats Card - Datos Reales

**Archivo principal:** `app/src/main/java/com/umbral/presentation/ui/screens/home/HomeScreen.kt`

**Problema identificado (línea 214):**
```kotlin
// MOCK DATA - ELIMINAR
weeklyData = listOf(0.3f, 0.5f, 0.4f, 0.8f, 0.6f, 0.9f, 0.7f),
```

**Solución:**
1. Agregar `weeklyStats: List<Float>` a `HomeUiState`
2. En `HomeViewModel`, obtener datos reales del `StatsRepository`
3. Modificar `StatsPreviewCard` para manejar empty state
4. Pasar datos desde `uiState` en lugar de hardcodear

**Empty State Design:**
```kotlin
@Composable
private fun StatsPreviewCard(
    weeklyData: List<Float>,
    hasData: Boolean,  // Nuevo parámetro
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    UmbralCard(...) {
        if (hasData) {
            // Mostrar gráfica con datos reales
            StatsGraph(data = weeklyData, ...)
        } else {
            // Empty state
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Sin datos todavía")
                Text("Activa un perfil para comenzar")
            }
        }
    }
}
```

### Issue 2: FAB Condicional en Tags

**Archivo:** `app/src/main/java/com/umbral/presentation/ui/screens/nfc/TagsScreen.kt`

**Problema (líneas 79-86):**
```kotlin
floatingActionButton = {
    FloatingActionButton(...) { ... }  // Siempre visible
}
```

**Solución:**
```kotlin
floatingActionButton = {
    // Solo mostrar FAB si hay tags
    if (uiState.tags.isNotEmpty()) {
        FloatingActionButton(
            onClick = onNavigateToScan,
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Agregar tag")
        }
    }
}
```

### Issue 3: Onboarding Simplificado

**Archivos a modificar:**

1. **`OnboardingNavHost.kt`** - Remover ruta `select_apps`
   - Cambiar navegación de `permissions` → `success` (saltar `select_apps`)
   - Ajustar lógica de `completeOnboarding`

2. **`SuccessScreen.kt`** - Ajustar mensaje
   - No mostrar `appsCount` (ya no aplica)
   - Mensaje genérico de bienvenida

3. **`HomeScreen.kt`** - Agregar `FirstProfilePromptCard`
   - Nuevo parámetro: `hasProfiles: Boolean`
   - Nuevo parámetro: `onCreateProfile: () -> Unit`
   - Mostrar card prominente si `!hasProfiles`

4. **`HomeViewModel.kt`** - Detectar si hay perfiles
   - Agregar `hasProfiles: Boolean` a `HomeUiState`
   - Consultar `ProfileRepository`

**Nuevo componente - FirstProfilePromptCard:**
```kotlin
@Composable
fun FirstProfilePromptCard(
    onCreateProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    UmbralCard(
        modifier = modifier.fillMaxWidth(),
        elevation = UmbralElevation.Medium
    ) {
        Column(
            modifier = Modifier.padding(UmbralSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.AddCircleOutline,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(UmbralSpacing.md))

            Text(
                text = "Crea tu primer perfil",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(UmbralSpacing.sm))

            Text(
                text = "Define qué apps bloquear y cómo desbloquearlas con NFC o timer",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(UmbralSpacing.lg))

            Button(
                onClick = onCreateProfile,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crear perfil")
            }
        }
    }
}
```

---

## Task Breakdown

| # | Task | Files | Effort |
|---|------|-------|--------|
| 1 | Fix FAB duplicado en TagsScreen | `TagsScreen.kt` | 15 min |
| 2 | Conectar StatsPreviewCard a datos reales | `HomeScreen.kt`, `HomeViewModel.kt` | 45 min |
| 3 | Agregar empty state a StatsPreviewCard | `HomeScreen.kt` | 30 min |
| 4 | Simplificar flujo de onboarding | `OnboardingNavHost.kt`, `SuccessScreen.kt` | 30 min |
| 5 | Crear FirstProfilePromptCard | `HomeScreen.kt` (nuevo componente) | 30 min |
| 6 | Integrar FirstProfilePromptCard en Home | `HomeScreen.kt`, `HomeViewModel.kt` | 30 min |
| 7 | Testing en dispositivo real | - | 30 min |

**Total: ~3.5 horas**

---

## Dependencies

### Internas
- `StatsRepository` - Debe tener método para obtener stats semanales
- `ProfileRepository` - Debe poder verificar si existen perfiles
- Navegación existente a crear perfil desde Home

### Externas
- Ninguna

---

## Success Criteria (Technical)

| Criteria | Measurement |
|----------|-------------|
| No mock data en código | Grep por valores hardcodeados = 0 |
| FAB condicional funciona | Visual: empty state sin FAB |
| Onboarding ≤3 pasos | Contar pantallas en flujo |
| FirstProfilePromptCard visible | Fresh install muestra card |
| Card desaparece post-perfil | Crear perfil → card no visible |
| Stats muestra empty state | Sin datos → mensaje informativo |
| Stats muestra datos reales | Con datos → gráfica real |

---

## Files Summary

### Modificar
1. `app/src/main/java/com/umbral/presentation/ui/screens/nfc/TagsScreen.kt`
2. `app/src/main/java/com/umbral/presentation/ui/screens/home/HomeScreen.kt`
3. `app/src/main/java/com/umbral/presentation/viewmodel/HomeViewModel.kt`
4. `app/src/main/java/com/umbral/presentation/ui/screens/onboarding/OnboardingNavHost.kt`
5. `app/src/main/java/com/umbral/presentation/ui/screens/onboarding/SuccessScreen.kt`

### Verificar (posibles ajustes)
- `app/src/main/java/com/umbral/presentation/viewmodel/OnboardingViewModel.kt`
- `app/src/main/java/com/umbral/presentation/viewmodel/HomeUiState.kt`

---

## Risk Mitigation

| Risk | Mitigation |
|------|------------|
| StatsRepository no tiene método semanal | Verificar API antes de implementar |
| Navegación rota post-cambios | Testing exhaustivo del flujo |
| Regresiones en onboarding | Mantener tests existentes |

---

## Tasks Created

- [x] #48 - Fix FAB duplicado en TagsScreen (parallel: true, ~0.25h) ✅
- [x] #49 - Stats Card - Datos reales y empty state (parallel: true, ~1.25h) ✅
- [x] #50 - Simplificar flujo de onboarding (parallel: true, ~0.75h) ✅
- [x] #51 - FirstProfilePromptCard en HomeScreen (parallel: false, depends: #50, ~1h) ✅

**Summary:**
- Total tasks: 4
- Parallel tasks: 3
- Sequential tasks: 1
- Estimated total effort: 3.25 hours

**GitHub Links:**
- Epic: https://github.com/javikin/umbral/issues/47

---

**Creado:** 2026-01-06T16:59:10Z
**Autor:** Technical Lead
