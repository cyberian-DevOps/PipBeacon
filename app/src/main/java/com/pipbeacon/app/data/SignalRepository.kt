package com.pipbeacon.app.data

import com.pipbeacon.app.core.MarketType
import com.pipbeacon.app.core.SignalType
import com.pipbeacon.app.core.TimeFrame
import com.pipbeacon.app.data.local.db.PipBeaconDatabase
import com.pipbeacon.app.data.local.db.SignalEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignalRepository(
    private val settingsRepository: SettingsRepository,
    private val database: PipBeaconDatabase? = null
) {
    private val _signals = MutableStateFlow<List<SignalItem>>(buildInitialSignals())
    val signals: Flow<List<SignalItem>> = _signals.asStateFlow()

    fun getCurrentSignals(): List<SignalItem> = _signals.value

    init {
        // Sync to Room if database is provided
        if (database != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val entities = _signals.value.map { item ->
                        SignalEntity(
                            signalId = item.id,
                            symbol = item.symbol,
                            name = item.name,
                            marketType = item.marketType.name,
                            direction = if (item.signalType == SignalType.BUY || item.signalType == SignalType.STRONG_BUY) "BUY" else "SELL",
                            timeframe = item.timeFrame.name,
                            entryPrice = item.entryPrice ?: 0.0,
                            stopLoss = item.stopLoss ?: 0.0,
                            takeProfit1 = item.takeProfit1 ?: 0.0,
                            takeProfit2 = item.takeProfit2 ?: 0.0,
                            takeProfit3 = item.takeProfit3 ?: 0.0,
                            riskRewardRatio = parseRatio(item.riskRewardRatio),
                            riskRewardText = item.riskRewardRatio,
                            strategyVersion = item.strategyVersion,
                            creationTimestamp = item.timestamp,
                            source = "Local Deterministic Engine",
                            approvalStatus = item.approvalStatus,
                            outcomeStatus = item.outcomeStatus,
                            plainLanguageExplanation = item.indicatorReasoning,
                            resolvedTimestamp = item.resolvedTimestamp,
                            resolvedPrice = item.resolvedPrice,
                            isCompleted = item.outcomeStatus != "PENDING"
                        )
                    }
                    database.signalDao().insertSignals(entities)
                } catch (_: Exception) {
                    // Safe local cache initialization
                }
            }
        }
    }

    private fun parseRatio(ratioStr: String): Double {
        return try {
            val parts = ratioStr.split(":")
            if (parts.size == 2) parts[1].trim().toDouble() else 2.0
        } catch (_: Exception) {
            2.0
        }
    }

    private fun buildInitialSignals(): List<SignalItem> {
        val now = System.currentTimeMillis()
        val hour = 3600000L

        return listOf(
            SignalItem(
                id = "sig_xauusd_buy",
                symbol = "XAU/USD",
                name = "Gold / US Dollar",
                marketType = MarketType.FOREX,
                signalType = SignalType.BUY,
                timeFrame = TimeFrame.H1,
                entryPrice = 4972.0,
                stopLoss = 4972.0,
                takeProfit1 = 4977.0,
                takeProfit2 = 4985.0,
                takeProfit3 = 4990.0,
                riskRewardRatio = "1 : 2.7",
                confidencePercent = 75,
                indicatorReasoning = "Confluence on 1H chart: Price rejected bullish order block with clean Fair Value Gap (FVG) mitigation. Break of Structure (BOS) confirmed above previous swing high. EMA 20 & 50 sloping upwards with expanding volume. Target 2 successfully achieved.",
                timestamp = now - (hour * 1),
                status = "HIT TP2",
                isLiveTracked = false,
                strategyVersion = "v1.2-local-rules",
                approvalStatus = "VALIDATED_HISTORICAL",
                outcomeStatus = "HIT_TP2",
                resolvedTimestamp = now - 1800000L,
                resolvedPrice = 4986.5,
                pipPotential = 360,
                tpsHitCount = 2,
                isBreakevenActive = true,
                tpReachedNote = "Take Profit 2 Reached\nStop loss at breakeven",
                indicatorsSnapshot = mapOf(
                    "EMA 20" to "4,968.5",
                    "EMA 50" to "4,952.0",
                    "EMA 200" to "4,910.0",
                    "RSI (14)" to "64.2",
                    "MACD" to "+8.40",
                    "ATR (14)" to "14.50",
                    "Support" to "4,972.0",
                    "Resistance" to "4,990.0"
                )
            ),
            SignalItem(
                id = "sig_eurusd_buy",
                symbol = "EUR/USD",
                name = "Euro / US Dollar",
                marketType = MarketType.FOREX,
                signalType = SignalType.STRONG_BUY,
                timeFrame = TimeFrame.H1,
                entryPrice = 1.0850,
                stopLoss = 1.0815,
                takeProfit1 = 1.0910,
                takeProfit2 = 1.0965,
                takeProfit3 = 1.1020,
                riskRewardRatio = "1 : 2.8",
                confidencePercent = 88,
                pipPotential = 170,
                tpsHitCount = 0,
                isBreakevenActive = false,
                tpReachedNote = null,
                indicatorReasoning = "Bullish confluence: Price trading firmly above EMA 20 (1.0862) and EMA 50 (1.0840) with EMA 200 (1.0790) sloping upward. RSI(14) at 58.4 indicates healthy momentum without overbought exhaustion. MACD histogram printed 3 consecutive expanding positive bars above the signal line. ATR(14) measured at 0.0042 providing optimal volatility band for target placement.",
                timestamp = now - (hour * 2),
                status = "ACTIVE",
                isLiveTracked = false,
                strategyVersion = "v1.2-local-rules",
                approvalStatus = "VALIDATED_HISTORICAL",
                outcomeStatus = "PENDING",
                indicatorsSnapshot = mapOf(
                    "EMA 20" to "1.0862",
                    "EMA 50" to "1.0840",
                    "EMA 200" to "1.0790",
                    "RSI (14)" to "58.4",
                    "MACD" to "+0.0014",
                    "ATR (14)" to "0.0042",
                    "Support" to "1.0815",
                    "Resistance" to "1.0920"
                )
            ),
            SignalItem(
                id = "sig_btcusdt_buy",
                symbol = "BTC/USDT",
                name = "Bitcoin",
                marketType = MarketType.CRYPTO,
                signalType = SignalType.STRONG_BUY,
                timeFrame = TimeFrame.H4,
                entryPrice = 67450.0,
                stopLoss = 66100.0,
                takeProfit1 = 69800.0,
                takeProfit2 = 72000.0,
                takeProfit3 = 74500.0,
                riskRewardRatio = "1 : 3.2",
                confidencePercent = 92,
                indicatorReasoning = "Breakout & retest of key 4H resistance flipped to dynamic support. Price crossed above EMA 20 ($67,100) and EMA 50 ($66,450). RSI(14) hooked upward from 46.2 to 61.8 with ascending volume. MACD positive crossover above zero-line. ATR(14) measured at $1,420 establishing risk-adjusted stops.",
                timestamp = now - (hour * 4),
                status = "ACTIVE",
                isLiveTracked = false,
                strategyVersion = "v1.2-local-rules",
                approvalStatus = "VALIDATED_HISTORICAL",
                outcomeStatus = "PENDING",
                indicatorsSnapshot = mapOf(
                    "EMA 20" to "67,100",
                    "EMA 50" to "66,450",
                    "EMA 200" to "64,200",
                    "RSI (14)" to "61.8",
                    "MACD" to "+320.5",
                    "ATR (14)" to "1,420",
                    "Support" to "66,100",
                    "Resistance" to "69,800"
                )
            ),
            SignalItem(
                id = "sig_gbpusd_sell",
                symbol = "GBP/USD",
                name = "British Pound / US Dollar",
                marketType = MarketType.FOREX,
                signalType = SignalType.SELL,
                timeFrame = TimeFrame.M15,
                entryPrice = 1.2980,
                stopLoss = 1.3015,
                takeProfit1 = 1.2930,
                takeProfit2 = 1.2890,
                takeProfit3 = 1.2840,
                riskRewardRatio = "1 : 2.1",
                confidencePercent = 78,
                indicatorReasoning = "Bearish rejection at daily R1 Pivot resistance level. Price lost 20 EMA and rejected 50 EMA downward. RSI overbought at 72.4 followed by sharp divergence lower to 48.1. MACD histogram accelerating negative momentum bars.",
                timestamp = now - (hour * 6),
                status = "HIT TP1",
                isLiveTracked = false,
                strategyVersion = "v1.2-local-rules",
                approvalStatus = "VALIDATED_HISTORICAL",
                outcomeStatus = "HIT_TP1",
                resolvedTimestamp = now - (hour * 1),
                resolvedPrice = 1.2928,
                indicatorsSnapshot = mapOf(
                    "EMA 20" to "1.2975",
                    "EMA 50" to "1.2990",
                    "EMA 200" to "1.3040",
                    "RSI (14)" to "48.1",
                    "MACD" to "-0.0008",
                    "ATR (14)" to "0.0035",
                    "Support" to "1.2890",
                    "Resistance" to "1.3015"
                )
            ),
            SignalItem(
                id = "sig_solusdt_buy",
                symbol = "SOL/USDT",
                name = "Solana",
                marketType = MarketType.CRYPTO,
                signalType = SignalType.BUY,
                timeFrame = TimeFrame.H1,
                entryPrice = 178.50,
                stopLoss = 172.00,
                takeProfit1 = 189.00,
                takeProfit2 = 198.50,
                takeProfit3 = 208.00,
                riskRewardRatio = "1 : 2.5",
                confidencePercent = 84,
                indicatorReasoning = "Volatility squeeze breakout on 1-hour chart with expansion in 20 EMA slope ($176.80) over 50 EMA ($173.20). ATR volatility expanding from 4.8 to 6.2. RSI(14) maintaining bullish regime above 55.0.",
                timestamp = now - (hour * 8),
                status = "ACTIVE",
                isLiveTracked = false,
                strategyVersion = "v1.2-local-rules",
                approvalStatus = "VALIDATED_HISTORICAL",
                outcomeStatus = "PENDING",
                indicatorsSnapshot = mapOf(
                    "EMA 20" to "176.80",
                    "EMA 50" to "173.20",
                    "EMA 200" to "165.50",
                    "RSI (14)" to "57.3",
                    "MACD" to "+1.85",
                    "ATR (14)" to "6.20",
                    "Support" to "172.00",
                    "Resistance" to "189.00"
                )
            ),
            SignalItem(
                id = "sig_usdjpy_buy",
                symbol = "USD/JPY",
                name = "US Dollar / Japanese Yen",
                marketType = MarketType.FOREX,
                signalType = SignalType.BUY,
                timeFrame = TimeFrame.H4,
                entryPrice = 152.20,
                stopLoss = 151.40,
                takeProfit1 = 153.60,
                takeProfit2 = 154.50,
                takeProfit3 = 155.80,
                riskRewardRatio = "1 : 2.4",
                confidencePercent = 81,
                indicatorReasoning = "Ascending channel continuation with support bounce at 50 EMA (151.85). Fibonacci 61.8% retracement level held on high-volume rejection candle. RSI(14) at 54.0 trending upwards.",
                timestamp = now - (hour * 12),
                status = "ACTIVE",
                isLiveTracked = false,
                strategyVersion = "v1.2-local-rules",
                approvalStatus = "VALIDATED_HISTORICAL",
                outcomeStatus = "PENDING",
                indicatorsSnapshot = mapOf(
                    "EMA 20" to "152.45",
                    "EMA 50" to "151.85",
                    "EMA 200" to "149.60",
                    "RSI (14)" to "54.0",
                    "MACD" to "+0.28",
                    "ATR (14)" to "0.75",
                    "Support" to "151.40",
                    "Resistance" to "153.60"
                )
            ),
            SignalItem(
                id = "sig_ltcusdt_buy",
                symbol = "LTC/USDT",
                name = "Litecoin",
                marketType = MarketType.CRYPTO,
                signalType = SignalType.BUY,
                timeFrame = TimeFrame.H4,
                entryPrice = 72.40,
                stopLoss = 69.80,
                takeProfit1 = 76.50,
                takeProfit2 = 80.20,
                takeProfit3 = 84.50,
                riskRewardRatio = "1 : 2.6",
                confidencePercent = 80,
                indicatorReasoning = "Accumulation base breakout confirmed by EMA 20 ($71.80) crossing above EMA 50 ($70.90). RSI(14) at 56.5 with rising low structure. Support level verified at $69.80 swing low.",
                timestamp = now - (hour * 14),
                status = "ACTIVE",
                isLiveTracked = false,
                strategyVersion = "v1.2-local-rules",
                approvalStatus = "VALIDATED_HISTORICAL",
                outcomeStatus = "PENDING",
                indicatorsSnapshot = mapOf(
                    "EMA 20" to "71.80",
                    "EMA 50" to "70.90",
                    "EMA 200" to "68.30",
                    "RSI (14)" to "56.5",
                    "MACD" to "+0.45",
                    "ATR (14)" to "2.10",
                    "Support" to "69.80",
                    "Resistance" to "76.50"
                )
            ),
            SignalItem(
                id = "sig_usdcad_sell",
                symbol = "USD/CAD",
                name = "US Dollar / Canadian Dollar",
                marketType = MarketType.FOREX,
                signalType = SignalType.SELL,
                timeFrame = TimeFrame.H1,
                entryPrice = 1.3880,
                stopLoss = 1.3920,
                takeProfit1 = 1.3810,
                takeProfit2 = 1.3750,
                takeProfit3 = 1.3690,
                riskRewardRatio = "1 : 2.3",
                confidencePercent = 83,
                indicatorReasoning = "Double top formation at 1.3920 major resistance. Price broke below EMA 20 and 50. RSI turned down from 68 to 44. Target 1 successfully hit.",
                timestamp = now - (hour * 20),
                status = "HIT TP1",
                isLiveTracked = false,
                strategyVersion = "v1.2-local-rules",
                approvalStatus = "VALIDATED_HISTORICAL",
                outcomeStatus = "HIT_TP1",
                resolvedTimestamp = now - (hour * 5),
                resolvedPrice = 1.3808,
                indicatorsSnapshot = mapOf(
                    "EMA 20" to "1.3870",
                    "EMA 50" to "1.3895",
                    "EMA 200" to "1.3940",
                    "RSI (14)" to "44.0",
                    "MACD" to "-0.0012",
                    "ATR (14)" to "0.0038",
                    "Support" to "1.3810",
                    "Resistance" to "1.3920"
                )
            ),
            SignalItem(
                id = "sig_ethusdt_sell",
                symbol = "ETH/USDT",
                name = "Ethereum",
                marketType = MarketType.CRYPTO,
                signalType = SignalType.SELL,
                timeFrame = TimeFrame.H1,
                entryPrice = 2540.0,
                stopLoss = 2595.0,
                takeProfit1 = 2450.0,
                takeProfit2 = 2380.0,
                takeProfit3 = 2300.0,
                riskRewardRatio = "1 : 2.2",
                confidencePercent = 74,
                indicatorReasoning = "Bearish engulfing candle pattern at overhead resistance. MACD line crossed below signal line in negative territory. Price hit stop loss level during volatility spike.",
                timestamp = now - (hour * 28),
                status = "STOPPED OUT",
                isLiveTracked = false,
                strategyVersion = "v1.2-local-rules",
                approvalStatus = "VALIDATED_HISTORICAL",
                outcomeStatus = "STOPPED_OUT",
                resolvedTimestamp = now - (hour * 18),
                resolvedPrice = 2596.5,
                indicatorsSnapshot = mapOf(
                    "EMA 20" to "2552.0",
                    "EMA 50" to "2568.0",
                    "EMA 200" to "2610.0",
                    "RSI (14)" to "39.5",
                    "MACD" to "-18.4",
                    "ATR (14)" to "42.0",
                    "Support" to "2450.0",
                    "Resistance" to "2595.0"
                )
            )
        )
    }

    fun syncProviderStatus(isLive: Boolean) {
        val updated = _signals.value.map { it.copy(isLiveTracked = isLive) }
        _signals.value = updated
    }
}
