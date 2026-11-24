package com.pipbeacon.app.data.provider

import com.pipbeacon.app.core.TimeFrame
import com.pipbeacon.app.data.provider.model.MarketQuote
import com.pipbeacon.app.data.provider.model.OhlcCandle

interface CryptoDataProvider : MarketDataProvider {
    suspend fun fetchCryptoQuote(symbol: String): Result<MarketQuote>

    suspend fun fetchCryptoQuotes(symbols: List<String>): Result<List<MarketQuote>>

    suspend fun fetchCryptoCandles(
        symbol: String,
        timeFrame: TimeFrame,
        count: Int
    ): Result<List<OhlcCandle>>
}
