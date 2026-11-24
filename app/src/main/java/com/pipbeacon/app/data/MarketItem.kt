package com.pipbeacon.app.data

import com.pipbeacon.app.core.MarketType
import com.pipbeacon.app.core.SignalType

data class MarketItem(
    val id: String,
    val symbol: String,
    val name: String,
    val marketType: MarketType,
    val currentPrice: Double?,
    val change24h: Double?,
    val changePercent24h: Double?,
    val high24h: Double?,
    val low24h: Double?,
    val pipSpread: Double? = null,
    val sparkline: List<Double> = emptyList(),
    val isProviderLive: Boolean = false,
    val signalSummary: SignalType = SignalType.NEUTRAL,
    val category: String = "Major",
    val providerName: String? = null,
    val quoteTimestamp: Long? = null,
    val isStale: Boolean = false,
    val statusText: String = "Waiting for permitted data provider"
)
