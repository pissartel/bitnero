package org.company.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateTransactionParams(
    val from: String,
    val to: String,
    val amount: String,
    val address: String,
    val refundAddress: String? = null
)