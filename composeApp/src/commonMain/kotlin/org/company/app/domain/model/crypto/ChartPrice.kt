package org.company.app.domain.model.crypto

import kotlinx.serialization.Serializable

@Serializable
data class ApiChartPriceList(
    val prices: List<List<String>>,
    val market_caps: List<List<String>>
)

data class ChartPrice(
    val time : Long,
    val value: Double
)

