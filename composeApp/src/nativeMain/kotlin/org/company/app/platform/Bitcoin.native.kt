package org.company.app.platform

import org.company.app.domain.model.crypto.Transaction
import org.company.app.domain.model.wallet.Network


//interface IOSBitcoinWalletFactory {
//    fun create(): String
//}
//
//// Add this variable to store the Swift implementation
//private var iOSBitcoinWalletFactory: PlatformBitcoinWallet? = null
//
//// Add this function to be called from Swift
//fun setNativeResponseFactory(factory: PlatformBitcoinWallet) {
//    iOSBitcoinWalletFactory = factory
//}

actual fun createPlatformBitcoinWallet(network: Network): PlatformBitcoinWallet? =
    object : PlatformBitcoinWallet {
        override suspend fun start(): Boolean {
            TODO("Not yet implemented")
        }

        override suspend fun load(mnemonicPhrase: List<String>, creationTime: Long): Boolean {
            TODO("Not yet implemented")
        }

        override suspend fun load(
            mnemonicPhrase: List<String>,
            creationTime: Long,
            password: String
        ): Boolean {
            TODO("Not yet implemented")
        }

        override fun setSetupListener(listener: () -> Unit) {
            TODO("Not yet implemented")
        }

        override fun setBalanceListener(listener: (Long) -> Unit) {
            TODO("Not yet implemented")
        }

        override fun setTransactionListener(listener: (Transaction) -> Unit) {
            TODO("Not yet implemented")
        }

        override fun getBalance(): Long {
            TODO("Not yet implemented")
        }

        override fun getPublicAddress(): String {
            TODO("Not yet implemented")
        }

        override fun getMnemonicPhrase(): List<String>? {
            TODO("Not yet implemented")
        }

        override fun getCreationTime(): Long? {
            TODO("Not yet implemented")
        }
    }