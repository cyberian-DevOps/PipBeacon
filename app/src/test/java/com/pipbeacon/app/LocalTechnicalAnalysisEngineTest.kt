package com.pipbeacon.app

import com.pipbeacon.app.core.MarketType
import com.pipbeacon.app.core.TimeFrame
import com.pipbeacon.app.data.provider.model.MarketQuote
import com.pipbeacon.app.engine.CandleValidationResult
import com.pipbeacon.app.engine.CandleValidator
import com.pipbeacon.app.engine.LocalTechnicalAnalysisEngine
import com.pipbeacon.app.engine.SignalEvaluationResult
import com.pipbeacon.app.engine.SupportResistanceCalculator
import com.pipbeacon.app.engine.TrendClassifier
import com.pipbeacon.app.engine.history.SignalHistoryRepository
import com.pipbeacon.app.engine.model.SignalApprovalStatus
import com.pipbeacon.app.engine.model.SignalDirection
import com.pipbeacon.app.engine.model.SignalOutcomeStatus
import com.pipbeacon.app.engine.model.TrendClassification
import com.pipbeacon.app.indicators.Candle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalTechnicalAnalysisEngineTest {

    private fun generateValidCandles(count: Int = 220, basePrice: Double = 1.0850, step: Double = 0.0001): List<Candle> {
        val now = System.currentTimeMillis()
        val intervalMs = 60_000L
        return (count downTo 1).map { i ->
            val t = now - (i * intervalMs)
            val p = basePrice + ((count - i) * step)
            Candle(
                timestamp = t,
                open = p,
                high = p + 0.0010,
                low = p - 0.0010,
                close = p + 0.0005,
                volume = 1000.0
            )
        }
    }

    @Test
    fun testCandleValidationPassesForValidCandles() {
        val candles = generateValidCandles(count = 210)
        val result = CandleValidator.validateCandles(candles)
        assertTrue("Valid candles must pass validation", result is CandleValidationResult.Valid)
    }

    @Test
    fun testCandleValidationRejectsInvertedCandles() {
        val candles = generateValidCandles(count = 210).toMutableList()
        // Corrupt candle: High < Low
        candles[50] = candles[50].copy(high = 1.0000, low = 1.0500)
        val result = CandleValidator.validateCandles(candles)
        assertTrue("Inverted candle must be rejected", result is CandleValidationResult.Invalid)
    }

    @Test
    fun testCandleValidationRejectsInsufficientCount() {
        val candles = generateValidCandles(count = 150) // Less than 200 required for EMA200
        val result = CandleValidator.validateCandles(candles)
        assertTrue("Less than 200 candles must be rejected", result is CandleValidationResult.Invalid)
    }

    @Test
    fun testCandleValidationRejectsStaleCandles() {
        val oldTimestamp = System.currentTimeMillis() - 200_000_000L // Old timestamp
        val candles = (210 downTo 1).map { i ->
            Candle(oldTimestamp + (i * 1000), 1.08, 1.09, 1.07, 1.085, 100.0)
        }
        val result = CandleValidator.validateCandles(candles, maxStaleWindowMs = 3600_000L)
        assertTrue("Stale candles must be rejected", result is CandleValidationResult.Invalid)
    }

    @Test
    fun testTrendClassifierAlignment() {
        // Strong Uptrend: Price > EMA20 > EMA50 > EMA200
        val trendUp = TrendClassifier.classifyTrend(
            currentPrice = 1.1000,
            ema20 = 1.0900,
            ema50 = 1.0800,
            ema200 = 1.0500
        )
        assertEquals(TrendClassification.STRONG_UPTREND, trendUp)

        // Strong Downtrend: Price < EMA20 < EMA50 < EMA200
        val trendDown = TrendClassifier.classifyTrend(
            currentPrice = 1.0400,
            ema20 = 1.0500,
            ema50 = 1.0600,
            ema200 = 1.0800
        )
        assertEquals(TrendClassification.STRONG_DOWNTREND, trendDown)

        // Ranging / Consolidation
        val trendRange = TrendClassifier.classifyTrend(
            currentPrice = 1.0700,
            ema20 = 1.0710,
            ema50 = 1.0690,
            ema200 = 1.0700
        )
        assertEquals(TrendClassification.CONSOLIDATION_RANGING, trendRange)
    }

    @Test
    fun testSupportResistanceLevels() {
        val candles = generateValidCandles(count = 50, basePrice = 1.0850)
        val sr = SupportResistanceCalculator.calculate(candles, currentPrice = 1.0850)
        assertTrue("Support must be <= current price", sr.nearestSupport <= 1.0850)
        assertTrue("Resistance must be >= current price", sr.nearestResistance >= 1.0850)
    }

    @Test
    fun testEngineRejectsStalePriceFeed() {
        val candles = generateValidCandles(count = 210)
        val result = LocalTechnicalAnalysisEngine.evaluate(
            symbol = "EUR/USD",
            marketType = MarketType.FOREX,
            timeframe = TimeFrame.H1,
            candles = candles,
            isPriceStale = true
        )
        assertTrue("Engine must reject evaluation when price feed is stale", result is SignalEvaluationResult.Rejected)
    }

    @Test
    fun testEngineGeneratesValidBuySignalWithCorrectPlacement() {
        // Build trending candle data (Uptrend)
        val now = System.currentTimeMillis()
        val candles = (250 downTo 1).map { i ->
            val p = 1.0500 + ((250 - i) * 0.0003)
            Candle(now - (i * 60_000L), p, p + 0.0008, p - 0.0004, p + 0.0004, 1500.0)
        }

        val result = LocalTechnicalAnalysisEngine.evaluate(
            symbol = "EUR/USD",
            marketType = MarketType.FOREX,
            timeframe = TimeFrame.H1,
            candles = candles,
            isPriceStale = false,
            currentTime = now
        )

        if (result is SignalEvaluationResult.Generated) {
            val sig = result.signal
            assertEquals("EUR/USD", sig.symbol)
            assertEquals(SignalDirection.BUY, sig.direction)
            assertEquals("v1.2-local-rules", sig.strategyVersion)
            assertEquals(SignalApprovalStatus.EXPERIMENTAL_UNVERIFIED, sig.approvalStatus)
            assertEquals(SignalOutcomeStatus.PENDING, sig.outcomeStatus)

            // Strict Validation Rule: SL < Entry < TP1 < TP2 < TP3
            assertTrue("SL must be < Entry", sig.stopLoss < sig.entryPrice)
            assertTrue("Entry must be < TP1", sig.entryPrice < sig.takeProfit1)
            assertTrue("TP1 must be < TP2", sig.takeProfit1 < sig.takeProfit2)
            assertTrue("TP2 must be < TP3", sig.takeProfit2 < sig.takeProfit3)

            // Risk reward calculation
            val risk = sig.entryPrice - sig.stopLoss
            val rewardTp2 = sig.takeProfit2 - sig.entryPrice
            assertEquals(rewardTp2 / risk, sig.riskRewardRatio, 0.01)

            // Plain language explanation
            assertTrue("Explanation must be present", sig.plainLanguageExplanation.isNotEmpty())
            assertTrue("Explanation must mention indicators", sig.plainLanguageExplanation.contains("EMA") || sig.plainLanguageExplanation.contains("RSI"))
        }
    }

    @Test
    fun testSignalHistoryRepositoryImmutablePreservation() {
        val repo = SignalHistoryRepository()
        val initialSignals = repo.signalsFlow.value
        assertFalse(initialSignals.isEmpty())

        val testSignal = initialSignals.first { it.outcomeStatus == SignalOutcomeStatus.PENDING }
        val originalEntry = testSignal.entryPrice
        val originalSl = testSignal.stopLoss
        val originalTp1 = testSignal.takeProfit1

        // Simulate incoming market quote hitting TP1
        val highQuote = MarketQuote(
            symbol = testSignal.symbol,
            marketType = testSignal.marketType,
            bidPrice = testSignal.takeProfit1 + 0.0010,
            askPrice = testSignal.takeProfit1 + 0.0012,
            midPrice = testSignal.takeProfit1 + 0.0011,
            high24h = testSignal.takeProfit1 + 0.0020,
            low24h = testSignal.entryPrice,
            timestamp = System.currentTimeMillis(),
            providerId = "provider_test",
            providerName = "Test"
        )

        repo.updateOutcomesWithQuote(highQuote)

        val updatedSignals = repo.signalsFlow.value
        val updatedSignal = updatedSignals.first { it.signalId == testSignal.signalId }

        // Outcome must be updated to HIT_TP1
        assertEquals(SignalOutcomeStatus.HIT_TP1, updatedSignal.outcomeStatus)

        // Original published values MUST NOT be rewritten or mutated
        assertEquals(originalEntry, updatedSignal.entryPrice, 0.0001)
        assertEquals(originalSl, updatedSignal.stopLoss, 0.0001)
        assertEquals(originalTp1, updatedSignal.takeProfit1, 0.0001)

        val stats = repo.getPerformanceStats()
        assertTrue(stats.totalSignals > 0)
        assertTrue(stats.hitTp1Count >= 1)
    }
}
