package com.pipbeacon.app.domain

import kotlin.math.abs

data class PipCalculationResult(
    val pipDifference: Double,
    val pipValueUsd: Double,
    val monetaryRiskUsd: Double,
    val monetaryRewardUsd: Double,
    val riskRewardRatio: String,
    val recommendedLotSize: Double
)

class PipCalculatorUseCase {
    fun calculate(
        symbol: String,
        entryPrice: Double,
        stopLoss: Double,
        takeProfit: Double,
        accountBalance: Double,
        riskPercent: Double,
        lotSize: Double = 0.1
    ): PipCalculationResult {
        val isJpy = symbol.contains("JPY", ignoreCase = true)
        val pipMultiplier = if (isJpy) 100.0 else 10000.0

        val stopLossDiff = abs(entryPrice - stopLoss)
        val takeProfitDiff = abs(takeProfit - entryPrice)

        val riskPips = stopLossDiff * pipMultiplier
        val rewardPips = takeProfitDiff * pipMultiplier

        // Standard lot (1.0) = 100,000 units. Pip value for 1 std lot in USD quote is approx $10.
        // For lotSize (e.g. 0.1 mini lot), pip value is $1.00
        val pipValue = 10.0 * lotSize

        val totalRiskUsd = riskPips * pipValue
        val totalRewardUsd = rewardPips * pipValue

        // Position sizing based on account balance and risk %
        val allowedRiskDollars = accountBalance * (riskPercent / 100.0)
        val recommendedLots = if (riskPips > 0) {
            (allowedRiskDollars / (riskPips * 10.0)).coerceIn(0.01, 50.0)
        } else {
            lotSize
        }

        val rrRatio = if (riskPips > 0) {
            String.format("1 : %.2f", rewardPips / riskPips)
        } else {
            "N/A"
        }

        return PipCalculationResult(
            pipDifference = riskPips,
            pipValueUsd = pipValue,
            monetaryRiskUsd = totalRiskUsd,
            monetaryRewardUsd = totalRewardUsd,
            riskRewardRatio = rrRatio,
            recommendedLotSize = recommendedLots
        )
    }
}
