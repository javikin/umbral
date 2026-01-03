# User Stories - Umbral

**Estado:** ✅ Completo
**Última actualización:** 2026-01-03T04:32:12Z

---

## Convenciones

### Formato de User Story
```
US-{ÉPICA}-{NÚMERO}: {Título}
Como {persona}
Quiero {acción}
Para {beneficio}

Criterios de Aceptación:
- [ ] {criterio 1}
- [ ] {criterio 2}

Prioridad: Alta/Media/Baja
Esfuerzo: S/M/L/XL
Fase: MVP/v1.1/Futuro
```

### Leyenda de Esfuerzo
| Tamaño | Descripción |
|--------|-------------|
| **S** | 1-2 días |
| **M** | 3-5 días |
| **L** | 1-2 semanas |
| **XL** | 2+ semanas |

---

## Épica 1: Onboarding y Configuración Inicial

### US-ONB-001: Ver pantalla de bienvenida
**Como** nuevo usuario
**Quiero** ver una pantalla de bienvenida que explique el concepto de Umbral
**Para** entender qué hace la app antes de configurarla

**Criterios de Aceptación:**
- [ ] Muestra animación/ilustración del concepto "umbral"
- [ ] Explica en 1-2 oraciones el propósito de la app
- [ ] Botón "Comenzar" para continuar
- [ ] Botón "Saltar" para usuarios que ya conocen la app

**Prioridad:** Alta
**Esfuerzo:** S
**Fase:** MVP

---

### US-ONB-002: Otorgar permisos necesarios
**Como** nuevo usuario
**Quiero** entender qué permisos necesita la app y por qué
**Para** sentirme seguro al otorgarlos

**Criterios de Aceptación:**
- [ ] Pantalla de explicación ANTES de cada permiso
- [ ] Explica en español sencillo qué hace cada permiso
- [ ] Muestra video/animación de la funcionalidad que habilita
- [ ] Permite continuar sin permisos opcionales
- [ ] Permisos requeridos: Usage Stats, Overlay, Notifications
- [ ] Permisos opcionales: Device Admin (para physical unlock)

**Prioridad:** Alta
**Esfuerzo:** M
**Fase:** MVP

---

### US-ONB-003: Configurar primer perfil de bloqueo
**Como** nuevo usuario
**Quiero** crear mi primer perfil de apps a bloquear
**Para** empezar a usar la app inmediatamente

**Criterios de Aceptación:**
- [ ] Muestra lista de apps instaladas con iconos
- [ ] Sugiere apps comunes para bloquear (Instagram, TikTok, Twitter, YouTube, Facebook)
- [ ] Permite buscar apps por nombre
- [ ] Muestra categorías de apps (Social, Games, News)
- [ ] Permite seleccionar múltiples apps con checkboxes
- [ ] Botón "Crear perfil" para finalizar

**Prioridad:** Alta
**Esfuerzo:** M
**Fase:** MVP

---

### US-ONB-004: Configurar primer tag NFC
**Como** nuevo usuario
**Quiero** configurar mi primer tag NFC
**Para** tener un trigger físico para activar el bloqueo

**Criterios de Aceptación:**
- [ ] Instrucciones visuales de cómo acercar el tag
- [ ] Animación de "esperando tag..."
- [ ] Feedback háptico y sonoro al detectar tag
- [ ] Permite nombrar el tag (ej: "Puerta Principal")
- [ ] Confirma escritura exitosa al tag
- [ ] Opción de saltar si no tiene NFC/tag

**Prioridad:** Alta
**Esfuerzo:** M
**Fase:** MVP

---

### US-ONB-005: Completar onboarding con test
**Como** nuevo usuario
**Quiero** probar que todo funciona antes de terminar el setup
**Para** tener confianza de que la app funciona

**Criterios de Aceptación:**
- [ ] Invita a hacer tap en el tag configurado
- [ ] Muestra estado "Bloqueando..." cuando funciona
- [ ] Permite desbloquear con otro tap para probar toggle
- [ ] Celebración visual al completar exitosamente
- [ ] Redirige a pantalla principal

**Prioridad:** Media
**Esfuerzo:** S
**Fase:** MVP

---

## Épica 2: Gestión de Perfiles

### US-PRF-001: Ver lista de perfiles
**Como** Marcos (profesional remoto)
**Quiero** ver todos mis perfiles de bloqueo
**Para** elegir cuál activar según mi contexto

