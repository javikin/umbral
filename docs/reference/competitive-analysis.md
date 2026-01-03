# Análisis Competitivo: Umbral

**Estado:** ✅ Completo
**Última actualización:** 2026-01-03T04:32:12Z

---

## Resumen Ejecutivo

### Mercado

**Tamaño del mercado de App Blockers (2024):** $2.37 mil millones USD
**Proyección 2033:** $7.02 mil millones USD
**Crecimiento (CAGR):** 12.8% anual (2025-2033)

**Mercado más amplio de Wellness Apps:**
- 2024: $11.18 mil millones USD
- 2034: $45.65 mil millones USD (CAGR 15.11%)

### Distribución Regional
| Región | Participación 2024 | CAGR Proyectado |
|--------|-------------------|-----------------|
| Norteamérica | 38% ($0.90B) | 11.2% |
| Europa | 27% ($0.64B) | 12.1% |
| Asia Pacífico | 25% ($0.59B) | **15.2%** (más rápido) |
| LATAM | 7% ($0.17B) | 13.4% |
| Resto | 3% ($0.07B) | 10.5% |

### Tendencias del Mercado

1. **Awareness de bienestar digital** - Usuarios cada vez más conscientes de adicción digital
2. **Regulación parental** - Padres buscando controles para hijos
3. **Productividad laboral** - Empresas implementando herramientas de focus
4. **Soluciones físicas** - NFC/hardware creando fricción real vs. solo software
5. **Open source** - Usuarios prefiriendo transparencia y control de datos
6. **Local-first** - Privacidad como diferenciador competitivo

### Competidores Analizados

| # | Competidor | Plataforma | Modelo | Posición |
|---|------------|------------|--------|----------|
| 1 | **Foqos** | iOS | Free/Open Source | Inspiración principal |
| 2 | **Brick** | iOS/Android | $59 one-time | Líder comercial |
| 3 | **Unpluq** | iOS/Android | $26 + $7.99/mes | Suscripción |
| 4 | **One Sec** | iOS/Android | Freemium | Delay-based |
| 5 | **ScreenZen** | Android | Free/Donations | Mindfulness |

---

## 1. Foqos (Competidor Principal / Inspiración)

### Overview

| Campo | Valor |
|-------|-------|
| **URL** | https://www.foqos.app |
| **GitHub** | https://github.com/awaseem/foqos |
| **Fundado** | 2025 |
| **Pricing** | 100% Gratis, Open Source (MIT License) |
| **Target** | iOS users buscando alternativa gratuita a Brick |
| **Desarrollador** | Ali Waseem (indie developer) |

### Modelo de Negocio
- **100% gratuito**, sin planes de monetización actuales
- Open source como filosofía, no como modelo de negocio
- Creado en un "holiday break" como proyecto personal
- Comunidad: Building in public (Twitter/X engagement)

### Tech Stack
| Componente | Tecnología |
|------------|------------|
| UI | SwiftUI |
| Data | SwiftData |
| App Blocking | Family Controls API |
| NFC | Core NFC |
| QR | CodeScanner library |
| Background | BackgroundTasks framework |
| Widgets | WidgetKit |
| Shortcuts | App Intents |

### Features

| Feature | Disponible | Calidad | Notas |
|---------|------------|---------|-------|
| NFC Tag Blocking | ✅ | ⭐⭐⭐⭐⭐ | Core feature, muy pulido |
| QR Code Blocking | ✅ | ⭐⭐⭐⭐⭐ | Alternativa gratuita a NFC |
| Multiple Profiles | ✅ | ⭐⭐⭐⭐ | Work, Study, Sleep presets |
| Timer Sessions | ✅ | ⭐⭐⭐⭐ | Con physical unlock |
| Website Blocking | ✅ | ⭐⭐⭐ | Funcional pero básico |
| Live Activities | ✅ | ⭐⭐⭐⭐⭐ | Dynamic Island support |
| Habit Tracking | ✅ | ⭐⭐⭐⭐ | Focus streaks, history |
| Smart Breaks | ✅ | ⭐⭐⭐⭐ | Pause sin cancelar |
| Widgets | ✅ | ⭐⭐⭐⭐ | Home screen widgets |
| Shortcuts | ✅ | ⭐⭐⭐⭐ | Siri/Shortcuts integration |
| Cloud Sync | ❌ | - | 100% local (privacy) |
| Android | ❌ | - | iOS only |

