# Glance Widgets - Umbral

Implementación de widgets para la pantalla de inicio usando Jetpack Glance.

## Widgets Implementados

### 1. StatusWidget (2x1)
- **Tamaño:** Small (2x1 celdas)
- **Función:** Muestra si el bloqueo está activo o inactivo
- **Contenido:**
  - Icono de candado (cerrado/abierto)
  - Texto: "Activo" / "Inactivo"
  - Nombre del perfil activo (si aplica)
- **Acción:** Click abre la app principal

### 2. StatsWidget (4x2)
- **Tamaño:** Medium (4x2 celdas)
- **Función:** Muestra estadísticas de uso
- **Contenido:**
  - Apps bloqueadas hoy
  - Racha actual (días)
  - Tiempo ahorrado
- **Acción:** Click abre la pantalla de estadísticas

### 3. QuickToggleWidget (2x2)
- **Tamaño:** Small (2x2 celdas)
- **Función:** Botón rápido para activar/desactivar bloqueo
- **Contenido:**
  - Botón grande con icono
  - Estado ON/OFF
  - Nombre del perfil activo (si aplica)
- **Acción:** Click hace toggle del bloqueo

## Estructura de Archivos

```
widget/
├── StatusWidget.kt           - Widget de estado
├── StatsWidget.kt            - Widget de estadísticas
├── QuickToggleWidget.kt      - Widget de toggle rápido
├── WidgetReceiver.kt         - Receivers para cada widget
├── WidgetState.kt            - Estado compartido
├── WidgetUpdater.kt          - Helper para actualizar widgets
├── action/
│   └── WidgetActions.kt      - Acciones de los widgets
└── README.md                 - Este archivo
```

## Configuración XML

Cada widget tiene su configuración en `res/xml/`:
- `status_widget_info.xml`
- `stats_widget_info.xml`
- `quick_toggle_widget_info.xml`

Los receivers están registrados en `AndroidManifest.xml`.

## Uso

### Actualizar Widgets Cuando Cambie el Estado

Inyectar `WidgetUpdater` en `BlockingManager` (o donde se maneje el estado):

```kotlin
@Inject lateinit var widgetUpdater: WidgetUpdater

fun updateBlockingState() {
    // ... cambiar estado ...

    // Actualizar todos los widgets
    widgetUpdater.updateAllWidgets()
}
```

### Acciones Disponibles

- **OpenAppAction:** Abre la app principal
- **OpenStatsScreenAction:** Abre la pantalla de estadísticas
- **ToggleBlockingAction:** Hace toggle del bloqueo (callback async)

## Colores

Los widgets usan los colores del tema:
- **Activo:** `BlockingActive` (rojo `#E53935`)
- **Inactivo:** `BlockingInactive` (verde `#4CAF50`)
- **Superficie:** `GlanceTheme.colors.surface`
- **Texto:** `GlanceTheme.colors.onSurface`

## Iconos

- `ic_lock.xml` - Candado cerrado (bloqueo activo)
- `ic_lock_open.xml` - Candado abierto (bloqueo inactivo)

## Textos

Todos los textos están en `strings.xml` en español:
- `status_widget_description`
- `stats_widget_description`
- `quick_toggle_widget_description`

## Pendientes / TODOs

1. **StatsWidget:** Conectar con `StatsRepository` cuando esté implementado para mostrar datos reales
2. **QuickToggleWidget:** Implementar selección de perfil por defecto desde preferences
3. **WidgetUpdater:** Integrar en `BlockingManager` para actualizaciones automáticas
4. **Testing:** Agregar tests unitarios para acciones de widgets

## Integración con BlockingManager

Para que los widgets se actualicen automáticamente cuando cambie el estado de bloqueo:

```kotlin
@HiltViewModel
class BlockingManagerImpl @Inject constructor(
    private val widgetUpdater: WidgetUpdater,
    // ... otras dependencias
) : BlockingManager {

    override suspend fun startBlocking(profileId: String): Result<Unit> {
        // ... lógica de bloqueo ...

        // Actualizar widgets
        widgetUpdater.updateAllWidgets()

        return Result.success(Unit)
    }

    override suspend fun stopBlocking(requireNfc: Boolean): Result<Unit> {
        // ... lógica de desbloqueo ...

        // Actualizar widgets
        widgetUpdater.updateAllWidgets()

        return Result.success(Unit)
    }
}
```

## Notas Importantes

- Los widgets usan **Glance Compose**, no RemoteViews tradicional
- Requieren Glance 1.1.1+ (ya incluido en `build.gradle.kts`)
- Los widgets se actualizan cada 30 minutos por defecto (`updatePeriodMillis="1800000"`)
- Para actualizaciones inmediatas, usar `WidgetUpdater`
- Todos los textos visibles están en **español** según las reglas del proyecto
