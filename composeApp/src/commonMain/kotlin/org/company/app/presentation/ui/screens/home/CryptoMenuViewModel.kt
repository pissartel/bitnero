package org.company.app.presentation.ui.screens.home

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.company.app.domain.model.crypto.ChartPrice
import org.company.app.domain.model.fiat.FiatCurrency
import org.company.app.domain.model.fiat.FiatCurrency.Companion.getLocalFiatCurrency
import org.company.app.domain.repository.CryptoMarketDataRepository
import org.company.app.domain.usecase.asResult
import org.company.app.domain.usecase.doOnFailure
import org.company.app.domain.usecase.doOnSuccess
import org.company.app.presentation.ui.base.BaseViewModel
import org.company.app.presentation.ui.base.UiEffect
import org.company.app.presentation.ui.components.Period

class CryptoMenuViewModel(
    private val cryptoMenuItem: CryptoMenuItem = CryptoMenuItem.BITCOIN,
    private val cryptoDataRepository: CryptoMarketDataRepository
) : BaseViewModel<CryptoMenuSate, UiEffect>() {

    init {
        fetchCryptoData()
    }

    private fun fetchCryptoData() {
        viewModelScope.launch {
            collectMarketData(currentState.fiatCurrency)
                .asResult()
                .doOnFailure {
                    println("ERROR : $it")
                }
                .doOnSuccess { data ->
                    println("size date : ${data.size}")
                    setState {
                        copy(
                            marketPrice = data.values.flatten().maxBy { it.time }.value,
                            marketChartPrices = data
                        )
                    }
                }.collect()
        }
    }

    private suspend fun collectMarketData(localFiatCurrency: FiatCurrency): Flow<Map<Period, List<ChartPrice>>> {
        val enums = Period.entries.map { period ->
            cryptoDataRepository.getMarketChart(
                id = cryptoMenuItem.id,
                fiatCurrency = localFiatCurrency,
                days = period.days
            ).map { markChartPrices ->
                period to if (period == Period.ONE_HOUR) {
                    val lastTime = markChartPrices.last().time
                    val oneHourInMillis = 60 * 60 * 1000L
                    markChartPrices.filter { it.time > lastTime - oneHourInMillis }
                } else markChartPrices
            }
        }
        return combine(enums) { arrayOfPairs ->
            arrayOfPairs.associate { it }
        }
    }

    override fun createInitialState(): CryptoMenuSate = CryptoMenuSate(
        marketPrice = null,
        fiatCurrency = getLocalFiatCurrency(),
        walletChartPrice = emptyMap(),
        marketChartPrices = emptyMap()
    )
}

