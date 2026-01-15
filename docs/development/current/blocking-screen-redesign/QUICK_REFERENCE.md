# Quick Reference - Blocking Screen

**Para consulta rápida durante implementación**

---

## 📋 Archivos Importantes

| Archivo | Ubicación | Propósito |
|---------|-----------|-----------|
| `SUMMARY.md` | Este dir | Resumen ejecutivo |
| `BlockingScreen.kt` | Este dir | Código fuente completo |
| `ImplementationGuide.md` | Este dir | Guía paso a paso |
| `IMPLEMENTATION_CHECKLIST.md` | Este dir | Checklist de tareas |

---

## 🎨 Paleta de Colores (Copy-Paste Ready)

### Light Theme
```kotlin
val FocusSky = Color(0xFFE8F4F8)
val DeepFocus = Color(0xFF0A4D68)
val FocusLeaf = Color(0xFF4CAF50)
val FocusAmber = Color(0xFFFFA726)
val FocusSurface = Color(0xFFFFFBFE)
val FocusSurfaceVariant = Color(0xFFE7F2F5)
```

### Dark Theme
```kotlin
val NightSky = Color(0xFF0D1B2A)
val MoonGlow = Color(0xFF415A77)
val NightLeaf = Color(0xFF66BB6A)
val StarLight = Color(0xFFE0E1DD)
val DarkSurface = Color(0xFF1B263B)
val DarkSurfaceVariant = Color(0xFF415A77)
```

---

## ⚡ Animaciones (Copy-Paste Ready)

### Breathing Icon
```kotlin
val scale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.15f,
    animationSpec = infiniteRepeatable(
        animation = tween(2000, easing = EaseInOutCubic),
        repeatMode = RepeatMode.Reverse
    )
)
```

### Crossfade Messages
```kotlin
AnimatedContent(
    targetState = currentMessage,
    transitionSpec = {
        fadeIn(tween(300)) togetherWith fadeOut(tween(300))
    }
)
```

### Spring Button
```kotlin
val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.95f else 1f,
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
)
```

---

## 📦 Dependencias (build.gradle.kts)

```kotlin
dependencies {
    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.01.00"))

    // Material 3
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.compose.material:material-icons-extended")

    // Animation
    implementation("androidx.compose.animation:animation")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.6")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-compiler:2.50")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
}
```

---

## 🎯 Estados (BlockingState)

```kotlin
data class BlockingState(
    val profileName: String,
    val isStrictMode: Boolean = false,
    val timerMinutesRemaining: Int? = null,
    val focusedTimeToday: String = "0h 0min",
    val isFirstTime: Boolean = false
)
```

---

## 💬 Mensajes Motivacionales (Array)

```kotlin
val motivationalMessages = listOf(
    MotivationalMessage("Estás eligiendo conscientemente tu tiempo", Icons.Outlined.SelfImprovement),
    MotivationalMessage("Tu yo futuro te lo agradecerá", Icons.Outlined.EmojiObjects),
    MotivationalMessage("Pequeñas decisiones, grandes cambios", Icons.Outlined.TrendingUp),
    MotivationalMessage("Estás construyendo un mejor hábito", Icons.Outlined.Stars),
    MotivationalMessage("El control es tuyo", Icons.Outlined.Shield),
    MotivationalMessage("Cada momento cuenta", Icons.Outlined.Timer),
    MotivationalMessage("Tu atención es valiosa", Icons.Outlined.Diamond),
    MotivationalMessage("Enfócate en lo que importa", Icons.Outlined.Favorite),
    MotivationalMessage("Estás presente, estás aquí", Icons.Outlined.WbSunny),
    MotivationalMessage("Tu bienestar primero", Icons.Outlined.Spa),
    MotivationalMessage("Eligiendo calma sobre caos", Icons.Outlined.Waves),
    MotivationalMessage("Tu concentración merece protección", Icons.Outlined.Security)
)
```

---

## 🧪 Testing Commands

