package com.pipbeacon.app.engine.history

import com.pipbeacon.app.core.MarketType
import com.pipbeacon.app.core.TimeFrame
import com.pipbeacon.app.data.provider.model.MarketQuote
import com.pipbeacon.app.engine.model.Signal
import com.pipbeacon.app.engine.model.SignalApprovalStatus
import com.pipbeacon.app.engine.model.SignalDirection
import com.pipbeacon.app.engine.model.SignalOutcomeStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SignalPerformanceStats(
    val totalSignals: Int,
    val pendingCount: Int,
    val hitTp1Count: Int,
    val hitTp2Count: Int,
    val hitTp3Count: Int,
    val stoppedOutCount: Int,
    val averageRiskReward: String,
    val disclaimerText: String = "Statistical logging for experimental rule-based setups. Does not guarantee future performance, win rates, or trading profit."
)

class SignalHistoryRepository {

    private val _signalsFlow = MutableStateFlow<List<Signal>>(initialBenchmarkHistory())
    val signalsFlow: StateFlow<List<Signal>> = _signalsFlow.asStateFlow()

    /**
     * Records a newly generated signal into the immutable history log.
     * Original published parameters are preserved and never overwritten.
     */
    fun recordSignal(signal: Signal) {
        val currentList = _signalsFlow.value.toMutableList()
        // Do not add duplicates
        if (currentList.none { it.signalId == signal.signalId }) {
            currentList.add(0, signal) // Add latest at top
            _signalsFlow.value = currentList
        }
    }

    /**
     * Evaluates subsequent market quotes against active/pending signals.
     * Only updates the outcomeStatus; original entry, SL, and TP levels remain immutable.
     */
    fun updateOutcomesWithQuote(quote: MarketQuote) {
        val currentList = _signalsFlow.value
        var changed = false

        val updatedList = currentList.map { signal ->
            if (signal.symbol == quote.symbol && signal.outcomeStatus == SignalOutcomeStatus.PENDING) {
                val newStatus = evaluateOutcome(signal, quote)
                if (newStatus != signal.outcomeStatus) {
                    changed = true
                    signal.copy(
                        outcomeStatus = newStatus,
                        resolvedTimestamp = quote.timestamp,
                        resolvedPrice = quote.midPrice
                    )
                } else {
                    signal
                }
            } else {
                signal
            }
        }

        if (changed) {
            _signalsFlow.value = updatedList
        }
    }

    private fun evaluateOutcome(signal: Signal, quote: MarketQuote): SignalOutcomeStatus {
        val high = quote.high24h ?: quote.midPrice
        val low = quote.low24h ?: quote.midPrice

        return when (signal.direction) {
            SignalDirection.BUY -> {
                when {
                    low <= signal.stopLoss -> SignalOutcomeStatus.STOPPED_OUT
                    high >= signal.takeProfit3 -> SignalOutcomeStatus.HIT_TP3
                    high >= signal.takeProfit2 -> SignalOutcomeStatus.HIT_TP2
                    high >= signal.takeProfit1 -> SignalOutcomeStatus.HIT_TP1
                    else -> SignalOutcomeStatus.PENDING
                }
            }
            SignalDirection.SELL -> {
                when {
                    high >= signal.stopLoss -> SignalOutcomeStatus.STOPPED_OUT
                    low <= signal.takeProfit3 -> SignalOutcomeStatus.HIT_TP3
                    low <= signal.takeProfit2 -> SignalOutcomeStatus.HIT_TP2
                    low <= signal.takeProfit1 -> SignalOutcomeStatus.HIT_TP1
                    else -> SignalOutcomeStatus.PENDING
                }
            }
        }
    }

    fun getPerformanceStats(): SignalPerformanceStats {
        val signals = _signalsFlow.value
        val total = signals.size
        val pending = signals.count { it.outcomeStatus == SignalOutcomeStatus.PENDING }
        val tp1 = signals.count { it.outcomeStatus == SignalOutcomeStatus.HIT_TP1 }
        val tp2 = signals.count { it.outcomeStatus == SignalOutcomeStatus.HIT_TP2 }
        val tp3 = signals.count { it.outcomeStatus == SignalOutcomeStatus.HIT_TP3 }
        val stopped = signals.count { it.outcomeStatus == SignalOutcomeStatus.STOPPED_OUT }

        val avgRr = if (total > 0) {
            val avg = signals.map { it.riskRewardRatio }.average()
            String.format("1 : %.2f", avg)
        } else {
            "N/A"
        }

        return SignalPerformanceStats(
            totalSignals = total,
            pendingCount = pending,
            hitTp1Count = tp1,
            hitTp2Count = tp2,
            hitTp3Count = tp3,
            stoppedOutCount = stopped,
            averageRiskReward = avgRr
        )
    }

