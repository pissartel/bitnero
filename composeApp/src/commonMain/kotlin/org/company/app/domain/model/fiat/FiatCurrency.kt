package org.company.app.domain.model.fiat

import org.company.app.platform.getLocalCurrencyCode
import kotlin.math.pow
import kotlin.math.round

enum class FiatCurrency(val symbol: String) {
    USD("USD"),
    EUR("EUR");

    companion object {
        fun Double.toFiatString(fiatCurrency: FiatCurrency): String {
            val roundNumber = round(this * 10.0.pow(2)) / 10.0.pow(2)
            return when (fiatCurrency) {
                USD -> "$${roundNumber}"
                EUR -> "$roundNumber€"
            }
        }

        fun getLocalFiatCurrency(): FiatCurrency {
            val localCurrencyCode = getLocalCurrencyCode()
            return entries.find { it.symbol == localCurrencyCode } ?: USD
        }
    }
}