package com.pipbeacon.app.data.provider.model

import com.pipbeacon.app.core.MarketType

data class MarketQuote(
    val symbol: String,
    val marketType: MarketType,
    val bidPrice: Double,
    val askPrice: Double,
    val midPrice: Double,
    val high24h: Double? = null,
    val low24h: Double? = null,
    val change24h: Double? = null,
    val changePercent24h: Double? = null,
    val timestamp: Long,
    val providerId: String,
    val providerName: String,
    val isStale: Boolean = false,
    val staleDurationMs: Long = 0L,
    val statusText: String = "FRESH"
)
