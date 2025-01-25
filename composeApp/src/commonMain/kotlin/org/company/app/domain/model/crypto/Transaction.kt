package org.company.app.domain.model.crypto

import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    val time: Long,
    val type: Type,
    val status: Status,
    val amount: Long
) {
    enum class Type {
        BUY,
        SELL,
        SEND,
        RECEIVE,
        UNKNOWN
    }

    enum class Status {
        PENDING,
        FAILED,
        DONE,
        UNKNOWN
    }
}



