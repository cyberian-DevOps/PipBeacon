package com.pipbeacon.app.engine

import com.pipbeacon.app.engine.model.TrendClassification

object TrendClassifier {

    /**
     * Classifies market trend structure based on triple EMA ribbon (EMA 20, 50, 200) alignment and price location.
     */
    fun classifyTrend(
        currentPrice: Double,
        ema20: Double,
        ema50: Double,
        ema200: Double
    ): TrendClassification {
        val isBullishRibbon = ema20 > ema50 && ema50 > ema200
        val isBearishRibbon = ema20 < ema50 && ema50 < ema200

        return when {
            isBullishRibbon && currentPrice > ema20 -> TrendClassification.STRONG_UPTREND
            currentPrice > ema50 && ema50 > ema200 -> TrendClassification.MODERATE_UPTREND
            isBearishRibbon && currentPrice < ema20 -> TrendClassification.STRONG_DOWNTREND
            currentPrice < ema50 && ema50 < ema200 -> TrendClassification.MODERATE_DOWNTREND
            else -> TrendClassification.CONSOLIDATION_RANGING
        }
    }
}
