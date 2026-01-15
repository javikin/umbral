# Blocking Screen Redesign - Umbral

**Estado:** 🟢 Propuesta Completa
**Fecha:** 2026-01-15
**Versión:** 1.0

---

## Resumen Ejecutivo

Rediseño completo de la pantalla de bloqueo de Umbral basado en las mejores prácticas y tendencias de apps de bienestar digital 2025-2026. El diseño enfatiza:

- **Refuerzo positivo** sobre castigo
- **Animaciones significativas** que promueven calma
- **Minimalismo funcional** con jerarquía visual clara
- **Accesibilidad** completa desde el diseño
- **Material Design 3** con soporte para Material You

---

## Contenido de esta Propuesta

### 📄 Documentos Incluidos

1. **[SUMMARY.md](SUMMARY.md)** ⭐ LEER PRIMERO
   - Resumen ejecutivo completo
   - TL;DR de toda la propuesta
   - Decisiones clave y justificaciones
   - Próximos pasos y timelines
   - Métricas de éxito

2. **[BlockingScreenDesign.md](BlockingScreenDesign.md)**
   - Investigación de mercado (Forest, Opal, One Sec, etc.)
   - Tendencias clave 2025-2026
   - Propuesta conceptual completa
   - Paletas de colores, tipografía, iconografía
   - Especificación de animaciones
   - Estados de la pantalla
   - Mensajes motivacionales

3. **[CompetitiveInsights.md](CompetitiveInsights.md)**
   - Análisis detallado de 6 apps líderes
   - Comparación de patrones de interacción
   - Paletas de colores benchmarking
   - Lecciones aprendidas
   - Recomendaciones DO/DON'T

4. **[BlockingScreen.kt](BlockingScreen.kt)**
   - Implementación completa en Jetpack Compose (530 líneas)
   - Todos los composables necesarios
   - Animaciones implementadas (breathing, crossfade, spring)
   - 4 preview states (normal, strict, timer, dark)
   - Documentación inline completa

5. **[UmbralTheme.kt](UmbralTheme.kt)**
   - Tema Material 3 personalizado
   - Paletas light/dark
   - Soporte para Material You dynamic colors
   - Tipografía y shapes configurados

6. **[strings.xml](strings.xml)**
   - Todos los textos en ESPAÑOL
   - 12 mensajes motivacionales
   - Strings de accesibilidad
   - Mensajes de error y éxito

7. **[ImplementationGuide.md](ImplementationGuide.md)**
   - Guía paso a paso de implementación
   - Dependencias requeridas
   - Integración con ViewModel
   - Testing (unit, UI, screenshot)
   - Performance optimization
   - Troubleshooting
   - Checklist completo

8. **[VisualMockups.md](VisualMockups.md)**
   - Mockups ASCII de todos los estados
   - Comparación antes/después
   - Visualización de animaciones frame-by-frame
   - Layouts responsivos

9. **[README.md](README.md)** (este archivo)
   - Índice general
   - Quick start
   - Arquitectura
   - Próximos pasos

---

## Guía de Lectura Según Rol

### Para Product Owners / Stakeholders
1. Leer: **[SUMMARY.md](SUMMARY.md)** - Resumen ejecutivo
2. Revisar: **[CompetitiveInsights.md](CompetitiveInsights.md)** - Análisis de mercado
3. Ver: **[VisualMockups.md](VisualMockups.md)** - Mockups visuales

### Para Diseñadores UX/UI
1. Leer: **[BlockingScreenDesign.md](BlockingScreenDesign.md)** - Propuesta de diseño
2. Revisar: **[CompetitiveInsights.md](CompetitiveInsights.md)** - Tendencias y benchmarking
3. Ver: **[VisualMockups.md](VisualMockups.md)** - Todas las variaciones

### Para Desarrolladores
1. Leer: **[SUMMARY.md](SUMMARY.md)** - Overview rápido
2. Revisar: **[BlockingScreen.kt](BlockingScreen.kt)** - Código fuente
3. Seguir: **[ImplementationGuide.md](ImplementationGuide.md)** - Paso a paso
4. Usar: **[IMPLEMENTATION_CHECKLIST.md](IMPLEMENTATION_CHECKLIST.md)** - Tracking
5. Consultar: **[QUICK_REFERENCE.md](QUICK_REFERENCE.md)** - Referencia rápida

### Para QA / Testers
1. Leer: **[SUMMARY.md](SUMMARY.md)** - Contexto general
2. Revisar: **[ImplementationGuide.md](ImplementationGuide.md)** - Sección Testing
3. Usar: **[IMPLEMENTATION_CHECKLIST.md](IMPLEMENTATION_CHECKLIST.md)** - Fase 6-7
4. Ver: **[VisualMockups.md](VisualMockups.md)** - Estados esperados

