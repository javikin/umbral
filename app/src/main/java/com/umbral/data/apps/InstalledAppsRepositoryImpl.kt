package com.umbral.data.apps

import com.umbral.domain.apps.InstalledApp
import com.umbral.domain.apps.InstalledAppsProvider
import com.umbral.domain.apps.InstalledAppsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InstalledAppsRepositoryImpl @Inject constructor(
    private val provider: InstalledAppsProvider
) : InstalledAppsRepository {

    private val _installedApps = MutableStateFlow<List<InstalledApp>>(emptyList())

    override fun getInstalledApps(includeSystemApps: Boolean): Flow<List<InstalledApp>> {
        return _installedApps.asStateFlow()
    }

    override suspend fun getAppByPackage(packageName: String): InstalledApp? {
        return provider.getAppByPackage(packageName)
    }

    override suspend fun getAppsByPackages(packageNames: List<String>): List<InstalledApp> {
        return provider.getAppsByPackages(packageNames)
    }

    override suspend fun refreshApps() {
        val apps = provider.getLaunchableApps(includeSystemApps = false)
        _installedApps.value = apps
    }

    init {
        // Initial load - ideally should be done in a coroutine scope
        // For now, apps will be loaded on first call
    }
}