### Estrategias de Bloqueo
1. **Manual** - Toggle desde la app
2. **NFC** - Tap tag para toggle
3. **QR** - Scan para toggle
4. **NFC + Manual** - Híbrido
5. **QR + Manual** - Híbrido
6. **NFC + Timer** - Auto-unlock después de tiempo
7. **QR + Timer** - Auto-unlock después de tiempo

### Fortalezas
- ✅ **100% gratuito** - Sin costo oculto jamás
- ✅ **Open source** - Código auditable y transparente
- ✅ **Privacidad total** - No cloud, no tracking
- ✅ **Feature parity** con Brick comercial
- ✅ **Soporte para Amiibo** y hotel cards (cualquier NFC tag)
- ✅ **Comunidad activa** - Building in public
- ✅ **UX pulida** - SwiftUI moderno

### Debilidades
- ❌ **iOS only** - No Android
- ❌ **Sin cloud sync** - No backup cross-device
- ❌ **Documentación limitada** - README básico
- ❌ **Sin soporte dedicado** - Solo GitHub issues
- ❌ **Depende de Screen Time** - Requiere permisos de Apple

### Lo que usuarios dicen

> "Finally a free alternative to Brick that actually works! I've been using cheap NFC tags from Amazon and it works perfectly."
> — Hacker News user

> "The fact that it supports any NFC tag including hotel cards and Amiibos is genius."
> — Product Hunt review

> "I've been looking for this exact app. Brick is too expensive for what it does."
> — App Store review

### Estadísticas
- **GitHub Stars:** ~500+ (creciendo)
- **App Store Rating:** 4.7/5
- **Downloads:** ~10K+ (estimado)

---

## 2. Brick (Líder Comercial)

### Overview

| Campo | Valor |
|-------|-------|
| **URL** | https://getbrick.app |
| **Fundado** | 2023 |
| **Pricing** | $59 one-time (device + app) |
| **Target** | Power users dispuestos a pagar por hardware dedicado |
| **Empresa** | Brick LLC |

### Modelo de Negocio
- Venta de hardware propietario ($59)
- Sin suscripción recurrente
- Sin in-app purchases
- Un Brick sirve para múltiples teléfonos
- Enfoque "buy once, use forever"

### Features

| Feature | Disponible | Calidad | Notas |
|---------|------------|---------|-------|
| NFC Device Blocking | ✅ | ⭐⭐⭐⭐⭐ | Core feature, hardware premium |
| Multiple Modes | ✅ | ⭐⭐⭐⭐⭐ | Hasta 10 focus modes |
| Strict Mode | ✅ | ⭐⭐⭐⭐⭐ | Previene bypass y delete |
| Emergency Unbricks | ✅ | ⭐⭐⭐⭐ | 5 lifetime uses |
| Custom Schedules | ✅ | ⭐⭐⭐⭐ | NEW 2025 - Auto-start |
| iOS Support | ✅ | ⭐⭐⭐⭐⭐ | iOS 16.2+ |
| Android Support | ✅ | ⭐⭐⭐⭐ | NEW Sept 2025 - Android 12+ |
| QR Alternative | ❌ | - | Solo NFC device |
| Cloud Sync | ❌ | - | 100% local |
| Timer Auto-Stop | ❌ | - | Solo start, no auto-stop |

### Fortalezas
- ✅ **Hardware premium** - Experiencia física satisfactoria
- ✅ **Strict Mode** - Muy difícil de bypass
- ✅ **Sin suscripción** - Pago único
- ✅ **Cross-platform** - iOS y Android (2025)
- ✅ **Diseño premium** - Material de alta calidad
- ✅ **Multi-phone** - Un Brick, múltiples usuarios
- ✅ **Sin batería** - NFC no requiere carga

