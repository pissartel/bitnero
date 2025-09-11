package org.company.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiChartPriceList(
    val prices: List<List<String>>,
    val market_caps: List<List<String>>
)