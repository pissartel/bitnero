package org.company.app.platform

import org.company.app.domain.model.crypto.Transaction
import org.company.app.domain.model.wallet.Network

expect fun createPlatformBitcoinWallet(network: Network): PlatformBitcoinWallet?

interface PlatformBitcoinWallet {
    suspend fun start(): Boolean
    suspend fun load(mnemonicPhrase: List<String>, creationTime: Long): Boolean
    suspend fun load(mnemonicPhrase: List<String>, creationTime: Long, password: String): Boolean
    fun setSetupListener(listener: () -> Unit)
    fun setBalanceListener(listener: (Long) -> Unit)
    fun setTransactionListener(listener: (Transaction) -> Unit)
    fun getBalance(): Long
    fun getPublicAddress(): String
    fun getMnemonicPhrase(): List<String>?
    fun getCreationTime(): Long?
}