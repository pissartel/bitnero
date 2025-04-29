package org.company.app.data.local

import com.russhwolf.settings.set
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.company.app.data.repository.WalletData
import org.company.app.platform.BitcoinWallet
import org.company.app.platform.createEncryptedLocalPref

class WalletDataRepository : WalletData {

    private val encryptedLocalPref = createEncryptedLocalPref("wallet-data")

    override suspend fun encrypt(walletData: BitcoinWallet.WalletData) {
        val walletDataString = Json.encodeToString(walletData)
        encryptedLocalPref[WALLET_DATA_KEY] = walletDataString
    }

    override suspend fun decrypt(): BitcoinWallet.WalletData? {
        val encryptedWalletDataString = encryptedLocalPref.getString(
            WALLET_DATA_KEY,
            defaultValue = ""
        )
        if (encryptedWalletDataString.isEmpty()) return null
        val walletData = Json.decodeFromString<BitcoinWallet.WalletData>(encryptedWalletDataString)
        return walletData
    }

    companion object {
        private const val WALLET_DATA_KEY = "wallet-data-key"
    }
}