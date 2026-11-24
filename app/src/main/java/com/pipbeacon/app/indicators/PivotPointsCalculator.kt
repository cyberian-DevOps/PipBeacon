package com.pipbeacon.app.indicators

data class PivotLevels(
    val pivot: Double,
    val r1: Double,
    val r2: Double,
    val r3: Double,
    val s1: Double,
    val s2: Double,
    val s3: Double
)

object PivotPointsCalculator {
    fun calculateStandard(high: Double, low: Double, close: Double): PivotLevels {
        val p = (high + low + close) / 3.0
        val r1 = (2.0 * p) - low
        val s1 = (2.0 * p) - high
        val r2 = p + (high - low)
        val s2 = p - (high - low)
        val r3 = high + 2.0 * (p - low)
        val s3 = low - 2.0 * (high - p)
        return PivotLevels(p, r1, r2, r3, s1, s2, s3)
    }

    fun calculateFibonacci(high: Double, low: Double, close: Double): PivotLevels {
        val p = (high + low + close) / 3.0
        val range = high - low
        val r1 = p + (0.382 * range)
        val r2 = p + (0.618 * range)
        val r3 = p + (1.000 * range)
        val s1 = p - (0.382 * range)
        val s2 = p - (0.618 * range)
        val s3 = p - (1.000 * range)
        return PivotLevels(p, r1, r2, r3, s1, s2, s3)
    }
}
