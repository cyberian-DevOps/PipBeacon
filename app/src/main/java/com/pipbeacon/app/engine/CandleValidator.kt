package com.pipbeacon.app.engine

import com.pipbeacon.app.indicators.Candle

sealed class CandleValidationResult {
    object Valid : CandleValidationResult()
    data class Invalid(val reason: String) : CandleValidationResult()
}

object CandleValidator {

    /**
     * Strictly verifies OHLC integrity and recency:
     * - Rejects incomplete, malformed, or inverted candles (e.g. high < low, close <= 0).
     * - Verifies non-empty chronological data.
     * - Requires minimum candle count (at least 200 for EMA 200 calculation).
     * - Verifies candle timestamp recency to prevent stale calculations.
     */
    fun validateCandles(
        candles: List<Candle>,
        maxStaleWindowMs: Long = 86_400_000L, // 24 hours
        currentTime: Long = System.currentTimeMillis()
    ): CandleValidationResult {
        if (candles.isEmpty()) {
            return CandleValidationResult.Invalid("Candle dataset is empty. Cannot perform technical analysis.")
        }

        if (candles.size < 200) {
            return CandleValidationResult.Invalid(
                "Insufficient candle count (${candles.size}/200 required). EMA 200 and long-term trend classification require at least 200 candles."
            )
        }

        // Validate individual candles
        for (i in candles.indices) {
            val c = candles[i]
            if (c.open <= 0.0 || c.high <= 0.0 || c.low <= 0.0 || c.close <= 0.0) {
                return CandleValidationResult.Invalid("Candle at index $i has non-positive prices (Open: ${c.open}, High: ${c.high}, Low: ${c.low}, Close: ${c.close}).")
            }
            if (c.high < c.low) {
                return CandleValidationResult.Invalid("Candle at index $i is inverted: High (${c.high}) < Low (${c.low}).")
            }
            if (c.high < c.open || c.high < c.close) {
                return CandleValidationResult.Invalid("Candle at index $i High (${c.high}) is lower than Open (${c.open}) or Close (${c.close}).")
            }
            if (c.low > c.open || c.low > c.close) {
                return CandleValidationResult.Invalid("Candle at index $i Low (${c.low}) is higher than Open (${c.open}) or Close (${c.close}).")
            }
            if (c.timestamp <= 0L) {
                return CandleValidationResult.Invalid("Candle at index $i has invalid timestamp (${c.timestamp}).")
            }

            // Verify chronological order
            if (i > 0 && c.timestamp <= candles[i - 1].timestamp) {
                return CandleValidationResult.Invalid("Candle sequence is not chronological at index $i (timestamp ${c.timestamp} <= ${candles[i - 1].timestamp}).")
            }
        }

        // Validate recency of the latest candle
        val latest = candles.last()
        val age = currentTime - latest.timestamp
        if (age > maxStaleWindowMs) {
            return CandleValidationResult.Invalid(
                "Latest candle is stale (${age / 3_600_000} hours old, max allowed is ${maxStaleWindowMs / 3_600_000} hours). Stale data rejected."
            )
        }

        return CandleValidationResult.Valid
    }
}
