package org.company.app.platform

import org.bitcoinj.core.NetworkParameters
import org.bitcoinj.core.TransactionConfidence
import org.bitcoinj.crypto.ECKey
import org.bitcoinj.kits.WalletAppKit
import org.bitcoinj.wallet.DeterministicSeed
import org.bitcoinj.wallet.Wallet
import org.company.app.domain.model.wallet.Network
import java.io.File

actual fun createPlatformBitcoinWallet(network: Network): PlatformBitcoinWallet? =
    object : PlatformBitcoinWallet {

        private val walletFolder = File("/wallet")
        private val walletFileName = "bitcoin"
        private val networkParameters = NetworkParameters.fromID(network.id)

        private var kit: WalletAppKit = createAndSetupWalletKit()
        private var isSetup = false

        private var setupListener: (() -> Unit)? = null
        private var balanceListener: ((Long) -> Unit)? = null
        private var transactionsListener: ((org.company.app.domain.model.crypto.Transaction) -> Unit)? = null

        override fun create(): Boolean {
            kit.setBlockingStartup(false)
            kit.startAsync()
            return true
        }

        override fun load(mnemonicPhrase: List<String>, creationTime: Long): Boolean {
            val seed = DeterministicSeed(mnemonicPhrase, null, "", creationTime)
            return restoreWallet(seed)
        }

        override fun load(mnemonicPhrase: List<String>, creationTime: Long, password: String): Boolean {
            val seed = DeterministicSeed(mnemonicPhrase, null, password, creationTime)
            return restoreWallet(seed)
        }

        private fun restoreWallet(seed: DeterministicSeed): Boolean {
            kit = createAndSetupWalletKit().apply {
                restoreWalletFromSeed(seed)
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

        override fun getBalance(): Long = kit.wallet().balance.toSat()

        override fun getTransactions(): List<org.company.app.domain.model.crypto.Transaction> =
            kit.wallet().getTransactions(true).map { it.toTransaction() }

        override fun isSetup(): Boolean = isSetup

        override fun getPublicAddress(): String =
            kit.wallet().currentReceiveAddress().toString()

        override fun getNewPublicAddress(): String =
            kit.wallet().freshReceiveAddress().toString()

        override fun getMnemonicPhrase(): List<String>? =
            kit.wallet().keyChainSeed?.mnemonicCode?.toList()

        override fun getCreationTime(): Long? =
            kit.wallet().keyChainSeed?.creationTimeSeconds

        private fun createWalletFolderIfNecessary() {
            if (!walletFolder.exists()) walletFolder.mkdirs()
        }

        private fun createAndSetupWalletKit(): WalletAppKit {
            createWalletFolderIfNecessary()
            return object : WalletAppKit(networkParameters, walletFolder, walletFileName) {
                override fun onSetupCompleted() {
                    if (wallet().importedKeys.isEmpty()) {
                        wallet().importKey(ECKey())
                    }
                    wallet().setupWalletListeners()
                    isSetup = true
                    setupListener?.invoke()
                }
            }
        }

        private fun Wallet.setupWalletListeners() {
            addCoinsReceivedEventListener { _, _, _, newBalance ->
                balanceListener?.invoke(newBalance.value)
            }

            addCoinsSentEventListener { _, _, _, newBalance ->
                newBalance?.value?.let { balanceListener?.invoke(it) }
            }

            addTransactionConfidenceEventListener { _, _ ->
                getTransactions(true).forEach {
                    transactionsListener?.invoke(it.toTransaction())
                }
            }
        }

        private fun org.bitcoinj.core.Transaction.toTransaction(): org.company.app.domain.model.crypto.Transaction {
            val status = when (confidence.confidenceType) {
                TransactionConfidence.ConfidenceType.BUILDING -> org.company.app.domain.model.crypto.Transaction.Status.DONE
                TransactionConfidence.ConfidenceType.PENDING,
                TransactionConfidence.ConfidenceType.IN_CONFLICT -> org.company.app.domain.model.crypto.Transaction.Status.PENDING
                TransactionConfidence.ConfidenceType.DEAD -> org.company.app.domain.model.crypto.Transaction.Status.FAILED
                else -> org.company.app.domain.model.crypto.Transaction.Status.UNKNOWN
            }

            return org.company.app.domain.model.crypto.Transaction(
                time = this.updateTime?.time ?: this.lockTime,
                type = org.company.app.domain.model.crypto.Transaction.Type.UNKNOWN,
                status = status,
                amount = this.getValue(kit.wallet()).toSat()
            )
        }
    }
