package com.pipbeacon.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CandlestickChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pipbeacon.app.core.DataResult
import com.pipbeacon.app.core.MarketType
import com.pipbeacon.app.data.MarketItem
import com.pipbeacon.app.ui.components.ShowcaseBackground
import com.pipbeacon.app.ui.components.ShowcaseGoldButton
import com.pipbeacon.app.ui.components.ShowcaseSparkline
import com.pipbeacon.app.ui.theme.CyanAccent
import com.pipbeacon.app.ui.theme.GoldAccent
import com.pipbeacon.app.ui.theme.GoldAccentBright
import com.pipbeacon.app.ui.theme.IndicatorBuy
import com.pipbeacon.app.ui.theme.IndicatorBuyBackground
import com.pipbeacon.app.ui.theme.IndicatorSell
import com.pipbeacon.app.ui.theme.IndicatorSellBackground
import com.pipbeacon.app.ui.theme.JetBrainsMonoFont
import com.pipbeacon.app.ui.theme.NavyBorder
import com.pipbeacon.app.ui.theme.NavyCard
import com.pipbeacon.app.ui.theme.NavyCardElevated
import com.pipbeacon.app.ui.theme.NavySurface
import com.pipbeacon.app.ui.theme.NavySurfaceVariant
import com.pipbeacon.app.ui.theme.TextMuted
import com.pipbeacon.app.ui.theme.TextPrimary
import com.pipbeacon.app.ui.theme.TextSecondary
import com.pipbeacon.app.viewmodel.LiveRateTab
import com.pipbeacon.app.viewmodel.MarketUiState
import com.pipbeacon.app.viewmodel.RateSortOption
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveRatesScreen(
    uiState: MarketUiState,
    liveRates: List<MarketItem>,
    providerStatus: DataResult<Unit>,
    lastRefreshTime: Long?,
    onSearchChanged: (String) -> Unit,
    onTabSelected: (LiveRateTab) -> Unit,
    onSortSelected: (RateSortOption) -> Unit,
    onToggleAutoRefresh: () -> Unit,
    onRefresh: () -> Unit,
    onNavigateToAnalysis: (String) -> Unit,
    onNavigateToCalculator: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedItemForDetail by remember { mutableStateOf<MarketItem?>(null) }
    var isSearchVisible by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    ShowcaseBackground(modifier = modifier.testTag("live_rates_screen")) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Showcase Markets Header
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Markets",
                                    color = TextPrimary,
                                    fontSize = 21.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(IndicatorBuy.copy(alpha = pulseAlpha))
                                )
                            }
                            Text(
                                text = "Live & Demonstration Forex & Crypto Quotes",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { isSearchVisible = !isSearchVisible },
                                modifier = Modifier.testTag("toggle_rates_search")
                            ) {
                                Icon(
                                    imageVector = if (isSearchVisible) Icons.Default.Close else Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = if (isSearchVisible) GoldAccentBright else TextSecondary
                                )
                            }

                            IconButton(
                                onClick = onRefresh,
                                enabled = !uiState.isRefreshing,
                                modifier = Modifier.testTag("refresh_live_rates_button")
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

                    AnimatedVisibility(visible = isSearchVisible) {
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = onSearchChanged,
                            placeholder = {
                                Text(
                                    "Search by symbol (e.g. BTC, EUR, SOL)...",
                                    color = TextMuted,
                                    fontSize = 13.sp
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                                .testTag("rates_search_input"),
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

                    Spacer(modifier = Modifier.height(10.dp))

                    // Showcase Segmented Pill Switcher: All | Forex | Crypto
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(NavyCard)
                            .border(1.dp, NavyBorder, RoundedCornerShape(24.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val primaryTabs = listOf(
                            LiveRateTab.ALL to "All Pairs",
                            LiveRateTab.FOREX to "Forex",
                            LiveRateTab.CRYPTO to "Crypto"
                        )
                        primaryTabs.forEach { (tab, label) ->
                            val isSelected = uiState.liveRateTab == tab ||
                                (tab == LiveRateTab.FOREX && uiState.liveRateTab in listOf(
                                    LiveRateTab.MAJORS,
                                    LiveRateTab.MINORS,
                                    LiveRateTab.METALS,
                                    LiveRateTab.EXOTICS
                                )) ||
                                (tab == LiveRateTab.CRYPTO && uiState.liveRateTab in listOf(
                                    LiveRateTab.LAYER1,
                                    LiveRateTab.DEFI,
                                    LiveRateTab.MEMES
                                ))

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isSelected) GoldAccentBright else Color.Transparent)
                                    .clickable { onTabSelected(tab) }
                                    .padding(vertical = 8.dp)
                                    .testTag("tab_${tab.name.lowercase()}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) Color(0xFF060D1A) else TextSecondary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Provider status & auto refresh bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = IndicatorBuy,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            val formattedTime = lastRefreshTime?.let {
                                SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(it))
                            } ?: "Offline Ready"
                            val feedLabel = if (providerStatus is DataResult.Success) {
                                "Live & Demo Feeds • $formattedTime"
                            } else {
                                "Demonstration Market Data (Offline Ready)"
                            }
                            Text(
                                text = feedLabel,
                                color = TextMuted,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (uiState.isAutoRefreshActive) GoldAccent.copy(alpha = 0.16f)
                                    else NavyCard
                                )
                                .border(
                                    1.dp,
                                    if (uiState.isAutoRefreshActive) GoldAccent.copy(alpha = 0.5f) else NavyBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { onToggleAutoRefresh() }
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (uiState.isAutoRefreshActive) "Auto: 15s ON" else "Auto: PAUSED",
                                color = if (uiState.isAutoRefreshActive) GoldAccentBright else TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Secondary Category Filters Row (preserving all LiveRateTab test tags)
            val secondaryTabs = LiveRateTab.entries.filter {
                it !in listOf(LiveRateTab.ALL, LiveRateTab.FOREX, LiveRateTab.CRYPTO)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                secondaryTabs.forEach { tab ->
                    val isSelected = uiState.liveRateTab == tab
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) CyanAccent else NavyCard)
                            .border(
                                1.dp,
                                if (isSelected) CyanAccent else NavyBorder,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { onTabSelected(tab) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("tab_${tab.name.lowercase()}")
                    ) {
                        Text(
                            text = tab.label,
                            color = if (isSelected) Color(0xFF060D1A) else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            // Sorting Chips Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(14.dp)
                )
                RateSortOption.entries.forEach { sort ->
                    val isSelected = uiState.sortOption == sort
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) CyanAccent.copy(alpha = 0.18f) else Color.Transparent)
                            .border(
                                1.dp,
                                if (isSelected) CyanAccent else NavyBorder.copy(alpha = 0.6f),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { onSortSelected(sort) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = sort.label,
                            color = if (isSelected) CyanAccent else TextMuted,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (liveRates.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (uiState.searchQuery.isNotBlank()) "No pairs match '${uiState.searchQuery}'"
                            else "Fetching market rates...",
                            color = TextSecondary,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        ShowcaseGoldButton(
                            text = "Refresh Feed",
                            onClick = onRefresh,
                            height = 46.dp
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        RatesOverviewBanner(liveRates = liveRates)
                    }

                    items(liveRates, key = { it.id }) { item ->
                        LiveRateRow(
                            item = item,
                            onClick = { onNavigateToAnalysis(item.symbol) },
                            onInfoClick = { selectedItemForDetail = item }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }

    selectedItemForDetail?.let { item ->
        RateDetailSheet(
            item = item,
            onDismiss = { selectedItemForDetail = null },
            onNavigateToAnalysis = {
                selectedItemForDetail = null
                onNavigateToAnalysis(item.symbol)
            },
            onNavigateToCalculator = {
                selectedItemForDetail = null
                onNavigateToCalculator(item.symbol)
            }
        )
    }
}

@Composable
fun LiveRateRow(
    item: MarketItem,
    onClick: () -> Unit,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPositive = (item.changePercent24h ?: 0.0) >= 0.0
    val changeColor = if (isPositive) IndicatorBuy else IndicatorSell
    val changeSign = if (isPositive) "+" else ""
    val isCrypto = item.marketType == MarketType.CRYPTO
    val badgeTint = if (isCrypto) GoldAccentBright else CyanAccent

    Surface(
        color = NavyCard,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, NavyBorder),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("rate_row_${item.symbol.replace('/', '_')}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 11.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Circular Currency/Crypto Badge + Symbol + Price
            Row(
                modifier = Modifier.weight(1.35f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(badgeTint.copy(alpha = 0.15f))
                        .border(1.dp, badgeTint.copy(alpha = 0.4f), CircleShape)
                        .clickable { onInfoClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isCrypto) Icons.Default.CurrencyBitcoin else Icons.Default.CurrencyExchange,
                        contentDescription = "Inspect ${item.symbol}",
                        tint = badgeTint,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = item.symbol,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = formatPrice(item.currentPrice, item.symbol),
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontFamily = JetBrainsMonoFont,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        val spreadStr = item.pipSpread?.let {
                            if (item.marketType == MarketType.FOREX) "${String.format(Locale.US, "%.1f", it)}p" else "0.02%"
                        } ?: "0.8p"
                        Text(
                            text = spreadStr,
                            color = GoldAccentBright,
                            fontSize = 9.sp,
                            fontFamily = JetBrainsMonoFont,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Center: Showcase Sparkline Trend Graph
            ShowcaseSparkline(
                points = item.sparkline,
                isPositive = isPositive,
                modifier = Modifier
                    .weight(0.9f)
                    .height(28.dp)
                    .padding(horizontal = 6.dp)
            )

            // Right: Percentage Pill + Graph Action
            Row(
                modifier = Modifier.weight(1.0f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isPositive) IndicatorBuyBackground else IndicatorSellBackground)
                        .border(1.dp, changeColor.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 7.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$changeSign${String.format(Locale.US, "%.2f", item.changePercent24h ?: 0.0)}%",
                        color = changeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = JetBrainsMonoFont
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(
                    onClick = onInfoClick,
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Details",
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RatesOverviewBanner(liveRates: List<MarketItem>) {
    val greenCount = liveRates.count { (it.changePercent24h ?: 0.0) >= 0.0 }
    val redCount = liveRates.size - greenCount

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(NavyCard)
            .border(1.dp, NavyBorder, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Tracked Pairs: ${liveRates.size}",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Educational Demonstration & Open Feeds",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(IndicatorBuy)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$greenCount Up",
                        color = IndicatorBuy,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(IndicatorSell)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$redCount Down",
                        color = IndicatorSell,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun LiveRateCard(
    item: MarketItem,
    onClick: () -> Unit,
    onNavigateToAnalysis: () -> Unit,
    onNavigateToCalculator: () -> Unit
) {
    val isPositive = (item.changePercent24h ?: 0.0) >= 0.0
    val changeColor = if (isPositive) IndicatorBuy else IndicatorSell
    val changeSign = if (isPositive) "+" else ""

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("rate_card_${item.symbol.replace('/', '_')}"),
        color = NavyCard,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, NavyBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (item.marketType == MarketType.CRYPTO) GoldAccent.copy(alpha = 0.15f)
                                else CyanAccent.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (item.marketType == MarketType.CRYPTO) Icons.Default.CurrencyBitcoin
                            else Icons.Default.CurrencyExchange,
                            contentDescription = null,
                            tint = if (item.marketType == MarketType.CRYPTO) GoldAccentBright else CyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = item.symbol,
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(NavySurfaceVariant)
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = item.category,
                                    color = TextMuted,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        Text(
                            text = item.name,
                            color = TextSecondary,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    val priceStr = formatPrice(item.currentPrice, item.symbol)
                    Text(
                        text = priceStr,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = JetBrainsMonoFont
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isPositive) IndicatorBuyBackground else IndicatorSellBackground)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isPositive) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                                contentDescription = null,
                                tint = changeColor,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "$changeSign${String.format(Locale.US, "%.2f", item.changePercent24h ?: 0.0)}%",
                                color = changeColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = JetBrainsMonoFont
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(NavySurfaceVariant)
                    .padding(horizontal = 10.dp, vertical = 7.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "24H RANGE",
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "L: ${formatPrice(item.low24h, item.symbol)} — H: ${formatPrice(item.high24h, item.symbol)}",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontFamily = JetBrainsMonoFont
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (item.marketType == MarketType.FOREX) "SPREAD" else "EST. SPREAD",
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    val spreadStr = item.pipSpread?.let {
                        if (item.marketType == MarketType.FOREX) "${String.format(Locale.US, "%.1f", it)} pips" else "0.02%"
                    } ?: "0.8 pips"
                    Text(
                        text = spreadStr,
                        color = GoldAccentBright,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = JetBrainsMonoFont
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.providerName ?: if (item.marketType == MarketType.CRYPTO) "Open Crypto Feed" else "Open Rates",
                    color = TextMuted,
                    fontSize = 9.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(NavyCardElevated)
                            .border(1.dp, NavyBorder, RoundedCornerShape(8.dp))
                            .clickable { onNavigateToCalculator() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Calculate, contentDescription = null, tint = GoldAccentBright, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Calc", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyanAccent.copy(alpha = 0.14f))
                            .border(1.dp, CyanAccent.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .clickable { onNavigateToAnalysis() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CandlestickChart, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Analyze", color = CyanAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RateDetailSheet(
    item: MarketItem,
    onDismiss: () -> Unit,
    onNavigateToAnalysis: () -> Unit,
    onNavigateToCalculator: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isPositive = (item.changePercent24h ?: 0.0) >= 0.0
    val changeColor = if (isPositive) IndicatorBuy else IndicatorSell

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
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.symbol,
                            color = TextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(GoldAccent.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = item.marketType.name,
                                color = GoldAccentBright,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(text = item.name, color = TextSecondary, fontSize = 13.sp)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formatPrice(item.currentPrice, item.symbol),
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = JetBrainsMonoFont
                    )
                    Text(
                        text = "${if (isPositive) "+" else ""}${String.format(Locale.US, "%.2f", item.changePercent24h ?: 0.0)}%",
                        color = changeColor,
                        fontSize = 12.sp,
                        fontFamily = JetBrainsMonoFont,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            ShowcaseSparkline(
                points = item.sparkline,
                isPositive = isPositive,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "24-Hour Market Metrics",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(NavyCard)
                    .border(1.dp, NavyBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DetailMetricRow("24h High", formatPrice(item.high24h, item.symbol))
                DetailMetricRow("24h Low", formatPrice(item.low24h, item.symbol))
                DetailMetricRow("24h Price Change", "${if (isPositive) "+" else ""}${formatPrice(item.change24h, item.symbol)}")
                DetailMetricRow(
                    label = if (item.marketType == MarketType.FOREX) "Pip Spread" else "Estimated Spread",
                    value = item.pipSpread?.let { "${String.format(Locale.US, "%.1f", it)} pips" } ?: "0.8 pips",
                    valueColor = GoldAccentBright
                )
                DetailMetricRow(
                    "Technical Bias",
                    item.signalSummary.name,
                    valueColor = if (item.signalSummary.name.contains("BUY")) IndicatorBuy else IndicatorSell
                )
                DetailMetricRow("Feed Provider", item.providerName ?: "Demonstration / Open Feed")
                val formattedTime = item.quoteTimestamp?.let {
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(it))
                } ?: "Demo Ready"
                DetailMetricRow("Quote Timestamp", formattedTime)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onNavigateToCalculator,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyCard),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, SolidColor(GoldAccentBright))
                ) {
                    Icon(Icons.Default.Calculate, contentDescription = null, tint = GoldAccentBright, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Calculator", color = GoldAccentBright, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                ShowcaseGoldButton(
                    text = "Chart Analysis",
                    onClick = onNavigateToAnalysis,
                    height = 48.dp,
                    modifier = Modifier.weight(1.2f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun DetailMetricRow(
    label: String,
    value: String,
    valueColor: Color = TextPrimary
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextSecondary, fontSize = 12.sp)
        Text(
            text = value,
            color = valueColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = JetBrainsMonoFont
        )
    }
}

private fun formatPrice(price: Double?, symbol: String): String {
    if (price == null || price == 0.0) return "---"
    return when {
        symbol.contains("JPY") -> String.format(Locale.US, "%.3f", price)
        symbol.contains("USDT") -> when {
            price >= 1000.0 -> String.format(Locale.US, "$%,.2f", price)
            price >= 1.0 -> String.format(Locale.US, "$%.4f", price)
            else -> String.format(Locale.US, "$%.6f", price)
        }
        else -> String.format(Locale.US, "%.5f", price)
    }
}
