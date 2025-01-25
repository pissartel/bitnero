package org.company.app.platform

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import org.bitcoinj.core.Coin
import org.bitcoinj.core.ECKey
import org.bitcoinj.core.Transaction
import org.bitcoinj.core.TransactionConfidence
import org.bitcoinj.kits.WalletAppKit
import org.bitcoinj.params.RegTestParams
import org.bitcoinj.wallet.DeterministicSeed
import org.bitcoinj.wallet.Wallet
import org.company.app.AndroidApp.Companion.APP_CONTEXT_INSTANCE
import java.io.File


actual fun createBitcoinWallet(network: Network): BitcoinWallet = object : BitcoinWallet() {

    private val walletFolder = File(
        APP_CONTEXT_INSTANCE.externalCacheDir, "wallet"
    )
    private val walletFileName = "bitcoin-wallet"
    private val networkParameters = RegTestParams.get()//NetworkParameters.fromID(network.id)

    private val _balance = MutableStateFlow<Long?>(null)
    override val balance: StateFlow<Long?> = _balance.asStateFlow()

    private val _publicAddress = MutableStateFlow<String?>(null)
    override val publicAddress: StateFlow<String?> = _publicAddress.asStateFlow()

    private val _transactionHistory =
        MutableStateFlow<List<org.company.app.domain.model.crypto.Transaction>>(
            emptyList()
        )
    override val transactionHistory: StateFlow<List<org.company.app.domain.model.crypto.Transaction>> =
        _transactionHistory.asStateFlow()

    private val _state = MutableStateFlow<WalletState>(WalletState.UNKNOWN)
    override val state: StateFlow<WalletState> = _state.asStateFlow()

    private var kit: WalletAppKit = crateWalletKit()

    init {
        // TODO check if there is already a wallet
        _state.tryEmit(WalletState.NOT_CREATED)
    }

    override suspend fun create(): Flow<WalletData> =
        flow {
            _state.tryEmit(WalletState.CREATING)

            kit.setBlockingStartup(false)
            kit.startAsync()

            // Wait for Wallet set up to emit wallet data
            _state.collect {
                if (_state.value == WalletState.READY) {
                    val keyChainSeed = kit.wallet()?.keyChainSeed
                    keyChainSeed?.mnemonicCode?.toList()
                        ?.let {
                            emit(WalletData(it, keyChainSeed.creationTimeSeconds))
                        }
                }
            }
        }

    override suspend fun load(data: WalletData) {
        val seed = DeterministicSeed(data.mnemonicPhrase, null, "", data.creationTime)
        kit.restoreWalletFromSeed(seed) ?: throw WalletException.LoadException("Wallet kit failed")
    }

    override suspend fun load(data: WalletData, password: String) {
        val seed = DeterministicSeed(data.mnemonicPhrase, null, password, data.creationTime)
        kit.restoreWalletFromSeed(seed) ?: throw WalletException.LoadException("Wallet kit failed")
    }

    private fun createWalletFolderIfNecessary() {
        if (!walletFolder.exists()) {
            val success = walletFolder.mkdirs()
        }
    }

    private fun crateWalletKit(): WalletAppKit {
        createWalletFolderIfNecessary()
        return object : WalletAppKit(networkParameters, walletFolder, walletFileName) {
            override fun onSetupCompleted() {
                println("address = ${wallet().freshReceiveAddress()}")
                println("set ready")
                _state.tryEmit(WalletState.READY)
                _balance.tryEmit(getBalance())
                _transactionHistory.tryEmit(
                    wallet().walletTransactions.map { it.transaction.toTransaction() }
                )
                _publicAddress.tryEmit(wallet().freshReceiveAddress().toString())
                if (wallet().importedKeys.size < 1) wallet().importKey(ECKey())
                wallet().setupWalletListeners()
            }
        }
    }

    private fun Wallet.setupWalletListeners() {
        this.addCoinsReceivedEventListener { wallet: Wallet?, tx: Transaction, prevBalance: Coin?, newBalance: Coin ->
            println("newBalance = $newBalance")
            _balance.tryEmit(newBalance.value)
        }
        this.addCoinsSentEventListener { wallet: Wallet?, tx: Transaction, prevBalance: Coin, newBalance: Coin? ->
            println("prevBalance = $prevBalance")
            println("newBalance = $newBalance")
            newBalance?.value?.let { _balance.tryEmit(it) }
        }
        this.addTransactionConfidenceEventListener { wallet, tx ->
            println("tx = $tx")
            this.getTransactions(true).map {
                it.toTransaction()
            }
        }
    }

    private fun getBalance(): Long = kit.wallet().balance.toSat()

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
            amount = this.getValue(kit.wallet()).toSat()
        )

    private val TAG = "Wallet"
}

