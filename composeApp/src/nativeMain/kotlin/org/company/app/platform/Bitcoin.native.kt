package org.company.app.platform

import org.company.app.domain.model.wallet.Network

interface IOSBitcoinWalletFactory {
    fun create(): PlatformBitcoinWallet
}

// Add this variable to store the Swift implementation
private var iOSBitcoinWalletFactory: IOSBitcoinWalletFactory? = null

// Add this function to be called from Swift
fun setNativeResponseFactory(factory: IOSBitcoinWalletFactory) {
    iOSBitcoinWalletFactory = factory
}

actual fun createPlatformBitcoinWallet(network: Network): PlatformBitcoinWallet? =
    iOSBitcoinWalletFactory?.create()