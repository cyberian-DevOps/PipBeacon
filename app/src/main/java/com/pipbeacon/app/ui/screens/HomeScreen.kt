package com.pipbeacon.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CandlestickChart
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pipbeacon.app.core.DataResult
import com.pipbeacon.app.core.MarketType
import com.pipbeacon.app.data.MarketItem
import com.pipbeacon.app.data.MarketSession
import com.pipbeacon.app.data.SignalItem
import com.pipbeacon.app.ui.components.PipBeaconLighthouseArt
import com.pipbeacon.app.ui.components.ShowcaseGoldButton
import com.pipbeacon.app.ui.components.ShowcaseSparkline
import com.pipbeacon.app.ui.theme.CyanAccent
import com.pipbeacon.app.ui.theme.GoldAccent
import com.pipbeacon.app.ui.theme.GoldAccentBright
import com.pipbeacon.app.ui.theme.IndicatorBuy
import com.pipbeacon.app.ui.theme.IndicatorBuyBackground
import com.pipbeacon.app.ui.theme.IndicatorNeutralBackground
import com.pipbeacon.app.ui.theme.IndicatorSell
import com.pipbeacon.app.ui.theme.JetBrainsMonoFont
import com.pipbeacon.app.ui.theme.LocalThemeIsDark
import com.pipbeacon.app.ui.theme.NavyBackground
import com.pipbeacon.app.ui.theme.NavyBackgroundBottom
import com.pipbeacon.app.ui.theme.NavyBorder
import com.pipbeacon.app.ui.theme.NavyCard
import com.pipbeacon.app.ui.theme.NavySurface
import com.pipbeacon.app.ui.theme.NavySurfaceVariant
import com.pipbeacon.app.ui.theme.PlusJakartaSansFont
import com.pipbeacon.app.ui.theme.TextMuted
import com.pipbeacon.app.ui.theme.TextPrimary
import com.pipbeacon.app.ui.theme.TextSecondary
import java.util.Locale

// =============================================================================
// 1. SHOWCASE COLOR PALETTE TOKENS (Dark & Light Mode)
// =============================================================================
val ColorScreenBackground: Color
    @Composable
    get() = NavyBackground

val ColorSurfaceCard: Color
    @Composable
    get() = NavyCard

val ColorBorderDivider: Color
    @Composable
    get() = NavyBorder

val ColorGoldAccent = Color(0xFFF5B731)
val ColorProfitGreen = Color(0xFF10B981)
val ColorLossRed = Color(0xFFEF4444)
val ColorAccentCyan = Color(0xFF38BDF8)

val ColorMutedSlate: Color
    @Composable
    get() = TextSecondary

val ColorTextWhite: Color
    @Composable
    get() = TextPrimary

data class DashboardThemeColors(
    val background: Color,
    val surface: Color,
    val border: Color,
    val textPrimary: Color,
    val textMuted: Color,
    val subSurface: Color,
    val gold: Color = Color(0xFFF5B731),
    val green: Color = Color(0xFF10B981),
    val red: Color = Color(0xFFEF4444),
    val cyan: Color = Color(0xFF38BDF8),
    val greenSurface: Color,
    val isDark: Boolean = true
)

fun getDashboardColors(isDark: Boolean): DashboardThemeColors {
    return if (isDark) {
        DashboardThemeColors(
            background = Color(0xFF060D1A),
            surface = Color(0xFF0F1C33),
            border = Color(0xFF1E3454),
            textPrimary = Color(0xFFF8FAFC),
            textMuted = Color(0xFF9FB0C7),
            subSurface = Color(0xFF091324),
            greenSurface = Color(0xFF072A22),
            isDark = true
        )
    } else {
        DashboardThemeColors(
            background = Color(0xFFF4F7FB),
            surface = Color(0xFFFFFFFF),
            border = Color(0xFFD6E0EC),
            textPrimary = Color(0xFF0F172A),
            textMuted = Color(0xFF475569),
            subSurface = Color(0xFFF1F5F9),
            greenSurface = Color(0xFFDCFCE7),
            isDark = false
        )
    }
}

// =============================================================================
// DATA MODELS
// =============================================================================
data class TechnicalSignal(
    val id: String,
    val symbol: String,
    val action: String,
    val status: String = "ACTIVE",
    val entryPrice: Double,
    val stopLoss: Double,
    val takeProfit1: Double,
    val takeProfit2: Double,
    val isLockedForFree: Boolean = true,
    val pipPotential: Int = 360,
    val timeframe: String = "H1",
    val riskReward: String = "1:2.8",
    val reasoning: String = "EMA 20/50 Golden Cross with RSI 48 oversold bounce."
)