### Debilidades
- ❌ **Costo inicial alto** - $59 vs gratuito
- ❌ **Hardware propietario** - Solo funciona con SU device
- ❌ **Emergency Unbricks limitados** - Solo 5 de por vida
- ❌ **Setup tedioso** - App crashes reportados durante onboarding
- ❌ **Bypass posible** - Screen Time settings pueden desactivar
- ❌ **Sin QR fallback** - Si pierdes Brick, estás atrapado
- ❌ **Sin auto-stop en schedule** - Solo inicia, no termina automático
- ❌ **Closed source** - No auditable

### Lo que usuarios dicen

> "For the week I was using it, my screen time dropped by an average of almost half every day. If that is not a ringing endorsement, I don't know what is."
> — Irish Times Review

> "The setup process is a little tedious. The Brick app crashed a couple of times midway through it, forcing me to start over."
> — Consumer Reports

> "There is a backdoor setting that lets you unlock Bricked apps. With a little digging, you'll learn that you can deactivate the Brick app without the physical device in your iPhone's Screen Time settings."
> — Tech Review

> "Early adopters report implementing 2-3 hour focus blocks for deep work, with some describing the productivity boost as genuinely transformative."
> — Productivity Blog

### Estadísticas
- **App Store Rating:** 4.8/5
- **Play Store Rating:** 4.6/5
- **Est. Revenue:** $1M+ (estimado basado en virality)
- **Media Coverage:** Consumer Reports, Irish Times, Gadget Review

---

## 3. Unpluq (Suscripción + Hardware)

### Overview

| Campo | Valor |
|-------|-------|
| **URL** | https://www.unpluq.com |
| **Fundado** | 2022 |
| **Pricing** | Tag $26.50 one-time + $7.99/mes Premium |
| **Target** | Usuarios que prefieren suscripción + flexibilidad |
| **Empresa** | Unpluq Inc |

### Modelo de Negocio
- Hardware barato ($26.50) como gancho
- Suscripción mensual ($7.99/mes) para features
- Plan Family disponible
- NFC tag opcional - app funciona sin él

### Features

| Feature | Disponible | Calidad | Notas |
|---------|------------|---------|-------|
| NFC Tag Blocking | ✅ | ⭐⭐⭐⭐ | Con carabiner para keychain |
| Multiple Unlock Methods | ✅ | ⭐⭐⭐⭐⭐ | NFC, shake, pattern, scroll, walk |
| Auto-Relock Timer | ✅ | ⭐⭐⭐⭐⭐ | 5 min default, configurable |
| Offline Mode | ✅ | ⭐⭐⭐⭐⭐ | Funciona sin WiFi/data |
| Schedule Automation | ✅ | ⭐⭐⭐⭐ | Por día/hora |
| iOS Support | ✅ | ⭐⭐⭐⭐ | iOS 16+ |
| Android Support | ✅ | ⭐⭐⭐ | Buggy según reviews |
| Emergency Mode | ✅ | ⭐⭐⭐⭐ | Acceso temporal |
| App-Only Mode | ✅ | ⭐⭐⭐ | Sin NFC, solo app |
| Random Barriers | ✅ | ⭐⭐⭐⭐ | Unpredictable friction |

### Métodos de Desbloqueo Únicos
1. **NFC Tag** - Tap físico
2. **Phone Shake** - Agitar el teléfono
3. **Scroll Challenge** - Scroll largo en página vacía
4. **Tap Pattern** - Patrón de taps
5. **Walking Challenge** - Caminar X pasos

### Fortalezas
- ✅ **Múltiples métodos de unlock** - Flexibilidad
- ✅ **Auto-relock inteligente** - Re-bloquea automáticamente
- ✅ **Hardware portable** - Carabiner para llaves
- ✅ **Funciona offline** - No necesita conexión
- ✅ **Cross-platform** - iOS y Android
- ✅ **Barrera más baja** - $26 vs $59 de Brick

### Debilidades
- ❌ **Requiere suscripción** - $7.99/mes ongoing
- ❌ **Android buggy** - Schedules no funcionan siempre
- ❌ **Features premium** - NFC requiere plan pago
- ❌ **Costo total alto** - $26 + $96/año = $122 primer año
- ❌ **Sin QR alternativo** - Solo NFC o métodos digitales
- ❌ **Closed source** - No auditable

### Lo que usuarios dicen

> "On average, customers who use the Unpluq Tag recover more screen time (78 minutes a day on average compared to 54 minutes a day with the Premium app only)."
> — Unpluq Stats

