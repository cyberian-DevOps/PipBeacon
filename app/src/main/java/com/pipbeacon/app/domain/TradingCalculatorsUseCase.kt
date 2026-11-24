package com.pipbeacon.app.domain

import kotlin.math.abs

data class PositionSizeResult(
    val lotSize: Double,
    val riskAmount: Double,
    val stopLossPips: Double,
    val pipValueTotal: Double,
    val units: Int
)

data class PipValueResult(
    val valuePerPip: Double,
    val totalPipsForMove: Double,
    val estimatedProfitLoss: Double
)

data class RiskRewardResult(
    val riskRewardRatioText: String,
    val riskRewardNumeric: Double,
    val riskPips: Double,
    val rewardPips: Double,
    val breakevenWinRatePercent: Double,
    val isFavorable: Boolean
)

class TradingCalculatorsUseCase {

    fun calculatePositionSize(
        accountBalance: Double,
        riskPercentage: Double,
        stopLossPips: Double,
        pipValuePerLot: Double = 10.0
    ): PositionSizeResult {
        if (accountBalance <= 0 || riskPercentage <= 0 || stopLossPips <= 0 || pipValuePerLot <= 0) {
            return PositionSizeResult(0.0, 0.0, stopLossPips, 0.0, 0)
        }
        val riskAmount = accountBalance * (riskPercentage / 100.0)
        val lotSize = riskAmount / (stopLossPips * pipValuePerLot)
        val roundedLots = (lotSize * 100).toInt() / 100.0
        val units = (lotSize * 100_000).toInt()
        val pipValueTotal = lotSize * pipValuePerLot
        return PositionSizeResult(
            lotSize = roundedLots.coerceAtLeast(0.01),
            riskAmount = riskAmount,
            stopLossPips = stopLossPips,
            pipValueTotal = pipValueTotal,
            units = units
        )
    }

    fun calculatePipValue(
        lotSize: Double,
        pipValuePerStandardLot: Double = 10.0,
        pipMovement: Double = 50.0
    ): PipValueResult {
        val valuePerPip = lotSize * pipValuePerStandardLot
        val estimatedPnl = valuePerPip * pipMovement
        return PipValueResult(
            valuePerPip = (valuePerPip * 100).toInt() / 100.0,
            totalPipsForMove = pipMovement,
            estimatedProfitLoss = (estimatedPnl * 100).toInt() / 100.0
        )
    }

    fun calculateRiskReward(
        entryPrice: Double,
        stopLossPrice: Double,
        takeProfitPrice: Double,
        isJpyPair: Boolean = false,
        isCrypto: Boolean = false
    ): RiskRewardResult {
        val multiplier = if (isCrypto) 1.0 else if (isJpyPair) 100.0 else 10_000.0
        val riskDistance = abs(entryPrice - stopLossPrice)
        val rewardDistance = abs(takeProfitPrice - entryPrice)

        if (riskDistance <= 0.0) {
            return RiskRewardResult("1 : 0.0", 0.0, 0.0, 0.0, 100.0, false)
        }

        val rawRisk = riskDistance * multiplier
        val rawReward = rewardDistance * multiplier
        val ratio = rewardDistance / riskDistance
        val ratioFormatted = "1 : " + String.format(java.util.Locale.US, "%.1f", ratio)
        val breakevenRate = 1.0 / (1.0 + ratio) * 100.0

        return RiskRewardResult(
            riskRewardRatioText = ratioFormatted,
            riskRewardNumeric = ratio,
            riskPips = kotlin.math.round(rawRisk * 10.0) / 10.0,
            rewardPips = kotlin.math.round(rawReward * 10.0) / 10.0,
            breakevenWinRatePercent = kotlin.math.round(breakevenRate * 10.0) / 10.0,
            isFavorable = ratio >= 1.5
        )
    }
}
