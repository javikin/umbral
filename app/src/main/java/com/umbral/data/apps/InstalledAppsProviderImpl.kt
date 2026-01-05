package com.umbral.data.apps

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.ResolveInfoFlags
import android.os.Build
import android.util.Log
import com.umbral.domain.apps.AppCategory
import com.umbral.domain.apps.InstalledApp
import com.umbral.domain.apps.InstalledAppsProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "InstalledAppsProvider"

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
                Log.d(TAG, "Starting to query installed apps (includeSystemApps: $includeSystemApps)")

                val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
                    addCategory(Intent.CATEGORY_LAUNCHER)
                }

                // Query launchable apps - <queries> in manifest allows visibility
                Log.d(TAG, "Calling queryIntentActivities...")
                val resolveInfos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // Android 13+ (API 33+)
                    Log.d(TAG, "Using TIRAMISU+ API")
                    packageManager.queryIntentActivities(
                        mainIntent,
                        ResolveInfoFlags.of(0L)
                    )
                } else {
                    // Android 11-12 (API 30-32)
                    Log.d(TAG, "Using pre-TIRAMISU API")
                    @Suppress("DEPRECATION")
                    packageManager.queryIntentActivities(mainIntent, 0)
                }

                Log.d(TAG, "queryIntentActivities returned ${resolveInfos.size} results")

                Timber.d("Found ${resolveInfos.size} launchable apps")

                val apps = resolveInfos
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
                                Timber.w(e, "Failed to load icon for $packageName")
                                null
                            },
                            category = AppCategory.fromPackageName(packageName),
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

                Timber.d("Returning ${apps.size} apps after filtering (systemEssentials excluded: ${systemEssentialPackages.size})")
                apps
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
                    category = AppCategory.fromPackageName(packageName),
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
