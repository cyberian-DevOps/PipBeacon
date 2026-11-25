package com.pipbeacon.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pipbeacon.app.core.SignalType
import com.pipbeacon.app.core.TimeFrame
import com.pipbeacon.app.engine.signal.ForexSignalResult
import com.pipbeacon.app.engine.signal.IndicatorBias
import com.pipbeacon.app.engine.signal.TargetLevel
import com.pipbeacon.app.engine.signal.VolatilityLevel
import com.pipbeacon.app.ui.theme.GoldAccent
import com.pipbeacon.app.ui.theme.GoldAccentBright
import com.pipbeacon.app.ui.theme.IndicatorBuy
import com.pipbeacon.app.ui.theme.IndicatorBuyBackground
import com.pipbeacon.app.ui.theme.IndicatorNeutral
import com.pipbeacon.app.ui.theme.IndicatorNeutralBackground
import com.pipbeacon.app.ui.theme.IndicatorSell
import com.pipbeacon.app.ui.theme.IndicatorSellBackground
import com.pipbeacon.app.ui.theme.NavyBackground
import com.pipbeacon.app.ui.theme.NavyBorder
import com.pipbeacon.app.ui.theme.NavyCard
import com.pipbeacon.app.ui.theme.NavyCardElevated
import com.pipbeacon.app.ui.theme.NavySurface
import com.pipbeacon.app.ui.theme.NavySurfaceVariant
import com.pipbeacon.app.ui.theme.TealAccent
import com.pipbeacon.app.ui.theme.TextMuted
import com.pipbeacon.app.ui.theme.TextPrimary
import com.pipbeacon.app.ui.theme.TextSecondary
import com.pipbeacon.app.viewmodel.ForexSignalEngineViewModel

