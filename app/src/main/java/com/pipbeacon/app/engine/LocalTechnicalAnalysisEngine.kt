package com.pipbeacon.app.engine

import com.pipbeacon.app.core.MarketType
import com.pipbeacon.app.core.TimeFrame
import com.pipbeacon.app.engine.model.Signal
import com.pipbeacon.app.engine.model.SignalApprovalStatus
import com.pipbeacon.app.engine.model.SignalDirection
import com.pipbeacon.app.engine.model.SignalOutcomeStatus
import com.pipbeacon.app.engine.model.TechnicalSnapshot
import com.pipbeacon.app.engine.model.TrendClassification
import com.pipbeacon.app.indicators.AtrCalculator
import com.pipbeacon.app.indicators.Candle
import com.pipbeacon.app.indicators.MacdCalculator
import com.pipbeacon.app.indicators.MovingAveragesCalculator
import com.pipbeacon.app.indicators.RsiCalculator
import kotlin.math.abs
import kotlin.math.max

sealed class SignalEvaluationResult {
    data class Generated(val signal: Signal) : SignalEvaluationResult()
    data class NoSignal(val reason: String, val snapshot: TechnicalSnapshot) : SignalEvaluationResult()
    data class Rejected(val reason: String) : SignalEvaluationResult()
}

object LocalTechnicalAnalysisEngine {

    const val STRATEGY_VERSION = "v1.2-local-rules"

