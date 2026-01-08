package com.umbral.notifications.domain.model

import android.app.Notification

/**
 * System-level whitelist of critical notifications that should ALWAYS be allowed,
 * regardless of blocking state.
 *
 * This ensures critical functionality like phone calls, SMS, alarms, and system alerts
 * are never blocked by Umbral.
 */
object SystemWhitelist {

    /**
     * Package names that are always allowed to show notifications.
     * These include system dialer, phone, messaging, and alarm apps.
     */
    val ALWAYS_ALLOWED = setOf(
        // Android Dialer & Phone
        "com.android.dialer",
        "com.google.android.dialer",
        "com.samsung.android.dialer",
        "com.android.phone",

        // Messaging & SMS
        "com.android.mms",
        "com.google.android.apps.messaging",
        "com.samsung.android.messaging",

        // Clock & Alarms
        "com.android.deskclock",
        "com.google.android.deskclock",
        "com.samsung.android.app.clockpackage",

        // System critical
        "android",
        "com.android.systemui"
    )

    /**
     * Notification categories that are always allowed.
     * These are Android's built-in notification categories for critical alerts.
     */
    val ALLOWED_CATEGORIES = setOf(
        Notification.CATEGORY_CALL,      // Incoming calls
        Notification.CATEGORY_ALARM,     // Alarm clock notifications
        Notification.CATEGORY_MESSAGE    // SMS only, not chat apps
    )

    /**
     * Package patterns for common authenticator apps.
     * These are suggested to users for their personal whitelist, as they often
     * require time-sensitive 2FA codes.
     */
    val COMMON_AUTHENTICATORS = setOf(
        "com.google.android.apps.authenticator2",
        "com.authy.authy",
        "com.microsoft.msa.authenticator",
        "org.fedorahosted.freeotp",
        "com.azure.authenticator",
        "com.yubico.yubioath",
        "com.duosecurity.duomobile"
    )

    /**
     * Package patterns for common banking apps (Mexico focused).
     * These are suggested to users for their personal whitelist.
     */
    val COMMON_BANKING_APPS = setOf(
        // Mexican banks
        "com.bbva.bbvacontigo",
        "com.banorte.mbanorte",
        "com.santander.mx",
        "com.scotiabank.mx",
        "mx.com.hsbc.hsbcmexico",
        "com.banregio.mobilebanking",
        "com.inbursa.inbursamovil",

        // International
        "com.chase.sig.android",
        "com.bankofamerica.mobile",
        "com.usaa.mobile.android.usaa",
        "com.paypal.android.p2pmobile"
    )

    /**
     * Checks if a package name matches any pattern in the always-allowed list.
     */
    fun isAlwaysAllowed(packageName: String): Boolean {
        return ALWAYS_ALLOWED.any { pattern ->
            packageName.startsWith(pattern)
        }
    }

    /**
     * Checks if a notification category is in the allowed categories list.
     */
    fun isCategoryAllowed(category: String?): Boolean {
        return category != null && category in ALLOWED_CATEGORIES
    }

    /**
     * Returns suggested apps for the user's personal whitelist.
     */
    fun getSuggestedWhitelistApps(): Set<String> {
        return COMMON_AUTHENTICATORS + COMMON_BANKING_APPS
    }
}
