package org.company.app.data.repository

import org.company.app.data.model.CreateTransactionParams
import org.company.app.data.model.TransactionResponse
import org.company.app.data.remote.ExchangeClient

class ExchangeRepository(
    private val client: ExchangeClient
) {
    suspend fun buyBitcoin(
        from: String,
        amount: String,
        payoutAddress: String
    ): TransactionResponse {
        val params = CreateTransactionParams(
            from = from,
            to = "btc",
            amount = amount,
            address = payoutAddress
        )
        return client.createTransaction(params)
    }

}
