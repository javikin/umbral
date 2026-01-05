# Task #23: Fix Lista de Apps Vacía - COMPLETED

**Date:** 2026-01-04
**Status:** ✅ Completed

## Problem
El selector de apps no mostraba ninguna aplicación instalada en Android 16 (API 36) debido a restricciones de Package Visibility introducidas en Android 11 (API 30).

## Root Cause
Desde Android 11+, las apps necesitan declarar explícitamente qué paquetes pueden consultar mediante el elemento `<queries>` en el AndroidManifest. Sin esta declaración, `queryIntentActivities()` retorna una lista vacía.

## Changes Made

### 1. AndroidManifest.xml
**File:** `/app/src/main/AndroidManifest.xml`

**Added:**
```xml
<!-- Package Visibility (Android 11+) - Required to query installed apps -->
<queries>
    <intent>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent>
</queries>
```

**Location:** Before the `<application>` tag, after permissions

**Why:** Este `<queries>` declara que la app necesita consultar todas las apps instaladas que tienen un launcher (apps que el usuario puede abrir).

### 2. InstalledAppsProviderImpl.kt
**File:** `/app/src/main/java/com/umbral/data/apps/InstalledAppsProviderImpl.kt`

**Changes:**

#### a) Added imports:
```kotlin
import android.content.pm.PackageManager.ResolveInfoFlags
import android.os.Build
```

#### b) Updated `getLaunchableApps()` method:

**Before:**
```kotlin
val resolveInfos = packageManager.queryIntentActivities(mainIntent, 0)
```

**After:**
```kotlin
// Use proper flag for Android 11+ (API 30+) to query all apps
val resolveInfos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    // Android 13+ (API 33+)
    packageManager.queryIntentActivities(
        mainIntent,
        ResolveInfoFlags.of(PackageManager.MATCH_ALL.toLong())
    )
} else {
    // Android 11-12 (API 30-32)
    @Suppress("DEPRECATION")
    packageManager.queryIntentActivities(
        mainIntent,
        PackageManager.MATCH_ALL
    )
}
```

**Why:** 
- `PackageManager.MATCH_ALL` es necesario para obtener TODAS las apps que coinciden con el intent
- Android 13+ requiere usar `ResolveInfoFlags` en lugar del flag int directo
- Manejo de compatibilidad con versiones anteriores

#### c) Added debug logging:
```kotlin
Timber.d("Starting to query installed apps (includeSystemApps: $includeSystemApps)")
Timber.d("Found ${resolveInfos.size} launchable apps")
Timber.d("Returning ${apps.size} apps after filtering...")
Timber.w(e, "Failed to load icon for $packageName") // For icon loading errors
```

**Why:** Facilita el debugging futuro y permite verificar que las apps se están cargando correctamente.

## Technical Details

### Package Visibility Restrictions (Android 11+)
- **API Level:** 30 (Android 11) and above
- **Reason:** Privacy - apps shouldn't be able to enumerate all installed apps without permission
- **Solution:** Declare queries in manifest + use MATCH_ALL flag

### API Compatibility
- **Android 10 and below:** No restrictions, flag `0` works
- **Android 11-12 (API 30-32):** Requires `<queries>` + `PackageManager.MATCH_ALL` (int)
- **Android 13+ (API 33+):** Requires `<queries>` + `ResolveInfoFlags.of(PackageManager.MATCH_ALL.toLong())`

### Files Not Modified
- ✅ `AppSelectorViewModel.kt` - Already correct, uses `InstalledAppsProvider` properly
- ✅ `InstalledAppsRepository.kt` - Interface unchanged
- ✅ `InstalledAppsRepositoryImpl.kt` - Implementation already delegates to provider correctly

## Testing Checklist

To verify the fix works:

1. **Build the app:**
   ```bash
   ./gradlew assembleDebug
   ```

2. **Install on Android 11+ device:**
   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

3. **Test app selector:**
   - Open Umbral app
   - Go to "Crear Perfil" or "Editar Perfil"
   - Tap "Seleccionar apps"
   - **Expected:** List of installed apps should appear
   - **Expected:** Toggle "Mostrar apps del sistema" should show/hide system apps

4. **Check logs:**
   ```bash
   adb logcat | grep -E "(InstalledAppsProvider|AppSelector)"
   ```
   - Should see: "Found X launchable apps"
   - Should see: "Returning Y apps after filtering"

## Known Limitations

- **System apps filtering:** Some manufacturer apps might still be marked as system apps
- **Essential apps:** Umbral itself, Settings, Phone, Contacts are excluded from blocking
- **Icon loading:** If an app's icon fails to load, it will show null (handled gracefully in UI)

## References

- [Android Package Visibility Docs](https://developer.android.com/training/package-visibility)
- [Queries Element Reference](https://developer.android.com/guide/topics/manifest/queries-element)
- [PackageManager.MATCH_ALL](https://developer.android.com/reference/android/content/pm/PackageManager#MATCH_ALL)

---

**Completed by:** Claude Sonnet 4.5
**Reviewed:** Pending user testing
