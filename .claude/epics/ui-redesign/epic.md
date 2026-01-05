---
name: ui-redesign
description: Rediseño completo de UI de Umbral con estilo minimalista y limpio
status: backlog
created: 2026-01-05T03:15:00Z
updated: 2026-01-05T03:30:00Z
github: https://github.com/javikin/umbral/issues/25
progress: 0%
---

# Epic: UI Redesign - Umbral

## Resumen

Transformar la interfaz de usuario de Umbral de una app funcional pero básica a una experiencia minimalista, limpia y premium que transmita calma, control y elegancia.

## Objetivos

1. Implementar sistema de diseño consistente (colores, tipografía, espaciado)
2. Rediseñar todas las pantallas con el nuevo estilo
3. Agregar animaciones y micro-interacciones significativas
4. Crear ilustraciones minimalistas para onboarding y estados
5. Mejorar la experiencia de la pantalla de bloqueo (de "castigo" a "apoyo")

## Alcance Técnico

### Sistema de Diseño

**Colores:**
```kotlin
// Light Theme
val Primary = Color(0xFF6366F1)      // Indigo - confianza, calma
val Secondary = Color(0xFF8B5CF6)    // Violeta - premium
val Success = Color(0xFF10B981)      // Verde menta - logro
val Warning = Color(0xFFF59E0B)      // Ámbar - atención
val Error = Color(0xFFEF4444)        // Rojo - errores
val Background = Color(0xFFFAFAFA)   // Casi blanco
val Surface = Color(0xFFFFFFFF)      // Blanco puro
val OnSurface = Color(0xFF1F2937)    // Gris oscuro
val OnSurfaceVariant = Color(0xFF6B7280) // Gris medio

// Dark Theme
val DarkBackground = Color(0xFF0F172A)  // Azul muy oscuro
val DarkSurface = Color(0xFF1E293B)     // Azul oscuro
val DarkOnSurface = Color(0xFFF8FAFC)   // Casi blanco
```

**Tipografía:**
- Display: 32sp, SemiBold
- Headline: 24sp, SemiBold
- Title: 20sp, Medium
- Body: 16sp, Regular
- Label: 14sp, Medium
- Caption: 12sp, Regular

**Espaciado:**
- xs: 4dp, sm: 8dp, md: 16dp, lg: 24dp, xl: 32dp, 2xl: 48dp
- Screen padding: 20dp horizontal
- Card padding: 16dp

**Border Radius:**
- Small: 8dp (chips, buttons)
- Medium: 12dp (cards pequeñas)
- Large: 16dp (cards principales)
- XL: 24dp (modals, bottom sheets)

### Pantallas a Modificar

1. **Onboarding Flow** (6 pantallas)
   - Welcome Screen con ilustración hero
   - How It Works con 3 pasos animados
   - Permissions con cards limpias
   - Select Apps (ya mejorado, pulir más)
   - How to Unblock con ilustraciones NFC/QR
   - Success con celebración visual

2. **Home Screen**
   - Card principal con gradiente/icono animado
   - Streak display con mini calendario
   - Quick actions rediseñadas
   - Stats preview con mini gráfica

3. **Blocking Screen**
   - Cambio de concepto: de "castigo" a "apoyo"
   - Background con gradiente suave (no rojo)
   - Shield icon en lugar de lock
   - Mensaje motivacional con streak
   - Breathing animation para calma

4. **Stats Screen** (nueva/mejorada)
   - Gráficas minimalistas
   - Métricas: streak, apps bloqueadas, tiempo ahorrado
   - Comparativas semana vs anterior

5. **Profiles Screen**
   - Cards más visuales con iconos/colores
   - Estados claros activo/inactivo
   - Animación al cambiar perfil

6. **Settings Screen**
   - Secciones con headers claros
   - Toggles con animación
   - About section con branding

### Componentes UI Nuevos

- `UmbralButton` - Botones con estados y animaciones
- `UmbralCard` - Cards con sombras y bordes consistentes
- `UmbralChip` - Chips de categoría/filtro
- `UmbralToggle` - Toggle con animación slide
- `AnimatedIcon` - Iconos con micro-animaciones
- `StatsGraph` - Gráfica minimalista reutilizable
- `StreakDisplay` - Componente de streak con calendario

### Dependencias Técnicas

```kotlin
// build.gradle.kts
implementation("com.airbnb.android:lottie-compose:6.3.0") // Animaciones
implementation("io.coil-kt:coil-compose:2.5.0") // Imágenes optimizadas
```

## Criterios de Aceptación

- [ ] Sistema de diseño implementado y documentado
- [ ] Todas las pantallas rediseñadas según spec
- [ ] Animaciones a 60fps constante
- [ ] Dark mode funcional
- [ ] Tiempo de carga < 2 segundos
- [ ] Screenshots dignos de compartir

## Tasks Created

- [ ] #26 - Implementar Design System foundation (colores, tipografía, spacing)
- [ ] #27 - Crear componentes UI base (Button, Card, Chip, Toggle)
- [ ] #28 - Rediseñar Onboarding Flow completo
- [ ] #29 - Rediseñar Home Screen
- [ ] #30 - Rediseñar Blocking Screen (concepto "apoyo")
- [ ] #31 - Crear Stats Screen
- [ ] #32 - Rediseñar Profiles Screen
- [ ] #33 - Rediseñar Settings Screen
- [ ] #34 - Agregar animaciones y micro-interacciones
- [ ] #35 - Implementar Dark Mode completo
- [ ] #36 - Crear ilustraciones/assets minimalistas
- [ ] #37 - Testing visual y polish final

Total tasks: 12
Parallel tasks: 4 (#26, #27, #35, #36 pueden empezar juntas)
Sequential tasks: 8 (dependen de foundation)
Estimated total effort: 80-100 hours

## Referencias

- PRD: `.claude/prds/ui-redesign.md`
- Inspiración: Headspace, Linear, Notion, Opal
- Material Design 3: https://m3.material.io/

---

**Fuente:** PRD ui-redesign
**Creado:** 2026-01-05
