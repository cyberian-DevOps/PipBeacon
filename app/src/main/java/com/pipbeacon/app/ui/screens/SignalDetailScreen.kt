package com.pipbeacon.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.window.DialogProperties
import com.pipbeacon.app.data.SignalItem
import com.pipbeacon.app.ui.components.ShowcaseBackground
import com.pipbeacon.app.ui.components.ShowcaseCandlestickStrip
import com.pipbeacon.app.ui.components.ShowcaseGoldButton
import com.pipbeacon.app.ui.theme.CyanAccent
import com.pipbeacon.app.ui.theme.GoldAccent
import com.pipbeacon.app.ui.theme.GoldAccentBright
import com.pipbeacon.app.ui.theme.IndicatorBuy
import com.pipbeacon.app.ui.theme.IndicatorBuyBackground
import com.pipbeacon.app.ui.theme.IndicatorSell
import com.pipbeacon.app.ui.theme.IndicatorSellBackground
import com.pipbeacon.app.ui.theme.JetBrainsMonoFont
import com.pipbeacon.app.ui.theme.NavyBackground
import com.pipbeacon.app.ui.theme.NavyBorder
import com.pipbeacon.app.ui.theme.NavyCard
import com.pipbeacon.app.ui.theme.NavyCardElevated
import com.pipbeacon.app.ui.theme.NavySurfaceVariant
import com.pipbeacon.app.ui.theme.TextMuted
import com.pipbeacon.app.ui.theme.TextPrimary
import com.pipbeacon.app.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun SignalDetailScreen(
    signal: SignalItem,
    onBack: () -> Unit,
    onAnalyzeSymbol: (String) -> Unit
) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val isBuy = signal.isBuy
    val isClosed = signal.isClosed
    val directionColor = if (isBuy) IndicatorBuy else IndicatorSell
    val directionBg = if (isBuy) IndicatorBuyBackground else IndicatorSellBackground

    ShowcaseBackground(modifier = Modifier.testTag("signal_detail_screen")) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Top Nav Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Signal Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(NavyCard)
                        .border(1.dp, NavyBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isClosed) "Closed" else "${signal.timeFrame.label} • ${signal.confidencePercent}%",
                        color = GoldAccentBright,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Centered Showcase Header Pill: "BUY Signal - EUR/USD"
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(20.dp))
                    .background(directionBg)
                    .border(1.dp, directionColor.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 7.dp)
            ) {
                Text(
                    text = "${if (isBuy) "BUY" else "SELL"} Signal - ${signal.symbol}",
                    color = directionColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Candlestick Preview Chart Container
            Surface(
                color = NavySurfaceVariant,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, NavyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                ShowcaseCandlestickStrip(
                    isBullishBias = isBuy,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(128.dp)
                        .padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Milestone Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(IndicatorBuyBackground)
                    .border(1.dp, IndicatorBuy.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(IndicatorBuy.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = IndicatorBuy,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (signal.tpsHitCount >= 2) "Take Profit 2 Reached"
                            else if (signal.tpsHitCount == 1) "Take Profit 1 Reached"
                            else "Signal Active",
                            color = IndicatorBuy,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (signal.isBreakevenActive) "Stop loss at breakeven" else "Educational setup with structured TP & SL levels",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Take Profit Stepper
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(NavyCard)
                    .border(1.dp, NavyBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TrackChanges, contentDescription = null, tint = GoldAccentBright, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Take Profit Progress",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StepperNode(stepNumber = 1, label = "TP1", isCompleted = signal.tpsHitCount >= 1)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(2.dp)
                                .background(if (signal.tpsHitCount >= 1) IndicatorBuy else NavyBorder)
                        )
                        StepperNode(stepNumber = 2, label = "TP2", isCompleted = signal.tpsHitCount >= 2)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(2.dp)
                                .background(if (signal.tpsHitCount >= 2) IndicatorBuy else NavyBorder)
                        )
                        StepperNode(stepNumber = 3, label = "TP3", isCompleted = signal.tpsHitCount >= 3)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Entry, Take Profit, Stop Loss Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(NavyCard)
                    .border(1.dp, NavyBorder, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Entry:", color = TextSecondary, fontSize = 13.sp)
                        Text(
                            text = formatPrice(signal.entryPrice),
                            color = TextPrimary,
                            fontFamily = JetBrainsMonoFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Take Profit:", color = TextSecondary, fontSize = 13.sp)
                        Text(
                            text = formatPrice(signal.takeProfit1),
                            color = IndicatorBuy,
                            fontFamily = JetBrainsMonoFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Stop Loss:", color = TextSecondary, fontSize = 13.sp)
                        Text(
                            text = formatPrice(signal.stopLoss),
                            color = IndicatorSell,
                            fontFamily = JetBrainsMonoFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TpTargetBox("TP1", formatPrice(signal.takeProfit1), signal.tpsHitCount >= 1, Modifier.weight(1f))
                        TpTargetBox("TP2", formatPrice(signal.takeProfit2), signal.tpsHitCount >= 2, Modifier.weight(1f))
                        signal.takeProfit3?.let { tp3 ->
                            TpTargetBox("TP3", formatPrice(tp3), signal.tpsHitCount >= 3, Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Analysis Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(NavyCard)
                    .border(1.dp, NavyBorder, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "Analysis",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = signal.indicatorReasoning,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Strategy: ${signal.strategyVersion}", color = TextMuted, fontSize = 10.sp)
                        Text("R:R ${signal.riskRewardRatio}", color = GoldAccentBright, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        val copyText = buildString {
                            appendLine("[PipBeacon Signal: ${signal.symbol}]")
                            appendLine("Direction: ${if (isBuy) "BUY" else "SELL"}")
                            appendLine("Entry: ${formatPrice(signal.entryPrice)}")
                            appendLine("Stop Loss: ${formatPrice(signal.stopLoss)}")
                            appendLine("TP1: ${formatPrice(signal.takeProfit1)}")
                            appendLine("TP2: ${formatPrice(signal.takeProfit2)}")
                            signal.takeProfit3?.let { appendLine("TP3: ${formatPrice(it)}") }
                        }
                        clipboardManager.setText(AnnotatedString(copyText))
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyCardElevated),
                    border = BorderStroke(1.dp, NavyBorder),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy Setup", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                ShowcaseGoldButton(
                    text = "Analyze Chart",
                    onClick = { onAnalyzeSymbol(signal.symbol) },
                    height = 48.dp,
                    modifier = Modifier.weight(1.2f)
                )
            }
        }
    }
}

@Composable
private fun StepperNode(
    stepNumber: Int,
    label: String,
    isCompleted: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(if (isCompleted) IndicatorBuy else NavySurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color(0xFF060D1A),
                    modifier = Modifier.size(14.dp)
                )
            } else {
                Text(
                    text = "$stepNumber",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = if (isCompleted) IndicatorBuy else TextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun TpTargetBox(
    label: String,
    price: String,
    isHit: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isHit) IndicatorBuyBackground else NavySurfaceVariant)
            .border(
                1.dp,
                if (isHit) IndicatorBuy.copy(alpha = 0.5f) else NavyBorder,
                RoundedCornerShape(10.dp)
            )
            .padding(vertical = 8.dp, horizontal = 6.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = if (isHit) "$label ✓" else label,
                color = if (isHit) IndicatorBuy else TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = price,
                color = if (isHit) IndicatorBuy else TextPrimary,
                fontFamily = JetBrainsMonoFont,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SignalDetailDialog(
    signal: SignalItem,
    onDismiss: () -> Unit,
    onAnalyzeSymbol: (String) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, NavyBorder, RoundedCornerShape(18.dp))
                .testTag("signal_detail_dialog"),
            color = NavyBackground
        ) {
            SignalDetailScreen(
                signal = signal,
                onBack = onDismiss,
                onAnalyzeSymbol = {
                    onDismiss()
                    onAnalyzeSymbol(it)
                }
            )
        }
    }
}

private fun formatPrice(price: Double?): String {
    if (price == null) return "--"
    return when {
        price >= 1000.0 -> String.format(Locale.US, "%,.0f", price)
        price >= 100.0 -> String.format(Locale.US, "%.2f", price)
        else -> String.format(Locale.US, "%.4f", price)
    }
}