---

## Quick Start

### 1. Revisar Propuesta de Diseño

Leer primero: **[BlockingScreenDesign.md](BlockingScreenDesign.md)**

Este documento contiene:
- Investigación de competidores
- Tendencias identificadas
- Decisiones de diseño con justificación
- Especificaciones visuales detalladas

### 2. Explorar Implementación

Revisar código: `BlockingScreen.kt`

Características destacadas:
```kotlin
// Breathing animation (icono central)
BreathingIcon() // Escala infinita suave

// Mensajes rotativos
MotivationalCard() // Crossfade cada 8 segundos

// Glassmorphism
GlassCard() // Material 3 surface con transparencia

// Spring animations
PrimaryActionButton() // Física realista en interacción
```

### 3. Probar Previews

En Android Studio:
1. Abrir `BlockingScreen.kt`
2. Ir a la pestaña "Split" o "Design"
3. Verificar 4 preview states:
   - Normal mode (light)
   - Strict mode
   - With timer
   - Dark mode

### 4. Seguir Guía de Implementación

Ver: `ImplementationGuide.md`

Pasos cubiertos:
1. Setup de dependencias
2. Integración con ViewModel
3. Configuración de navegación
4. Testing strategy
5. Performance optimization
6. Accessibility verification

---

## Arquitectura del Componente

### Jerarquía de Composables

```
BlockingScreen (root)
├── Box (background gradient)
└── Column (main content)
    ├── BreathingIcon
    │   ├── Surface (outer circle)
    │   ├── Surface (middle circle)
    │   └── Surface (inner circle + icon)
    ├── Text (title)
    ├── MotivationalCard
    │   └── GlassCard
    │       └── AnimatedContent (crossfade)
    ├── StatsCard (conditional)
    │   └── GlassCard
    ├── ProfileCard
    │   └── GlassCard
    ├── TimerCard (conditional)
    │   └── GlassCard
    ├── StrictModeChip (conditional)
    ├── PrimaryActionButton
    └── TextButton (emergency, conditional)
```

### Estado y ViewModel

```kotlin
// State
data class BlockingState(
    val profileName: String,
    val isStrictMode: Boolean,
    val timerMinutesRemaining: Int?,
    val focusedTimeToday: String,
    val isFirstTime: Boolean
)

// ViewModel
class BlockingViewModel {
    val state: StateFlow<BlockingState>
    fun onBackToHome()
    fun onEmergencyAccess()
    fun onScanNfc()
}

// Screen
@Composable
fun BlockingScreen(
    state: BlockingState,
    onBackToHome: () -> Unit,
    onEmergencyAccess: () -> Unit,
    onScanNfc: () -> Unit
)
```

---

## Decisiones de Diseño Clave

### 1. Breathing Animation

**Por qué:** Técnica de calming usado en apps de meditación (Headspace, Calm).

**Implementación:**
- Escala: 1.0 → 1.15 → 1.0 (4 segundos)
- Easing: EaseInOutCubic (suave)
- Repeat: Infinite
- Efecto secundario: induce respiración consciente en el usuario

### 2. Mensajes Rotativos

**Por qué:** Refuerzo positivo variable es más efectivo que mensaje estático.

**Implementación:**
- Pool de 12 mensajes
- Rotación cada 8 segundos
- Random sin repetición inmediata (tracking de últimos 3)
- Crossfade suave (300ms)

### 3. Glassmorphism en Cards

**Por qué:** Tendencia 2025, transmite profundidad sin saturar visualmente.

**Implementación:**
- `surfaceVariant` con 70% alpha
- Elevation sutil (2dp tonal, 4dp shadow)
- BorderRadius large (16dp)

### 4. Modo Estricto con Pulso

**Por qué:** Llamar atención inicial sin molestar continuamente.

**Implementación:**
- Alpha pulse: 0.7 → 1.0 (1 segundo)
- Se detiene después de 3 ciclos
- Color: `errorContainer` para indicar restricción

### 5. Spring Animation en Botón

**Por qué:** Feedback táctil visual, se siente natural.

**Implementación:**
- Scale down a 0.95 en press
- Spring back con `DampingRatioMediumBouncy`
- Haptic feedback complementario

---

## Estados Soportados

### Estado 1: Modo Normal
**Cuándo:** Usuario puede cerrar fácilmente.

**UI:**
- Botón "Volver al inicio" prominente
- Link "¿Emergencia?" discreto
- Sin chips de advertencia
- Stats y perfil visible