**Criterios de Aceptación:**
- [ ] Lista con nombre, icono y descripción de cada perfil
- [ ] Indica cuál perfil está activo (si hay uno)
- [ ] Muestra cantidad de apps bloqueadas por perfil
- [ ] Ordenado por uso reciente
- [ ] FAB para crear nuevo perfil

**Prioridad:** Alta
**Esfuerzo:** S
**Fase:** MVP

---

### US-PRF-002: Crear nuevo perfil
**Como** Ana (emprendedora)
**Quiero** crear un perfil personalizado
**Para** tener diferentes configuraciones para trabajo y personal

**Criterios de Aceptación:**
- [ ] Campo de nombre (requerido, max 50 chars)
- [ ] Campo de descripción (opcional)
- [ ] Selector de icono (8-10 opciones: social, games, work, etc.)
- [ ] Selector de color accent
- [ ] Botón para agregar apps a bloquear
- [ ] Validación: nombre no puede estar vacío
- [ ] Guardar perfil y mostrar en lista

**Prioridad:** Alta
**Esfuerzo:** M
**Fase:** MVP

---

### US-PRF-003: Editar perfil existente
**Como** Sofía (estudiante)
**Quiero** modificar las apps bloqueadas en un perfil
**Para** ajustar según mis necesidades cambiantes

**Criterios de Aceptación:**
- [ ] Acceso desde detalle de perfil
- [ ] Permite cambiar nombre, descripción, icono, color
- [ ] Permite agregar/quitar apps del perfil
- [ ] No permite editar perfiles predefinidos (solo personalizar)
- [ ] Guardar cambios con confirmación

**Prioridad:** Media
**Esfuerzo:** S
**Fase:** MVP

---

### US-PRF-004: Eliminar perfil
**Como** usuario
**Quiero** eliminar un perfil que ya no uso
**Para** mantener mi lista de perfiles ordenada

**Criterios de Aceptación:**
- [ ] Botón de eliminar en detalle de perfil
- [ ] Confirmación: "¿Eliminar perfil? Esta acción no se puede deshacer"
- [ ] No permite eliminar perfil activo (primero desactivar)
- [ ] No permite eliminar perfiles predefinidos
- [ ] Actualiza lista después de eliminar

**Prioridad:** Baja
**Esfuerzo:** S
**Fase:** MVP

---

### US-PRF-005: Usar perfiles predefinidos
**Como** nuevo usuario
**Quiero** empezar con perfiles listos para usar
**Para** no tener que configurar todo desde cero

**Criterios de Aceptación:**
- [ ] Perfil "Redes Sociales" preconfigurado (Instagram, TikTok, Twitter, Facebook)
- [ ] Perfil "Juegos" preconfigurado (apps de categoría Games)
- [ ] Perfil "Noticias" preconfigurado (apps de categoría News)
- [ ] Perfiles predefinidos no se pueden eliminar pero sí personalizar
- [ ] Detecta automáticamente apps instaladas en cada categoría

**Prioridad:** Media
**Esfuerzo:** M
**Fase:** MVP

---

## Épica 3: Bloqueo de Apps

### US-BLK-001: Activar bloqueo con NFC
**Como** Carlos (padre de familia)
**Quiero** activar el bloqueo al hacer tap en mi tag NFC de la puerta
**Para** desconectarme automáticamente al llegar a casa

**Criterios de Aceptación:**
- [ ] Detecta tap de NFC en cualquier momento (foreground y background)
- [ ] Activa perfil asociado al tag (o el último usado si no hay asociación)
- [ ] Muestra notificación persistente "Bloqueo activo: {nombre perfil}"
- [ ] Vibración y sonido de confirmación
- [ ] Funciona con pantalla apagada (Android 10+)

**Prioridad:** Alta
**Esfuerzo:** L
**Fase:** MVP

---

### US-BLK-002: Desactivar bloqueo con NFC
**Como** Marcos (profesional)
**Quiero** desactivar el bloqueo al volver a hacer tap en el tag
**Para** tener acceso completo a mis apps cuando lo necesite

**Criterios de Aceptación:**
- [ ] Segundo tap en el mismo tag desactiva el bloqueo
- [ ] Opción de requerir el MISMO tag para desbloquear
- [ ] Muestra notificación "Bloqueo desactivado"
- [ ] Actualiza widget si está en home screen
- [ ] Registra sesión terminada para estadísticas