    private fun initialBenchmarkHistory(): List<Signal> {
        val now = System.currentTimeMillis()
        val hour = 3600000L

        return listOf(
            Signal(
                signalId = "sig_eurusd_benchmark_01",
                symbol = "EUR/USD",
                marketType = MarketType.FOREX,
                direction = SignalDirection.BUY,
                timeframe = TimeFrame.H1,
                entryPrice = 1.0850,
                stopLoss = 1.0815,
                takeProfit1 = 1.0902,
                takeProfit2 = 1.0937,
                takeProfit3 = 1.0972,
                riskRewardRatio = 2.48,
                riskRewardText = "1 : 2.48",
                strategyVersion = "v1.2-local-rules",
                creationTimestamp = now - (hour * 24),
                source = "Local Rule Engine (Deterministic Kotlin)",
                approvalStatus = SignalApprovalStatus.EXPERIMENTAL_UNVERIFIED,
                outcomeStatus = SignalOutcomeStatus.HIT_TP2,
                plainLanguageExplanation = "Strong Uptrend: Price broke out above EMA 50 & 200 ribbon with RSI(14) at 56.4 and positive MACD histogram. Target 2 reached.",
                resolvedTimestamp = now - (hour * 18),
                resolvedPrice = 1.0940
            ),
            Signal(
                signalId = "sig_btcusdt_benchmark_02",
                symbol = "BTC/USDT",
                marketType = MarketType.CRYPTO,
                direction = SignalDirection.BUY,
                timeframe = TimeFrame.H4,
                entryPrice = 67450.0,
                stopLoss = 66100.0,
                takeProfit1 = 69475.0,
                takeProfit2 = 70825.0,
                takeProfit3 = 72175.0,
                riskRewardRatio = 2.50,
                riskRewardText = "1 : 2.50",
                strategyVersion = "v1.2-local-rules",
                creationTimestamp = now - (hour * 16),
                source = "Local Rule Engine (Deterministic Kotlin)",
                approvalStatus = SignalApprovalStatus.EXPERIMENTAL_UNVERIFIED,
                outcomeStatus = SignalOutcomeStatus.HIT_TP1,
                plainLanguageExplanation = "Moderate Uptrend: Price tested EMA 20 support zone. MACD printed bullish expansion and RSI was 58.0. Target 1 achieved.",
                resolvedTimestamp = now - (hour * 10),
                resolvedPrice = 69500.0
            ),
            Signal(
                signalId = "sig_gbpusd_benchmark_03",
                symbol = "GBP/USD",
                marketType = MarketType.FOREX,
                direction = SignalDirection.SELL,
                timeframe = TimeFrame.M15,
                entryPrice = 1.2980,
                stopLoss = 1.3015,
                takeProfit1 = 1.2927,
                takeProfit2 = 1.2892,
                takeProfit3 = 1.2857,
                riskRewardRatio = 2.51,
                riskRewardText = "1 : 2.51",
                strategyVersion = "v1.2-local-rules",
                creationTimestamp = now - (hour * 12),
                source = "Local Rule Engine (Deterministic Kotlin)",
                approvalStatus = SignalApprovalStatus.EXPERIMENTAL_UNVERIFIED,
                outcomeStatus = SignalOutcomeStatus.STOPPED_OUT,
                plainLanguageExplanation = "Moderate Downtrend: Price rejected resistance below EMA 200 with negative MACD histogram. Volatility spiked and triggered Stop Loss.",
                resolvedTimestamp = now - (hour * 11),
                resolvedPrice = 1.3018
            ),
            Signal(
                signalId = "sig_solusdt_benchmark_04",
                symbol = "SOL/USDT",
                marketType = MarketType.CRYPTO,
                direction = SignalDirection.BUY,
                timeframe = TimeFrame.H1,
                entryPrice = 178.50,
                stopLoss = 172.00,
                takeProfit1 = 188.25,
                takeProfit2 = 194.75,
                takeProfit3 = 201.25,
                riskRewardRatio = 2.50,
                riskRewardText = "1 : 2.50",
                strategyVersion = "v1.2-local-rules",
                creationTimestamp = now - (hour * 6),
                source = "Local Rule Engine (Deterministic Kotlin)",
                approvalStatus = SignalApprovalStatus.EXPERIMENTAL_UNVERIFIED,
                outcomeStatus = SignalOutcomeStatus.PENDING,
                plainLanguageExplanation = "Strong Uptrend: Triple EMA ribbon stacked upward. ATR volatility expansion with RSI at 62.1. Signal active in market."
            ),
            Signal(
                signalId = "sig_usdjpy_benchmark_05",
                symbol = "USD/JPY",
                marketType = MarketType.FOREX,
                direction = SignalDirection.BUY,
                timeframe = TimeFrame.H4,
                entryPrice = 152.20,
                stopLoss = 151.40,
                takeProfit1 = 153.40,
                takeProfit2 = 154.20,
                takeProfit3 = 155.00,
                riskRewardRatio = 2.50,
                riskRewardText = "1 : 2.50",
                strategyVersion = "v1.2-local-rules",
                creationTimestamp = now - (hour * 4),
                source = "Local Rule Engine (Deterministic Kotlin)",
                approvalStatus = SignalApprovalStatus.EXPERIMENTAL_UNVERIFIED,
                outcomeStatus = SignalOutcomeStatus.PENDING,
                plainLanguageExplanation = "Strong Uptrend: Price consolidating above EMA 20 support with bullish MACD histogram. Signal active in market."
            )
        )
    }
}
