package com.pipbeacon.app.data.lessons

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AcademyTrack(val title: String, val subtitle: String) {
    FOREX("Forex Academy", "Currencies, Pips, Sessions & Smart Money"),
    CRYPTO("Crypto Academy", "Blockchain, Spot vs Perps, DeFi & Custody")
}

data class LessonItem(
    val id: String,
    val title: String,
    val level: String, // Beginner, Intermediate, Advanced, Pro
    val category: String, // forex-basics, price-action, smc, risk-management, crypto-basics, defi
    val track: AcademyTrack = AcademyTrack.FOREX,
    val durationMinutes: Int,
    val isPremium: Boolean = false,
    val isCompleted: Boolean = false,
    val summary: String,
    val fullContent: String
)

class LessonRepository {

    private val completedLessonIds = mutableSetOf<String>()

    private val allLessons = listOf(
        // =====================================================================
        // FOREX ACADEMY LESSONS
        // =====================================================================
        LessonItem(
            id = "lesson_what_is_forex",
            title = "What is Forex Trading?",
            level = "Beginner",
            category = "forex-basics",
            track = AcademyTrack.FOREX,
            durationMinutes = 10,
            isPremium = false,
            summary = "Understanding the basics of foreign exchange markets, quotes, and major liquidity participants.",
            fullContent = """
# What is Forex Trading?

Foreign Exchange (Forex or FX) is the global decentralized marketplace for trading national currencies against one another. With a daily trading volume exceeding $7.5 Trillion, it is by far the largest and most liquid financial market in the world.

### How Currencies are Quoted
Currencies are always traded in pairs (e.g., EUR/USD, GBP/USD, USD/JPY).
• **Base Currency**: The first currency listed (e.g., EUR).
• **Quote Currency**: The second currency listed (e.g., USD).

The exchange rate tells you how much of the quote currency is needed to purchase one unit of the base currency. For example, if EUR/USD is 1.0850, 1 Euro equals 1.0850 US Dollars.

### Major Market Participants
1. **Central Banks** (Federal Reserve, ECB, Bank of Japan) – control monetary policy and interest rates.
2. **Commercial & Investment Banks** (Tier 1 institutions) – provide interbank liquidity.
3. **Multinational Corporations** – hedge international business operations.
4. **Retail Traders** – study price behavior and macroeconomic shifts.
            """.trimIndent()
        ),
        LessonItem(
            id = "lesson_pips_lots_leverage",
            title = "Pips, Lot Sizing & Spread Explained",
            level = "Beginner",
            category = "forex-basics",
            track = AcademyTrack.FOREX,
            durationMinutes = 12,
            isPremium = false,
            summary = "Master pip math, standard vs micro lots, and how bid/ask spreads impact trade execution.",
            fullContent = """
# Pips, Lot Sizing & Spread Explained

Precision in Forex starts with knowing how price movements translate into account risk and reward.

### What is a Pip?
• **Standard Pairs (EUR/USD, GBP/USD)**: 1 Pip = 0.0001 (the 4th decimal place).
• **JPY Crosses (USD/JPY, EUR/JPY)**: 1 Pip = 0.01 (the 2nd decimal place).
• **Gold (XAU/USD)**: 1 Pip = 0.10 movement in spot price.

### Lot Sizes & Value per Pip (USD Accounts)
1. **Standard Lot (1.00)**: 100,000 units ≈ $10.00 per pip.
2. **Mini Lot (0.10)**: 10,000 units ≈ $1.00 per pip.
3. **Micro Lot (0.01)**: 1,000 units ≈ $0.10 per pip.

### Understanding the Spread
Every pair has a **Bid** (sell) price and **Ask** (buy) price. The difference between them is the spread. Tighter spreads during high-liquidity sessions reduce transaction drag.
            """.trimIndent()
        ),
        LessonItem(
            id = "lesson_forex_sessions",
            title = "Market Sessions: London, NY & Tokyo",
            level = "Beginner",
            category = "forex-basics",
            track = AcademyTrack.FOREX,
            durationMinutes = 14,
            isPremium = false,
            summary = "Learn when currency pairs move fastest and why the London/New York overlap drives peak volatility.",
            fullContent = """
# Global Forex Trading Sessions

Even though Forex runs 24 hours a day Monday through Friday, liquidity and volatility cluster around four major financial centers.

### The Four Core Sessions (UTC)
1. **Sydney Session (21:00 – 06:00 UTC)**: Lower volatility, AUD & NZD pairs gain focus.
2. **Tokyo / Asian Session (00:00 – 09:00 UTC)**: JPY crosses become active; ranges frequently form before European open.
3. **London Session (07:00 – 16:00 UTC)**: Accounts for over 35% of global FX volume. EUR, GBP, and CHF pairs establish directional trends.
4. **New York Session (12:00 – 21:00 UTC)**: Major USD economic releases (NFP, CPI, FOMC) drive sharp market repricing.

### The Golden Overlap (12:00 – 16:00 UTC)
When London and New York operate simultaneously, institutional liquidity peaks—producing cleaner breakouts, tighter spreads, and deeper ATR expansion.
            """.trimIndent()
        ),
        LessonItem(
            id = "lesson_support_resistance",
            title = "Support and Resistance",
            level = "Intermediate",
            category = "price-action",
            track = AcademyTrack.FOREX,
            durationMinutes = 15,
            isPremium = false,
            summary = "Locating dynamic and horizontal price floors and ceilings to time structured market setups.",
            fullContent = """
# Support and Resistance Masterclass

Support and resistance (S/R) are structural price levels where buying and selling forces collide.

### Defining Key Levels
• **Support**: A price zone where demand exceeds supply, causing price declines to stall and bounce higher.
• **Resistance**: A price zone where supply exceeds demand, halting upward rallies.

### The Role Reversal Principle
When a strong support level is broken to the downside, that exact level frequently becomes resistance when price returns to retest it. Conversely, broken resistance becomes support.

### Types of Support & Resistance
1. **Horizontal Swing Pivots**: Prior major swing highs and swing lows.
2. **Psychological Round Numbers**: Key psychological barriers like 1.1000, 1.3000, or $2,700 in Gold.
3. **Dynamic Moving Averages**: The 20 EMA, 50 EMA, and 200 EMA act as floating support/resistance in trending markets.
            """.trimIndent()
        ),
        LessonItem(
            id = "lesson_smc_order_blocks",
            title = "Smart Money Concepts & Order Blocks",
            level = "Intermediate",
            category = "smc",
            track = AcademyTrack.FOREX,
            durationMinutes = 25,
            isPremium = false,
            summary = "Learn how institutions accumulate positions and how to identify institutional order footprints.",
            fullContent = """
# Smart Money Concepts: Order Blocks

Institutions cannot execute multi-billion dollar positions with a single market order without extreme slippage. Instead, they build positions inside consolidation zones known as **Order Blocks**.

### The Bullish Order Block (OB)
• The last down-close (bearish) candle immediately preceding an aggressive, impulsive bullish breakout that creates a Break of Structure (BOS).
• The order block contains resting institutional buy limit orders.
• **Execution Study**: Wait patiently for price to retrace back into the 50% equilibrium of the order block, confirm lower-timeframe rejection, and measure invalidation below the order block low.

### Fair Value Gaps (FVG)
When institutional orders enter the market rapidly, they create 3-candle price imbalances where one party was excluded. Price frequently returns to rebalance these inefficiencies before continuing the dominant trend.
            """.trimIndent()
        ),
        LessonItem(
            id = "lesson_portfolio_management",
            title = "ATR Risk Management & The 1% Rule",
            level = "Pro",
            category = "risk-management",
            track = AcademyTrack.FOREX,
            durationMinutes = 30,
            isPremium = true,
            summary = "Protect capital using ATR volatility stops, the 1% risk allocation cap, and TP1/TP2/TP3 scaling.",
            fullContent = """
# ATR Risk Management & Portfolio Protection

Long-term consistency is determined not by occasional big wins, but by how systematically you control downside risk during drawdowns.

### The 1% Risk Allocation Rule
Never risk more than 1% of your total account balance on any single trade idea.
If your demo account balance is $10,000:
• Max risk = $100.
• If ATR(14) dictates a 50-pip Stop-Loss buffer, your calculated size is 0.20 lots.

### Multi-Tier Target Management
• **TP1 (1:1 R:R)**: Bank 33%–50% of the setup and consider moving Stop-Loss to Breakeven.
• **TP2 (1:2 R:R)**: Capture core daily ATR expansion.
• **TP3 (1:3 R:R)**: Trail the remaining runner behind structural swing points.
            """.trimIndent()
        ),

        // =====================================================================
        // CRYPTO ACADEMY LESSONS
        // =====================================================================
        LessonItem(
            id = "lesson_crypto_blockchain_basics",
            title = "Blockchain, Bitcoin & Market Structure",
            level = "Beginner",
            category = "crypto-basics",
            track = AcademyTrack.CRYPTO,
            durationMinutes = 12,
            isPremium = false,
            summary = "Understand distributed ledgers, Bitcoin scarcity, and why crypto trades 24/7 with unique liquidity.",
            fullContent = """
# Blockchain, Bitcoin & Crypto Market Structure

Unlike traditional Forex markets that close on weekends, cryptocurrency exchanges operate continuously 24/7/365 across global venues.

### Why Crypto Moves Differently
• **Fixed Supply Mechanics**: Bitcoin has a hard cap of 21 million coins, making demand shocks amplified during accumulation waves.
• **24/7 Continuous Price Discovery**: Weekend liquidity can be thinner, leading to sharp Sunday evening volatility spikes before CME futures open.
• **High Beta Sensitivity**: Altcoins (SOL, AVAX, LINK) often move 2x–3x more in percentage terms than BTC.

### Core Pair Conventions
Most active pairs trade against **USDT** or **USDC** (dollar-pegged stablecoins), or against **BTC** to measure relative altcoin strength.
            """.trimIndent()
        ),
        LessonItem(
            id = "lesson_crypto_spot_vs_perps",
            title = "Spot vs Perpetual Futures & Funding Rates",
            level = "Beginner",
            category = "crypto-basics",
            track = AcademyTrack.CRYPTO,
            durationMinutes = 16,
            isPremium = false,
            summary = "Learn the difference between owning spot coins, perpetual contracts, open interest, and funding rates.",
            fullContent = """
# Spot vs Perpetual Futures & Funding Rates

Understanding derivatives positioning helps explain sudden squeezes in Bitcoin and major altcoins.

### Spot Markets
• Direct ownership of the underlying digital asset (e.g., buying 1 ETH on spot).
• Zero liquidation risk when held unleveraged.

### Perpetual Futures ("Perps")
• Derivative contracts with no expiration date that track spot prices.
• **Funding Rate**: Periodic payments exchanged every 8 hours between longs and shorts to anchor perp prices to spot.
  - High positive funding = crowded longs (risk of long squeeze).
  - Negative funding = crowded shorts (potential short squeeze).
            """.trimIndent()
        ),
        LessonItem(
            id = "lesson_crypto_order_books",
            title = "Order Books, Liquidity Walls & Slippage",
            level = "Intermediate",
            category = "crypto-basics",
            track = AcademyTrack.CRYPTO,
            durationMinutes = 18,
            isPremium = false,
            summary = "Read exchange order book depth, detect bid/ask liquidity walls, and avoid high-slippage fills.",
            fullContent = """
# Order Books, Liquidity Walls & Slippage

Every cryptocurrency exchange matches buyers and sellers through a central limit order book (CLOB).

### Maker vs Taker Orders
• **Limit Order (Maker)**: Rests on the order book at your specified price, adding liquidity.
• **Market Order (Taker)**: Executes immediately against resting orders, paying spread + taker fees.

### Reading Liquidity Clusters
Large resting bid or ask walls frequently act as magnets or temporary inflection zones. Always confirm real-time 24h volume before interpreting breakout strength on mid-cap altcoins.
            """.trimIndent()
        ),
        LessonItem(
            id = "lesson_crypto_halving_cycles",
            title = "BTC Halving Cycles & Altcoin Rotation",
            level = "Intermediate",
            category = "crypto-basics",
            track = AcademyTrack.CRYPTO,
            durationMinutes = 20,
            isPremium = false,
            summary = "Follow capital rotation patterns from Bitcoin dominance into Ethereum, Layer 1s, and DeFi sectors.",
            fullContent = """
# Bitcoin Halving Cycles & Capital Rotation

Historically, cryptocurrency liquidity tends to flow in distinct rotational phases across market cycles.

### The Classic Liquidity Cascade
1. **Bitcoin Accumulation**: BTC absorbs primary institutional and ETF inflows; BTC Dominance (BTC.D) rises.
2. **Large-Cap Rotation**: Once BTC stabilizes at higher ranges, profits rotate into ETH, SOL, and major Layer-1 ecosystems.
3. **High-Beta & DeFi Expansion**: Mid-cap infrastructure, DeFi, and narrative tokens see heightened volatility.

### Tracking BTC Dominance
Rising BTC.D alongside falling altcoin/BTC pairs signals defensive positioning. Falling BTC.D with stable BTC price favors altcoin outperformance.
            """.trimIndent()
        ),
        LessonItem(
            id = "lesson_crypto_defi_onchain",
            title = "DeFi Pools, TVL & On-Chain Analytics",
            level = "Advanced",
            category = "defi",
            track = AcademyTrack.CRYPTO,
            durationMinutes = 24,
            isPremium = true,
            summary = "Evaluate Decentralized Finance protocols using Total Value Locked (TVL), DEX volume, and whale flows.",
            fullContent = """
# DeFi Liquidity Pools & On-Chain Analytics

Because public blockchains are transparent ledgers, traders can monitor capital movements in real time.

### Key On-Chain Indicators
• **Exchange Netflows**: Large BTC/ETH inflows into centralized exchanges can precede selling pressure; outflows toward cold wallets suggest long-term holding.
• **Stablecoin Minting**: Rising USDT/USDC circulating supply expands dry-powder buying liquidity.
• **Total Value Locked (TVL)**: Measures capital committed to lending and automated market maker (AMM) smart contracts.
            """.trimIndent()
        ),
        LessonItem(
            id = "lesson_crypto_wallet_security",
            title = "Wallet Security, Self-Custody & Risk Hygiene",
            level = "Pro",
            category = "risk-management",
            track = AcademyTrack.CRYPTO,
            durationMinutes = 22,
            isPremium = true,
            summary = "Protect digital assets with hardware cold storage, seed-phrase safety, and smart contract permission hygiene.",
            fullContent = """
# Wallet Security & Risk Hygiene

In cryptocurrency markets, operational security is just as vital as chart analysis.

### Best Practices for Digital Asset Protection
1. **Hot vs Cold Storage**: Keep active study/trading balances minimal while securing long-term holdings in offline hardware wallets.
2. **Seed Phrase Hygiene**: Never type recovery phrases into websites, cloud notes, or screenshots. Store backups physically offline.
3. **Signature & Approval Audits**: Verify every smart-contract interaction carefully and revoke unused token spend allowances periodically.
4. **Position Volatility Caps**: Crypto ATR swings routinely exceed Forex by 3x–5x—scale position size proportionally smaller.
            """.trimIndent()
        )
    )

