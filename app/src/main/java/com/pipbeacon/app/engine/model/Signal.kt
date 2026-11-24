package com.pipbeacon.app.engine.model

import com.pipbeacon.app.core.MarketType
import com.pipbeacon.app.core.TimeFrame

data class Signal(
    val signalId: String,
    val symbol: String,
    val marketType: MarketType,
    val direction: SignalDirection,
    val timeframe: TimeFrame,
    val entryPrice: Double,
    val stopLoss: Double,
    val takeProfit1: Double,
    val takeProfit2: Double,
    val takeProfit3: Double,
    val riskRewardRatio: Double,
    val riskRewardText: String,
    val strategyVersion: String = "v1.2-local-rules",
    val creationTimestamp: Long,
    val source: String = "Local Rule Engine (Deterministic Kotlin)",
    val approvalStatus: SignalApprovalStatus = SignalApprovalStatus.EXPERIMENTAL_UNVERIFIED,
    var outcomeStatus: SignalOutcomeStatus = SignalOutcomeStatus.PENDING,
    val plainLanguageExplanation: String,
    val technicalSnapshot: TechnicalSnapshot? = null,
    val resolvedTimestamp: Long? = null,
    val resolvedPrice: Double? = null
)
