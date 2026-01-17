---
name: fix-stats-chart-kt
description: Reemplazar 6 valores dp hardcodeados en StatsChart.kt con UmbralSpacing tokens
status: open
priority: 2
estimate: 30min
created: 2026-01-16T20:02:16Z
updated: 2026-01-16T20:11:00Z
github_issue: 84
---

# Issue: Fix StatsChart.kt Hardcoded Values

## Description

El archivo `StatsChart.kt` contiene 6 valores de dimension hardcodeados que deben ser reemplazados con tokens de `UmbralSpacing` para mantener consistencia con el design system y facilitar cambios futuros.

## Technical Details

**File:** `app/src/main/java/com/umbral/presentation/ui/components/StatsChart.kt` (o ubicacion similar)

### Valores a Reemplazar

Buscar y reemplazar valores como:
- `4.dp` -> `UmbralSpacing.xs`
- `8.dp` -> `UmbralSpacing.sm`
- `16.dp` -> `UmbralSpacing.md`
- `24.dp` -> `UmbralSpacing.lg`
- `32.dp` -> `UmbralSpacing.xl`
- `48.dp` -> `UmbralSpacing.xxl`

### Ejemplo de Cambio

```kotlin
// ANTES
Modifier.padding(horizontal = 16.dp, vertical = 8.dp)

// DESPUES
Modifier.padding(
    horizontal = UmbralSpacing.md,
    vertical = UmbralSpacing.sm
)
```

### Tokens de UmbralSpacing Disponibles

```kotlin
object UmbralSpacing {
    val xs  = 4.dp   // Extra small
    val sm  = 8.dp   // Small
    val md  = 16.dp  // Medium (default)
    val lg  = 24.dp  // Large
    val xl  = 32.dp  // Extra large
    val xxl = 48.dp  // 2XL

    // Component-specific
    val cardPadding  = 16.dp
    val buttonHeight = 56.dp
    val chipHeight   = 32.dp
    val iconSizeSmall  = 18.dp
    val iconSizeMedium = 24.dp
    val iconSizeLarge  = 32.dp
}
```

## Acceptance Criteria

- [ ] Identificar los 6 valores hardcodeados en StatsChart.kt
- [ ] Reemplazar cada valor con el token UmbralSpacing correspondiente
- [ ] Importar UmbralSpacing correctamente en el archivo
- [ ] Verificar que el layout no cambia visualmente
- [ ] Build exitoso sin errores
- [ ] No hay mas valores dp hardcodeados en el archivo

## Dependencies

- Ninguna - UmbralSpacing ya existe en el proyecto

## Notes

- Si un valor no tiene equivalente exacto en UmbralSpacing, usar el mas cercano y documentar
- Considerar si algun valor deberia agregarse a UmbralSpacing en lugar de usar uno existente
- Mantener la apariencia visual identica despues del cambio

## Testing

1. Compilar proyecto sin errores
2. Navegar a pantalla con StatsChart
3. Comparar visualmente antes y despues del cambio
4. Verificar que graficos mantienen proporcion correcta
5. Probar en diferentes tamanos de pantalla
