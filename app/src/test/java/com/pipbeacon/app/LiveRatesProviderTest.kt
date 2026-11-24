package com.pipbeacon.app

import com.pipbeacon.app.core.MarketType
import com.pipbeacon.app.data.provider.MarketDataRepository
import com.pipbeacon.app.data.provider.impl.BinanceCryptoDataProvider
import com.pipbeacon.app.data.provider.impl.EcbForexDataProvider
import com.pipbeacon.app.data.provider.network.NetworkHandler
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LiveRatesProviderTest {

    @Test
    fun testBinanceCryptoDataProviderConfig() {
        val networkHandler = NetworkHandler()
        val provider = BinanceCryptoDataProvider(networkHandler, initialEnabled = true, initialVerified = true)

        assertEquals("provider_binance_crypto", provider.config.id)
        assertTrue(provider.config.isEnabled)
        assertTrue(provider.config.isVerifiedPermitted)
        assertEquals("https://api.binance.com/api/v3/ticker/24hr", provider.config.baseUrl)
    }

    @Test
    fun testEcbForexDataProviderConfig() {
        val networkHandler = NetworkHandler()
        val provider = EcbForexDataProvider(networkHandler, initialEnabled = true, initialVerified = true)

        assertEquals("provider_ecb_forex", provider.config.id)
        assertTrue(provider.config.isEnabled)
        assertTrue(provider.config.isVerifiedPermitted)
        assertEquals("https://open.er-api.com/v6/latest/USD", provider.config.baseUrl)
    }

    @Test
    fun testMarketDataRepositoryProviderRegistration() {
        val repo = MarketDataRepository()

        assertNotNull(repo.binanceProvider)
        assertNotNull(repo.ecbProvider)
        repo.enableLiveProviders()
        assertTrue(repo.hasAnyActivePermittedProvider())

        val statuses = runBlocking {
            repo.refreshStatuses()
            repo.providerStatusesFlow.value
        }

        assertTrue(statuses.any { it.providerId == "provider_binance_crypto" })
        assertTrue(statuses.any { it.providerId == "provider_ecb_forex" })
    }
}
