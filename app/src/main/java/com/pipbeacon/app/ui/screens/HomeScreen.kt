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
import androidx.compose.foundation.basicMarquee
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CandlestickChart
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pipbeacon.app.core.DataResult
import com.pipbeacon.app.data.MarketItem
import com.pipbeacon.app.data.MarketSession
import com.pipbeacon.app.data.SignalItem
import java.util.Locale

// =============================================================================
// 1. EXACT COLOR PALETTE (PipBeacon Dark & Light Mode Specifications)
// =============================================================================
val ColorScreenBackground: Color
    @Composable
    get() = if (com.pipbeacon.app.ui.theme.LocalThemeIsDark.current) Color(0xFF0B0F17) else Color(0xFFF1F5F9)

val ColorSurfaceCard: Color
    @Composable
    get() = if (com.pipbeacon.app.ui.theme.LocalThemeIsDark.current) Color(0xFF1E293B) else Color(0xFFFFFFFF)

val ColorBorderDivider: Color
    @Composable
    get() = if (com.pipbeacon.app.ui.theme.LocalThemeIsDark.current) Color(0xFF334155) else Color(0xFFCBD5E1)

val ColorGoldAccent       = Color(0xFFEAB308) // PipBeacon Gold Accent (VIP, Highlights)
val ColorProfitGreen      = Color(0xFF10B981) // Profit / BUY Green
val ColorLossRed          = Color(0xFFEF4444) // Loss / SELL Red
val ColorAccentCyan       = Color(0xFF38BDF8) // Accent Blue / Cyan

val ColorMutedSlate: Color
    @Composable
    get() = if (com.pipbeacon.app.ui.theme.LocalThemeIsDark.current) Color(0xFF94A3B8) else Color(0xFF64748B)

val ColorTextWhite: Color
    @Composable
    get() = if (com.pipbeacon.app.ui.theme.LocalThemeIsDark.current) Color(0xFFFFFFFF) else Color(0xFF0F172A)

data class DashboardThemeColors(
    val background: Color,
    val surface: Color,
    val border: Color,
    val textPrimary: Color,
    val textMuted: Color,
    val subSurface: Color,
    val gold: Color = Color(0xFFEAB308),
    val green: Color = Color(0xFF10B981),
    val red: Color = Color(0xFFEF4444),
    val cyan: Color = Color(0xFF38BDF8),
    val greenSurface: Color,
    val isDark: Boolean = true
)

