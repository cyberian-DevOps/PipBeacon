package com.pipbeacon.app.data.news

enum class NewsCategory(val label: String) {
    ALL("All News"),
    FOREX("Forex"),
    CRYPTO("Crypto"),
    CENTRAL_BANKS("Central Banks"),
    COMMODITIES("Gold & Oil")
}

enum class NewsImpact(val label: String) {
    HIGH("High Impact"),
    MEDIUM("Medium Impact"),
    LOW("Low Impact")
}

enum class MarketSentiment(val label: String) {
    BULLISH("Bullish"),
    BEARISH("Bearish"),
    NEUTRAL("Neutral")
}

data class NewsItem(
    val id: String,
    val title: String,
    val summary: String,
    val content: String,
    val source: String,
    val timestamp: Long,
    val category: NewsCategory,
    val impact: NewsImpact,
    val sentiment: MarketSentiment,
    val affectedSymbols: List<String>,
    val readTimeMinutes: Int = 2
)
