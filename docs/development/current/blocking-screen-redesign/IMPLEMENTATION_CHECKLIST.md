# Implementation Checklist - Blocking Screen

**Proyecto:** Umbral
**Feature:** Blocking Screen Redesign
**Fecha de creación:** 2026-01-15

---

## Cómo Usar Este Checklist

1. Marcar cada item con `[x]` al completarlo
2. Agregar notas en sección de comentarios si es necesario
3. Actualizar fecha de última modificación
4. Commitear cambios regularmente

---

## Fase 1: Setup de Proyecto (Día 1)

### Dependencias

```kotlin
// build.gradle.kts (Module: app)
```

- [ ] `androidx.compose:compose-bom:2024.01.00`
- [ ] `androidx.compose.material3:material3:1.2.0`
- [ ] `androidx.compose.material:material-icons-extended`
- [ ] `androidx.compose.animation:animation`
- [ ] `androidx.navigation:navigation-compose:2.7.6`
- [ ] `androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0`
- [ ] `com.google.dagger:hilt-android:2.50`
- [ ] `androidx.hilt:hilt-navigation-compose:1.1.0`

**Comentarios:**
```
[Agregar notas sobre problemas de dependencias aquí]
```

---

### Estructura de Carpetas

- [ ] Crear `presentation/ui/screens/blocking/`
- [ ] Crear `presentation/ui/theme/`
- [ ] Crear `res/values/strings.xml` (si no existe)
- [ ] Crear `test/ui/screens/blocking/` (tests)

**Comentarios:**
```
[Notas sobre estructura]
```

---

### Archivos Base

- [ ] Copiar `BlockingScreen.kt` → `app/src/main/java/com/umbral/presentation/ui/screens/blocking/`
- [ ] Copiar `UmbralTheme.kt` → `app/src/main/java/com/umbral/presentation/ui/theme/`
- [ ] Copiar strings de `strings.xml` → `app/src/main/res/values/strings.xml`
- [ ] Verificar imports (ajustar package names)

**Comentarios:**
```
[Notas sobre imports o ajustes]
```

---

## Fase 2: Integración con ViewModel (Día 2-3)

### BlockingViewModel.kt

- [ ] Crear archivo `BlockingViewModel.kt`
- [ ] Agregar `@HiltViewModel` annotation
- [ ] Inyectar repositories necesarios
- [ ] Implementar `StateFlow<BlockingState>`
- [ ] Implementar `loadBlockingState()`
- [ ] Implementar `onBackToHome()`
- [ ] Implementar `onEmergencyAccess()`
- [ ] Implementar `onScanNfc()`

**Código de referencia:** Ver `ImplementationGuide.md` sección "BlockingViewModel.kt"

**Comentarios:**
```
[Notas sobre integración]
```

---

### Repositories Necesarios

- [ ] `BlockingProfileRepository` existe?
- [ ] `UsageStatsRepository` existe?
- [ ] Crear interfaces si no existen
- [ ] Implementar métodos:
  - [ ] `getActiveProfile(): Flow<BlockingProfile?>`
  - [ ] `getFocusedTimeToday(): Flow<Int>`
  - [ ] `isFirstTimeBlocking(): Boolean`

**Comentarios:**
```
[Notas sobre repositories]
```

---

## Fase 3: Navegación (Día 3)

### AppNavigation.kt

- [ ] Agregar route `"blocking"` a NavHost
- [ ] Integrar `hiltViewModel()` en composable
- [ ] Conectar `collectAsState()` para state
- [ ] Pasar callbacks correctamente
- [ ] Probar navegación home → blocking
- [ ] Probar navegación blocking → home

**Comentarios:**
```
[Notas sobre navegación]
```

---

## Fase 4: Theme Integration (Día 3-4)

### UmbralTheme.kt

- [ ] Verificar paletas light/dark
- [ ] Probar dynamic colors en Android 12+
- [ ] Verificar en Android <12 (fallback)
- [ ] Ajustar colores si es necesario
- [ ] Probar en modo claro
- [ ] Probar en modo oscuro

**Comentarios:**
```
[Notas sobre theming]
```

---

### strings.xml

- [ ] Todos los strings en español
- [ ] Sin hardcoded strings en código
- [ ] Content descriptions completos
- [ ] Mensajes motivacionales (12 total)
- [ ] Plurals configurados (si aplica)

**Comentarios:**
```
[Notas sobre strings]
```

---

## Fase 5: Preview & Visual Testing (Día 4)

### Previews en Android Studio

