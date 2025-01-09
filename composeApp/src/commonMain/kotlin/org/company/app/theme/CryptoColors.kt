package org.company.app.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

class CryptoColors {
    val Currency = Currency()
    val Charts = Charts()
}

class Currency {
    val bitcoin = Color(0xFFFF9901)
    val monero: Color = Color(0xFFFF6600)
}

class Charts {
    val positive: Color = Color(0xFF33CB5C)
    val negative: Color = Color(0xFFE50019)
}

val LocaleCryptoColors = compositionLocalOf { CryptoColors() }
val cryptoColors = CryptoColors()

val MaterialTheme.cryptoColors: CryptoColors
    @Composable
    @ReadOnlyComposable
    get() = LocaleCryptoColors.current