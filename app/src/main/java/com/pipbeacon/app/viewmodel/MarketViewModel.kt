package com.pipbeacon.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pipbeacon.app.core.DataResult
import com.pipbeacon.app.core.MarketType
import com.pipbeacon.app.data.MarketItem
import com.pipbeacon.app.data.MarketRepository
import com.pipbeacon.app.data.MarketSession
import com.pipbeacon.app.domain.GetCryptoMarketsUseCase
import com.pipbeacon.app.domain.GetForexMarketsUseCase
import com.pipbeacon.app.domain.GetMarketSessionsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class LiveRateTab(val label: String) {
    ALL("All Pairs"),
    FOREX("All Forex"),
    MAJORS("Majors"),
    MINORS("Minors & Crosses"),
    METALS("Precious Metals"),
    EXOTICS("Exotics"),
    CRYPTO("All Crypto"),
    LAYER1("Layer 1"),
    DEFI("DeFi"),
    MEMES("Memes"),
    GAINERS("Top Gainers"),
    LOSERS("Top Losers")
}

enum class RateSortOption(val label: String) {
    DEFAULT("Default"),
    PRICE_DESC("Price: High to Low"),
    PRICE_ASC("Price: Low to High"),
    GAIN_DESC("24h Gain %"),
    LOSS_DESC("24h Loss %"),
    SPREAD_ASC("Tightest Spread")
}

data class MarketUiState(
    val searchQuery: String = "",
    val forexCategoryFilter: String = "All",
    val cryptoCategoryFilter: String = "All",
    val liveRateTab: LiveRateTab = LiveRateTab.ALL,
    val sortOption: RateSortOption = RateSortOption.DEFAULT,
    val isRefreshing: Boolean = false,
    val selectedItem: MarketItem? = null,
    val isAutoRefreshActive: Boolean = true,
    val autoRefreshSeconds: Int = 15
)

