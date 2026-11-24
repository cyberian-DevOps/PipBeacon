package com.pipbeacon.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pipbeacon.app.core.MarketType
import com.pipbeacon.app.core.SignalType
import com.pipbeacon.app.data.SignalItem
import com.pipbeacon.app.domain.GetTradingSignalsUseCase
import com.pipbeacon.app.engine.history.SignalHistoryRepository
import com.pipbeacon.app.engine.history.SignalPerformanceStats
import com.pipbeacon.app.engine.model.Signal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SignalUiState(
    val filterType: String = "ALL", // ALL, BUY, SELL, FOREX, CRYPTO
    val onlyHighConfidence: Boolean = false,
    val selectedSignal: SignalItem? = null,
    val showHistoryTab: Boolean = false
)

class SignalViewModel(
    private val getTradingSignalsUseCase: GetTradingSignalsUseCase,
    val signalHistoryRepository: SignalHistoryRepository = SignalHistoryRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignalUiState())
    val uiState: StateFlow<SignalUiState> = _uiState.asStateFlow()

    private var currentSignals: List<SignalItem> = getTradingSignalsUseCase.getCurrentSignals()

    private val _filteredSignals = MutableStateFlow<List<SignalItem>>(filterSignalsList(currentSignals, _uiState.value))
    val filteredSignals: StateFlow<List<SignalItem>> = _filteredSignals.asStateFlow()

    init {
        viewModelScope.launch {
            getTradingSignalsUseCase().collect { signals ->
                currentSignals = signals
                updateFilteredSignals()
            }
        }
    }

    private fun updateFilteredSignals() {
        _filteredSignals.value = filterSignalsList(currentSignals, _uiState.value)
    }

    private fun filterSignalsList(signals: List<SignalItem>, state: SignalUiState): List<SignalItem> {
        return signals.filter { signal ->
            val matchesFilter = when (state.filterType.uppercase()) {
                "BUY" -> signal.signalType == SignalType.BUY || signal.signalType == SignalType.STRONG_BUY
                "SELL" -> signal.signalType == SignalType.SELL || signal.signalType == SignalType.STRONG_SELL
                "FOREX" -> signal.marketType == MarketType.FOREX
                "CRYPTO" -> signal.marketType == MarketType.CRYPTO
                "ACTIVE" -> signal.outcomeStatus == "PENDING" || signal.status.equals("ACTIVE", ignoreCase = true)
                "COMPLETED" -> signal.outcomeStatus != "PENDING" || !signal.status.equals("ACTIVE", ignoreCase = true)
                else -> true
            }
            val matchesConfidence = !state.onlyHighConfidence || signal.confidencePercent >= 80
            matchesFilter && matchesConfidence
        }
    }

    val historicalSignals: StateFlow<List<Signal>> = signalHistoryRepository.signalsFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setFilter(filter: String) {
        _uiState.value = _uiState.value.copy(filterType = filter)
        updateFilteredSignals()
    }

    fun toggleHighConfidence(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(onlyHighConfidence = enabled)
        updateFilteredSignals()
    }

    fun selectSignal(signal: SignalItem?) {
        _uiState.value = _uiState.value.copy(selectedSignal = signal)
    }

    fun setShowHistoryTab(show: Boolean) {
        _uiState.value = _uiState.value.copy(showHistoryTab = show)
    }

    fun getPerformanceStats(): SignalPerformanceStats {
        return signalHistoryRepository.getPerformanceStats()
    }
}
