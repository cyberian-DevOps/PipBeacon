package com.pipbeacon.app

import com.pipbeacon.app.core.MarketType
import com.pipbeacon.app.data.provider.MarketDataRepository
import com.pipbeacon.app.data.provider.cache.MarketQuoteCache
import com.pipbeacon.app.data.provider.model.MarketQuote
import com.pipbeacon.app.data.provider.network.HttpException
import com.pipbeacon.app.data.provider.network.RetryWithBackoff
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MarketDataProviderArchitectureTest {

    @Test
    fun testProviderDisabledByDefaultUntilVerified() {
        val repository = MarketDataRepository()

        // Strict Rule: A provider must remain disabled for public distribution until verified
        assertFalse("ECB provider must be disabled by default", repository.ecbProvider.config.isEnabled)
        assertFalse("ECB provider must be unverified by default", repository.ecbProvider.config.isVerifiedPermitted)

        assertFalse("Binance provider must be disabled by default", repository.binanceProvider.config.isEnabled)
        assertFalse("Binance provider must be unverified by default", repository.binanceProvider.config.isVerifiedPermitted)

        assertFalse("CoinCap provider must be disabled by default", repository.coinCapProvider.config.isEnabled)
        assertFalse("CoinCap provider must be unverified by default", repository.coinCapProvider.config.isVerifiedPermitted)

        assertFalse("Custom bridge must be disabled by default", repository.customBridgeProvider.config.isEnabled)
        assertFalse("Custom bridge must be unverified by default", repository.customBridgeProvider.config.isVerifiedPermitted)

        assertFalse("No active permitted provider should exist initially", repository.hasAnyActivePermittedProvider())
    }

    @Test
    fun testNeverInventQuoteOnFailure() = runBlocking {
        val repository = MarketDataRepository()

        // Attempt refresh with all providers disabled (default safe state)
        val res = repository.refreshQuotes(listOf("EUR/USD"), listOf("BTC/USDT"))
        assertTrue(res.isSuccess)

        // Must NOT invent or fabricate any quote
        val quotes = repository.quotesFlow.value
        assertTrue("No fake quotes must be created", quotes.isEmpty())
        assertNull(repository.getQuote("EUR/USD"))
        assertNull(repository.getQuote("BTC/USDT"))
    }

    @Test
    fun testQuoteCacheAndStaleDetection() {
        val cache = MarketQuoteCache(
            forexStaleThresholdMs = 10_000L, // 10 seconds for test
            cryptoStaleThresholdMs = 5_000L   // 5 seconds for test
        )

        val now = 100_000L
        val freshQuote = MarketQuote(
            symbol = "EUR/USD",
            marketType = MarketType.FOREX,
            bidPrice = 1.0850,
            askPrice = 1.0852,
            midPrice = 1.0851,
            timestamp = now,
            providerId = "provider_test",
            providerName = "Test Provider",
            isStale = false,
            statusText = "FRESH"
        )

        cache.putQuote(freshQuote)

        // Read at now + 2 seconds: Fresh
        val retrievedFresh = cache.getQuote("EUR/USD", currentTime = now + 2_000L)
        assertNotNull(retrievedFresh)
        assertFalse(retrievedFresh!!.isStale)
        assertEquals("FRESH", retrievedFresh.statusText)

        // Read at now + 15 seconds: Exceeds 10s threshold -> Stale
        val retrievedStale = cache.getQuote("EUR/USD", currentTime = now + 15_000L)
        assertNotNull(retrievedStale)
        assertTrue("Quote must be flagged stale", retrievedStale!!.isStale)
        assertEquals(15_000L, retrievedStale.staleDurationMs)
        assertTrue(retrievedStale.statusText.contains("STALE"))
    }

    @Test
    fun testRetryAbortsImmediatelyOnRateLimit() = runBlocking {
        var attempts = 0
        val result = RetryWithBackoff.execute<String>(maxRetries = 3) {
            attempts++
            Result.failure(HttpException(429, "Rate limit exceeded"))
        }

        assertTrue(result.isFailure)
        assertEquals("Must NOT retry on HTTP 429 rate limit to avoid ban", 1, attempts)
    }

    @Test
    fun testProviderLicensingConfigurationVerification() {
        val repository = MarketDataRepository()

        repository.verifyProvider(repository.ecbProvider.config.id, true)
        repository.toggleProvider(repository.ecbProvider.config.id, true)

        assertTrue(repository.ecbProvider.config.isEnabled)
        assertTrue(repository.ecbProvider.config.isVerifiedPermitted)
        assertTrue(repository.hasAnyActivePermittedProvider())
    }
}
