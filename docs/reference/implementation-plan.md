# Plan de Implementacion: Umbral

**Estado:** Aprobado
**Ultima actualizacion:** 2026-01-03
**Modalidad:** Modo Completo
**Duracion:** 14 semanas

---

## 1. Resumen Ejecutivo

### Timeline de Alto Nivel

```
+----------------------------------------------------------------------------+
|                        TIMELINE DEL PROYECTO                               |
+----------------------------------------------------------------------------+
|                                                                            |
|  Sem 1-2    |========| Foundation (Setup + Arquitectura)                  |
|  Sem 3-4    |========| NFC Core (Lectura/Escritura tags)                  |
|  Sem 5-6    |========| Blocking Works (UsageStats + Overlay)              |
|  Sem 7-8    |========| Profiles + QR (Gestion + Alternativa NFC)          |
|  Sem 9-10   |========| Stats + Onboarding (Metricas + UX)                 |
|  Sem 11-12  |========| Feature Complete (Widgets + Polish)                |
|  Sem 13-14  |========| Launch Ready (Testing + Release)                   |
|                                                                            |
+----------------------------------------------------------------------------+
```

### Milestones

| # | Milestone | Semana | Criterio de Exito |
|---|-----------|--------|-------------------|
| M1 | Foundation Ready | 2 | Proyecto compilando, DI configurado, navegacion basica |
| M2 | NFC Core | 4 | Leer/escribir NTAG213, validacion funciona |
| M3 | Blocking Works | 6 | Bloqueo efectivo con overlay, whitelist |
| M4 | Profiles + QR | 8 | CRUD perfiles, QR scan/generate |
| M5 | Stats + Onboarding | 10 | Metricas funcionando, onboarding completo |
| M6 | Feature Complete | 12 | Widgets, Quick Settings, achievements |
| M7 | Launch Ready | 14 | Tests >80%, 0 bugs criticos, Play Store ready |

### Metricas del Proyecto

| Metrica | Valor |
|---------|-------|
| Total de modulos | 7 |
| Lineas de specs | ~5,300 |
| Horas estimadas | ~496h |
| Tareas totales | ~120 |
| Desarrollador(es) | 1 |

---

## 2. Inventario de Trabajo

### Modulos y Estimaciones

| Modulo | Spec (lineas) | Complejidad | Horas Base | Con Buffer (25%) |
|--------|---------------|-------------|------------|------------------|
| NFC | ~1,100 | Alta | 80h | 100h |
| Blocking | ~1,050 | Alta | 72h | 90h |
| Profiles | ~900 | Media | 48h | 60h |
| UI | ~900 | Media | 56h | 70h |
| QR | ~450 | Media | 40h | 50h |
| Stats | ~650 | Media | 48h | 60h |
| Onboarding | ~400 | Baja | 24h | 30h |
| **Foundation** | - | Media | 40h | 50h |
| **Testing/QA** | - | - | 48h | 60h |
| **Total** | ~5,450 | - | 456h | **570h** |

### Multiplicadores Aplicados

- NFC APIs: 1.2x (APIs de bajo nivel, debugging hardware)
- UsageStats: 1.3x (permisos especiales, testing en multiples devices)
- Overlay UI: 1.2x (comportamiento especifico por OEM)

---

## 3. Fase 1: Foundation (Semanas 1-2)

### Objetivo
Proyecto Android configurado con Clean Architecture, DI, y navegacion basica.

### Semana 1: Project Setup

#### Dia 1-2: Inicializacion

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 1.1 | Crear proyecto Android Studio | 2h | - | Proyecto compila |
| 1.2 | Configurar estructura Clean Architecture | 4h | 1.1 | data/domain/presentation folders |
| 1.3 | Setup Gradle con versiones catalog | 2h | 1.1 | libs.versions.toml configurado |
| 1.4 | Configurar Hilt DI | 4h | 1.2 | AppModule inyecta correctamente |
| 1.5 | Setup Room Database base | 4h | 1.4 | AppDatabase compila |

