package com.pipbeacon.app.indicators

data class MacdResult(
    val macdLine: Double,
    val signalLine: Double,
    val histogram: Double
)

object MacdCalculator {
    fun calculate(
        closes: List<Double>,
        fastPeriod: Int = 12,
        slowPeriod: Int = 26,
        signalPeriod: Int = 9
    ): List<MacdResult> {
        if (closes.size < slowPeriod + signalPeriod) return emptyList()

        val fastEma = MovingAveragesCalculator.calculateEma(closes, fastPeriod)
        val slowEma = MovingAveragesCalculator.calculateEma(closes, slowPeriod)

        val macdRaw = ArrayList<Double>(closes.size)
        for (i in closes.indices) {
            val f = fastEma.getOrNull(i)
            val s = slowEma.getOrNull(i)
            if (f != null && !f.isNaN() && s != null && !s.isNaN()) {
                macdRaw.add(f - s)
            } else {
                macdRaw.add(Double.NaN)
            }
        }

        val validMacdValues = macdRaw.filter { !it.isNaN() }
        val signalValues = MovingAveragesCalculator.calculateEma(validMacdValues, signalPeriod)

        val results = ArrayList<MacdResult>()
        var signalIdx = 0
        for (i in closes.indices) {
            val m = macdRaw[i]
            if (!m.isNaN() && signalIdx < signalValues.size) {
                val sig = signalValues[signalIdx]
                if (!sig.isNaN()) {
                    results.add(MacdResult(macdLine = m, signalLine = sig, histogram = m - sig))
                }
                signalIdx++
            }
        }

        return results
    }

    fun latest(closes: List<Double>): MacdResult? {
        return calculate(closes).lastOrNull()
    }
}
