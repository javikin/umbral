package com.umbral.data.apps

import android.util.Log
import com.umbral.domain.apps.InstalledApp
import com.umbral.domain.apps.InstalledAppsProvider
import com.umbral.domain.apps.InstalledAppsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "InstalledAppsRepo"

@Singleton
class InstalledAppsRepositoryImpl @Inject constructor(
    private val provider: InstalledAppsProvider
) : InstalledAppsRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _installedApps = MutableStateFlow<List<InstalledApp>>(emptyList())

    init {
        // Load apps on initialization
        Log.d(TAG, "Initializing repository, loading apps...")
        scope.launch {
            refreshApps()
        }
    }

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
        Log.d(TAG, "refreshApps() called")
        val apps = provider.getLaunchableApps(includeSystemApps = false)
        Log.d(TAG, "Loaded ${apps.size} apps")
        _installedApps.value = apps
    }
}
