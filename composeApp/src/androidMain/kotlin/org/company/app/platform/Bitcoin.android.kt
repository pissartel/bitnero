package org.company.app.platform

import org.bitcoinj.base.Coin
import org.bitcoinj.core.NetworkParameters
import org.bitcoinj.core.Transaction
import org.bitcoinj.core.TransactionConfidence
import org.bitcoinj.crypto.ECKey
import org.bitcoinj.kits.WalletAppKit
import org.bitcoinj.wallet.DeterministicSeed
import org.bitcoinj.wallet.Wallet
import org.company.app.AndroidApp.Companion.APP_CONTEXT_INSTANCE
import org.company.app.domain.model.wallet.Network
import java.io.File

actual fun createPlatformBitcoinWallet(network: Network): PlatformBitcoinWallet? =
    object : PlatformBitcoinWallet {

        private val walletFolder = File(
            APP_CONTEXT_INSTANCE.externalCacheDir, "wallet"
        )
        private val walletFileName = "bitcoin-wallet"
        private val networkParameters = NetworkParameters.fromID("org.bitcoin.production")

        private var kit: WalletAppKit? = null

        private var isSetup: Boolean = false

        private var setupListener: (() -> Unit)? = null
        private var balanceListener: ((Long) -> Unit)? = null
        private var transactionsListener: ((org.company.app.domain.model.crypto.Transaction) -> Unit)? =
            null

        override fun create(): Boolean {
            kit = createAndSetupWalletKit().apply {
                setBlockingStartup(false)
                startAsync()
            }
            return true
        }

        override fun load(mnemonicPhrase: List<String>, creationTime: Long): Boolean {
            val seed = DeterministicSeed(mnemonicPhrase, null, "", creationTime)
            println("seed load = ${seed.mnemonicCode}")
            kit = createAndSetupWalletKit().apply {
                restoreWalletFromSeed(seed)
                setBlockingStartup(false)
                startAsync()
            }
            return true
        }

        override fun load(
            mnemonicPhrase: List<String>,
            creationTime: Long,
            password: String
        ): Boolean {
            val seed = DeterministicSeed(mnemonicPhrase, null, password, creationTime)
            kit = createAndSetupWalletKit().apply {
                restoreWalletFromSeed(seed)
                setBlockingStartup(false)
                setBlockingStartup(false)
                startAsync()
            }
            return true
        }

        override fun setOnSetupListener(listener: () -> Unit) {
            setupListener = listener
        }

        override fun setBalanceListener(listener: (Long) -> Unit) {
            balanceListener = listener
        }

        override fun setTransactionListener(listener: (org.company.app.domain.model.crypto.Transaction) -> Unit) {
            transactionsListener = listener
        }

        override fun getBalance(): Long {
            return kit?.wallet()?.balance?.toSat() ?: 0L
        }

        override fun getTransactions(): List<org.company.app.domain.model.crypto.Transaction> {
            return kit?.wallet()?.getTransactions(true)?.map {
                it.toTransaction()
            } ?: emptyList()
        }

        override fun isSetup(): Boolean = isSetup

        override fun getPublicAddress(): String {
            return kit?.wallet()?.currentReceiveAddress().toString()
        }

        override fun getNewPublicAddress(): String {
            return kit?.wallet()?.freshReceiveAddress().toString()
        }

        override fun getMnemonicPhrase(): List<String>? {
            val keyChainSeed = kit?.wallet()?.keyChainSeed
            return keyChainSeed?.mnemonicCode?.toList()
        }

        override fun getCreationTime(): Long? {
            val keyChainSeed = kit?.wallet()?.keyChainSeed
            return keyChainSeed?.creationTimeSeconds
        }

        private fun createWalletFolderIfNecessary() {
            if (!walletFolder.exists()) {
                val success = walletFolder.mkdirs()
            }
        }

        private fun createAndSetupWalletKit(): WalletAppKit {
            createWalletFolderIfNecessary()
            return object : WalletAppKit(networkParameters, walletFolder, walletFileName) {
                override fun onSetupCompleted() {
                    if (wallet().importedKeys.size < 1) wallet().importKey(ECKey())
                    wallet().setupWalletListeners()
                    isSetup = true
                    setupListener?.invoke()
                }
            }
        }

        private fun Wallet.setupWalletListeners() {
            this.addCoinsReceivedEventListener { wallet: Wallet?, tx: Transaction, prevBalance: Coin?, newBalance: Coin ->
                println("newBalance = $newBalance")
                balanceListener?.invoke(newBalance.value)
            }

            this.addCoinsSentEventListener { wallet: Wallet?, tx: Transaction, prevBalance: Coin, newBalance: Coin? ->
                println("prevBalance = $prevBalance")
                println("newBalance = $newBalance")
                newBalance?.value?.let {
                    balanceListener?.invoke(newBalance.value)
                }
            }
            this.addTransactionConfidenceEventListener { wallet, tx ->
                println("tx = $tx")
                this.getTransactions(true).map {
                    it.toTransaction()
                }
            }
        }


        private fun org.bitcoinj.core.Transaction.toTransaction(): org.company.app.domain.model.crypto.Transaction =
            org.company.app.domain.model.crypto.Transaction(
                time = this.lockTime,
                type = org.company.app.domain.model.crypto.Transaction.Type.UNKNOWN,
                status = when (confidence.confidenceType) {
                    TransactionConfidence.ConfidenceType.BUILDING -> org.company.app.domain.model.crypto.Transaction.Status.DONE
                    TransactionConfidence.ConfidenceType.IN_CONFLICT,
                    TransactionConfidence.ConfidenceType.PENDING -> org.company.app.domain.model.crypto.Transaction.Status.PENDING

                    TransactionConfidence.ConfidenceType.DEAD -> org.company.app.domain.model.crypto.Transaction.Status.FAILED
                    null,
                    TransactionConfidence.ConfidenceType.UNKNOWN -> org.company.app.domain.model.crypto.Transaction.Status.UNKNOWN
                },
                amount = this.getValue(kit?.wallet()).toSat()
            )
    }