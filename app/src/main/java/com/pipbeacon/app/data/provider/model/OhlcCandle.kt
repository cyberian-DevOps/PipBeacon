package com.pipbeacon.app.data.provider.model

import com.pipbeacon.app.core.TimeFrame

data class OhlcCandle(
    val timestamp: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Double = 0.0,
    val timeFrame: TimeFrame = TimeFrame.H1,
    val providerId: String = ""
)
