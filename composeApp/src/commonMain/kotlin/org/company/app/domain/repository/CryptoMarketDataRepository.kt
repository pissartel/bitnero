package org.company.app.domain.repository

import org.company.app.data.remote.CryptoMarketClient
import org.company.app.data.repository.CryptoMarketApi
import org.company.app.domain.model.crypto.Data
import org.company.app.domain.model.crypto.MarketPrice
import org.company.app.domain.model.fiat.FiatCurrency

class CryptoMarketDataRepository(
    private val cryptoMarketClient: CryptoMarketClient
) : CryptoMarketApi {

    override suspend fun getMarketPrice(id: String, fiatCurrency: FiatCurrency): Data {
        return cryptoMarketClient.getMarketPrice(id, fiatCurrency)
    }

    override suspend fun getMarketChart(id: String, fiatCurrency: FiatCurrency, days: Int): List<MarketPrice> {
        return cryptoMarketClient.getMarketChart(id, fiatCurrency, days).prices.map {
            MarketPrice(it[0].toLong(), it[1].toDouble())
        }
    }
}