data class MarketTickerItem(
    val symbol: String,
    val price: String,
    val changePercent: Double,
    val isForex: Boolean = true
)

// =============================================================================
// 2. MODULAR COMPOSABLES ARCHITECTURE (SHOWCASE REDESIGN)
// =============================================================================

@Composable
fun PipBeaconHeader(
    title: String = "PipBeacon",
    statusText: String = "Offline Ready • Demo Data",
    isRefreshing: Boolean = false,
    isDarkTheme: Boolean = true,
    onToggleTheme: () -> Unit = {},
    onCalculatorClick: () -> Unit = {},
    onRefreshClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    colors: DashboardThemeColors = getDashboardColors(isDarkTheme),
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_dot")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotAlpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(NavySurface)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onProfileClick,
                    modifier = Modifier.testTag("header_profile_action")
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(colors.cyan.copy(alpha = 0.12f))
                            .border(1.dp, colors.cyan.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = colors.cyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    PipBeaconLighthouseArt(
                        modifier = Modifier.size(28.dp),
                        showCandles = false
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = title,
                            color = colors.textPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = PlusJakartaSansFont
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(colors.green.copy(alpha = dotAlpha))
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = statusText,
                                color = colors.green,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onToggleTheme,
                    modifier = Modifier.testTag("header_theme_toggle_action")
                ) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = if (isDarkTheme) "Switch to Light Mode" else "Switch to Dark Mode",
                        tint = if (isDarkTheme) GoldAccentBright else Color(0xFF4F46E5),
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onCalculatorClick,
                    modifier = Modifier.testTag("header_calculator_action")
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = "Calculators",
                        tint = colors.gold,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onRefreshClick,
                    enabled = !isRefreshing,
                    modifier = Modifier.testTag("header_refresh_action")
                ) {
                    if (isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = colors.cyan,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Feeds",
                            tint = colors.cyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
        HorizontalDivider(color = colors.border.copy(alpha = 0.7f), thickness = 1.dp)
    }
}

/**
 * Showcase Hero Card: "Trade Smarter with PipBeacon" + "Learn More ->" pill + Lighthouse art
 */
@Composable
fun ShowcaseDashboardHeroCard(
    onLearnMoreClick: () -> Unit,
    colors: DashboardThemeColors,
    modifier: Modifier = Modifier
) {
    val cardBrush = if (colors.isDark) {
        Brush.horizontalGradient(
            colors = listOf(Color(0xFF0E1D36), Color(0xFF13294B))
        )
    } else {
        Brush.horizontalGradient(
            colors = listOf(Color(0xFFFFFFFF), Color(0xFFEFF6FF))
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(cardBrush)
            .border(1.dp, colors.border, RoundedCornerShape(18.dp))
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1.2f)) {
                Text(
                    text = "Trade Smarter\nwith PipBeacon",
                    color = colors.textPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 25.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Learn. Analyze. Study setups.",
                    color = colors.textMuted,
                    fontSize = 11.5.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                ShowcaseGoldButton(
                    text = "Learn More",
                    trailingArrow = true,
                    height = 40.dp,
                    onClick = onLearnMoreClick,
                    modifier = Modifier.testTag("hero_learn_more_button")
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            PipBeaconLighthouseArt(
                modifier = Modifier
                    .size(width = 118.dp, height = 110.dp),
                showCandles = true
            )
        }
    }
}

/**
 * Showcase Markets Overview Card: EUR/USD & BTC/USDT rows with smooth Canvas Sparklines
 */
@Composable
fun ShowcaseMarketsOverviewSection(
    items: List<MarketItem>,
    onSeeAllClick: () -> Unit,
    onSelectSymbol: (String) -> Unit,
    colors: DashboardThemeColors,
    modifier: Modifier = Modifier
) {
    val overviewPairs = remember(items) {
        val eur = items.firstOrNull { it.symbol == "EUR/USD" }
        val btc = items.firstOrNull { it.symbol.startsWith("BTC") }
        val list = listOfNotNull(eur, btc)
        if (list.size >= 2) {
            list
        } else {
            listOf(
                MarketItem(
                    id = "eurusd_demo",
                    symbol = "EUR/USD",
                    name = "Euro / US Dollar",
                    marketType = MarketType.FOREX,
                    category = "Major",
                    currentPrice = 1.0825,
                    change24h = 0.0037,
                    changePercent24h = 0.34,
                    high24h = 1.0842,
                    low24h = 1.0778,
                    sparkline = listOf(1.078, 1.080, 1.079, 1.081, 1.083, 1.082, 1.0825),
                    isProviderLive = true
                ),
                MarketItem(
                    id = "btcusdt_demo",
                    symbol = "BTC/USDT",
                    name = "Bitcoin",
                    marketType = MarketType.CRYPTO,
                    category = "Layer 1",
                    currentPrice = 67432.0,
                    change24h = 1238.0,
                    changePercent24h = 1.87,
                    high24h = 67890.0,
                    low24h = 65750.0,
                    sparkline = listOf(65800.0, 66100.0, 65950.0, 66600.0, 67100.0, 66900.0, 67432.0),
                    isProviderLive = true
                )
            )
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Markets Overview",
                color = colors.textPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onSeeAllClick)
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "See All",
                    color = colors.cyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "See All Markets",
                    tint = colors.cyan,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            color = colors.surface,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, colors.border),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                overviewPairs.forEachIndexed { idx, item ->
                    val isPositive = (item.changePercent24h ?: 0.0) >= 0.0
                    val changeColor = if (isPositive) colors.green else colors.red
                    val sign = if (isPositive) "+" else ""
                    val isCrypto = item.marketType == MarketType.CRYPTO
                    val priceStr = item.currentPrice?.let {
                        if (it >= 100.0) String.format(Locale.US, "%,.0f", it) else String.format(Locale.US, "%.4f", it)
                    } ?: "--"

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectSymbol(item.symbol) }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1.25f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isCrypto) colors.gold.copy(alpha = 0.16f) else colors.cyan.copy(alpha = 0.16f)
                                    )
                                    .border(
                                        1.dp,
                                        if (isCrypto) colors.gold.copy(alpha = 0.45f) else colors.cyan.copy(alpha = 0.45f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isCrypto) Icons.Default.CurrencyBitcoin else Icons.Default.CurrencyExchange,
                                    contentDescription = null,
                                    tint = if (isCrypto) colors.gold else colors.cyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = item.symbol,
                                    color = colors.textPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = priceStr,
                                    color = colors.textMuted,
                                    fontSize = 12.sp,
                                    fontFamily = JetBrainsMonoFont,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        ShowcaseSparkline(
                            points = item.sparkline,
                            isPositive = isPositive,
                            modifier = Modifier
                                .weight(1f)
                                .height(30.dp)
                                .padding(horizontal = 8.dp)
                        )

                        Column(
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier.weight(0.75f)
                        ) {
                            Text(
                                text = "$sign${String.format(Locale.US, "%.2f", item.changePercent24h ?: 0.0)}%",
                                color = changeColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = JetBrainsMonoFont
                            )
                            Text(
                                text = if (isCrypto) "24H Demo" else "FX Spot",
                                color = colors.textMuted,
                                fontSize = 10.sp
                            )
                        }
                    }

                    if (idx < overviewPairs.lastIndex) {
                        HorizontalDivider(color = colors.border.copy(alpha = 0.5f), thickness = 1.dp)
                    }
                }
            }
        }
    }
}

