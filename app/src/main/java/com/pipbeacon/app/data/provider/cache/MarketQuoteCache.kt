package com.pipbeacon.app.data.provider.cache

import com.pipbeacon.app.data.provider.model.MarketQuote
import com.pipbeacon.app.data.provider.model.OhlcCandle
import java.util.concurrent.ConcurrentHashMap

class MarketQuoteCache(
    private val forexStaleThresholdMs: Long = 300_000L, // 5 minutes
    private val cryptoStaleThresholdMs: Long = 60_000L   // 1 minute
) {
    private val quotesMap = ConcurrentHashMap<String, MarketQuote>()
    private val candlesMap = ConcurrentHashMap<String, List<OhlcCandle>>()

    fun putQuote(quote: MarketQuote) {
        quotesMap[quote.symbol] = quote.copy(isStale = false, statusText = "FRESH")
    }

    fun putQuotes(quotes: List<MarketQuote>) {
        quotes.forEach { putQuote(it) }
    }

    fun getQuote(symbol: String, currentTime: Long = System.currentTimeMillis()): MarketQuote? {
        val cached = quotesMap[symbol] ?: return null
        val threshold = if (symbol.contains("USDT") || symbol.contains("BTC") || symbol.contains("ETH")) {
            cryptoStaleThresholdMs
        } else {
            forexStaleThresholdMs
        }

        val elapsed = currentTime - cached.timestamp
        val isStale = elapsed > threshold
        return if (isStale) {
            val updated = cached.copy(
                isStale = true,
                staleDurationMs = elapsed,
                statusText = "STALE (${elapsed / 1000}s old)"
            )
            quotesMap[symbol] = updated
            updated
        } else {
            cached
        }
    }

    fun getAllQuotes(currentTime: Long = System.currentTimeMillis()): List<MarketQuote> {
        return quotesMap.keys().toList().mapNotNull { getQuote(it, currentTime) }
    }

    fun putCandles(symbolKey: String, candles: List<OhlcCandle>) {
        candlesMap[symbolKey] = candles
    }

    fun getCandles(symbolKey: String): List<OhlcCandle>? {
        return candlesMap[symbolKey]
    }

    fun clear() {
        quotesMap.clear()
        candlesMap.clear()
    }
}