**Total: 16h**

#### Dia 3-4: Core Infrastructure

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 1.6 | Configurar Jetpack Compose | 2h | 1.1 | Theme aplicado |
| 1.7 | Crear Design System base | 6h | 1.6 | Colors, Typography, Dimensions |
| 1.8 | Setup Navigation Compose | 4h | 1.6 | NavHost con rutas |
| 1.9 | Configurar DataStore | 2h | 1.4 | Preferences guardando |
| 1.10 | Setup Logger (Timber) | 1h | 1.1 | Logs funcionando |

**Total: 15h**

#### Dia 5: Testing + Buffer

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 1.11 | Setup testing framework | 3h | 1.4 | JUnit5 + MockK configurados |
| 1.12 | Configurar CI basico | 2h | 1.11 | GitHub Actions build |
| 1.13 | Buffer imprevistos | 2h | - | - |
| 1.14 | Documentar setup | 1h | Todo | README actualizado |

**Total: 8h**

---

### Semana 2: UI Base + Permisos

#### Dia 1-2: Pantallas Base

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 2.1 | HomeScreen scaffold | 4h | 1.8 | Pantalla renderiza |
| 2.2 | BottomNavigation | 3h | 2.1 | Navegacion funciona |
| 2.3 | ProfilesScreen scaffold | 3h | 1.8 | Lista vacia visible |
| 2.4 | SettingsScreen scaffold | 3h | 1.8 | Opciones basicas |
| 2.5 | TopAppBar reusable | 2h | 1.7 | Componente creado |

**Total: 15h**

#### Dia 3-4: Sistema de Permisos

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 2.6 | PermissionManager interface | 2h | 1.4 | Interface definida |
| 2.7 | UsageStats permission flow | 4h | 2.6 | Redirect a Settings |
| 2.8 | Overlay permission flow | 4h | 2.6 | Redirect a Settings |
| 2.9 | Camera permission (para QR) | 2h | 2.6 | Permission request |
| 2.10 | Permission status UI | 3h | 2.6 | Indicadores en Settings |

**Total: 15h**

#### Dia 5: Integracion + Review

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 2.11 | Integrar pantallas | 2h | 2.1-2.4 | Navegacion completa |
| 2.12 | Tests de navegacion | 2h | 2.11 | Tests pasando |
| 2.13 | Code review | 2h | Todo | PRs mergeados |
| 2.14 | Buffer | 2h | - | - |

**Total: 8h**

### Entregables M1 (Foundation Ready)
- [ ] Proyecto compilando con Compose
- [ ] Clean Architecture implementada
- [ ] Hilt DI configurado
- [ ] Room Database base
- [ ] Navegacion funcionando
- [ ] Sistema de permisos base
- [ ] Design System inicial
- [ ] CI/CD basico

---

## 4. Fase 2: NFC Core (Semanas 3-4)

### Objetivo
Lectura y escritura de tags NFC NTAG213/215/216 completamente funcional.

### Semana 3: NFC Reading

#### Dia 1-2: Infraestructura NFC

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 3.1 | NfcManager interface | 3h | 1.4 | Interface completa |
| 3.2 | NfcManagerImpl con Adapter | 6h | 3.1 | Detecta NFC enabled |
| 3.3 | Foreground dispatch setup | 4h | 3.2 | Activity recibe intents |
| 3.4 | NfcTag domain model | 2h | - | Data class creada |
| 3.5 | Room Entity: NfcTagEntity | 2h | 1.5 | Entity + DAO |

**Total: 17h**

#### Dia 3-4: Lectura de Tags

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 3.6 | NDEF record parsing | 4h | 3.3 | Lee payloads validos |
| 3.7 | Tag validation logic | 4h | 3.6 | Valida formato Umbral |
| 3.8 | NfcScanScreen UI | 6h | 3.3, 1.7 | Animacion + feedback |
| 3.9 | Haptic feedback | 2h | 3.8 | Vibracion en scan |
| 3.10 | Error handling UI | 3h | 3.8 | Errores claros |

