package com.pipbeacon.app.indicators

import kotlin.math.abs
import kotlin.math.max

object AtrCalculator {
    fun calculate(candles: List<Candle>, period: Int = 14): List<Double> {
        if (candles.size <= period) return emptyList()

        val trueRanges = ArrayList<Double>(candles.size)
        trueRanges.add(candles[0].high - candles[0].low)

        for (i in 1 until candles.size) {
            val highLow = candles[i].high - candles[i].low
            val highClose = abs(candles[i].high - candles[i - 1].close)
            val lowClose = abs(candles[i].low - candles[i - 1].close)
            val tr = max(highLow, max(highClose, lowClose))
            trueRanges.add(tr)
        }

        // Wilder's smoothing for ATR
        var atr = trueRanges.subList(0, period).average()
        val result = ArrayList<Double>(candles.size)
        for (i in 0 until period) {
            result.add(Double.NaN)
        }
        result.add(atr)

        for (i in (period + 1) until trueRanges.size) {
            atr = ((atr * (period - 1)) + trueRanges[i]) / period
            result.add(atr)
        }

        return result
    }

    fun latest(candles: List<Candle>, period: Int = 14): Double? {
        return calculate(candles, period).lastOrNull { !it.isNaN() }
    }
}
