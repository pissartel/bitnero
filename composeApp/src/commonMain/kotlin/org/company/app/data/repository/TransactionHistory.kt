package org.company.app.data.repository

import kotlinx.coroutines.flow.StateFlow
import org.company.app.domain.model.crypto.Transaction

interface TransactionHistory {
    suspend fun getHistory(): StateFlow<List<Transaction>>
    suspend fun addTransaction(transaction: Transaction)
    suspend fun reset()
}