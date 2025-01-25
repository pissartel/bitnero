package org.company.app.presentation.ui.screens.home

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import org.company.app.domain.model.crypto.CryptoCurrency

// TODO : when tabs will be implemented
class HomeScreen : Screen {
    @Composable
    override fun Content() {
        CryptoMenu(CryptoCurrency.BITCOIN)
    }
}