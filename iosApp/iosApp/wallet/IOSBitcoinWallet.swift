//
//  IOSBitcoinWallet.swift
//  iosApp
//
//  Created by pierre issartel on 06/05/2025.
//

import Foundation
import BitcoinDevKit
import Combine
import ComposeApp

class IOSBitcoinWallet:  ComposeApp.IOSBitcoinWalletFactory {

    func create() -> String {
       return "IOS working"
    }
}