### Estado 2: Modo Estricto
**Cuándo:** Requiere NFC/QR para desbloquear.

**UI:**
- Chip "Modo estricto activo" con pulso
- Botón "Escanear para desbloquear" con icono NFC
- Sin link de emergencia
- Stats y perfil visible

### Estado 3: Con Timer
**Cuándo:** Auto-unlock programado.

**UI:**
- Card adicional: "Se desbloqueará en X"
- Opcional: progreso circular en icono
- Botón según modo (normal/estricto)
- Stats y perfil visible

### Estado 4: Primera Vez
**Cuándo:** Usuario nunca ha sido bloqueado.

**UI:**
- Tooltip sobre icono central
- Mensaje especial: "¡Gran decisión!"
- Resto igual a modo normal

---

## Paleta de Colores

### Modo Claro (Light)

| Nombre | Hex | Uso |
|--------|-----|-----|
| FocusSky | `#E8F4F8` | Background principal |
| DeepFocus | `#0A4D68` | Primary (botones, acentos) |
| FocusLeaf | `#4CAF50` | Tertiary (stats positivas) |
| FocusAmber | `#FFA726` | Warning (si se usa) |
| FocusSurface | `#FFFBFE` | Surface de cards |
| FocusSurfaceVariant | `#E7F2F5` | Cards con glassmorphism |

### Modo Oscuro (Dark)

| Nombre | Hex | Uso |
|--------|-----|-----|
| NightSky | `#0D1B2A` | Background principal |
| MoonGlow | `#415A77` | Primary container |
| NightLeaf | `#66BB6A` | Tertiary (stats) |
| StarLight | `#E0E1DD` | Texto principal |
| Surface | `#1B263B` | Surface de cards |
| SurfaceVariant | `#415A77` | Cards glassmorphism |

### Material You

Cuando disponible (Android 12+):
- `dynamicLightColorScheme(context)`
- `dynamicDarkColorScheme(context)`

---

## Animaciones Especificadas

### 1. Breathing (Icono Central)
```kotlin
Tipo: Infinite scale
Duración: 2000ms ida + 2000ms vuelta = 4000ms total
Easing: EaseInOutCubic
Valores: scale 1.0 ↔ 1.15, alpha 0.3 ↔ 0.7
Repeat: Infinite
```

### 2. Screen Enter
```kotlin
Tipo: Fade + SlideUp con stagger
Fade: 0 → 1 (300ms)
Slide: 50dp → 0dp (400ms, spring)
Stagger: 100ms entre elementos
```

### 3. Message Rotation
```kotlin
Tipo: Crossfade
Duración: 300ms out, 300ms in
Interval: 8000ms
Lógica: Random sin repetición (tracking 3 últimos)
```

### 4. Button Press
```kotlin
Tipo: Scale animation
Press: scale 1.0 → 0.95 (instant)
Release: scale 0.95 → 1.0 (spring)
Haptic: LongPress feedback
```

### 5. Strict Chip Pulse
```kotlin
Tipo: Alpha pulse limitado
Duración: 1000ms por ciclo
Valores: alpha 1.0 ↔ 0.7
Repeat: 3 ciclos, luego stop en alpha 1.0
```

---

## Mensajes Motivacionales

Pool de 12 mensajes con iconos:

1. "Estás eligiendo conscientemente tu tiempo" - SelfImprovement
2. "Tu yo futuro te lo agradecerá" - EmojiObjects
3. "Pequeñas decisiones, grandes cambios" - TrendingUp
4. "Estás construyendo un mejor hábito" - Stars
5. "El control es tuyo" - Shield
6. "Cada momento cuenta" - Timer
7. "Tu atención es valiosa" - Diamond
8. "Enfócate en lo que importa" - Favorite
9. "Estás presente, estás aquí" - WbSunny
10. "Tu bienestar primero" - Spa
11. "Eligiendo calma sobre caos" - Waves
12. "Tu concentración merece protección" - Security

**Estrategia de rotación:**
- Random selection cada 8 segundos
- Tracking de últimos 3 mensajes para evitar repetición
- Crossfade suave en transición

---

## Testing Strategy

### Unit Tests
```kotlin
BlockingViewModelTest
├── initial_state_has_default_values
├── loads_active_profile_correctly
├── updates_focused_time_today
└── handles_emergency_access_request
```

### UI Tests (Compose)
```kotlin
BlockingScreenTest
├── normalMode_showsCorrectButtons
├── strictMode_showsNfcButton
├── withTimer_showsTimerCard
├── breathingAnimation_isVisible
└── motivationalCard_rotatesMessages
```

