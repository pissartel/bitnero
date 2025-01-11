package org.company.app.presentation.ui.components

import InteractiveGraph
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.company.app.domain.model.crypto.ChartPrice
import org.company.app.domain.model.fiat.FiatCurrency
import org.company.app.domain.model.fiat.FiatCurrency.Companion.toFiatString
import org.company.app.presentation.ui.components.Period.*
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
        mutableStateOf(ONE_YEAR)
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

@Composable
private fun ChartView(
    headerRow: @Composable () -> Unit,
    selectedChartPrice: ChartPrice?,
    chartPrices: List<ChartPrice>,
    accentColor: Color,
    selectedPeriod: Period,
    onSelectedMarketPrice: (ChartPrice?) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedPointIndex by remember(chartPrices, selectedChartPrice) {
        derivedStateOf {
            chartPrices.indexOf(selectedChartPrice).takeIf { index -> index >= 0 }
        }
    }

    Column(modifier = Modifier.fillMaxSize())
    {
        headerRow()
        Spacer(modifier = Modifier.weight(1f))
        InteractiveGraph(
            graphColor = accentColor,
            selectedPointIndex = selectedPointIndex,
            modifier = modifier.fillMaxSize(),
            timeData = chartPrices.map { it.time },
            yData = chartPrices.map { it.value },
            convertXCallback = { time: Long ->
                time.convertMillisToReadableDate(
                    when (selectedPeriod) {
                        ONE_HOUR -> "mm:ss"
                        ONE_DAY -> "HH:mm"
                        ONE_WEEK -> "MM dd · HH:mm"
                        ONE_MONTH -> "MM dd"
                        ONE_YEAR -> "MM dd yyyy"
                    }
                )
            },
            onPointSelected = { selectedIndex ->
                val chartPrice = selectedIndex?.let { chartPrices.getOrNull(it) }
                onSelectedMarketPrice(chartPrice)
            }
        )
        Spacer(modifier = Modifier.weight(1f))
        PeriodAxis(selectedPeriod, chartPrices.first().time, chartPrices.last().time)
    }
}

@Composable
private fun PeriodAxis(period: Period, start: Long, end: Long) {
    val time = when (period) {
        ONE_HOUR -> {
            val times = generateRangeWithDivisions(
                start,
                end,
                8
            )
            times.mapIndexed { index, time ->
                val previousHour = times.getOrNull(index - 1)?.toLocalDateTime()?.hour
                val hour = time.toLocalDateTime().hour
                if (previousHour != null && hour != previousHour)
                    time.convertMillisToReadableDate(
                        "HHhmm"
                    ) else time.convertMillisToReadableDate("mm")
            }
        }

        ONE_DAY -> generateRangeWithDivisions(
            start,
            end,
            8
        ).map { it.convertMillisToReadableDate("HH") + "h" }

        ONE_WEEK -> generateRangeWithDivisions(
            start,
            end,
            7
        ).map {
            it.toLocalDateTime().dayOfWeek.toString().take(3)
        }

        ONE_MONTH -> generateRangeWithDivisions(
            start,
            end,
            4
        ).map { it.convertMillisToReadableDate("dd") }

        ONE_YEAR -> generateRangeWithDivisions(
            start,
            end,
            12
        ).map { it.convertMillisToReadableDate("MM") }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        time.forEach {
            Text(
                it,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                ),
            )
        }
    }
}

private fun Long.convertMillisToYearString(): String {
    val localDateTime = this.toLocalDateTime()
    return localDateTime.year.toString()
}

private fun Long.convertMillisToMonthString(): String {
    val localDateTime = this.toLocalDateTime()
    return localDateTime.month.toString().take(3)
}

private fun Long.convertMillisToReadableDate(format: String): String {
    val localDateTime = this.toLocalDateTime()
    return format
        .replace("yyyy", localDateTime.year.toString().takeLast(2))
        .replace("MM", localDateTime.month.toString().padStart(2, '0').take(3))
        .replace("dd", localDateTime.dayOfMonth.toString().padStart(2, '0'))
        .replace("HH", localDateTime.hour.toString().padStart(2, '0'))
        .replace("mm", localDateTime.minute.toString().padStart(2, '0'))
        .replace("ss", localDateTime.second.toString().padStart(2, '0'))
}

private fun Long.toLocalDateTime(): LocalDateTime {
    val startInstant = Instant.fromEpochMilliseconds(this)
    return startInstant.toLocalDateTime(TimeZone.currentSystemDefault())
}

private fun generateRangeWithDivisions(start: Long, end: Long, divisions: Int): List<Long> {
    require(divisions > 0) { "Divisions must be greater than 0" }

    val range = end - start
    val step = range / divisions
    val result = mutableListOf<Long>()

    for (i in 0..divisions) {
        val currentValue = start + (i * step)
        result.add(currentValue)
    }

    return result
}


enum class Period(val title: String, val days: Int) {
    ONE_HOUR("1H", 1),
    ONE_DAY("1D", 1),
    ONE_WEEK("1W", 7),
    ONE_MONTH("1M", 30),
    ONE_YEAR("1Y", 365),
}

private const val ONE_HOUR_IN_MILLIS = 1000 * 60 * 60L
