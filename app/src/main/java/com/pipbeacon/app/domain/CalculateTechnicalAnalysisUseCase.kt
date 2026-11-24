package com.pipbeacon.app.domain

import com.pipbeacon.app.data.MarketRepository
import com.pipbeacon.app.indicators.Candle
import com.pipbeacon.app.indicators.TechnicalAnalysisReport
import com.pipbeacon.app.indicators.TechnicalIndicatorsEngine

class CalculateTechnicalAnalysisUseCase(private val repository: MarketRepository) {
    operator fun invoke(symbol: String, customCandles: List<Candle>? = null): Pair<List<Candle>, TechnicalAnalysisReport> {
        val candles = customCandles ?: repository.getHistoricalCandlesForAnalysis(symbol)
        val report = TechnicalIndicatorsEngine.analyze(candles)
        return Pair(candles, report)
    }
}
