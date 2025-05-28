package org.company.app.platform

interface CryptoHelper {
    fun hmacSha512(key: String, message: String): String
}

expect class CryptoHelperImpl : CryptoHelper {
    override fun hmacSha512(key: String, message: String): String
}