### Screenshot Tests
```kotlin
BlockingScreenScreenshotTest
├── normalMode_light
├── normalMode_dark
├── strictMode_light
├── strictMode_dark
├── withTimer_light
└── firstTime_light
```

### Accessibility Tests
- Contrast checker (automated)
- Screen reader testing (manual)
- Touch target verification (automated)
- Reduced motion support (manual)

---

## Checklist de Implementación

### ✅ Pre-implementación
- [x] Investigación de mercado completada
- [x] Diseño conceptual definido
- [x] Paleta de colores especificada
- [x] Animaciones documentadas
- [x] Estados identificados
- [x] Código base escrito
- [x] Guía de implementación creada

### 🔄 Durante Implementación
- [ ] Dependencias agregadas
- [ ] Archivos copiados a proyecto
- [ ] Strings.xml configurado
- [ ] Theme integrado
- [ ] ViewModel creado
- [ ] Navegación configurada
- [ ] Previews verificadas

### ⏳ Testing
- [ ] Unit tests escritos
- [ ] UI tests escritos
- [ ] Screenshot tests generados
- [ ] Accessibility scanner ejecutado
- [ ] Manual testing completado

### 🎯 Pre-release
- [ ] Performance profiling
- [ ] Contrast verification
- [ ] Dark/Light themes verificados
- [ ] Material You tested
- [ ] Reduced motion tested
- [ ] Haptic feedback tested

---

## Performance Considerations

### Optimizaciones Implementadas

1. **Animaciones:**
   - Respetar `ANIMATOR_DURATION_SCALE`
   - Cleanup en `DisposableEffect`
   - Uso eficiente de `rememberInfiniteTransition`

2. **Recomposiciones:**
   - `derivedStateOf` para estados computados
   - `remember` para valores estables
   - Keys específicas en `LaunchedEffect`

3. **Memory:**
   - No se crean objetos en loop de animación
   - Pool de mensajes pre-definido
   - Cancellation de coroutines en dispose

4. **Rendering:**
   - Minimize overdraw (capas transparentes limitadas)
   - Lazy composition where possible
   - Efficient preview functions

---

## Accesibilidad Features

### Implementado

- ✅ Content descriptions en todos los iconos
- ✅ Semantic roles en elementos interactivos
- ✅ Touch targets mínimo 48dp
- ✅ Contrast ratio 4.5:1 mínimo
- ✅ Support para reduced motion
- ✅ Screen reader friendly

### Por Implementar (Cuando se integre)

- [ ] Anunciar cambios de mensaje motivacional
- [ ] Talkback testing extensivo
- [ ] Focus order verification
- [ ] Keyboard navigation support (if applicable)

---

## Próximos Pasos

### Fase 1: Implementación Base (Semana 1)
1. Copiar archivos a proyecto real
2. Configurar dependencias
3. Integrar con ViewModel existente
4. Verificar previews en Android Studio

### Fase 2: Testing (Semana 2)
1. Escribir unit tests
2. Escribir UI tests
3. Generar screenshot tests
4. Ejecutar accessibility scanner

### Fase 3: Refinamiento (Semana 3)
1. Performance profiling
2. Ajustar animaciones según feedback
3. Verificar en dispositivos reales
4. Testing con usuarios beta

### Fase 4: Lanzamiento (Semana 4)
1. Code review final
2. Merge a develop
3. QA testing
4. Release a producción

---

## Recursos de Referencia

### Documentación Oficial
- [Material Design 3](https://m3.material.io/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Compose Animation](https://developer.android.com/jetpack/compose/animation)
- [Accessibility](https://developer.android.com/guide/topics/ui/accessibility)

### Apps de Referencia
- [Forest](https://www.forestapp.cc/)
- [Opal](https://www.opal.so/)
- [One Sec](https://one-sec.app/)
- [ScreenZen](https://screenzen.app/)
- [Foqos (iOS)](https://github.com/awaseem/foqos)

### Herramientas
- Android Studio Layout Inspector
- Accessibility Scanner
- Paparazzi (Screenshot testing)
- Compose Preview

---

## Contacto y Feedback

Para preguntas o sugerencias sobre esta propuesta:

1. Revisar `ImplementationGuide.md` para detalles técnicos
2. Consultar `BlockingScreenDesign.md` para decisiones de diseño
3. Ver código en `BlockingScreen.kt` para implementación

**Mantener documentación actualizada conforme se implemente.**

---

**Creado:** 2026-01-15
**Última actualización:** 2026-01-15
**Versión:** 1.0
**Estado:** 🟢 Listo para implementación
