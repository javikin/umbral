# Rediseño Pantalla de Bloqueo - Umbral

**Estado:** 🟢 Propuesta Activa
**Fecha:** 2026-01-15
**Versión:** 1.0

---

## Resumen Ejecutivo

Rediseño completo de la pantalla de bloqueo de Umbral basado en tendencias de apps de bienestar digital 2025-2026. Enfoque en refuerzo positivo, minimalismo funcional y animaciones significativas.

## Investigación de Mercado

### Apps Analizadas
1. **Forest** - Gamificación, colores naturales
2. **Opal** - Glassmorphism, animaciones fluidas
3. **One Sec** - Breathing exercises, diseño calmado
4. **ScreenZen** - Motivación positiva, stats visuales
5. **Headspace** - Gradientes suaves, ilustraciones abstractas
6. **Calm** - Paisajes naturales, colores profundos

### Tendencias Clave 2025-2026

1. **Glassmorphism & Depth**
   - Fondos con blur y transparencias
   - Capas con profundidad visual sutil
   - Sombras suaves

2. **Animaciones Significativas**
   - Spring animations (física realista)
   - Breathing animations para calma
   - Micro-interacciones que refuerzan comportamiento

3. **Mensajería Positiva**
   - Refuerzo en lugar de castigo
   - Estadísticas de logros
   - Mensajes rotativos motivacionales

4. **Paletas Naturales**
   - Inspiradas en naturaleza (cielo, plantas, agua)
   - Material You dynamic colors
   - Transiciones suaves claro/oscuro

5. **Minimalismo Funcional**
   - Una acción primaria clara
   - Jerarquía visual fuerte
   - Información sin saturación

## Propuesta de Diseño

### Layout Principal

```
┌─────────────────────────────────┐
│    [Status Bar]                 │
├─────────────────────────────────┤
│                                 │
│         [Breathing Icon]        │ ← Animación de respiración
│                                 │
│    "Estás en modo enfoque"      │ ← Título contextual
│                                 │
│  ┌──────────────────────────┐  │
│  │  💡 Mensaje rotativo     │  │ ← Card glassmorphism
│  │  "Tu yo futuro te lo     │  │
│  │   agradecerá"            │  │
│  └──────────────────────────┘  │
│                                 │
│  ┌──────────────────────────┐  │
│  │  📊 Enfocado hoy         │  │ ← Stats (opcional)
│  │      2h 35min            │  │
│  └──────────────────────────┘  │
│                                 │
│  ┌──────────────────────────┐  │
│  │  🎯 Perfil: Trabajo      │  │ ← Perfil activo
│  └──────────────────────────┘  │
│                                 │
│   [Botón: Volver al Inicio]    │ ← Acción principal
│                                 │
│   [Link: Emergencia]            │ ← Discreto
│                                 │
└─────────────────────────────────┘
```

### Paleta de Colores

**Modo Claro:**
- Background: `#E8F4F8` (azul cielo suave)
- Surface: `#FFFBFE` (blanco cálido)
- Primary: `#0A4D68` (azul profundo)
- Success: `#4CAF50` (verde natural)
- Warning: `#FFA726` (ámbar suave)

**Modo Oscuro:**
- Background: `#0D1B2A` (azul noche)
- Surface: `#1B263B` (superficie oscura)
- Primary: `#66BB6A` (verde suave)
- Text: `#E0E1DD` (gris claro)

**Material You:**
- Usar `surfaceVariant` para cards
- `primaryContainer` para elementos destacados
- `onSurface` con opacidad para jerarquía

### Tipografía

- **Título:** 24sp, Medium - "Estás en modo enfoque"
- **Mensaje:** 18sp, Regular - Contenido motivacional
- **Stats:** 16sp, Regular - Información secundaria
- **Botones:** 14sp, Medium - Acciones

### Iconografía

**Icono Principal:**
- Breathing circle (círculos concéntricos)
- Tamaño: 120dp
- Animación: escala 1.0 → 1.15 → 1.0 (4s)

