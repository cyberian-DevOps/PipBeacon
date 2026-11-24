package com.pipbeacon.app.data.ai

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TutorMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class MessageSender {
    USER, AI_TUTOR
}

data class NewsAnalysisItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val headline: String,
    val sentiment: String, // BUY, SELL, NEUTRAL
    val currencyPair: String, // e.g. USD/JPY, EUR/USD
    val impact: String, // HIGH, MEDIUM, LOW
    val analysisText: String,
    val timestamp: Long = System.currentTimeMillis()
)

class AiTradingService {

    private val tutorKnowledgeBase = mapOf(
        "order block" to """
An Order Block (OB) is a specific price range where institutional market participants (banks, hedge funds) place massive buy or sell orders.

Key Characteristics:
• Bullish Order Block: The last bearish candle before an aggressive, impulsive bullish expansion that breaks market structure (BOS).
• Bearish Order Block: The last bullish candle before an impulsive downward drop that breaks structure.
• Validation: Look for high volume, a resulting Fair Value Gap (FVG), and displacement away from the zone.
• Entry Strategy: Wait for price to retrace back into the 50% equilibrium of the order block, confirming with lower timeframe rejection before entering.
        """.trimIndent(),

        "fair value gap" to """
A Fair Value Gap (FVG) or Inefficiency occurs when price moves so violently in one direction that one side of the market is completely unfulfilled.

How to Identify:
• A 3-candle pattern where Candle 1's high (for bullish FVG) does not overlap with Candle 3's low.
• The empty space between Candle 1 and Candle 3 is the imbalance zone.
• Market Mechanics: Price acts like a magnet to these imbalances. Institutions will often retrace back to fill or "mitigate" the FVG before continuing the dominant trend.
• High Probability Setups: Trade FVGs that are aligned with higher timeframe order flow and located within premium/discount pricing.
        """.trimIndent(),

        "break of structure" to """
A Break of Structure (BOS) indicates trend continuation in Smart Money Concepts (SMC).

Structure Mechanics:
• In an Uptrend: Price forms a Higher High (HH). When a subsequent candle body closes firmly above this swing high, a Bullish BOS occurs.
• In a Downtrend: Price forms a Lower Low (LL). When price closes below this swing low, a Bearish BOS occurs.
• BOS vs CHoCH: A BOS represents continuation in the same trend direction, while a Change of Character (CHoCH) signals an initial trend reversal when the last major counter-structure swing is broken.
• Tip: Always look for a candle BODY close, not just a wick sweep, to confirm a genuine BOS.
        """.trimIndent(),

        "smart money concepts" to """
Smart Money Concepts (SMC) is an analytical framework that studies how institutional liquidity providers, central banks, and market makers navigate the markets.

Core Pillars:
1. Liquidity: Markets move to take out retail stop-loss clusters (Buy-side Liquidity above equal highs, Sell-side Liquidity below equal lows).
2. Market Structure: Identifying Higher Highs, Lower Lows, Breaks of Structure (BOS), and Change of Character (CHoCH).
3. Order Blocks & Imbalances: Pinpointing where institutional capital left footprint zones (Order Blocks & Fair Value Gaps).
4. Premium vs Discount: Buying below the 50% equilibrium level (Discount) and selling above the 50% level (Premium).
        """.trimIndent(),

        "price action" to """
Price Action Trading is the methodology of making trading decisions based purely on the movement and patterns of price over time, rather than relying exclusively on lagging indicators.

Key Components:
• Swing highs and swing lows (market structure).
• Key horizontal support and resistance zones.
• Multi-timeframe trend alignment (Daily -> 4H -> 15M).
• Candlestick morphology: Pin bars (rejection), engulfing bars (momentum), and consolidation breakouts.
• Risk Management: Placing invalidation levels behind structural invalidation points, maintaining at least a 1:2 Risk/Reward ratio.
        """.trimIndent(),

        "support and resistance" to """
Support and Resistance represent key price levels where supply and demand reach a critical imbalance.

Principles:
• Support: A price floor where buying pressure overcomes selling pressure, preventing price from falling further.
• Resistance: A price ceiling where selling pressure overcomes buying pressure, capping upward momentum.
• Role Reversal: Broken support frequently becomes new resistance on a retest, and broken resistance flips into support.
• Dynamic S/R: Moving averages like the EMA 20, EMA 50, and EMA 200 act as floating support and resistance during trending phases.
        """.trimIndent(),

        "risk management" to """
Risk Management is the single most critical factor separating profitable traders from unprofitable ones.

Golden Rules:
1. Never risk more than 1% to 2% of total account equity on any individual trade.
2. Calculate lot size dynamically based on your Stop Loss distance in pips.
3. Always aim for minimum 1:2 Risk/Reward ratio (so a 40% win rate remains profitable).
4. Move stop loss to breakeven once Take Profit 1 is reached.
5. Avoid trading immediately before high-impact economic releases (NFP, CPI, FOMC) unless specifically positioned for volatility.
        """.trimIndent()
    )

