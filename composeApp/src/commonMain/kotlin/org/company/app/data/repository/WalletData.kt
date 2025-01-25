package org.company.app.data.repository

import org.company.app.platform.BitcoinWallet

interface WalletData {
    suspend fun encrypt(walletData: BitcoinWallet.WalletData)
    suspend fun decrypt(): BitcoinWallet.WalletData?
}