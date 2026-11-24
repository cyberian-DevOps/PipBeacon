package com.pipbeacon.app.indicators

object MovingAveragesCalculator {
    /**
     * Calculates Simple Moving Average (SMA)
     */
    fun calculateSma(values: List<Double>, period: Int): List<Double> {
        if (values.size < period) return emptyList()
        val result = ArrayList<Double>(values.size)
        for (i in 0 until period - 1) {
            result.add(Double.NaN)
        }
        var windowSum = 0.0
        for (i in 0 until period) {
            windowSum += values[i]
        }
        result.add(windowSum / period)

        for (i in period until values.size) {
            windowSum += values[i] - values[i - period]
            result.add(windowSum / period)
        }
        return result
    }

    /**
     * Calculates Exponential Moving Average (EMA)
     */
    fun calculateEma(values: List<Double>, period: Int): List<Double> {
        if (values.size < period) return emptyList()
        val result = ArrayList<Double>(values.size)
        for (i in 0 until period - 1) {
            result.add(Double.NaN)
        }

        // Start with SMA of first `period` items
        var initialSma = 0.0
        for (i in 0 until period) {
            initialSma += values[i]
        }
        initialSma /= period
        result.add(initialSma)

        val multiplier = 2.0 / (period + 1.0)
        var prevEma = initialSma

        for (i in period until values.size) {
            val currentEma = ((values[i] - prevEma) * multiplier) + prevEma
            result.add(currentEma)
            prevEma = currentEma
        }
        return result
    }
}