/**
 * Showcase Quick Access: 4 glowing circular rings ("Learn", "Analysis", "Signals", "News")
 */
@Composable
fun ShowcaseQuickAccessRings(
    onLearnClick: () -> Unit,
    onAnalysisClick: () -> Unit,
    onSignalsClick: () -> Unit,
    onNewsClick: () -> Unit,
    colors: DashboardThemeColors,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Quick Access",
            color = colors.textPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            QuickRingItem(
                label = "Learn",
                icon = Icons.Default.School,
                ringColor = colors.gold,
                onClick = onLearnClick,
                colors = colors,
                modifier = Modifier.weight(1f).testTag("quick_ring_learn")
            )
            QuickRingItem(
                label = "Analysis",
                icon = Icons.Default.QueryStats,
                ringColor = colors.cyan,
                onClick = onAnalysisClick,
                colors = colors,
                modifier = Modifier.weight(1f).testTag("quick_ring_analysis")
            )
            QuickRingItem(
                label = "Signals",
                icon = Icons.Default.CrisisAlert,
                ringColor = colors.gold,
                onClick = onSignalsClick,
                colors = colors,
                modifier = Modifier.weight(1f).testTag("quick_ring_signals")
            )
            QuickRingItem(
                label = "News",
                icon = Icons.Default.Newspaper,
                ringColor = colors.cyan,
                onClick = onNewsClick,
                colors = colors,
                modifier = Modifier.weight(1f).testTag("quick_ring_news")
            )
        }
    }
}

