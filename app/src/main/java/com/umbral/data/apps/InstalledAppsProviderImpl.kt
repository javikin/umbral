package com.umbral.data.apps

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.umbral.domain.apps.InstalledApp
import com.umbral.domain.apps.InstalledAppsProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InstalledAppsProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : InstalledAppsProvider {

    private val packageManager: PackageManager = context.packageManager

    // Apps that should never be blocked
    private val systemEssentialPackages = setOf(
        "com.android.systemui",
        "com.android.settings",
        "com.android.phone",
        "com.android.dialer",
        "com.android.contacts",
        "com.android.emergency",
        "com.google.android.dialer",
        "com.google.android.contacts",
        "com.samsung.android.dialer",
        "com.samsung.android.contacts",
        context.packageName // Umbral itself
    )

    override suspend fun getLaunchableApps(includeSystemApps: Boolean): List<InstalledApp> {
        return withContext(Dispatchers.IO) {
            try {
                val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
                    addCategory(Intent.CATEGORY_LAUNCHER)
                }

                val resolveInfos = packageManager.queryIntentActivities(mainIntent, 0)

                resolveInfos
                    .asSequence()
                    .map { resolveInfo ->
                        val appInfo = resolveInfo.activityInfo.applicationInfo
                        val packageName = appInfo.packageName
                        val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

                        InstalledApp(
                            packageName = packageName,
                            name = appInfo.loadLabel(packageManager).toString(),
                            icon = try {
                                appInfo.loadIcon(packageManager)
                            } catch (e: Exception) {
                                null
                            },
                            isSystemApp = isSystem
                        )
                    }
                    .filter { app ->
                        // Always exclude essential system packages
                        app.packageName !in systemEssentialPackages
                    }
                    .filter { app ->
                        // Filter system apps if requested
                        includeSystemApps || !app.isSystemApp
                    }
                    .distinctBy { it.packageName }
                    .sortedBy { it.name.lowercase() }
                    .toList()
            } catch (e: Exception) {
                Timber.e(e, "Error getting installed apps")
                emptyList()
            }
        }
    }

    override suspend fun getAppByPackage(packageName: String): InstalledApp? {
        return withContext(Dispatchers.IO) {
            try {
                val appInfo = packageManager.getApplicationInfo(packageName, 0)
                val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

                InstalledApp(
                    packageName = packageName,
                    name = appInfo.loadLabel(packageManager).toString(),
                    icon = try {
                        appInfo.loadIcon(packageManager)
                    } catch (e: Exception) {
                        null
                    },
                    isSystemApp = isSystem
                )
            } catch (e: PackageManager.NameNotFoundException) {
                Timber.w("App not found: $packageName")
                null
            } catch (e: Exception) {
                Timber.e(e, "Error getting app info for $packageName")
                null
            }
        }
    }

    override suspend fun getAppsByPackages(packageNames: List<String>): List<InstalledApp> {
        return withContext(Dispatchers.IO) {
            packageNames.mapNotNull { packageName ->
                getAppByPackage(packageName)
            }
        }
    }
}