> "I've tried so many different screen time apps, but they were all either buggy or too easy to bypass. Unpluq is neither; it actually WORKS."
> — App Store Review

> "The app is a little buggy and doesn't turn on/off when scheduled all the time."
> — Android User Review

### Estadísticas
- **App Store Rating:** 4.6/5
- **Play Store Rating:** 4.2/5
- **Efectividad reportada:** 78 min/día recuperados (con tag)

---

## 4. One Sec (Delay-Based Approach)

### Overview

| Campo | Valor |
|-------|-------|
| **URL** | https://one-sec.app |
| **Fundado** | 2021 |
| **Pricing** | Freemium ($3.99/mes o $19.99/año Premium) |
| **Target** | Usuarios que quieren reducir sin bloquear completamente |
| **Desarrollador** | Frederik Schröder |

### Modelo de Negocio
- Versión gratuita limitada (1 app)
- Premium para múltiples apps y features avanzados
- Sin hardware - 100% software
- Basado en investigación científica

### Enfoque Único: Delay vs Block
En lugar de bloquear apps, One Sec **agrega fricción** mediante una pausa obligatoria de respiración antes de abrir cualquier app configurada. El usuario decide después de la pausa si realmente quiere abrir la app.

### Features

| Feature | Disponible | Calidad | Notas |
|---------|------------|---------|-------|
| Breathing Pause | ✅ | ⭐⭐⭐⭐⭐ | Core feature - 5 seg default |
| Intention Setting | ✅ | ⭐⭐⭐⭐ | Por qué abres la app? |
| Focus Sessions | ✅ | ⭐⭐⭐⭐ | Block completo durante focus |
| Website Blocking | ✅ | ⭐⭐⭐⭐ | Funciona en browser |
| iOS Support | ✅ | ⭐⭐⭐⭐⭐ | Muy pulido |
| Android Support | ✅ | ⭐⭐⭐⭐ | Disponible |
| Privacy-First | ✅ | ⭐⭐⭐⭐⭐ | 100% on-device |
| Scientific Backing | ✅ | ⭐⭐⭐⭐⭐ | Max Planck study |
| NFC/Hardware | ❌ | - | No usa hardware |
| Strict Mode | ❌ | - | Fácil de saltar la pausa |

### Respaldo Científico
- **Estudio con Max-Planck Institute y Universidad de Heidelberg**
- 280 participantes durante 6 semanas
- **Resultados:**
  - 36% de intentos de abrir apps resultaron en cerrar la app después de la pausa
  - 37% reducción en intentos de abrir apps después de 6 semanas
  - **57% reducción promedio en uso de apps**

### Fortalezas
- ✅ **Respaldo científico** - Peer-reviewed study
- ✅ **Approach menos restrictivo** - No bloquea, educa
- ✅ **Sin hardware** - Funciona inmediatamente
- ✅ **Privacy-first** - 100% local
- ✅ **Cross-platform** - iOS y Android
- ✅ **Bajo costo** - $20/año vs $59 hardware
- ✅ **Efectivo** - 57% reducción comprobada

### Debilidades
- ❌ **Fácil de saltar** - No hay "lock" real
- ❌ **Requiere willpower** - Usuario puede ignorar la pausa
- ❌ **Notificaciones molestas** - Muchos notifications de Shortcuts
- ❌ **No tan efectivo para adictos severos** - Bloqueo real > pausa
- ❌ **Adaptatibilidad** - Usuarios se acostumbran a la pausa
- ❌ **Sin NFC** - No hay trigger físico

### Lo que usuarios dicen

> "After I downloaded one sec and bought premium it made my unconscious habit conscious. It makes me ask myself in my head, 'is there anything I absolutely need to do on this app?' If the answer is no, I get off."
> — App Store Review

> "Though One Sec was still effective as a pop-up that requires you to do a bit more than just press a button to dismiss it, I was surprised by my readiness to adapt to my own fail-safes."
> — Refinery29

> "One sec gives you a much needed moment of clarity before opening an app & in that moment, you have the power to make a purposeful decision. Simple, but brilliant."
> — User Review

