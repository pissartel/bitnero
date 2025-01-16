package org.company.app.presentation.ui.screens.home

import org.company.app.domain.model.crypto.ChartPrice
import org.company.app.domain.model.fiat.FiatCurrency
import org.company.app.platform.BitcoinWallet
import org.company.app.presentation.ui.base.UiEffect
import org.company.app.presentation.ui.base.UiState
import org.company.app.presentation.ui.components.Period

data class CryptoMenuSate(
    val walletState: BitcoinWallet.WalletState,
    val walletBalance: Long?,
    val walletPrice: Double?,
    val marketPrice: Double?,
    val fiatCurrency: FiatCurrency,
    val walletChartPrice: Map<Period, List<ChartPrice>>,
    val marketChartPrices: Map<Period, List<ChartPrice>>
) : UiState

sealed interface CryptoMenuEffect : UiEffect {
    data class WalletCreated(val mnemonics: List<String>) : CryptoMenuEffect
}
