package org.company.app.platform

actual fun getLocalCurrencyCode(): String {
    val locale = js("navigator.language") as String
    return getCurrencyFromLocale(locale)
}

private fun getCurrencyFromLocale(locale: String): String {
    // Utilise Intl pour récupérer la devise
    val options = js("{}")
    options.style = "currency"
    options.currencyDisplay = "code"

    val formatter = js("new Intl.NumberFormat(locale, options)")
    return formatter.resolvedOptions().currency as? String ?: "USD"
}