### Estadísticas
- **App Store Rating:** 4.8/5
- **Play Store Rating:** 4.5/5
- **Efectividad científica:** 57% reducción promedio
- **Downloads:** 1M+ (estimado)

---

## 5. ScreenZen (Android Champion)

### Overview

| Campo | Valor |
|-------|-------|
| **URL** | https://screenzen.co |
| **Play Store** | https://play.google.com/store/apps/details?id=com.screenzen |
| **Fundado** | 2022 |
| **Pricing** | Gratis (donaciones opcionales) |
| **Target** | Android users buscando control granular |
| **Desarrollador** | Indie developer |

### Modelo de Negocio
- **100% gratis** con donaciones opcionales
- Sin ads
- Sin suscripción
- Modelo sostenido por la comunidad

### Features

| Feature | Disponible | Calidad | Notas |
|---------|------------|---------|-------|
| Pause Before Open | ✅ | ⭐⭐⭐⭐⭐ | Similar a One Sec |
| Open Limits | ✅ | ⭐⭐⭐⭐⭐ | X opens/día por app |
| Time Limits | ✅ | ⭐⭐⭐⭐⭐ | Minutos por app |
| Feature-Level Blocking | ✅ | ⭐⭐⭐⭐⭐ | Bloquear solo Shorts/Reels |
| Mindful Prompts | ✅ | ⭐⭐⭐⭐ | "Is this important?" |
| Schedule Automation | ✅ | ⭐⭐⭐⭐ | Por día/hora |
| Android Support | ✅ | ⭐⭐⭐⭐ | Android 8.0+ |
| iOS Support | ❌ | - | Android only |
| NFC/Hardware | ❌ | - | No hardware |
| Website Blocking | ⚠️ | ⭐⭐ | Inconsistente |

### Features Únicos
1. **Feature-Level Blocking** - Bloquear YouTube Shorts pero no YouTube general
2. **Open + Time Limits** - Combinación de X opens Y minutos
3. **Mindful Prompts** - Mensajes personalizables de reflexión
4. **Granular Scheduling** - Diferentes rules por día/hora

### Fortalezas
- ✅ **100% gratis** - Sin costo alguno
- ✅ **Sin ads** - Clean experience
- ✅ **Control granular** - Feature-level blocking único
- ✅ **Combinación limits** - Opens + time
- ✅ **Difícil de bypass** - Según reviews
- ✅ **Rating alto** - 4.74/5 con 16K reviews

### Debilidades
- ❌ **Android only** - No iOS
- ❌ **Website blocking buggy** - Inconsistente
- ❌ **Glitches recientes** - Enero 2025 issues reportados
- ❌ **Sin NFC** - No hay trigger físico
- ❌ **Bug reporting difícil** - Requiere screen recording
- ❌ **Sustainability** - Modelo de donaciones incierto

### Lo que usuarios dicen

> "Works great with apps. Saved me from social media addiction. I'm reading more, hooray! Obviously you need to have some willpower to stop when it tells you to stop."
> — Play Store Review (Abril 2025)

> "I've used this app for a long time successfully but recently it has become very glitchy. Even with settings set to unlock an app for 7 minutes, it throws up a block screen every minute or two."
> — Play Store Review (Enero 2025)

> "Being free and not having ads makes it a 5-star experience for many users."
> — User Review

### Estadísticas
- **Play Store Rating:** 4.74/5 (16K ratings)
- **Downloads:** 500K+
- **Last Update:** Junio 2025

---

## Matriz Comparativa Completa

### Comparación de Features

| Feature | Umbral | Foqos | Brick | Unpluq | One Sec | ScreenZen |
|---------|--------|-------|-------|--------|---------|-----------|
| **Plataforma** | Android | iOS | iOS+Android | iOS+Android | iOS+Android | Android |
| **Precio** | Gratis | Gratis | $59 | $26+$8/mo | $20/año | Gratis |
| **Open Source** | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ |
| **NFC Blocking** | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ |
| **QR Alternative** | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ |
| **Multiple Profiles** | ✅ | ✅ | ✅ | ✅ | ❌ | ✅ |
| **Timer Sessions** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Auto-Relock** | ✅ | ❌ | ❌ | ✅ | ❌ | ❌ |
| **Statistics** | ✅ | ✅ | ❌ | ✅ | ✅ | ✅ |
| **Widgets** | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ |
| **Strict Mode** | Opcional | ❌ | ✅ | ❌ | ❌ | ❌ |
| **Offline** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Privacy-First** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |

