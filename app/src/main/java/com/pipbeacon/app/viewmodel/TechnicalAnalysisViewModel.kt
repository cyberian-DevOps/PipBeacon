package com.pipbeacon.app.viewmodel

import androidx.lifecycle.ViewModel
import com.pipbeacon.app.core.TimeFrame
import com.pipbeacon.app.domain.CalculateTechnicalAnalysisUseCase
import com.pipbeacon.app.domain.PipCalculationResult
import com.pipbeacon.app.domain.PipCalculatorUseCase
import com.pipbeacon.app.indicators.Candle
import com.pipbeacon.app.indicators.TechnicalAnalysisReport
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class TechnicalAnalysisUiState(
    val selectedSymbol: String = "EUR/USD",
    val selectedTimeFrame: TimeFrame = TimeFrame.H1,
    val isCandleChart: Boolean = true,
    val showSmaOverlay: Boolean = true,
    val showEmaOverlay: Boolean = true,
    val showBollingerOverlay: Boolean = false,
    val candles: List<Candle> = emptyList(),
    val report: TechnicalAnalysisReport? = null,
    // Pip Calculator state
    val showPipCalculatorDialog: Boolean = false,
    val calcEntryPrice: String = "1.0850",
    val calcStopLoss: String = "1.0815",
    val calcTakeProfit: String = "1.0920",
    val calcAccountBalance: String = "10000",
    val calcRiskPercent: String = "1.5",
    val calcLotSize: String = "0.1",
    val calcResult: PipCalculationResult? = null
)

class TechnicalAnalysisViewModel(
    private val calculateTechnicalAnalysisUseCase: CalculateTechnicalAnalysisUseCase,
    private val pipCalculatorUseCase: PipCalculatorUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TechnicalAnalysisUiState())
    val uiState: StateFlow<TechnicalAnalysisUiState> = _uiState.asStateFlow()

    init {
        loadAnalysis("EUR/USD")
        recalculatePipValues()
    }

    fun selectSymbol(symbol: String) {
        _uiState.value = _uiState.value.copy(
            selectedSymbol = symbol,
            calcEntryPrice = if (symbol.contains("BTC")) "67450.0" else if (symbol.contains("JPY")) "152.20" else "1.0850"
        )
        loadAnalysis(symbol)
        recalculatePipValues()
    }

    fun selectTimeFrame(timeFrame: TimeFrame) {
        _uiState.value = _uiState.value.copy(selectedTimeFrame = timeFrame)
        loadAnalysis(_uiState.value.selectedSymbol)
    }

    fun toggleChartType() {
        _uiState.value = _uiState.value.copy(isCandleChart = !_uiState.value.isCandleChart)
    }

    fun toggleSmaOverlay() {
        _uiState.value = _uiState.value.copy(showSmaOverlay = !_uiState.value.showSmaOverlay)
    }

    fun toggleEmaOverlay() {
        _uiState.value = _uiState.value.copy(showEmaOverlay = !_uiState.value.showEmaOverlay)
    }

    fun toggleBollingerOverlay() {
        _uiState.value = _uiState.value.copy(showBollingerOverlay = !_uiState.value.showBollingerOverlay)
    }

    fun openPipCalculator(show: Boolean) {
        _uiState.value = _uiState.value.copy(showPipCalculatorDialog = show)
        if (show) recalculatePipValues()
    }

    fun updateCalcEntryPrice(price: String) {
        _uiState.value = _uiState.value.copy(calcEntryPrice = price)
        recalculatePipValues()
    }

    fun updateCalcStopLoss(sl: String) {
        _uiState.value = _uiState.value.copy(calcStopLoss = sl)
        recalculatePipValues()
    }

    fun updateCalcTakeProfit(tp: String) {
        _uiState.value = _uiState.value.copy(calcTakeProfit = tp)
        recalculatePipValues()
    }

    fun updateCalcAccountBalance(balance: String) {
        _uiState.value = _uiState.value.copy(calcAccountBalance = balance)
        recalculatePipValues()
    }

    fun updateCalcRiskPercent(risk: String) {
        _uiState.value = _uiState.value.copy(calcRiskPercent = risk)
        recalculatePipValues()
    }

    fun updateCalcLotSize(lotSize: String) {
        _uiState.value = _uiState.value.copy(calcLotSize = lotSize)
        recalculatePipValues()
    }

    private fun recalculatePipValues() {
        val state = _uiState.value
        val entry = state.calcEntryPrice.toDoubleOrNull() ?: 1.0850
        val sl = state.calcStopLoss.toDoubleOrNull() ?: 1.0815
        val tp = state.calcTakeProfit.toDoubleOrNull() ?: 1.0920
        val balance = state.calcAccountBalance.toDoubleOrNull() ?: 10000.0
        val riskPct = state.calcRiskPercent.toDoubleOrNull() ?: 1.5
        val lots = state.calcLotSize.toDoubleOrNull() ?: 0.1

        val result = pipCalculatorUseCase.calculate(
            symbol = state.selectedSymbol,
            entryPrice = entry,
            stopLoss = sl,
            takeProfit = tp,
            accountBalance = balance,
            riskPercent = riskPct,
            lotSize = lots
        )
        _uiState.value = _uiState.value.copy(calcResult = result)
    }

    private fun loadAnalysis(symbol: String) {
        val basePrice = when {
            symbol.contains("BTC") -> 67450.0
            symbol.contains("ETH") -> 2540.0
            symbol.contains("SOL") -> 178.50
            symbol.contains("JPY") -> 152.20
            symbol.contains("GBP") -> 1.2980
            else -> 1.0850
        }
        val (candles, report) = calculateTechnicalAnalysisUseCase(symbol)
        _uiState.value = _uiState.value.copy(
            candles = candles,
            report = report
        )
    }
}
