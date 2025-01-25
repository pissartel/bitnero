package org.company.app.presentation.ui.components.chart

import InteractiveGraph
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.company.app.domain.model.Period
import org.company.app.domain.model.crypto.ChartPrice
import org.company.app.theme.convertMillisToReadableDate

@Composable
internal fun ChartView(
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
                        Period.ONE_HOUR -> "mm:ss"
                        Period.ONE_DAY -> "HH:mm"
                        Period.ONE_WEEK -> "MM dd · HH:mm"
                        Period.ONE_MONTH -> "MM dd"
                        Period.ONE_YEAR -> "MM dd yyyy"
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
