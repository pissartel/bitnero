package org.company.app.presentation.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.company.app.domain.model.crypto.MarketPrice
import org.company.app.domain.usecase.ResultState
import org.company.app.presentation.ui.components.LoadingBox
import org.company.app.presentation.ui.components.MarketChartView
import org.company.app.presentation.ui.components.Period
import org.company.app.presentation.ui.components.Period.ONE_YEAR
import org.company.app.presentation.ui.components.PeriodSelection
import org.koin.compose.koinInject

@Composable
fun CryptoMenu(
    cryptoMenuItem: CryptoMenuItem,
    viewModel: CryptoMenuViewModel = koinInject(),
) {
    val data by viewModel.cryptoData.collectAsState()
    var selectedPeriod by remember {
        mutableStateOf(ONE_YEAR)
    }

    var marketPrices by remember {
        mutableStateOf(emptyList<MarketPrice>())
    }

    Column {
        Text("Cours")
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(500.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(
                    BorderStroke(1.dp, MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)),
                    RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            when (data) {
                is ResultState.ERROR -> Text("ERROR")
                ResultState.LOADING -> {
                    LoadingBox()
                }

                is ResultState.SUCCESS -> {
                    marketPrices =
                        (data as ResultState.SUCCESS<List<MarketPrice>>).response

                    MarketChartView(
                        selectedPeriod,
                        marketPrices,
                    )

                }
            }

            PeriodSelection(selectedPeriod, modifier = Modifier.align(Alignment.TopEnd)) {
                selectedPeriod = it
                viewModel.fetchCryptoData(selectedPeriod)
            }

        }
    }
}