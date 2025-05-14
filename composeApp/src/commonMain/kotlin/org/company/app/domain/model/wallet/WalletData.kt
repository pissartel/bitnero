package org.company.app.domain.model.wallet

import kotlinx.serialization.Serializable

@Serializable
data class WalletData(val mnemonicPhrase: List<String>, val creationTime: Long)
