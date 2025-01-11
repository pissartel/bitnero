package org.company.app.domain.model.crypto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.jsonObject

@Serializable
data class CryptoData(
    val price: Double,
    val market_cap: Double,
    val volume_24h: Double,
    val change_24h: Double,
    val last_updated: Long
)

object CryptoDataSerializer : JsonTransformingSerializer<CryptoData>(CryptoData.serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        val jsonObject = element.jsonObject
        val currencyKey = jsonObject.keys.first()
        val fields = jsonObject[currencyKey]?.jsonObject
            ?: throw IllegalArgumentException("Missing key: $currencyKey")

        return JsonObject(
            mapOf(
                "price" to fields[currencyKey]!!,
                "market_cap" to fields["${currencyKey}_market_cap"]!!,
                "volume_24h" to fields["${currencyKey}_24h_vol"]!!,
                "change_24h" to fields["${currencyKey}_24h_change"]!!,
                "last_updated" to fields["${currencyKey}_updated_at"]!!
            )
        )
    }
}

// Un alias pour mapper toutes les cryptomonnaies
typealias CryptoResponse = Map<String, CryptoData>
