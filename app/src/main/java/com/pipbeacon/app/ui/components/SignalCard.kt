package com.pipbeacon.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.CandlestickChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pipbeacon.app.data.SignalItem
import com.pipbeacon.app.ui.theme.GoldAccent
import com.pipbeacon.app.ui.theme.GoldAccentBright
import com.pipbeacon.app.ui.theme.IndicatorBuy
import com.pipbeacon.app.ui.theme.IndicatorBuyBackground
import com.pipbeacon.app.ui.theme.IndicatorSell
import com.pipbeacon.app.ui.theme.IndicatorSellBackground
import com.pipbeacon.app.ui.theme.NavyBorder
import com.pipbeacon.app.ui.theme.NavyCard
import com.pipbeacon.app.ui.theme.NavySurface
import com.pipbeacon.app.ui.theme.NavySurfaceVariant
import com.pipbeacon.app.ui.theme.TealAccent
import com.pipbeacon.app.ui.theme.TextMuted
import com.pipbeacon.app.ui.theme.TextPrimary
import com.pipbeacon.app.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Signal Row with inline tap-to-expand details, fulfilling Task 3:
 * "All Signals also should be shown as rows and it should expand details after tapping on it."
 */
@Composable
fun SignalCard(
    signal: SignalItem,
    onClick: () -> Unit = {},
    onReplayClick: (() -> Unit)? = null,
    initiallyExpanded: Boolean = false,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(initiallyExpanded) }
    val isBuy = signal.isBuy
    val accentColor = if (isBuy) IndicatorBuy else IndicatorSell
    val isClosed = signal.isClosed
    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "chevron_rotation"
    )

    Surface(
        color = NavyCard,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isExpanded) TealAccent.copy(alpha = 0.5f) else NavyBorder
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .testTag("signal_row_${signal.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            // === Top Compact Row (always visible, ~50dp) ===
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Direction Badge + Symbol + Timeframe
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1.3f)
                ) {
                    // BUY / SELL Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isBuy) IndicatorBuy else IndicatorSell)
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isBuy) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = if (isBuy) "BUY" else "SELL",
                                color = Color.Black,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = signal.symbol.replace("/", ""),
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            fontFamily = com.pipbeacon.app.ui.theme.JetBrainsMonoFont
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = signal.timeFrame.label,
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "• R:R ${signal.riskRewardRatio}",
                                color = GoldAccentBright,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Center: Potential Pips Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(IndicatorBuyBackground)
                        .border(1.dp, TealAccent.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "◎ ${signal.pipPotential}p",
                        color = TealAccent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Right: TPs Hit Badge + Chevron
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    if (signal.tpsHitCount > 0) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(IndicatorBuyBackground)
                                .border(1.dp, IndicatorBuy.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "✓ ${signal.tpsHitCount} TPs Hit",
                                color = IndicatorBuy,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isClosed) NavySurfaceVariant else IndicatorBuyBackground)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isClosed) "Closed" else "Live",
                                color = if (isClosed) TextMuted else IndicatorBuy,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand",
                        tint = if (isExpanded) TealAccent else TextMuted,
                        modifier = Modifier
                            .size(20.dp)
                            .rotate(chevronRotation)
                    )
                }
            }

            // === Expanded Details (Inline Accordion) ===
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
                    HorizontalDivider(
                        color = NavyBorder.copy(alpha = 0.6f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    // 1. Milestone Status Banner (Matching Screenshot 2)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(IndicatorBuyBackground)
                            .border(1.dp, IndicatorBuy.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(IndicatorBuy.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = IndicatorBuy,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (signal.tpsHitCount >= 2) "Take Profit 2 Reached" else if (signal.tpsHitCount == 1) "Take Profit 1 Reached" else "Signal Order Active",
                                    color = IndicatorBuy,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = if (signal.isBreakevenActive) "Stop loss at breakeven" else "Orders placed with rule-based safety buffer",
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 2. Take Profit Progress Stepper (Matching Screenshot 2)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(NavySurfaceVariant)
                            .border(1.dp, NavyBorder, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "Take Profit Progress",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StepperStep(step = "1", label = "TP1", isHit = signal.tpsHitCount >= 1)
                                StepperLine(isCompleted = signal.tpsHitCount >= 2, modifier = Modifier.weight(1f))
                                StepperStep(step = "2", label = "TP2", isHit = signal.tpsHitCount >= 2)
                                StepperLine(isCompleted = signal.tpsHitCount >= 3, modifier = Modifier.weight(1f))
                                StepperStep(step = "3", label = "TP3", isHit = signal.tpsHitCount >= 3)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 3. Trade Parameters Grid
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(NavySurfaceVariant)
                            .border(1.dp, NavyBorder, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "Trade Parameters",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Entry & SL
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(text = "Entry Price", color = TextMuted, fontSize = 9.sp)
                                    Text(
                                        text = formatPriceValue(signal.entryPrice),
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = if (signal.isBreakevenActive) "Stop Loss (B.E. Active)" else "Stop Loss",
                                        color = if (signal.isBreakevenActive) GoldAccentBright else IndicatorSell,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = formatPriceValue(signal.stopLoss),
                                        color = if (signal.isBreakevenActive) GoldAccentBright else IndicatorSell,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Take Profit Target Boxes
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                TpBox(
                                    label = "TP1",
                                    price = formatPriceValue(signal.takeProfit1),
                                    isHit = signal.tpsHitCount >= 1,
                                    modifier = Modifier.weight(1f)
                                )
                                signal.takeProfit2?.let { tp2 ->
                                    TpBox(
                                        label = "TP2",
                                        price = formatPriceValue(tp2),
                                        isHit = signal.tpsHitCount >= 2,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                signal.takeProfit3?.let { tp3 ->
                                    TpBox(
                                        label = "TP3",
                                        price = formatPriceValue(tp3),
                                        isHit = signal.tpsHitCount >= 3,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 4. Indicator Reasoning & Notes
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(NavySurfaceVariant)
                            .padding(8.dp)
                    ) {
                        Column {
                            Text(
                                text = "Technical Confluence Notes",
                                color = GoldAccentBright,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = signal.indicatorReasoning,
                                color = TextSecondary,
                                fontSize = 10.sp,
                                lineHeight = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${formatCardTimestamp(signal.timestamp)} • Strategy: ${signal.strategyVersion}",
                                color = TextMuted,
                                fontSize = 8.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 5. Action Buttons (Chart & Copy)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val copyText = buildString {
                                    appendLine("[PipBeacon Signal: ${signal.symbol}]")
                                    appendLine("Direction: ${if (isBuy) "BUY" else "SELL"}")
                                    appendLine("Timeframe: ${signal.timeFrame.label}")
                                    appendLine("Entry: ${formatPriceValue(signal.entryPrice)}")
                                    appendLine("Stop Loss: ${formatPriceValue(signal.stopLoss)}")
                                    appendLine("TP1: ${formatPriceValue(signal.takeProfit1)}")
                                    signal.takeProfit2?.let { appendLine("TP2: ${formatPriceValue(it)}") }
                                    signal.takeProfit3?.let { appendLine("TP3: ${formatPriceValue(it)}") }
                                    appendLine("R:R: ${signal.riskRewardRatio}")
                                }
                                clipboardManager.setText(AnnotatedString(copyText))
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = NavySurfaceVariant),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy Setup", color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = { onReplayClick?.invoke() ?: onClick() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.CandlestickChart, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Open Graph", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepperStep(step: String, label: String, isHit: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (isHit) IndicatorBuy else NavySurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (isHit) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(13.dp)
                )
            } else {
                Text(
                    text = step,
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (isHit) IndicatorBuy else TextMuted,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun StepperLine(isCompleted: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(2.dp)
            .padding(horizontal = 4.dp)
            .background(if (isCompleted) IndicatorBuy else NavyBorder)
    )
}

@Composable
private fun TpBox(
    label: String,
    price: String,
    isHit: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isHit) IndicatorBuyBackground else NavySurfaceVariant)
            .border(
                1.dp,
                if (isHit) IndicatorBuy.copy(alpha = 0.5f) else NavyBorder,
                RoundedCornerShape(6.dp)
            )
            .padding(vertical = 5.dp, horizontal = 4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "$label ${if (isHit) "✓" else ""}",
                color = if (isHit) IndicatorBuy else TextMuted,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = price,
                color = if (isHit) IndicatorBuy else TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

private fun formatPriceValue(price: Double?): String {
    if (price == null) return "—"
    return if (price >= 1000) {
        String.format(Locale.US, "%,.0f", price)
    } else if (price >= 10) {
        String.format(Locale.US, "%.2f", price)
    } else {
        String.format(Locale.US, "%.4f", price)
    }
}

private fun formatCardTimestamp(millis: Long): String {
    val sdf = SimpleDateFormat("MMM d, h:mm a", Locale.US)
    return sdf.format(Date(millis))
}
