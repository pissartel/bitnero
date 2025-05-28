package org.company.app.platform

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

actual class CryptoHelperImpl : CryptoHelper {
    actual override fun hmacSha512(key: String, message: String): String {
        val hmac = Mac.getInstance("HmacSHA512")
        val secretKey = SecretKeySpec(key.toByteArray(Charsets.UTF_8), "HmacSHA512")
        hmac.init(secretKey)
        return hmac.doFinal(message.toByteArray(Charsets.UTF_8)).joinToString("") {
            "%02x".format(it)
        }
    }
}