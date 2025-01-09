package org.company.app.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.company.app.domain.model.crypto.ApiMarketPriceList
import org.company.app.domain.model.crypto.Data
import org.company.app.domain.model.crypto.MarketPrice
import org.company.app.domain.model.fiat.FiatCurrency
import org.company.app.utils.Constant.COIN_GECKO_BASE_URL

class CryptoMarketClient(private val client: HttpClient) {

    suspend fun getMarketPrice(id: String, currency: FiatCurrency): Data {
        return client.get(COIN_GECKO_BASE_URL + "simple/price") {
            parameter("ids", id)
            parameter("vs_currencies", currency.symbol)
        }
            .body()
    }

    suspend fun getMarketChart(
        id: String,
        currency: FiatCurrency,
        days: Int
    ): ApiMarketPriceList {
        return client.get(COIN_GECKO_BASE_URL + "coins/$id/market_chart") {
            parameter("id", id)
            parameter("vs_currency", currency.symbol)
            parameter("days", days)
        }
            .body()
    }
}