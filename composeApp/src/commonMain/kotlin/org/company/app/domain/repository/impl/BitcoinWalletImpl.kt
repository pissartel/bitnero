package org.company.app.domain.repository.impl

import WalletState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.company.app.domain.model.crypto.Transaction
import org.company.app.domain.model.wallet.WalletData
import org.company.app.domain.model.wallet.WalletException
import org.company.app.domain.repository.BitcoinWallet
import org.company.app.platform.PlatformBitcoinWallet

class BitcoinWalletImpl(
    private val platformBitcoinWallet: PlatformBitcoinWallet
) : BitcoinWallet {

    private val _balance = MutableStateFlow<Long?>(null)
    override val balance: StateFlow<Long?> = _balance.asStateFlow()

    private val _publicAddress = MutableStateFlow<String?>(null)
    override val publicAddress: StateFlow<String?> = _publicAddress.asStateFlow()

    private val _transactionHistory = MutableStateFlow<List<Transaction>>(emptyList())
    override val transactionHistory: StateFlow<List<Transaction>> =
        _transactionHistory.asStateFlow()

    private val _state = MutableStateFlow(WalletState.UNKNOWN)
    override val state: StateFlow<WalletState> = _state.asStateFlow()

    init {
        with(platformBitcoinWallet) {
            setBalanceListener { newBalance ->
                _balance.value = newBalance
            }
            setTransactionListener { transaction ->
                _transactionHistory.value += transaction
            }
            setOnSetupListener {
                _publicAddress.value = getPublicAddress()
                _state.value = WalletState.READY
            }
        }
    }

    override suspend fun create(): Flow<WalletData> = flow {
        _state.value = WalletState.CREATING

        val success = platformBitcoinWallet.create()
        if (!success) {
            _state.value = WalletState.NOT_CREATED
            throw WalletException.StartException("Wallet start failed")
        }

        // Wait for Wallet set up to emit wallet data
        state.filter { it == WalletState.READY }.first()

        val mnemonic = checkNotNull(platformBitcoinWallet.getMnemonicPhrase()) {
            "Mnemonic phrase is null"
        }
        val creationTime = checkNotNull(platformBitcoinWallet.getCreationTime()) {
            "Creation time is null"
        }

        emit(WalletData(mnemonic, creationTime))
    }

    override suspend fun load(data: WalletData?) {
        loadWallet(data) { mnemonic, creationTime ->
            platformBitcoinWallet.load(mnemonic, creationTime)
        }
    }

    override suspend fun load(data: WalletData?, password: String) {
        loadWallet(data) { mnemonic, creationTime ->
            platformBitcoinWallet.load(mnemonic, creationTime, password)
        }
    }

    private suspend fun loadWallet(
        data: WalletData?,
        loader: suspend (List<String>, Long) -> Boolean
    ) {
        if (data == null) {
            _state.value = WalletState.NOT_CREATED
            return
        }

        val success = loader(data.mnemonicPhrase, data.creationTime)
        if (!success) {
            _state.value = WalletState.NOT_CREATED
            throw WalletException.LoadException("Wallet load failed")
        }

        _state.value = WalletState.READY
    }
}
