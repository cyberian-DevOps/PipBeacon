package com.pipbeacon.app.indicators

import kotlin.math.max
import kotlin.math.min

data class StochasticResult(
    val k: Double,
    val d: Double
)

object StochasticCalculator {
    fun calculate(
        candles: List<Candle>,
        kPeriod: Int = 14,
        dPeriod: Int = 3
    ): List<StochasticResult> {
        if (candles.size < kPeriod + dPeriod) return emptyList()

        val rawK = ArrayList<Double>()

        for (i in (kPeriod - 1) until candles.size) {
            val window = candles.subList(i - kPeriod + 1, i + 1)
            val highestHigh = window.maxOf { it.high }
            val lowestLow = window.minOf { it.low }
            val currentClose = candles[i].close

            val k = if (highestHigh - lowestLow != 0.0) {
                ((currentClose - lowestLow) / (highestHigh - lowestLow)) * 100.0
            } else {
                50.0
            }
            rawK.add(k)
        }

        val dValues = MovingAveragesCalculator.calculateSma(rawK, dPeriod)
        val results = ArrayList<StochasticResult>()

        for (i in (dPeriod - 1) until rawK.size) {
            val k = rawK[i]
            val d = dValues[i]
            if (!d.isNaN()) {
                results.add(StochasticResult(k = k, d = d))
            }
        }
        return results
    }

    fun latest(candles: List<Candle>): StochasticResult? {
        return calculate(candles).lastOrNull()
    }
}
