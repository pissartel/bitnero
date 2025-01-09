package org.company.app.domain.model.crypto

import kotlinx.serialization.Serializable

@Serializable
data class ApiMarketPriceList(
    val prices: List<List<String>>,
    val market_caps: List<List<String>>
)


data class MarketPrice(
    val time : Long,
    val value: Double
)