```bash
# Unit tests
./gradlew testDebugUnitTest

# UI tests (connected device)
./gradlew connectedAndroidTest

# Lint check
./gradlew lintDebug

# Build release
./gradlew assembleRelease

# Screenshot tests (if Paparazzi)
./gradlew recordPaparazziDebug
./gradlew verifyPaparazziDebug
```

---

## 🔍 Debugging

### Ver frame rate
```bash
adb shell dumpsys gfxinfo com.umbral.app
```

### Ver battery usage
```bash
adb shell dumpsys batterystats com.umbral.app
```

### Ver recompositions (Compose)
```kotlin
// Add in BlockingScreen.kt temporarily
LogCompositions(tag = "BlockingScreen")
```

---

## 📐 Tamaños Importantes

| Elemento | Tamaño |
|----------|--------|
| Breathing Icon | 120dp |
| Touch targets | 48dp mínimo |
| Button height | 56dp |
| Card padding | 16-20dp |
| Spacing between elements | 24dp |
| Screen padding | 24dp |

---

## 🎨 Tipografía

| Uso | Style | Size | Weight |
|-----|-------|------|--------|
| Título principal | headlineSmall | 24sp | Medium |
| Mensaje motivacional | bodyLarge | 18sp | Regular |
| Stats/Info | bodyMedium | 16sp | Regular |
| Botones | labelLarge | 14sp | Medium |

---

## ♿ Accesibilidad Checklist

- [ ] Content descriptions en todos los iconos
- [ ] Touch targets > 48dp
- [ ] Contrast ratio > 4.5:1
- [ ] Funciona con TalkBack
- [ ] Respeta reduced motion
- [ ] Semantic properties correctas

---

## 🐛 Issues Comunes y Soluciones

### Issue 1: Animación no se ve fluida
**Solución:** Verificar overdraw con GPU profiler
```bash
adb shell setprop debug.hwui.overdraw show
```

### Issue 2: Mensajes no rotan
**Solución:** Verificar LaunchedEffect key
```kotlin
LaunchedEffect(Unit) { // Key debe ser Unit para infinite loop
    while (true) {
        delay(8000)
        // rotate
    }
}
```

### Issue 3: Dynamic colors no funcionan
**Solución:** Verificar Android version
```kotlin
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    dynamicLightColorScheme(context)
} else {
    LightColorScheme // Fallback
}
```

### Issue 4: Haptic feedback no funciona
**Solución:** Wrap en try-catch
```kotlin
try {
    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
} catch (e: Exception) {
    // Dispositivo sin soporte, ignorar
}
```

---

## 📱 Testing Devices Recomendados

| Device | Android | Screen | Propósito |
|--------|---------|--------|-----------|
| Pixel 5 | 12+ | 1080x2340 | Material You |
| Samsung S20 | 11 | 1440x3200 | Large screen |
| Moto G | 10 | 720x1600 | Budget device |
| Emulator | 9 | 320dp wide | Min width |

---

## 🚀 Shortcuts Útiles

### Android Studio
- `Ctrl+Shift+A` - Find action
- `Ctrl+B` - Go to declaration
- `Alt+Enter` - Quick fix
- `Ctrl+Alt+L` - Reformat code
- `Ctrl+/` - Comment line

### Compose Preview
- `Build > Refresh Layout Preview`
- `Tools > Layout Inspector`
- `View > Tool Windows > Preview`

---

## 📞 Contactos / Referencias

| Recurso | Link |
|---------|------|
| Material 3 Guidelines | https://m3.material.io/ |
| Compose Docs | https://developer.android.com/jetpack/compose |
| Accessibility Guide | https://developer.android.com/guide/topics/ui/accessibility |
| Hilt Documentation | https://dagger.dev/hilt/ |

---

## 🎯 Next Steps Quick View

1. Setup dependencies ✅
2. Copy files to project ✅
3. Create ViewModel 🔄
4. Integrate navigation 🔄
5. Test previews ⏳
6. Write tests ⏳
7. Accessibility check ⏳
8. Performance profiling ⏳
9. Code review ⏳
10. Release ⏳

---

**Última actualización:** 2026-01-15
**Mantener este documento actualizado durante implementación**
