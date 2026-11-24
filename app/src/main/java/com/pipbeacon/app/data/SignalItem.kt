package com.pipbeacon.app.data

import com.pipbeacon.app.core.MarketType
import com.pipbeacon.app.core.SignalType
import com.pipbeacon.app.core.TimeFrame

data class SignalItem(
    val id: String,
    val symbol: String,
    val name: String,
    val marketType: MarketType,
    val signalType: SignalType,
    val timeFrame: TimeFrame,
    val entryPrice: Double?,
    val stopLoss: Double?,
    val takeProfit1: Double?,
    val takeProfit2: Double?,
    val takeProfit3: Double? = null,
    val riskRewardRatio: String,
    val confidencePercent: Int,
    val indicatorReasoning: String,
    val timestamp: Long,
    val status: String = "ACTIVE",
    val isLiveTracked: Boolean = false,
    val strategyVersion: String = "v1.2-local-rules",
    val approvalStatus: String = "EXPERIMENTAL_UNVERIFIED",
    val outcomeStatus: String = "PENDING",
    val indicatorsSnapshot: Map<String, String> = emptyMap(),
    val resolvedTimestamp: Long? = null,
    val resolvedPrice: Double? = null,
    val pipPotential: Int = 180,
    val tpsHitCount: Int = 0,
    val isBreakevenActive: Boolean = false,
    val tpReachedNote: String? = null
) {
    val isBuy: Boolean get() = signalType == SignalType.BUY || signalType == SignalType.STRONG_BUY
    val isSell: Boolean get() = signalType == SignalType.SELL || signalType == SignalType.STRONG_SELL
    val isClosed: Boolean get() = outcomeStatus != "PENDING" || !status.equals("ACTIVE", ignoreCase = true)
}