**Prioridad:** Alta
**Esfuerzo:** S
**Fase:** MVP

---

### US-BLK-003: Activar bloqueo con QR
**Como** Sofía (estudiante)
**Quiero** activar el bloqueo escaneando un código QR
**Para** tener una alternativa si no tengo tag NFC disponible

**Criterios de Aceptación:**
- [ ] Escanear QR desde la app activa bloqueo
- [ ] QR puede estar impreso o en otra pantalla
- [ ] Mismo comportamiento que NFC (toggle on/off)
- [ ] Genera QR único para cada perfil
- [ ] Permite imprimir QR desde la app

**Prioridad:** Alta
**Esfuerzo:** M
**Fase:** MVP

---

### US-BLK-004: Ver overlay cuando intento abrir app bloqueada
**Como** usuario
**Quiero** ver una pantalla de bloqueo cuando intento abrir una app bloqueada
**Para** recordar que estoy en modo focus

**Criterios de Aceptación:**
- [ ] Overlay cubre la pantalla completa
- [ ] Muestra nombre del perfil activo
- [ ] Muestra tiempo restante (si hay timer)
- [ ] Muestra mensaje motivacional aleatorio
- [ ] Botón "Volver" para regresar a home
- [ ] No permite cerrar el overlay tocando afuera
- [ ] Registra intento de abrir app bloqueada

**Prioridad:** Alta
**Esfuerzo:** M
**Fase:** MVP

---

### US-BLK-005: Activar bloqueo manual desde app
**Como** usuario
**Quiero** activar el bloqueo desde la app sin usar NFC/QR
**Para** tener flexibilidad cuando no tengo el tag cerca

**Criterios de Aceptación:**
- [ ] Botón grande "Activar" en pantalla principal
- [ ] Permite seleccionar qué perfil activar
- [ ] Mismo comportamiento que NFC/QR
- [ ] Opción de configurar timer al activar manualmente

**Prioridad:** Media
**Esfuerzo:** S
**Fase:** MVP

---

### US-BLK-006: Configurar timer de auto-desbloqueo
**Como** Ana (emprendedora)
**Quiero** configurar un timer para que el bloqueo se desactive automáticamente
**Para** tener acceso a mis apps después de mi sesión de focus

**Criterios de Aceptación:**
- [ ] Opción de timer en configuración de perfil
- [ ] Duraciones preconfiguradas: 30min, 1h, 2h, 4h
- [ ] Opción de tiempo personalizado
- [ ] Muestra countdown en notificación
- [ ] Notificación 5 min antes de desbloquear
- [ ] Desbloquea automáticamente al terminar timer

**Prioridad:** Media
**Esfuerzo:** M
**Fase:** MVP

---

### US-BLK-007: Requerir tag físico para desbloquear
**Como** usuario que quiere máxima disciplina
**Quiero** que SOLO el tag físico pueda desbloquear (no manual)
**Para** evitar hacer trampa y desbloquear fácilmente

**Criterios de Aceptación:**
- [ ] Opción "Requerir tag para desbloquear" en configuración de perfil
- [ ] Si activado, no muestra botón de desbloqueo manual
- [ ] Muestra mensaje "Usa tu tag NFC para desbloquear"
- [ ] Warning claro al activar: "Solo podrás desbloquear con el tag físico"
- [ ] Puede desactivarse desde configuración (con confirmación)

**Prioridad:** Baja
**Esfuerzo:** S
**Fase:** v1.1

---

## Épica 4: Gestión de Apps

### US-APP-001: Ver apps instaladas
**Como** usuario
**Quiero** ver todas las apps instaladas en mi dispositivo
**Para** elegir cuáles agregar a un perfil de bloqueo

**Criterios de Aceptación:**
- [ ] Lista de apps con icono, nombre y categoría
- [ ] Ordenado alfabéticamente por default
- [ ] Opción de ordenar por categoría
- [ ] Barra de búsqueda para filtrar
- [ ] No muestra apps del sistema ocultas
- [ ] Marca visualmente apps ya en el perfil actual

**Prioridad:** Alta
**Esfuerzo:** S
**Fase:** MVP

---

### US-APP-002: Agregar app a perfil
**Como** usuario
**Quiero** agregar una app específica a mi perfil de bloqueo
**Para** personalizarlo según mis necesidades

