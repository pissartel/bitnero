package org.company.app.domain.model.wallet

sealed class WalletException(override val message: String?) : Exception(message) {
    data class StartException(override val message: String?) : WalletException(message)
    data class LoadException(override val message: String?) : WalletException(message)
}