package org.company.app.domain.model.wallet

enum class Network(
    val id: String,
    segwitAddressHrp: String? = null,
    uriScheme: String? = null
) {
    /** The main Bitcoin network, known as {@code "mainnet"}, with {@code id} string {@code "org.bitcoin.production"}  */
    MAINNET("org.bitcoin.production", "main", "prod"),

    /** The Bitcoin test network, known as {@code "testnet"}, with {@code id} string {@code "org.bitcoin.test"}  */
    TESTNET("org.bitcoin.test", "test"),

    /** The Bitcoin signature-based test network, known as {@code "signet"}, with {@code id} string {@code "org.bitcoin.signet"}  */
    SIGNET("org.bitcoin.signet", "sig"),

    /** A local Bitcoin regression test network, known as {@code "regtest"}, with {@code id} string {@code "org.bitcoin.regtest"}  */
    REGTEST("org.bitcoin.regtest");
}