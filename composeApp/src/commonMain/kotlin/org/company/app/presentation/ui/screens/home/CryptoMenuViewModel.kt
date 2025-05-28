package org.company.app.presentation.ui.screens.home

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.company.app.domain.model.Period
import org.company.app.domain.model.crypto.ChartPrice
import org.company.app.domain.model.crypto.CryptoCurrency
import org.company.app.domain.model.fiat.FiatCurrency
import org.company.app.domain.model.fiat.FiatCurrency.Companion.getLocalFiatCurrency
import org.company.app.domain.repository.CryptoMarketDataRepository
import org.company.app.data.local.WalletDataEncryptionRepository
import org.company.app.data.repository.TransactionHistory
import org.company.app.domain.model.Period.Companion.toTimeMillis
import org.company.app.domain.model.crypto.ChartBalance
import org.company.app.domain.model.crypto.ChartData
import org.company.app.domain.repository.BitcoinWallet
import org.company.app.domain.usecase.asResult
import org.company.app.domain.usecase.doOnFailure
import org.company.app.domain.usecase.doOnSuccess
import org.company.app.presentation.ui.base.BaseViewModel

class CryptoMenuViewModel(
    private val cryptoCurrency: CryptoCurrency = CryptoCurrency.BITCOIN,
    private val bitcoinWallet: BitcoinWallet,
    private val transactionHistory: TransactionHistory,
    private val cryptoDataRepository: CryptoMarketDataRepository,
    private val walletDataRepository: WalletDataEncryptionRepository
) : BaseViewModel<CryptoMenuEvent, CryptoMenuSate, CryptoMenuEffect>() {

    init {
        viewModelScope.launch {
            val walletData = walletDataRepository.decrypt()
            bitcoinWallet.load(walletData)
        }
        synchroniseWalletData()
        fetchCryptoData()
    }

    override fun createInitialState(): CryptoMenuSate {
        return CryptoMenuSate(
            walletState = WalletState.UNKNOWN,
            walletBalance = null,
            walletPrice = null,
            marketPrice = null,
            fiatCurrency = getLocalFiatCurrency(),
            walletChartBalance = emptyMap(),
            marketChartPrices = emptyMap()
        )
    }

    override fun handleEvent(event: CryptoMenuEvent) {
        when (event) {
            CryptoMenuEvent.OnCreateWalletClicked -> createWallet()
            CryptoMenuEvent.OnActionMenuClicked -> sendEffect { CryptoMenuEffect.ShowActionMenuSheet }
            CryptoMenuEvent.OnPurchaseClicked -> bitcoinWallet.publicAddress.value?.let {
                sendEffect { CryptoMenuEffect.OpenPurchaseActivity(it) }
            }
        }
    }

    private fun createWallet() {
        viewModelScope.launch {
            bitcoinWallet.create()
                .asResult()
                .doOnFailure {
                    println("error = $it")
                }
                .doOnSuccess { walletData ->
                    println("wallet create at ${walletData.creationTime}")
                    walletDataRepository.encrypt(walletData)
                    sendEffect { CryptoMenuEffect.ShowCreatedWalletSheet(walletData.mnemonicPhrase) }
                }
                .collect()
        }
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

    private fun synchroniseWalletData() {
        viewModelScope.launch {
            bitcoinWallet.state.collect {
                setState {
                    copy(
                        walletState = it,
                    )
                }
                if (it == WalletState.READY) {
                    syncWalletChart()
                }
            }
        }
        collectWalletPublicAddress()
        collectWalletData()
    }

    private fun syncWalletChart() {
        println("sync wallet chart")
        if (currentState.walletState != WalletState.READY) return
        if (currentState.walletBalance == null) return
        val marketChartPrices = currentState.marketChartPrices
        if (marketChartPrices.isEmpty()) return
        println("launch")
        viewModelScope.launch(Dispatchers.Default) {
            transactionHistory.getHistory().collect { transactions ->

                // merge app transactions history with wallet history
                val mergedHistory =
                    transactions + bitcoinWallet.transactionHistory.value.filter { walletTransaction -> transactions.find { it.time == walletTransaction.time } != null }

                // init to actual value
                var walletBalance = currentState.walletBalance ?: 0L

                val historyChartBalance = mutableListOf<ChartBalance>()

                mergedHistory
                    .sortedByDescending { it.time }
                    .forEach { history ->
                        walletBalance -= history.amount
                        val findClosestMarketPrice =
                            marketChartPrices.values.flatten().findForClosestTime(history.time)
                        findClosestMarketPrice?.let {
                            historyChartBalance.add(
                                ChartBalance(
                                    history.time,
                                    findClosestMarketPrice.value,
                                    walletBalance,
                                )
                            )
                        }
                    }

                val chartBalances = mutableListOf<Pair<Period, ChartBalance>>()
                marketChartPrices.forEach { chartPrices ->
                    chartPrices.value
                        .sortedByDescending { it.time }
                        .mapNotNull { marketChartPrice ->
                            val closestChartBalance =
                                historyChartBalance.findForClosestTime(marketChartPrice.time)
                            closestChartBalance?.let {
                                chartBalances.add(
                                    chartPrices.key to
                                            ChartBalance(
                                                time = marketChartPrice.time,
                                                marketValue = marketChartPrice.value,
                                                balance = it.balance
                                            )
                                )
                            }
                        }
                }

                val now = Clock.System.now().toEpochMilliseconds()
                historyChartBalance.forEach {
                    Period.entries.forEach { period ->
                        if (it.time < now - period.toTimeMillis()) {
                            chartBalances.add(period to it)
                        }
                    }
                }
                setState {
                    copy(
                        walletChartBalance = chartBalances.groupBy(
                            { it.first },
                            { it.second })
                    )
                }
                //println("walletChartBalance = ${currentState.walletChartBalance}")
            }
        }
    }

    private fun collectWalletData() {
        viewModelScope.launch {
            bitcoinWallet.balance.collect {
                setState { copy(walletBalance = it) }
                syncWalletChart()
            }
        }
    }

    private fun collectWalletPublicAddress() {
        viewModelScope.launch {
            bitcoinWallet.publicAddress.collect {
                println("walletPublicAddress = $it")
                syncWalletChart()
            }
        }
    }

    private suspend fun collectMarketData(localFiatCurrency: FiatCurrency): Flow<Map<Period, List<ChartPrice>>> {
        val enums = Period.entries.map { period ->
            cryptoDataRepository.getMarketChart(
                id = cryptoCurrency.id,
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

    private inline fun <T : ChartData> List<T>.findForClosestTime(value: Long): T? =
        minByOrNull { kotlin.math.abs(it.time - value) }
}

