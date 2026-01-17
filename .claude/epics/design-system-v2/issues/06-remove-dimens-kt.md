---
name: remove-dimens-kt
description: Eliminar archivo Dimens.kt deprecado ya que UmbralSpacing lo reemplaza
status: open
priority: 3
estimate: 15min
created: 2026-01-16T20:02:16Z
updated: 2026-01-16T20:11:00Z
github_issue: 86
---

# Issue: Remove Deprecated Dimens.kt

## Description

El archivo `Dimens.kt` esta deprecado y ha sido reemplazado por `UmbralSpacing`. Este issue cubre la eliminacion segura del archivo y la migracion de cualquier uso restante.

## Technical Details

**File to Remove:** `app/src/main/java/com/umbral/presentation/ui/theme/Dimens.kt` (o ubicacion similar)

### Pre-requisitos

Antes de eliminar, verificar que no hay usos restantes:

```bash
# Buscar usos de Dimens en el proyecto
grep -r "Dimens\." --include="*.kt" app/src/
```

### Proceso de Eliminacion

1. Buscar referencias a `Dimens` en todo el proyecto
2. Reemplazar cada uso con el equivalente de `UmbralSpacing`:
   - `Dimens.paddingSmall` -> `UmbralSpacing.sm`
   - `Dimens.paddingMedium` -> `UmbralSpacing.md`
   - `Dimens.paddingLarge` -> `UmbralSpacing.lg`
   - etc.
3. Actualizar imports en archivos afectados
4. Eliminar el archivo `Dimens.kt`
5. Verificar que el proyecto compila

### Mapeo de Valores (Referencia)

| Dimens (viejo) | UmbralSpacing (nuevo) |
|----------------|----------------------|
| paddingXSmall  | xs (4.dp)           |
| paddingSmall   | sm (8.dp)           |
| paddingMedium  | md (16.dp)          |
| paddingLarge   | lg (24.dp)          |
| paddingXLarge  | xl (32.dp)          |

## Acceptance Criteria

- [ ] Buscar todas las referencias a Dimens en el proyecto
- [ ] Reemplazar cada uso con UmbralSpacing equivalente
- [ ] Eliminar archivo Dimens.kt
- [ ] Actualizar imports en archivos que usaban Dimens
- [ ] Build exitoso sin errores
- [ ] Grep no encuentra referencias a "Dimens\." en codigo

## Dependencies

- Issue #04: Fix StatsChart.kt hardcoded values (puede revelar mas usos de Dimens)

## Notes

- Verificar dos veces que no hay usos antes de eliminar
- Si hay valores en Dimens que no existen en UmbralSpacing, agregar a UmbralSpacing primero
- Mantener registro de archivos modificados por si hay issues

## Testing

1. Ejecutar grep para verificar no hay usos de Dimens
2. Compilar proyecto sin errores
3. Ejecutar tests existentes
4. Verificar que la app funciona normalmente
5. Revisar visualmente que layouts no cambiaron
