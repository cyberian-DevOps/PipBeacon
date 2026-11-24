package com.pipbeacon.app.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pipbeacon.app.data.local.db.MarketQuoteEntity
import com.pipbeacon.app.data.local.db.PipBeaconDatabase
import com.pipbeacon.app.data.provider.MarketDataRepository
import com.pipbeacon.app.engine.model.SignalOutcomeStatus
import com.pipbeacon.app.notifications.NotificationHelper
import kotlinx.coroutines.flow.first

class MarketRefreshWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val database = PipBeaconDatabase.getInstance(applicationContext)
            val quoteDao = database.marketQuoteDao()
            val signalDao = database.signalDao()

            val marketDataRepo = MarketDataRepository()
            // Verify & enable public free feeds for background polling if allowed
            marketDataRepo.verifyProvider(marketDataRepo.ecbProvider.config.id, true)
            marketDataRepo.toggleProvider(marketDataRepo.ecbProvider.config.id, true)
            marketDataRepo.verifyProvider(marketDataRepo.coinCapProvider.config.id, true)
            marketDataRepo.toggleProvider(marketDataRepo.coinCapProvider.config.id, true)

            val forexSymbols = listOf("EUR/USD", "GBP/USD", "USD/JPY", "USD/CHF", "AUD/USD", "USD/CAD")
            val cryptoSymbols = listOf("BTC/USDT", "ETH/USDT", "SOL/USDT", "XRP/USDT", "LTC/USDT")

            marketDataRepo.refreshQuotes(forexSymbols, cryptoSymbols)
            val quotes = marketDataRepo.quotesFlow.value.values.toList()

            if (quotes.isNotEmpty()) {
                val entities = quotes.map { q ->
                    MarketQuoteEntity(
                        symbol = q.symbol,
                        name = q.symbol,
                        marketType = q.marketType.name,
                        midPrice = q.midPrice,
                        bidPrice = q.bidPrice,
                        askPrice = q.askPrice,
                        change24hPercent = q.changePercent24h,
                        high24h = q.high24h,
                        low24h = q.low24h,
                        volume24h = null,
                        providerId = q.providerId,
                        providerName = q.providerName,
                        timestamp = q.timestamp,
                        status = q.statusText
                    )
                }
                quoteDao.insertQuotes(entities)

                // Check active pending signals against latest quotes
                val activeSignals = signalDao.getActiveSignals().first()
                for (active in activeSignals) {
                    val matchingQuote = quotes.find { it.symbol == active.symbol }
                    if (matchingQuote != null) {
                        val currentPrice = matchingQuote.midPrice
                        var outcome = active.outcomeStatus
                        val isBuy = active.direction == "BUY"

                        if (isBuy) {
                            if (currentPrice >= active.takeProfit3) outcome = SignalOutcomeStatus.HIT_TP3.name
                            else if (currentPrice >= active.takeProfit2) outcome = SignalOutcomeStatus.HIT_TP2.name
                            else if (currentPrice >= active.takeProfit1) outcome = SignalOutcomeStatus.HIT_TP1.name
                            else if (currentPrice <= active.stopLoss) outcome = SignalOutcomeStatus.STOPPED_OUT.name
                        } else {
                            if (currentPrice <= active.takeProfit3) outcome = SignalOutcomeStatus.HIT_TP3.name
                            else if (currentPrice <= active.takeProfit2) outcome = SignalOutcomeStatus.HIT_TP2.name
                            else if (currentPrice <= active.takeProfit1) outcome = SignalOutcomeStatus.HIT_TP1.name
                            else if (currentPrice >= active.stopLoss) outcome = SignalOutcomeStatus.STOPPED_OUT.name
                        }

                        if (outcome != active.outcomeStatus) {
                            val isCompleted = outcome != SignalOutcomeStatus.PENDING.name
                            signalDao.updateOutcome(
                                id = active.signalId,
                                outcome = outcome,
                                timestamp = System.currentTimeMillis(),
                                price = currentPrice,
                                completed = isCompleted
                            )

                            // Send notification
                            val outcomeTitle = when (outcome) {
                                SignalOutcomeStatus.HIT_TP1.name -> "Target 1 Reached: ${active.symbol}"
                                SignalOutcomeStatus.HIT_TP2.name -> "Target 2 Reached: ${active.symbol}"
                                SignalOutcomeStatus.HIT_TP3.name -> "Target 3 Reached: ${active.symbol}"
                                SignalOutcomeStatus.STOPPED_OUT.name -> "Stop Loss Hit: ${active.symbol}"
                                else -> "Signal Update: ${active.symbol}"
                            }
                            val outcomeMsg = "${active.direction} ${active.symbol} reached $currentPrice (Original Entry: ${active.entryPrice})"

                            NotificationHelper.sendSignalAlert(
                                context = applicationContext,
                                notificationId = active.signalId.hashCode(),
                                title = outcomeTitle,
                                message = outcomeMsg
                            )
                        }
                    }
                }
            }

            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }
}
