---
name: design-system-v2-components
description: Umbral Design System 2.0 - Fase 2 Componentes con rediseño completo y nuevos elementos UI
status: backlog
created: 2026-01-16T19:51:47Z
---

# PRD: Umbral Design System 2.0 - Fase 2: Componentes

## Executive Summary

Rediseño completo de la biblioteca de componentes de Umbral siguiendo los tokens establecidos en Fase 1. Incluye mejora de componentes existentes (botones, cards, inputs, navegación) y creación de nuevos componentes esenciales (empty states, feedback visual, data display, skeletons). El objetivo es lograr una experiencia premium, minimalista y cohesiva en toda la app.

**Dependencia:** Este PRD requiere la implementación previa de `design-system-v2` (Fase 1: Tokens).

---

## Problem Statement

### Problema Actual
- Los componentes actuales fueron creados incrementalmente sin una visión unificada
- Falta de componentes esenciales para UX completa (empty states, skeletons)
- No existe un catálogo visual para verificar consistencia
- Las micro-interacciones no están estandarizadas

### Por Qué Ahora
- Fase 1 (Tokens) establece la base visual necesaria
- Momento ideal para refactorizar antes de nuevos módulos
- Inversión que pagará dividendos en velocidad de desarrollo futura

---

## User Stories

### US-1: Como usuario
**Quiero** que cada interacción se sienta fluida y premium
**Para** tener una experiencia que me motive a seguir usando la app.

**Criterios de Aceptación:**
- [ ] Botones responden con feedback visual inmediato
- [ ] Transiciones entre estados son suaves (no hay "saltos")
- [ ] Loading states claros cuando algo está procesando
- [ ] Empty states guían hacia la siguiente acción

### US-2: Como desarrollador
**Quiero** componentes reutilizables con API consistente
**Para** implementar pantallas nuevas rápidamente sin reinventar la rueda.

**Criterios de Aceptación:**
- [ ] Cada componente tiene preview funcional en Android Studio
- [ ] API de componentes es predecible (mismos patrones)
- [ ] Documentación inline explica uso y variantes
- [ ] Catálogo de debug muestra todos los componentes

### US-3: Como QA/diseñador
**Quiero** ver todos los componentes en un solo lugar
**Para** verificar consistencia visual y detectar regresiones.

**Criterios de Aceptación:**
- [ ] Pantalla de catálogo accesible en modo debug
- [ ] Cada componente mostrado en todos sus estados
- [ ] Toggle para cambiar entre light/dark theme en catálogo

---

## Requirements

### Functional Requirements

---

## SECCIÓN A: REDISEÑO DE COMPONENTES EXISTENTES

---

### A1: Sistema de Botones

**Filosofía:** Filled + Ghost - Minimalista, sin ruido visual innecesario.

#### A1.1: UmbralButton (Primary)

```kotlin
@Composable
fun UmbralButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: ImageVector? = null,
    size: ButtonSize = ButtonSize.Medium
)

enum class ButtonSize {
    Small,   // height: 36.dp, text: labelMedium
    Medium,  // height: 48.dp, text: labelLarge
    Large    // height: 56.dp, text: titleSmall
}
```

