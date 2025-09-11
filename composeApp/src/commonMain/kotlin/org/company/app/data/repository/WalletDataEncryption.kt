package org.company.app.data.repository

import org.company.app.domain.model.wallet.WalletData

interface WalletDataEncryption {
    suspend fun encrypt(walletData: WalletData)
    suspend fun decrypt(): WalletData?
}