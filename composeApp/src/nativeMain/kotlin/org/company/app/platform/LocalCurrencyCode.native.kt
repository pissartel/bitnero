package org.company.app.platform

import platform.Foundation.NSLocale
import platform.Foundation.NSLocaleCurrencyCode
import platform.Foundation.currentLocale

actual fun getLocalCurrencyCode(): String {
    val locale = NSLocale.currentLocale
    return locale.objectForKey(NSLocaleCurrencyCode) as? String ?: "USD" // Par défaut "USD")
}