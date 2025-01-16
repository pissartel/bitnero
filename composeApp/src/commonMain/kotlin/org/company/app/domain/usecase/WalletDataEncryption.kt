package org.company.app.domain.usecase

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.company.app.platform.BitcoinWallet
import org.company.app.platform.EncryptedToolbox

class WalletDataEncryption(
    private val encryptedToolbox: EncryptedToolbox,
) {
    suspend fun encrypt(walletData: BitcoinWallet.WalletData) {
        val walletDataString = Json.encodeToString(walletData)
        encryptedToolbox.setValueFor(WALLET_DATA_KEY, walletDataString)
    }

    suspend fun decrypt(): BitcoinWallet.WalletData? {
        val encryptedWalletDataString = encryptedToolbox.getValueFor(WALLET_DATA_KEY) ?: return null
        val walletData = Json.decodeFromString<BitcoinWallet.WalletData>(encryptedWalletDataString)
        return walletData
    }

    companion object {
        private const val WALLET_DATA_KEY = "wallet-data-key"
    }
}