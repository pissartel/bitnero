package org.company.app.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.company.app.domain.model.crypto.ApiChartPriceList
import org.company.app.domain.model.crypto.CryptoData
import org.company.app.domain.model.fiat.FiatCurrency
import org.company.app.utils.Constant.COIN_GECKO_BASE_URL

class CryptoMarketClient(private val client: HttpClient) {

    suspend fun getMarketPrice(id: String, currency: FiatCurrency): Map<String, Map<String, String>> {
        return client.get(COIN_GECKO_BASE_URL + "simple/price") {
            parameter("ids", id)
            parameter("vs_currencies", currency.symbol)
        }
            .body()
    }

    suspend fun getMarketChartPrices(
        id: String,
        currency: FiatCurrency,
        days: Int
    ): ApiChartPriceList {
        return client.get(COIN_GECKO_BASE_URL + "coins/$id/market_chart") {
            parameter("id", id)
            parameter("vs_currency", currency.symbol)
            parameter("days", days)
        }
            .body()
    }
}