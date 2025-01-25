package org.company.app.domain.model.crypto

import androidx.compose.ui.graphics.Color
import org.company.app.theme.cryptoColors

enum class CryptoCurrency(
    val id: String,
    val title: String,
    val symbol: String,
    val color: Color
) {
    BITCOIN("bitcoin", "Bitcoin", "BTC", cryptoColors.Currency.bitcoin),
    MONERO("monero", "Monero", "XMR", cryptoColors.Currency.monero),
}
