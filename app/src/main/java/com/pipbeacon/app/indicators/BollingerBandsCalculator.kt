package com.pipbeacon.app.indicators

import kotlin.math.pow
import kotlin.math.sqrt

data class BollingerBandsResult(
    val middleBand: Double,
    val upperBand: Double,
    val lowerBand: Double,
    val bandwidth: Double,
    val percentB: Double
)

object BollingerBandsCalculator {
    fun calculate(
        closes: List<Double>,
        period: Int = 20,
        multiplier: Double = 2.0
    ): List<BollingerBandsResult> {
        if (closes.size < period) return emptyList()

        val sma = MovingAveragesCalculator.calculateSma(closes, period)
        val results = ArrayList<BollingerBandsResult>()

        for (i in (period - 1) until closes.size) {
            val middle = sma[i]
            if (middle.isNaN()) continue

            // Standard deviation
            var varianceSum = 0.0
            for (j in (i - period + 1)..i) {
                varianceSum += (closes[j] - middle).pow(2.0)
            }
            val stdDev = sqrt(varianceSum / period)

            val upper = middle + (multiplier * stdDev)
            val lower = middle - (multiplier * stdDev)
            val bandwidth = if (middle != 0.0) ((upper - lower) / middle) * 100.0 else 0.0
            val percentB = if ((upper - lower) != 0.0) (closes[i] - lower) / (upper - lower) else 0.5

            results.add(
                BollingerBandsResult(
                    middleBand = middle,
                    upperBand = upper,
                    lowerBand = lower,
                    bandwidth = bandwidth,
                    percentB = percentB
                )
            )
        }
        return results
    }

    fun latest(
        closes: List<Double>,
        period: Int = 20,
        multiplier: Double = 2.0
    ): BollingerBandsResult? {
        return calculate(closes, period, multiplier).lastOrNull()
    }
}
