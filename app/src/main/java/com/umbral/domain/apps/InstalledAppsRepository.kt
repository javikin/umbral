package com.umbral.domain.apps

import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing installed applications.
 */
interface InstalledAppsRepository {

    /**
     * Get all installed apps as a Flow.
     * Updates automatically when apps are installed/uninstalled.
     */
    fun getInstalledApps(includeSystemApps: Boolean = false): Flow<List<InstalledApp>>

    /**
     * Get app by package name.
     */
    suspend fun getAppByPackage(packageName: String): InstalledApp?

    /**
     * Get multiple apps by package names.
     */
    suspend fun getAppsByPackages(packageNames: List<String>): List<InstalledApp>

    /**
     * Refresh the list of installed apps.
     */
    suspend fun refreshApps()
}
