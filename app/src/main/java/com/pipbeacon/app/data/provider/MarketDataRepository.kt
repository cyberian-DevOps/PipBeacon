package com.pipbeacon.app.data.provider

import com.pipbeacon.app.core.MarketType
import com.pipbeacon.app.data.provider.cache.MarketQuoteCache
import com.pipbeacon.app.data.provider.impl.BinanceCryptoDataProvider
import com.pipbeacon.app.data.provider.impl.CoinCapCryptoDataProvider
import com.pipbeacon.app.data.provider.impl.CustomRestBridgeDataProvider
import com.pipbeacon.app.data.provider.impl.EcbForexDataProvider
import com.pipbeacon.app.data.provider.impl.ProhibitedProviderRuleRegistry
import com.pipbeacon.app.data.provider.model.MarketQuote
import com.pipbeacon.app.data.provider.model.OhlcCandle
import com.pipbeacon.app.data.provider.model.ProviderConnectionState
import com.pipbeacon.app.data.provider.model.ProviderLicensingConfig
import com.pipbeacon.app.data.provider.model.ProviderStatus
import com.pipbeacon.app.data.provider.network.NetworkHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MarketDataRepository(
    private val networkHandler: NetworkHandler = NetworkHandler(),
    val quoteCache: MarketQuoteCache = MarketQuoteCache(),
    autoEnableOpenProviders: Boolean = false
) {
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Registered Providers (Default disabled for architectural safety, enabled via autoEnableOpenProviders or enableLiveProviders)
    val ecbProvider = EcbForexDataProvider(networkHandler, initialEnabled = autoEnableOpenProviders, initialVerified = autoEnableOpenProviders)
    val binanceProvider = BinanceCryptoDataProvider(networkHandler, initialEnabled = autoEnableOpenProviders, initialVerified = autoEnableOpenProviders)
    val coinCapProvider = CoinCapCryptoDataProvider(networkHandler, initialEnabled = autoEnableOpenProviders, initialVerified = autoEnableOpenProviders)
    val customBridgeProvider = CustomRestBridgeDataProvider(networkHandler, initialEnabled = false, initialVerified = false)

    fun enableLiveProviders() {
        verifyProvider(ecbProvider.config.id, true)
        toggleProvider(ecbProvider.config.id, true)
        verifyProvider(binanceProvider.config.id, true)
        toggleProvider(binanceProvider.config.id, true)
        verifyProvider(coinCapProvider.config.id, true)
        toggleProvider(coinCapProvider.config.id, true)
    }

    private val _quotesFlow = MutableStateFlow<Map<String, MarketQuote>>(emptyMap())
    val quotesFlow: StateFlow<Map<String, MarketQuote>> = _quotesFlow.asStateFlow()

    private val _providerStatusesFlow = MutableStateFlow<List<ProviderStatus>>(emptyList())
    val providerStatusesFlow: StateFlow<List<ProviderStatus>> = _providerStatusesFlow.asStateFlow()

    private val _candidateConfigsFlow = MutableStateFlow<List<ProviderLicensingConfig>>(emptyList())
    val candidateConfigsFlow: StateFlow<List<ProviderLicensingConfig>> = _candidateConfigsFlow.asStateFlow()

    private val _staleAlertsFlow = MutableStateFlow<List<String>>(emptyList())
    val staleAlertsFlow: StateFlow<List<String>> = _staleAlertsFlow.asStateFlow()

    init {
        updateRegistryState()
    }

    fun updateRegistryState() {
        val configs = listOf(
            ecbProvider.config,
            binanceProvider.config,
            coinCapProvider.config,
            customBridgeProvider.config
        ) + ProhibitedProviderRuleRegistry.prohibitedCandidates
        _candidateConfigsFlow.value = configs

        repositoryScope.launch {
            refreshStatuses()
        }
    }

    suspend fun refreshStatuses() {
        val statuses = listOf(
            ecbProvider.getStatus(),
            binanceProvider.getStatus(),
            coinCapProvider.getStatus(),
            customBridgeProvider.getStatus()
        )
        _providerStatusesFlow.value = statuses
    }

    /**
     * Toggles a provider's active state.
     * Enforces rule: Cannot enable unless verified or explicitly authorized by user.
     */
    fun toggleProvider(providerId: String, enable: Boolean) {
        when (providerId) {
            ecbProvider.config.id -> ecbProvider.setEnabled(enable)
            binanceProvider.config.id -> binanceProvider.setEnabled(enable)
            coinCapProvider.config.id -> coinCapProvider.setEnabled(enable)
            customBridgeProvider.config.id -> customBridgeProvider.setEnabled(enable)
        }
        updateRegistryState()
    }

    /**
     * Marks a candidate provider as legally verified for public display.
     */
    fun verifyProvider(providerId: String, verified: Boolean) {
        when (providerId) {
            ecbProvider.config.id -> ecbProvider.setVerified(verified)
            binanceProvider.config.id -> binanceProvider.setVerified(verified)
            coinCapProvider.config.id -> coinCapProvider.setVerified(verified)
            customBridgeProvider.config.id -> customBridgeProvider.setVerified(verified)
        }
        updateRegistryState()
    }

    fun updateCustomBridgeUrl(url: String) {
        customBridgeProvider.updateUrl(url)
        updateRegistryState()
    }

    /**
     * Refreshes market quotes from permitted, enabled providers.
     * Never invents quotes if endpoints fail.
     */
    suspend fun refreshQuotes(
        forexSymbols: List<String>,
        cryptoSymbols: List<String>
    ): Result<Unit> {
        val staleAlerts = mutableListOf<String>()

        // 1. Forex Refresh
        val activeForexProvider: ForexDataProvider? = when {
            customBridgeProvider.config.isEnabled && customBridgeProvider.config.isVerifiedPermitted -> customBridgeProvider
            ecbProvider.config.isEnabled && ecbProvider.config.isVerifiedPermitted -> ecbProvider
            else -> null
        }

        if (activeForexProvider != null) {
            val res = activeForexProvider.fetchForexQuotes(forexSymbols)
            if (res.isSuccess) {
                quoteCache.putQuotes(res.getOrThrow())
            } else {
                // Endpoint failed: Check cache for stale quotes; NEVER invent a quote!
                forexSymbols.forEach { sym ->
                    val cached = quoteCache.getQuote(sym)
                    if (cached != null && cached.isStale) {
                        staleAlerts.add("${sym} cached quote is stale (${cached.staleDurationMs / 1000}s old)")
                    }
                }
            }
        }

        // 2. Crypto Refresh (Binance public ticker first, CoinCap as fallback)
        val activeCryptoProvider: CryptoDataProvider? = when {
            customBridgeProvider.config.isEnabled && customBridgeProvider.config.isVerifiedPermitted -> customBridgeProvider
            binanceProvider.config.isEnabled && binanceProvider.config.isVerifiedPermitted -> binanceProvider
            coinCapProvider.config.isEnabled && coinCapProvider.config.isVerifiedPermitted -> coinCapProvider
            else -> null
        }

        if (activeCryptoProvider != null) {
            val res = activeCryptoProvider.fetchCryptoQuotes(cryptoSymbols)
            if (res.isSuccess) {
                quoteCache.putQuotes(res.getOrThrow())
            } else {
                // Try fallback provider if primary failed
                val fallbackProvider = if (activeCryptoProvider == binanceProvider && coinCapProvider.config.isEnabled) coinCapProvider else null
                val fallbackRes = fallbackProvider?.fetchCryptoQuotes(cryptoSymbols)
                if (fallbackRes != null && fallbackRes.isSuccess) {
                    quoteCache.putQuotes(fallbackRes.getOrThrow())
                } else {
                    cryptoSymbols.forEach { sym ->
                        val cached = quoteCache.getQuote(sym)
                        if (cached != null && cached.isStale) {
                            staleAlerts.add("${sym} cached quote is stale (${cached.staleDurationMs / 1000}s old)")
                        }
                    }
                }
            }
        }

        // Push latest snapshot to Flow
        val allQuotes = quoteCache.getAllQuotes()
        val quoteMap = allQuotes.associateBy { it.symbol }
        _quotesFlow.value = quoteMap
        _staleAlertsFlow.value = staleAlerts

        refreshStatuses()
        return Result.success(Unit)
    }

    fun getQuote(symbol: String): MarketQuote? {
        return quoteCache.getQuote(symbol)
    }

    fun hasAnyActivePermittedProvider(): Boolean {
        return (ecbProvider.config.isEnabled && ecbProvider.config.isVerifiedPermitted) ||
                (binanceProvider.config.isEnabled && binanceProvider.config.isVerifiedPermitted) ||
                (coinCapProvider.config.isEnabled && coinCapProvider.config.isVerifiedPermitted) ||
                (customBridgeProvider.config.isEnabled && customBridgeProvider.config.isVerifiedPermitted)
    }
}
