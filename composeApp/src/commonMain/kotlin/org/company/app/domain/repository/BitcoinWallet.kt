package org.company.app.domain.repository

import WalletState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.company.app.domain.model.crypto.Transaction
import org.company.app.domain.model.wallet.WalletData

interface BitcoinWallet {
    val state: StateFlow<WalletState>
    val balance: StateFlow<Long?>
    val publicAddress: StateFlow<String?>
    val transactionHistory: StateFlow<List<Transaction>>

    suspend fun start(): Flow<WalletData>
    suspend fun load(data: WalletData?)
    suspend fun load(data: WalletData?, password: String)
}
