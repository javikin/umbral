package com.umbral.domain.onboarding

import com.umbral.domain.model.NfcStatus
import com.umbral.domain.model.PermissionStates
import com.umbral.domain.model.PermissionStatus
import com.umbral.domain.model.RequiredPermission

interface PermissionHelper {

    /**
     * Verifica el estado de todos los permisos
     */
    fun checkAllPermissions(): PermissionStates

    /**
     * Verifica un permiso específico
     */
    fun checkPermission(permission: RequiredPermission): PermissionStatus

    /**
     * Abre settings para un permiso específico
     */
    fun openPermissionSettings(permission: RequiredPermission)

    /**
     * Verifica si NFC está disponible y habilitado
     */
    fun checkNfcStatus(): NfcStatus

    /**
     * Abre settings de NFC
     */
    fun openNfcSettings()

    /**
     * Permisos mínimos requeridos para funcionar
     */
    fun hasMinimumPermissions(): Boolean

    /**
     * Todos los permisos recomendados
     */
    fun hasAllRecommendedPermissions(): Boolean
}
