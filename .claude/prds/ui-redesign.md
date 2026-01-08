---
name: ui-redesign
description: Rediseño completo de la interfaz de usuario de Umbral con estilo minimalista y limpio
status: backlog
created: 2026-01-05T03:13:47Z
updated: 2026-01-05T03:13:47Z
---

# PRD: Rediseño de UI - Umbral

## 1. Problema

### Situación Actual
La app Umbral es funcional pero carece de pulido visual. La interfaz actual:
- Es básica y genérica (colores Material Design por defecto)
- No transmite la identidad de marca de Umbral
- Carece de animaciones y micro-interacciones
- Las pantallas no tienen consistencia visual entre sí
- El onboarding es funcional pero no memorable
- La pantalla de bloqueo es efectiva pero intimidante
- No hay visualización atractiva de estadísticas

### Impacto
- Primera impresión pobre → usuarios no confían en la app
- Sin diferenciación visual → se ve como "otra app más"
- Experiencia plana → usuarios no disfrutan usar la app
- Sin gamificación visual → menor engagement con streaks/stats

## 2. Visión

### Concepto
Transformar Umbral en una app **minimalista y limpia** que transmita:
- **Calma**: El usuario siente paz al usar la app, no ansiedad
- **Control**: Sensación de empoderamiento, no restricción
- **Claridad**: Todo es obvio, sin confusión
- **Elegancia**: Atención al detalle, se siente premium

### Principios de Diseño
1. **Menos es más** - Eliminar todo lo innecesario
2. **Espacio respira** - Generous whitespace
3. **Tipografía clara** - Jerarquía visual con fonts
4. **Colores con propósito** - Paleta reducida, acentos intencionales
5. **Movimiento con significado** - Animaciones que comunican, no decoran

## 3. User Stories

### Como usuario nuevo...
- Quiero un onboarding que me inspire confianza
- Quiero entender inmediatamente cómo funciona la app
- Quiero sentir que esta app es diferente a otras que he probado

### Como usuario recurrente...
- Quiero ver mi progreso de forma visualmente atractiva
- Quiero que la pantalla principal sea clara y rápida de usar
- Quiero animaciones sutiles que hagan la experiencia agradable

### Como usuario bloqueado...
- Quiero una pantalla de bloqueo que me motive, no me frustre
- Quiero ver claramente mis opciones (ir a home, escanear NFC)
- Quiero sentir que la app me está ayudando, no castigando

## 4. Pantallas a Rediseñar

### 4.1 Onboarding Flow

#### Welcome Screen
- Ilustración hero minimalista (líneas simples, colores suaves)
- Título grande y claro
- Subtítulo que explica el valor
- CTA prominente pero no agresivo
- Animación sutil de entrada

#### How It Works Screen
- 3 pasos con iconografía custom
- Animaciones secuenciales al entrar
- Transiciones suaves entre pasos
- Botón "Continuar" con micro-interacción

#### Permissions Screen
- Cards limpias para cada permiso
- Estados claros (pendiente/otorgado)
- Explicación breve de por qué se necesita
- Animación de check al otorgar

#### Select Apps Screen (ya mejorada, pulir más)
- Mantener categorización actual
- Mejorar visual de chips de categoría
- Añadir animaciones de selección
- Feedback visual más claro al seleccionar

#### How to Unblock Screen
- Ilustraciones de NFC tag y QR code
- Animación mostrando el flujo
- Mensaje motivacional

#### Success Screen
- Celebración visual (confetti sutil o animación)
- Resumen de configuración
- Transición suave al home

### 4.2 Home Screen

#### Estado Actual
- Card de estado de bloqueo
- Contador de streak
- Botones de NFC/QR

#### Mejoras Propuestas
- **Card principal rediseñada**:
  - Gradiente sutil o color sólido elegante
  - Icono animado (candado que se abre/cierra)
  - Tipografía más grande y clara
  - Sombra suave para profundidad

- **Streak display**:
  - Número grande y prominente
  - Icono de fuego con animación sutil
  - Mini calendario de últimos 7 días
  - Celebración visual en milestones (7, 30, 100 días)

