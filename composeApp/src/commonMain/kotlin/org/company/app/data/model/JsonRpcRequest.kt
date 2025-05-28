package org.company.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class JsonRpcRequest<T>(
    val jsonrpc: String = "2.0",
    val id: Int = 1,
    val method: String,
    val params: T
)

@Serializable
data class JsonRpcResponse<T>(
    val result: T? = null,
    val error: RpcError? = null
)

@Serializable
data class RpcError(val code: Int, val message: String)

