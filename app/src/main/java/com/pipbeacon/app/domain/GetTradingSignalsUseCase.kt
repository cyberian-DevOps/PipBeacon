package com.pipbeacon.app.domain

import com.pipbeacon.app.data.SignalItem
import com.pipbeacon.app.data.SignalRepository
import kotlinx.coroutines.flow.Flow

class GetTradingSignalsUseCase(private val repository: SignalRepository) {
    operator fun invoke(): Flow<List<SignalItem>> = repository.signals
    fun getCurrentSignals(): List<SignalItem> = repository.getCurrentSignals()
}