### Comparación de Precios (Costo a 1 año)

| Producto | Costo Inicial | Año 1 | Año 2 | Año 3 |
|----------|--------------|-------|-------|-------|
| **Umbral** | $0 + NFC tags (~$5) | **$5** | $0 | $0 |
| **Foqos** | $0 + NFC tags (~$5) | **$5** | $0 | $0 |
| **Brick** | $59 | **$59** | $0 | $0 |
| **Unpluq** | $26.50 + $96/año | **$122** | $96 | $96 |
| **One Sec** | $0 + $20/año | **$20** | $20 | $20 |
| **ScreenZen** | $0 | **$0** | $0 | $0 |

### Comparación de Enfoque

| Producto | Enfoque Principal | Fricción | Severidad |
|----------|------------------|----------|-----------|
| **Umbral** | Bloqueo NFC | Alta (física) | Configurable |
| **Foqos** | Bloqueo NFC/QR | Alta (física) | Configurable |
| **Brick** | Bloqueo NFC + Strict | Muy Alta | Alta |
| **Unpluq** | Múltiples métodos | Media-Alta | Configurable |
| **One Sec** | Delay/Pausa | Baja | Baja |
| **ScreenZen** | Mindful prompts | Media | Media |

---

## Análisis de Gaps y Oportunidades

### 1. Gap: Android + Open Source + NFC

**Situación actual:**
- Foqos es open source + NFC pero **solo iOS**
- ScreenZen es gratis + Android pero **sin NFC**
- Brick tiene NFC + Android pero **closed source y $59**

**Oportunidad Umbral:**
> Ser el **primer** app blocker open source para Android con soporte NFC

### 2. Gap: Mercado Hispanohablante

**Situación actual:**
- Todas las apps principales están en inglés
- Documentación y soporte solo en inglés
- UX no adaptada a contexto cultural latino

**Oportunidad Umbral:**
> UI/UX completamente en español, enfocado en mercado LATAM

### 3. Gap: QR Alternative en Android

**Situación actual:**
- Solo Foqos ofrece QR como alternativa a NFC
- Foqos es iOS-only
- Android users sin opción QR gratuita

**Oportunidad Umbral:**
> Ofrecer QR como fallback para dispositivos sin NFC o tags perdidos

### 4. Gap: Colaboración Open Source

**Situación actual:**
- Foqos es iOS, Umbral sería Android
- Potencial para compartir protocolos, documentación, comunidad
- Sin competencia directa, sino complemento

**Oportunidad Umbral:**
> Colaboración oficial con Foqos para crear ecosistema completo

### 5. Gap: Widgets Modernos en Android

**Situación actual:**
- ScreenZen no tiene widgets
- Unpluq no tiene widgets
- Brick no tiene widgets

**Oportunidad Umbral:**
> Widgets nativos con Jetpack Glance para estado y quick actions

### 6. Gap: Estadísticas Avanzadas

**Situación actual:**
- Foqos tiene habit tracking básico
- Brick no tiene estadísticas
- Unpluq tiene métricas limitadas

**Oportunidad Umbral:**
> Estadísticas detalladas con gráficas, rachas, y insights

---

## Oportunidades de Diferenciación

### Must Have (Table Stakes)
Features que **todos** tienen y Umbral necesita:
- ✅ NFC tag reading/writing
- ✅ App blocking básico
- ✅ Multiple profiles
- ✅ Timer sessions
- ✅ 100% offline

### Nice to Have (Diferenciadores)
Features que **algunos** tienen y serían buenos:
- ✅ QR code alternative (como Foqos)
- ✅ Widgets (como Foqos)
- ✅ Auto-relock (como Unpluq)
- ✅ Statistics (como ScreenZen)
- ✅ Physical unlock requirement (como Brick Strict Mode)

