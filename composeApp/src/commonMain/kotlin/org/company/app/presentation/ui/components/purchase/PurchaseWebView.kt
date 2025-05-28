package org.company.app.presentation.ui.components.purchase

import androidx.compose.runtime.Composable
import org.company.app.platform.PlatformWebView

@Composable
fun PurchaseWebView(
    btcAddress: String,
    fiatAmount: Int = 20
) {
//    val url = "https://widget.onramper.com" +
//            "?defaultCrypto=BTC" +
//            "&defaultFiat=EUR" +
//            "&wallets=BTC:$btcAddress" +
//            "&amount=$fiatAmount" +
//            "&onlyGateways=simplex"
val url = "https://widget.onramper.com?defaultCrypto=BTC&isWebView=true"
    PlatformWebView(url)
}