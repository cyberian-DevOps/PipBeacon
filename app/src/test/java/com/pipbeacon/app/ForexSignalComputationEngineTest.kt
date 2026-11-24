package com.pipbeacon.app

import com.pipbeacon.app.core.SignalType
import com.pipbeacon.app.core.TimeFrame
import com.pipbeacon.app.engine.signal.ForexSignalComputationEngine
import com.pipbeacon.app.engine.signal.VolatilityLevel
import com.pipbeacon.app.indicators.Candle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.sin

class ForexSignalComputationEngineTest {

    @Test
    fun testPipSizeCalculation() {
        assertEquals(0.0001, ForexSignalComputationEngine.getPipSize("EUR/USD"), 0.000001)
        assertEquals(0.0001, ForexSignalComputationEngine.getPipSize("GBP/USD"), 0.000001)
        assertEquals(0.01, ForexSignalComputationEngine.getPipSize("USD/JPY"), 0.001)
        assertEquals(0.01, ForexSignalComputationEngine.getPipSize("EUR/JPY"), 0.001)
        assertEquals(0.10, ForexSignalComputationEngine.getPipSize("XAU/USD"), 0.01)
    }

    @Test
    fun testBullishForexSignalMultiTierTargets() {
        val now = System.currentTimeMillis()
        val candles = ArrayList<Candle>()
        var price = 1.0500

        // Create 60 candles with upward slope
        for (i in 0 until 60) {
            price += 0.0008 + (sin(i.toDouble()) * 0.0002)
            val open = price - 0.0003
            val close = price
            val high = close + 0.0005
            val low = open - 0.0004
            candles.add(Candle(now + (i * 3600000L), open, high, low, close, 1500.0))
        }

        val result = ForexSignalComputationEngine.computeSignal(
            symbol = "EUR/USD",
            pairName = "Euro / US Dollar",
            candles = candles,
            timeframe = TimeFrame.H1,
            riskMultiplier = 1.5
        )

        assertNotNull(result)
        assertEquals("EUR/USD", result.symbol)
        assertTrue(result.signalType == SignalType.BUY || result.signalType == SignalType.STRONG_BUY)

        // Multi-tier targets verification
        assertTrue("Stop loss must be below entry for BUY", result.stopLossPrice < result.entryPrice)
        assertTrue("TP1 must be above entry", result.tp1.price > result.entryPrice)
        assertTrue("TP2 must be above TP1", result.tp2.price > result.tp1.price)
        assertTrue("TP3 must be above TP2", result.tp3.price > result.tp2.price)

        // Risk-to-reward ratios
        assertEquals(1.0, result.tp1.riskRewardRatio, 0.01)
        assertEquals(2.0, result.tp2.riskRewardRatio, 0.01)
        assertEquals(3.0, result.tp3.riskRewardRatio, 0.01)

        // Pips progression
        assertTrue(result.stopLossPips > 0)
        assertTrue(result.tp1.pips > 0)
        assertTrue(result.tp2.pips > result.tp1.pips)
        assertTrue(result.tp3.pips > result.tp2.pips)

        // Confluence & Indicators Telemetry
        assertTrue(result.confluenceScore in 0..100)
        val telemetry = result.telemetry
        assertTrue(telemetry.rsiValue in 0.0..100.0)
        assertTrue(telemetry.ema20Value > 0)
        assertTrue(telemetry.ema50Value > 0)
        assertTrue(telemetry.atr14Pips > 0)
    }

    @Test
    fun testBearishForexSignalMultiTierTargets() {
        val now = System.currentTimeMillis()
        val candles = ArrayList<Candle>()
        var price = 1.3000

        // Create 60 candles with strong downward trend
        for (i in 0 until 60) {
            price -= 0.0008 + (sin(i.toDouble()) * 0.0001)
            val open = price + 0.0004
            val close = price
            val high = open + 0.0003
            val low = close - 0.0005
            candles.add(Candle(now + (i * 3600000L), open, high, low, close, 1800.0))
        }

        val result = ForexSignalComputationEngine.computeSignal(
            symbol = "GBP/USD",
            pairName = "British Pound / US Dollar",
            candles = candles,
            timeframe = TimeFrame.H1,
            riskMultiplier = 1.5
        )

        assertNotNull(result)
        assertTrue(result.signalType == SignalType.SELL || result.signalType == SignalType.STRONG_SELL)

        // Stop loss above entry for SELL
        assertTrue("Stop loss must be above entry for SELL", result.stopLossPrice > result.entryPrice)
        assertTrue("TP1 must be below entry for SELL", result.tp1.price < result.entryPrice)
        assertTrue("TP2 must be below TP1 for SELL", result.tp2.price < result.tp1.price)
        assertTrue("TP3 must be below TP2 for SELL", result.tp3.price < result.tp2.price)

        // R:R ratios
        assertEquals(1.0, result.tp1.riskRewardRatio, 0.01)
        assertEquals(2.0, result.tp2.riskRewardRatio, 0.01)
        assertEquals(3.0, result.tp3.riskRewardRatio, 0.01)
    }

    @Test
    fun testVolatilityAdjustedLotSizeCalculation() {
        val now = System.currentTimeMillis()
        val candles = ArrayList<Candle>()
        for (i in 0 until 60) {
            candles.add(Candle(now + (i * 60000), 1.0800, 1.0820, 1.0780, 1.0810, 1000.0))
        }

        val result = ForexSignalComputationEngine.computeSignal(
            symbol = "EUR/USD",
            pairName = "Euro / US Dollar",
            candles = candles
        )

        // Test position sizing: $10,000 account, 1% risk = $100 risk
        val lotSize1Pct = result.calculateRecommendedLotSize(
            accountBalance = 10000.0,
            riskPercent = 1.0,
            pipValuePerStandardLot = 10.0
        )
        assertTrue("Lot size must be positive and bounded", lotSize1Pct in 0.01..50.0)

        // 2% risk should yield roughly double the lot size of 1% risk
        val lotSize2Pct = result.calculateRecommendedLotSize(
            accountBalance = 10000.0,
            riskPercent = 2.0,
            pipValuePerStandardLot = 10.0
        )
        assertTrue(lotSize2Pct >= lotSize1Pct)
    }
}