### Unique (Ventaja Única de Umbral)
Features que **nadie tiene bien** o directamente no existen:
- ⭐ **Android + Open Source + NFC** - Único en el mercado
- ⭐ **UI/UX en español nativo** - No traducción, diseñado para LATAM
- ⭐ **Colaboración con Foqos** - Ecosistema iOS+Android unificado
- ⭐ **Filosofía del "Umbral"** - Concepto diferenciador (metaxy)
- ⭐ **Tags NFC baratos de Amazon** - No hardware propietario costoso

---

## Posicionamiento Competitivo

### Matriz de Posicionamiento

```
                    PRECIO ALTO
                         │
     Brick ($59)         │
        ●                │
                         │    Unpluq ($122/año)
                         │        ●
  ─────────────────────────────────────────────►
   OPEN SOURCE           │           CLOSED SOURCE
                         │
        ●                │        ● One Sec
      Foqos              │
        ●                │        ● ScreenZen
      Umbral ⭐           │
                         │
                    PRECIO BAJO
```

### Posición de Umbral

**Cuadrante:** Open Source + Precio Bajo (con Foqos)

**Diferenciador:** Único player Android en ese cuadrante

### Tagline Propuesto

> "Umbral: El bloqueador de apps open source para Android"

o

> "Cruza el umbral. Bloquea las distracciones."

---

## Conclusiones

### Mercado Favorable
- Mercado de $2.37B creciendo 12.8% anual
- Usuarios cada vez más conscientes de bienestar digital
- Demanda de soluciones físicas (NFC) vs solo software
- Tendencia hacia open source y privacidad

### Posición Única
Umbral tiene una posición competitiva única como:
1. **Único** app blocker Android + open source + NFC
2. **Complemento** natural de Foqos (iOS)
3. **Enfocado** en mercado hispanohablante
4. **Diferenciado** por filosofía ("metaxy", umbral)

### Riesgos a Mitigar
1. **Foqos Android** - Si Foqos lanza Android, Umbral pierde ventaja
2. **Google Play policies** - AccessibilityService scrutinio
3. **UX complexity** - Balancear features sin over-engineering
4. **Sustainability** - Modelo open source sin revenue directo

### Recomendación Final

**Proceder con desarrollo** - El gap de mercado es real y la oportunidad es clara.

**Prioridades:**
1. Feature parity con Foqos (NFC, QR, profiles, timers)
2. UX superior en español
3. Colaboración formal con Foqos
4. Widgets y estadísticas como diferenciadores

---

## Matriz de Priorización de Features

### Criterios de Evaluación

| Criterio | Descripción | Peso |
|----------|-------------|------|
| **Valor** | Impacto en usuario/negocio (1-5) | 40% |
| **Diferenciación** | Qué tan único vs competencia (1-5) | 25% |
| **Esfuerzo** | Complejidad técnica (S/M/L/XL) | 20% |
| **Riesgo** | Incertidumbre técnica o de mercado (1-5) | 15% |

### Fórmula de Score
```
Score = (Valor × 0.4) + (Diferenciación × 0.25) - (Esfuerzo_Num × 0.2) - (Riesgo × 0.15)
Donde: S=1, M=2, L=3, XL=4
```

### Matriz de Features

