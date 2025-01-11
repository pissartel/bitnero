package org.company.app.presentation.ui.screens.home

import org.company.app.domain.model.crypto.ChartPrice
import org.company.app.domain.model.fiat.FiatCurrency
import org.company.app.presentation.ui.base.UiState
import org.company.app.presentation.ui.components.Period

data class CryptoMenuSate(
    val marketPrice: Double?,
    val fiatCurrency: FiatCurrency,
    val walletChartPrice: Map<Period, List<ChartPrice>>,
    val marketChartPrices: Map<Period, List<ChartPrice>>
) : UiState
