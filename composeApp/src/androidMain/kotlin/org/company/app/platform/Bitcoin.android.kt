package org.company.app.platform

import android.os.Environment
import io.kamel.core.utils.File
import org.bitcoinj.core.Coin.SATOSHI
import org.bitcoinj.core.NetworkParameters
import org.bitcoinj.kits.WalletAppKit
import org.bitcoinj.wallet.DeterministicSeed
import org.bitcoinj.wallet.Wallet


actual fun createBitcoinWallet(network: Network): BitcoinWallet = object : BitcoinWallet() {

    private val walletFolder = File(Environment.getExternalStorageDirectory(), "/" + "wallet")
    private val walletFileName = "bitcoin"
    private val networkParameters = NetworkParameters.fromID(network.id)
    private var kit = WalletAppKit(networkParameters, walletFolder, walletFileName)


    override val balance: Long
        get() = kit.wallet().getBalanceFuture(SATOSHI, Wallet.BalanceType.AVAILABLE).get().value

    override suspend fun create(): WalletData {
        createWalletFolderIfNecessay()
        val keyChainSeed = kit.wallet()?.keyChainSeed
            ?: throw WalletException.CreationException("Wallet kit failed")
        return keyChainSeed.mnemonicCode?.toList()
            ?.let { WalletData(it, keyChainSeed.creationTimeSeconds) }
            ?: throw WalletException.CreationException("Wallet kit failed")
    }

    override suspend fun load(data: WalletData) {
        val seed = DeterministicSeed(data.mnemonicPhrase, null, "", data.creationTime)
        kit.restoreWalletFromSeed(seed) ?: throw WalletException.LoadException("Wallet kit failed")
    }

    override suspend fun load(data: WalletData, password: String) {
        val seed = DeterministicSeed(data.mnemonicPhrase, null, password, data.creationTime)
        kit.restoreWalletFromSeed(seed) ?: throw WalletException.LoadException("Wallet kit failed")
    }

    private fun createWalletFolderIfNecessay() {
        if (walletFolder.exists()) {
            walletFolder.mkdirs()
        }
    }
}

