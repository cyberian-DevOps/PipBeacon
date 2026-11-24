package com.pipbeacon.app.ui.screens

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.pipbeacon.app.data.SignalItem
import com.pipbeacon.app.engine.history.SignalPerformanceStats
import com.pipbeacon.app.engine.model.Signal
import com.pipbeacon.app.ui.components.SignalBadge
import com.pipbeacon.app.ui.components.SignalCard
import com.pipbeacon.app.ui.theme.GoldAccent
import com.pipbeacon.app.ui.theme.GoldAccentBright
import com.pipbeacon.app.ui.theme.IndicatorBuy
import com.pipbeacon.app.ui.theme.IndicatorSell
import com.pipbeacon.app.ui.theme.NavyBackground
import com.pipbeacon.app.ui.theme.NavyBorder
import com.pipbeacon.app.ui.theme.NavyCard
import com.pipbeacon.app.ui.theme.NavySurface
import com.pipbeacon.app.ui.theme.NavySurfaceVariant
import com.pipbeacon.app.ui.theme.TealAccent
import com.pipbeacon.app.ui.theme.TextMuted
import com.pipbeacon.app.ui.theme.TextPrimary
import com.pipbeacon.app.ui.theme.TextSecondary

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
    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .testTag("signals_screen")
    ) {
        // Navigation Segment: Active Radar vs History & Performance
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(NavySurface)
                .border(1.dp, NavyBorder, RoundedCornerShape(10.dp))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (!showHistoryTab) TealAccent else Color.Transparent)
                    .clickable { onToggleTab(false) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Radar,
                        contentDescription = null,
                        tint = if (!showHistoryTab) Color.Black else TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Active Radar",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (!showHistoryTab) Color.Black else TextSecondary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (showHistoryTab) TealAccent else Color.Transparent)
                    .clickable { onToggleTab(true) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = if (showHistoryTab) Color.Black else TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "History & Audit",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (showHistoryTab) Color.Black else TextSecondary
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
            // Active Signals Filter Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filters) { f ->
                        val isSelected = filterType == f
                        FilterChip(
                            selected = isSelected,
                            onClick = { onFilterChange(f) },
                            label = { Text(f, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TealAccent,
                                selectedLabelColor = Color.Black,
                                containerColor = NavySurface,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) TealAccent else NavyBorder
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // High confidence toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(NavySurface)
                        .border(1.dp, NavyBorder, RoundedCornerShape(8.dp))
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
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = TealAccent,
                            uncheckedThumbColor = TextSecondary,
                            uncheckedTrackColor = NavyBackground
                        ),
                        modifier = Modifier.testTag("high_confidence_switch")
                    )
                }
            }

            // Launch Forex ATR Signal Engine Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(NavySurfaceVariant)
                    .border(1.dp, GoldAccentBright.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .clickable(onClick = onNavigateToSignalEngine)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(GoldAccentBright.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Radar, contentDescription = null, tint = GoldAccentBright, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Forex ATR Signal Engine", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("RSI(14) • EMA(20/50) • MACD • Multi-tier TP1/2/3", color = TextMuted, fontSize = 9.sp)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(GoldAccentBright)
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text("COMPUTE ➔", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Educational & Sample Setup Notice
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(com.pipbeacon.app.ui.theme.IndicatorNeutralBackground)
                    .border(
                        1.dp,
                        com.pipbeacon.app.ui.theme.IndicatorNeutral.copy(alpha = 0.45f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Educational Setups Only • Sample & Engine-Computed Signals • Not Financial Advice",
                    color = com.pipbeacon.app.ui.theme.IndicatorNeutral,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // List of Signal Rows
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
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
            }
        }
    }

    // Detail & Trade Execution Sheet Dialog
    selectedSignal?.let { sig ->
        SignalDetailDialog(
            signal = sig,
            onDismiss = { onSelectSignal(null) },
            onAnalyzeSymbol = onAnalyzeSymbol
        )
    }
}

@Composable
private fun TradeParamRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = TextMuted)
        Text(text = value, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold)
    }
}
