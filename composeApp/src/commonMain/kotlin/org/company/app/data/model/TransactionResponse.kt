package org.company.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TransactionResponse(
    val id: String,
    val amountExpectedFrom: String,
    val status: String,
    val payinAddress: String,
    val payoutAddress: String
)
