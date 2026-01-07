package com.umbral.notifications.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DataStore-based preferences for notification whitelist management.
 *
 * Manages the user's custom whitelist of apps that should always show notifications
 * even when blocking is active (e.g., banking apps, authenticators).
 */
@Singleton
class NotificationPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private object Keys {
        /**
         * Comma-separated list of package names in the user's custom whitelist.
         */
        val USER_WHITELIST = stringPreferencesKey("notification_user_whitelist")
    }

    /**
     * Flow of the user's custom whitelist.
     * Returns an empty set if no whitelist is configured.
     */
    val userWhitelist: Flow<Set<String>> = dataStore.data.map { prefs ->
        prefs[Keys.USER_WHITELIST]
            ?.split(",")
            ?.filter { it.isNotBlank() }
            ?.toSet()
            ?: emptySet()
    }

    /**
     * Adds a package name to the user's whitelist.
     * If the package is already in the whitelist, this is a no-op.
     *
     * @param packageName The package name to add (e.g., "com.google.android.apps.authenticator2")
     */
    suspend fun addToWhitelist(packageName: String) {
        if (packageName.isBlank()) return

        dataStore.edit { prefs ->
            val currentWhitelist = prefs[Keys.USER_WHITELIST]
                ?.split(",")
                ?.filter { it.isNotBlank() }
                ?.toMutableSet()
                ?: mutableSetOf()

            currentWhitelist.add(packageName)

            prefs[Keys.USER_WHITELIST] = currentWhitelist.joinToString(",")
        }
    }

    /**
     * Removes a package name from the user's whitelist.
     * If the package is not in the whitelist, this is a no-op.
     *
     * @param packageName The package name to remove
     */
    suspend fun removeFromWhitelist(packageName: String) {
        dataStore.edit { prefs ->
            val currentWhitelist = prefs[Keys.USER_WHITELIST]
                ?.split(",")
                ?.filter { it.isNotBlank() }
                ?.toMutableSet()
                ?: return@edit

            currentWhitelist.remove(packageName)

            if (currentWhitelist.isEmpty()) {
                prefs.remove(Keys.USER_WHITELIST)
            } else {
                prefs[Keys.USER_WHITELIST] = currentWhitelist.joinToString(",")
            }
        }
    }

    /**
     * Checks if a package is in the user's whitelist.
     *
     * @param packageName The package name to check
     * @return Flow<Boolean> indicating if the package is whitelisted
     */
    fun isWhitelisted(packageName: String): Flow<Boolean> {
        return userWhitelist.map { whitelist ->
            packageName in whitelist
        }
    }

    /**
     * Clears the entire user whitelist.
     */
    suspend fun clearWhitelist() {
        dataStore.edit { prefs ->
            prefs.remove(Keys.USER_WHITELIST)
        }
    }

    /**
     * Replaces the entire whitelist with a new set of package names.
     * Useful for bulk operations or restoring from backup.
     *
     * @param packageNames Set of package names to set as the new whitelist
     */
    suspend fun setWhitelist(packageNames: Set<String>) {
        dataStore.edit { prefs ->
            val cleanedPackages = packageNames
                .filter { it.isNotBlank() }
                .toSet()

            if (cleanedPackages.isEmpty()) {
                prefs.remove(Keys.USER_WHITELIST)
            } else {
                prefs[Keys.USER_WHITELIST] = cleanedPackages.joinToString(",")
            }
        }
    }
}
