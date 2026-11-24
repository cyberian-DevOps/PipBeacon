package com.pipbeacon.app.engine.signal

import com.pipbeacon.app.core.SignalType
import com.pipbeacon.app.core.TimeFrame
import com.pipbeacon.app.indicators.AtrCalculator
import com.pipbeacon.app.indicators.Candle
import com.pipbeacon.app.indicators.MacdCalculator
import com.pipbeacon.app.indicators.MovingAveragesCalculator
import com.pipbeacon.app.indicators.RsiCalculator
import kotlin.math.abs
import kotlin.math.round

/**
 * Native Forex Signal Computation Engine.
 * Integrates RSI(14), EMA(20/50), MACD(12,26,9), and ATR(14) for volatility-adjusted
 * risk management and multi-tier Take-Profit targets (TP1, TP2, TP3).
 */
object ForexSignalComputationEngine {

    /**
     * Determines standard pip size based on Forex pair convention.
     */
    fun getPipSize(symbol: String): Double {
        return when {
            symbol.contains("JPY", ignoreCase = true) -> 0.01
            symbol.contains("XAU", ignoreCase = true) || symbol.contains("GOLD", ignoreCase = true) -> 0.10
            else -> 0.0001
        }
    }

    /**
     * Computes a full Forex signal with volatility-adjusted TP1, TP2, TP3 and Stop-Loss.
     *
     * @param symbol Currency pair symbol (e.g. "EUR/USD")
     * @param pairName Descriptive name (e.g. "Euro / US Dollar")
     * @param candles Historical OHLC candle series (minimum 50 candles recommended for EMA50 & MACD)
     * @param timeframe Timeframe used for analysis
     * @param riskMultiplier ATR multiplier for Stop-Loss (default 1.5x ATR)
     */
    fun computeSignal(
        symbol: String,
        pairName: String,
        candles: List<Candle>,
        timeframe: TimeFrame = TimeFrame.H1,
        riskMultiplier: Double = 1.5
    ): ForexSignalResult {
        require(candles.isNotEmpty()) { "Candles list cannot be empty for signal computation." }

        val closes = candles.map { it.close }
        val currentPrice = closes.last()
        val pipSize = getPipSize(symbol)

        // 1. Calculate Technical Indicators
        // --- ATR (14) Volatility ---
        val atrValues = AtrCalculator.calculate(candles, 14)
        val atr14 = atrValues.lastOrNull { !it.isNaN() } ?: (currentPrice * 0.0035)
        val atrPips = round((atr14 / pipSize) * 10.0) / 10.0

        // Volatility regime determination
        val validAtrs = atrValues.filter { !it.isNaN() }
        val averageAtr = if (validAtrs.isNotEmpty()) validAtrs.average() else atr14
        val atrRatio = if (averageAtr > 0) atr14 / averageAtr else 1.0

        val volatilityLevel = when {
            atrRatio < 0.75 -> VolatilityLevel.LOW
            atrRatio <= 1.30 -> VolatilityLevel.NORMAL
            atrRatio <= 1.80 -> VolatilityLevel.HIGH
            else -> VolatilityLevel.EXTREME
        }

        val volatilityDescription = when (volatilityLevel) {
            VolatilityLevel.LOW -> "Low volatility (${atrPips}p ATR) - tighter price compression."
            VolatilityLevel.NORMAL -> "Optimal volatility (${atrPips}p ATR) - ideal momentum for targets."
            VolatilityLevel.HIGH -> "High market volatility (${atrPips}p ATR) - wider stop-loss applied."
            VolatilityLevel.EXTREME -> "Extreme market volatility (${atrPips}p ATR) - reduce lot size."
        }

        // --- EMA (20 & 50) Trend Bias ---
        val ema20Series = MovingAveragesCalculator.calculateEma(closes, 20)
        val ema50Series = MovingAveragesCalculator.calculateEma(closes, 50)
        val ema20 = ema20Series.lastOrNull { !it.isNaN() } ?: currentPrice
        val ema50 = ema50Series.lastOrNull { !it.isNaN() } ?: currentPrice

        val prevEma20 = ema20Series.dropLast(1).lastOrNull { !it.isNaN() } ?: ema20
        val prevEma50 = ema50Series.dropLast(1).lastOrNull { !it.isNaN() } ?: ema50

        val isGoldenCross = prevEma20 <= prevEma50 && ema20 > ema50
        val isDeathCross = prevEma20 >= prevEma50 && ema20 < ema50

        val emaBias = when {
            ema20 > ema50 && currentPrice >= ema20 -> IndicatorBias.BULLISH
            ema20 < ema50 && currentPrice <= ema20 -> IndicatorBias.BEARISH
            ema20 > ema50 -> IndicatorBias.BULLISH
            ema20 < ema50 -> IndicatorBias.BEARISH
            else -> IndicatorBias.NEUTRAL
        }

        val emaCrossState = when {
            isGoldenCross -> "Golden Cross Fresh (EMA20 crossed above EMA50)"
            isDeathCross -> "Death Cross Fresh (EMA20 crossed below EMA50)"
            ema20 > ema50 -> "Bullish Alignment (EMA20 > EMA50 by ${(round(abs(ema20 - ema50) / pipSize * 10.0) / 10.0)}p)"
            ema20 < ema50 -> "Bearish Alignment (EMA20 < EMA50 by ${(round(abs(ema20 - ema50) / pipSize * 10.0) / 10.0)}p)"
            else -> "Converged / Neutral Moving Averages"
        }

        // --- RSI (14) Momentum ---
        val rsiSeries = RsiCalculator.calculate(closes, 14)
        val rsi14 = rsiSeries.lastOrNull { !it.isNaN() } ?: 50.0

        val rsiBias = when {
            rsi14 in 48.0..68.0 -> IndicatorBias.BULLISH
            rsi14 < 32.0 -> IndicatorBias.BULLISH // Oversold reversal potential
            rsi14 in 32.0..52.0 -> IndicatorBias.BEARISH
            rsi14 > 68.0 -> IndicatorBias.BEARISH // Overbought reversal potential
            else -> IndicatorBias.NEUTRAL
        }

        val rsiDescription = when {
            rsi14 > 70 -> "Overbought (RSI ${round(rsi14 * 10) / 10}) - Watch for pullback resistance"
            rsi14 < 30 -> "Oversold (RSI ${round(rsi14 * 10) / 10}) - Bullish exhaustion reversal area"
            rsi14 in 50.0..65.0 -> "Bullish Momentum Continuation (RSI ${round(rsi14 * 10) / 10})"
            rsi14 in 35.0..50.0 -> "Bearish Momentum Continuation (RSI ${round(rsi14 * 10) / 10})"
            else -> "Neutral Zone (RSI ${round(rsi14 * 10) / 10})"
        }

        // --- MACD (12, 26, 9) Momentum ---
        val macdList = MacdCalculator.calculate(closes, 12, 26, 9)
        val latestMacd = macdList.lastOrNull()
        val macdLine = latestMacd?.macdLine ?: 0.0
        val macdSignalLine = latestMacd?.signalLine ?: 0.0
        val macdHistogram = latestMacd?.histogram ?: 0.0

        val macdBias = when {
            macdHistogram > 0 && macdLine > macdSignalLine -> IndicatorBias.BULLISH
            macdHistogram < 0 && macdLine < macdSignalLine -> IndicatorBias.BEARISH
            macdLine > macdSignalLine -> IndicatorBias.BULLISH
            macdLine < macdSignalLine -> IndicatorBias.BEARISH
            else -> IndicatorBias.NEUTRAL
        }

        val macdDescription = when {
            macdHistogram > 0 && macdLine > 0 -> "Bullish Expansion (Line > Signal > 0)"
            macdHistogram > 0 -> "Bullish Convergence Crossing Above Signal"
            macdHistogram < 0 && macdLine < 0 -> "Bearish Expansion (Line < Signal < 0)"
            macdHistogram < 0 -> "Bearish Convergence Crossing Below Signal"
            else -> "Neutral / Flat MACD"
        }

        // 2. Confluence Score & Direction Synthesis
        var bullishPoints = 0
        var bearishPoints = 0

        // EMA Weight: 35 points
        if (emaBias == IndicatorBias.BULLISH) bullishPoints += 30
        if (emaBias == IndicatorBias.BEARISH) bearishPoints += 30
        if (isGoldenCross) bullishPoints += 5
        if (isDeathCross) bearishPoints += 5

        // MACD Weight: 30 points
        if (macdBias == IndicatorBias.BULLISH) bullishPoints += 25
        if (macdBias == IndicatorBias.BEARISH) bearishPoints += 25
        if (macdHistogram > 0 && macdLine > 0) bullishPoints += 5
        if (macdHistogram < 0 && macdLine < 0) bearishPoints += 5

        // RSI Weight: 25 points
        if (rsiBias == IndicatorBias.BULLISH) bullishPoints += 20
        if (rsiBias == IndicatorBias.BEARISH) bearishPoints += 20
        if (rsi14 in 52.0..62.0) bullishPoints += 5 // Golden zone
        if (rsi14 in 38.0..48.0) bearishPoints += 5 // Bearish golden zone

        // Volatility Support Weight: 10 points
        if (volatilityLevel == VolatilityLevel.NORMAL || volatilityLevel == VolatilityLevel.HIGH) {
            bullishPoints += 10
            bearishPoints += 10
        }

        val signalType: SignalType
        val confluenceScore: Int

        if (bullishPoints >= 65 && bullishPoints > bearishPoints + 20) {
            signalType = if (bullishPoints >= 80) SignalType.STRONG_BUY else SignalType.BUY
            confluenceScore = bullishPoints.coerceIn(55, 95)
        } else if (bearishPoints >= 65 && bearishPoints > bullishPoints + 20) {
            signalType = if (bearishPoints >= 80) SignalType.STRONG_SELL else SignalType.SELL
            confluenceScore = bearishPoints.coerceIn(55, 95)
        } else {
            signalType = SignalType.NEUTRAL
            confluenceScore = maxOf(bullishPoints, bearishPoints).coerceIn(35, 55)
        }

        // 3. Volatility-Adjusted Stop-Loss and Multi-tier Take-Profit Targets
        val isBuySignal = signalType == SignalType.BUY || signalType == SignalType.STRONG_BUY
        val isSellSignal = signalType == SignalType.SELL || signalType == SignalType.STRONG_SELL

        // Sl distance in raw price
        val slDistance = (riskMultiplier * atr14).coerceAtLeast(pipSize * 10.0)
        val slPips = round((slDistance / pipSize) * 10.0) / 10.0

        val stopLossPrice: Double
        val tp1Price: Double
        val tp2Price: Double
        val tp3Price: Double

        val tp1Distance = slDistance * 1.0 // 1:1 R:R
        val tp2Distance = slDistance * 2.0 // 1:2 R:R
        val tp3Distance = slDistance * 3.0 // 1:3 R:R

        val tp1Pips = round((tp1Distance / pipSize) * 10.0) / 10.0
        val tp2Pips = round((tp2Distance / pipSize) * 10.0) / 10.0
        val tp3Pips = round((tp3Distance / pipSize) * 10.0) / 10.0

        if (isBuySignal || signalType == SignalType.NEUTRAL) {
            stopLossPrice = currentPrice - slDistance
            tp1Price = currentPrice + tp1Distance
            tp2Price = currentPrice + tp2Distance
            tp3Price = currentPrice + tp3Distance
        } else {
            stopLossPrice = currentPrice + slDistance
            tp1Price = currentPrice - tp1Distance
            tp2Price = currentPrice - tp2Distance
            tp3Price = currentPrice - tp3Distance
        }

        val precisionFormat = when {
            symbol.contains("JPY") -> "%.3f"
            symbol.contains("XAU") -> "%.2f"
            else -> "%.5f"
        }

        val tp1 = TargetLevel(
            label = "Take Profit 1",
            shortLabel = "TP1",
            price = roundToDecimals(tp1Price, precisionFormat),
            pips = tp1Pips,
            riskRewardRatio = 1.0,
            description = "Conservative Target (1:1 R:R | ${riskMultiplier}x ATR)"
        )

        val tp2 = TargetLevel(
            label = "Take Profit 2",
            shortLabel = "TP2",
            price = roundToDecimals(tp2Price, precisionFormat),
            pips = tp2Pips,
            riskRewardRatio = 2.0,
            description = "Balanced Structural Target (1:2 R:R | ${(riskMultiplier * 2)}x ATR)"
        )

        val tp3 = TargetLevel(
            label = "Take Profit 3",
            shortLabel = "TP3",
            price = roundToDecimals(tp3Price, precisionFormat),
            pips = tp3Pips,
            riskRewardRatio = 3.0,
            description = "Runner Expansion Target (1:3 R:R | ${(riskMultiplier * 3)}x ATR)"
        )

        val strategyNotes = buildString {
            append("Engine Confluence: ")
            when (signalType) {
                SignalType.BUY, SignalType.STRONG_BUY -> {
                    append("Strong Bullish setup on ${timeframe.label}. ")
                    append("EMA(20) at ${String.format(precisionFormat, ema20)} leads EMA(50) at ${String.format(precisionFormat, ema50)}. ")
                    append("RSI(14) confirmed at ${String.format("%.1f", rsi14)}. ")
                    append("MACD histogram expanded positive (${String.format("%.4f", macdHistogram)}). ")
                    append("ATR(14) provides a ${slPips}p risk buffer.")
                }
                SignalType.SELL, SignalType.STRONG_SELL -> {
                    append("High-probability Bearish setup on ${timeframe.label}. ")
                    append("EMA(20) at ${String.format(precisionFormat, ema20)} trades below EMA(50) at ${String.format(precisionFormat, ema50)}. ")
                    append("RSI(14) downward slope at ${String.format("%.1f", rsi14)}. ")
                    append("MACD negative histogram (${String.format("%.4f", macdHistogram)}). ")
                    append("Volatility stop-loss buffered at ${slPips}p.")
                }
                SignalType.NEUTRAL -> {
                    append("Market is consolidating sideways. Indicators show mixed confluence between EMA and oscillators. Maintain watch.")
                }
            }
        }

        val telemetry = TechnicalIndicatorTelemetry(
            rsiValue = round(rsi14 * 100.0) / 100.0,
            rsiBias = rsiBias,
            rsiDescription = rsiDescription,
            ema20Value = roundToDecimals(ema20, precisionFormat),
            ema50Value = roundToDecimals(ema50, precisionFormat),
            emaBias = emaBias,
            emaCrossState = emaCrossState,
            macdLine = round(macdLine * 10000.0) / 10000.0,
            macdSignalLine = round(macdSignalLine * 10000.0) / 10000.0,
            macdHistogram = round(macdHistogram * 10000.0) / 10000.0,
            macdBias = macdBias,
            macdDescription = macdDescription,
            atr14Value = roundToDecimals(atr14, precisionFormat),
            atr14Pips = atrPips,
            volatilityLevel = volatilityLevel,
            volatilityDescription = volatilityDescription
        )

        return ForexSignalResult(
            symbol = symbol,
            pairName = pairName,
            timeframe = timeframe,
            signalType = signalType,
            entryPrice = roundToDecimals(currentPrice, precisionFormat),
            stopLossPrice = roundToDecimals(stopLossPrice, precisionFormat),
            stopLossPips = slPips,
            stopLossRiskReward = "${riskMultiplier}x ATR Volatility Buffer",
            tp1 = tp1,
            tp2 = tp2,
            tp3 = tp3,
            telemetry = telemetry,
            confluenceScore = confluenceScore,
            strategyNotes = strategyNotes
        )
    }

    private fun roundToDecimals(value: Double, format: String): Double {
        return try {
            String.format(java.util.Locale.US, format, value).toDouble()
        } catch (_: Exception) {
            value
        }
    }
}
