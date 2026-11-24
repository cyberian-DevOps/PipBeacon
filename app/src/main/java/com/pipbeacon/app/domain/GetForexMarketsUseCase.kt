package com.pipbeacon.app.domain

import com.pipbeacon.app.data.MarketItem
import com.pipbeacon.app.data.MarketRepository
import kotlinx.coroutines.flow.Flow

class GetForexMarketsUseCase(private val repository: MarketRepository) {
    operator fun invoke(): Flow<List<MarketItem>> = repository.forexList
}
