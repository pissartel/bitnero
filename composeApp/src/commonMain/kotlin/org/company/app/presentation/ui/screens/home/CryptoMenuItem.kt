package org.company.app.presentation.ui.screens.home

import androidx.compose.ui.graphics.Color
import org.company.app.theme.cryptoColors

enum class CryptoMenuItem(
    val id: String,
    val title: String,
    val symbol: String,
    val color: Color
) {
    BITCOIN("bitcoin", "Bitcoin", "BTC", cryptoColors.Currency.bitcoin),
    MONERO("monero", "Monero", "XMR", cryptoColors.Currency.monero),
}