    private val initialNewsAnalyses = listOf(
        NewsAnalysisItem(
            headline = "US GDP growth exceeded forecasts at 4.4%, signaling strong economic resilience",
            sentiment = "BUY",
            currencyPair = "USD/JPY",
            impact = "HIGH",
            analysisText = "The US GDP growth exceeded forecasts at 4.4%, signaling strong economic performance, while unemployment claims were slightly lower than expected. This combination indicates a robust economy, likely supporting the US dollar against lower-yielding currencies.",
            timestamp = System.currentTimeMillis() - 7_200_000L
        ),
        NewsAnalysisItem(
            headline = "President announces tariff framework resolution with European trading partners",
            sentiment = "BUY",
            currencyPair = "EUR/USD",
            impact = "HIGH",
            analysisText = "Easing trade tensions and tariff cancellation frameworks remove significant tail risk for European exporters, spurring short-covering and bullish momentum across EUR cross-pairs.",
            timestamp = System.currentTimeMillis() - 18_000_000L
        ),
        NewsAnalysisItem(
            headline = "UK CPI inflation drops unexpectedly to 1.8%, below Bank of England 2% target",
            sentiment = "SELL",
            currencyPair = "GBP/USD",
            impact = "MEDIUM",
            analysisText = "Lower inflation print sharply increases the probability of an accelerated rate cut cycle from the Bank of England, creating downside pressure on Sterling against the Dollar.",
            timestamp = System.currentTimeMillis() - 36_000_000L
        ),
        NewsAnalysisItem(
            headline = "SEC issues new institutional custody clarity for Bitcoin and digital asset treasuries",
            sentiment = "BUY",
            currencyPair = "BTC/USDT",
            impact = "HIGH",
            analysisText = "Regulatory clarity on digital asset custody clears corporate treasury barriers for spot Bitcoin accumulation, triggering institutional spot inflows and momentum breakout.",
            timestamp = System.currentTimeMillis() - 54_000_000L
        )
    )

    private val _newsAnalyses = mutableListOf<NewsAnalysisItem>().apply {
        addAll(initialNewsAnalyses)
    }

    fun getRecentAnalyses(): List<NewsAnalysisItem> = _newsAnalyses.toList()

    suspend fun askTutor(question: String): String = withContext(Dispatchers.Default) {
        val qLower = question.lowercase(Locale.ROOT)

        for ((key, answer) in tutorKnowledgeBase) {
            if (qLower.contains(key) || (key == "smart money concepts" && qLower.contains("smc")) || (key == "fair value gap" && qLower.contains("fvg")) || (key == "break of structure" && qLower.contains("bos"))) {
                return@withContext answer
            }
        }

        if (qLower.contains("lot") || qLower.contains("position") || qLower.contains("pip")) {
            return@withContext "In Forex, a standard lot is 100,000 units (~$10 per pip on EUR/USD), a mini lot is 10,000 units (~$1 per pip), and a micro lot is 1,000 units (~$0.10 per pip). Use PipBeacon's Built-In Trading Calculator tab to automatically size your lots according to your exact account balance and stop loss."
        }

        if (qLower.contains("indicator") || qLower.contains("rsi") || qLower.contains("macd") || qLower.contains("ema")) {
            return@withContext "Technical indicators provide mathematical confirmation of price action:\n• EMA 20/50/200: Gauges trend direction and momentum.\n• RSI (14): Measures velocity (above 70 overbought, below 30 oversold, 50 center line).\n• MACD: Measures convergence and divergence of fast and slow moving averages.\n• ATR (14): Gauges current market volatility for optimal stop loss placement."
        }

        // Generic intelligent trading mentor synthesis
        "Regarding \"$question\": In smart money trading, always examine the higher timeframe market structure first (Daily/4H). Identify key liquidity pools (equal highs/lows) and wait for a structural shift before executing. Ensure your position sizing allows you to endure normal market fluctuations with strict invalidation stops."
    }

    suspend fun analyzeNews(headline: String): NewsAnalysisItem = withContext(Dispatchers.Default) {
        val hLower = headline.lowercase(Locale.ROOT)

        val sentiment: String
        val pair: String
        val impact: String
        val reasoning: String

        if (hLower.contains("gdp") || hLower.contains("fed") || hLower.contains("rate hike") || hLower.contains("jobs") || hLower.contains("payroll") || hLower.contains("dollar")) {
            sentiment = if (hLower.contains("beat") || hLower.contains("strong") || hLower.contains("hike") || hLower.contains("exceed") || hLower.contains("growth")) "BUY" else "SELL"
            pair = "USD/JPY"
            impact = "HIGH"
            reasoning = "Strong macroeconomic indicators reinforce interest rate yield advantage for the US dollar, favoring bullish continuation against lower-yielding funding currencies."
        } else if (hLower.contains("euro") || hLower.contains("ecb") || hLower.contains("europe") || hLower.contains("tariff")) {
            sentiment = if (hLower.contains("cut") || hLower.contains("recession") || hLower.contains("weak")) "SELL" else "BUY"
            pair = "EUR/USD"
            impact = "HIGH"
            reasoning = "European central bank outlook and trade dynamics directly influence liquidity flow. Sentiment aligns with monetary policy divergence and risk appetite."
        } else if (hLower.contains("crypto") || hLower.contains("bitcoin") || hLower.contains("btc") || hLower.contains("sec")) {
            sentiment = if (hLower.contains("ban") || hLower.contains("sue") || hLower.contains("crash")) "SELL" else "BUY"
            pair = "BTC/USDT"
            impact = "HIGH"
            reasoning = "Institutional adoption and regulatory developments drive spot liquidity and market sentiment across major cryptocurrency pairs."
        } else {
            sentiment = "BUY"
            pair = "EUR/USD"
            impact = "MEDIUM"
            reasoning = "Market absorption indicates moderate volatility. Recommended to confirm direction with higher timeframe Fair Value Gap (FVG) and Break of Structure on 1H chart."
        }

        val item = NewsAnalysisItem(
            headline = headline,
            sentiment = sentiment,
            currencyPair = pair,
            impact = impact,
            analysisText = reasoning,
            timestamp = System.currentTimeMillis()
        )
        _newsAnalyses.add(0, item)
        item
    }
}
