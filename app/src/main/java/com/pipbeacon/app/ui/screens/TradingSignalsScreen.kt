package com.pipbeacon.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pipbeacon.app.data.SignalItem
import com.pipbeacon.app.engine.history.SignalPerformanceStats
import com.pipbeacon.app.engine.model.Signal
import com.pipbeacon.app.ui.components.ShowcaseBackground
import com.pipbeacon.app.ui.components.ShowcaseCandlestickStrip
import com.pipbeacon.app.ui.components.ShowcaseGoldButton
import com.pipbeacon.app.ui.components.SignalCard
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
import com.pipbeacon.app.ui.theme.NavyCardElevated
import com.pipbeacon.app.ui.theme.NavySurface
import com.pipbeacon.app.ui.theme.NavySurfaceVariant
import com.pipbeacon.app.ui.theme.TextMuted
import com.pipbeacon.app.ui.theme.TextPrimary
import com.pipbeacon.app.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun TradingSignalsScreen(
    signals: List<SignalItem>,
    historicalSignals: List<Signal>,
    performanceStats: SignalPerformanceStats,
    showHistoryTab: Boolean,
    filterType: String,
    onlyHighConfidence: Boolean,
    selectedSignal: SignalItem?,
    onToggleTab: (Boolean) -> Unit,
    onFilterChange: (String) -> Unit,
    onToggleHighConfidence: (Boolean) -> Unit,
    onSelectSignal: (SignalItem?) -> Unit,
    onAnalyzeSymbol: (String) -> Unit,
    onNavigateToSignalEngine: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val filters = listOf("ALL", "FOREX", "CRYPTO", "BUY", "SELL", "ACTIVE", "COMPLETED")

    ShowcaseBackground(modifier = modifier.testTag("signals_screen")) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Segmented Tab Switcher: Active Radar vs History & Audit
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(NavyCard)
                    .border(1.dp, NavyBorder, RoundedCornerShape(24.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (!showHistoryTab) GoldAccentBright else Color.Transparent)
                        .clickable { onToggleTab(false) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Radar,
                            contentDescription = null,
                            tint = if (!showHistoryTab) Color(0xFF060D1A) else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Active Radar",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!showHistoryTab) Color(0xFF060D1A) else TextSecondary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (showHistoryTab) GoldAccentBright else Color.Transparent)
                        .clickable { onToggleTab(true) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = if (showHistoryTab) Color(0xFF060D1A) else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "History & Audit",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (showHistoryTab) Color(0xFF060D1A) else TextSecondary
                        )
                    }
                }
            }

            if (showHistoryTab) {
                SignalHistoryScreen(
                    signals = historicalSignals,
                    performanceStats = performanceStats,
                    onAnalyzeSymbol = onAnalyzeSymbol
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Showcase Spotlight Card for Top Signal
                    signals.firstOrNull()?.let { spotlight ->
                        item {
                            FeaturedSignalSpotlightCard(
                                signal = spotlight,
                                onViewDetails = { onSelectSignal(spotlight) }
                            )
                        }
                    }

                    // Filter Pills + High Confidence Switch
                    item {
                        Column {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(filters) { f ->
                                    val isSelected = filterType == f
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { onFilterChange(f) },
                                        label = { Text(f, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = GoldAccentBright,
                                            selectedLabelColor = Color(0xFF060D1A),
                                            containerColor = NavyCard,
                                            labelColor = TextSecondary
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            enabled = true,
                                            selected = isSelected,
                                            borderColor = if (isSelected) GoldAccentBright else NavyBorder
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(NavyCard)
                                    .border(1.dp, NavyBorder, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "High Confidence Setups (≥ 85%)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextPrimary
                                )
                                Switch(
                                    checked = onlyHighConfidence,
                                    onCheckedChange = onToggleHighConfidence,
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color(0xFF060D1A),
                                        checkedTrackColor = GoldAccentBright,
                                        uncheckedThumbColor = TextSecondary,
                                        uncheckedTrackColor = NavySurfaceVariant
                                    ),
                                    modifier = Modifier.testTag("high_confidence_switch")
                                )
                            }
                        }
                    }

                    // Forex ATR Signal Engine Card
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(NavyCardElevated)
                                .border(1.dp, GoldAccent.copy(alpha = 0.55f), RoundedCornerShape(12.dp))
                                .clickable(onClick = onNavigateToSignalEngine)
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(GoldAccent.copy(alpha = 0.16f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Radar, contentDescription = null, tint = GoldAccentBright, modifier = Modifier.size(17.dp))
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("Forex ATR Signal Engine", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text("RSI(14) • EMA(20/50) • MACD • Multi-tier TP1/2/3", color = TextMuted, fontSize = 10.sp)
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(GoldAccentBright)
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text("COMPUTE →", color = Color(0xFF060D1A), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Educational Disclaimer
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(IndicatorNeutralBackground)
                                .border(1.dp, IndicatorNeutral.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Educational Setups Only • Sample & Engine-Computed Signals • Not Financial Advice",
                                color = IndicatorNeutral,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    if (signals.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No active trading signals match the criteria.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextMuted
                                )
                            }
                        }
                    } else {
                        items(signals) { signal ->
                            SignalCard(
                                signal = signal,
                                onClick = { onSelectSignal(signal) },
                                onReplayClick = { onAnalyzeSymbol(signal.symbol) }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }

    selectedSignal?.let { sig ->
        SignalDetailDialog(
            signal = sig,
            onDismiss = { onSelectSignal(null) },
            onAnalyzeSymbol = onAnalyzeSymbol
        )
    }
}

@Composable
private fun FeaturedSignalSpotlightCard(
    signal: SignalItem,
    onViewDetails: () -> Unit
) {
    val isBuy = signal.isBuy
    val badgeColor = if (isBuy) IndicatorBuy else IndicatorSell
    val badgeBg = if (isBuy) IndicatorBuyBackground else IndicatorSellBackground
    val entryVal = signal.entryPrice ?: 0.0
    val priceDigits = if (entryVal >= 100.0) "%.2f" else "%.4f"

    Surface(
        color = NavyCard,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, NavyBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("featured_signal_spotlight")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(NavyCardElevated, NavyCard)
                    )
                )
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Pill Badge: BUY SIGNAL ↗ or SELL SIGNAL ↘
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(badgeBg)
                    .border(1.dp, badgeColor.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isBuy) "BUY SIGNAL" else "SELL SIGNAL",
                        color = badgeColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = if (isBuy) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                        contentDescription = null,
                        tint = badgeColor,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = signal.symbol,
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Entry: ${signal.entryPrice?.let { String.format(Locale.US, priceDigits, it) } ?: "--"}",
                color = TextPrimary,
                fontSize = 13.sp,
                fontFamily = JetBrainsMonoFont,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "TP1: ${signal.takeProfit1?.let { String.format(Locale.US, priceDigits, it) } ?: "--"} (+${signal.pipPotential} pips)",
                color = IndicatorBuy,
                fontSize = 13.sp,
                fontFamily = JetBrainsMonoFont,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "SL: ${signal.stopLoss?.let { String.format(Locale.US, priceDigits, it) } ?: "--"}",
                color = IndicatorSell,
                fontSize = 13.sp,
                fontFamily = JetBrainsMonoFont,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            ShowcaseCandlestickStrip(
                isBullishBias = isBuy,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(74.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(NavySurfaceVariant)
                    .border(1.dp, NavyBorder, RoundedCornerShape(12.dp))
                    .padding(6.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            ShowcaseGoldButton(
                text = "View Details",
                onClick = onViewDetails,
                height = 48.dp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
