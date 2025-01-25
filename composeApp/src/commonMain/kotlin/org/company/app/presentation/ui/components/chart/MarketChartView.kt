package org.company.app.presentation.ui.components.chart

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.company.app.domain.model.Period
import org.company.app.domain.model.crypto.ChartPrice
import org.company.app.domain.model.fiat.FiatCurrency
import org.company.app.domain.model.fiat.FiatCurrency.Companion.toFiatString
import org.company.app.presentation.ui.components.LoadingBox
import org.company.app.presentation.ui.components.PeriodSelection
import org.company.app.theme.cryptoColors

@Composable
fun MarketChartView(
    marketPrice: Double?,
    chartPrices: Map<Period, List<ChartPrice>>,
    fiatCurrency: FiatCurrency,
    modifier: Modifier = Modifier
) {
    var selectedChartPrice by remember { mutableStateOf<ChartPrice?>(null) }

    var selectedPeriod by remember {
        mutableStateOf(Period.ONE_YEAR)
    }

    val selectedChartPrices by remember(selectedPeriod, chartPrices) {
        derivedStateOf {
            chartPrices[selectedPeriod]
        }
    }

    val periodPercentChange by remember(chartPrices, selectedChartPrice, selectedChartPrices) {
        derivedStateOf {
            val minChartValue = selectedChartPrices?.minBy { it.time }?.value
            if (selectedChartPrices != null && minChartValue != null) {
                minChartValue.let {
                    (selectedChartPrice?.value
                        ?: marketPrice)?.minus(it)
                }?.let {
                    kotlin.math.floor(
                        (100 * it / minChartValue)
                                * 100.0
                    ) / 100.0
                }
            } else null
        }
    }
    val isPositive by remember(periodPercentChange) {
        derivedStateOf {
            periodPercentChange?.let { it > 0 }
        }
    }
    val percentColor by remember(periodPercentChange) {
        derivedStateOf {
            when (isPositive) {
                true -> cryptoColors.Charts.positive
                false -> cryptoColors.Charts.negative
                null -> Color.Unspecified
            }
        }
    }

    val arrowSymbol by remember(periodPercentChange) {
        derivedStateOf {
            periodPercentChange?.let {
                when (it) {
                    in Double.NEGATIVE_INFINITY..(-10).toDouble() -> '↓'
                    in (-10).toDouble()..(0).toDouble() -> "↘"
                    in (0).toDouble()..10.toDouble() -> "↗"
                    in 10.toDouble()..Double.POSITIVE_INFINITY -> '↑'
                    else -> ""
                }
            } ?: ""
        }
    }

    selectedChartPrices?.let {
        Column(modifier) {
            ChartView(
                modifier = modifier.fillMaxHeight(fraction = 0.90f),
                chartPrices = it,
                selectedPeriod = selectedPeriod,
                selectedChartPrice = selectedChartPrice,
                accentColor = percentColor,
                headerRow = {
                    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        if (periodPercentChange != null) {
                            Text(
                                "${
                                    (selectedChartPrice?.value ?: marketPrice)?.toFiatString(
                                        fiatCurrency
                                    )
                                }",
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                            Spacer(modifier.width(10.dp))
                            Text(
                                "$arrowSymbol $periodPercentChange %",
                                style = TextStyle(color = percentColor)
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        PeriodSelection(selectedPeriod) {
                            selectedChartPrice = null
                            selectedPeriod = it
                        }
                    }
                },
                onSelectedMarketPrice = { selectedChartPrice = it }
            )
        }
    } ?: LoadingBox()
}
