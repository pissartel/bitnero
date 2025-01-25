package org.company.app.platform

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import org.company.app.domain.model.crypto.Transaction

expect fun createBitcoinWallet(network: Network): BitcoinWallet

abstract class BitcoinWallet {
    abstract val state: StateFlow<WalletState>
    abstract val balance: StateFlow<Long?>
    abstract val publicAddress: StateFlow<String?>
    abstract val transactionHistory: StateFlow<List<Transaction>>

    abstract suspend fun create(): Flow<WalletData>
    abstract suspend fun load(data: WalletData)
    abstract suspend fun load(data: WalletData, password: String)

    sealed class WalletException(override val message: String?) : Exception(message) {
        data class CreationException(override val message: String?) : WalletException(message)
        data class LoadException(override val message: String?) : WalletException(message)
    }

    @Serializable
    data class WalletData(val mnemonicPhrase: List<String>, val creationTime: Long)

    enum class WalletState {
        READY,
        UNKNOWN,
        NOT_CREATED,
        CREATING
    }
}

enum class Network(
    val id: String,
    segwitAddressHrp: String? = null,
    uriScheme: String? = null
) {
    /** The main Bitcoin network, known as {@code "mainnet"}, with {@code id} string {@code "org.bitcoin.production"}  */
    MAINNET("org.bitcoin.production", "main", "prod"),

    /** The Bitcoin test network, known as {@code "testnet"}, with {@code id} string {@code "org.bitcoin.test"}  */
    TESTNET("org.bitcoin.test", "test"),

    /** The Bitcoin signature-based test network, known as {@code "signet"}, with {@code id} string {@code "org.bitcoin.signet"}  */
    SIGNET("org.bitcoin.signet", "sig"),

    /** A local Bitcoin regression test network, known as {@code "regtest"}, with {@code id} string {@code "org.bitcoin.regtest"}  */
    REGTEST("org.bitcoin.regtest");

}