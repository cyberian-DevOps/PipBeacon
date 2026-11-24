package com.pipbeacon.app.data.provider

import com.pipbeacon.app.core.TimeFrame
import com.pipbeacon.app.data.provider.model.MarketQuote
import com.pipbeacon.app.data.provider.model.OhlcCandle

interface ForexDataProvider : MarketDataProvider {
    suspend fun fetchForexQuote(symbol: String): Result<MarketQuote>

    suspend fun fetchForexQuotes(symbols: List<String>): Result<List<MarketQuote>>

    suspend fun fetchForexCandles(
        symbol: String,
        timeFrame: TimeFrame,
        count: Int
    ): Result<List<OhlcCandle>>
}
