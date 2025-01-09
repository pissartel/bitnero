package org.company.app.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.company.app.data.remote.CryptoMarketClient
import org.company.app.domain.repository.CryptoMarketDataRepository
import org.company.app.presentation.ui.screens.home.CryptoMenuItem
import org.company.app.presentation.ui.screens.home.CryptoMenuViewModel
import org.company.app.presentation.viewmodel.MainViewModel
import org.company.app.utils.Constant
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(
                    json = Json {
                        isLenient = true
                        ignoreUnknownKeys = true
                    }
                )
            }
            install(Logging) {
                level = LogLevel.ALL
                logger = object : Logger {
                    override fun log(message: String) {
                        println(message)
                    }
                }
                filter { filter -> filter.url.host.contains("api.coingecko.com") }
                sanitizeHeader { header -> header == HttpHeaders.Authorization }
            }
            install(HttpTimeout) {
                requestTimeoutMillis = Constant.TIME_OUT
                connectTimeoutMillis = Constant.TIME_OUT
                socketTimeoutMillis = Constant.TIME_OUT
            }
            defaultRequest {
                headers {
                    append("x-cg-demo-api-key", Constant.COIN_GECKO_API_KEY)
                }
            }
        }
    }
    single { CryptoMarketClient(get()) }
    single {
        CryptoMarketDataRepository(get())
    }
    singleOf(::MainViewModel)
    single { CryptoMenuViewModel(CryptoMenuItem.BITCOIN, get()) }
}