package com.pipbeacon.app.engine

import com.pipbeacon.app.indicators.Candle

data class SupportResistanceLevels(
    val nearestSupport: Double,
    val nearestResistance: Double,
    val swingHighs: List<Double>,
    val swingLows: List<Double>
)

object SupportResistanceCalculator {

    /**
     * Calculates dynamic support and resistance levels using rolling fractal swing highs/lows.
     */
    fun calculate(
        candles: List<Candle>,
        currentPrice: Double,
        window: Int = 5
    ): SupportResistanceLevels {
        if (candles.size < (window * 2) + 1) {
            val fallbackSupport = currentPrice * 0.985
            val fallbackResistance = currentPrice * 1.015
            return SupportResistanceLevels(
                nearestSupport = fallbackSupport,
                nearestResistance = fallbackResistance,
                swingHighs = listOf(fallbackResistance),
                swingLows = listOf(fallbackSupport)
            )
        }

        val swingHighs = mutableListOf<Double>()
        val swingLows = mutableListOf<Double>()

        for (i in window until (candles.size - window)) {
            val currentHigh = candles[i].high
            val currentLow = candles[i].low

            val isSwingHigh = (1..window).all { offset ->
                currentHigh >= candles[i - offset].high && currentHigh >= candles[i + offset].high
            }
            if (isSwingHigh) {
                swingHighs.add(currentHigh)
            }

            val isSwingLow = (1..window).all { offset ->
                currentLow <= candles[i - offset].low && currentLow <= candles[i + offset].low
            }
            if (isSwingLow) {
                swingLows.add(currentLow)
            }
        }

        // Find nearest levels to current price
        val validSupports = swingLows.filter { it < currentPrice }
        val nearestSupport = if (validSupports.isNotEmpty()) {
            validSupports.maxOrNull() ?: (currentPrice * 0.985)
        } else {
            currentPrice * 0.985
        }

        val validResistances = swingHighs.filter { it > currentPrice }
        val nearestResistance = if (validResistances.isNotEmpty()) {
            validResistances.minOrNull() ?: (currentPrice * 1.015)
        } else {
            currentPrice * 1.015
        }

        return SupportResistanceLevels(
            nearestSupport = nearestSupport,
            nearestResistance = nearestResistance,
            swingHighs = swingHighs.takeLast(5),
            swingLows = swingLows.takeLast(5)
        )
    }
}
