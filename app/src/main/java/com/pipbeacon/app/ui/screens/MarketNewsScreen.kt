package com.pipbeacon.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CandlestickChart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pipbeacon.app.data.news.MarketSentiment
import com.pipbeacon.app.data.news.NewsCategory
import com.pipbeacon.app.data.news.NewsImpact
import com.pipbeacon.app.data.news.NewsItem
import com.pipbeacon.app.ui.components.ShowcaseBackground
import com.pipbeacon.app.ui.components.ShowcaseGoldButton
import com.pipbeacon.app.ui.theme.CyanAccent
import com.pipbeacon.app.ui.theme.GoldAccent
import com.pipbeacon.app.ui.theme.GoldAccentBright
import com.pipbeacon.app.ui.theme.IndicatorBuy
import com.pipbeacon.app.ui.theme.IndicatorBuyBackground
import com.pipbeacon.app.ui.theme.IndicatorNeutral
import com.pipbeacon.app.ui.theme.IndicatorNeutralBackground
import com.pipbeacon.app.ui.theme.IndicatorSell
import com.pipbeacon.app.ui.theme.IndicatorSellBackground
import com.pipbeacon.app.ui.theme.JetBrainsMonoFont
import com.pipbeacon.app.ui.theme.NavyBorder
import com.pipbeacon.app.ui.theme.NavyCard
import com.pipbeacon.app.ui.theme.NavySurface
import com.pipbeacon.app.ui.theme.NavySurfaceVariant
import com.pipbeacon.app.ui.theme.TextMuted
import com.pipbeacon.app.ui.theme.TextPrimary
import com.pipbeacon.app.ui.theme.TextSecondary
import com.pipbeacon.app.viewmodel.NewsUiState
import com.pipbeacon.app.viewmodel.NewsViewModel

private data class EconomicEventSample(
    val id: String,
    val timeUtc: String,
    val currency: String,
    val title: String,
    val impact: NewsImpact,
    val forecast: String,
    val previous: String
)

private val demoEconomicEvents = listOf(
    EconomicEventSample("ev1", "14:30", "USD", "Core Durable Goods Orders m/m", NewsImpact.HIGH, "0.4%", "0.2%"),
    EconomicEventSample("ev2", "16:00", "USD", "New Home Sales", NewsImpact.MEDIUM, "680K", "662K"),
    EconomicEventSample("ev3", "19:00", "USD", "Fed Chair Powell Speech", NewsImpact.HIGH, "Policy Remarks", "—"),
    EconomicEventSample("ev4", "08:00", "EUR", "German Flash Manufacturing PMI", NewsImpact.HIGH, "46.5", "45.8"),
    EconomicEventSample("ev5", "12:00", "GBP", "BoE Monetary Policy Report", NewsImpact.MEDIUM, "Hold 4.75%", "4.75%"),
    EconomicEventSample("ev6", "23:50", "JPY", "Tokyo Core CPI y/y", NewsImpact.MEDIUM, "2.1%", "2.0%")
)

