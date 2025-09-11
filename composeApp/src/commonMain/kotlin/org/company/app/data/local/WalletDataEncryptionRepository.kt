package org.company.app.data.local

import com.russhwolf.settings.set
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.company.app.data.repository.WalletDataEncryption
import org.company.app.domain.model.wallet.WalletData
import org.company.app.platform.createEncryptedLocalPref

class WalletDataEncryptionRepository : WalletDataEncryption {

    private val encryptedLocalPref = createEncryptedLocalPref("wallet-data")

    override suspend fun encrypt(walletData: WalletData) {
        val walletDataString = Json.encodeToString(walletData)
        encryptedLocalPref[WALLET_DATA_KEY] = walletDataString
    }

    override suspend fun decrypt(): WalletData? {
        val encryptedWalletDataString = encryptedLocalPref.getString(
            WALLET_DATA_KEY,
            defaultValue = ""
        )
        if (encryptedWalletDataString.isEmpty()) return null
        val walletData = Json.decodeFromString<WalletData>(encryptedWalletDataString)
        return walletData
    }

    companion object {
        private const val WALLET_DATA_KEY = "wallet-data-key"
    }
}