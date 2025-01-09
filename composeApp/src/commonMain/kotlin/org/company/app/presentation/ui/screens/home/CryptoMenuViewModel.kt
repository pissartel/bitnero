package org.company.app.presentation.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.company.app.domain.model.crypto.Data
import org.company.app.domain.model.crypto.MarketPrice
import org.company.app.domain.model.fiat.FiatCurrency
import org.company.app.domain.repository.CryptoMarketDataRepository
import org.company.app.domain.usecase.ResultState
import org.company.app.presentation.ui.components.Period

class CryptoMenuViewModel(
    private val cryptoMenuItem: CryptoMenuItem = CryptoMenuItem.BITCOIN,
    private val cryptoDataRepository: CryptoMarketDataRepository
) : ViewModel() {

    private val _cryptoData = MutableStateFlow<ResultState<List<MarketPrice>>>(ResultState.LOADING)
    var cryptoData: StateFlow<ResultState<List<MarketPrice>>> = _cryptoData.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        ResultState.LOADING
    )

    init {
        fetchCryptoData(Period.ONE_YEAR)
    }

    fun fetchCryptoData(period: Period) {
        viewModelScope.launch {
            _cryptoData.value = ResultState.LOADING
            try {
                val response =
                    cryptoDataRepository.getMarketChart(
                        id = cryptoMenuItem.id,
                        fiatCurrency = FiatCurrency.EUR,
                        days = period.days
                    )
               val data =  if (period == Period.ONE_HOUR) {
                    val lastTime = response.last().time
                    val oneHourInMillis = 60 * 60 * 1000L
                    response.filter { it.time > lastTime - oneHourInMillis }
                } else response
                println("start = ${response.first().time}")
                println("end = ${response.last().time}")
                _cryptoData.value = ResultState.SUCCESS(data)
            } catch (e: Exception) {
                _cryptoData.value = ResultState.ERROR(e.message.toString())
            }
        }
    }
}

