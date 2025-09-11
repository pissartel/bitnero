package org.company.app.domain.model.crypto

import androidx.compose.ui.graphics.Color
import bitnero.composeapp.generated.resources.Res
import bitnero.composeapp.generated.resources.btc_icon
import org.company.app.theme.cryptoColors
import org.jetbrains.compose.resources.DrawableResource

enum class CryptoCurrency(
    val id: String,
    val title: String,
    val symbol: String,
    val color: Color,
    val icon: DrawableResource
) {
    BITCOIN("bitcoin", "Bitcoin", "BTC", cryptoColors.Currency.bitcoin, Res.drawable.btc_icon),
    MONERO("monero", "Monero", "XMR", cryptoColors.Currency.monero, Res.drawable.btc_icon),
}
