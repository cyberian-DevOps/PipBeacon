package com.pipbeacon.app.domain

import com.pipbeacon.app.core.TimeFrame
import com.pipbeacon.app.data.MarketRepository
import com.pipbeacon.app.engine.signal.ForexSignalComputationEngine
import com.pipbeacon.app.engine.signal.ForexSignalResult

class ComputeForexSignalsUseCase(
    private val marketRepository: MarketRepository
) {
    /**
     * Computes signals for all supported Forex pairs and metals in the repository.
     */
    operator fun invoke(
        timeframe: TimeFrame = TimeFrame.H1,
        riskMultiplier: Double = 1.5
    ): List<ForexSignalResult> {
        val currentForex = marketRepository.getCurrentForexList()
        return currentForex.map { item ->
            val candles = marketRepository.getHistoricalCandlesForAnalysis(
                symbol = item.symbol,
                basePrice = item.currentPrice ?: 1.0850,
                count = 60
            )
            ForexSignalComputationEngine.computeSignal(
                symbol = item.symbol,
                pairName = item.name,
                candles = candles,
                timeframe = timeframe,
                riskMultiplier = riskMultiplier
            )
        }
    }

    /**
     * Computes signal for a single targeted pair.
     */
    fun computeForSymbol(
        symbol: String,
        pairName: String? = null,
        timeframe: TimeFrame = TimeFrame.H1,
        riskMultiplier: Double = 1.5
    ): ForexSignalResult {
        val item = marketRepository.getCurrentForexList().firstOrNull { it.symbol == symbol }
        val name = pairName ?: item?.name ?: symbol
        val basePrice = item?.currentPrice ?: 1.0850
        val candles = marketRepository.getHistoricalCandlesForAnalysis(
            symbol = symbol,
            basePrice = basePrice,
            count = 60
        )
        return ForexSignalComputationEngine.computeSignal(
            symbol = symbol,
            pairName = name,
            candles = candles,
            timeframe = timeframe,
            riskMultiplier = riskMultiplier
        )
    }
}
