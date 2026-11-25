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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.pipbeacon.app.ui.components.ShowcaseBackground
import com.pipbeacon.app.ui.components.ShowcaseGoldButton
import com.pipbeacon.app.ui.theme.CyanAccent
import com.pipbeacon.app.ui.theme.GoldAccentBright
import com.pipbeacon.app.ui.theme.IndicatorBuy
import com.pipbeacon.app.ui.theme.IndicatorNeutral
import com.pipbeacon.app.ui.theme.IndicatorNeutralBackground
import com.pipbeacon.app.ui.theme.IndicatorSell
import com.pipbeacon.app.ui.theme.JetBrainsMonoFont
import com.pipbeacon.app.ui.theme.NavyBorder
import com.pipbeacon.app.ui.theme.NavyCard
import com.pipbeacon.app.ui.theme.NavySurface
import com.pipbeacon.app.ui.theme.NavySurfaceVariant
import com.pipbeacon.app.ui.theme.TextMuted
import com.pipbeacon.app.ui.theme.TextPrimary
import com.pipbeacon.app.ui.theme.TextSecondary

private data class JournalEntry(
    val id: String,
    val symbol: String,
    val direction: String,
    val pips: Int,
    val dateLabel: String,
    val notes: String,
    val isCrypto: Boolean
)

@Composable
fun TradingJournalScreen(
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var entries by remember {
        mutableStateOf(
            listOf(
                JournalEntry("j1", "EUR/USD", "BUY", 45, "Apr 24, 2025", "London open bounce off EMA 50 support.", false),
                JournalEntry("j2", "BTC/USDT", "BUY", 120, "Apr 23, 2025", "Breakout above 66.8k consolidation box.", true),
                JournalEntry("j3", "GBP/USD", "SELL", -18, "Apr 22, 2025", "Stopped out during NY macro volatility.", false),
                JournalEntry("j4", "USD/JPY", "BUY", 62, "Apr 20, 2025", "Trend continuation after RSI pullback to 52.", false)
            )
        )
    }
    var showAddDialog by remember { mutableStateOf(false) }

    val netPips = entries.sumOf { it.pips }
    val winCount = entries.count { it.pips > 0 }
    val winPct = if (entries.isEmpty()) 0 else (winCount * 100) / entries.size

    ShowcaseBackground(modifier = modifier.testTag("trading_journal_screen")) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NavySurface)
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onBack != null) {
                    IconButton(onClick = onBack, modifier = Modifier.size(38.dp)) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Column {
                    Text(
                        text = "Trading Journal",
                        color = TextPrimary,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Practice Trade Log (Offline Demo Notebook)",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Educational Notice
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(IndicatorNeutralBackground)
                            .border(1.dp, IndicatorNeutral.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Offline Practice Notebook • Sample & Local Study Logs Only • No Live Brokerage Sync",
                            color = IndicatorNeutral,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Summary Strip
                item {
                    Surface(
                        color = NavyCard,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, NavyBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            JournalStatColumn("Logged Trades", "${entries.size}", TextPrimary)
                            JournalStatColumn("Demo Win Rate", "$winPct%", GoldAccentBright)
                            JournalStatColumn(
                                "Net Demo Pips",
                                "${if (netPips >= 0) "+" else ""}$netPips p",
                                if (netPips >= 0) IndicatorBuy else IndicatorSell
                            )
                        }
                    }
                }

                items(entries, key = { it.id }) { entry ->
                    val isProfit = entry.pips >= 0
                    val pipColor = if (isProfit) IndicatorBuy else IndicatorSell
                    val badgeTint = if (entry.isCrypto) GoldAccentBright else IndicatorBuy

                    Surface(
                        color = NavyCard,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, NavyBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(badgeTint.copy(alpha = 0.16f))
                                        .border(1.dp, badgeTint.copy(alpha = 0.4f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (entry.isCrypto) Icons.Default.CurrencyBitcoin
                                        else Icons.Default.CurrencyExchange,
                                        contentDescription = null,
                                        tint = badgeTint,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = entry.symbol,
                                            color = TextPrimary,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "${if (isProfit) "+" else ""}${entry.pips} pips",
                                            color = pipColor,
                                            fontFamily = JetBrainsMonoFont,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text = "${entry.direction} • ${entry.dateLabel}",
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                    if (entry.notes.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = entry.notes,
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }

                            Icon(
                                imageVector = if (isProfit) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                                contentDescription = if (isProfit) "Profit" else "Loss",
                                tint = pipColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            // Bottom Gold Add Trade CTA
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                ShowcaseGoldButton(
                    text = "Add Trade",
                    onClick = { showAddDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_journal_trade_button")
                )
            }
        }
    }

    if (showAddDialog) {
        AddJournalTradeDialog(
            onDismiss = { showAddDialog = false },
            onSave = { symbol, direction, pips, notes ->
                val newEntry = JournalEntry(
                    id = "j_${System.currentTimeMillis()}",
                    symbol = symbol.uppercase(),
                    direction = direction,
                    pips = pips,
                    dateLabel = "Today",
                    notes = notes,
                    isCrypto = symbol.contains("BTC", ignoreCase = true) || symbol.contains("ETH", ignoreCase = true) || symbol.contains("SOL", ignoreCase = true)
                )
                entries = listOf(newEntry) + entries
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun JournalStatColumn(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = TextMuted, fontSize = 10.sp)
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = value,
            color = color,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = JetBrainsMonoFont
        )
    }
}

@Composable
private fun AddJournalTradeDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, Int, String) -> Unit
) {
    var symbol by remember { mutableStateOf("EUR/USD") }
    var direction by remember { mutableStateOf("BUY") }
    var pipsText by remember { mutableStateOf("35") }
    var notes by remember { mutableStateOf("Practice setup logged on H1 support.") }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(NavySurface)
                .border(1.dp, NavyBorder, RoundedCornerShape(18.dp))
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Log Practice Trade",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = symbol,
                    onValueChange = { symbol = it },
                    label = { Text("Pair (e.g. EUR/USD, BTC/USDT)", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldAccentBright,
                        unfocusedBorderColor = NavyBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = NavySurfaceVariant,
                        unfocusedContainerColor = NavySurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("BUY", "SELL").forEach { dir ->
                        val isSel = direction == dir
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSel && dir == "BUY") IndicatorBuy
                                    else if (isSel) IndicatorSell
                                    else NavyCard
                                )
                                .border(1.dp, NavyBorder, RoundedCornerShape(10.dp))
                                .clickable { direction = dir }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dir,
                                color = if (isSel) Color(0xFF060D1A) else TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = pipsText,
                    onValueChange = { pipsText = it },
                    label = { Text("Result (+/- Pips)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldAccentBright,
                        unfocusedBorderColor = NavyBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = NavySurfaceVariant,
                        unfocusedContainerColor = NavySurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Setup Notes", fontSize = 11.sp) },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldAccentBright,
                        unfocusedBorderColor = NavyBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = NavySurfaceVariant,
                        unfocusedContainerColor = NavySurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                ShowcaseGoldButton(
                    text = "Save Entry",
                    onClick = {
                        val parsedPips = pipsText.toIntOrNull() ?: 0
                        onSave(symbol.ifBlank { "EUR/USD" }, direction, parsedPips, notes)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