**Especificaciones Visuales:**
| Propiedad | Valor |
|-----------|-------|
| Background | `accentPrimary` (#4ECDC4 dark / #3DB5AD light) |
| Text Color | `#151515` (siempre oscuro para contraste) |
| Corner Radius | `8.dp` (UmbralShape.sm) |
| Padding H | `24.dp` |
| Border | None |
| Shadow | None (flat) |

**Estados y Animaciones:**
| Estado | Cambio Visual | Animación |
|--------|--------------|-----------|
| Default | Como especificado | - |
| Pressed | `accentPressed`, scale 0.98 | spring(dampingRatio=0.6, stiffness=500) |
| Hover | `accentHover` | tween(150ms, easeOut) |
| Disabled | 40% opacity | - |
| Loading | Spinner reemplaza texto | crossfade(200ms) |

#### A1.2: UmbralTextButton (Ghost/Secondary)

```kotlin
@Composable
fun UmbralTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    destructive: Boolean = false  // Para acciones de eliminar
)
```

**Especificaciones Visuales:**
| Propiedad | Valor |
|-----------|-------|
| Background | `transparent` |
| Text Color | `accentPrimary` (o `error` si destructive) |
| Corner Radius | `8.dp` |
| Padding H | `16.dp` |
| Border | None |

**Estados:**
| Estado | Cambio Visual |
|--------|--------------|
| Default | Solo texto en accent |
| Pressed | Background `accentPrimary` 10% opacity |
| Disabled | 40% opacity |

#### A1.3: UmbralIconButton

```kotlin
@Composable
fun UmbralIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String,
    size: IconButtonSize = IconButtonSize.Medium,
    variant: IconButtonVariant = IconButtonVariant.Ghost
)

enum class IconButtonSize {
    Small,  // 32.dp, icon 18.dp
    Medium, // 40.dp, icon 24.dp
    Large   // 48.dp, icon 28.dp
}

enum class IconButtonVariant {
    Ghost,    // Sin fondo
    Filled,   // Fondo accent
    Tonal     // Fondo accent 10% opacity
}
```

---

### A2: Cards y Contenedores

**Filosofía:** Flat con borde sutil - Profundidad sin sombras.

#### A2.1: UmbralCard

```kotlin
@Composable
fun UmbralCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,  // null = no clickeable
    variant: CardVariant = CardVariant.Default,
    content: @Composable ColumnScope.() -> Unit
)

enum class CardVariant {
    Default,    // Borde estándar
    Elevated,   // Fondo surface elevated
    Outlined,   // Borde más visible
    Interactive // Hover/press states
}
```

**Especificaciones Visuales:**
| Propiedad | Dark Theme | Light Theme |
|-----------|------------|-------------|
| Background | `backgroundSurface` (#1E1E1E) | `backgroundSurface` (#FFFFFF) |
| Border | 1px `borderDefault` (6% white) | 1px `borderDefault` (4% black) |
| Corner Radius | `16.dp` (UmbralShape.lg) |
| Padding | `16.dp` (UmbralSpacing.md) |
| Shadow | None | None |

**Estados (si Interactive):**
| Estado | Cambio |
|--------|--------|
| Pressed | Background +4% lighter, scale 0.99 |
| Focused | Border `borderFocus` (30% accent) |

#### A2.2: UmbralSurface

```kotlin
@Composable
fun UmbralSurface(
    modifier: Modifier = Modifier,
    elevation: SurfaceElevation = SurfaceElevation.Level1,
    shape: Shape = UmbralShape.md,
    content: @Composable () -> Unit
)

enum class SurfaceElevation {
    Level0,  // backgroundBase
    Level1,  // backgroundSurface
    Level2,  // backgroundElevated
    Level3   // backgroundElevated + 2% overlay
}
```

#### A2.3: UmbralDivider

```kotlin
@Composable
fun UmbralDivider(
    modifier: Modifier = Modifier,
    variant: DividerVariant = DividerVariant.Full
)

enum class DividerVariant {
    Full,    // Ancho completo
    Inset,   // Con padding horizontal 16.dp
    Middle   // Con padding horizontal 72.dp (para listas con iconos)
}
```

**Especificaciones:**
- Height: `1.dp`
- Color: `borderDefault`

---

### A3: Inputs y Formularios

#### A3.1: UmbralTextField

```kotlin
@Composable
fun UmbralTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    error: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
)
```

**Especificaciones Visuales:**
| Propiedad | Valor |
|-----------|-------|
| Background | `backgroundSurface` |
| Border (default) | 1px `borderDefault` |
| Border (focused) | 2px `accentPrimary` |
| Border (error) | 2px `error` |
| Corner Radius | `12.dp` (UmbralShape.md) |
| Height | `56.dp` |
| Padding H | `16.dp` |
| Label | `textSecondary`, animates up on focus |
| Placeholder | `textTertiary` |
| Input Text | `textPrimary` |

**Animaciones:**
| Transición | Especificación |
|------------|---------------|
| Label float | tween(150ms, easeOut) |
| Border color | tween(200ms) |
| Error shake | spring con 3 oscillations |

#### A3.2: UmbralSearchField

```kotlin
@Composable
fun UmbralSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Buscar...",
    onClear: () -> Unit = { }
)
```

**Especificaciones:**
- Icono search a la izquierda
- Botón clear (X) cuando hay texto
- Border radius: `full` (pill shape)
- Height: `48.dp`

#### A3.3: UmbralCheckbox

```kotlin
@Composable
fun UmbralCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String? = null
)
```

**Especificaciones:**
| Estado | Visual |
|--------|--------|
| Unchecked | Border 2px `borderDefault`, background transparent |
| Checked | Background `accentPrimary`, checkmark blanco |
| Indeterminate | Background `accentPrimary`, dash blanco |

**Animación check:**
- Path drawing animation 200ms
- Scale bounce en check: spring(dampingRatio=0.5)

#### A3.4: UmbralSwitch (rediseño de UmbralToggle)

```kotlin
@Composable
fun UmbralSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String? = null
)
```

**Especificaciones:**
| Propiedad | Off | On |
|-----------|-----|-----|
| Track BG | `borderDefault` (12% opacity) | `accentPrimary` |
| Track Width | `52.dp` | |
| Track Height | `32.dp` | |
| Thumb Color | `textSecondary` | `#151515` |
| Thumb Size | `28.dp` | |

**Animación:**
- Thumb position: spring(dampingRatio=0.6, stiffness=400)
- Track color: tween(200ms)

---

### A4: Navegación

#### A4.1: UmbralBottomBar (Tab Bar Minimalista)

```kotlin
@Composable
fun UmbralBottomBar(
    items: List<BottomBarItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
)

data class BottomBarItem(
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon,
    val label: String,
    val badge: Int? = null  // Para notificaciones
)
```

**Especificaciones Visuales:**
| Propiedad | Valor |
|-----------|-------|
| Background | `backgroundBase` |
| Top Border | 1px `borderDefault` |
| Height | `64.dp` |
| Icon Size | `28.dp` |
| Label | Hidden (solo iconos) |
| Indicator | Línea 3px debajo del icono activo |

**Estados:**
| Estado | Visual |
|--------|--------|
| Unselected | Icon `textSecondary` |
| Selected | Icon `accentPrimary`, línea indicadora |
| Pressed | Icon `accentPrimary`, ripple subtle |

**Animación indicador:**
- Position: spring(dampingRatio=0.8, stiffness=300)
- Width expand/contract: tween(200ms)

#### A4.2: UmbralTopBar

```kotlin
@Composable
fun UmbralTopBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = { }
)
```

**Especificaciones:**
| Propiedad | Valor |
|-----------|-------|
| Background | `backgroundBase` |
| Height | `64.dp` |
| Title Style | `titleLarge` |
| Bottom Border | None (seamless) |
| Elevation | 0 |

#### A4.3: UmbralTabRow

```kotlin
@Composable
fun UmbralTabRow(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
)
```

**Especificaciones:**
- Indicator: pill shape con `accentPrimary` 15% opacity
- Text unselected: `textSecondary`
- Text selected: `textPrimary`
- Animación: indicator slides con spring

---

## SECCIÓN B: NUEVOS COMPONENTES

---

### B1: Empty States

#### B1.1: UmbralEmptyState

```kotlin
@Composable
fun UmbralEmptyState(
    illustration: EmptyStateIllustration,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    action: EmptyStateAction? = null
)

enum class EmptyStateIllustration {
    NoProfiles,     // Perfil con líneas punteadas
    NoApps,         // Grid de apps vacío
    NoStats,        // Gráfica vacía
    NoNfc,          // Tag NFC con ?
    SearchEmpty,    // Lupa con X
    Success,        // Checkmark
    Error,          // Triángulo alerta
    Offline         // Nube con X
}

data class EmptyStateAction(
    val label: String,
    val onClick: () -> Unit
)
```

**Especificaciones Visuales:**
| Elemento | Especificación |
|----------|---------------|
| Illustration | Line art monocromático, 120x120.dp |
| Illustration Color | `textTertiary` con detalles en `accentPrimary` |
| Title | `titleMedium`, `textPrimary`, centered |
| Description | `bodyMedium`, `textSecondary`, centered, max 2 líneas |
| Action | UmbralButton size Small, margin top 24.dp |
| Vertical Spacing | 16.dp entre elementos |

**Ilustraciones Minimalistas (Line Art):**
```
NoProfiles:
    ┌─────────────┐
    │  ┌─────┐    │
    │  │  ?  │    │
    │  └─────┘    │
    │ - - - - - - │
    │ - - - - - - │
    └─────────────┘

NoStats:
      │
      │
    ──┼─────────
      │

SearchEmpty:
      ◯
     /
    ✕
```

#### B1.2: Ilustraciones como Composables

```kotlin
@Composable
fun EmptyStateIllustration.Render(
    modifier: Modifier = Modifier,
    primaryColor: Color = UmbralTheme.colors.textTertiary,
    accentColor: Color = UmbralTheme.colors.accentPrimary
)
```

---

### B2: Feedback Visual

#### B2.1: UmbralSnackbar

```kotlin
@Composable
fun UmbralSnackbar(
    message: String,
    modifier: Modifier = Modifier,
    variant: SnackbarVariant = SnackbarVariant.Default,
    action: SnackbarAction? = null,
    duration: SnackbarDuration = SnackbarDuration.Short
)

enum class SnackbarVariant {
    Default,  // Background surface
    Success,  // Con icono check, borde success
    Error,    // Con icono X, borde error
    Warning   // Con icono !, borde warning
}

enum class SnackbarDuration {
    Short,    // 3 segundos
    Medium,   // 5 segundos
    Long,     // 8 segundos
    Indefinite
}
```

**Especificaciones:**
| Propiedad | Valor |
|-----------|-------|
| Background | `backgroundElevated` |
| Border | 1px del color semántico (success/error/etc) |
| Corner Radius | `12.dp` |
| Padding | `16.dp` |
| Icon Size | `20.dp` |
| Max Width | `400.dp` |
| Position | Bottom, 16.dp margin |

**Animaciones:**
| Transición | Especificación |
|------------|---------------|
| Enter | slideInVertically + fadeIn, 250ms |
| Exit | slideOutVertically + fadeOut, 200ms |

#### B2.2: UmbralToast (más sutil que Snackbar)

```kotlin
@Composable
fun UmbralToast(
    message: String,
    icon: ImageVector? = null
)
```

**Especificaciones:**
- Más compacto que Snackbar
- Sin acción, solo informativo
- Aparece en top-center
- Auto-dismiss 2 segundos
- Background con blur (si performance ok)

#### B2.3: UmbralProgressIndicator

```kotlin
@Composable
fun UmbralProgressIndicator(
    modifier: Modifier = Modifier,
    variant: ProgressVariant = ProgressVariant.Circular
)

@Composable
fun UmbralProgressBar(
    progress: Float,  // 0f to 1f
    modifier: Modifier = Modifier
)

enum class ProgressVariant {
    Circular,    // Spinner estándar
    Dots,        // 3 dots animados
    Pulse        // Círculo que pulsa
}
```

**Especificaciones Circular:**
- Size: `24.dp` (small), `40.dp` (medium), `56.dp` (large)
- Stroke: `3.dp`
- Color: `accentPrimary`
- Animation: rotate 360° en 1000ms, easeInOut

**Especificaciones Dots:**
- 3 circles de `8.dp`
- Spacing: `8.dp`
- Animation: scale staggered, 600ms total cycle

**Especificaciones Progress Bar:**
- Height: `4.dp`
- Background: `borderDefault`
- Fill: `accentPrimary`
- Corner Radius: `full`
- Animation: smooth width transition

---

### B3: Data Display

#### B3.1: UmbralBadge

```kotlin
@Composable
fun UmbralBadge(
    content: String,
    modifier: Modifier = Modifier,
    variant: BadgeVariant = BadgeVariant.Default
)

enum class BadgeVariant {
    Default,   // Background accent, text dark
    Success,
    Warning,
    Error,
    Neutral    // Background surface, text secondary
}
```

**Especificaciones:**
| Propiedad | Valor |
|-----------|-------|
| Height | `20.dp` |
| Padding H | `8.dp` |
| Corner Radius | `full` |
| Text Style | `labelSmall` |
| Min Width | `20.dp` (para números de 1 dígito) |

#### B3.2: UmbralTag

```kotlin
@Composable
fun UmbralTag(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    onRemove: (() -> Unit)? = null  // Muestra X si no es null
)
```

**Especificaciones:**
- Similar a Chip pero más pequeño
- Height: `28.dp`
- Background: `accentPrimary` 10% opacity
- Text: `accentPrimary`
- Border: None

#### B3.3: UmbralAvatar

```kotlin
@Composable
fun UmbralAvatar(
    modifier: Modifier = Modifier,
    image: ImageBitmap? = null,
    initials: String? = null,
    size: AvatarSize = AvatarSize.Medium,
    badge: AvatarBadge? = null
)

enum class AvatarSize {
    Small,   // 32.dp
    Medium,  // 40.dp
    Large,   // 56.dp
    XLarge   // 80.dp
}

enum class AvatarBadge {
    Online,   // Dot verde
    Offline,  // Dot gris
    Active,   // Dot accent pulsing
    None
}
```

**Especificaciones:**
| Propiedad | Valor |
|-----------|-------|
| Shape | Circle |
| Background (no image) | `accentPrimary` 15% opacity |
| Initials Color | `accentPrimary` |
| Border | 2px `backgroundBase` (para stacking) |
| Badge Position | Bottom-right |
| Badge Size | 25% del avatar size |

#### B3.4: UmbralListItem

```kotlin
@Composable
fun UmbralListItem(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
)
```

**Especificaciones:**
| Propiedad | Valor |
|-----------|-------|
| Min Height | `56.dp` (single line), `72.dp` (two line) |
| Padding H | `16.dp` |
| Leading Size | `40.dp` área |
| Title | `bodyLarge`, `textPrimary` |
| Subtitle | `bodyMedium`, `textSecondary` |
| Divider | Optional, variant Inset |

---

### B4: Skeleton Loaders

#### B4.1: UmbralSkeleton

```kotlin
@Composable
fun UmbralSkeleton(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp)
)
```

**Especificaciones:**
| Propiedad | Dark Theme | Light Theme |
|-----------|------------|-------------|
| Background | `#252525` | `#E8E8E8` |
| Shimmer Highlight | `#303030` | `#F5F5F5` |
| Animation | Shimmer left-to-right, 1200ms, infinite |

#### B4.2: Skeleton Presets

```kotlin
@Composable
fun SkeletonCard(modifier: Modifier = Modifier)
// Card completa con título, subtítulo, imagen placeholder

@Composable
fun SkeletonListItem(modifier: Modifier = Modifier)
// List item con avatar, título, subtítulo

@Composable
fun SkeletonText(
    lines: Int = 3,
    modifier: Modifier = Modifier
)
// Líneas de texto con anchos variados (100%, 90%, 60%)

@Composable
fun SkeletonProfileCard(modifier: Modifier = Modifier)
// Específico para ProfileCard de Umbral
```

---

## SECCIÓN C: CATÁLOGO DE COMPONENTES

---

### C1: ComponentCatalogScreen

```kotlin
@Composable
fun ComponentCatalogScreen(
    onBack: () -> Unit
)
```

**Acceso:**
- Solo visible en debug builds
- Accesible desde Settings > "Component Catalog" (oculto con long press en version number)

**Estructura:**
```
Component Catalog
├── Theme Toggle (Dark/Light)
├── Buttons
│   ├── Primary (all sizes)
│   ├── Text Button
│   ├── Icon Button (all variants)
│   └── States demo
├── Cards
│   ├── Default
│   ├── Interactive
│   └── Outlined
├── Inputs
│   ├── TextField (states)
│   ├── SearchField
│   ├── Checkbox
│   └── Switch
├── Navigation
│   ├── Bottom Bar
│   ├── Top Bar
│   └── Tab Row
├── Feedback
│   ├── Snackbars (all variants)
│   ├── Toast
│   └── Progress Indicators
├── Data Display
│   ├── Badges
│   ├── Tags
│   ├── Avatar
│   └── List Items
├── Empty States
│   └── All illustrations
└── Skeletons
    └── All presets
```

**Features del Catálogo:**
- Toggle instantáneo dark/light
- Cada componente muestra todos sus estados
- Copy-to-clipboard del código de uso
- Spacing visible con overlay toggle

---

## SECCIÓN D: ESPECIFICACIONES DE ANIMACIÓN

---

### D1: Principios de Animación

1. **Responsive:** Feedback inmediato (< 100ms para inicio)
2. **Natural:** Movimientos que siguen física real (springs)
3. **Purposeful:** Cada animación tiene una razón
4. **Consistent:** Mismos timings en toda la app

### D2: Tokens de Animación

```kotlin
object UmbralMotion {
    // Durations
    val instant = 0.ms
    val quick = 100.ms      // Micro-interactions
    val fast = 150.ms       // Hover, color changes
    val normal = 250.ms     // Standard transitions
    val slow = 400.ms       // Page transitions
    val slower = 600.ms     // Complex animations

    // Springs
    val springSnappy = spring(
        dampingRatio = 0.7f,
        stiffness = 500f
    )
    val springBouncy = spring(
        dampingRatio = 0.5f,
        stiffness = 400f
    )
    val springGentle = spring(
        dampingRatio = 1f,
        stiffness = 200f
    )

    // Easings
    val easeOut = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)
    val easeIn = CubicBezierEasing(0.4f, 0.0f, 1.0f, 1.0f)
    val easeInOut = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
    val emphasis = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
}
```

### D3: Animaciones por Componente

| Componente | Trigger | Animación | Spec |
|------------|---------|-----------|------|
| Button | Press | Scale + Color | springSnappy, scale 0.98 |
| Button | Loading | Spinner fade | crossfade 200ms |
| Card | Press | Scale | springGentle, scale 0.99 |
| TextField | Focus | Border + Label | tween 150ms easeOut |
| TextField | Error | Shake | spring 3 oscillations |
| Switch | Toggle | Thumb + Track | springBouncy |
| Checkbox | Check | Path draw + Scale | 200ms + springBouncy |
| BottomBar | Select | Indicator slide | springSnappy |
| Snackbar | Enter | Slide up + Fade | 250ms easeOut |
| Snackbar | Exit | Slide down + Fade | 200ms easeIn |
| Skeleton | Continuous | Shimmer | 1200ms linear infinite |
| Badge | Appear | Scale + Fade | springBouncy |

### D4: Transiciones de Pantalla

```kotlin
// Navegación hacia adelante
val enterTransition = fadeIn(tween(300)) + slideInHorizontally(
    initialOffsetX = { it / 4 },
    animationSpec = tween(300, easing = UmbralMotion.easeOut)
)

// Navegación hacia atrás
val exitTransition = fadeOut(tween(200)) + slideOutHorizontally(
    targetOffsetX = { it / 4 },
    animationSpec = tween(200, easing = UmbralMotion.easeIn)
)
```

---

## Non-Functional Requirements

### NFR-1: Performance
- Animaciones a 60fps mínimo en dispositivos mid-range
- Skeleton shimmer no debe causar battery drain
- Lazy loading para ComponentCatalog

### NFR-2: Accessibility
- Todos los componentes soportan TalkBack
- Respetar "Reduce Motion" del sistema
- Focus indicators visibles (2px accent border)
- Touch targets mínimo 48x48.dp

### NFR-3: Maintainability
- Un archivo por componente
- Previews para cada estado
- Documentación KDoc completa

---

## Success Criteria

### Cuantitativos
- [ ] 100% componentes con preview funcional
- [ ] 0 animaciones que causen frame drops
- [ ] Catálogo muestra 100% de componentes
- [ ] Touch targets >= 48dp en todos los interactivos

### Cualitativos
- [ ] Look premium y cohesivo validado
- [ ] Animaciones se sienten naturales
- [ ] Desarrolladores pueden usar componentes sin documentación externa

---

## Technical Implementation

### Estructura de Archivos

```
presentation/ui/components/
├── buttons/
│   ├── UmbralButton.kt
│   ├── UmbralTextButton.kt
│   └── UmbralIconButton.kt
├── cards/
│   ├── UmbralCard.kt
│   ├── UmbralSurface.kt
│   └── UmbralDivider.kt
├── inputs/
│   ├── UmbralTextField.kt
│   ├── UmbralSearchField.kt
│   ├── UmbralCheckbox.kt
│   └── UmbralSwitch.kt
├── navigation/
│   ├── UmbralBottomBar.kt
│   ├── UmbralTopBar.kt
│   └── UmbralTabRow.kt
├── feedback/
│   ├── UmbralSnackbar.kt
│   ├── UmbralToast.kt
│   └── UmbralProgressIndicator.kt
├── display/
│   ├── UmbralBadge.kt
│   ├── UmbralTag.kt
│   ├── UmbralAvatar.kt
│   └── UmbralListItem.kt
├── empty/
│   ├── UmbralEmptyState.kt
│   └── EmptyStateIllustrations.kt
├── skeleton/
│   ├── UmbralSkeleton.kt
│   └── SkeletonPresets.kt
└── catalog/
    └── ComponentCatalogScreen.kt

presentation/ui/theme/
├── Motion.kt  // Nuevos tokens de animación
└── [archivos existentes]
```

### Orden de Implementación

**Sprint 1: Foundation (3-4 días)**
1. Motion.kt (tokens de animación)
2. UmbralButton (rediseño)
3. UmbralTextButton (nuevo)
4. UmbralIconButton (rediseño)
5. UmbralCard (rediseño)

**Sprint 2: Inputs & Navigation (3-4 días)**
1. UmbralTextField (rediseño)
2. UmbralSearchField (nuevo)
3. UmbralCheckbox (nuevo)
4. UmbralSwitch (rediseño de Toggle)
5. UmbralBottomBar (rediseño)
6. UmbralTopBar (rediseño)

**Sprint 3: New Components (3-4 días)**
1. UmbralSnackbar (nuevo)
2. UmbralToast (nuevo)
3. UmbralProgressIndicator (mejora)
4. UmbralBadge (nuevo)
5. UmbralTag (nuevo)
6. UmbralAvatar (nuevo)

**Sprint 4: Polish (2-3 días)**
1. UmbralEmptyState + Ilustraciones
2. UmbralSkeleton + Presets
3. ComponentCatalogScreen
4. Testing y ajustes

**Total estimado:** 12-15 días de desarrollo

---

## Constraints & Assumptions

### Constraints
- Requiere Fase 1 (Tokens) completada primero
- No breaking changes en APIs públicas de componentes existentes
- Mantener compatibilidad con screens existentes

### Assumptions
- El sistema de animación actual (Animation.kt) es base válida
- Jetpack Compose Animation APIs son suficientes (no Lottie requerido para la mayoría)
- Performance de shimmer es aceptable en dispositivos target

---

## Out of Scope

- Componentes específicos de features (ProfileCard se mantiene separado)
- Integración con Figma
- Temas adicionales (seasonal, etc.)
- Animaciones Lottie complejas
- Testing automatizado de componentes visuales

---

## Dependencies

### Internas
- `design-system-v2` (Fase 1: Tokens) - **BLOQUEANTE**

### Externas
- Material 3 Compose - ya incluida
- Compose Animation - ya incluida

---

## Appendix

### A. Referencia Visual (ASCII)

**Bottom Bar Minimalista:**
```
┌─────────────────────────────────────────┐
│─────────────────────────────────────────│ ← 1px border
│                                         │
│    🏠        📊        ⚙️        │
│    ───                               │ ← indicator line
│                                         │
└─────────────────────────────────────────┘
```

**Card Flat con Borde:**
```
┌─────────────────────────────────────────┐
│ ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░ │ ← 1px border
│ ░                                     ░ │
│ ░   Title                             ░ │
│ ░   Subtitle text here                ░ │
│ ░                                     ░ │
│ ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░ │
└─────────────────────────────────────────┘
```

**Empty State:**
```
         ┌─────────────┐
         │             │
         │    ┌───┐    │
         │    │ ? │    │
         │    └───┘    │
         │             │
         └─────────────┘

    No hay perfiles aún

  Crea tu primer perfil para
  empezar a bloquear apps

      [ + Crear perfil ]
```

### B. Migración de Componentes Existentes

| Componente Actual | Nuevo Nombre | Cambios |
|-------------------|--------------|---------|
| UmbralButton | UmbralButton | Rediseño visual, misma API |
| UmbralToggle | UmbralSwitch | Rename + rediseño |
| UmbralCard | UmbralCard | Flat style, misma API |
| UmbralChip | UmbralTag | Simplificar |
| ProfileCard | (sin cambios) | Usar nuevos tokens |

---

## Next Steps

1. **Aprobar PRD:** Revisar especificaciones
2. **Completar Fase 1:** Ejecutar `/oden:prd-parse design-system-v2`
3. **Iniciar Fase 2:** Ejecutar `/oden:prd-parse design-system-v2-components`
4. **Desarrollo:** Seguir orden de sprints

---

**Autor:** Claude Code
**Revisado por:** Pendiente
**Aprobado:** Pendiente
