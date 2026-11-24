package com.pipbeacon.app.data.provider.impl

import com.pipbeacon.app.core.MarketType
import com.pipbeacon.app.core.TimeFrame
import com.pipbeacon.app.data.provider.CryptoDataProvider
import com.pipbeacon.app.data.provider.ForexDataProvider
import com.pipbeacon.app.data.provider.model.AuthRequirement
import com.pipbeacon.app.data.provider.model.CommercialPermissionStatus
import com.pipbeacon.app.data.provider.model.MarketQuote
import com.pipbeacon.app.data.provider.model.OhlcCandle
import com.pipbeacon.app.data.provider.model.ProviderConnectionState
import com.pipbeacon.app.data.provider.model.ProviderLicensingConfig
import com.pipbeacon.app.data.provider.model.ProviderStatus
import com.pipbeacon.app.data.provider.model.WrittenPermissionStatus
import com.pipbeacon.app.data.provider.network.NetworkHandler
import okhttp3.Request
import org.json.JSONObject

class CustomRestBridgeDataProvider(
    private val networkHandler: NetworkHandler,
    endpointUrl: String = "",
    initialEnabled: Boolean = false,
    initialVerified: Boolean = false
) : ForexDataProvider, CryptoDataProvider {

    override var config = ProviderLicensingConfig(
        id = "provider_custom_bridge",
        name = "Private Custom REST Bridge",
        baseUrl = endpointUrl,
        authRequirement = AuthRequirement.NONE_OPEN,
        dataRefreshLimits = "Determined by self-hosted bridge capacity.",
        attributionRequirement = "Self-hosted private bridge (Authorized by user).",
        commercialDisplayPermission = CommercialPermissionStatus.PERMITTED_OPEN_DATA,
        writtenPermissionStatus = WrittenPermissionStatus.GRANTED,
        isEnabled = initialEnabled,
        isVerifiedPermitted = initialVerified,
        complianceRuleJustification = "Complies with Rule 1 & Rule 2: Personal self-hosted relay where user maintains their own permitted license.",
        documentationUrl = "https://github.com/PipBeacon/market-bridge"
    )
        private set

    fun updateUrl(url: String) {
        config = config.copy(baseUrl = url)
    }

    fun setEnabled(enabled: Boolean) {
        config = config.copy(isEnabled = enabled)
    }

    fun setVerified(verified: Boolean) {
        config = config.copy(isVerifiedPermitted = verified)
    }

    private var lastSuccessfulSync: Long? = null
    private var lastError: String? = null
    private var lastQuoteCount: Int = 0

    override suspend fun getStatus(): ProviderStatus {
        val state = when {
            config.baseUrl.isBlank() -> ProviderConnectionState.DISCONNECTED
            !config.isEnabled -> ProviderConnectionState.DISABLED_PENDING_VERIFICATION
            lastError != null -> ProviderConnectionState.ERROR
            lastSuccessfulSync != null -> ProviderConnectionState.CONNECTED
            else -> ProviderConnectionState.DISCONNECTED
        }
        return ProviderStatus(
            providerId = config.id,
            providerName = config.name,
            state = state,
            lastSuccessfulSync = lastSuccessfulSync,
            lastErrorMessage = lastError,
            quoteCount = lastQuoteCount,
            isVerifiedPermitted = config.isVerifiedPermitted,
            attributionText = config.attributionRequirement,
            licensingNotice = config.commercialDisplayPermission.label,
            rateLimitNotice = config.dataRefreshLimits
        )
    }

    override suspend fun testConnection(): Result<Unit> {
        if (config.baseUrl.isBlank()) {
            return Result.failure(IllegalArgumentException("Custom endpoint URL is empty"))
        }
        val req = Request.Builder().url(config.baseUrl).build()
        val res = networkHandler.executeRequest(req)
        return if (res.isSuccess) {
            lastError = null
            Result.success(Unit)
        } else {
            val err = res.exceptionOrNull()?.message ?: "Custom bridge test failed"
            lastError = err
            Result.failure(res.exceptionOrNull() ?: Exception(err))
        }
    }

    override suspend fun fetchForexQuote(symbol: String): Result<MarketQuote> {
        return fetchQuoteFromBridge(symbol, MarketType.FOREX)
    }

    override suspend fun fetchForexQuotes(symbols: List<String>): Result<List<MarketQuote>> {
        return fetchQuotesFromBridge(symbols, MarketType.FOREX)
    }

    override suspend fun fetchCryptoQuote(symbol: String): Result<MarketQuote> {
        return fetchQuoteFromBridge(symbol, MarketType.CRYPTO)
    }

    override suspend fun fetchCryptoQuotes(symbols: List<String>): Result<List<MarketQuote>> {
        return fetchQuotesFromBridge(symbols, MarketType.CRYPTO)
    }

    private suspend fun fetchQuoteFromBridge(symbol: String, marketType: MarketType): Result<MarketQuote> {
        val quotes = fetchQuotesFromBridge(listOf(symbol), marketType)
        return quotes.mapCatching { list ->
            list.firstOrNull { it.symbol == symbol }
                ?: throw NoSuchElementException("Quote for $symbol not found in custom bridge response")
        }
    }

    private suspend fun fetchQuotesFromBridge(symbols: List<String>, marketType: MarketType): Result<List<MarketQuote>> {
        if (config.baseUrl.isBlank() || !config.isEnabled || !config.isVerifiedPermitted) {
            return Result.failure(IllegalStateException("Custom bridge is not configured or disabled pending verification."))
        }

        val req = Request.Builder().url(config.baseUrl).build()
        val res = networkHandler.executeRequest(req)
        if (res.isFailure) {
            lastError = res.exceptionOrNull()?.message
            return Result.failure(res.exceptionOrNull()!!)
        }

        return try {
            val json = JSONObject(res.getOrThrow())
            val now = System.currentTimeMillis()
            val list = mutableListOf<MarketQuote>()

            symbols.forEach { sym ->
                val key = sym.replace("/", "_").lowercase()
                if (json.has(key)) {
                    val price = json.getDouble(key)
                    list.add(
                        MarketQuote(
                            symbol = sym,
                            marketType = marketType,
                            bidPrice = price,
                            askPrice = price,
                            midPrice = price,
                            timestamp = now,
                            providerId = config.id,
                            providerName = config.name,
                            isStale = false,
                            statusText = "FRESH"
                        )
                    )
                }
            }

            lastSuccessfulSync = now
            lastError = null
            lastQuoteCount = list.size
            Result.success(list)
        } catch (e: Exception) {
            lastError = e.message
            Result.failure(e)
        }
    }

    override suspend fun fetchForexCandles(symbol: String, timeFrame: TimeFrame, count: Int): Result<List<OhlcCandle>> {
        return Result.failure(UnsupportedOperationException("Custom bridge historical candle feed not implemented"))
    }

    override suspend fun fetchCryptoCandles(symbol: String, timeFrame: TimeFrame, count: Int): Result<List<OhlcCandle>> {
        return Result.failure(UnsupportedOperationException("Custom bridge historical candle feed not implemented"))
    }
}