@Composable
fun ForexSignalEngineScreen(
    viewModel: ForexSignalEngineViewModel,
    onBack: () -> Unit,
    onOpenChart: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    com.pipbeacon.app.ui.components.ShowcaseBackground(
        modifier = Modifier.testTag("forex_signal_engine_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Top Navigation Bar
            ForexSignalEngineHeader(
                isLoading = uiState.isLoading,
                onBack = onBack,
                onRefresh = { viewModel.computeSignals() }
            )

            // 2. Pair Quick Selector Strip
            PairSelectorScrollRow(
                signals = uiState.allSignals,
                selectedSymbol = uiState.selectedSymbol,
                onSelect = { viewModel.selectSymbol(it) }
            )

            // 3. Main Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                val signal = uiState.activeSignal

                if (signal != null) {
                    // Hero Signal & Confluence Card
                    item {
                        HeroSignalCard(
                            signal = signal,
                            selectedTimeframe = uiState.selectedTimeframe,
                            onTimeframeSelect = { viewModel.setTimeframe(it) },
                            onOpenChart = { onOpenChart(signal.symbol) }
                        )
                    }

                    // Multi-Tier Targets Section (TP1, TP2, TP3 and Stop-Loss)
                    item {
                        MultiTierTargetsSection(
                            signal = signal,
                            riskMultiplier = uiState.riskMultiplier,
                            onRiskMultiplierChange = { viewModel.setRiskMultiplier(it) }
                        )
                    }

                    // Technical Indicators Telemetry Grid (RSI, EMA 20/50, MACD, ATR 14)
                    item {
                        IndicatorsTelemetrySection(signal = signal)
                    }

                    // Volatility-Adjusted Risk Management & Lot Size Calculator
                    item {
                        VolatilityRiskManagementCard(
                            signal = signal,
                            accountBalance = uiState.accountBalance,
                            riskPercent = uiState.riskPercentage,
                            recommendedLots = uiState.recommendedLotSize,
                            onBalanceChange = { viewModel.setAccountBalance(it) },
                            onRiskChange = { viewModel.setRiskPercentage(it) }
                        )
                    }

                    // Strategy Notes & Confluence Summary
                    item {
                        StrategyConfluenceCard(signal = signal)
                    }
                } else if (uiState.isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = GoldAccentBright, strokeWidth = 3.dp)
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "Computing technical indicators across Forex pairs...",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                // Quick Overview of other Forex Pairs
                item {
                    Text(
                        text = "ALL COMPUTED FOREX SIGNALS",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(uiState.allSignals) { otherSignal ->
                    ForexSignalRowItem(
                        signal = otherSignal,
                        isSelected = otherSignal.symbol == uiState.selectedSymbol,
                        onClick = { viewModel.selectSymbol(otherSignal.symbol) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ForexSignalEngineHeader(
    isLoading: Boolean,
    onBack: () -> Unit,
    onRefresh: () -> Unit
) {
    Surface(
        color = NavySurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("signal_engine_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Forex Signal Engine",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(GoldAccent.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("ATR VOLATILITY", color = GoldAccentBright, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(
                        text = "RSI(14) • EMA(20/50) • MACD • Multi-tier TP1/TP2/TP3",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            IconButton(
                onClick = onRefresh,
                enabled = !isLoading,
                modifier = Modifier.testTag("signal_engine_refresh_button")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = GoldAccentBright, strokeWidth = 2.dp)
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Recompute",
                        tint = TealAccent
                    )
                }
            }
        }
    }
}

@Composable
private fun PairSelectorScrollRow(
    signals: List<ForexSignalResult>,
    selectedSymbol: String,
    onSelect: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(NavySurfaceVariant)
            .horizontalScroll(scrollState)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        signals.forEach { signal ->
            val isSelected = signal.symbol == selectedSymbol
            val (badgeColor, badgeText) = when {
                signal.isBuy -> Pair(IndicatorBuy, "BUY")
                signal.isSell -> Pair(IndicatorSell, "SELL")
                else -> Pair(IndicatorNeutral, "HOLD")
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) NavyCardElevated else NavySurface)
                    .border(
                        1.dp,
                        if (isSelected) GoldAccentBright else NavyBorder,
                        RoundedCornerShape(8.dp)
                    )
                    .clickable { onSelect(signal.symbol) }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = signal.symbol,
                        color = if (isSelected) GoldAccentBright else TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(badgeColor)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = badgeText,
                        color = badgeColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroSignalCard(
    signal: ForexSignalResult,
    selectedTimeframe: TimeFrame,
    onTimeframeSelect: (TimeFrame) -> Unit,
    onOpenChart: () -> Unit
) {
    val (directionColor, directionBg, directionText) = when (signal.signalType) {
        SignalType.STRONG_BUY -> Triple(IndicatorBuy, IndicatorBuyBackground, "STRONG BUY")
        SignalType.BUY -> Triple(IndicatorBuy, IndicatorBuyBackground, "BUY")
        SignalType.STRONG_SELL -> Triple(IndicatorSell, IndicatorSellBackground, "STRONG SELL")
        SignalType.SELL -> Triple(IndicatorSell, IndicatorSellBackground, "SELL")
        SignalType.NEUTRAL -> Triple(IndicatorNeutral, IndicatorNeutralBackground, "NEUTRAL")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hero_signal_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Pair & Direction Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = signal.symbol,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = signal.pairName,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(directionBg)
                        .border(1.dp, directionColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (signal.isBuy) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                            contentDescription = null,
                            tint = directionColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = directionText,
                            color = directionColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Price & Confluence Score Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "CURRENT ENTRY PRICE", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = String.format(java.util.Locale.US, if (signal.symbol.contains("JPY")) "%.3f" else "%.5f", signal.entryPrice),
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "CONFLUENCE SCORE", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${signal.confluenceScore}%",
                            color = if (signal.confluenceScore >= 75) IndicatorBuy else GoldAccentBright,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(GoldAccent.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (signal.confluenceScore >= 75) "HIGH CONFLUENCE" else "MODERATE",
                                color = GoldAccentBright,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Confluence Linear Bar
            LinearProgressIndicator(
                progress = { signal.confluenceScore / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (signal.confluenceScore >= 75) IndicatorBuy else GoldAccentBright,
                trackColor = NavySurfaceVariant,
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Timeframe Selector & Open Graph Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(TimeFrame.M15, TimeFrame.H1, TimeFrame.H4, TimeFrame.D1).forEach { tf ->
                        val isTfSelected = selectedTimeframe == tf
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isTfSelected) GoldAccentBright else NavySurfaceVariant)
                                .border(1.dp, if (isTfSelected) GoldAccentBright else NavyBorder, RoundedCornerShape(6.dp))
                                .clickable { onTimeframeSelect(tf) }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = tf.label,
                                color = if (isTfSelected) Color.Black else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Button(
                    onClick = onOpenChart,
                    colors = ButtonDefaults.buttonColors(containerColor = NavySurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TealAccent),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.Timeline, contentDescription = null, tint = TealAccent, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Interactive Chart", color = TealAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun MultiTierTargetsSection(
    signal: ForexSignalResult,
    riskMultiplier: Double,
    onRiskMultiplierChange: (Double) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MULTI-TIER TARGETS & STOP-LOSS",
                        color = GoldAccentBright,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Volatility-Adjusted via ATR(14) Risk Anchor",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }

                // ATR Multiplier Pill Selector
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(1.0, 1.5, 2.0).forEach { mult ->
                        val isSel = riskMultiplier == mult
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) TealAccent else NavySurfaceVariant)
                                .clickable { onRiskMultiplierChange(mult) }
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "${mult}x ATR",
                                color = if (isSel) Color.Black else TextSecondary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stop Loss Row Card
            TargetLevelCard(
                title = "Stop Loss (Risk Anchor)",
                badge = "${signal.stopLossPips}p Risk",
                price = signal.stopLossPrice,
                pips = -signal.stopLossPips,
                riskReward = "1.0x Risk",
                description = "Volatility cushion (${signal.stopLossRiskReward}) to prevent stop-hunting whipsaws.",
                accentColor = IndicatorSell,
                accentBg = IndicatorSellBackground,
                isStopLoss = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Take Profit 1 (Conservative)
            TargetLevelCard(
                title = signal.tp1.label,
                badge = "1:1 R:R",
                price = signal.tp1.price,
                pips = signal.tp1.pips,
                riskReward = "1:1 R:R",
                description = "${signal.tp1.description} • Bank partial profits and move SL to Breakeven.",
                accentColor = IndicatorBuy,
                accentBg = IndicatorBuyBackground,
                isStopLoss = false
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Take Profit 2 (Balanced Target)
            TargetLevelCard(
                title = signal.tp2.label,
                badge = "1:2 R:R",
                price = signal.tp2.price,
                pips = signal.tp2.pips,
                riskReward = "1:2 R:R",
                description = "${signal.tp2.description} • Core structural target based on average daily ATR range.",
                accentColor = IndicatorBuy,
                accentBg = IndicatorBuyBackground,
                isStopLoss = false
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Take Profit 3 (Runner Target)
            TargetLevelCard(
                title = signal.tp3.label,
                badge = "1:3 R:R",
                price = signal.tp3.price,
                pips = signal.tp3.pips,
                riskReward = "1:3 R:R",
                description = "${signal.tp3.description} • Trend runner capture for maximum profit expansion.",
                accentColor = GoldAccentBright,
                accentBg = GoldAccent.copy(alpha = 0.15f),
                isStopLoss = false
            )
        }
    }
}

@Composable
private fun TargetLevelCard(
    title: String,
    badge: String,
    price: Double,
    pips: Double,
    riskReward: String,
    description: String,
    accentColor: Color,
    accentBg: Color,
    isStopLoss: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(NavySurfaceVariant)
            .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(accentColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(accentBg)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badge,
                        color = accentColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = String.format(java.util.Locale.US, "%.5f", price),
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                Text(
                    text = if (isStopLoss) "-${kotlin.math.abs(pips)} pips" else "+${pips} pips",
                    color = accentColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                color = TextSecondary,
                fontSize = 10.sp,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
private fun IndicatorsTelemetrySection(signal: ForexSignalResult) {
    val telemetry = signal.telemetry

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TECHNICAL INDICATORS CONFLUENCE",
                    color = GoldAccentBright,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(TealAccent.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("4/4 SYNCED", color = TealAccent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 1. RSI (14)
            IndicatorTelemetryRow(
                name = "RSI (14)",
                value = "${telemetry.rsiValue}",
                bias = telemetry.rsiBias,
                details = telemetry.rsiDescription
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 2. EMA (20 / 50)
            IndicatorTelemetryRow(
                name = "EMA (20 / 50)",
                value = "${telemetry.ema20Value} / ${telemetry.ema50Value}",
                bias = telemetry.emaBias,
                details = telemetry.emaCrossState
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 3. MACD (12, 26, 9)
            IndicatorTelemetryRow(
                name = "MACD (12,26,9)",
                value = "Hist: ${telemetry.macdHistogram}",
                bias = telemetry.macdBias,
                details = telemetry.macdDescription
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 4. ATR (14) Volatility
            IndicatorTelemetryRow(
                name = "ATR (14)",
                value = "${telemetry.atr14Pips} pips (${telemetry.atr14Value})",
                bias = IndicatorBias.NEUTRAL,
                details = telemetry.volatilityDescription
            )
        }
    }
}

@Composable
private fun IndicatorTelemetryRow(
    name: String,
    value: String,
    bias: IndicatorBias,
    details: String
) {
    val (biasColor, biasBg) = when (bias) {
        IndicatorBias.BULLISH -> Pair(IndicatorBuy, IndicatorBuyBackground)
        IndicatorBias.BEARISH -> Pair(IndicatorSell, IndicatorSellBackground)
        IndicatorBias.NEUTRAL -> Pair(IndicatorNeutral, IndicatorNeutralBackground)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(NavySurfaceVariant)
            .border(1.dp, NavyBorder, RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    color = TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = value,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(biasBg)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = bias.label,
                            color = biasColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = details,
                color = TextMuted,
                fontSize = 10.sp,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
private fun VolatilityRiskManagementCard(
    signal: ForexSignalResult,
    accountBalance: Double,
    riskPercent: Double,
    recommendedLots: Double,
    onBalanceChange: (Double) -> Unit,
    onRiskChange: (Double) -> Unit
) {
    var balanceText by remember(accountBalance) { mutableStateOf(accountBalance.toInt().toString()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Calculate, contentDescription = null, tint = GoldAccentBright, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "VOLATILITY RISK POSITION SIZER",
                        color = GoldAccentBright,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Recommended Lot Size Result Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(NavySurfaceVariant)
                    .border(1.dp, GoldAccentBright.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "RECOMMENDED LOT SIZE", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "$recommendedLots Lots",
                            color = GoldAccentBright,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "MAX DOLLAR RISK", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        val maxRiskDollars = accountBalance * (riskPercent / 100.0)
                        Text(
                            text = "$${String.format(java.util.Locale.US, "%.2f", maxRiskDollars)}",
                            color = IndicatorSell,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Inputs Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Balance Input
                OutlinedTextField(
                    value = balanceText,
                    onValueChange = { newText ->
                        balanceText = newText
                        newText.toDoubleOrNull()?.let { onBalanceChange(it) }
                    },
                    label = { Text("Balance ($)", fontSize = 10.sp) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldAccentBright,
                        unfocusedBorderColor = NavyBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = NavySurfaceVariant,
                        unfocusedContainerColor = NavySurfaceVariant
                    )
                )

                // Risk % Selector Buttons
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Risk % per Trade", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(0.5, 1.0, 2.0).forEach { r ->
                            val isSel = riskPercent == r
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) GoldAccentBright else NavySurfaceVariant)
                                    .border(1.dp, if (isSel) GoldAccentBright else NavyBorder, RoundedCornerShape(6.dp))
                                    .clickable { onRiskChange(r) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$r%",
                                    color = if (isSel) Color.Black else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StrategyConfluenceCard(signal: ForexSignalResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = TealAccent, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "AI ENGINE CONFLUENCE REASONING",
                    color = TealAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = signal.strategyNotes,
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun ForexSignalRowItem(
    signal: ForexSignalResult,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val (badgeColor, badgeBg, badgeText) = when {
        signal.isBuy -> Triple(IndicatorBuy, IndicatorBuyBackground, "BUY")
        signal.isSell -> Triple(IndicatorSell, IndicatorSellBackground, "SELL")
        else -> Triple(IndicatorNeutral, IndicatorNeutralBackground, "HOLD")
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) NavyCardElevated else NavyCard)
            .border(
                1.dp,
                if (isSelected) GoldAccentBright else NavyBorder,
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = signal.symbol,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(badgeBg)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badgeText,
                            color = badgeColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Entry: ${signal.entryPrice} • ATR: ${signal.telemetry.atr14Pips}p • ${signal.confluenceScore}% Match",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "TP1: +${signal.tp1.pips}p",
                    color = IndicatorBuy,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "SL: -${signal.stopLossPips}p",
                    color = IndicatorSell,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
