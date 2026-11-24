package com.pipbeacon.app.indicators

import com.pipbeacon.app.core.SignalType

data class IndicatorItem(
    val name: String,
    val value: String,
    val signal: SignalType
)

data class TechnicalAnalysisReport(
    val overallSignal: SignalType,
    val buyCount: Int,
    val neutralCount: Int,
    val sellCount: Int,
    val rsiValue: Double?,
    val macdResult: MacdResult?,
    val bollingerBands: BollingerBandsResult?,
    val stochasticResult: StochasticResult?,
    val atrValue: Double?,
    val movingAverages: List<IndicatorItem>,
    val oscillators: List<IndicatorItem>,
    val standardPivots: PivotLevels?,
    val fibonacciPivots: PivotLevels?
)

object TechnicalIndicatorsEngine {
    fun analyze(candles: List<Candle>): TechnicalAnalysisReport {
        if (candles.isEmpty()) {
            return TechnicalAnalysisReport(
                overallSignal = SignalType.NEUTRAL,
                buyCount = 0,
                neutralCount = 0,
                sellCount = 0,
                rsiValue = null,
                macdResult = null,
                bollingerBands = null,
                stochasticResult = null,
                atrValue = null,
                movingAverages = emptyList(),
                oscillators = emptyList(),
                standardPivots = null,
                fibonacciPivots = null
            )
        }

        val closes = candles.map { it.close }
        val currentPrice = closes.last()
        val latestCandle = candles.last()
        val prevCandle = if (candles.size > 1) candles[candles.size - 2] else latestCandle

        // Oscillators
        val rsi = RsiCalculator.latestRsi(closes)
        val macd = MacdCalculator.latest(closes)
        val bb = BollingerBandsCalculator.latest(closes)
        val stoch = StochasticCalculator.latest(candles)
        val atr = AtrCalculator.latest(candles)

        val oscillatorsList = mutableListOf<IndicatorItem>()

        // RSI signal
        if (rsi != null) {
            val rsiSig = when {
                rsi < 30 -> SignalType.BUY
                rsi > 70 -> SignalType.SELL
                else -> SignalType.NEUTRAL
            }
            oscillatorsList.add(IndicatorItem("RSI (14)", String.format("%.2f", rsi), rsiSig))
        }

        // MACD signal
        if (macd != null) {
            val macdSig = when {
                macd.histogram > 0 && macd.macdLine > macd.signalLine -> SignalType.BUY
                macd.histogram < 0 && macd.macdLine < macd.signalLine -> SignalType.SELL
                else -> SignalType.NEUTRAL
            }
            oscillatorsList.add(
                IndicatorItem(
                    "MACD (12,26,9)",
                    String.format("%.4f", macd.histogram),
                    macdSig
                )
            )
        }

        // Stochastic signal
        if (stoch != null) {
            val stochSig = when {
                stoch.k < 20 && stoch.k > stoch.d -> SignalType.BUY
                stoch.k > 80 && stoch.k < stoch.d -> SignalType.SELL
                else -> SignalType.NEUTRAL
            }
            oscillatorsList.add(
                IndicatorItem(
                    "Stochastic %K(14,3)",
                    String.format("%.1f / %.1f", stoch.k, stoch.d),
                    stochSig
                )
            )
        }

        // Bollinger Bands signal
        if (bb != null) {
            val bbSig = when {
                currentPrice <= bb.lowerBand -> SignalType.BUY
                currentPrice >= bb.upperBand -> SignalType.SELL
                else -> SignalType.NEUTRAL
            }
            oscillatorsList.add(
                IndicatorItem(
                    "Bollinger Bands (20,2)",
                    String.format("%.1f%%", bb.percentB * 100),
                    bbSig
                )
            )
        }

        // Moving Averages
        val maList = mutableListOf<IndicatorItem>()
        val periods = listOf(9, 21, 50, 100, 200)

        for (p in periods) {
            if (closes.size >= p) {
                val emaSeries = MovingAveragesCalculator.calculateEma(closes, p)
                val emaVal = emaSeries.lastOrNull { !it.isNaN() }
                if (emaVal != null) {
                    val sig = if (currentPrice > emaVal) SignalType.BUY else SignalType.SELL
                    maList.add(IndicatorItem("EMA ($p)", String.format("%.4f", emaVal), sig))
                }

                val smaSeries = MovingAveragesCalculator.calculateSma(closes, p)
                val smaVal = smaSeries.lastOrNull { !it.isNaN() }
                if (smaVal != null) {
                    val sig = if (currentPrice > smaVal) SignalType.BUY else SignalType.SELL
                    maList.add(IndicatorItem("SMA ($p)", String.format("%.4f", smaVal), sig))
                }
            }
        }

        // Tally votes
        val allSignals = (oscillatorsList + maList).map { it.signal }
        val buyVotes = allSignals.count { it == SignalType.BUY || it == SignalType.STRONG_BUY }
        val sellVotes = allSignals.count { it == SignalType.SELL || it == SignalType.STRONG_SELL }
        val neutralVotes = allSignals.count { it == SignalType.NEUTRAL }

        val overall = when {
            buyVotes >= sellVotes + 3 && buyVotes >= 5 -> SignalType.STRONG_BUY
            buyVotes > sellVotes -> SignalType.BUY
            sellVotes >= buyVotes + 3 && sellVotes >= 5 -> SignalType.STRONG_SELL
            sellVotes > buyVotes -> SignalType.SELL
            else -> SignalType.NEUTRAL
        }

        // Pivot levels
        val stdPivots = PivotPointsCalculator.calculateStandard(
            prevCandle.high,
            prevCandle.low,
            prevCandle.close
        )
        val fibPivots = PivotPointsCalculator.calculateFibonacci(
            prevCandle.high,
            prevCandle.low,
            prevCandle.close
        )

        return TechnicalAnalysisReport(
            overallSignal = overall,
            buyCount = buyVotes,
            neutralCount = neutralVotes,
            sellCount = sellVotes,
            rsiValue = rsi,
            macdResult = macd,
            bollingerBands = bb,
            stochasticResult = stoch,
            atrValue = atr,
            movingAverages = maList,
            oscillators = oscillatorsList,
            standardPivots = stdPivots,
            fibonacciPivots = fibPivots
        )
    }
}
