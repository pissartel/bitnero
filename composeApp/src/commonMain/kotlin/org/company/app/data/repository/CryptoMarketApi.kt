package org.company.app.data.repository

import org.company.app.domain.model.crypto.Data
import org.company.app.domain.model.crypto.MarketPrice
import org.company.app.domain.model.fiat.FiatCurrency

interface CryptoMarketApi {
    suspend fun getMarketPrice(
        id: String,
        fiatCurrency: FiatCurrency = FiatCurrency.USD
    ): Data

    suspend fun getMarketChart(
        id: String,
        fiatCurrency: FiatCurrency = FiatCurrency.USD,
        days: Int
    ): List<MarketPrice>
}