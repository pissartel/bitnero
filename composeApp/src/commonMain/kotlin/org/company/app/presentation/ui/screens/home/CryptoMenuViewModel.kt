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
        observeWalletState()
        observeWalletData()
        observeWalletAddress()
        fetchCryptoData()
    }

    override fun createInitialState(): CryptoMenuSate = CryptoMenuSate(
        walletState = WalletState.UNKNOWN,
        walletBalance = null,
        walletPrice = null,
        marketPrice = null,
        fiatCurrency = getLocalFiatCurrency(),
        walletChartBalance = emptyMap(),
        marketChartPrices = emptyMap()
    )

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
                    // logError("Wallet creation failed", it)
                }
                .doOnSuccess { walletData ->
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
                    // logError("Failed to fetch crypto market data", it)
                }
                .doOnSuccess { data ->
                    setState {
                        copy(
                            marketPrice = data.values.flatten().maxByOrNull { it.time }?.value,
                            marketChartPrices = data
                        )
                    }
                }
                .collect()
        }
    }

    private fun observeWalletState() {
        viewModelScope.launch {
            bitcoinWallet.state.collect { state ->
                setState { copy(walletState = state) }
                if (state == WalletState.READY) syncWalletChart()
            }
        }
    }

    private fun observeWalletData() {
        viewModelScope.launch {
            bitcoinWallet.balance.collect {
                setState { copy(walletBalance = it) }
                syncWalletChart()
            }
        }
    }

    private fun observeWalletAddress() {
        viewModelScope.launch {
            bitcoinWallet.publicAddress.collect {
                // logDebug("Wallet address: $it")
                syncWalletChart()
            }
        }
    }

    private fun syncWalletChart() {
        if (currentState.walletState != WalletState.READY) return
        if (currentState.walletBalance == null) return
        if (currentState.marketChartPrices.isEmpty()) return

        viewModelScope.launch(Dispatchers.Default) {
            transactionHistory.getHistory().collect { appTransactions ->
                val walletTransactions = bitcoinWallet.transactionHistory.value
                val mergedHistory = appTransactions + walletTransactions.filter { wt ->
                    appTransactions.any { it.time == wt.time }
                }

                var balance = currentState.walletBalance ?: 0L
                val historyChart = mutableListOf<ChartBalance>()

                val marketPrices = currentState.marketChartPrices.values.flatten()

                mergedHistory.sortedByDescending { it.time }.forEach { tx ->
                    balance -= tx.amount
                    marketPrices.findForClosestTime(tx.time)?.let { market ->
                        historyChart.add(ChartBalance(tx.time, market.value, balance))
                    }
                }

                val chartBalances = currentState.marketChartPrices.flatMap { (period, prices) ->
                    prices.sortedByDescending { it.time }.mapNotNull { price ->
                        historyChart.findForClosestTime(price.time)?.let {
                            period to ChartBalance(price.time, price.value, it.balance)
                        }
                    }
                }.toMutableList()

                val now = Clock.System.now().toEpochMilliseconds()
                Period.entries.forEach { period ->
                    historyChart.filter { it.time < now - period.toTimeMillis() }
                        .forEach { chartBalances.add(period to it) }
                }

                setState {
                    copy(walletChartBalance = chartBalances.groupBy({ it.first }, { it.second }))
                }
            }
        }
    }

    private suspend fun collectMarketData(fiatCurrency: FiatCurrency): Flow<Map<Period, List<ChartPrice>>> {
        val flows = Period.entries.map { period ->
            cryptoDataRepository.getMarketChart(
                id = cryptoCurrency.id,
                fiatCurrency = fiatCurrency,
                days = period.days
            ).map { chartPrices ->
                val filtered = if (period == Period.ONE_HOUR) {
                    val oneHourAgo = chartPrices.lastOrNull()?.time?.minus(60 * 60 * 1000L)
                    chartPrices.filter { it.time > (oneHourAgo ?: 0L) }
                } else chartPrices
                period to filtered
            }
        }

        return combine(flows) { it.toMap() }
    }

    private inline fun <T : ChartData> List<T>.findForClosestTime(time: Long): T? =
        minByOrNull { kotlin.math.abs(it.time - time) }
}

