package org.company.app.platform

import org.company.app.domain.model.crypto.Transaction
import org.company.app.domain.model.wallet.Network

expect fun createPlatformBitcoinWallet(network: Network): PlatformBitcoinWallet?

interface PlatformBitcoinWallet {
    fun create(): Boolean
    fun load(mnemonicPhrase: List<String>, creationTime: Long): Boolean
    fun load(mnemonicPhrase: List<String>, creationTime: Long, password: String): Boolean
    fun setOnSetupListener(listener: () -> Unit)
    fun setBalanceListener(listener: (Long) -> Unit)
    fun setTransactionListener(listener: (Transaction) -> Unit)
    fun getBalance(): Long
    fun getTransactions(): List<Transaction>
    fun isSetup(): Boolean
    fun getPublicAddress(): String
    fun getNewPublicAddress(): String
    fun getMnemonicPhrase(): List<String>?
    fun getCreationTime(): Long?
}