**Iconos de Contenido:**
- Material Symbols (rounded, weight 400)
- Tamaño: 24dp
- Color: `onSurfaceVariant`

## Animaciones

### 1. Breathing Animation (Icono Central)
```
Escala: 1.0 → 1.15 → 1.0
Duración: 4000ms
Easing: EaseInOutCubic
Repeat: Infinite
Opacidad inner: 0.3 → 0.7 → 0.3
```

### 2. Entrada de Pantalla
```
Fade: 0 → 1 (300ms)
SlideUp: 50dp → 0dp (400ms, spring)
Stagger: 100ms entre elementos
```

### 3. Rotación de Mensajes
```
Crossfade: 300ms
Intervalo: 8 segundos
Random sin repetición inmediata
```

### 4. Botón Interacción
```
onPress: scale 0.95 + haptic light
onRelease: spring to 1.0
```

### 5. Modo Estricto Pulse
```
Alpha: 0.7 → 1.0 → 0.7
Duración: 2000ms
Repeat: 3 veces, luego stop
```

## Estados

### Estado 1: Modo Normal
- Botón "Volver al inicio" visible
- Link "¿Emergencia?" abajo (discreto)
- Sin restricciones adicionales

### Estado 2: Modo Estricto
- Chip "Modo estricto activo" arriba
- Botón "Escanear NFC para desbloquear"
- Icono NFC visible
- Texto: "Necesitas tu tag para continuar"

### Estado 3: Con Timer
- Card extra: "Desbloqueará en 1h 25min"
- Progreso circular en icono
- Opción "Cancelar" si permitido

### Estado 4: Primera Vez
- Tooltip sobre icono central
- Mensaje especial: "¡Gran decisión!"

## Mensajes Motivacionales

Pool de 12 mensajes rotativos:

1. "Estás eligiendo conscientemente tu tiempo" 🧘
2. "Tu yo futuro te lo agradecerá" 💡
3. "Pequeñas decisiones, grandes cambios" 📈
4. "Estás construyendo un mejor hábito" ⭐
5. "El control es tuyo" 🛡️
6. "Cada momento cuenta" ⏱️
7. "Tu atención es valiosa" 💎
8. "Enfócate en lo que importa" ❤️
9. "Estás presente, estás aquí" ☀️
10. "Tu bienestar primero" 🌿
11. "Eligiendo calma sobre caos" 🌊
12. "Tu concentración merece protección" 🔒

## Accesibilidad

### Contraste
- Texto normal: mínimo 4.5:1
- Texto importante: 7:1
- Verificar con Material 3 checker

### Touch Targets
- Botón principal: 48dp altura mínima
- Link emergencia: 44dp área tocable
- Spacing: 8dp mínimo

### Screen Readers
- Semantic descriptions para iconos
- Anunciar cambios de mensaje
- Labels claros para estados

### Animaciones
- Respetar `ANIMATOR_DURATION_SCALE`
- Soporte para reduced motion

## Notas de Implementación

### Dependencias Compose
```kotlin
// Animaciones
implementation "androidx.compose.animation:animation:1.6.0"

// Material 3
implementation "androidx.compose.material3:material3:1.2.0"

// Icons extended
implementation "androidx.compose.material:material-icons-extended:1.6.0"
```

### Performance
- Lazy loading de mensajes
- Cancelar animaciones en onDispose
- Usar `derivedStateOf` para estados computados
- Evitar recomposiciones innecesarias

### Testing
- Screenshot tests para estados
- Accessibility scanner
- Contrast checker automation
- Animation delay tests

## Próximos Pasos

1. ✅ Crear wireframes en Figma (opcional)
2. 🔄 Implementar composables base
3. 🔄 Agregar animaciones
4. 🔄 Testing de accesibilidad
5. ⏳ User testing con beta testers

---

**Referencias:**
- Material Design 3: https://m3.material.io/
- Compose Animation: https://developer.android.com/jetpack/compose/animation
- Accessibility: https://developer.android.com/guide/topics/ui/accessibility

**Creado:** 2026-01-15
**Última actualización:** 2026-01-15
