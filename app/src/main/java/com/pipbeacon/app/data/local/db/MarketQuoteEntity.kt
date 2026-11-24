package com.pipbeacon.app.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pipbeacon.app.core.MarketType

@Entity(tableName = "market_quotes")
data class MarketQuoteEntity(
    @PrimaryKey
    val symbol: String,
    val name: String,
    val marketType: String, // MarketType name
    val midPrice: Double,
    val bidPrice: Double?,
    val askPrice: Double?,
    val change24hPercent: Double?,
    val high24h: Double?,
    val low24h: Double?,
    val volume24h: Double?,
    val providerId: String,
    val providerName: String,
    val timestamp: Long,
    val status: String
)
