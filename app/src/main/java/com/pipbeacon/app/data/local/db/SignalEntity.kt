package com.pipbeacon.app.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "signals")
data class SignalEntity(
    @PrimaryKey
    val signalId: String,
    val symbol: String,
    val name: String,
    val marketType: String,
    val direction: String,
    val timeframe: String,
    val entryPrice: Double,
    val stopLoss: Double,
    val takeProfit1: Double,
    val takeProfit2: Double,
    val takeProfit3: Double,
    val riskRewardRatio: Double,
    val riskRewardText: String,
    val strategyVersion: String,
    val creationTimestamp: Long,
    val source: String,
    val approvalStatus: String,
    val outcomeStatus: String,
    val plainLanguageExplanation: String,
    val resolvedTimestamp: Long?,
    val resolvedPrice: Double?,
    val isCompleted: Boolean
)
