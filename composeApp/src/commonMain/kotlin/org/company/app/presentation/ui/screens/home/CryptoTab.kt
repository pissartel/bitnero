package org.company.app.presentation.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.company.app.domain.model.crypto.ChartPrice
import org.company.app.presentation.ui.components.LoadingBox
import org.company.app.presentation.ui.components.MarketChartView
import org.company.app.presentation.ui.components.Period.ONE_YEAR
import org.company.app.presentation.ui.components.PeriodSelection
import org.koin.compose.koinInject

@Composable
fun CryptoMenu(
    cryptoMenuItem: CryptoMenuItem,
    viewModel: CryptoMenuViewModel = koinInject(),
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier.padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Cours")
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(
                    BorderStroke(2.dp, MaterialTheme.colorScheme.outline),
                    RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            MarketChartView(
                marketPrice = state.marketPrice,
                chartPrices = state.marketChartPrices,
                fiatCurrency = state.fiatCurrency
            )
        }
    }
}