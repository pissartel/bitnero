package org.company.app.platform

expect fun createEncryptedToolbox(): EncryptedToolbox

interface EncryptedToolbox {
    fun getValueFor(key: String, doubleEncryption: Boolean = true): String?

    fun setValueFor(
        key: String,
        value: String,
        doubleEncryption: Boolean = true,
    )

    fun deleteValueFor(key: String)
}

class EncryptionException(
    override val message: String,
    override val cause: Throwable,
) : RuntimeException()