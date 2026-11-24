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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.pipbeacon.app.core.MarketType
import com.pipbeacon.app.engine.history.SignalPerformanceStats
import com.pipbeacon.app.engine.model.Signal
import com.pipbeacon.app.engine.model.SignalDirection
import com.pipbeacon.app.engine.model.SignalOutcomeStatus
import com.pipbeacon.app.ui.theme.IndicatorBuy
import com.pipbeacon.app.ui.theme.IndicatorNeutral
import com.pipbeacon.app.ui.theme.IndicatorSell
import com.pipbeacon.app.ui.theme.NavyBackground
import com.pipbeacon.app.ui.theme.NavyBorder
import com.pipbeacon.app.ui.theme.NavyCard
import com.pipbeacon.app.ui.theme.NavyCardElevated
import com.pipbeacon.app.ui.theme.NavySurface
import com.pipbeacon.app.ui.theme.TealAccent
import com.pipbeacon.app.ui.theme.TextMuted
import com.pipbeacon.app.ui.theme.TextPrimary
import com.pipbeacon.app.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SignalHistoryScreen(
    signals: List<Signal>,
    performanceStats: SignalPerformanceStats,
    onAnalyzeSymbol: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("ALL") }
    val filters = listOf("ALL", "PENDING", "TARGET HIT", "STOPPED OUT", "FOREX", "CRYPTO")
    var inspectedSignal by remember { mutableStateOf<Signal?>(null) }

    val filteredList = remember(signals, selectedFilter) {
        signals.filter { sig ->
            when (selectedFilter) {
                "PENDING" -> sig.outcomeStatus == SignalOutcomeStatus.PENDING
                "TARGET HIT" -> sig.outcomeStatus in listOf(
                    SignalOutcomeStatus.HIT_TP1,
                    SignalOutcomeStatus.HIT_TP2,
                    SignalOutcomeStatus.HIT_TP3
                )
                "STOPPED OUT" -> sig.outcomeStatus == SignalOutcomeStatus.STOPPED_OUT
                "FOREX" -> sig.marketType == MarketType.FOREX
                "CRYPTO" -> sig.marketType == MarketType.CRYPTO
                else -> true
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .testTag("signal_history_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Mandatory Compliance & Risk Warning Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E1E12))
                    .border(1.dp, IndicatorNeutral.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = IndicatorNeutral,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "NO WIN RATES OR PROFITS PROMISED",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = IndicatorNeutral
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "PipBeacon does not execute trades, manage funds, or connect to brokerage accounts. All signals are generated by local deterministic rules, marked experimental, and recorded immutably for research verification.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            lineHeight = 16.sp,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Performance Statistics Summary Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(NavyCard)
                    .border(1.dp, NavyBorder, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "HISTORICAL AUDIT LOG",
                            style = MaterialTheme.typography.labelSmall,
                            color = TealAccent,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF1E293B))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Rule Engine v1.2",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatColumn("Total Published", "${performanceStats.totalSignals}", TextPrimary)
                        StatColumn("Pending", "${performanceStats.pendingCount}", TealAccent)
                        StatColumn("Hit Target", "${performanceStats.hitTp1Count + performanceStats.hitTp2Count + performanceStats.hitTp3Count}", IndicatorBuy)
                        StatColumn("Stopped Out", "${performanceStats.stoppedOutCount}", IndicatorSell)
                        StatColumn("Avg R:R", performanceStats.averageRiskReward, TealAccent)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Original published values are permanently preserved and cannot be silently rewritten.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // Filter chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filters) { f ->
                    val isSel = selectedFilter == f
                    FilterChip(
                        selected = isSel,
                        onClick = { selectedFilter = f },
                        label = { Text(f, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TealAccent,
                            selectedLabelColor = Color.Black,
                            containerColor = NavySurface,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSel,
                            borderColor = if (isSel) TealAccent else NavyBorder
                        )
                    )
                }
            }
        }

        // Signal records list
        if (filteredList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No historical signals match the filter.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }
            }
        } else {
            items(filteredList) { sig ->
                HistoricalSignalCard(
                    signal = sig,
                    onClick = { inspectedSignal = sig }
                )
            }
        }
    }

    // Detailed Audit Modal
    inspectedSignal?.let { sig ->
        SignalAuditDetailDialog(
            signal = sig,
            onDismiss = { inspectedSignal = null },
            onAnalyze = {
                inspectedSignal = null
                onAnalyzeSymbol(sig.symbol)
            }
        )
    }
}

