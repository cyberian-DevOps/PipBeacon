package com.pipbeacon.app.engine.signal

import com.pipbeacon.app.core.SignalType
import com.pipbeacon.app.core.TimeFrame

enum class VolatilityLevel(val label: String, val description: String) {
    LOW("Low Volatility", "Market is consolidating; tighter ATR buffers apply."),
    NORMAL("Normal Volatility", "Optimal market movement for technical trend continuation."),
    HIGH("High Volatility", "Elevated volatility; wider ATR stop-loss required to avoid whipsaws."),
    EXTREME("Extreme Volatility", "Event-driven volatility; exercise cautious position sizing.")
}

enum class IndicatorBias(val label: String) {
    BULLISH("Bullish"),
    BEARISH("Bearish"),
    NEUTRAL("Neutral")
}

data class TechnicalIndicatorTelemetry(
    val rsiValue: Double,
    val rsiBias: IndicatorBias,
    val rsiDescription: String,
    val ema20Value: Double,
    val ema50Value: Double,
    val emaBias: IndicatorBias,
    val emaCrossState: String,
    val macdLine: Double,
    val macdSignalLine: Double,
    val macdHistogram: Double,
    val macdBias: IndicatorBias,
    val macdDescription: String,
    val atr14Value: Double,
    val atr14Pips: Double,
    val volatilityLevel: VolatilityLevel,
    val volatilityDescription: String
)

data class TargetLevel(
    val label: String,
    val shortLabel: String,
    val price: Double,
    val pips: Double,
    val riskRewardRatio: Double,
    val description: String
)

data class ForexSignalResult(
    val symbol: String,
    val pairName: String,
    val timeframe: TimeFrame,
    val signalType: SignalType,
    val entryPrice: Double,
    val stopLossPrice: Double,
    val stopLossPips: Double,
    val stopLossRiskReward: String,
    val tp1: TargetLevel,
    val tp2: TargetLevel,
    val tp3: TargetLevel,
    val telemetry: TechnicalIndicatorTelemetry,
    val confluenceScore: Int,
    val strategyNotes: String,
    val calculatedAtTimestamp: Long = System.currentTimeMillis()
) {
    val isBuy: Boolean get() = signalType == SignalType.BUY || signalType == SignalType.STRONG_BUY
    val isSell: Boolean get() = signalType == SignalType.SELL || signalType == SignalType.STRONG_SELL
    val isNeutral: Boolean get() = signalType == SignalType.NEUTRAL

    /**
     * Calculates recommended lot size based on account balance, risk percent, and ATR stop-loss pips.
     */
    fun calculateRecommendedLotSize(
        accountBalance: Double,
        riskPercent: Double,
        pipValuePerStandardLot: Double = 10.0
    ): Double {
        if (stopLossPips <= 0 || pipValuePerStandardLot <= 0) return 0.01
        val riskAmountDollars = accountBalance * (riskPercent / 100.0)
        val rawLot = riskAmountDollars / (stopLossPips * pipValuePerStandardLot)
        return (kotlin.math.round(rawLot * 100.0) / 100.0).coerceIn(0.01, 100.0)
    }
}
