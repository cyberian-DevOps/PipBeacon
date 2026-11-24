package com.pipbeacon.app.data.news

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NewsRepository {

    private val now = System.currentTimeMillis()

    private val initialNews = listOf(
        NewsItem(
            id = "news_01",
            title = "Federal Reserve Holds Benchmark Rate Steady, Signals Data-Dependent Policy Path",
            summary = "FOMC officials emphasize sustained progress on inflation before considering rate reductions, keeping US Treasury yields elevated.",
            content = "The Federal Open Market Committee concluded its policy meeting maintaining the federal funds rate within the target range. Chair Powell reiterated that while disinflation continues across core services and goods, officials require further confidence that inflation is moving sustainably toward the 2 percent target. The US Dollar Index (DXY) saw upward momentum following the announcement, exerting downward pressure on EUR/USD and GBP/USD.",
            source = "Macro Intelligence Wire",
            timestamp = now - (18 * 60_000L),
            category = NewsCategory.CENTRAL_BANKS,
            impact = NewsImpact.HIGH,
            sentiment = MarketSentiment.BEARISH,
            affectedSymbols = listOf("EUR/USD", "GBP/USD", "USD/JPY"),
            readTimeMinutes = 3
        ),
        NewsItem(
            id = "news_02",
            title = "Bitcoin Consolidates Above $77,000 as Institutional Spot ETF Inflows Reach Weekly Highs",
            summary = "Spot crypto ETFs record net inflows surpassing $420M, absorbing exchange sell pressure and reinforcing long-term floor levels.",
            content = "Institutional demand continues to anchor Bitcoin and Ethereum markets as weekly spot ETF inflows reached positive records. On-chain analytics indicate that long-term holder supply remains constrained, while futures funding rates stabilized into neutral territory. Analysts note that key support levels around $75,500 have held through multiple retests, paving the way for continuation toward psychological targets.",
            source = "Crypto Markets Daily",
            timestamp = now - (45 * 60_000L),
            category = NewsCategory.CRYPTO,
            impact = NewsImpact.HIGH,
            sentiment = MarketSentiment.BULLISH,
            affectedSymbols = listOf("BTC/USDT", "ETH/USDT", "SOL/USDT"),
            readTimeMinutes = 2
        ),
        NewsItem(
            id = "news_03",
            title = "Gold (XAU/USD) Rebounds Toward Key Resistance on Safe-Haven Demand and Central Bank Buying",
            summary = "Spot bullion prices edge higher as geopolitical caution and global sovereign reserve accumulation offset higher dollar yields.",
            content = "Spot Gold (XAU/USD) witnessed renewed bids during London trading hours, testing the $2,700-$2,740 zone. Central banks across emerging markets reported continued sovereign bullion additions. Technical analysts observe strong Order Block defense at the daily 50 EMA, indicating buyers remain eager on shallow liquidity dips.",
            source = "Global Bullion Report",
            timestamp = now - (90 * 60_000L),
            category = NewsCategory.COMMODITIES,
            impact = NewsImpact.HIGH,
            sentiment = MarketSentiment.BULLISH,
            affectedSymbols = listOf("XAU/USD", "USD/CHF"),
            readTimeMinutes = 3
        ),
        NewsItem(
            id = "news_04",
            title = "European Central Bank Signals Cautious Stance Amid Slowing Eurozone Manufacturing PMI",
            summary = "Lagarde highlights divergence between sluggish manufacturing output and resilient wage growth across the eurozone.",
            content = "Speaking at the Frankfurt symposium, ECB policymakers noted that while headline inflation has cooled toward the 2.2% threshold, industrial activity in Germany and France remains restrained. The euro traded within a tight 40-pip range against the US Dollar and British Pound as traders evaluate the probability of an additional 25bps rate cut at the upcoming quarterly meeting.",
            source = "Eurozone Economics",
            timestamp = now - (150 * 60_000L),
            category = NewsCategory.FOREX,
            impact = NewsImpact.MEDIUM,
            sentiment = MarketSentiment.NEUTRAL,
            affectedSymbols = listOf("EUR/USD", "EUR/GBP", "EUR/JPY"),
            readTimeMinutes = 2
        ),
        NewsItem(
            id = "news_05",
            title = "Solana Ecosystem DeFi TVL Surges Following Major Protocol Scalability Upgrade",
            summary = "Network transactions per second climb with lower fees, boosting liquidity across decentralized exchanges and automated market makers.",
            content = "Solana (SOL) demonstrated relative strength against major cryptocurrencies, testing key supply zones near $100. Total Value Locked (TVL) on Solana protocols grew by 8.4% over seven days, driven by decentralized perpetual exchange trading volume and staking integrations.",
            source = "DeFi Network Insider",
            timestamp = now - (220 * 60_000L),
            category = NewsCategory.CRYPTO,
            impact = NewsImpact.MEDIUM,
            sentiment = MarketSentiment.BULLISH,
            affectedSymbols = listOf("SOL/USDT", "JUP/USDT", "RAY/USDT"),
            readTimeMinutes = 2
        ),
        NewsItem(
            id = "news_06",
            title = "Bank of Japan (BOJ) Holds Rates Steady, Warns of Currency Volatility Impact on Import Costs",
            summary = "Governor Ueda reaffirms that rate trajectory will align with sustained wage hikes, while keeping vigilant on Yen exchange moves.",
            content = "The Bank of Japan maintained its policy balance rate, causing USD/JPY to fluctuate between 156.80 and 157.60. Japanese finance officials stressed that disorderly foreign exchange swings negatively influence household purchasing power, maintaining the possibility of currency market intervention if speculative volatility persists.",
            source = "Tokyo Financial Press",
            timestamp = now - (310 * 60_000L),
            category = NewsCategory.CENTRAL_BANKS,
            impact = NewsImpact.HIGH,
            sentiment = MarketSentiment.BEARISH,
            affectedSymbols = listOf("USD/JPY", "EUR/JPY", "GBP/JPY"),
            readTimeMinutes = 3
        ),
        NewsItem(
            id = "news_07",
            title = "UK Services PMI Outperforms Expectations, Giving Support to British Pound (GBP/USD)",
            summary = "Stronger services sector expansion strengthens the Pound against G10 peers, reducing expectations for near-term BoE easing.",
            content = "The UK Services Purchasing Managers Index printed at 53.4, surpassing consensus forecasts and underscoring resilience in domestic consumer demand. Sterling gained 35 pips against the dollar, lifting GBP/USD above 1.3350, while GBP/JPY tested multi-week highs.",
            source = "London FX Herald",
            timestamp = now - (420 * 60_000L),
            category = NewsCategory.FOREX,
            impact = NewsImpact.MEDIUM,
            sentiment = MarketSentiment.BULLISH,
            affectedSymbols = listOf("GBP/USD", "EUR/GBP", "GBP/JPY"),
            readTimeMinutes = 2
        ),
        NewsItem(
            id = "news_08",
            title = "XRP Advances on Cross-Border Settlement Liquidity Announcements and Regulatory Clarity",
            summary = "Enterprise payment rails expand multi-currency corridors, driving on-chain volume and derivative open interest higher.",
            content = "XRP recorded a 4.2% daily gain following institutional announcements regarding cross-border liquidity integrations for Asian banking corridors. Open interest in XRP derivatives increased by $85 million, indicating speculative interest returning alongside broader altcoin liquidity rotation.",
            source = "Digital Assets Gazette",
            timestamp = now - (580 * 60_000L),
            category = NewsCategory.CRYPTO,
            impact = NewsImpact.MEDIUM,
            sentiment = MarketSentiment.BULLISH,
            affectedSymbols = listOf("XRP/USDT", "LTC/USDT"),
            readTimeMinutes = 2
        )
    )

    private val _newsFlow = MutableStateFlow<List<NewsItem>>(initialNews)
    val newsFlow: Flow<List<NewsItem>> = _newsFlow.asStateFlow()

    fun getAllNews(): List<NewsItem> = _newsFlow.value

    fun getNewsByCategory(category: NewsCategory): List<NewsItem> {
        return if (category == NewsCategory.ALL) {
            _newsFlow.value
        } else {
            _newsFlow.value.filter { it.category == category }
        }
    }
}