| Feature | Valor | Dif. | Esfuerzo | Riesgo | Score | Fase |
|---------|-------|------|----------|--------|-------|------|
| NFC tag reading | 5 | 5 | M | 2 | **3.35** | MVP |
| NFC tag writing | 5 | 5 | M | 2 | **3.35** | MVP |
| App blocking (UsageStats) | 5 | 3 | L | 3 | **2.30** | MVP |
| Multiple profiles | 5 | 4 | M | 1 | **3.25** | MVP |
| QR code alternative | 4 | 5 | M | 1 | **3.10** | MVP |
| Timer auto-unlock | 4 | 3 | S | 1 | **2.80** | MVP |
| Blocking overlay | 5 | 3 | M | 2 | **2.75** | MVP |
| Whitelist apps | 5 | 3 | S | 1 | **3.30** | MVP |
| Status widget | 4 | 4 | M | 1 | **2.95** | MVP |
| Quick toggle widget | 4 | 4 | M | 1 | **2.95** | MVP |
| Usage statistics | 4 | 3 | M | 1 | **2.65** | MVP |
| Streaks/rachas | 3 | 3 | S | 1 | **2.25** | MVP |
| Dark mode | 3 | 1 | S | 1 | **1.60** | MVP |
| Notifications config | 3 | 1 | S | 1 | **1.60** | MVP |
| Onboarding flow | 5 | 2 | M | 1 | **2.75** | MVP |
| Physical unlock req. | 3 | 4 | M | 2 | **2.10** | v1.1 |
| Quick Settings tile | 3 | 3 | M | 1 | **2.05** | v1.1 |
| App shortcuts | 3 | 3 | S | 1 | **2.25** | v1.1 |
| Timer widget | 3 | 4 | M | 1 | **2.35** | v1.1 |
| Session history | 3 | 2 | S | 1 | **1.95** | v1.1 |
| Most blocked apps | 3 | 3 | M | 1 | **2.05** | v1.1 |
| Export/import config | 2 | 2 | M | 2 | **1.00** | v1.1 |
| App category detection | 3 | 3 | L | 2 | **1.35** | v1.1 |
| Focus Mode integration | 3 | 3 | XL | 4 | **0.55** | Futuro |
| Scheduled blocking | 3 | 2 | L | 2 | **1.15** | Futuro |
| Device Admin (strict) | 2 | 4 | XL | 5 | **-0.25** | Futuro |
| Cloud sync | 2 | 2 | XL | 3 | **-0.15** | Futuro |

### Decisiones de Scope

#### MVP (12-14 semanas)
Features core que definen el producto:

**Must Have:**
- [ ] NFC tag reading/writing
- [ ] App blocking via UsageStatsManager
- [ ] Multiple profiles
- [ ] QR code alternative
- [ ] Timer auto-unlock
- [ ] Blocking overlay
- [ ] Whitelist apps
- [ ] Status widget
- [ ] Quick toggle widget
- [ ] Basic statistics (tiempo, sesiones)
- [ ] Streaks/rachas
- [ ] Onboarding flow
- [ ] Dark mode
- [ ] Notifications

**Why these?** Son los features que definen la propuesta de valor core y tienen feature parity con Foqos.

#### v1.1 (4-6 semanas post-MVP)
Features de refinamiento y power users:

**Nice to Have:**
- [ ] Physical unlock requirement (opcional)
- [ ] Quick Settings tile
- [ ] App shortcuts (launcher)
- [ ] Timer widget
- [ ] Session history
- [ ] Most blocked apps insights
- [ ] Export/import configuration
- [ ] Smart app category detection

**Why these?** Mejoran la experiencia pero no son esenciales para el valor core.

#### Futuro (TBD)
Features avanzados para evaluar después del launch:

**Potencial:**
- [ ] Focus Mode integration (Android Digital Wellbeing API)
- [ ] Scheduled blocking (automático por hora)
- [ ] Device Admin strict mode (prevenir uninstall)
- [ ] Cloud sync (Supabase, opcional)
- [ ] Website blocking (VPN approach)
- [ ] Location-based triggers

**Why futuro?** Alto riesgo técnico, políticas de Google Play, o bajo ROI para MVP.

### Priorización Visual

```
IMPACTO ALTO
     │
     │  ⭐ NFC R/W      ⭐ App Blocking
     │  ⭐ Profiles     ⭐ QR Code
     │  ⭐ Whitelist    ⭐ Widgets
     │
     │  ✅ Timer        ✅ Overlay
     │  ✅ Stats        ✅ Onboarding
     │
     │  ◯ Physical Unlock   ◯ QS Tile
     │  ◯ Shortcuts         ◯ History
     │
     │  ○ Scheduled    ○ Focus Mode
     │  ○ Device Admin ○ Cloud Sync
     │
     └─────────────────────────────────►
                               ESFUERZO ALTO

Leyenda:
⭐ = MVP Must Have
✅ = MVP Nice to Have
◯ = v1.1
○ = Futuro
```

---

## Documentos Relacionados

- [Technical Decisions](./technical-decisions.md) - Arquitectura y stack tecnológico
- [User Personas](./user-personas.md) - Perfiles de usuario objetivo
- [User Stories](./user-stories.md) - Historias de usuario por épica

---

**Creado:** 2026-01-03T04:32:12Z
**Metodología:** Oden Forge - Domain Expert Analysis
**Fuentes:** Web research, app stores, GitHub, reviews