- [ ] Abrir `BlockingScreen.kt`
- [ ] Ver preview: Normal Mode
- [ ] Ver preview: Strict Mode
- [ ] Ver preview: With Timer
- [ ] Ver preview: Dark Mode
- [ ] Ajustar layout si es necesario

**Comentarios:**
```
[Notas sobre previews]
```

---

### Testing Visual Manual

- [ ] Compilar app en dispositivo real
- [ ] Probar breathing animation (fluido?)
- [ ] Probar rotación de mensajes (8s interval)
- [ ] Probar button press animation (spring)
- [ ] Probar modo claro/oscuro
- [ ] Probar en pantalla pequeña (320dp)
- [ ] Probar en pantalla grande (420dp+)

**Comentarios:**
```
[Notas sobre testing visual]
```

---

## Fase 6: Testing Automatizado (Día 5-6)

### Unit Tests

- [ ] Crear `BlockingViewModelTest.kt`
- [ ] Test: `initial_state_has_default_values`
- [ ] Test: `loads_active_profile_correctly`
- [ ] Test: `updates_focused_time_today`
- [ ] Test: `handles_back_to_home`
- [ ] Test: `handles_emergency_access`
- [ ] Test: `handles_scan_nfc`
- [ ] Coverage > 80%

**Comando:** `./gradlew testDebugUnitTest`

**Comentarios:**
```
[Resultados de tests]
```

---

### UI Tests

- [ ] Crear `BlockingScreenTest.kt`
- [ ] Test: `normalMode_showsCorrectButtons`
- [ ] Test: `strictMode_showsNfcButton`
- [ ] Test: `withTimer_showsTimerCard`
- [ ] Test: `breathingAnimation_isVisible`
- [ ] Test: `motivationalCard_displaysMessage`
- [ ] Test: `statsCard_showsFocusedTime`
- [ ] Test: `profileCard_showsProfileName`

**Comando:** `./gradlew connectedAndroidTest`

**Comentarios:**
```
[Resultados de tests]
```

---

### Screenshot Tests (Opcional con Paparazzi)

- [ ] Setup Paparazzi dependency
- [ ] Crear `BlockingScreenScreenshotTest.kt`
- [ ] Snapshot: Normal mode light
- [ ] Snapshot: Strict mode light
- [ ] Snapshot: With timer
- [ ] Snapshot: Dark mode
- [ ] Verificar diffs

**Comando:** `./gradlew recordPaparazziDebug`

**Comentarios:**
```
[Resultados de screenshots]
```

---

## Fase 7: Accesibilidad (Día 6-7)

### Accessibility Scanner

- [ ] Instalar Accessibility Scanner app
- [ ] Escanear pantalla de bloqueo
- [ ] Verificar 0 errores
- [ ] Fix any warnings (si aplica)

**Comentarios:**
```
[Resultados de scanner]
```

---

### Manual Accessibility Testing

- [ ] TalkBack ON: navegar pantalla completa
- [ ] Verificar orden de navegación lógico
- [ ] Content descriptions hacen sentido
- [ ] Touch targets > 44dp (visual check)
- [ ] Contrast checker: textos legibles
- [ ] Reduced motion: animaciones se respetan

**Comentarios:**
```
[Notas sobre accesibilidad]
```

---

## Fase 8: Performance (Día 7)

### Profiling

- [ ] Abrir Android Studio Profiler
- [ ] Medir CPU durante animaciones
- [ ] Medir memoria en idle
- [ ] Medir frame time (target: <16ms)
- [ ] Verificar sin frame drops (>1%)
- [ ] Verificar battery impact (<2% diario)

**Comandos:**
```bash
adb shell dumpsys gfxinfo com.umbral.app
adb shell dumpsys batterystats com.umbral.app
```

**Comentarios:**
```
[Resultados de profiling]
```

---

### Optimización (si necesario)

- [ ] Revisar recomposiciones innecesarias
- [ ] Verificar `remember` y `derivedStateOf`
- [ ] Cancelar animaciones en onDispose
- [ ] Verificar no hay memory leaks

**Comentarios:**
```
[Optimizaciones aplicadas]
```

---

## Fase 9: Edge Cases & Polish (Día 8)

### Edge Cases

- [ ] Qué pasa si no hay perfil activo?
- [ ] Qué pasa si stats son 0?
- [ ] Qué pasa si timer es null?
- [ ] Qué pasa en landscape mode?
- [ ] Qué pasa con font scaling (large text)?
- [ ] Qué pasa con animator duration scale 0?

