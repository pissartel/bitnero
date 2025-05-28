package org.company.app.platform

actual class CryptoHelper {
    actual fun hmacSha512(key: String, message: String): String {
        TODO("Not yet implemented")
    }
}

actual class CryptoHelperImpl : CryptoHelper {
    actual override fun hmacSha512(key: String, message: String): String {
        TODO("Not yet implemented")
    }
}