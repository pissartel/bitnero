package org.company.app.data.repository

import kotlinx.coroutines.flow.Flow
import org.company.app.domain.model.crypto.ChartPrice
import org.company.app.domain.model.fiat.FiatCurrency

interface CryptoMarketApi {
    suspend fun getMarketPrice(
        id: String,
        fiatCurrency: FiatCurrency = FiatCurrency.USD
    ): Flow<Double>

    suspend fun getMarketChart(
        id: String,
        fiatCurrency: FiatCurrency = FiatCurrency.USD,
        days: Int
    ): Flow<List<ChartPrice>>
}