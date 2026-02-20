# Contribuir a Umbral

Gracias por tu interés en contribuir a Umbral. Este documento describe las reglas y el proceso para colaborar.

## Reglas del Repositorio

### Ramas Protegidas

- **`main`** está protegida. No se permiten pushes directos.
- Todos los cambios deben llegar mediante **Pull Request (PR)**.
- Cada PR requiere **al menos 1 review aprobado** antes de hacer merge.
- Los reviews descartados se invalidan si se pushean nuevos cambios.
- Todos los hilos de conversación deben resolverse antes del merge.
- No se permite borrar ni hacer force-push a `main`.

### Flujo de Trabajo

1. **Fork** el repositorio
2. **Crea una rama** desde `main`:
   ```bash
   git checkout -b feature/nombre-descriptivo
   ```
3. **Haz tus cambios** siguiendo las convenciones del proyecto
4. **Commit** con mensajes claros:
   ```
   [Feat] Add NFC tag validation
   [Fix] Resolve crash on Android 14
   [Docs] Update setup guide
   ```
5. **Push** a tu fork y abre un **Pull Request** contra `main`
6. **Espera el review** y responde a los comentarios

### Convenciones de Código

#### Nombres en el Código (INGLÉS)
- **Clases:** PascalCase (`BlockingProfile`, `NfcTagManager`)
- **Funciones/Variables:** camelCase (`startBlocking`, `profileId`)
- **Paquetes:** lowercase (`com.umbral.nfc`, `com.umbral.blocking`)
- **Base de datos:** snake_case (`blocking_profiles`, `created_at`)

#### Textos de UI (ESPAÑOL)
- Todos los textos visibles al usuario deben estar en español
- Usar `strings.xml`, nunca hardcodear texto en composables

### Formato de Commits

```
[Type] Descripción breve

Tipos válidos:
- [Feat]     Nueva funcionalidad
- [Fix]      Corrección de bug
- [Docs]     Documentación
- [Refactor] Refactorización sin cambio funcional
- [Test]     Tests
- [Chore]    Tareas de mantenimiento
```

### Pull Requests

- Título claro y descriptivo
- Descripción de qué cambia y por qué
- Screenshots si hay cambios visuales
- Referencia al issue relacionado (si existe): `Closes #123`

## Configuración del Entorno

### Requisitos
- Android Studio Hedgehog o superior
- JDK 17
- Android SDK 34

### Setup
```bash
git clone https://github.com/javikin/umbral.git
cd umbral
# Abrir en Android Studio
```

## Reportar Bugs

Abre un [issue](https://github.com/javikin/umbral/issues) con:
- Descripción del problema
- Pasos para reproducir
- Comportamiento esperado vs actual
- Versión de Android y dispositivo

## Proponer Features

Abre un [issue](https://github.com/javikin/umbral/issues) con la etiqueta `enhancement` describiendo:
- Qué problema resuelve
- Cómo debería funcionar
- Alternativas consideradas

## Licencia

Al contribuir, aceptas que tus contribuciones se publiquen bajo la misma licencia del proyecto.