**Total: 19h**

#### Dia 5: Testing + Polish

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 3.11 | Unit tests NFC | 3h | 3.1-3.7 | Coverage >70% |
| 3.12 | Manual testing con tags | 2h | 3.8 | Funciona con NTAG213 |
| 3.13 | Buffer | 2h | - | - |

**Total: 7h**

---

### Semana 4: NFC Writing + Profiles Link

#### Dia 1-2: Escritura de Tags

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 4.1 | NDEF message creation | 4h | 3.6 | Crea mensajes validos |
| 4.2 | Tag writing logic | 6h | 4.1 | Escribe a NTAG |
| 4.3 | Write confirmation flow | 3h | 4.2 | UI confirma escritura |
| 4.4 | Overwrite protection | 2h | 4.2 | Confirma si tiene datos |
| 4.5 | Tag info screen | 3h | 3.4 | Muestra detalles tag |

**Total: 18h**

#### Dia 3-4: Integracion con Perfiles

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 4.6 | Profile-Tag linking logic | 4h | 4.2, 1.5 | Relacion en DB |
| 4.7 | "Write to Tag" from profile | 4h | 4.6 | Flujo completo |
| 4.8 | Tag validation on scan | 3h | 4.6 | Verifica perfil existe |
| 4.9 | Multiple tags per profile | 3h | 4.6 | Soporta N tags |
| 4.10 | Tag management screen | 4h | 4.9 | Lista/elimina tags |

**Total: 18h**

#### Dia 5: Testing + Docs

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 4.11 | Integration tests | 3h | 4.1-4.10 | Flujos completos |
| 4.12 | Test con NTAG215/216 | 2h | 4.2 | Compatibilidad |
| 4.13 | Documentar troubleshooting | 1h | - | Guia errores NFC |
| 4.14 | Buffer | 2h | - | - |

**Total: 8h**

### Entregables M2 (NFC Core)
- [ ] Lectura de tags NTAG213/215/216
- [ ] Escritura de payloads Umbral
- [ ] UI de escaneo con feedback
- [ ] Validacion de tags
- [ ] Linking tag-perfil funcional
- [ ] Tests pasando

---

## 5. Fase 3: Blocking Works (Semanas 5-6)

### Objetivo
Sistema de bloqueo de apps funcional con UsageStatsManager y overlay UI.

### Semana 5: App Detection

#### Dia 1-2: UsageStats Infrastructure

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 5.1 | AppMonitor interface | 3h | 1.4 | Interface definida |
| 5.2 | UsageStatsMonitor impl | 8h | 5.1, 2.7 | Detecta app actual |
| 5.3 | Polling service | 4h | 5.2 | Foreground Service |
| 5.4 | App info retrieval | 3h | 5.2 | Nombre, icono, package |
| 5.5 | BlockingState model | 2h | - | Estados definidos |

**Total: 20h**

#### Dia 3-4: App Selection

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 5.6 | Installed apps list | 4h | 5.4 | Lista completa apps |
| 5.7 | App selection UI | 6h | 5.6 | Checkbox + busqueda |
| 5.8 | System apps filter | 2h | 5.6 | Oculta/muestra sistema |
| 5.9 | Whitelist management | 4h | 5.7 | Essentials siempre OK |
| 5.10 | Save selections to DB | 2h | 5.7 | Room persistence |

**Total: 18h**

#### Dia 5: Testing

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 5.11 | Unit tests detection | 3h | 5.1-5.5 | Mocks funcionan |
| 5.12 | Manual testing | 2h | 5.6-5.10 | UX fluido |
| 5.13 | Buffer | 2h | - | - |

**Total: 7h**

---

### Semana 6: Overlay + Blocking Logic