fun getDashboardColors(isDark: Boolean): DashboardThemeColors {
    return if (isDark) {
        DashboardThemeColors(
            background = Color(0xFF0B0F17),
            surface = Color(0xFF1E293B),
            border = Color(0xFF334155),
            textPrimary = Color(0xFFFFFFFF),
            textMuted = Color(0xFF94A3B8),
            subSurface = Color(0xFF0F172A),
            greenSurface = Color(0xFF092E20),
            isDark = true
        )
    } else {
        DashboardThemeColors(
            background = Color(0xFFF1F5F9),
            surface = Color(0xFFFFFFFF),
            border = Color(0xFFCBD5E1),
            textPrimary = Color(0xFF0F172A),
            textMuted = Color(0xFF64748B),
            subSurface = Color(0xFFF8FAFC),
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
    val action: String, // "BUY" or "SELL"
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
// 2. MODULAR COMPOSABLES ARCHITECTURE
// =============================================================================

/**
 * 2.1 Top App Bar:
 * - App title "PipBeacon: Market Pulse" with a green "Live Data Active" status dot.
 * - Actions: Theme Switcher (☀️/🌙), Calculator icon, and Refresh icon.
 */
@Composable
fun PipBeaconHeader(
    title: String = "PipBeacon: Market Pulse",
    statusText: String = "Live Data Active",
    isRefreshing: Boolean = false,
    isDarkTheme: Boolean = true,
    onToggleTheme: () -> Unit = {},
    onCalculatorClick: () -> Unit = {},
    onRefreshClick: () -> Unit = {},
    colors: DashboardThemeColors = getDashboardColors(isDarkTheme),
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_dot")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotAlpha"
    )

    Surface(
        color = colors.surface,
        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.border),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    color = colors.textPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(colors.green.copy(alpha = dotAlpha))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = statusText,
                        color = colors.green,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Task 1: Dark / Light Mode Toggle Button
                IconButton(
                    onClick = onToggleTheme,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("header_theme_toggle_action")
                ) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = if (isDarkTheme) "Switch to Light Mode" else "Switch to Dark Mode",
                        tint = if (isDarkTheme) colors.gold else Color(0xFF6366F1),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(2.dp))

                IconButton(
                    onClick = onCalculatorClick,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("header_calculator_action")
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = "Calculators",
                        tint = colors.gold,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(2.dp))

                IconButton(
                    onClick = onRefreshClick,
                    enabled = !isRefreshing,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("header_refresh_action")
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
    }
}

/**
 * 2.2 Hero Win Rate & Metrics Card:
 * - Left side: Circular progress win rate gauge showing "86%" with badge "5 Active".
 * - Right side: Total pips card showing "+9927 PIPS" in green.
 * - Filter Chips Row: "All", "Active", "Closed", "Stats".
 * - Regulatory Disclaimer Banner: Yellow-bordered notice reading:
 *   "Educational only. Not financial advice. Trading involves risk of loss."
 */
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
        border = androidx.compose.foundation.BorderStroke(1.dp, ColorBorderDivider),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Main Metrics Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Circular Progress Win Rate Gauge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1.1f)
                ) {
                    Box(
                        modifier = Modifier.size(68.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { 1.0f },
                            modifier = Modifier.fillMaxSize(),
                            color = ColorBorderDivider,
                            strokeWidth = 7.dp,
                            strokeCap = StrokeCap.Round
                        )
                        CircularProgressIndicator(
                            progress = { winRate / 100f },
                            modifier = Modifier.fillMaxSize(),
                            color = ColorGoldAccent,
                            strokeWidth = 7.dp,
                            strokeCap = StrokeCap.Round
                        )
                        Text(
                            text = "$winRate%",
                            color = ColorGoldAccent,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace
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
                                text = "MARKET PULSE",
                                color = ColorMutedSlate,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Text(
                            text = "Win Rate",
                            color = ColorTextWhite,
                            fontSize = 15.sp,
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

                // Right: Total Pips Banner
                Surface(
                    color = com.pipbeacon.app.ui.theme.IndicatorBuyBackground,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ColorProfitGreen.copy(alpha = 0.4f)),
                    modifier = Modifier.weight(0.9f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp, horizontal = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = ColorProfitGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "NET GAIN",
                                color = ColorProfitGreen,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = totalPips,
                            color = ColorProfitGreen,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "$tpHits TP Hits • $totalSignals Total",
                            color = ColorProfitGreen.copy(alpha = 0.85f),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Chips Row: "All", "Active", "Closed", "Stats"
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
                            selectedLabelColor = Color.Black,
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

            Spacer(modifier = Modifier.height(12.dp))

            // Regulatory Disclaimer Banner (Yellow-bordered notice)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(com.pipbeacon.app.ui.theme.IndicatorNeutralBackground)
                    .border(1.dp, ColorGoldAccent.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
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
                        text = "Educational only. Not financial advice. Trading involves risk of loss.",
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

/**
 * 2.3 Quick Action Grid:
 * 4 rounded grid cards side-by-side: "Live Rates", "AI Signals", "Market News", "Calculators"
 */
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
            subtitle = "60+ Pairs",
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            accentColor = ColorProfitGreen,
            onClick = onLiveRatesClick,
            modifier = Modifier.weight(1f)
        )
        QuickActionItem(
            title = "AI Signals",
            subtitle = "Radar",
            icon = Icons.Default.CrisisAlert,
            accentColor = ColorGoldAccent,
            onClick = onAiSignalsClick,
            modifier = Modifier.weight(1f)
        )
        QuickActionItem(
            title = "Market News",
            subtitle = "Macro",
            icon = Icons.Default.Newspaper,
            accentColor = ColorAccentCyan,
            onClick = onMarketNewsClick,
            modifier = Modifier.weight(1f)
        )
        QuickActionItem(
            title = "Calculators",
            subtitle = "Lot & Pips",
            icon = Icons.Default.Calculate,
            accentColor = Color(0xFFF59E0B),
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
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ColorBorderDivider),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
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
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
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

/**
 * 2.4 Market Sessions Bar:
 * Horizontal indicator pill showing active sessions: London, New York, Tokyo (🟢 Active), Sydney.
 */
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
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ColorBorderDivider),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
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

/**
 * 2.5 Horizontal Live Market Rates Ticker:
 * Continuous ticker tape moving from Right to Left with live pairs like EUR/USD, GBP/USD, BTC/USDT.
 */
@Composable
fun MarketTickerRow(
    rates: List<MarketTickerItem> = listOf(
        MarketTickerItem("EUR/USD", "1.08450", 0.42),
        MarketTickerItem("GBP/USD", "1.26110", -0.28),
        MarketTickerItem("BTC/USDT", "$88,450", 2.85, isForex = false),
        MarketTickerItem("USD/JPY", "154.220", 0.15),
        MarketTickerItem("SOL/USDT", "$184.20", 4.12, isForex = false),
        MarketTickerItem("XAU/USD", "$2,748.50", 0.65)
    ),
    onSelectSymbol: (String) -> Unit = {},
    colors: DashboardThemeColors = getDashboardColors(true),
    modifier: Modifier = Modifier
) {
    // Duplicate rates to create an endless continuous scrolling marquee tape from Right to Left
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
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.border),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
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
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = rate.price,
                        color = colors.textMuted,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(changeColor.copy(alpha = 0.15f))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "$changeSign${String.format(Locale.US, "%.2f", rate.changePercent)}%",
                            color = changeColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

/**
 * 2.6 Smart Trading Signals Section (Choice B Layout):
 * - Header Row: Asset pair (e.g., EUR/USD), Action badge (BUY in green / SELL in red), and Status pill (e.g., ACTIVE 🟢).
 * - Entry Price row.
 * - Full-Term Target Section explicitly spelling out full names: "Stop Loss" (in red), "Take Profit 1" (in green), and "Take Profit 2" (in green).
 * - Free User Tier Lock: For free users, the Stop Loss and Take Profit targets must be hidden behind an aesthetic blurred overlay with an "Unlock VIP Targets" gold button.
 */
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
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ColorBorderDivider),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onToggleExpand)
            .testTag("choice_b_signal_${signal.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Row: Pair + Action Badge + Status Pill
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
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    // Action badge: BUY (green) / SELL (red)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(actionColor)
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isBuy) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = signal.action.uppercase(),
                                color = Color.Black,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }

                // Status pill: ACTIVE 🟢
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

            // Entry Price Row
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
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Full-Term Target Section explicitly spelling out full names:
            // "Stop Loss", "Take Profit 1", "Take Profit 2"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(ColorScreenBackground)
                    .border(1.dp, ColorBorderDivider, RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                // Underlying targets grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Stop Loss Column
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
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Take Profit 1 Column
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
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Take Profit 2 Column
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
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Free User Tier Lock: Aesthetic blurred overlay with "Unlock VIP Targets"
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
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Unlock",
                                tint = Color.Black,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Unlock VIP Targets",
                                color = Color.Black,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }

            // Expandable details section upon tap
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
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Potential: ◎ ${signal.pipPotential}p • ${signal.timeframe}",
                            color = ColorProfitGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Button(
                            onClick = onOpenGraphClick,
                            colors = ButtonDefaults.buttonColors(containerColor = ColorScreenBackground),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ColorAccentCyan),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(28.dp)
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

/**
 * 2.7 Bottom Navigation Bar:
 * 5 icons with text tabs: Pulse (active gold), Live Rates, Signals, News, Settings.
 */
@Composable
fun PipBeaconBottomNav(
    selectedTab: String = "home",
    onTabSelect: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = ColorSurfaceCard,
        tonalElevation = 6.dp,
        modifier = modifier
            .fillMaxWidth()
            .border(androidx.compose.foundation.BorderStroke(1.dp, ColorBorderDivider))
    ) {
        val navItems = listOf(
            Triple("home", "Pulse", Icons.Default.Dashboard),
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
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = {
                    Text(
                        text = title,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    selectedTextColor = ColorGoldAccent,
                    indicatorColor = ColorGoldAccent,
                    unselectedIconColor = ColorMutedSlate,
                    unselectedTextColor = ColorMutedSlate
                ),
                modifier = Modifier.testTag("bottom_nav_$route")
            )
        }
    }
}