@Composable
fun MarketNewsScreen(
    viewModel: NewsViewModel,
    onNavigateToAnalysis: (String) -> Unit,
    initialTab: String = "NEWS",
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val newsList by viewModel.newsList.collectAsStateWithLifecycle()
    MarketNewsScreen(
        uiState = uiState,
        newsList = newsList,
        onSearchChanged = { viewModel.onSearchQueryChanged(it) },
        onCategorySelected = { viewModel.onCategorySelected(it) },
        onImpactToggled = { viewModel.onImpactFilterToggled(it) },
        onArticleSelected = { viewModel.selectArticle(it) },
        onRefresh = { viewModel.refresh() },
        onNavigateToAnalysis = onNavigateToAnalysis,
        initialTab = initialTab,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketNewsScreen(
    uiState: NewsUiState,
    newsList: List<NewsItem>,
    onSearchChanged: (String) -> Unit,
    onCategorySelected: (NewsCategory) -> Unit,
    onImpactToggled: (NewsImpact?) -> Unit,
    onArticleSelected: (NewsItem?) -> Unit,
    onRefresh: () -> Unit,
    onNavigateToAnalysis: (String) -> Unit,
    initialTab: String = "NEWS",
    modifier: Modifier = Modifier
) {
    var activeSection by remember(initialTab) { mutableStateOf(initialTab) } // "NEWS" or "CALENDAR"
    var isSearchVisible by remember { mutableStateOf(false) }

    ShowcaseBackground(modifier = modifier.testTag("market_news_screen")) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Showcase Header
            Surface(
                color = NavySurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (activeSection == "CALENDAR") "Economic Calendar" else "Market News",
                                color = TextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (activeSection == "CALENDAR") {
                                    "Scheduled Macro Events (Educational Demo)"
                                } else {
                                    "Forex, Crypto & Central Bank Catalysts"
                                },
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (activeSection == "NEWS") {
                                IconButton(
                                    onClick = { isSearchVisible = !isSearchVisible },
                                    modifier = Modifier.testTag("toggle_news_search")
                                ) {
                                    Icon(
                                        imageVector = if (isSearchVisible) Icons.Default.Close else Icons.Default.Search,
                                        contentDescription = "Search News",
                                        tint = if (isSearchVisible) GoldAccentBright else TextSecondary
                                    )
                                }
                            }

                            IconButton(
                                onClick = onRefresh,
                                enabled = !uiState.isRefreshing,
                                modifier = Modifier.testTag("refresh_news_button")
                            ) {
                                if (uiState.isRefreshing) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = GoldAccentBright
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Refresh",
                                        tint = CyanAccent
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Showcase Top Segmented Pill Switch: Market News vs Economic Calendar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(NavyCard)
                            .border(1.dp, NavyBorder, RoundedCornerShape(24.dp))
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (activeSection == "NEWS") GoldAccentBright else Color.Transparent)
                                .clickable { activeSection = "NEWS" }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Newspaper,
                                    contentDescription = null,
                                    tint = if (activeSection == "NEWS") Color(0xFF060D1A) else TextSecondary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Market News",
                                    color = if (activeSection == "NEWS") Color(0xFF060D1A) else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (activeSection == "CALENDAR") GoldAccentBright else Color.Transparent)
                                .clickable { activeSection = "CALENDAR" }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = if (activeSection == "CALENDAR") Color(0xFF060D1A) else TextSecondary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Calendar",
                                    color = if (activeSection == "CALENDAR") Color(0xFF060D1A) else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    AnimatedVisibility(visible = isSearchVisible && activeSection == "NEWS") {
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = onSearchChanged,
                            placeholder = {
                                Text(
                                    "Search news (e.g. Fed, BTC, CPI)...",
                                    color = TextMuted,
                                    fontSize = 13.sp
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                                .testTag("news_search_input"),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldAccentBright,
                                unfocusedBorderColor = NavyBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                cursorColor = GoldAccentBright,
                                focusedContainerColor = NavyCard,
                                unfocusedContainerColor = NavyCard
                            )
                        )
                    }
                }
            }

            if (activeSection == "CALENDAR") {
                EconomicCalendarSection()
            } else {
                // Category Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NewsCategory.entries.forEach { cat ->
                        val isSelected = uiState.selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) GoldAccentBright else NavyCard)
                                .border(
                                    1.dp,
                                    if (isSelected) GoldAccentBright else NavyBorder,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { onCategorySelected(cat) }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                                .testTag("news_cat_${cat.name.lowercase()}")
                        ) {
                            Text(
                                text = cat.label,
                                color = if (isSelected) Color(0xFF060D1A) else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                // Impact Filter Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Impact:", color = TextMuted, fontSize = 11.sp)

                    val isHighSelected = uiState.selectedImpact == NewsImpact.HIGH
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isHighSelected) IndicatorSellBackground else Color.Transparent)
                            .border(
                                1.dp,
                                if (isHighSelected) IndicatorSell else NavyBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { onImpactToggled(NewsImpact.HIGH) }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "High Impact",
                            color = if (isHighSelected) IndicatorSell else TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    val isMedSelected = uiState.selectedImpact == NewsImpact.MEDIUM
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isMedSelected) IndicatorNeutralBackground else Color.Transparent)
                            .border(
                                1.dp,
                                if (isMedSelected) GoldAccent else NavyBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { onImpactToggled(NewsImpact.MEDIUM) }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Medium Impact",
                            color = if (isMedSelected) GoldAccentBright else TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                if (newsList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Newspaper,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No news articles found for current filter",
                                color = TextSecondary,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            ShowcaseGoldButton(
                                text = "Reset & Refresh",
                                onClick = onRefresh,
                                height = 46.dp
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(newsList, key = { it.id }) { news ->
                            NewsRowItem(
                                news = news,
                                onClick = { onArticleSelected(news) }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(30.dp))
                        }
                    }
                }
            }
        }
    }

    uiState.selectedArticle?.let { article ->
        ArticleReaderSheet(
            article = article,
            onDismiss = { onArticleSelected(null) },
            onNavigateToAnalysis = { symbol ->
                onArticleSelected(null)
                onNavigateToAnalysis(symbol)
            }
        )
    }
}