#### Dia 1-2: Overlay UI

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 6.1 | BlockingOverlay service | 6h | 2.8 | Overlay aparece |
| 6.2 | Overlay design (Compose) | 4h | 6.1 | Match design system |
| 6.3 | Unlock options UI | 4h | 6.2 | NFC/QR/Timer visible |
| 6.4 | Overlay animations | 3h | 6.2 | Transiciones suaves |
| 6.5 | Dismiss protection | 2h | 6.1 | No se puede cerrar |

**Total: 19h**

#### Dia 3-4: Blocking Logic

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 6.6 | BlockingEngine core | 6h | 5.2, 5.5 | Logica completa |
| 6.7 | Profile-based blocking | 4h | 6.6 | Perfil activo bloquea |
| 6.8 | NFC unlock flow | 4h | 6.6, 3.6 | Scan desbloquea |
| 6.9 | Timer-based unlock | 3h | 6.6 | Countdown funciona |
| 6.10 | Notification for active | 2h | 6.6 | Notificacion sticky |

**Total: 19h**

#### Dia 5: Integration Testing

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 6.11 | End-to-end blocking test | 3h | 6.1-6.10 | Flujo completo |
| 6.12 | Battery optimization | 2h | 5.3 | Sin drain excesivo |
| 6.13 | Multi-device testing | 2h | - | 3+ devices OK |
| 6.14 | Buffer | 2h | - | - |

**Total: 9h**

### Entregables M3 (Blocking Works)
- [ ] Deteccion de app actual
- [ ] Lista de apps instaladas
- [ ] Sistema de whitelist
- [ ] Overlay de bloqueo
- [ ] Unlock con NFC
- [ ] Unlock con timer
- [ ] Notificacion persistente
- [ ] Tests en multiples devices

---

## 6. Fase 4: Profiles + QR (Semanas 7-8)

### Objetivo
Gestion completa de perfiles de bloqueo y alternativa QR a NFC.

### Semana 7: Profiles CRUD

#### Dia 1-2: Profile Management

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 7.1 | BlockingProfile domain | 3h | - | Model completo |
| 7.2 | ProfileRepository | 4h | 7.1, 1.5 | CRUD en Room |
| 7.3 | CreateProfile screen | 6h | 7.2 | Form funcional |
| 7.4 | EditProfile screen | 4h | 7.3 | Edicion works |
| 7.5 | Profile icons/colors | 2h | 7.3 | Personalizacion |

**Total: 19h**

#### Dia 3-4: Profile Features

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 7.6 | Schedule per profile | 4h | 7.2 | Horarios definidos |
| 7.7 | Strict mode toggle | 2h | 7.2 | Sin bypass |
| 7.8 | Profile templates | 4h | 7.2 | "Focus", "Sleep", etc |
| 7.9 | Profile duplicating | 2h | 7.2 | Copia rapida |
| 7.10 | Profile deletion | 2h | 7.2 | Con confirmacion |

**Total: 14h**

#### Dia 5: Testing

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 7.11 | Profile CRUD tests | 3h | 7.1-7.10 | Coverage >80% |
| 7.12 | UI/UX review | 2h | 7.3-7.4 | Flujo intuitivo |
| 7.13 | Buffer | 2h | - | - |

**Total: 7h**

---

### Semana 8: QR Code System

#### Dia 1-2: QR Generation

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 8.1 | QrPayload model | 2h | - | Estructura definida |
| 8.2 | QrGenerator (ZXing) | 4h | 8.1 | Genera QR valido |
| 8.3 | Encryption (AES-256) | 4h | 8.2 | Payload encriptado |
| 8.4 | QR display screen | 4h | 8.2 | Muestra QR grande |
| 8.5 | Save QR to gallery | 2h | 8.4 | Export funciona |

**Total: 16h**