**Criterios de Aceptación:**
- [ ] Tap en app la agrega/quita del perfil (checkbox)
- [ ] Permite selección múltiple
- [ ] Feedback visual inmediato
- [ ] Botón "Guardar" para confirmar cambios
- [ ] Muestra contador de apps seleccionadas

**Prioridad:** Alta
**Esfuerzo:** S
**Fase:** MVP

---

### US-APP-003: Configurar whitelist
**Como** Carlos (padre de familia)
**Quiero** tener ciertas apps siempre disponibles (teléfono, banco, mensajes)
**Para** no perder comunicación importante

**Criterios de Aceptación:**
- [ ] Sección "Apps siempre permitidas" en configuración
- [ ] Apps del sistema preseleccionadas (Phone, Messages, Camera, Calendar)
- [ ] Permite agregar/quitar apps de la whitelist
- [ ] Apps en whitelist nunca se bloquean, en ningún perfil
- [ ] Warning si intenta agregar app distractora a whitelist

**Prioridad:** Alta
**Esfuerzo:** M
**Fase:** MVP

---

### US-APP-004: Sugerir apps por categoría
**Como** nuevo usuario
**Quiero** que la app me sugiera qué apps bloquear
**Para** no tener que revisar todas mis apps manualmente

**Criterios de Aceptación:**
- [ ] Detecta categoría de apps desde Play Store metadata
- [ ] Sugiere automáticamente apps de categoría "Social"
- [ ] Sugiere automáticamente apps de categoría "Games"
- [ ] Permite aceptar/rechazar sugerencias
- [ ] Aprende de selecciones del usuario

**Prioridad:** Media
**Esfuerzo:** M
**Fase:** v1.1

---

## Épica 5: Gestión de Tags NFC

### US-NFC-001: Agregar nuevo tag
**Como** usuario
**Quiero** agregar un nuevo tag NFC a mi configuración
**Para** tener múltiples puntos de activación (casa, oficina, etc.)

**Criterios de Aceptación:**
- [ ] Pantalla de "Acerca el tag al teléfono"
- [ ] Detecta cualquier tag NTAG213/215/216
- [ ] Permite nombrar el tag
- [ ] Permite asignar ubicación (opcional)
- [ ] Permite asociar a un perfil específico
- [ ] Escribe mensaje NDEF con identificador Umbral

**Prioridad:** Alta
**Esfuerzo:** M
**Fase:** MVP

---

### US-NFC-002: Ver lista de tags
**Como** usuario con múltiples tags
**Quiero** ver todos mis tags configurados
**Para** gestionarlos y ver cuáles tengo

**Criterios de Aceptación:**
- [ ] Lista con nombre, ubicación y perfil asociado
- [ ] Muestra fecha de último uso
- [ ] Muestra veces usado
- [ ] Permite editar/eliminar cada tag
- [ ] FAB para agregar nuevo tag

**Prioridad:** Media
**Esfuerzo:** S
**Fase:** MVP

---

### US-NFC-003: Editar tag existente
**Como** usuario
**Quiero** cambiar el nombre o perfil asociado a un tag
**Para** mantener mi configuración actualizada

**Criterios de Aceptación:**
- [ ] Acceso desde lista de tags
- [ ] Permite cambiar nombre y ubicación
- [ ] Permite cambiar perfil asociado
- [ ] NO requiere re-escribir el tag físico
- [ ] Guardar cambios con confirmación

**Prioridad:** Baja
**Esfuerzo:** S
**Fase:** MVP

---

### US-NFC-004: Eliminar tag
**Como** usuario
**Quiero** eliminar un tag que ya no uso
**Para** mantener mi lista ordenada

**Criterios de Aceptación:**
- [ ] Botón de eliminar en detalle de tag
- [ ] Confirmación antes de eliminar
- [ ] Tag físico sigue funcionando (app lo trata como nuevo)
- [ ] Actualiza lista después de eliminar

**Prioridad:** Baja
**Esfuerzo:** S
**Fase:** MVP

---

### US-NFC-005: Usar tag no registrado
**Como** usuario
**Quiero** que un tag nuevo active el perfil activo o me pida configurarlo
**Para** tener flexibilidad con cualquier tag NFC

**Criterios de Aceptación:**
- [ ] Al detectar tag desconocido, ofrece configurarlo
- [ ] Opción de usar temporalmente sin registrar
- [ ] Si hay perfil activo, hace toggle
- [ ] Si no hay perfil activo, pide seleccionar uno

