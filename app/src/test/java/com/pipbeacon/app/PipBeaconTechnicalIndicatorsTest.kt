package com.pipbeacon.app

import com.pipbeacon.app.domain.PipCalculatorUseCase
import com.pipbeacon.app.indicators.BollingerBandsCalculator
import com.pipbeacon.app.indicators.Candle
import com.pipbeacon.app.indicators.MovingAveragesCalculator
import com.pipbeacon.app.indicators.PivotPointsCalculator
import com.pipbeacon.app.indicators.RsiCalculator
import com.pipbeacon.app.indicators.TechnicalIndicatorsEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PipBeaconTechnicalIndicatorsTest {

    @Test
    fun testRsiCalculationBounds() {
        val closes = listOf(
            44.34, 44.09, 44.15, 43.61, 44.33, 44.83, 45.10, 45.42, 45.84, 46.08,
            45.89, 46.03, 45.61, 46.28, 46.28, 46.00, 46.03, 46.41, 46.22, 45.64
        )
        val rsi = RsiCalculator.latestRsi(closes, 14)
        assertNotNull(rsi)
        assertTrue("RSI should be between 0 and 100", rsi!! in 0.0..100.0)
    }

    @Test
    fun testMovingAveragesCalculation() {
        val values = listOf(1.0, 2.0, 3.0, 4.0, 5.0)
        val sma = MovingAveragesCalculator.calculateSma(values, 3)
        // Last 3 are 3.0, 4.0, 5.0 -> avg is 4.0
        val latestSma = sma.lastOrNull { !it.isNaN() }
        assertNotNull(latestSma)
        assertEquals(4.0, latestSma!!, 0.001)

        val ema = MovingAveragesCalculator.calculateEma(values, 3)
        val latestEma = ema.lastOrNull { !it.isNaN() }
        assertNotNull(latestEma)
        assertTrue(latestEma!! > 3.0)
    }

    @Test
    fun testBollingerBandsSymmetry() {
        val closes = (1..30).map { 1.0800 + (it * 0.0005) }
        val bb = BollingerBandsCalculator.latest(closes, 20, 2.0)
        assertNotNull(bb)
        assertTrue("Upper band must be higher than middle", bb!!.upperBand > bb.middleBand)
        assertTrue("Lower band must be lower than middle", bb.lowerBand < bb.middleBand)
    }

    @Test
    fun testPivotPointsStandard() {
        val high = 1.0900
        val low = 1.0800
        val close = 1.0850
        val pivots = PivotPointsCalculator.calculateStandard(high, low, close)
        assertEquals(1.0850, pivots.pivot, 0.0001)
        assertTrue(pivots.r1 > pivots.pivot)
        assertTrue(pivots.s1 < pivots.pivot)
    }

    @Test
    fun testPipCalculatorUseCase() {
        val calc = PipCalculatorUseCase()
        val result = calc.calculate(
            symbol = "EUR/USD",
            entryPrice = 1.0850,
            stopLoss = 1.0815,
            takeProfit = 1.0920,
            accountBalance = 10000.0,
            riskPercent = 1.0,
            lotSize = 0.1
        )
        // Risk = 35 pips (0.0035 * 10000)
        assertEquals(35.0, result.pipDifference, 0.1)
        // 1.0 mini lot = 0.1 std lot -> $1.00 per pip -> 35 * 1 = $35
        assertEquals(35.0, result.monetaryRiskUsd, 0.1)
        // Reward = 70 pips -> 70 * 1 = $70
        assertEquals(70.0, result.monetaryRewardUsd, 0.1)
        assertEquals("1 : 2.00", result.riskRewardRatio)
    }

    @Test
    fun testTechnicalIndicatorsEngineExecution() {
        val now = System.currentTimeMillis()
        val candles = (1..60).map { i ->
            val p = 1.0850 + (i * 0.0002)
            Candle(now + (i * 60000), p, p + 0.0005, p - 0.0005, p + 0.0001, 1000.0)
        }
        val report = TechnicalIndicatorsEngine.analyze(candles)
        assertNotNull(report.overallSignal)
        assertTrue(report.oscillators.isNotEmpty())
        assertTrue(report.movingAverages.isNotEmpty())
        assertNotNull(report.standardPivots)
    }
}