@Composable
fun HistoricalSignalCard(
    signal: Signal,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBuy = signal.direction == SignalDirection.BUY
    val directionColor = if (isBuy) IndicatorBuy else IndicatorSell
    val outcomeColor = when (signal.outcomeStatus) {
        SignalOutcomeStatus.PENDING -> TealAccent
        SignalOutcomeStatus.HIT_TP1, SignalOutcomeStatus.HIT_TP2, SignalOutcomeStatus.HIT_TP3 -> IndicatorBuy
        SignalOutcomeStatus.STOPPED_OUT -> IndicatorSell
        SignalOutcomeStatus.EXPIRED -> TextMuted
    }

    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US) }
    val formattedDate = remember(signal.creationTimestamp) { dateFormat.format(Date(signal.creationTimestamp)) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NavyCard)
            .border(1.dp, NavyBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Column {
            // Header: Symbol, Timeframe, Direction Badge, Outcome Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = signal.symbol,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF1E293B))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(signal.timeframe.label, color = TealAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(directionColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(signal.direction.label, color = directionColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Outcome Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(outcomeColor.copy(alpha = 0.15f))
                        .border(1.dp, outcomeColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = signal.outcomeStatus.label,
                        color = outcomeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Immutable Levels: Entry, SL, TP1, TP2, TP3
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0A0F1D))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AuditLevel("ENTRY", String.format("%.4f", signal.entryPrice), TextPrimary)
                AuditLevel("SL", String.format("%.4f", signal.stopLoss), IndicatorSell)
                AuditLevel("TP1", String.format("%.4f", signal.takeProfit1), IndicatorBuy)
                AuditLevel("TP2", String.format("%.4f", signal.takeProfit2), IndicatorBuy)
                AuditLevel("TP3", String.format("%.4f", signal.takeProfit3), IndicatorBuy)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = signal.plainLanguageExplanation,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Footer metadata
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Published: $formattedDate",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    fontSize = 10.sp
                )
                Text(
                    text = signal.approvalStatus.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = IndicatorNeutral,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun SignalAuditDetailDialog(
    signal: Signal,
    onDismiss: () -> Unit,
    onAnalyze: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm:ss UTC", Locale.US) }
    val publishedDate = remember(signal.creationTimestamp) { dateFormat.format(Date(signal.creationTimestamp)) }
    val resolvedDate = remember(signal.resolvedTimestamp) {
        signal.resolvedTimestamp?.let { dateFormat.format(Date(it)) } ?: "Pending Forward Resolution"
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(NavySurface)
                .border(1.dp, NavyBorder, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${signal.symbol} • ${signal.direction.label}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Signal ID: ${signal.signalId}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(NavyCard)
                        .border(1.dp, NavyBorder, RoundedCornerShape(10.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        AuditDetailRow("Approval Status", signal.approvalStatus.label, IndicatorNeutral)
                        AuditDetailRow("Outcome Status", signal.outcomeStatus.label, when (signal.outcomeStatus) {
                            SignalOutcomeStatus.PENDING -> TealAccent
                            SignalOutcomeStatus.STOPPED_OUT -> IndicatorSell
                            else -> IndicatorBuy
                        })
                        AuditDetailRow("Published Timestamp", publishedDate, TextSecondary)
                        AuditDetailRow("Resolution Timestamp", resolvedDate, TextSecondary)
                        AuditDetailRow("Original Entry", String.format("%.4f", signal.entryPrice), TextPrimary)
                        AuditDetailRow("Stop Loss (SL)", String.format("%.4f", signal.stopLoss), IndicatorSell)
                        AuditDetailRow("Take Profit 1 (TP1)", String.format("%.4f", signal.takeProfit1), IndicatorBuy)
                        AuditDetailRow("Take Profit 2 (TP2)", String.format("%.4f", signal.takeProfit2), IndicatorBuy)
                        AuditDetailRow("Take Profit 3 (TP3)", String.format("%.4f", signal.takeProfit3), IndicatorBuy)
                        AuditDetailRow("Risk:Reward (TP2)", signal.riskRewardText, TealAccent)
                        AuditDetailRow("Strategy Engine", signal.strategyVersion, TextMuted)
                        AuditDetailRow("Calculation Source", signal.source, TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Deterministic Rationale:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TealAccent,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = signal.plainLanguageExplanation,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    lineHeight = 16.sp,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onAnalyze,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = TealAccent, contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Open Interactive Technical Chart", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun StatColumn(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = TextMuted, fontSize = 9.sp)
        Text(text = value, style = MaterialTheme.typography.labelMedium, color = color, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun AuditLevel(label: String, value: String, color: Color) {
    Column {
        Text(label, fontSize = 8.sp, color = TextMuted, fontWeight = FontWeight.Bold)
        Text(value, fontSize = 11.sp, color = color, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun AuditDetailRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = TextMuted, fontSize = 10.sp)
        Text(value, style = MaterialTheme.typography.labelSmall, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}
