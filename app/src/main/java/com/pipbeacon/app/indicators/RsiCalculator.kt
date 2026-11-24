package com.pipbeacon.app.indicators

import kotlin.math.max

object RsiCalculator {
    /**
     * Calculates Relative Strength Index (RSI) using Wilder's smoothed method.
     * Default period is 14.
     */
    fun calculate(closes: List<Double>, period: Int = 14): List<Double> {
        if (closes.size <= period) return emptyList()

        val results = ArrayList<Double>(closes.size)
        // Pad with nulls/NaN for indices before period
        for (i in 0 until period) {
            results.add(Double.NaN)
        }

        var gainSum = 0.0
        var lossSum = 0.0

        for (i in 1..period) {
            val change = closes[i] - closes[i - 1]
            if (change > 0) gainSum += change else lossSum += -change
        }

        var avgGain = gainSum / period
        var avgLoss = lossSum / period

        val firstRsi = if (avgLoss == 0.0) 100.0 else 100.0 - (100.0 / (1.0 + (avgGain / avgLoss)))
        results.add(firstRsi)

        for (i in (period + 1) until closes.size) {
            val change = closes[i] - closes[i - 1]
            val gain = if (change > 0) change else 0.0
            val loss = if (change < 0) -change else 0.0

            avgGain = ((avgGain * (period - 1)) + gain) / period
            avgLoss = ((avgLoss * (period - 1)) + loss) / period

            val rsi = if (avgLoss == 0.0) {
                100.0
            } else {
                val rs = avgGain / avgLoss
                100.0 - (100.0 / (1.0 + rs))
            }
            results.add(rsi)
        }

        return results
    }

    fun latestRsi(closes: List<Double>, period: Int = 14): Double? {
        val series = calculate(closes, period)
        return series.lastOrNull { !it.isNaN() }
    }
}
