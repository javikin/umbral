---
name: validate-accessibility
description: Validar que todos los ratios de contraste cumplen WCAG 2.1 AA (4.5:1 minimo)
status: open
priority: qa
estimate: 30min
created: 2026-01-16T20:02:16Z
updated: 2026-01-16T20:11:00Z
github_issue: 87
---

# Issue: Validate Accessibility Contrast Ratios

## Description

Validar que todas las combinaciones de color texto/fondo en la nueva paleta cumplen con los requisitos de accesibilidad WCAG 2.1 nivel AA, que requiere un ratio de contraste minimo de 4.5:1 para texto normal y 3:1 para texto grande.

## Technical Details

### Combinaciones a Validar - Dark Theme

| Combinacion | Colores | Ratio Esperado | Status |
|-------------|---------|----------------|--------|
| Background + Text Primary | #151515 + #E8E8E8 | 12.8:1 | AAA |
| Background + Text Secondary | #151515 + #A0A0A0 | 7.4:1 | AA |
| Background + Text Tertiary | #151515 + #6B6B6B | 4.0:1 | Check* |
| Background + Accent | #151515 + #4ECDC4 | 8.9:1 | AAA |
| Surface + Text Primary | #1E1E1E + #E8E8E8 | 11.5:1 | AAA |
| Surface + Text Secondary | #1E1E1E + #A0A0A0 | 6.2:1 | AA |
| Elevated + Text Primary | #282828 + #E8E8E8 | 9.4:1 | AAA |

### Combinaciones a Validar - Light Theme

| Combinacion | Colores | Ratio Esperado | Status |
|-------------|---------|----------------|--------|
| Background + Text Primary | #F8F8F8 + #1A1A1A | 14.1:1 | AAA |
| Background + Text Secondary | #F8F8F8 + #5C5C5C | 6.8:1 | AA |
| Background + Accent | #F8F8F8 + #3DB5AD | 3.2:1 | Large text only* |
| Surface + Text Primary | #FFFFFF + #1A1A1A | 16.0:1 | AAA |
| Surface + Text Secondary | #FFFFFF + #5C5C5C | 7.5:1 | AA |

### Herramientas de Validacion

1. **WebAIM Contrast Checker:** https://webaim.org/resources/contrastchecker/
2. **Contrast Ratio:** https://contrast-ratio.com/
3. **Android Accessibility Scanner:** Play Store

### Criterios WCAG 2.1

- **AA (Normal text):** 4.5:1 minimo
- **AA (Large text):** 3:1 minimo (18pt+ o 14pt+ bold)
- **AAA (Normal text):** 7:1 minimo
- **AAA (Large text):** 4.5:1 minimo

## Acceptance Criteria

- [ ] Todas las combinaciones Dark Theme verificadas
- [ ] Todas las combinaciones Light Theme verificadas
- [ ] Ratios documentados en tabla de resultados
- [ ] Text tertiary cumple AA para texto grande
- [ ] Accent en Light theme solo usado para elementos grandes/interactivos
- [ ] Sin combinaciones que fallen AA para su uso previsto
- [ ] Reporte de accesibilidad completado y guardado

## Dependencies

- Issue #01: Update Color.kt with new palette (colores a validar)
- Issue #02: Update Theme.kt ColorScheme mappings (para ver uso real)

## Notes

- El texto terciario (#6B6B6B en dark) esta al limite de AA
  - Usar solo para placeholders y hints
  - Nunca para contenido importante
- El accent en light theme (#3DB5AD) tiene bajo contraste con fondo claro
  - Usar solo para botones grandes, iconos, elementos interactivos
  - Nunca para texto pequeno sobre fondo claro

## Testing Procedure

1. **Automatizado:**
   - Instalar Android Accessibility Scanner
   - Escanear todas las pantallas de la app
   - Documentar warnings y errores

2. **Manual:**
   - Usar WebAIM Contrast Checker para cada combinacion
   - Registrar ratios reales
   - Comparar con ratios esperados

3. **Visual:**
   - Revisar la app en condiciones de poca luz
   - Revisar con brillo bajo del dispositivo
   - Verificar legibilidad en todas las pantallas

## Deliverables

- [ ] Tabla de ratios de contraste actualizada con valores reales
- [ ] Screenshot de Accessibility Scanner (sin errores criticos)
- [ ] Lista de recomendaciones si hay issues encontrados
