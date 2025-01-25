package org.company.app.data.local

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.set
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.company.app.data.repository.TransactionHistory
import org.company.app.domain.model.crypto.Transaction
import org.company.app.platform.createLocalPref

class TransactionHistoryStorage : TransactionHistory {

    private val localPref: ObservableSettings = createLocalPref("transactions")

    private val history = MutableStateFlow<List<Transaction>>(emptyList())

    init {
        CoroutineScope(Dispatchers.Default).launch {
            val historyString = localPref.getString(HISTORY_KEY, "")
            if (!historyString.isEmpty()) {
                history.value = Json.decodeFromString<List<Transaction>>(historyString)
            }
        }
    }

    override suspend fun getHistory(): StateFlow<List<Transaction>> = history

    override suspend fun addTransaction(transaction: Transaction) {
        history.value += transaction
        localPref[HISTORY_KEY] = history
    }

    override suspend fun reset() {
        localPref.remove(HISTORY_KEY)
        history.value = emptyList()
    }

    companion object {
        private const val HISTORY_KEY = "history-key"
    }
}