package org.company.app.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.company.app.data.remote.CryptoMarketClient
import org.company.app.data.repository.CryptoMarketApi
import org.company.app.domain.model.crypto.ChartPrice
import org.company.app.domain.model.fiat.FiatCurrency

class CryptoMarketDataRepository(
    private val cryptoMarketClient: CryptoMarketClient
) : CryptoMarketApi {

    override suspend fun getMarketPrice(id: String, fiatCurrency: FiatCurrency): Flow<Double> =
        flow {
            emit(
                cryptoMarketClient.getMarketPrice(id, fiatCurrency).get(id)
                    ?.get(fiatCurrency.symbol)?.toDouble() ?: throw Exception("Error")
            )
        }

    override suspend fun getMarketChart(
        id: String,
        fiatCurrency: FiatCurrency,
        days: Int
    ): Flow<List<ChartPrice>> = flow {
        emit(cryptoMarketClient.getMarketChartPrices(id, fiatCurrency, days).prices.map {
            ChartPrice(it[0].toLong(), it[1].toDouble())
        })
    }
}