# NFC Testing Checklist - Umbral V1

**Estado:** 🟡 Pendiente
**Última actualización:** 2026-01-04

---

## Requisitos de Hardware

### Tags NFC Necesarios
- [ ] **NTAG213** (144 bytes) - El más común y económico
- [ ] **NTAG215** (504 bytes) - Compatible con amiibo
- [ ] **NTAG216** (888 bytes) - Máxima capacidad

### Dispositivos Android
- [ ] Dispositivo principal con NFC habilitado
- [ ] Dispositivo secundario (opcional, para cross-device testing)

---

## Casos de Test - Lectura de Tags

### Leer Tag Vacío
| # | Caso | Esperado | Resultado | Notas |
|---|------|----------|-----------|-------|
| 1.1 | Acercar tag NTAG213 vacío | Detecta tag, muestra "Tag no reconocido" | ⬜ | |
| 1.2 | Acercar tag NTAG215 vacío | Detecta tag, muestra "Tag no reconocido" | ⬜ | |
| 1.3 | Acercar tag NTAG216 vacío | Detecta tag, muestra "Tag no reconocido" | ⬜ | |

### Leer Tag con Datos de Umbral
| # | Caso | Esperado | Resultado | Notas |
|---|------|----------|-----------|-------|
| 2.1 | Tag con profile ID válido | Activa perfil, muestra confirmación | ⬜ | |
| 2.2 | Tag con profile ID inexistente | Muestra error "Perfil no encontrado" | ⬜ | |
| 2.3 | Tag Umbral corrupto | Maneja error gracefully | ⬜ | |

### Leer Tag Externo (no Umbral)
| # | Caso | Esperado | Resultado | Notas |
|---|------|----------|-----------|-------|
| 3.1 | Tag con URL | Detecta pero ignora, ofrece sobrescribir | ⬜ | |
| 3.2 | Tag con texto plano | Detecta pero ignora, ofrece sobrescribir | ⬜ | |
| 3.3 | Tag protegido contra escritura | Detecta, no permite sobrescribir | ⬜ | |

---

## Casos de Test - Escritura de Tags

### Escribir Perfil en Tag
| # | Caso | Esperado | Resultado | Notas |
|---|------|----------|-----------|-------|
| 4.1 | Escribir en NTAG213 | Escritura exitosa, feedback visual | ⬜ | |
| 4.2 | Escribir en NTAG215 | Escritura exitosa, feedback visual | ⬜ | |
| 4.3 | Escribir en NTAG216 | Escritura exitosa, feedback visual | ⬜ | |
| 4.4 | Sobrescribir tag existente | Sobrescribe, confirma cambio | ⬜ | |

### Errores de Escritura
| # | Caso | Esperado | Resultado | Notas |
|---|------|----------|-----------|-------|
| 5.1 | Alejar tag durante escritura | Error "Tag perdido", permite reintentar | ⬜ | |
| 5.2 | Tag solo lectura | Error "Tag protegido" | ⬜ | |
| 5.3 | Tag con capacidad insuficiente | Error descriptivo | ⬜ | |

---

## Casos de Test - Flujo Completo

### Activar Bloqueo via NFC
| # | Caso | Esperado | Resultado | Notas |
|---|------|----------|-----------|-------|
| 6.1 | Escanear tag → Activar perfil | Apps bloqueadas inmediatamente | ⬜ | |
| 6.2 | Verificar overlay en app bloqueada | Overlay de Umbral aparece | ⬜ | |
| 6.3 | Whitelist funciona | Apps en whitelist no bloqueadas | ⬜ | |

### Desactivar Bloqueo via NFC
| # | Caso | Esperado | Resultado | Notas |
|---|------|----------|-----------|-------|
| 7.1 | Escanear mismo tag para desactivar | Perfil desactivado, apps liberadas | ⬜ | |
| 7.2 | Verificar apps accesibles | Todas las apps funcionan normal | ⬜ | |

---

## Casos de Test - Edge Cases

### NFC Deshabilitado
| # | Caso | Esperado | Resultado | Notas |
|---|------|----------|-----------|-------|
| 8.1 | Intentar escribir sin NFC | Mensaje "Habilita NFC" + botón settings | ⬜ | |
| 8.2 | Intentar leer sin NFC | Mensaje "Habilita NFC" + botón settings | ⬜ | |

### Estados Inconsistentes
| # | Caso | Esperado | Resultado | Notas |
|---|------|----------|-----------|-------|
| 9.1 | Perfil eliminado pero tag existe | Error "Perfil no encontrado" | ⬜ | |
| 9.2 | Doble scan rápido | Ignora segundo scan, no crash | ⬜ | |
| 9.3 | Scan durante escritura previa | Espera o error controlado | ⬜ | |

---

## Notas del Tester

### Ambiente de Test
- **Dispositivo:**
- **Android Version:**
- **Umbral Version:**
- **Fecha:**

### Problemas Encontrados
1.
2.
3.

### Sugerencias de UX
1.
2.
3.

---

## Resultados Finales

| Categoría | Pasados | Fallidos | Bloqueados |
|-----------|---------|----------|------------|
| Lectura Tags | /7 | | |
| Escritura Tags | /7 | | |
| Flujo Completo | /4 | | |
| Edge Cases | /5 | | |
| **TOTAL** | **/23** | | |

### Conclusión
- [ ] ✅ NFC listo para producción
- [ ] ⚠️ Requiere fixes antes de lanzar
- [ ] ❌ Bloquea lanzamiento

---

**Tester:** _________________
**Fecha Completado:** _________________