// =============================================================================
// MAIN PRODUCTION-READY DASHBOARD COMPOSABLE (PipBeacon: Market Pulse)
// =============================================================================

@Composable
fun HomeScreen(
    // Data inputs (with rich production defaults)
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
    modifier: Modifier = Modifier
) {
    var pulseFilter by remember { mutableStateOf("All") }
    var selectedNavTab by remember { mutableStateOf("home") }
    var expandedSignalId by remember { mutableStateOf<String?>("sig_1") }

    val colors = getDashboardColors(isDarkTheme)

    // Exactly 3 Preview Signals (Choice B Layout with Full Terminology)
    var previewSignals by remember {
        mutableStateOf(
            listOf(
                TechnicalSignal(
                    id = "sig_1",
                    symbol = "EUR/USD",
                    action = "BUY",
                    status = "ACTIVE 🟢",
                    entryPrice = 1.08450,
                    stopLoss = 1.08150,
                    takeProfit1 = 1.08950,
                    takeProfit2 = 1.09450,
                    isLockedForFree = true,
                    pipPotential = 50,
                    timeframe = "H1",
                    reasoning = "Bullish momentum bounce above 50 EMA on H1 timeframe with positive RSI divergence."
                ),
                TechnicalSignal(
                    id = "sig_2",
                    symbol = "XAU/USD",
                    action = "BUY",
                    status = "ACTIVE 🟢",
                    entryPrice = 2742.80,
                    stopLoss = 2728.00,
                    takeProfit1 = 2765.00,
                    takeProfit2 = 2788.00,
                    isLockedForFree = true,
                    pipPotential = 360,
                    timeframe = "H4",
                    reasoning = "Safe-haven gold demand breakout past $2,735 resistance with strong volume confirmation."
                ),
                TechnicalSignal(
                    id = "sig_3",
                    symbol = "BTC/USDT",
                    action = "SELL",
                    status = "ACTIVE 🟢",
                    entryPrice = 88450.0,
                    stopLoss = 89900.0,
                    takeProfit1 = 86200.0,
                    takeProfit2 = 84500.0,
                    isLockedForFree = true,
                    pipPotential = 2250,
                    timeframe = "M15",
                    reasoning = "Overbought RSI 76 rejection at $88.5k with bearish MACD divergence."
                )
            )
        )
    }

    // Horizontal Ticker items dynamically mapped from live backend feeds
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
                MarketTickerItem("EUR/USD", "1.08450", 0.42),
                MarketTickerItem("GBP/USD", "1.26110", -0.28),
                MarketTickerItem("BTC/USDT", "$88,450", 2.85, isForex = false),
                MarketTickerItem("USD/JPY", "154.220", 0.15),
                MarketTickerItem("SOL/USDT", "$184.20", 4.12, isForex = false),
                MarketTickerItem("XAU/USD", "$2,748.50", 0.65)
            )
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
        topBar = {
            PipBeaconHeader(
                title = "PipBeacon: Market Pulse",
                statusText = if (providerStatus is DataResult.Success) "Live / Demo Data • Offline Ready" else "Demo Data • Offline Ready",
                isRefreshing = isRefreshing,
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme,
                onCalculatorClick = onOpenCalculator,
                onRefreshClick = onRefresh,
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colors.background)
                .testTag("dashboard_screen"),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 2. Hero Win Rate & Metrics Card
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

            // Task 2: Rates ticker moved directly after the Win Rate windows section
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LIVE MARKET RATES",
                            color = ColorMutedSlate,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Tap pair for graph",
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

            // 3. Quick Action Grid (Live Rates, AI Signals, Market News, Calculators)
            item {
                QuickActionGrid(
                    onLiveRatesClick = onNavigateToLiveRates,
                    onAiSignalsClick = onNavigateToSignals,
                    onMarketNewsClick = onNavigateToAiNews,
                    onCalculatorsClick = onNavigateToCalculators
                )
            }

            // 3b. Academy Learning Hub (Forex Academy & Crypto Academy)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = ColorSurfaceCard,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ColorBorderDivider),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(onClick = onNavigateToForexAcademy)
                            .testTag("home_forex_academy_card")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(ColorAccentCyan.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CurrencyExchange,
                                    contentDescription = "Forex Academy",
                                    tint = ColorAccentCyan,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Forex Academy",
                                    color = ColorTextWhite,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "6 Offline Modules",
                                    color = ColorMutedSlate,
                                    fontSize = 9.5.sp
                                )
                            }
                        }
                    }

                    Surface(
                        color = ColorSurfaceCard,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ColorBorderDivider),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(onClick = onNavigateToCryptoAcademy)
                            .testTag("home_crypto_academy_card")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(ColorGoldAccent.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CurrencyBitcoin,
                                    contentDescription = "Crypto Academy",
                                    tint = ColorGoldAccent,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Crypto Academy",
                                    color = ColorTextWhite,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "6 Offline Modules",
                                    color = ColorMutedSlate,
                                    fontSize = 9.5.sp
                                )
                            }
                        }
                    }
                }
            }

            // 4. Market Sessions Bar (London, New York, Tokyo Active, Sydney)
            item {
                MarketSessionsBar()
            }

            // 6. Smart Trading Signals Section (Choice B Layout)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Smart Trading Signals - Tap row to expand",
                        color = ColorTextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "View All (9) ->",
                        color = ColorAccentCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable(onClick = onNavigateToSignals)
                            .padding(4.dp)
                    )
                }
            }

            // Launch Forex ATR Signal Engine Action Card
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onNavigateToSignalEngine)
                        .testTag("home_launch_signal_engine_button"),
                    color = ColorSurfaceCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ColorGoldAccent.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ColorGoldAccent.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Radar,
                                    contentDescription = null,
                                    tint = ColorGoldAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Forex ATR Signal Engine",
                                    color = ColorTextWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "RSI(14) • EMA(20/50) • MACD • Multi-Tier TP1/2/3",
                                    color = ColorMutedSlate,
                                    fontSize = 10.sp
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ColorGoldAccent)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("ENGINE ➔", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Exactly 3 Preview Signal Cards (Choice B Layout)
            items(previewSignals, key = { it.id }) { signal ->
                ChoiceBSignalCard(
                    signal = signal,
                    isExpanded = expandedSignalId == signal.id,
                    onToggleExpand = {
                        expandedSignalId = if (expandedSignalId == signal.id) null else signal.id
                    },
                    onUnlockVipClick = {
                        // Toggle VIP unlock for demonstration
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
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