    private val _lessonsFlow = MutableStateFlow<List<LessonItem>>(allLessons)
    val lessonsFlow: StateFlow<List<LessonItem>> = _lessonsFlow.asStateFlow()

    fun getLessons(levelFilter: String = "ALL", trackFilter: AcademyTrack? = null): List<LessonItem> {
        val byTrack = if (trackFilter != null) {
            allLessons.filter { it.track == trackFilter }
        } else {
            allLessons
        }
        val filtered = when (levelFilter.uppercase()) {
            "BEGINNER" -> byTrack.filter { it.level.equals("Beginner", ignoreCase = true) }
            "INTERMEDIATE" -> byTrack.filter { it.level.equals("Intermediate", ignoreCase = true) }
            "ADVANCED" -> byTrack.filter { it.level.equals("Advanced", ignoreCase = true) }
            "PRO" -> byTrack.filter { it.level.equals("Pro", ignoreCase = true) }
            else -> byTrack
        }
        return filtered.map { lesson ->
            lesson.copy(isCompleted = completedLessonIds.contains(lesson.id))
        }
    }

    fun toggleLessonCompletion(lessonId: String): Boolean {
        val added = if (completedLessonIds.contains(lessonId)) {
            completedLessonIds.remove(lessonId)
            false
        } else {
            completedLessonIds.add(lessonId)
            true
        }
        _lessonsFlow.value = allLessons.map { lesson ->
            lesson.copy(isCompleted = completedLessonIds.contains(lesson.id))
        }
        return added
    }

    fun getProgress(trackFilter: AcademyTrack? = null): Pair<Int, Int> {
        val scoped = if (trackFilter != null) {
            allLessons.filter { it.track == trackFilter }
        } else {
            allLessons
        }
        val completed = scoped.count { completedLessonIds.contains(it.id) }
        return Pair(completed, scoped.size)
    }
}