@Composable
private fun EconomicCalendarSection() {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxSize()
            .testTag("economic_calendar_section")
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Macro Schedule",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(IndicatorNeutralBackground)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "Demo Schedule",
                        color = IndicatorNeutral,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        items(demoEconomicEvents, key = { it.id }) { ev ->
            val impactColor = if (ev.impact == NewsImpact.HIGH) IndicatorSell else GoldAccentBright
            val impactBg = if (ev.impact == NewsImpact.HIGH) IndicatorSellBackground else IndicatorNeutralBackground
            val impactText = if (ev.impact == NewsImpact.HIGH) "High" else "Medium"

            Surface(
                color = NavyCard,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, NavyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.width(64.dp)) {
                        Text(
                            text = ev.timeUtc,
                            color = TextPrimary,
                            fontFamily = JetBrainsMonoFont,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = ev.currency,
                            color = CyanAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                        Text(
                            text = ev.title,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Fcst: ${ev.forecast}  •  Prev: ${ev.previous}",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontFamily = JetBrainsMonoFont
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(impactBg)
                            .border(1.dp, impactColor.copy(alpha = 0.45f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = impactText,
                            color = impactColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NewsRowItem(
    news: NewsItem,
    onClick: () -> Unit
) {
    val thumbIcon = when (news.category) {
        NewsCategory.CRYPTO -> Icons.Default.CurrencyBitcoin
        NewsCategory.FOREX -> Icons.Default.CurrencyExchange
        else -> Icons.Default.Public
    }
    val thumbTint = if (news.category == NewsCategory.CRYPTO) GoldAccentBright else CyanAccent

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("news_item_${news.id}"),
        color = NavyCard,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, NavyBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Showcase thumbnail badge on left
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(thumbTint.copy(alpha = 0.14f))
                    .border(1.dp, thumbTint.copy(alpha = 0.32f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = thumbIcon,
                    contentDescription = null,
                    tint = thumbTint,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        val impactColor = when (news.impact) {
                            NewsImpact.HIGH -> IndicatorSell
                            NewsImpact.MEDIUM -> GoldAccentBright
                            NewsImpact.LOW -> TextMuted
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(impactColor.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = news.impact.label,
                                color = impactColor,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        val sentimentColor = when (news.sentiment) {
                            MarketSentiment.BULLISH -> IndicatorBuy
                            MarketSentiment.BEARISH -> IndicatorSell
                            MarketSentiment.NEUTRAL -> TextSecondary
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(sentimentColor.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (news.sentiment == MarketSentiment.BULLISH) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                                    contentDescription = null,
                                    tint = sentimentColor,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = news.sentiment.label,
                                    color = sentimentColor,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    val timeMinutes = ((System.currentTimeMillis() - news.timestamp) / 60_000L).coerceAtLeast(1)
                    val timeAgoStr = if (timeMinutes < 60) "${timeMinutes}m ago" else "${timeMinutes / 60}h ago"
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = TextMuted, modifier = Modifier.size(11.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(text = timeAgoStr, color = TextMuted, fontSize = 10.sp)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = news.title,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 19.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = news.summary,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        news.affectedSymbols.take(3).forEach { sym ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(NavySurfaceVariant)
                                    .border(1.dp, NavyBorder, RoundedCornerShape(5.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = sym,
                                    color = GoldAccentBright,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Text(
                        text = "${news.source} • ${news.readTimeMinutes}m",
                        color = TextMuted,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleReaderSheet(
    article: NewsItem,
    onDismiss: () -> Unit,
    onNavigateToAnalysis: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = NavySurface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .size(width = 36.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(NavyBorder)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = article.source,
                    color = GoldAccentBright,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${article.readTimeMinutes} min read",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = article.title,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val impactColor = when (article.impact) {
                    NewsImpact.HIGH -> IndicatorSell
                    NewsImpact.MEDIUM -> GoldAccentBright
                    NewsImpact.LOW -> TextMuted
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(impactColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = article.impact.label,
                        color = impactColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                val sentimentColor = when (article.sentiment) {
                    MarketSentiment.BULLISH -> IndicatorBuy
                    MarketSentiment.BEARISH -> IndicatorSell
                    MarketSentiment.NEUTRAL -> TextSecondary
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(sentimentColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "Bias: ${article.sentiment.label}",
                        color = sentimentColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = article.content,
                color = TextSecondary,
                fontSize = 14.sp,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Key Affected Trading Pairs",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                article.affectedSymbols.forEach { sym ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(NavyCard)
                            .border(1.dp, CyanAccent.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                            .clickable { onNavigateToAnalysis(sym) }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CandlestickChart,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Chart $sym",
                                color = CyanAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