@Composable
private fun QuickRingItem(
    label: String,
    icon: ImageVector,
    ringColor: Color,
    onClick: () -> Unit,
    colors: DashboardThemeColors,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(colors.surface)
                .border(1.5.dp, ringColor.copy(alpha = 0.7f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = ringColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            color = colors.textPrimary,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

@Composable
fun WinRateHeroCard(
    winRate: Int = 86,
    activeCount: Int = 5,
    totalPips: String = "+9927 PIPS",
    tpHits: Int = 100,
    totalSignals: Int = 121,
    selectedFilter: String = "All",
    onFilterSelect: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        color = ColorSurfaceCard,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, ColorBorderDivider),
        modifier = modifier.fillMaxWidth()
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1.1f)
                ) {
                    Box(
                        modifier = Modifier.size(64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material3.CircularProgressIndicator(
                            progress = { 1.0f },
                            modifier = Modifier.fillMaxSize(),
                            color = ColorBorderDivider,
                            strokeWidth = 6.dp,
                            strokeCap = StrokeCap.Round
                        )
                        androidx.compose.material3.CircularProgressIndicator(
                            progress = { winRate / 100f },
                            modifier = Modifier.fillMaxSize(),
                            color = ColorGoldAccent,
                            strokeWidth = 6.dp,
                            strokeCap = StrokeCap.Round
                        )
                        Text(
                            text = "$winRate%",
                            color = ColorGoldAccent,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = JetBrainsMonoFont
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(ColorProfitGreen)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "SAMPLE TRACK RECORD",
                                color = ColorMutedSlate,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.4.sp
                            )
                        }
                        Text(
                            text = "Study Win Rate",
                            color = ColorTextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ColorScreenBackground)
                                .border(1.dp, ColorBorderDivider, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "⚡ $activeCount Active",
                                color = ColorAccentCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Surface(
                    color = IndicatorBuyBackground,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ColorProfitGreen.copy(alpha = 0.4f)),
                    modifier = Modifier.weight(0.9f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp, horizontal = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = ColorProfitGreen,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "SAMPLE PIPS",
                                color = ColorProfitGreen,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = totalPips,
                            color = ColorProfitGreen,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = JetBrainsMonoFont
                        )
                        Text(
                            text = "$tpHits TP Hits • $totalSignals Setups",
                            color = ColorProfitGreen.copy(alpha = 0.85f),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            val filters = listOf("All", "Active", "Closed", "Stats")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEach { chip ->
                    val isSelected = selectedFilter == chip
                    FilterChip(
                        selected = isSelected,
                        onClick = { onFilterSelect(chip) },
                        label = {
                            Text(
                                text = chip,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ColorGoldAccent,
                            selectedLabelColor = Color(0xFF060D1A),
                            containerColor = ColorScreenBackground,
                            labelColor = ColorMutedSlate
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) ColorGoldAccent else ColorBorderDivider
                        ),
                        modifier = Modifier.height(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(IndicatorNeutralBackground)
                    .border(1.dp, ColorGoldAccent.copy(alpha = 0.65f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 7.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = ColorGoldAccent,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                    Text(
                        text = "Educational only. Demonstration data. Not financial advice.",
                        color = ColorGoldAccent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
fun QuickActionGrid(
    onLiveRatesClick: () -> Unit = {},
    onAiSignalsClick: () -> Unit = {},
    onMarketNewsClick: () -> Unit = {},
    onCalculatorsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuickActionItem(
            title = "Live Rates",
            subtitle = "FX & Crypto",
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            accentColor = ColorProfitGreen,
            onClick = onLiveRatesClick,
            modifier = Modifier.weight(1f)
        )
        QuickActionItem(
            title = "Signals",
            subtitle = "Rule-Based",
            icon = Icons.Default.CrisisAlert,
            accentColor = ColorGoldAccent,
            onClick = onAiSignalsClick,
            modifier = Modifier.weight(1f)
        )
        QuickActionItem(
            title = "News",
            subtitle = "Calendar",
            icon = Icons.Default.Newspaper,
            accentColor = ColorAccentCyan,
            onClick = onMarketNewsClick,
            modifier = Modifier.weight(1f)
        )
        QuickActionItem(
            title = "Calculators",
            subtitle = "Lot & Pips",
            icon = Icons.Default.Calculate,
            accentColor = GoldAccentBright,
            onClick = onCalculatorsClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickActionItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = ColorSurfaceCard,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, ColorBorderDivider),
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f))
                    .border(1.dp, accentColor.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(17.dp)
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = title,
                color = ColorTextWhite,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                color = ColorMutedSlate,
                fontSize = 9.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun MarketSessionsBar(
    sessions: List<Pair<String, Boolean>> = listOf(
        Pair("London", false),
        Pair("New York", false),
        Pair("Tokyo", true),
        Pair("Sydney", false)
    ),
    modifier: Modifier = Modifier
) {
    Surface(
        color = ColorSurfaceCard,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, ColorBorderDivider),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 9.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sessions",
                color = ColorGoldAccent,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                sessions.forEach { (name, isActive) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isActive) ColorProfitGreen else ColorMutedSlate)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isActive) "$name (Active)" else name,
                            color = if (isActive) ColorTextWhite else ColorMutedSlate,
                            fontSize = 10.sp,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MarketTickerRow(
    rates: List<MarketTickerItem> = listOf(
        MarketTickerItem("EUR/USD", "1.08450", 0.42),
        MarketTickerItem("GBP/USD", "1.26110", -0.28),
        MarketTickerItem("BTC/USDT", "$67,432", 1.87, isForex = false),
        MarketTickerItem("USD/JPY", "149.620", -0.12),
        MarketTickerItem("SOL/USDT", "$142.67", 2.12, isForex = false),
        MarketTickerItem("XAU/USD", "$2,748.50", 0.65)
    ),
    onSelectSymbol: (String) -> Unit = {},
    colors: DashboardThemeColors = getDashboardColors(true),
    modifier: Modifier = Modifier
) {
    val displayRates = if (rates.isNotEmpty()) rates + rates + rates else rates

    Row(
        modifier = modifier
            .fillMaxWidth()
            .basicMarquee(
                iterations = Int.MAX_VALUE,
                velocity = 45.dp
            ),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        displayRates.forEach { rate ->
            val isPositive = rate.changePercent >= 0.0
            val changeColor = if (isPositive) colors.green else colors.red
            val changeSign = if (isPositive) "+" else ""

            Surface(
                color = colors.surface,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, colors.border),
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onSelectSymbol(rate.symbol) }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = rate.symbol,
                        color = colors.textPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = JetBrainsMonoFont
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = rate.price,
                        color = colors.textMuted,
                        fontSize = 11.sp,
                        fontFamily = JetBrainsMonoFont
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(changeColor.copy(alpha = 0.15f))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "$changeSign${String.format(Locale.US, "%.2f", rate.changePercent)}%",
                            color = changeColor,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = JetBrainsMonoFont
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChoiceBSignalCard(
    signal: TechnicalSignal,
    isExpanded: Boolean = false,
    onToggleExpand: () -> Unit = {},
    onUnlockVipClick: () -> Unit = {},
    onOpenGraphClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isBuy = signal.action.uppercase() == "BUY"
    val actionColor = if (isBuy) ColorProfitGreen else ColorLossRed

    Surface(
        color = ColorSurfaceCard,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, ColorBorderDivider),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onToggleExpand)
            .testTag("choice_b_signal_${signal.id}")
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
                    Text(
                        text = signal.symbol,
                        color = ColorTextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = JetBrainsMonoFont
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(actionColor)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isBuy) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                                contentDescription = null,
                                tint = Color(0xFF060D1A),
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = signal.action.uppercase(),
                                color = Color(0xFF060D1A),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(ColorScreenBackground)
                        .border(1.dp, ColorBorderDivider, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(ColorProfitGreen)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = signal.status,
                            color = ColorProfitGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Entry Price",
                    color = ColorMutedSlate,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = String.format(Locale.US, if (signal.entryPrice >= 1000) "%,.2f" else "%.5f", signal.entryPrice),
                    color = ColorTextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = JetBrainsMonoFont
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ColorScreenBackground)
                    .border(1.dp, ColorBorderDivider, RoundedCornerShape(12.dp))
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Stop Loss",
                            color = ColorLossRed,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = String.format(Locale.US, if (signal.stopLoss >= 1000) "%,.2f" else "%.5f", signal.stopLoss),
                            color = ColorLossRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = JetBrainsMonoFont
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Take Profit 1",
                            color = ColorProfitGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = String.format(Locale.US, if (signal.takeProfit1 >= 1000) "%,.2f" else "%.5f", signal.takeProfit1),
                            color = ColorProfitGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = JetBrainsMonoFont
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "Take Profit 2",
                            color = ColorProfitGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = String.format(Locale.US, if (signal.takeProfit2 >= 1000) "%,.2f" else "%.5f", signal.takeProfit2),
                            color = ColorProfitGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = JetBrainsMonoFont
                        )
                    }
                }

                if (signal.isLockedForFree) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        ColorSurfaceCard.copy(alpha = 0.94f),
                                        ColorScreenBackground.copy(alpha = 0.98f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = onUnlockVipClick,
                            colors = ButtonDefaults.buttonColors(containerColor = ColorGoldAccent),
                            shape = RoundedCornerShape(18.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Reveal",
                                tint = Color(0xFF060D1A),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Reveal Educational Targets",
                                color = Color(0xFF060D1A),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    HorizontalDivider(color = ColorBorderDivider, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Technical Confluence Notes",
                        color = ColorAccentCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = signal.reasoning,
                        color = ColorMutedSlate,
                        fontSize = 10.5.sp,
                        lineHeight = 15.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sample Range: ◎ ${signal.pipPotential}p • ${signal.timeframe}",
                            color = ColorProfitGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Button(
                            onClick = onOpenGraphClick,
                            colors = ButtonDefaults.buttonColors(containerColor = ColorScreenBackground),
                            border = BorderStroke(1.dp, ColorAccentCyan),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.CandlestickChart, contentDescription = null, tint = ColorAccentCyan, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Open Graph", color = ColorAccentCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PipBeaconBottomNav(
    selectedTab: String = "home",
    onTabSelect: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(NavySurface)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        HorizontalDivider(color = NavyBorder, thickness = 1.dp)
        NavigationBar(
            containerColor = NavySurface,
            tonalElevation = 0.dp
        ) {
            val navItems = listOf(
                Triple("home", "Home", Icons.Default.Home),
                Triple("rates", "Markets", Icons.AutoMirrored.Filled.TrendingUp),
                Triple("signals", "Signals", Icons.Default.CrisisAlert),
                Triple("news", "News", Icons.Default.Newspaper),
                Triple("profile", "Profile", Icons.Default.Person)
            )

            navItems.forEach { (route, title, icon) ->
                val isSelected = selectedTab == route
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onTabSelect(route) },
                    icon = {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            modifier = Modifier.size(21.dp)
                        )
                    },
                    label = {
                        Text(
                            text = title,
                            fontSize = 10.sp,
                            maxLines = 1,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF060D1A),
                        selectedTextColor = GoldAccentBright,
                        indicatorColor = GoldAccent,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("bottom_nav_$route")
                )
            }
        }
    }
}

// =============================================================================
// MAIN SHOWCASE DASHBOARD COMPOSABLE
// =============================================================================
@Composable
fun HomeScreen(
    forexItems: List<MarketItem> = emptyList(),
    cryptoItems: List<MarketItem> = emptyList(),
    signals: List<SignalItem> = emptyList(),
    sessions: List<MarketSession> = emptyList(),
    providerStatus: DataResult<Unit> = DataResult.Success(Unit),
    lastRefreshTime: Long? = null,
    isRefreshing: Boolean = false,
    isDarkTheme: Boolean = true,
    onToggleTheme: () -> Unit = {},
    onRefresh: () -> Unit = {},
    onNavigateToForex: () -> Unit = {},
    onNavigateToCrypto: () -> Unit = {},
    onNavigateToSignals: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onOpenCalculator: () -> Unit = {},
    onSelectItem: (MarketItem) -> Unit = {},
    onAnalyzeSymbol: (String) -> Unit = {},
    onNavigateToAiTutor: () -> Unit = {},
    onNavigateToAiNews: () -> Unit = {},
    onNavigateToCalculators: () -> Unit = onOpenCalculator,
    onNavigateToLessons: () -> Unit = {},
    onNavigateToLiveRates: () -> Unit = {},
    onNavigateToSignalEngine: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToForexAcademy: () -> Unit = {},
    onNavigateToCryptoAcademy: () -> Unit = {},
    onNavigateToJournal: () -> Unit = {},
    onNavigateToCalendar: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var pulseFilter by remember { mutableStateOf("All") }
    var selectedNavTab by remember { mutableStateOf("home") }
    var expandedSignalId by remember { mutableStateOf<String?>("sig_1") }

    val colors = getDashboardColors(isDarkTheme)

    var previewSignals by remember {
        mutableStateOf(
            listOf(
                TechnicalSignal(
                    id = "sig_1",
                    symbol = "EUR/USD",
                    action = "BUY",
                    status = "ACTIVE",
                    entryPrice = 1.08200,
                    stopLoss = 1.07900,
                    takeProfit1 = 1.08500,
                    takeProfit2 = 1.08900,
                    isLockedForFree = false,
                    pipPotential = 70,
                    timeframe = "H1",
                    reasoning = "Bullish trend with support near 1.0800 and positive RSI divergence."
                ),
                TechnicalSignal(
                    id = "sig_2",
                    symbol = "XAU/USD",
                    action = "BUY",
                    status = "ACTIVE",
                    entryPrice = 2742.80,
                    stopLoss = 2728.00,
                    takeProfit1 = 2765.00,
                    takeProfit2 = 2788.00,
                    isLockedForFree = true,
                    pipPotential = 360,
                    timeframe = "H4",
                    reasoning = "Safe-haven demand breakout past $2,735 resistance with ATR confirmation."
                ),
                TechnicalSignal(
                    id = "sig_3",
                    symbol = "BTC/USDT",
                    action = "SELL",
                    status = "ACTIVE",
                    entryPrice = 67432.0,
                    stopLoss = 68200.0,
                    takeProfit1 = 66100.0,
                    takeProfit2 = 64800.0,
                    isLockedForFree = true,
                    pipPotential = 2250,
                    timeframe = "M15",
                    reasoning = "Overbought RSI 74 rejection at local supply with bearish MACD cross."
                )
            )
        )
    }

    val tickerItems = remember(forexItems, cryptoItems) {
        val list = mutableListOf<MarketTickerItem>()
        forexItems.filter { it.currentPrice != null }.take(6).forEach {
            list.add(MarketTickerItem(it.symbol, String.format(Locale.US, "%.5f", it.currentPrice ?: 0.0), it.changePercent24h ?: 0.0, true))
        }
        cryptoItems.filter { it.currentPrice != null }.take(6).forEach {
            val formatted = if ((it.currentPrice ?: 0.0) >= 1.0) {
                String.format(Locale.US, "$%,.2f", it.currentPrice ?: 0.0)
            } else {
                String.format(Locale.US, "$%.4f", it.currentPrice ?: 0.0)
            }
            list.add(MarketTickerItem(it.symbol, formatted, it.changePercent24h ?: 0.0, false))
        }
        if (list.isNotEmpty()) {
            list
        } else {
            listOf(
                MarketTickerItem("EUR/USD", "1.08250", 0.34),
                MarketTickerItem("GBP/USD", "1.27340", 0.18),
                MarketTickerItem("BTC/USDT", "$67,432", 1.87, isForex = false),
                MarketTickerItem("USD/JPY", "149.620", -0.12),
                MarketTickerItem("SOL/USDT", "$142.67", 2.12, isForex = false)
            )
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
        topBar = {
            PipBeaconHeader(
                title = "PipBeacon",
                statusText = if (providerStatus is DataResult.Success) "Live / Demo Data • Offline Ready" else "Offline Demo Data",
                isRefreshing = isRefreshing,
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme,
                onCalculatorClick = onOpenCalculator,
                onRefreshClick = onRefresh,
                onProfileClick = onNavigateToProfile,
                colors = colors
            )
        },
        bottomBar = {
            PipBeaconBottomNav(
                selectedTab = selectedNavTab,
                onTabSelect = { tab ->
                    selectedNavTab = tab
                    when (tab) {
                        "rates" -> onNavigateToLiveRates()
                        "signals" -> onNavigateToSignals()
                        "news" -> onNavigateToAiNews()
                        "profile" -> onNavigateToProfile()
                        "settings" -> onNavigateToSettings()
                    }
                }
            )
        },
        containerColor = colors.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(NavyBackground, NavyBackgroundBottom)))
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("dashboard_screen"),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Showcase Hero Banner Card ("Trade Smarter with PipBeacon" + Lighthouse)
                item {
                    ShowcaseDashboardHeroCard(
                        onLearnMoreClick = onNavigateToForexAcademy,
                        colors = colors
                    )
                }

                // 2. Markets Overview ("See All >" + Sparkline rows)
                item {
                    ShowcaseMarketsOverviewSection(
                        items = forexItems + cryptoItems,
                        onSeeAllClick = onNavigateToLiveRates,
                        onSelectSymbol = onAnalyzeSymbol,
                        colors = colors
                    )
                }

                // 3. Quick Access Circular Rings (Learn, Analysis, Signals, News)
                item {
                    ShowcaseQuickAccessRings(
                        onLearnClick = onNavigateToForexAcademy,
                        onAnalysisClick = { onAnalyzeSymbol("EUR/USD") },
                        onSignalsClick = onNavigateToSignals,
                        onNewsClick = onNavigateToAiNews,
                        colors = colors
                    )
                }

                // 4. Academy Tracks Row (Forex Academy & Crypto Academy)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            color = ColorSurfaceCard,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, ColorBorderDivider),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable(onClick = onNavigateToForexAcademy)
                                .testTag("home_forex_academy_card")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 11.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(ColorAccentCyan.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CurrencyExchange,
                                        contentDescription = "Forex Academy",
                                        tint = ColorAccentCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Forex Academy",
                                        color = ColorTextWhite,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "6 Offline Modules",
                                        color = ColorMutedSlate,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        Surface(
                            color = ColorSurfaceCard,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, ColorBorderDivider),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable(onClick = onNavigateToCryptoAcademy)
                                .testTag("home_crypto_academy_card")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 11.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(ColorGoldAccent.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CurrencyBitcoin,
                                        contentDescription = "Crypto Academy",
                                        tint = ColorGoldAccent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Crypto Academy",
                                        color = ColorTextWhite,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "6 Offline Modules",
                                        color = ColorMutedSlate,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // 5. Practice Tools Row (Calculators, Journal, Economic Calendar)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickActionItem(
                            title = "Calculators",
                            subtitle = "Pip & Lots",
                            icon = Icons.Default.Calculate,
                            accentColor = ColorGoldAccent,
                            onClick = onNavigateToCalculators,
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionItem(
                            title = "Journal",
                            subtitle = "Study Log",
                            icon = Icons.Default.Book,
                            accentColor = ColorProfitGreen,
                            onClick = onNavigateToJournal,
                            modifier = Modifier.weight(1f).testTag("home_journal_card")
                        )
                        QuickActionItem(
                            title = "Calendar",
                            subtitle = "Macro Events",
                            icon = Icons.Default.CalendarMonth,
                            accentColor = ColorAccentCyan,
                            onClick = onNavigateToCalendar,
                            modifier = Modifier.weight(1f).testTag("home_calendar_card")
                        )
                    }
                }

                // 6. Win Rate & Educational Track Record Window
                item {
                    WinRateHeroCard(
                        winRate = 86,
                        activeCount = 5,
                        totalPips = "+9927 PIPS",
                        tpHits = 100,
                        totalSignals = 121,
                        selectedFilter = pulseFilter,
                        onFilterSelect = { pulseFilter = it }
                    )
                }

                // 7. Continuous Live/Demo Rates Ticker directly after Win Rate window
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "MARKET TICKER",
                                color = ColorMutedSlate,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Tap pair for chart",
                                color = ColorAccentCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        MarketTickerRow(
                            rates = tickerItems,
                            onSelectSymbol = onAnalyzeSymbol,
                            colors = colors
                        )
                    }
                }

                // 8. Market Sessions Bar
                item {
                    MarketSessionsBar()
                }

                // 9. Forex ATR Signal Engine Card
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(onClick = onNavigateToSignalEngine)
                            .testTag("home_launch_signal_engine_button"),
                        color = ColorSurfaceCard,
                        border = BorderStroke(1.dp, ColorGoldAccent.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(ColorGoldAccent.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Radar,
                                        contentDescription = null,
                                        tint = ColorGoldAccent,
                                        modifier = Modifier.size(19.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Forex ATR Signal Engine",
                                        color = ColorTextWhite,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "RSI(14) • EMA(20/50) • MACD • TP1/2/3",
                                        color = ColorMutedSlate,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(ColorGoldAccent)
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text("OPEN", color = Color(0xFF060D1A), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }

                // 10. Smart Trading Signals Preview Section
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Educational Signal Setups",
                            color = ColorTextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "View All →",
                            color = ColorAccentCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable(onClick = onNavigateToSignals)
                                .padding(4.dp)
                        )
                    }
                }

                items(previewSignals, key = { it.id }) { signal ->
                    ChoiceBSignalCard(
                        signal = signal,
                        isExpanded = expandedSignalId == signal.id,
                        onToggleExpand = {
                            expandedSignalId = if (expandedSignalId == signal.id) null else signal.id
                        },
                        onUnlockVipClick = {
                            previewSignals = previewSignals.map {
                                if (it.id == signal.id) it.copy(isLockedForFree = false) else it
                            }
                        },
                        onOpenGraphClick = {
                            onAnalyzeSymbol(signal.symbol)
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}