#### Dia 3-4: QR Scanning

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 8.6 | CameraX setup | 4h | 2.9 | Preview funciona |
| 8.7 | ML Kit scanner | 4h | 8.6 | Detecta QR |
| 8.8 | QR validation | 3h | 8.7, 8.3 | Decrypt + validate |
| 8.9 | Scan UI polish | 3h | 8.6 | Viewfinder, feedback |
| 8.10 | QR unlock flow | 4h | 8.8, 6.6 | Scan desbloquea |

**Total: 18h**

#### Dia 5: Integration

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 8.11 | QR from profile screen | 2h | 8.4, 7.2 | Generate from profile |
| 8.12 | QR history | 2h | 8.1 | Lista QRs generados |
| 8.13 | QR tests | 2h | 8.1-8.10 | Encrypt/decrypt OK |
| 8.14 | Buffer | 2h | - | - |

**Total: 8h**

### Entregables M4 (Profiles + QR)
- [ ] CRUD completo de perfiles
- [ ] Personalizacion (iconos, colores)
- [ ] Templates predefinidos
- [ ] Scheduling de perfiles
- [ ] Generacion QR con encriptacion
- [ ] Escaneo QR con ML Kit
- [ ] Unlock via QR funcional
- [ ] Tests integracion

---

## 7. Fase 5: Stats + Onboarding (Semanas 9-10)

### Objetivo
Sistema de metricas/gamification y experiencia de primer uso.

### Semana 9: Statistics System

#### Dia 1-2: Stats Tracking

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 9.1 | BlockedAttempt entity | 2h | 1.5 | Room entity |
| 9.2 | StatsTracker interface | 3h | 9.1 | Interface definida |
| 9.3 | StatsTrackerImpl | 4h | 9.2, 6.6 | Registra intentos |
| 9.4 | BlockingSession entity | 2h | 9.1 | Sesiones tracking |
| 9.5 | Daily/Weekly summaries | 4h | 9.4 | Agregaciones |

**Total: 15h**

#### Dia 3-4: Stats UI

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 9.6 | StatsScreen scaffold | 4h | 9.5 | Pantalla base |
| 9.7 | MainStatsCard | 3h | 9.6 | Resumen visual |
| 9.8 | TrendChart (Vico) | 6h | 9.6 | Grafica 7 dias |
| 9.9 | TopBlockedApps | 3h | 9.6 | Lista top apps |
| 9.10 | Export stats | 2h | 9.5 | JSON/CSV export |

**Total: 18h**

#### Dia 5: Achievements

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 9.11 | Achievement model | 2h | - | 12 achievements |
| 9.12 | Achievement tracker | 3h | 9.11, 9.3 | Detecta logros |
| 9.13 | Achievements UI | 3h | 9.12 | Grid de badges |
| 9.14 | Buffer | 2h | - | - |

**Total: 10h**

---

### Semana 10: Onboarding Flow

#### Dia 1-2: Onboarding Screens

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 10.1 | OnboardingManager | 2h | 1.9 | State en DataStore |
| 10.2 | WelcomeScreen | 3h | 1.7 | Diseno atractivo |
| 10.3 | HowItWorksScreen | 4h | 10.2 | Animaciones Lottie |
| 10.4 | PermissionsScreen | 4h | 10.2, 2.6 | Request fluido |
| 10.5 | SelectAppsScreen | 4h | 10.2, 5.7 | Quick selection |

**Total: 17h**

#### Dia 3-4: Onboarding Polish

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 10.6 | SuccessScreen | 3h | 10.1 | Celebracion |
| 10.7 | Skip/Resume logic | 2h | 10.1 | Puede saltar |
| 10.8 | First profile creation | 4h | 10.6, 7.3 | Auto-crea perfil |
| 10.9 | Onboarding animations | 3h | 10.2-10.6 | Transiciones |
| 10.10 | Progress indicator | 2h | 10.1 | Dots/stepper |

**Total: 14h**

