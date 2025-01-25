package org.company.app.domain.model.crypto

class ChartBalance(
    time: Long,
    val marketValue: Double,
    val balance: Long
) : ChartData(time)