    /**
     * Executes local rule-based technical analysis using Kotlin.
     * Does NOT use AI models or external paid APIs.
     * All calculations are purely mathematical and deterministic.
     */
    fun evaluate(
        symbol: String,
        marketType: MarketType,
        timeframe: TimeFrame,
        candles: List<Candle>,
        isPriceStale: Boolean = false,
        currentTime: Long = System.currentTimeMillis()
    ): SignalEvaluationResult {

        // Rule: Never create signals from stale or missing prices
        if (isPriceStale) {
            return SignalEvaluationResult.Rejected(
                "Market price feed is stale. Signal generation rejected until fresh permitted data arrives."
            )
        }

        // Rule: Require valid, sufficiently recent OHLC data
        val validation = CandleValidator.validateCandles(candles, currentTime = currentTime)
        if (validation is CandleValidationResult.Invalid) {
            return SignalEvaluationResult.Rejected("Candle validation failed: ${validation.reason}")
        }

        val closes = candles.map { it.close }
        val currentPrice = closes.last()

        // 1. Moving Averages: EMA 20, EMA 50, EMA 200
        val ema20Series = MovingAveragesCalculator.calculateEma(closes, 20)
        val ema50Series = MovingAveragesCalculator.calculateEma(closes, 50)
        val ema200Series = MovingAveragesCalculator.calculateEma(closes, 200)

        val ema20 = ema20Series.lastOrNull { !it.isNaN() }
            ?: return SignalEvaluationResult.Rejected("EMA 20 could not be computed.")
        val ema50 = ema50Series.lastOrNull { !it.isNaN() }
            ?: return SignalEvaluationResult.Rejected("EMA 50 could not be computed.")
        val ema200 = ema200Series.lastOrNull { !it.isNaN() }
            ?: return SignalEvaluationResult.Rejected("EMA 200 could not be computed.")

        // 2. RSI 14 (Wilder's method)
        val rsi14 = RsiCalculator.latestRsi(closes, 14)
            ?: return SignalEvaluationResult.Rejected("RSI 14 could not be computed.")

        // 3. MACD (12, 26, 9)
        val macd = MacdCalculator.latest(closes)
            ?: return SignalEvaluationResult.Rejected("MACD (12,26,9) could not be computed.")

        // 4. ATR 14
        val atr14 = AtrCalculator.latest(candles, 14)
            ?: return SignalEvaluationResult.Rejected("ATR 14 could not be computed.")

        // 5. Support and Resistance
        val srLevels = SupportResistanceCalculator.calculate(candles, currentPrice)

        // 6. Trend Classification
        val trend = TrendClassifier.classifyTrend(currentPrice, ema20, ema50, ema200)

        val snapshot = TechnicalSnapshot(
            ema20 = ema20,
            ema50 = ema50,
            ema200 = ema200,
            rsi14 = rsi14,
            macdHistogram = macd.histogram,
            macdLine = macd.macdLine,
            macdSignal = macd.signalLine,
            atr14 = atr14,
            supportLevel = srLevels.nearestSupport,
            resistanceLevel = srLevels.nearestResistance,
            trend = trend
        )

        // Evaluate Deterministic Signal Rules:
        val isBullishConfluence = (trend == TrendClassification.STRONG_UPTREND || trend == TrendClassification.MODERATE_UPTREND) &&
                rsi14 in 42.0..68.0 &&
                macd.histogram > 0.0

        val isBearishConfluence = (trend == TrendClassification.STRONG_DOWNTREND || trend == TrendClassification.MODERATE_DOWNTREND) &&
                rsi14 in 32.0..58.0 &&
                macd.histogram < 0.0

        if (isBullishConfluence) {
            // BUY Signal Setup
            // Stop Loss placed below nearest support or 1.5 * ATR below entry
            val rawSl = minOf(srLevels.nearestSupport, currentPrice - (1.5 * atr14))
            val sl = if (rawSl < currentPrice) rawSl else (currentPrice - (1.5 * atr14))
            val risk = currentPrice - sl

            if (risk <= 0.0) {
                return SignalEvaluationResult.Rejected("Invalid risk calculation: Stop loss must be strictly below entry for BUY.")
            }

            val tp1 = currentPrice + (1.5 * risk)
            val tp2 = currentPrice + (2.5 * risk)
            val tp3 = currentPrice + (3.5 * risk)

            // Validate placement: SL < Entry < TP1 < TP2 < TP3
            if (!(sl < currentPrice && currentPrice < tp1 && tp1 < tp2 && tp2 < tp3)) {
                return SignalEvaluationResult.Rejected("Stop-loss or take-profit price placement validation failed for BUY.")
            }

            val rrRatio = (tp2 - currentPrice) / risk // R:R based on intermediate TP2
            val rrText = String.format("1 : %.2f", rrRatio)

            val explanation = buildString {
                append("Rule-Based BUY Confluence ($trend): ")
                append("Price ($currentPrice) is above EMA 50 ($ema50) and EMA 200 ($ema200). ")
                append("RSI(14) is healthy at ${String.format("%.1f", rsi14)} without overbought saturation. ")
                append("MACD histogram is bullish at ${String.format("%.5f", macd.histogram)}. ")
                append("Stop Loss placed at $sl (${String.format("%.4f", risk)} risk buffer). ")
                append("Targets set at 1.5x (TP1), 2.5x (TP2), and 3.5x (TP3) of risk.")
            }

            val signal = Signal(
                signalId = "sig_${symbol.replace("/", "").lowercase()}_${currentTime}",
                symbol = symbol,
                marketType = marketType,
                direction = SignalDirection.BUY,
                timeframe = timeframe,
                entryPrice = currentPrice,
                stopLoss = sl,
                takeProfit1 = tp1,
                takeProfit2 = tp2,
                takeProfit3 = tp3,
                riskRewardRatio = rrRatio,
                riskRewardText = rrText,
                strategyVersion = STRATEGY_VERSION,
                creationTimestamp = currentTime,
                source = "Local Rule Engine (Deterministic Kotlin)",
                approvalStatus = SignalApprovalStatus.EXPERIMENTAL_UNVERIFIED,
                outcomeStatus = SignalOutcomeStatus.PENDING,
                plainLanguageExplanation = explanation,
                technicalSnapshot = snapshot
            )
            return SignalEvaluationResult.Generated(signal)
        } else if (isBearishConfluence) {
            // SELL Signal Setup
            val rawSl = maxOf(srLevels.nearestResistance, currentPrice + (1.5 * atr14))
            val sl = if (rawSl > currentPrice) rawSl else (currentPrice + (1.5 * atr14))
            val risk = sl - currentPrice

            if (risk <= 0.0) {
                return SignalEvaluationResult.Rejected("Invalid risk calculation: Stop loss must be strictly above entry for SELL.")
            }

            val tp1 = currentPrice - (1.5 * risk)
            val tp2 = currentPrice - (2.5 * risk)
            val tp3 = currentPrice - (3.5 * risk)

            // Validate placement: SL > Entry > TP1 > TP2 > TP3
            if (!(sl > currentPrice && currentPrice > tp1 && tp1 > tp2 && tp2 > tp3)) {
                return SignalEvaluationResult.Rejected("Stop-loss or take-profit price placement validation failed for SELL.")
            }

            val rrRatio = (currentPrice - tp2) / risk
            val rrText = String.format("1 : %.2f", rrRatio)

            val explanation = buildString {
                append("Rule-Based SELL Confluence ($trend): ")
                append("Price ($currentPrice) is below EMA 50 ($ema50) and EMA 200 ($ema200). ")
                append("RSI(14) reflects bearish momentum at ${String.format("%.1f", rsi14)}. ")
                append("MACD histogram is negative at ${String.format("%.5f", macd.histogram)}. ")
                append("Stop Loss placed at $sl (${String.format("%.4f", risk)} risk buffer). ")
                append("Targets set at 1.5x (TP1), 2.5x (TP2), and 3.5x (TP3) of risk.")
            }

            val signal = Signal(
                signalId = "sig_${symbol.replace("/", "").lowercase()}_${currentTime}",
                symbol = symbol,
                marketType = marketType,
                direction = SignalDirection.SELL,
                timeframe = timeframe,
                entryPrice = currentPrice,
                stopLoss = sl,
                takeProfit1 = tp1,
                takeProfit2 = tp2,
                takeProfit3 = tp3,
                riskRewardRatio = rrRatio,
                riskRewardText = rrText,
                strategyVersion = STRATEGY_VERSION,
                creationTimestamp = currentTime,
                source = "Local Rule Engine (Deterministic Kotlin)",
                approvalStatus = SignalApprovalStatus.EXPERIMENTAL_UNVERIFIED,
                outcomeStatus = SignalOutcomeStatus.PENDING,
                plainLanguageExplanation = explanation,
                technicalSnapshot = snapshot
            )
            return SignalEvaluationResult.Generated(signal)
        } else {
            return SignalEvaluationResult.NoSignal(
                reason = "No high-confluence setup detected. Trend: ${trend.label}, RSI(14): ${String.format("%.1f", rsi14)}, MACD Hist: ${String.format("%.5f", macd.histogram)}.",
                snapshot = snapshot
            )
        }
    }
}
