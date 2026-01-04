package com.umbral.domain.apps

import android.graphics.drawable.Drawable

/**
 * Represents an installed application on the device.
 */
data class InstalledApp(
    val packageName: String,
    val name: String,
    val icon: Drawable? = null,
    val category: String? = null,
    val isSystemApp: Boolean = false
)

/**
 * Provider interface for getting installed apps.
 */
interface InstalledAppsProvider {

    /**
     * Get all launchable apps (excluding system apps by default).
     */
    suspend fun getLaunchableApps(includeSystemApps: Boolean = false): List<InstalledApp>

    /**
     * Get app info by package name.
     */
    suspend fun getAppByPackage(packageName: String): InstalledApp?

    /**
     * Get multiple apps by package names.
     */
    suspend fun getAppsByPackages(packageNames: List<String>): List<InstalledApp>
}