class MarketViewModel(
    private val marketRepository: MarketRepository,
    getForexMarketsUseCase: GetForexMarketsUseCase,
    getCryptoMarketsUseCase: GetCryptoMarketsUseCase,
    private val getMarketSessionsUseCase: GetMarketSessionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MarketUiState())
    val uiState: StateFlow<MarketUiState> = _uiState.asStateFlow()

    val providerStatus: StateFlow<DataResult<Unit>> = marketRepository.providerStatus
        .stateIn(viewModelScope, SharingStarted.Lazily, DataResult.WaitingForProvider())

    val lastRefreshTime: StateFlow<Long?> = marketRepository.lastRefreshTime
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val forexList: StateFlow<List<MarketItem>> = combine(
        getForexMarketsUseCase(),
        _uiState
    ) { items, state ->
        items.filter { item ->
            val matchesQuery = state.searchQuery.isBlank() ||
                    item.symbol.contains(state.searchQuery, ignoreCase = true) ||
                    item.name.contains(state.searchQuery, ignoreCase = true)
            val matchesCategory = state.forexCategoryFilter == "All" || item.category == state.forexCategoryFilter
            matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val cryptoList: StateFlow<List<MarketItem>> = combine(
        getCryptoMarketsUseCase(),
        _uiState
    ) { items, state ->
        items.filter { item ->
            val matchesQuery = state.searchQuery.isBlank() ||
                    item.symbol.contains(state.searchQuery, ignoreCase = true) ||
                    item.name.contains(state.searchQuery, ignoreCase = true)
            val matchesCategory = state.cryptoCategoryFilter == "All" || item.category == state.cryptoCategoryFilter
            matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Unified Live Rates List with Tab Filtering and Sorting
    val liveRatesList: StateFlow<List<MarketItem>> = combine(
        getForexMarketsUseCase(),
        getCryptoMarketsUseCase(),
        _uiState
    ) { forexItems, cryptoItems, state ->
        val combined = when (state.liveRateTab) {
            LiveRateTab.ALL -> forexItems + cryptoItems
            LiveRateTab.FOREX -> forexItems
            LiveRateTab.MAJORS -> forexItems.filter { it.category == "Major" }
            LiveRateTab.MINORS -> forexItems.filter { it.category == "Minor" }
            LiveRateTab.METALS -> (forexItems + cryptoItems).filter { it.category == "Metals" || it.symbol.contains("XAU") }
            LiveRateTab.EXOTICS -> forexItems.filter { it.category == "Exotic" }
            LiveRateTab.CRYPTO -> cryptoItems
            LiveRateTab.LAYER1 -> cryptoItems.filter { it.category == "Layer 1" }
            LiveRateTab.DEFI -> cryptoItems.filter { it.category == "DeFi" || it.category == "AI & Infra" || it.category == "Modular" }
            LiveRateTab.MEMES -> cryptoItems.filter { it.category == "Meme" }
            LiveRateTab.GAINERS -> (forexItems + cryptoItems).filter { (it.changePercent24h ?: 0.0) > 0.0 }
            LiveRateTab.LOSERS -> (forexItems + cryptoItems).filter { (it.changePercent24h ?: 0.0) < 0.0 }
        }

        val filtered = combined.filter { item ->
            state.searchQuery.isBlank() ||
                    item.symbol.contains(state.searchQuery, ignoreCase = true) ||
                    item.name.contains(state.searchQuery, ignoreCase = true)
        }

        when (state.sortOption) {
            RateSortOption.DEFAULT -> filtered
            RateSortOption.PRICE_DESC -> filtered.sortedByDescending { it.currentPrice ?: 0.0 }
            RateSortOption.PRICE_ASC -> filtered.sortedBy { it.currentPrice ?: Double.MAX_VALUE }
            RateSortOption.GAIN_DESC -> filtered.sortedByDescending { it.changePercent24h ?: Double.NEGATIVE_INFINITY }
            RateSortOption.LOSS_DESC -> filtered.sortedBy { it.changePercent24h ?: Double.POSITIVE_INFINITY }
            RateSortOption.SPREAD_ASC -> filtered.sortedBy { it.pipSpread ?: Double.MAX_VALUE }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _marketSessions = MutableStateFlow<List<MarketSession>>(emptyList())
    val marketSessions: StateFlow<List<MarketSession>> = _marketSessions.asStateFlow()

    private var autoRefreshJob: Job? = null

    init {
        refreshMarketSessions()
        refresh()
        startAutoRefreshLoop()
    }

    private fun startAutoRefreshLoop() {
        autoRefreshJob?.cancel()
        autoRefreshJob = viewModelScope.launch {
            while (isActive) {
                val delaySec = _uiState.value.autoRefreshSeconds
                delay(delaySec * 1000L)
                if (_uiState.value.isAutoRefreshActive) {
                    marketRepository.refreshPrices()
                    refreshMarketSessions()
                }
            }
        }
    }

    fun refreshMarketSessions() {
        _marketSessions.value = getMarketSessionsUseCase()
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun setForexCategoryFilter(cat: String) {
        _uiState.value = _uiState.value.copy(forexCategoryFilter = cat)
    }

    fun setCryptoCategoryFilter(cat: String) {
        _uiState.value = _uiState.value.copy(cryptoCategoryFilter = cat)
    }

    fun setLiveRateTab(tab: LiveRateTab) {
        _uiState.value = _uiState.value.copy(liveRateTab = tab)
    }

    fun setRateSortOption(option: RateSortOption) {
        _uiState.value = _uiState.value.copy(sortOption = option)
    }

    fun toggleAutoRefresh() {
        val current = _uiState.value.isAutoRefreshActive
        _uiState.value = _uiState.value.copy(isAutoRefreshActive = !current)
    }

    fun setAutoRefreshSeconds(seconds: Int) {
        _uiState.value = _uiState.value.copy(autoRefreshSeconds = seconds)
        startAutoRefreshLoop()
    }

    fun selectItem(item: MarketItem?) {
        _uiState.value = _uiState.value.copy(selectedItem = item)
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            marketRepository.refreshPrices()
            refreshMarketSessions()
            _uiState.value = _uiState.value.copy(isRefreshing = false)
        }
    }

    override fun onCleared() {
        super.onCleared()
        autoRefreshJob?.cancel()
    }
}
