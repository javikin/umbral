package com.umbral.domain.model

enum class RequiredPermission {
    USAGE_STATS,           // Obligatorio
    OVERLAY,               // Obligatorio
    NOTIFICATIONS,         // Recomendado
    POST_NOTIFICATIONS,    // Android 13+
    NOTIFICATION_LISTENER  // Recomendado - Para interceptar notificaciones
}
