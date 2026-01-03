# Instrucciones de Proyecto - Umbral

## Información del Proyecto

**Nombre:** Umbral
**Descripción:** App Android open-source para bloqueo automático de apps mediante NFC tags
**Stack:** Kotlin + Jetpack Compose + Room + Hilt
**Arquitectura:** Clean Architecture + MVVM
**Metodología:** Oden (Documentation-First Development)

---

## Naming Conventions CRÍTICAS

### Database & TypeScript (si se agrega backend futuro)
- **Nombres de tablas:** INGLÉS (ej: `blocking_profiles`, `nfc_tags`)
- **Nombres de columnas:** INGLÉS snake_case (ej: `created_at`, `is_whitelisted`)
- **NUNCA:** Nombres en español en DB

### Código Kotlin
- **Classes:** PascalCase en INGLÉS (ej: `BlockingProfile`, `NfcTagManager`)
- **Functions/Variables:** camelCase en INGLÉS (ej: `startBlocking`, `profileId`)
- **Packages:** lowercase en INGLÉS (ej: `com.umbral.nfc`, `com.umbral.blocking`)

### UI - Textos para Usuario
- **Todos los strings visibles:** ESPAÑOL
- **Usar strings.xml:** Siempre, nunca hardcodear texto
- **Ejemplos:**
  - ✅ `<string name="btn_save">Guardar</string>`
  - ✅ `<string name="profile_name">Nombre del perfil</string>`
  - ❌ Hardcoded: `Text("Save")` o `Text("Guardar")`

---

## Filosofía del Producto

> "Umbral" representa el concepto filosófico griego del **metaxy (μεταξύ)** - el espacio liminal entre dos estados. El momento consciente de transición al cruzar el umbral de tu casa.

**Principios:**
1. **Privacidad primero:** 100% local, sin cloud sync en V1
2. **Open source:** Todo el código disponible en GitHub
3. **UX simple:** Funcionar debe ser obvio, no requiere manual
4. **Respeto al usuario:** No dark patterns, fácil desinstalar si no funciona

---

## Comandos Oden Disponibles

### Pre-Desarrollo (ESTADO ACTUAL)
- `/oden:architect` - **SIGUIENTE PASO** - Completar arquitectura y schema detallado
- `/oden:analyze` - Análisis competitivo profundo
- `/oden:spec [modulo]` - Especificaciones detalladas por módulo
- `/oden:plan` - Plan de implementación semana por semana
- `/oden:checklist` - Verificar que todo esté listo antes de codificar

### Durante Desarrollo
- `/oden:daily` - Registrar progreso diario
- `/oden:test` - Testing strategy
- `/oden:review` - Code review
- `/oden:debug` - Debugging

### Gestión
- `/oden:status` - Ver estado del proyecto
- `/oden:help` - Ver todos los comandos

---

## Decisiones Técnicas Clave

### 1. Android Only (por ahora)
- iOS ya está cubierto por Foqos (open source)
- Colaboración con Foqos, no competencia
- Mejor soporte NFC en Android que iOS

### 2. 100% Local-First
- Sin backend en V1
- Room Database para persistencia
- DataStore para preferences
- Funciona completamente offline

### 3. App Blocking Strategy
**Nivel 1 (Preferido):** UsageStatsManager
- Menos fricción en Google Play
- API oficial de Google

**Nivel 2 (Backup):** AccessibilityService
- Solo si necesario
- Requiere Permission Declaration Form
- Mayor escrutinio de Google

### 4. Scope - Modo Completo
- 12-16 semanas de desarrollo
- Todas las features desde V1
- Producto enterprise-ready

---

## Features V1 (Completo)

### Core ✅
- [x] NFC tag reading/writing (NTAG213/215/216)
- [x] Bloqueo básico de apps (UsageStatsManager)
- [x] Whitelist de apps esenciales
- [x] Multiple blocking profiles

### Advanced ✅
- [x] Timer-based auto-unblock
- [x] QR code alternative to NFC
- [x] Widgets (estado, quick toggle)
- [x] Usage statistics y gráficas
- [x] Physical unlock requirement (optional)
- [x] Focus Mode integration
- [x] Shortcuts & Quick Settings

---

## Estructura del Proyecto

```
umbral/
├── docs/
│   ├── guides/              # Guías de usuario/desarrollo
│   ├── reference/
│   │   ├── technical-decisions.md  # ✅ Creado
│   │   ├── competitive-analysis.md # 🔄 Template
│   │   ├── implementation-plan.md  # 🔄 Template
│   │   └── modules/        # Specs por módulo
│   ├── development/
│   │   ├── current/        # Features en progreso
│   │   └── completed/      # Features completadas
│   ├── archived/           # Docs obsoletos
│   └── temp/               # Temporal (max 5 archivos)
├── .claude/
│   ├── commands/           # Custom commands
│   ├── scripts/            # Automation scripts
│   ├── rules/              # Project-specific rules
│   └── context/            # Context for agents
├── app/                    # Android app (por crear)
└── CLAUDE.md               # Este archivo
```

---

## Reglas de Documentación

### SIEMPRE documentar:
- Nuevas features o sistemas
- Cambios de arquitectura
- Migraciones de base de datos
- Decisiones de diseño importantes
- Guías de testing para features complejas

### NUNCA documentar:
- Bugfixes menores
- Cambios de estilo/UI simples
- Ajustes de configuración triviales

### Ubicación de archivos:
- **Features en desarrollo:** `docs/development/current/<feature-name>/`
- **Features completadas:** `docs/development/completed/`
- **Specs técnicas:** `docs/reference/modules/<module-name>.md`
- **Guías permanentes:** `docs/guides/`

---

## Git Workflow

### Commits
- **NO** incluir "Generated with Claude Code" ni "Co-Authored-By: Claude"
- **Formato:** `[Type] Brief description`
- **Ejemplos:**
  - `[Feat] Add NFC tag reading module`
  - `[Fix] Resolve crash on permission denial`
  - `[Docs] Update architecture decisions`

### Branches
- `main` - Producción estable
- `develop` - Desarrollo activo
- `feature/nombre` - Features individuales

---

## Próximos Pasos Inmediatos

1. **AHORA:** Ejecutar `/oden:architect`
   - Completar arquitectura detallada
   - Schema de Room DB completo
   - Estructura de carpetas del código
   - Patrones de diseño

2. **Después:** Ejecutar `/oden:analyze`
   - Analizar Foqos, Brick, Unpluq en detalle
   - User personas
   - Priorización de features

3. **Luego:** Specs por módulo con `/oden:spec`
   - `nfc-module` (800-1200 líneas)
   - `app-blocking-module` (800-1200 líneas)
   - `profiles-module` (800-1200 líneas)
   - `ui-module` (800-1200 líneas)

4. **Finalmente:** `/oden:plan` - Plan semana por semana

---

## Recursos de Referencia

- [Foqos GitHub](https://github.com/awaseem/foqos) - iOS reference
- [Android NFC Guide](https://developer.android.com/develop/connectivity/nfc/nfc)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Material Design 3](https://m3.material.io/)

---

**Creado:** 2026-01-03T01:53:14Z
**Última actualización:** 2026-01-03T01:53:14Z