#### Dia 5: Testing + Streak System

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 10.11 | Streak calculation | 3h | 9.4 | Dias consecutivos |
| 10.12 | Streak UI | 2h | 10.11 | Fire icon, contador |
| 10.13 | Onboarding tests | 2h | 10.1-10.10 | Flow completo |
| 10.14 | Buffer | 2h | - | - |

**Total: 9h**

### Entregables M5 (Stats + Onboarding)
- [ ] Tracking de intentos bloqueados
- [ ] Dashboard de estadisticas
- [ ] Graficas de tendencia
- [ ] Sistema de achievements
- [ ] Onboarding 5 pantallas
- [ ] Permisos durante onboarding
- [ ] Sistema de streaks
- [ ] Tests completos

---

## 8. Fase 6: Feature Complete (Semanas 11-12)

### Objetivo
Widgets, Quick Settings, polish general y features finales.

### Semana 11: Widgets + Quick Settings

#### Dia 1-2: Glance Widgets

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 11.1 | StatusWidget (small) | 4h | 1.6 | Estado en homescreen |
| 11.2 | StatsWidget (medium) | 4h | 9.6 | Stats resumidas |
| 11.3 | QuickToggleWidget | 3h | 6.6 | On/off rapido |
| 11.4 | Widget configuration | 3h | 11.1-11.3 | Selector perfil |
| 11.5 | Widget refresh logic | 2h | 11.1-11.3 | Updates correctos |

**Total: 16h**

#### Dia 3-4: Quick Settings + Shortcuts

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 11.6 | Quick Settings Tile | 4h | 6.6 | Tile funcional |
| 11.7 | Tile states | 2h | 11.6 | Active/inactive icons |
| 11.8 | App Shortcuts | 3h | 7.2 | Long-press shortcuts |
| 11.9 | Deep links | 3h | 1.8 | umbral://profile/x |
| 11.10 | Intent handling | 2h | 11.9 | External triggers |

**Total: 14h**

#### Dia 5: Integration

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 11.11 | Widget tests | 2h | 11.1-11.5 | Render OK |
| 11.12 | Tile tests | 2h | 11.6-11.7 | Toggle works |
| 11.13 | Documentation | 2h | - | User guide widgets |
| 11.14 | Buffer | 2h | - | - |

**Total: 8h**

---

### Semana 12: Polish + Final Features

#### Dia 1-2: UX Polish

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 12.1 | Animaciones globales | 4h | 1.7 | Micro-interactions |
| 12.2 | Loading states | 3h | - | Skeletons, progress |
| 12.3 | Empty states | 3h | - | Ilustraciones |
| 12.4 | Error handling UI | 4h | - | Mensajes claros |
| 12.5 | Haptic feedback | 2h | - | Tactil consistente |

**Total: 16h**

#### Dia 3-4: Accessibility + Dark Mode

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 12.6 | ContentDescriptions | 3h | - | TalkBack funciona |
| 12.7 | Touch targets 48dp | 2h | - | WCAG compliance |
| 12.8 | Dark mode complete | 4h | 1.7 | Todos los screens |
| 12.9 | High contrast option | 2h | 12.8 | Accessibility |
| 12.10 | Font scaling | 2h | - | Respeta settings |

**Total: 13h**

#### Dia 5: Final Polish

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 12.11 | App icon final | 2h | - | Adaptive icon |
| 12.12 | Splash screen | 2h | - | Themed splash |
| 12.13 | About screen | 2h | - | Version, credits |
| 12.14 | Buffer | 2h | - | - |

**Total: 8h**

### Entregables M6 (Feature Complete)
- [ ] 3 tipos de widgets
- [ ] Quick Settings Tile
- [ ] App Shortcuts
- [ ] Deep linking
- [ ] Dark mode completo
- [ ] Accessibility WCAG
- [ ] Polish UI/UX
- [ ] Splash + Icon finales

---

## 9. Fase 7: Launch Ready (Semanas 13-14)

