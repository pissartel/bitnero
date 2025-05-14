package org.company.app.presentation.ui.screens.home

import WalletState
import org.company.app.domain.model.Period
import org.company.app.domain.model.crypto.ChartBalance
import org.company.app.domain.model.crypto.ChartPrice
import org.company.app.domain.model.fiat.FiatCurrency
import org.company.app.presentation.ui.base.UiEffect
import org.company.app.presentation.ui.base.UiEvent
import org.company.app.presentation.ui.base.UiState

sealed interface CryptoMenuEvent : UiEvent {
    data object OnCreateWalletClicked : CryptoMenuEvent
}

data class CryptoMenuSate(
    val walletState: WalletState,
    val walletBalance: Long?,
    val walletPrice: Double?,
    val marketPrice: Double?,
    val fiatCurrency: FiatCurrency,
    val walletChartBalance: Map<Period, List<ChartBalance>>,
    val marketChartPrices: Map<Period, List<ChartPrice>>
) : UiState

sealed interface CryptoMenuEffect : UiEffect {
    data class ShowCreatedWalletSheet(val mnemonics: List<String>) : CryptoMenuEffect
}