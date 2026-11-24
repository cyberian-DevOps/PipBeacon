package com.pipbeacon.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pipbeacon.app.core.SignalType
import com.pipbeacon.app.core.TimeFrame
import com.pipbeacon.app.domain.ComputeForexSignalsUseCase
import com.pipbeacon.app.engine.signal.ForexSignalResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ForexSignalEngineUiState(
    val isLoading: Boolean = false,
    val selectedSymbol: String = "EUR/USD",
    val selectedTimeframe: TimeFrame = TimeFrame.H1,
    val riskMultiplier: Double = 1.5, // 1.0x, 1.5x, 2.0x, 2.5x ATR buffer
    val accountBalance: Double = 10000.0,
    val riskPercentage: Double = 1.0, // 1% default risk
    val filterType: String = "ALL", // ALL, BUY, SELL, HIGH_CONFLUENCE
    val allSignals: List<ForexSignalResult> = emptyList(),
    val filteredSignals: List<ForexSignalResult> = emptyList(),
    val activeSignal: ForexSignalResult? = null,
    val recommendedLotSize: Double = 0.25,
    val lastCalculatedTime: Long = System.currentTimeMillis()
)

class ForexSignalEngineViewModel(
    private val computeForexSignalsUseCase: ComputeForexSignalsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForexSignalEngineUiState(isLoading = true))
    val uiState: StateFlow<ForexSignalEngineUiState> = _uiState.asStateFlow()

    init {
        computeSignals()
    }

    fun computeSignals() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val current = _uiState.value

            val computedSignals = withContext(Dispatchers.Default) {
                computeForexSignalsUseCase(
                    timeframe = current.selectedTimeframe,
                    riskMultiplier = current.riskMultiplier
                )
            }

            val filtered = filterSignals(computedSignals, current.filterType)
            val active = computedSignals.firstOrNull { it.symbol == current.selectedSymbol }
                ?: computedSignals.firstOrNull()

            val lotSize = active?.calculateRecommendedLotSize(
                accountBalance = current.accountBalance,
                riskPercent = current.riskPercentage
            ) ?: 0.10

            _uiState.update {
                it.copy(
                    isLoading = false,
                    allSignals = computedSignals,
                    filteredSignals = filtered,
                    activeSignal = active,
                    selectedSymbol = active?.symbol ?: current.selectedSymbol,
                    recommendedLotSize = lotSize,
                    lastCalculatedTime = System.currentTimeMillis()
                )
            }
        }
    }

    fun selectSymbol(symbol: String) {
        val active = _uiState.value.allSignals.firstOrNull { it.symbol == symbol }
        val lotSize = active?.calculateRecommendedLotSize(
            accountBalance = _uiState.value.accountBalance,
            riskPercent = _uiState.value.riskPercentage
        ) ?: 0.10

        _uiState.update {
            it.copy(
                selectedSymbol = symbol,
                activeSignal = active,
                recommendedLotSize = lotSize
            )
        }
    }

    fun setTimeframe(timeframe: TimeFrame) {
        if (_uiState.value.selectedTimeframe == timeframe) return
        _uiState.update { it.copy(selectedTimeframe = timeframe) }
        computeSignals()
    }

    fun setRiskMultiplier(multiplier: Double) {
        if (_uiState.value.riskMultiplier == multiplier) return
        _uiState.update { it.copy(riskMultiplier = multiplier) }
        computeSignals()
    }

    fun setAccountBalance(balance: Double) {
        val safeBalance = balance.coerceAtLeast(100.0)
        val lotSize = _uiState.value.activeSignal?.calculateRecommendedLotSize(
            accountBalance = safeBalance,
            riskPercent = _uiState.value.riskPercentage
        ) ?: 0.10

        _uiState.update {
            it.copy(
                accountBalance = safeBalance,
                recommendedLotSize = lotSize
            )
        }
    }

    fun setRiskPercentage(riskPercent: Double) {
        val safeRisk = riskPercent.coerceIn(0.1, 10.0)
        val lotSize = _uiState.value.activeSignal?.calculateRecommendedLotSize(
            accountBalance = _uiState.value.accountBalance,
            riskPercent = safeRisk
        ) ?: 0.10

        _uiState.update {
            it.copy(
                riskPercentage = safeRisk,
                recommendedLotSize = lotSize
            )
        }
    }

    fun setFilter(filter: String) {
        _uiState.update { current ->
            val filtered = filterSignals(current.allSignals, filter)
            current.copy(
                filterType = filter,
                filteredSignals = filtered
            )
        }
    }

    private fun filterSignals(signals: List<ForexSignalResult>, filter: String): List<ForexSignalResult> {
        return when (filter.uppercase()) {
            "BUY" -> signals.filter { it.signalType == SignalType.BUY || it.signalType == SignalType.STRONG_BUY }
            "SELL" -> signals.filter { it.signalType == SignalType.SELL || it.signalType == SignalType.STRONG_SELL }
            "HIGH_CONFLUENCE" -> signals.filter { it.confluenceScore >= 75 }
            else -> signals
        }
    }
}