### Objetivo
Testing completo, optimizacion, y preparacion para Google Play.

### Semana 13: Testing + QA

#### Dia 1-2: Unit + Integration Tests

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 13.1 | Complete unit tests | 6h | - | Coverage >80% |
| 13.2 | Integration tests | 6h | - | Flows criticos |
| 13.3 | Repository tests | 3h | - | CRUD Room |
| 13.4 | ViewModel tests | 3h | - | States correctos |

**Total: 18h**

#### Dia 3-4: E2E + Performance

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 13.5 | E2E con Espresso | 6h | - | Happy paths |
| 13.6 | Performance profiling | 4h | - | Sin memory leaks |
| 13.7 | Battery testing | 3h | - | Drain aceptable |
| 13.8 | Multi-device QA | 4h | - | 5+ devices |

**Total: 17h**

#### Dia 5: Bug Fixes

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 13.9 | Critical bug fixes | 4h | 13.1-13.8 | 0 criticos |
| 13.10 | Minor bug fixes | 2h | 13.9 | Priorizados |
| 13.11 | Regression testing | 2h | 13.10 | No regresiones |

**Total: 8h**

---

### Semana 14: Release Prep

#### Dia 1-2: Store Preparation

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 14.1 | App signing config | 2h | - | Keystore seguro |
| 14.2 | ProGuard rules | 3h | - | Release compila |
| 14.3 | Privacy policy | 2h | - | Documento legal |
| 14.4 | Store listing text | 3h | - | ES + EN |
| 14.5 | Screenshots | 4h | - | 8 screenshots |

**Total: 14h**

#### Dia 3-4: Final Release

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 14.6 | Feature graphic | 2h | - | 1024x500 |
| 14.7 | Release notes | 2h | - | v1.0.0 changelog |
| 14.8 | AAB generation | 2h | 14.2 | Bundle valido |
| 14.9 | Internal testing | 4h | 14.8 | Track funciona |
| 14.10 | Open testing prep | 2h | 14.9 | Beta ready |

**Total: 12h**

#### Dia 5: Launch

| ID | Tarea | Est. | Deps | Criterio Done |
|----|-------|------|------|---------------|
| 14.11 | GitHub release | 2h | - | Source publicado |
| 14.12 | Documentation final | 2h | - | README completo |
| 14.13 | Production release | 2h | 14.10 | Play Store submit |
| 14.14 | Monitoring setup | 2h | - | Crashlytics |

**Total: 8h**

### Entregables M7 (Launch Ready)
- [ ] Tests >80% coverage
- [ ] 0 bugs criticos
- [ ] Performance optimizada
- [ ] Store listing completo
- [ ] Privacy policy
- [ ] AAB signed
- [ ] Open beta
- [ ] v1.0.0 publicada

---

## 10. Dependencias

### Grafo de Dependencias

```
Foundation (1-2)
     |
     +---> NFC Core (3-4)------------+
     |                               |
     +---> Blocking (5-6)------------+---> Profiles+QR (7-8)
     |                               |
     +---> UI Base (2)---------------+
                                     |
                                     v
                              Stats+Onboarding (9-10)
                                     |
                                     v
                              Feature Complete (11-12)
                                     |
                                     v
                              Launch Ready (13-14)
```

### Dependencias Criticas

| Tarea | Bloquea | Impacto si late |
|-------|---------|-----------------|
| 1.4 Hilt DI | Todo | Critico - sin DI no hay arquitectura |
| 2.7 UsageStats permission | Blocking module | Alto - core del app |
| 3.3 Foreground dispatch | NFC reading | Alto - sin NFC no hay producto |
| 5.2 UsageStatsMonitor | Blocking logic | Alto - funcionalidad core |
| 6.6 BlockingEngine | Stats, Widgets, QR unlock | Alto - multiples dependencias |

---

## 11. Riesgos y Mitigacion