**Prioridad:** Media
**Esfuerzo:** S
**Fase:** v1.1

---

## Épica 6: Estadísticas y Tracking

### US-STA-001: Ver resumen de uso
**Como** Marcos (profesional)
**Quiero** ver cuánto tiempo he estado en modo focus
**Para** medir mi progreso y mantener motivación

**Criterios de Aceptación:**
- [ ] Total de tiempo bloqueado hoy/semana/mes
- [ ] Cantidad de sesiones completadas
- [ ] Promedio de duración de sesiones
- [ ] Gráfica de barras con últimos 7 días
- [ ] Compara con semana anterior

**Prioridad:** Media
**Esfuerzo:** M
**Fase:** MVP

---

### US-STA-002: Ver racha actual
**Como** Sofía (estudiante)
**Quiero** ver mi racha de días consecutivos usando Umbral
**Para** mantener mi motivación y no romper la racha

**Criterios de Aceptación:**
- [ ] Muestra días consecutivos de uso
- [ ] Define "día de uso" como mínimo 1 sesión de bloqueo
- [ ] Muestra racha más larga histórica
- [ ] Celebración visual al superar récord
- [ ] Notificación si está por perder la racha

**Prioridad:** Media
**Esfuerzo:** S
**Fase:** MVP

---

### US-STA-003: Ver apps más bloqueadas
**Como** usuario
**Quiero** ver qué apps intento abrir más cuando estoy bloqueado
**Para** entender mis patrones de distracción

**Criterios de Aceptación:**
- [ ] Lista de apps ordenada por intentos de apertura
- [ ] Muestra icono, nombre y cantidad de intentos
- [ ] Filtra por período (hoy/semana/mes)
- [ ] Insight: "Instagram es tu mayor distracción"

**Prioridad:** Baja
**Esfuerzo:** M
**Fase:** v1.1

---

### US-STA-004: Ver historial de sesiones
**Como** usuario
**Quiero** ver el historial de mis sesiones de bloqueo
**Para** revisar mi uso pasado

**Criterios de Aceptación:**
- [ ] Lista cronológica de sesiones
- [ ] Muestra fecha, hora inicio, duración
- [ ] Muestra perfil usado y método de activación (NFC/QR/Manual)
- [ ] Muestra intentos de abrir apps bloqueadas
- [ ] Permite filtrar por perfil

**Prioridad:** Baja
**Esfuerzo:** S
**Fase:** v1.1

---

## Épica 7: Widgets

### US-WDG-001: Widget de estado
**Como** usuario
**Quiero** tener un widget que muestre si el bloqueo está activo
**Para** ver el estado de un vistazo sin abrir la app

**Criterios de Aceptación:**
- [ ] Widget 2x1 para home screen
- [ ] Muestra "Activo" o "Inactivo"
- [ ] Si activo, muestra nombre del perfil
- [ ] Si activo con timer, muestra countdown
- [ ] Tap en widget abre la app

**Prioridad:** Media
**Esfuerzo:** M
**Fase:** MVP

---

### US-WDG-002: Widget de quick toggle
**Como** Ana (emprendedora)
**Quiero** tener un widget para activar/desactivar bloqueo rápidamente
**Para** no tener que abrir la app cada vez

**Criterios de Aceptación:**
- [ ] Widget 2x2 con botón grande de toggle
- [ ] Muestra estado actual
- [ ] Tap activa/desactiva bloqueo
- [ ] Usa el último perfil usado
- [ ] Long-press permite seleccionar perfil

**Prioridad:** Media
**Esfuerzo:** M
**Fase:** MVP

---

### US-WDG-003: Widget de timer/countdown
**Como** usuario con timer activo
**Quiero** ver el tiempo restante de mi sesión
**Para** saber cuándo se desbloqueará automáticamente

**Criterios de Aceptación:**
- [ ] Widget 2x1 con countdown
- [ ] Actualiza en tiempo real
- [ ] Muestra "∞" si no hay timer
- [ ] Muestra nombre del perfil
- [ ] Cambia de color cuando quedan <5 min

**Prioridad:** Baja
**Esfuerzo:** M
**Fase:** v1.1

---

## Épica 8: Configuración y Preferencias

