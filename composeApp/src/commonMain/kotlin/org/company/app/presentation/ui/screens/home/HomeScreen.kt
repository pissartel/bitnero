package org.company.app.presentation.ui.screens.home

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

// TODO : when tabs will be implemented
class HomeScreen : Screen {
    @Composable
    override fun Content() {
        CryptoMenu(CryptoMenuItem.BITCOIN)
    }
}