- **Quick actions**:
  - Cards más pequeñas y elegantes
  - Iconos con estilo consistente
  - Hover/press states con animación

- **Stats preview**:
  - Mini gráfica de últimos 7 días
  - "Ver más" que lleva a stats completas

### 4.3 Blocking Screen

#### Concepto: De "castigo" a "apoyo"
En lugar de una pantalla roja intimidante, crear una experiencia que:
- Reconozca el intento sin juzgar
- Motive a continuar el streak
- Ofrezca alternativas claras

#### Diseño Propuesto
- **Background**: Gradiente suave (no rojo agresivo)
- **Icono**: Shield protector en lugar de bloqueo
- **Mensaje principal**: "Estás protegiendo tu enfoque"
- **Mensaje secundario**: Muestra streak actual ("Llevas 5 días concentrado")
- **Acciones**:
  - "Volver al inicio" (botón principal)
  - "Desbloquear" (solo si no es modo estricto, menos prominente)
- **Animación**: Breathing animation suave para calma

### 4.4 Stats Screen (Nueva/Mejorada)

#### Métricas a mostrar
- **Streak actual** con gráfica de progreso
- **Apps más bloqueadas** con iconos y contadores
- **Tiempo ahorrado** (estimado)
- **Intentos bloqueados** por día/semana
- **Mejor streak histórico**

#### Visualización
- Gráficas minimalistas (líneas, no barras pesadas)
- Colores consistentes con la app
- Animaciones de entrada para cada sección
- Comparativas (esta semana vs anterior)

### 4.5 Profiles Screen

#### Mejoras
- Cards de perfil más visuales
- Icono y color prominentes
- Estado activo/inactivo claro
- Contador de apps bloqueadas
- Animación al cambiar de perfil

### 4.6 Settings Screen

#### Organización
- Secciones claras con headers
- Toggles con animación
- Links externos con iconos
- About section con branding

## 5. Sistema de Diseño

### 5.1 Colores

```
Primary:     #6366F1 (Indigo suave - confianza, calma)
Secondary:   #8B5CF6 (Violeta - premium, único)
Success:     #10B981 (Verde menta - logro, positivo)
Warning:     #F59E0B (Ámbar - atención suave)
Error:       #EF4444 (Rojo - solo para errores críticos)

Background:  #FAFAFA (Casi blanco)
Surface:     #FFFFFF (Blanco puro)
Text:        #1F2937 (Gris oscuro, no negro)
TextSecondary: #6B7280 (Gris medio)

Dark Mode:
Background:  #0F172A (Azul muy oscuro)
Surface:     #1E293B (Azul oscuro)
Text:        #F8FAFC (Casi blanco)
```

### 5.2 Tipografía

```
Font Family: Inter (o system default para performance)

Display:     32sp, SemiBold (títulos principales)
Headline:    24sp, SemiBold (secciones)
Title:       20sp, Medium (cards)
Body:        16sp, Regular (texto general)
Label:       14sp, Medium (botones, chips)
Caption:     12sp, Regular (texto secundario)
```

### 5.3 Espaciado

```
xs:  4dp
sm:  8dp
md:  16dp
lg:  24dp
xl:  32dp
2xl: 48dp

Screen padding: 20dp horizontal
Card padding:   16dp
```

### 5.4 Bordes y Sombras

```
Border radius:
- Small:  8dp (chips, buttons)
- Medium: 12dp (cards pequeñas)
- Large:  16dp (cards principales)
- XL:     24dp (modals, bottom sheets)

Shadows:
- Subtle: 0 1dp 2dp rgba(0,0,0,0.05)
- Medium: 0 4dp 6dp rgba(0,0,0,0.07)
- Large:  0 10dp 15dp rgba(0,0,0,0.1)
```

### 5.5 Animaciones

```
Duration:
- Quick:   150ms (hover, press)
- Normal:  300ms (transitions)
- Slow:    500ms (page transitions)

Easing:
- Standard: cubic-bezier(0.4, 0.0, 0.2, 1)
- Decelerate: cubic-bezier(0.0, 0.0, 0.2, 1)
- Accelerate: cubic-bezier(0.4, 0.0, 1, 1)
```