### US-CFG-001: Configurar notificaciones
**Como** usuario
**Quiero** controlar qué notificaciones recibo
**Para** no ser molestado innecesariamente

**Criterios de Aceptación:**
- [ ] Toggle para notificación persistente durante bloqueo
- [ ] Toggle para notificación de "intento de abrir app"
- [ ] Toggle para notificación de timer próximo a terminar
- [ ] Toggle para notificación de racha en peligro
- [ ] Preview de cómo se ve cada notificación

**Prioridad:** Media
**Esfuerzo:** S
**Fase:** MVP

---

### US-CFG-002: Configurar tema oscuro
**Como** usuario
**Quiero** poder usar la app en modo oscuro
**Para** reducir fatiga visual de noche

**Criterios de Aceptación:**
- [ ] Opción: Claro / Oscuro / Sistema
- [ ] Respeta configuración del sistema por default
- [ ] Aplica inmediatamente sin reiniciar
- [ ] Widgets también respetan el tema

**Prioridad:** Media
**Esfuerzo:** S
**Fase:** MVP

---

### US-CFG-003: Configurar sonido y vibración
**Como** usuario
**Quiero** controlar los sonidos y vibraciones de la app
**Para** personalizarlo a mis preferencias

**Criterios de Aceptación:**
- [ ] Toggle para sonido al activar/desactivar bloqueo
- [ ] Toggle para vibración al detectar NFC
- [ ] Toggle para sonido de overlay cuando intento abrir app
- [ ] Respeta modo silencioso del dispositivo

**Prioridad:** Baja
**Esfuerzo:** S
**Fase:** MVP

---

### US-CFG-004: Exportar/Importar configuración
**Como** usuario
**Quiero** poder exportar mi configuración
**Para** respaldarla o transferirla a otro dispositivo

**Criterios de Aceptación:**
- [ ] Botón "Exportar" genera archivo JSON
- [ ] Incluye perfiles, apps, tags, preferencias
- [ ] Botón "Importar" lee archivo JSON
- [ ] Pregunta si reemplazar o fusionar con existente
- [ ] Validación de archivo antes de importar

**Prioridad:** Baja
**Esfuerzo:** M
**Fase:** v1.1

---

## Épica 9: Quick Settings y Shortcuts

### US-QST-001: Quick Settings tile
**Como** usuario power
**Quiero** tener un toggle en Quick Settings
**Para** activar/desactivar bloqueo sin abrir la app

**Criterios de Aceptación:**
- [ ] Tile disponible para agregar a Quick Settings
- [ ] Muestra estado: activo (colored) o inactivo (grey)
- [ ] Tap hace toggle del bloqueo
- [ ] Long-press abre la app
- [ ] Usa el último perfil usado

**Prioridad:** Baja
**Esfuerzo:** M
**Fase:** v1.1

---

### US-QST-002: App shortcuts (long-press icon)
**Como** usuario frecuente
**Quiero** tener shortcuts al hacer long-press en el icono de la app
**Para** acceder rápidamente a acciones comunes

**Criterios de Aceptación:**
- [ ] Shortcut "Activar bloqueo" con perfil default
- [ ] Shortcut "Desactivar bloqueo"
- [ ] Shortcut dinámico con perfil más usado
- [ ] Shortcuts funcionan desde launcher

**Prioridad:** Baja
**Esfuerzo:** S
**Fase:** v1.1

---

## Resumen de User Stories por Fase

### MVP (51 story points)

| Epic | Stories | Points |
|------|---------|--------|
| Onboarding | 5 | 8 |
| Perfiles | 5 | 9 |
| Bloqueo | 6 | 14 |
| Apps | 3 | 6 |
| Tags NFC | 4 | 8 |
| Estadísticas | 2 | 5 |
| Widgets | 2 | 6 |
| Configuración | 3 | 4 |
| **Total** | **30** | **60** |

### v1.1 (29 story points)

| Epic | Stories | Points |
|------|---------|--------|
| Bloqueo | 1 | 2 |
| Apps | 1 | 4 |
| Tags NFC | 1 | 2 |
| Estadísticas | 2 | 5 |
| Widgets | 1 | 4 |
| Configuración | 1 | 4 |
| Quick Settings | 2 | 8 |
| **Total** | **9** | **29** |

---

**Creado:** 2026-01-03T04:32:12Z
**Metodología:** Oden Forge - Domain Expert Analysis
