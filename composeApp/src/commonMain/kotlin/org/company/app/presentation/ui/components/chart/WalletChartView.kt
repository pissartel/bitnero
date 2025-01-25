package org.company.app.presentation.ui.components.chart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
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
import org.company.app.domain.model.crypto.ChartBalance
import org.company.app.domain.model.crypto.ChartPrice
import org.company.app.domain.model.crypto.CryptoCurrency
import org.company.app.domain.model.fiat.FiatCurrency
import org.company.app.domain.model.fiat.FiatCurrency.Companion.toFiatString
import org.company.app.presentation.ui.components.LoadingBox
import org.company.app.presentation.ui.components.PeriodSelection
import org.company.app.theme.cryptoColors

@Composable
fun WalletChartView(
    walletBalance: ChartBalance,
    walletChartBalance: Map<Period, List<ChartBalance>>,
    fiatCurrency: FiatCurrency,
    cryptoCurrency: CryptoCurrency,
    modifier: Modifier = Modifier
) {
    var selectedChartBalance by remember { mutableStateOf<ChartBalance?>(null) }

    var selectedPeriod by remember {
        mutableStateOf(Period.ONE_YEAR)
    }

    val periodChartBalances by remember(selectedPeriod, walletChartBalance) {
        derivedStateOf {
            walletChartBalance[selectedPeriod]
        }
    }

    val periodChartPrices by remember(periodChartBalances) {
        derivedStateOf {
            periodChartBalances?.map { it.asChartPrice() }
        }
    }

    val periodPercentChange by remember(
        walletChartBalance,
        selectedChartBalance,
        periodChartBalances
    ) {
        derivedStateOf {
            periodChartPrices?.filter { it.time < (selectedChartBalance?.time ?: 0) }
                ?.getPerformancePercent()
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

    periodChartPrices?.let {
        Column(modifier) {
            ChartView(
                modifier = modifier.fillMaxHeight(fraction = 0.90f),
                chartPrices = it,
                selectedPeriod = selectedPeriod,
                selectedChartPrice = selectedChartBalance?.asChartPrice(),
                accentColor = cryptoCurrency.color,
                headerRow = {
                    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Row {
                                Text(
                                    (selectedChartBalance
                                        ?: walletBalance).asChartPrice().value.toFiatString(
                                        fiatCurrency
                                    ),
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1
                                )
                                if (periodPercentChange != null) {
                                    Spacer(modifier.width(10.dp))
                                    Text(
                                        "$arrowSymbol $periodPercentChange %",
                                        style = TextStyle(color = percentColor)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                "${
                                    (selectedChartBalance
                                        ?: walletBalance).balance * 0.00000001
                                } ${cryptoCurrency.symbol}",
                                style = TextStyle(color = cryptoCurrency.color),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        PeriodSelection(selectedPeriod) {
                            selectedChartBalance = null
                            selectedPeriod = it
                        }
                    }
                },
                onSelectedMarketPrice = {
                    selectedChartBalance = periodChartBalances?.find { it.time == it.time }
                }
            )
        }
    } ?: LoadingBox()
}

@Composable
fun WalletEmpty(
    fiatCurrency: FiatCurrency, cryptoCurrency: CryptoCurrency
) {
    Column {
        Text(
            0.toDouble().toFiatString(fiatCurrency),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            "${
                0
            } ${cryptoCurrency.symbol}",
            style = TextStyle(color = cryptoCurrency.color),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}

private fun List<ChartPrice>.getPerformancePercent(): Double {
    val first = this.first().value
    val last = this.last().value
    return kotlin.math.floor(
        (100 * (last - first) / first)
                * 100.0
    ) / 100.0
}

private fun ChartBalance.asChartPrice() = ChartPrice(time, marketValue * balance)