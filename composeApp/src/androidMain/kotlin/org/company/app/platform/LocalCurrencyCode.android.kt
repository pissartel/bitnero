package org.company.app.platform

import java.util.Currency
import java.util.Locale

actual fun getLocalCurrencyCode(): String {
    return Currency.getInstance(Locale.getDefault()).currencyCode
}