**Comentarios:**
```
[Casos encontrados y fixes]
```

---

### Polish

- [ ] Haptic feedback funciona?
- [ ] Transiciones suaves?
- [ ] Loading states (si aplica)
- [ ] Error states (si aplica)
- [ ] Empty states (si aplica)

**Comentarios:**
```
[Ajustes finales]
```

---

## Fase 10: Code Review & Documentation (Día 9)

### Code Review

- [ ] Self-review completo
- [ ] Clean code principles
- [ ] No hardcoded values
- [ ] Comments donde necesario
- [ ] No warnings en build
- [ ] No TODOs pendientes

**Comentarios:**
```
[Issues encontrados en review]
```

---

### Documentation

- [ ] KDoc en public functions
- [ ] README.md del módulo (si aplica)
- [ ] CHANGELOG entry
- [ ] Update docs/development/current/

**Comentarios:**
```
[Documentación agregada]
```

---

## Fase 11: Pre-Release (Día 10)

### Final Verification

- [ ] Build release APK exitoso
- [ ] ProGuard rules (si aplica)
- [ ] Signing config OK
- [ ] Version code incrementado
- [ ] All tests passing (green)

**Comandos:**
```bash
./gradlew assembleRelease
./gradlew testReleaseUnitTest
./gradlew lintRelease
```

**Comentarios:**
```
[Build results]
```

---

### QA Testing

- [ ] Install en dispositivo físico
- [ ] Probar flujo completo:
  - [ ] Activar perfil
  - [ ] Intentar abrir app bloqueada
  - [ ] Ver pantalla de bloqueo
  - [ ] Volver al inicio
  - [ ] Modo estricto (si implementado)
  - [ ] Emergency access (si implementado)

**Comentarios:**
```
[Issues encontrados en QA]
```

---

## Fase 12: Launch (Día 10-11)

### Merge to Develop

- [ ] Create Pull Request
- [ ] Description completa
- [ ] Screenshots incluidos
- [ ] Link a documentación
- [ ] Peer review aprobado
- [ ] CI/CD passing
- [ ] Merge to develop

**Comentarios:**
```
[PR link y notas]
```

---

### Release

- [ ] Merge develop → main
- [ ] Tag version (ej: v1.1.0)
- [ ] Generate release notes
- [ ] Upload to Play Store (internal track)
- [ ] Monitor crash reports
- [ ] Monitor user feedback

**Comentarios:**
```
[Release notes y monitoring]
```

---

## Post-Launch Monitoring (Semana 2-4)

### Analytics (si implementado)

- [ ] Tasa de "volver al inicio": ____%
- [ ] Tiempo promedio en pantalla: ____s
- [ ] Uso de modo estricto: ____%
- [ ] Crash rate: ____%
- [ ] User ratings: ____/5

**Comentarios:**
```
[Metrics y insights]
```

---

### Iteration

- [ ] Revisar feedback de usuarios
- [ ] Identificar mejoras
- [ ] Priorizar siguiente iteración
- [ ] Update backlog

**Comentarios:**
```
[Feedback y próximos pasos]
```

---

## Notas Generales

### Blockers Encontrados
```
[Agregar blockers aquí con fecha]
- 2026-01-XX: [Descripción del blocker]
```

### Decisiones Técnicas
```
[Agregar decisiones técnicas no documentadas]
- 2026-01-XX: [Decisión tomada y razón]
```

### Aprendizajes
```
[Agregar aprendizajes del proceso]
- [Lección aprendida]
```

---

## Sign-off

### Developer
- [ ] Implementación completa
- [ ] Tests passing
- [ ] Code reviewed

**Nombre:** _______________
**Fecha:** _______________

### QA
- [ ] Testing completo
- [ ] No blocking issues
- [ ] Performance aceptable

**Nombre:** _______________
**Fecha:** _______________

### Product Owner
- [ ] Cumple specs
- [ ] UX aprobada
- [ ] Lista para release

**Nombre:** _______________
**Fecha:** _______________

---

## Resumen de Progreso

**Fecha inicio:** _______________
**Fecha fin:** _______________
**Días totales:** _______________

**Líneas de código:**
- Producción: _____
- Tests: _____
- Total: _____

**Tests:**
- Unit tests: _____
- UI tests: _____
- Coverage: _____%

**Issues:**
- Bugs encontrados: _____
- Bugs fixed: _____
- Pendientes: _____

---

**Última actualización:** [Fecha]
**Actualizado por:** [Nombre]
