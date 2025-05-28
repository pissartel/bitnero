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

class IOSBitcoinWallet :PlatformBitcoinWallet {
    
    // MARK: - Properties
    private var wallet: Wallet? = nil
    private var connection: Connection? = nil
    private var mnemonic: Mnemonic? = nil
    private var descriptor: Descriptor? = nil
    private var changeDescriptor: Descriptor? = nil
    
    private var _isSetup : Bool = false
    
    private var balanceListener: ((KotlinLong) -> Void)?
    private var setupListener: (() -> Void)? = nil
    private var txListener: ((ComposeApp.Transaction) -> Void)?
    
    // MARK: - State
    private var balanceSat: Int64 = 0
    private var creationTimestamp: Int64 = 0
    private var address: String? = nil
    private var mnemonicWords: [String]?
    
    init() {
        setupWallet()
    }
    
    func getBalance() -> Int64 {
        let wallet = self.wallet
        let balance = wallet?.balance()
        return Int64(exactly:(balance?.total.toSat())!)!
    }
    
    func getTransactions() -> [ComposeApp.Transaction] {
        return []
    }
    
    func isSetup() -> Bool {
        return _isSetup
    }
    
    
    func getCreationTime() -> KotlinLong? {
        return KotlinLong(value: creationTimestamp)
    }
    
    func getMnemonicPhrase() -> [String]? {
        return mnemonicWords
    }
    
    func getPublicAddress() -> String {
       return address ?? getNewPublicAddress()
    }
    
    func getNewPublicAddress() -> String {
        let wallet = self.wallet
        let connection = self.connection
        let addressInfo = wallet?.revealNextAddress(keychain: KeychainKind.external)
        let _ =  try? wallet?.persist(connection: self.connection!)
        return (addressInfo?.address.description) ?? ""
    }
    
    
    func setBalanceListener(listener: @escaping (KotlinLong) -> Void) {
        
    }
    
    func setOnSetupListener(listener: @escaping () -> Void) {
        setupListener = listener
        if(_isSetup) {
            setupListener?()
        }
        print(setupListener==nil)
    }
    
    func setTransactionListener(listener: @escaping (ComposeApp.Transaction) -> Void) {
        
    }
    
    
    func create_() -> Bool {
        setupListener?()
        guard let connection = try? IOSBitcoinWallet.createConnection() else {
            print("conncetion failed")
            return false
        }
        self.connection = connection
        guard
            let descriptor = self.descriptor,
            let changeDescriptor = changeDescriptor,
            let wallet = try? Wallet(
                descriptor: descriptor,
                changeDescriptor: changeDescriptor,
                network: Network.regtest,
                connection: connection
            ) else {
            print("wallet failed")
            return false
        }
        
        self.wallet = wallet
        return true
    }
    
    func load(mnemonicPhrase: [String], creationTime: Int64) -> Bool {
        
        let phrase = mnemonicPhrase.joined(separator: " ")
        guard let mnemonic = try? Mnemonic.fromString(mnemonic: phrase) else{
            print("mnemonic failed")
            return false
        }
    
        
        let network = BitcoinDevKit.Network.regtest
        
        setupListener?()
        guard let connection = try? IOSBitcoinWallet.createConnection() else {
            print("conncetion failed")
            return false
        }
        self.connection = connection

        
        // 2. Générer la clé privée maître (xprv)
        let secretKey = DescriptorSecretKey(
            network: network,
            mnemonic: mnemonic,
            password: nil
        )
        descriptor = Descriptor.newBip86(
            secretKey: secretKey,
            keychain: .external,
            network: network
        )
        changeDescriptor = Descriptor.newBip86(
            secretKey: secretKey,
            keychain: .internal,
            network: network
        )
        
        // 5. Créer le wallet
        guard let connection = self.connection,
              let descriptor = descriptor,
              let changeDescriptor = changeDescriptor,
              let wallet = try? Wallet(
                descriptor: descriptor,
                changeDescriptor: changeDescriptor,
                network: Network.regtest,
                connection:connection
              ) else {
            print("wallet failed")
            return false
        }
        
        return true
    }
    
    
    func load(mnemonicPhrase: [String], creationTime: Int64, password: String) -> Bool {
        return false
    }
    
    private func setupWallet() {
        
        print("setup start")
        
        let network = BitcoinDevKit.Network.regtest
        
        let mnemonic = Mnemonic(wordCount: WordCount.words12)
        mnemonicWords = [ mnemonic.description]
        
        let secretKey = DescriptorSecretKey(
            network: network,
            mnemonic: mnemonic,
            password: nil
        )
        descriptor = Descriptor.newBip86(
            secretKey: secretKey,
            keychain: .external,
            network: network
        )
        changeDescriptor = Descriptor.newBip86(
            secretKey: secretKey,
            keychain: .internal,
            network: network
        )
        print("setup end")
        _isSetup = true
    }
    
    private static func createConnection() throws -> Connection {
        let documentsDirectoryURL = URL.documentsDirectory
        let walletDataDirectoryURL = documentsDirectoryURL.appendingPathComponent("wallet_data")
        
        if FileManager.default.fileExists(atPath: walletDataDirectoryURL.path) {
            try FileManager.default.removeItem(at: walletDataDirectoryURL)
        }
        
        try FileManager.default.ensureDirectoryExists(at: walletDataDirectoryURL)
        try FileManager.default.removeOldFlatFileIfNeeded(at: documentsDirectoryURL)
        let persistenceBackendPath = walletDataDirectoryURL.appendingPathComponent("wallet.sqlite")
            .path
        let connection = try Connection(path: persistenceBackendPath)
        return connection
    }
    
    
    private func ensureDirectoryExists(at url: URL) throws {
        var isDir: ObjCBool = false
        if FileManager.default.fileExists(atPath: url.path, isDirectory: &isDir) {
            if !isDir.boolValue {
                try FileManager.default.removeItem(at: url)
            }
        }
        if !FileManager.default.fileExists(atPath: url.path) {
            try FileManager.default.createDirectory(at: url, withIntermediateDirectories: true, attributes: nil)
        }
    }
}

import Foundation

enum WalletError: Error {
    case blockchainConfigNotFound
    case dbNotFound
    case notSigned
    case walletNotFound
    
    var message: String {
        switch self {
            
        case .blockchainConfigNotFound: return ""
        case .dbNotFound: return ""
        case .notSigned: return ""
        case .walletNotFound: return ""
        }
    }
}

fileprivate extension FileManager {
    
    func deleteAllContentsInDocumentsDirectory() throws {
        let documentsURL = URL.documentsDirectory
        let contents = try contentsOfDirectory(
            at: documentsURL,
            includingPropertiesForKeys: nil,
            options: []
        )
        for fileURL in contents {
            try removeItem(at: fileURL)
        }
    }
    
    func ensureDirectoryExists(at url: URL) throws {
        var isDir: ObjCBool = false
        if fileExists(atPath: url.path, isDirectory: &isDir) {
            if !isDir.boolValue {
                try removeItem(at: url)
            }
        }
        if !fileExists(atPath: url.path) {
            try createDirectory(at: url, withIntermediateDirectories: true, attributes: nil)
        }
    }
    
    func removeOldFlatFileIfNeeded(at directoryURL: URL) throws {
        let flatFileURL = directoryURL.appendingPathComponent("wallet_data")
        var isDir: ObjCBool = false
        if fileExists(atPath: flatFileURL.path, isDirectory: &isDir) {
            if !isDir.boolValue {
                try removeItem(at: flatFileURL)
            }
        }
    }
    
}
