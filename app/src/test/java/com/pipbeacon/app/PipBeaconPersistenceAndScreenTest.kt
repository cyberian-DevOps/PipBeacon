package com.pipbeacon.app

import com.pipbeacon.app.core.MarketType
import com.pipbeacon.app.core.SignalType
import com.pipbeacon.app.core.TimeFrame
import com.pipbeacon.app.data.MarketRepository
import com.pipbeacon.app.data.SettingsRepository
import com.pipbeacon.app.data.SignalItem
import com.pipbeacon.app.data.SignalRepository
import com.pipbeacon.app.data.local.db.MarketQuoteEntity
import com.pipbeacon.app.data.local.db.SignalEntity
import com.pipbeacon.app.domain.GetTradingSignalsUseCase
import com.pipbeacon.app.viewmodel.SignalViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PipBeaconPersistenceAndScreenTest {

    @Test
    fun testForexMarketsContainAllRequiredPairs() = runBlocking {
        val settingsRepo = SettingsRepository()
        val marketRepo = MarketRepository(settingsRepo)

        val forexList = marketRepo.forexList.first()
        val symbols = forexList.map { it.symbol }

        assertTrue("Must include EUR/USD", symbols.contains("EUR/USD"))
        assertTrue("Must include GBP/USD", symbols.contains("GBP/USD"))
        assertTrue("Must include USD/JPY", symbols.contains("USD/JPY"))
        assertTrue("Must include USD/CHF", symbols.contains("USD/CHF"))
        assertTrue("Must include AUD/USD", symbols.contains("AUD/USD"))
        assertTrue("Must include USD/CAD", symbols.contains("USD/CAD"))
    }

    @Test
    fun testCryptoMarketsContainAllRequiredPairs() = runBlocking {
        val settingsRepo = SettingsRepository()
        val marketRepo = MarketRepository(settingsRepo)

        val cryptoList = marketRepo.cryptoList.first()
        val symbols = cryptoList.map { it.symbol }

        assertTrue("Must include BTC", symbols.any { it.contains("BTC") })
        assertTrue("Must include ETH", symbols.any { it.contains("ETH") })
        assertTrue("Must include SOL", symbols.any { it.contains("SOL") })
        assertTrue("Must include XRP", symbols.any { it.contains("XRP") })
        assertTrue("Must include LTC", symbols.any { it.contains("LTC") })
    }

    @Test
    fun testSignalDetailModelHasTp1Tp2Tp3AndIndicators() = runBlocking {
        val settingsRepo = SettingsRepository()
        val signalRepo = SignalRepository(settingsRepo)

        val signals = signalRepo.signals.first()
        assertTrue("Must have initial signals", signals.isNotEmpty())

        val eurusdSignal = signals.first { it.symbol == "EUR/USD" }
        assertNotNull("Entry price required", eurusdSignal.entryPrice)
        assertNotNull("Stop loss required", eurusdSignal.stopLoss)
        assertNotNull("TP1 required", eurusdSignal.takeProfit1)
        assertNotNull("TP2 required", eurusdSignal.takeProfit2)
        assertNotNull("TP3 required", eurusdSignal.takeProfit3)
        assertTrue("Indicators snapshot populated", eurusdSignal.indicatorsSnapshot.isNotEmpty())
        assertTrue("Contains EMA 20 in snapshot", eurusdSignal.indicatorsSnapshot.containsKey("EMA 20"))
        assertTrue("Contains RSI in snapshot", eurusdSignal.indicatorsSnapshot.containsKey("RSI (14)"))
        assertTrue("Has plain language rationale", eurusdSignal.indicatorReasoning.isNotBlank())
    }

    @Test
    fun testSignalViewModelFilters() = runBlocking {
        val settingsRepo = SettingsRepository()
        val signalRepo = SignalRepository(settingsRepo)
        val useCase = GetTradingSignalsUseCase(signalRepo)
        val viewModel = SignalViewModel(useCase)

        // Test BUY filter
        viewModel.setFilter("BUY")
        val buySignals = viewModel.filteredSignals.first()
        assertTrue("All filtered signals must be BUY", buySignals.all {
            it.signalType == SignalType.BUY || it.signalType == SignalType.STRONG_BUY
        })

        // Test SELL filter
        viewModel.setFilter("SELL")
        val sellSignals = viewModel.filteredSignals.first()
        assertTrue("All filtered signals must be SELL", sellSignals.all {
            it.signalType == SignalType.SELL || it.signalType == SignalType.STRONG_SELL
        })

        // Test FOREX filter
        viewModel.setFilter("FOREX")
        val forexSignals = viewModel.filteredSignals.first()
        assertTrue("All filtered signals must be FOREX", forexSignals.all {
            it.marketType == MarketType.FOREX
        })

        // Test CRYPTO filter
        viewModel.setFilter("CRYPTO")
        val cryptoSignals = viewModel.filteredSignals.first()
        assertTrue("All filtered signals must be CRYPTO", cryptoSignals.all {
            it.marketType == MarketType.CRYPTO
        })

        // Test ACTIVE filter
        viewModel.setFilter("ACTIVE")
        val activeSignals = viewModel.filteredSignals.first()
        assertTrue("All filtered signals must be ACTIVE/PENDING", activeSignals.all {
            it.outcomeStatus == "PENDING" || it.status.equals("ACTIVE", ignoreCase = true)
        })

        // Test COMPLETED filter
        viewModel.setFilter("COMPLETED")
        val completedSignals = viewModel.filteredSignals.first()
        assertTrue("Completed signals list must have items", completedSignals.isNotEmpty())
        assertTrue("All filtered signals must be COMPLETED (hit TP or SL)", completedSignals.all {
            it.outcomeStatus != "PENDING"
        })
    }

    @Test
    fun testRoomEntityMappingIntegrity() {
        val quoteEntity = MarketQuoteEntity(
            symbol = "EUR/USD",
            name = "Euro / US Dollar",
            marketType = "FOREX",
            midPrice = 1.0855,
            bidPrice = 1.0854,
            askPrice = 1.0856,
            change24hPercent = 0.15,
            high24h = 1.0880,
            low24h = 1.0820,
            volume24h = null,
            providerId = "ecb",
            providerName = "European Central Bank",
            timestamp = System.currentTimeMillis(),
            status = "LIVE"
        )
        assertEquals("EUR/USD", quoteEntity.symbol)
        assertEquals(1.0855, quoteEntity.midPrice, 0.0001)

        val signalEntity = SignalEntity(
            signalId = "sig_test_01",
            symbol = "BTC/USDT",
            name = "Bitcoin",
            marketType = "CRYPTO",
            direction = "BUY",
            timeframe = "H4",
            entryPrice = 67000.0,
            stopLoss = 66000.0,
            takeProfit1 = 69000.0,
            takeProfit2 = 71000.0,
            takeProfit3 = 73000.0,
            riskRewardRatio = 3.0,
            riskRewardText = "1 : 3.0",
            strategyVersion = "v1.2-local-rules",
            creationTimestamp = System.currentTimeMillis(),
            source = "Local Deterministic Engine",
            approvalStatus = "VALIDATED_HISTORICAL",
            outcomeStatus = "PENDING",
            plainLanguageExplanation = "Breakout test",
            resolvedTimestamp = null,
            resolvedPrice = null,
            isCompleted = false
        )
        assertEquals("sig_test_01", signalEntity.signalId)
        assertEquals(73000.0, signalEntity.takeProfit3, 0.01)
    }
}