| Riesgo | Prob. | Impacto | Mitigacion |
|--------|-------|---------|------------|
| UsageStats insuficiente | Media | Alto | AccessibilityService como backup |
| OEM-specific issues | Alta | Medio | Testing en 5+ marcas, documentar workarounds |
| Google Play rejection | Baja | Alto | Seguir policies estrictamente, no usar Accessibility |
| NFC no funciona en device | Baja | Medio | QR code como alternativa completa |
| Battery drain excesivo | Media | Medio | Polling optimizado, Doze-aware |
| Performance en low-end | Media | Medio | Profiling desde semana 6 |

### Contingencias

- **Si UsageStats no es suficiente:** Implementar AccessibilityService con Permission Declaration
- **Si Play Store rechaza:** Preparar sideload APK en GitHub
- **Si testing revela bugs criticos:** Extender semana 13 a 2 semanas
- **Si feature X toma mas tiempo:** Reducir polish en semana 12

---

## 12. Definition of Done

### Por Tarea
- [ ] Codigo implementado segun spec
- [ ] Tests escritos (unit minimo)
- [ ] Sin warnings de linter
- [ ] Funciona en emulador
- [ ] Code review (si aplica)

### Por Milestone
- [ ] Todas las tareas del milestone done
- [ ] Tests de integracion pasando
- [ ] Manual testing en 2+ devices
- [ ] Documentacion actualizada
- [ ] No bugs criticos

### Para Launch
- [ ] Test coverage >80%
- [ ] 0 crashes en 100 sesiones
- [ ] Battery drain <5% por hora activo
- [ ] Cold start <2 segundos
- [ ] APK size <20MB

---

## 13. Recursos

### Desarrollador
| Rol | % Tiempo | Semanas |
|-----|----------|---------|
| Android Developer | 100% | 1-14 |

### Herramientas
| Herramienta | Proposito |
|-------------|-----------|
| Android Studio | IDE |
| Firebase Crashlytics | Crash reporting |
| GitHub Actions | CI/CD |
| Figma | Design reference |

### Costos

| Servicio | Costo |
|----------|-------|
| Google Play Developer | $25 (unico) |
| Dominios/hosting | $0 (GitHub Pages) |
| CI/CD | $0 (GitHub Actions free tier) |
| **Total inicial** | **$25** |

---

## 14. Metricas de Seguimiento

### Weekly Check

| Metrica | Target | Como medir |
|---------|--------|------------|
| Tasks completed | 80%+ | GitHub Projects |
| Critical bugs | 0 | Issues |
| Tests passing | 100% | CI/CD |
| Build time | <5min | GitHub Actions |

### Burndown Proyectado

```
Tareas (~120)
|
120+--*
   |    \
100+-----*
   |       \
 80+---------*
   |           \
 60+-------------*
   |               \
 40+-----------------*
   |                   \
 20+---------------------*
   |                       \
  0+-------------------------*
   +---+---+---+---+---+---+---+
      S2  S4  S6  S8  S10 S12 S14
```

---

## 15. Checklist Pre-Codigo

Antes de empezar Semana 1:

- [x] technical-decisions.md completo (~2,800 lineas)
- [x] competitive-analysis.md completo
- [x] user-personas.md completo
- [x] user-stories.md completo
- [x] nfc-module.md spec (~1,100 lineas)
- [x] blocking-module.md spec (~1,050 lineas)
- [x] profiles-module.md spec (~900 lineas)
- [x] ui-module.md spec (~900 lineas)
- [x] qr-module.md spec (~450 lineas)
- [x] stats-module.md spec (~650 lineas)
- [x] onboarding-module.md spec (~400 lineas)
- [x] Este implementation-plan.md revisado

**Total specs: ~8,250 lineas de documentacion**

Siguiente paso:
```
/oden:checklist  --> Verificar todo listo
```

---

**Creado:** 2026-01-03
**Autor:** Implementation Planner Agent
**Metodologia:** Oden Forge - Documentation-First Development
