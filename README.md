# Umbral

> *"El umbral de tu casa es el punto de decisión consciente."*

**Umbral** es una app Android open-source que bloquea automáticamente apps de redes sociales cuando sales de casa, usando tags NFC como trigger físico.

---

## 🎯 Concepto

El nombre "Umbral" proviene del concepto filosófico griego del **metaxy (μεταξύ)** - el espacio liminal entre dos estados. Representa el momento consciente de transición al cruzar el umbral de tu casa, donde eliges tu estado digital.

**¿Cómo funciona?**
1. Colocas un tag NFC barato ($1 USD) en tu puerta
2. Al salir de casa, tocas tu teléfono al tag
3. Umbral bloquea automáticamente tus apps de redes sociales
4. Al regresar, tocas el tag nuevamente para desbloquear

---

## ✨ Features (V1 - Completo)

### Core
- 📱 **NFC tag reading/writing** - Compatible con NTAG213/215/216
- 🚫 **App blocking** - UsageStatsManager integration
- ✅ **Whitelist** - Apps esenciales (banco, sistema, etc.)
- 📋 **Multiple profiles** - Diferentes perfiles para diferentes situaciones

### Advanced
- ⏱️ **Timer auto-unlock** - Desbloqueo automático después de X tiempo
- 📷 **QR alternative** - Fallback si NFC no disponible
- 🎨 **Widgets** - Estado, quick toggle, countdown
- 📊 **Statistics** - Tiempo bloqueado, apps más bloqueadas, rachas
- 🔒 **Physical unlock** - Solo tag específico puede desbloquear (opcional)
- 🎯 **Focus Mode** - Integración con Digital Wellbeing
- ⚡ **Quick Settings** - Toggle desde panel rápido

---

## 🛠️ Tech Stack

- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose (Material Design 3)
- **Arquitectura:** Clean Architecture + MVVM
- **Database:** Room (SQLite)
- **DI:** Hilt
- **Build:** Gradle (Kotlin DSL)

---

## 🚀 Estado del Proyecto

**Fase actual:** 🟡 Pre-Desarrollo (Documentación)

### Metodología: Documentation-First Development

Seguimos la **Metodología Oden** donde documentamos y diseñamos COMPLETAMENTE antes de escribir código.

**Progreso:**
- [x] Inicialización del proyecto
- [x] Technical decisions documentadas
- [ ] Arquitectura detallada (próximo paso)
- [ ] Análisis competitivo
- [ ] Especificaciones por módulo
- [ ] Plan de implementación
- [ ] Desarrollo (12-16 semanas)

---

## 📚 Documentación

Ver [docs/README.md](docs/README.md) para documentación completa.

**Documentos clave:**
- [Technical Decisions](docs/reference/technical-decisions.md) - Stack, arquitectura y decisiones
- [Competitive Analysis](docs/reference/competitive-analysis.md) - Análisis de mercado (pendiente)
- [Implementation Plan](docs/reference/implementation-plan.md) - Plan detallado (pendiente)

---

## 🤝 Inspiración y Colaboración

Umbral está inspirado en [**Foqos**](https://github.com/awaseem/foqos), una excelente app iOS open-source con funcionalidad similar.

**Estrategia:**
- Foqos cubre iOS perfectamente
- Umbral cubre Android
- Colaboración, no competencia
- Tags NFC compatibles entre ambas apps

---

## 🎨 Diferenciadores

vs **Foqos** (iOS open source):
- ✅ Plataforma Android
- ✅ UX más pulida y onboarding mejorado
- ✅ Mercado hispanohablante (UI en español)

vs **Brick** (iOS/Android comercial):
- ✅ 100% gratis y open source
- ✅ No requiere hardware propietario
- ✅ Tags NFC baratos de Amazon

vs **Unpluq** (iOS/Android comercial):
- ✅ Sin suscripción mensual
- ✅ Código abierto
- ✅ Privacidad total (100% local, sin cloud)

---

## 🔒 Privacidad

- 🔐 **100% local** - Sin backend en V1
- 🔐 **Sin tracking** - Cero analytics por defecto
- 🔐 **Open source** - Auditable por cualquiera
- 🔐 **Sin permisos innecesarios** - Solo lo estrictamente necesario

---

## 📦 Distribución

**Planeada:**
- Google Play Store (primario)
- F-Droid (secundario, para usuarios privacy-focused)

---

## 🗺️ Roadmap

### V1.0 - Core (12-16 semanas)
Todas las features listadas arriba

### V1.1 - Refinement (2 semanas)
Bug fixes y polish basado en feedback

### V2.0 - Cloud Features (4-6 semanas)
- Supabase backend (opcional)
- Cloud sync de perfiles
- Multi-device support
- Premium tier

### V3.0 - Advanced (6-8 semanas)
- Website blocking
- Location-based triggers
- Scheduled blocking
- Social features (accountability partner)

---

## 👥 Contribuciones

**¡Contributions welcome!**

Este proyecto está en fase de documentación. Una vez que empecemos desarrollo, publicaremos guías de contribución.

Por ahora, si quieres ayudar:
- ⭐ Dale star al repo
- 💡 Sugiere features (Issues)
- 📖 Revisa la documentación y da feedback

---

## 📄 Licencia

[Pendiente definir - probablemente MIT]

---

## 🙏 Agradecimientos

- [Foqos](https://github.com/awaseem/foqos) - Inspiración y referencia
- Comunidad open source de Android
- Filósofos griegos por el concepto de metaxy 😄

---

## 📬 Contacto

[Pendiente: agregar info de contacto]

---

**Proyecto iniciado:** 2026-01-03
**Filosofía:** Documentation-First Development (Metodología Oden)
