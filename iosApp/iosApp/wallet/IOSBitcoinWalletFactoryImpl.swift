//
//  IOSBitcoinWalletFactoryImpl.swift
//  iosApp
//
//  Created by pierre issartel on 14/05/2025.
//

import Foundation
import ComposeApp

class IOSBitcoinWalletFactoryImpl: ComposeApp.IOSBitcoinWalletFactory {
    func create() -> PlatformBitcoinWallet {
        return IOSBitcoinWallet()
    }
}