## 6. Ilustraciones & Iconografía

### Estilo de Ilustraciones
- **Líneas simples** - No ilustraciones complejas
- **Colores de la paleta** - Consistencia con la app
- **Personajes abstractos** - Formas geométricas, no caras
- **Tamaño moderado** - No ocupar toda la pantalla

### Iconografía
- **Estilo**: Outlined con 2dp stroke
- **Tamaño base**: 24dp
- **Consistencia**: Mismo estilo en toda la app
- **Fuente**: Material Symbols o Lucide Icons

### Ilustraciones Necesarias
1. Welcome - Concepto de "umbral" (puerta/transición)
2. How it works - 3 iconos para los pasos
3. NFC tag - Tag siendo escaneado
4. QR code - Teléfono escaneando
5. Success - Celebración minimalista
6. Blocking - Shield protector
7. Empty states - Para listas vacías

## 7. Micro-interacciones

### Botones
- Press: Scale down 0.95 + color darken
- Release: Spring back animation
- Loading: Skeleton o spinner inline

### Cards
- Press: Subtle elevation change
- Selección: Border highlight + scale 1.02

### Toggles
- Slide animation con bounce sutil
- Color transition suave
- Haptic feedback (si disponible)

### Checkboxes
- Check mark dibujándose animado
- Background color fill
- Bounce sutil

### Pull to Refresh
- Custom animation (no default Android)
- Logo de Umbral girando

### Page Transitions
- Shared element transitions donde aplique
- Fade + slide para navegación normal

## 8. Criterios de Éxito

### Métricas de UX
- [ ] Onboarding completion rate > 90%
- [ ] Time to first block < 3 minutos
- [ ] User retention D7 > 40%
- [ ] App store rating > 4.5 estrellas

### Métricas Técnicas
- [ ] Animaciones a 60fps constante
- [ ] Tiempo de carga < 2 segundos
- [ ] Tamaño de APK < 15MB
- [ ] Crash rate < 0.1%

### Métricas Cualitativas
- [ ] Feedback positivo en reviews sobre diseño
- [ ] Usuarios mencionan "se siente premium"
- [ ] Screenshots dignos de compartir

## 9. Fases de Implementación

### Fase 1: Foundation (Semana 1-2)
- Implementar sistema de diseño (colores, tipografía, espaciado)
- Crear componentes base (buttons, cards, inputs)
- Configurar tema light/dark

### Fase 2: Onboarding (Semana 3-4)
- Rediseñar todas las pantallas de onboarding
- Crear ilustraciones
- Implementar animaciones

### Fase 3: Core Screens (Semana 5-6)
- Home screen rediseño
- Blocking screen rediseño
- Stats screen nueva

### Fase 4: Polish (Semana 7-8)
- Profiles y Settings
- Micro-interacciones
- Animaciones de transición
- Testing y ajustes

## 10. Referencias Visuales

### Apps con buen diseño minimalista
- Headspace (calma, espacios)
- Linear (clean, profesional)
- Notion (tipografía, jerarquía)
- Stripe (elegante, moderno)
- Opal (app similar, buen diseño)

### Tendencias 2024-2025
- Bento grids
- Glassmorphism sutil
- Gradientes suaves
- Tipografía bold
- Ilustraciones geométricas

## 11. Recursos Necesarios

### Assets a Crear
- [ ] Logo refinado (si necesario)
- [ ] Set de ilustraciones (5-7)
- [ ] Set de iconos custom (opcional)
- [ ] Screenshots para tiendas

### Dependencias Técnicas
- Lottie para animaciones complejas
- Coil para imágenes optimizadas
- Compose Animation APIs

---

## Próximos Pasos

1. Aprobar este PRD
2. Crear mockups/wireframes de pantallas clave
3. `/oden:sync epic ui-redesign` para crear tareas
4. Comenzar implementación por fases

---

**Creado:** 2026-01-05
**Autor:** Claude + Usuario
**Estado:** Pendiente de aprobación
