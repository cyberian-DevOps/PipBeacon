package com.pipbeacon.app

import com.pipbeacon.app.data.ai.AiTradingService
import com.pipbeacon.app.data.lessons.LessonRepository
import com.pipbeacon.app.domain.TradingCalculatorsUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PipBeaconAiAndCalculatorsTest {

    @Test
    fun testPositionSizeCalculator() {
        val calculator = TradingCalculatorsUseCase()
        // Account $10,000, 1% risk ($100), 50 pip SL, $10 per pip per lot
        // Optimal lot size = 100 / (50 * 10) = 0.20 lots
        val result = calculator.calculatePositionSize(
            accountBalance = 10000.0,
            riskPercentage = 1.0,
            stopLossPips = 50.0,
            pipValuePerLot = 10.0
        )

        assertEquals(0.20, result.lotSize, 0.01)
        assertEquals(100.0, result.riskAmount, 0.01)
        assertEquals(20_000, result.units)
        assertEquals(2.0, result.pipValueTotal, 0.01)
    }

    @Test
    fun testPipValueCalculator() {
        val calculator = TradingCalculatorsUseCase()
        // 1.5 lots, $10 standard lot pip value, 40 pip movement
        val result = calculator.calculatePipValue(
            lotSize = 1.5,
            pipValuePerStandardLot = 10.0,
            pipMovement = 40.0
        )

        assertEquals(15.0, result.valuePerPip, 0.01)
        assertEquals(600.0, result.estimatedProfitLoss, 0.01)
    }

    @Test
    fun testRiskRewardCalculator() {
        val calculator = TradingCalculatorsUseCase()
        // Entry: 1.0850, SL: 1.0825 (25 pips), TP: 1.0925 (75 pips) -> R:R 1:3.0
        val result = calculator.calculateRiskReward(
            entryPrice = 1.0850,
            stopLossPrice = 1.0825,
            takeProfitPrice = 1.0925
        )

        assertEquals("1 : 3.0", result.riskRewardRatioText)
        assertEquals(3.0, result.riskRewardNumeric, 0.05)
        assertEquals(25.0, result.riskPips, 0.1)
        assertEquals(75.0, result.rewardPips, 0.1)
        assertTrue(result.isFavorable)
    }

    @Test
    fun testAiTutorKnowledgeBaseAnswersSmc() = runBlocking {
        val aiService = AiTradingService()

        val obAnswer = aiService.askTutor("What is an order block?")
        assertTrue(obAnswer.contains("Order Block", ignoreCase = true))
        assertTrue(obAnswer.contains("institutional", ignoreCase = true))

        val fvgAnswer = aiService.askTutor("Explain Fair Value Gaps (FVG)")
        assertTrue(fvgAnswer.contains("Fair Value Gap", ignoreCase = true) || fvgAnswer.contains("imbalance", ignoreCase = true))

        val bosAnswer = aiService.askTutor("How do I identify a Break of Structure?")
        assertTrue(bosAnswer.contains("Break of Structure", ignoreCase = true) || bosAnswer.contains("BOS", ignoreCase = true))
    }

    @Test
    fun testAiNewsAnalyzerClassifiesSentiment() = runBlocking {
        val aiService = AiTradingService()

        val gdpAnalysis = aiService.analyzeNews("US GDP growth exceeded forecasts at 4.4% beating expectations")
        assertEquals("BUY", gdpAnalysis.sentiment)
        assertEquals("USD/JPY", gdpAnalysis.currencyPair)
        assertEquals("HIGH", gdpAnalysis.impact)

        val inflationAnalysis = aiService.analyzeNews("UK CPI inflation drops to 1.8% prompting BoE rate cut expectations")
        assertNotNull(inflationAnalysis.sentiment)
        assertNotNull(inflationAnalysis.analysisText)
    }

    @Test
    fun testLessonRepositoryCurriculumAndProgress() {
        val repo = LessonRepository()

        val allLessons = repo.getLessons("ALL")
        assertTrue(allLessons.isNotEmpty())

        val beginner = repo.getLessons("BEGINNER")
        assertTrue(beginner.all { it.level.equals("Beginner", ignoreCase = true) })

        val initialProgress = repo.getProgress()
        assertEquals(0, initialProgress.first)

        val toggled = repo.toggleLessonCompletion("lesson_what_is_forex")
        assertTrue(toggled)

        val updatedProgress = repo.getProgress()
        assertEquals(1, updatedProgress.first)
    }
}
