package org.company.app.domain.repository.impl

import WalletState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import org.company.app.domain.model.wallet.WalletData
import org.company.app.domain.model.wallet.WalletException
import org.company.app.domain.repository.BitcoinWallet
import org.company.app.platform.PlatformBitcoinWallet

class BitcoinWalletImpl(private val platformBitcoinWallet: PlatformBitcoinWallet) : BitcoinWallet {

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

    init {
        _state.tryEmit(WalletState.UNKNOWN)
        with(platformBitcoinWallet) {
            setSetupListener {
                _state.tryEmit(WalletState.READY)
            }
            setBalanceListener {
                _balance.tryEmit(it)
            }
            setTransactionListener {
                _transactionHistory.tryEmit(
                    _transactionHistory.value.plus(it)
                )
            }
        }
    }

    override suspend fun start(): Flow<WalletData> =
        flow {
            _state.tryEmit(WalletState.CREATING)

            val success = platformBitcoinWallet.start()
            if (!success) {
                _state.tryEmit(WalletState.NOT_CREATED)
                throw WalletException.StartException("Wallet start failed")
            }

            // Wait for Wallet set up to emit wallet data
            _state.collect {
                println("state = $_state")
                if (_state.value == WalletState.READY) {
                    val mnemonicPhrase = platformBitcoinWallet.getMnemonicPhrase()
                        ?: throw WalletException.LoadException("Mnemonic phrase is null")
                    val creationTime = platformBitcoinWallet.getCreationTime()
                        ?: throw WalletException.LoadException("Creation time is null")
                    emit(
                        WalletData(
                            mnemonicPhrase,
                            creationTime
                        )
                    )
                }
            }
        }

    override suspend fun load(data: WalletData?) {
        if (data == null) {
            _state.tryEmit(WalletState.NOT_CREATED)
            return
        }
        val success = platformBitcoinWallet.load(
            mnemonicPhrase = data.mnemonicPhrase,
            creationTime = data.creationTime
        )
        if (!success) {
            _state.tryEmit(WalletState.NOT_CREATED)
            throw WalletException.LoadException("Wallet kit failed")
        }
        _state.tryEmit(WalletState.READY)
    }

    override suspend fun load(data: WalletData?, password: String) {
        if (data == null) {
            _state.tryEmit(WalletState.NOT_CREATED)
            return
        }
        val success = platformBitcoinWallet.load(
            mnemonicPhrase = data.mnemonicPhrase,
            creationTime = data.creationTime,
            password = password
        )
        if (!success) {
            _state.tryEmit(WalletState.NOT_CREATED)
            throw WalletException.LoadException("Wallet kit failed")
        }
        _state.tryEmit(WalletState.READY)
    }
}