package com.pipbeacon.app.domain

import com.pipbeacon.app.data.MarketRepository
import com.pipbeacon.app.data.MarketSession

class GetMarketSessionsUseCase(private val repository: MarketRepository) {
    operator fun invoke(): List<MarketSession> = repository.getMarketSessions()
}
