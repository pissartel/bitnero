package org.company.app.data.remote

import org.company.app.data.model.CreateTransactionParams
import org.company.app.data.model.JsonRpcRequest
import org.company.app.data.model.JsonRpcResponse
import org.company.app.data.model.TransactionResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.call.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import org.company.app.platform.CryptoHelper

class ExchangeClient(
    private val client: HttpClient,
    private val apiKey: String,
    private val apiSecret: String,
    private val cryptoHelper: CryptoHelper
) {
    private val endpoint = "https://api.changelly.com"

    suspend fun createTransaction(params: CreateTransactionParams): TransactionResponse {
        val body = JsonRpcRequest(
            method = "createTransaction",
            params = params
        )

        val jsonBody = Json.encodeToString(
            JsonRpcRequest.serializer(CreateTransactionParams.serializer()),
            body
        )
        val signature = cryptoHelper.hmacSha512(apiSecret, jsonBody)

        val response = client.post(endpoint) {
            contentType(ContentType.Application.Json)
            header("api-key", apiKey)
            header("sign", signature)
            setBody(jsonBody)
        }

        val rpcResponse = response.body<JsonRpcResponse<TransactionResponse>>()
        return rpcResponse.result
            ?: throw Exception("Changelly error: ${rpcResponse.error?.message}")

    }
}

