package com.pipbeacon.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CandlestickChart
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pipbeacon.app.core.TimeFrame
import com.pipbeacon.app.domain.PipCalculationResult
import com.pipbeacon.app.indicators.Candle
import com.pipbeacon.app.indicators.IndicatorItem
import com.pipbeacon.app.indicators.PivotLevels
import com.pipbeacon.app.indicators.TechnicalAnalysisReport
import com.pipbeacon.app.ui.components.InteractiveCandlestickChart
import com.pipbeacon.app.ui.components.PipCalculatorDialog
import com.pipbeacon.app.ui.components.ShowcaseBackground
import com.pipbeacon.app.ui.components.SignalBadge
import com.pipbeacon.app.ui.components.TechnicalGauge
import com.pipbeacon.app.ui.theme.CyanAccent
import com.pipbeacon.app.ui.theme.GoldAccentBright
import com.pipbeacon.app.ui.theme.IndicatorBuy
import com.pipbeacon.app.ui.theme.IndicatorSell
import com.pipbeacon.app.ui.theme.JetBrainsMonoFont
import com.pipbeacon.app.ui.theme.NavyBorder
import com.pipbeacon.app.ui.theme.NavyBorderSubtle
import com.pipbeacon.app.ui.theme.NavyCard
import com.pipbeacon.app.ui.theme.NavySurface
import com.pipbeacon.app.ui.theme.NavySurfaceVariant
import com.pipbeacon.app.ui.theme.TextMuted
import com.pipbeacon.app.ui.theme.TextPrimary
import com.pipbeacon.app.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun TechnicalAnalysisScreen(
    selectedSymbol: String,
    selectedTimeFrame: TimeFrame,
    isCandleChart: Boolean,
    showSmaOverlay: Boolean,
    showEmaOverlay: Boolean,
    showBollingerOverlay: Boolean,
    candles: List<Candle>,
    report: TechnicalAnalysisReport?,
    showPipCalculator: Boolean,
    calcEntry: String,
    calcSl: String,
    calcTp: String,
    calcBalance: String,
    calcRisk: String,
    calcLots: String,
    calcResult: PipCalculationResult?,
    onSelectSymbol: (String) -> Unit,
    onSelectTimeFrame: (TimeFrame) -> Unit,
    onToggleChartType: () -> Unit,
    onToggleSma: () -> Unit,
    onToggleEma: () -> Unit,
    onToggleBollinger: () -> Unit,
    onOpenCalculator: (Boolean) -> Unit,
    onUpdateCalcEntry: (String) -> Unit,
    onUpdateCalcSl: (String) -> Unit,
    onUpdateCalcTp: (String) -> Unit,
    onUpdateCalcBalance: (String) -> Unit,
    onUpdateCalcRisk: (String) -> Unit,
    onUpdateCalcLots: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val symbols = listOf("EUR/USD", "GBP/USD", "USD/JPY", "BTC/USDT", "ETH/USDT", "SOL/USDT", "USD/CHF")
    var expandedIndicator by remember { mutableStateOf<String?>("MA") }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("technical_analysis_screen"),
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onOpenCalculator(true) },
                containerColor = GoldAccentBright,
                contentColor = Color(0xFF060D1A),
                modifier = Modifier.testTag("floating_pip_calculator_button")
            ) {
                Icon(Icons.Default.Calculate, contentDescription = "Pip Calculator")
            }
        }
    ) { innerPadding ->
        ShowcaseBackground(modifier = Modifier.padding(innerPadding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Showcase Pair Selector Header Row
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Chart Analysis",
                                color = TextPrimary,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(NavyCard)
                                    .border(1.dp, NavyBorder, RoundedCornerShape(10.dp))
                                    .clickable {
                                        val nextIdx = (symbols.indexOf(selectedSymbol) + 1) % symbols.size
                                        onSelectSymbol(symbols[nextIdx])
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = selectedSymbol,
                                        color = CyanAccent,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Cycle Pair",
                                        tint = CyanAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(symbols) { sym ->
                                val isSelected = selectedSymbol == sym
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (isSelected) GoldAccentBright else NavyCard)
                                        .border(
                                            1.dp,
                                            if (isSelected) GoldAccentBright else NavyBorder,
                                            RoundedCornerShape(20.dp)
                                        )
                                        .clickable { onSelectSymbol(sym) }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = sym,
                                        color = if (isSelected) Color(0xFF060D1A) else TextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Timeframe Bar & Chart Mode Selector
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            TimeFrame.entries.forEach { tf ->
                                val isSelected = selectedTimeFrame == tf
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) CyanAccent else NavyCard)
                                        .border(
                                            1.dp,
                                            if (isSelected) CyanAccent else NavyBorder,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { onSelectTimeFrame(tf) }
                                        .padding(horizontal = 11.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = tf.label,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color(0xFF060D1A) else TextSecondary
                                    )
                                }
                            }
                        }

                        OverlayChip(
                            label = if (isCandleChart) "Candles" else "Line",
                            isActive = isCandleChart,
                            onClick = onToggleChartType
                        )
                    }
                }

                // Overlay controls row
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OverlayChip("SMA 20", showSmaOverlay, onToggleSma)
                        OverlayChip("EMA 50", showEmaOverlay, onToggleEma)
                        OverlayChip("Bollinger Bands", showBollingerOverlay, onToggleBollinger)
                    }
                }

                // Candlestick Chart
                item {
                    InteractiveCandlestickChart(
                        candles = candles,
                        isCandleMode = isCandleChart,
                        showSma = showSmaOverlay,
                        showEma = showEmaOverlay,
                        showBollinger = showBollingerOverlay
                    )
                }

                // Showcase Bottom Chart Action Strip
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
                                .padding(vertical = 8.dp, horizontal = 6.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ChartQuickAction("Indicators", Icons.Default.Insights, expandedIndicator != null) {
                                expandedIndicator = if (expandedIndicator == null) "MA" else null
                            }
                            ChartQuickAction(
                                if (isCandleChart) "Candles" else "Line",
                                Icons.Default.CandlestickChart,
                                true,
                                onToggleChartType
                            )
                            ChartQuickAction("Overlays", Icons.Default.Layers, showSmaOverlay || showEmaOverlay) {
                                onToggleSma()
                            }
                            ChartQuickAction("Pip Calc", Icons.Default.Calculate, false) {
                                onOpenCalculator(true)
                            }
                        }
                    }
                }

                // Showcase Indicators Hub (collapsible icon-badged rows matching Showcase Technical Indicators screen)
                if (report != null) {
                    item {
                        Text(
                            text = "Indicators",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    val rsiItem = report.oscillators.firstOrNull { it.name.contains("RSI", ignoreCase = true) }
                    val macdItem = report.oscillators.firstOrNull { it.name.contains("MACD", ignoreCase = true) }
                    val bbUpperLower = if (showBollingerOverlay) "Band Overlay Active" else "20-Period StdDev 2.0"

                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            ShowcaseIndicatorCard(
                                title = "Moving Average",
                                subtitle = "(SMA, EMA)",
                                icon = Icons.AutoMirrored.Filled.ShowChart,
                                tint = CyanAccent,
                                badgeSignal = report.movingAverages.firstOrNull()?.signal,
                                expanded = expandedIndicator == "MA",
                                onClick = { expandedIndicator = if (expandedIndicator == "MA") null else "MA" }
                            ) {
                                report.movingAverages.forEach { item ->
                                    IndicatorDetailLine(item)
                                }
                            }

                            ShowcaseIndicatorCard(
                                title = "RSI",
                                subtitle = "(Relative Strength Index)",
                                icon = Icons.Default.Speed,
                                tint = GoldAccentBright,
                                badgeSignal = rsiItem?.signal,
                                expanded = expandedIndicator == "RSI",
                                onClick = { expandedIndicator = if (expandedIndicator == "RSI") null else "RSI" }
                            ) {
                                report.oscillators.filter { !it.name.contains("MACD", ignoreCase = true) }.forEach { item ->
                                    IndicatorDetailLine(item)
                                }
                            }

                            ShowcaseIndicatorCard(
                                title = "MACD",
                                subtitle = "(Moving Average Convergence Divergence)",
                                icon = Icons.Default.Analytics,
                                tint = CyanAccent,
                                badgeSignal = macdItem?.signal,
                                expanded = expandedIndicator == "MACD",
                                onClick = { expandedIndicator = if (expandedIndicator == "MACD") null else "MACD" }
                            ) {
                                if (macdItem != null) {
                                    IndicatorDetailLine(macdItem)
                                } else {
                                    Text(
                                        text = "Momentum histogram calculated over 12/26/9 closing periods.",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            ShowcaseIndicatorCard(
                                title = "Bollinger Bands",
                                subtitle = bbUpperLower,
                                icon = Icons.Default.Tune,
                                tint = GoldAccentBright,
                                badgeSignal = null,
                                expanded = expandedIndicator == "BB",
                                onClick = {
                                    onToggleBollinger()
                                    expandedIndicator = if (expandedIndicator == "BB") null else "BB"
                                }
                            ) {
                                Text(
                                    text = "Upper and lower volatility channels plotted 2 standard deviations from the 20-period SMA. Tap to toggle band overlay on chart.",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )
                            }

                            ShowcaseIndicatorCard(
                                title = "Fibonacci Retracement",
                                subtitle = "38.2% • 50.0% • 61.8% Key Levels",
                                icon = Icons.Default.Layers,
                                tint = CyanAccent,
                                badgeSignal = null,
                                expanded = expandedIndicator == "FIB",
                                onClick = { expandedIndicator = if (expandedIndicator == "FIB") null else "FIB" }
                            ) {
                                PivotPointsCard(
                                    standard = report.standardPivots,
                                    fibonacci = report.fibonacciPivots
                                )
                            }
                        }
                    }

                    // Summary Gauge
                    item {
                        TechnicalGauge(
                            overallSignal = report.overallSignal,
                            buyCount = report.buyCount,
                            neutralCount = report.neutralCount,
                            sellCount = report.sellCount
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(60.dp))
                }
            }
        }
    }

    if (showPipCalculator) {
        PipCalculatorDialog(
            symbol = selectedSymbol,
            entryPrice = calcEntry,
            stopLoss = calcSl,
            takeProfit = calcTp,
            accountBalance = calcBalance,
            riskPercent = calcRisk,
            lotSize = calcLots,
            result = calcResult,
            onEntryChange = onUpdateCalcEntry,
            onStopLossChange = onUpdateCalcSl,
            onTakeProfitChange = onUpdateCalcTp,
            onBalanceChange = onUpdateCalcBalance,
            onRiskChange = onUpdateCalcRisk,
            onLotSizeChange = onUpdateCalcLots,
            onDismiss = { onOpenCalculator(false) }
        )
    }
}

@Composable
private fun ChartQuickAction(
    label: String,
    icon: ImageVector,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isActive) GoldAccentBright else TextMuted,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (isActive) GoldAccentBright else TextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun ShowcaseIndicatorCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    badgeSignal: com.pipbeacon.app.core.SignalType?,
    expanded: Boolean,
    onClick: () -> Unit,
    expandedContent: @Composable () -> Unit
) {
    Surface(
        color = NavyCard,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, if (expanded) tint.copy(alpha = 0.55f) else NavyBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(tint.copy(alpha = 0.14f))
                            .border(1.dp, tint.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = tint,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = title,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = subtitle,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (badgeSignal != null) {
                        SignalBadge(signal = badgeSignal)
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowDown
                        else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Expand $title",
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(NavyBorderSubtle)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    expandedContent()
                }
            }
        }
    }
}

@Composable
private fun IndicatorDetailLine(item: IndicatorItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.name, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(text = item.value, color = TextMuted, fontSize = 11.sp, fontFamily = JetBrainsMonoFont)
        }
        SignalBadge(signal = item.signal)
    }
}

@Composable
private fun OverlayChip(
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isActive) CyanAccent.copy(alpha = 0.16f) else NavyCard)
            .border(1.dp, if (isActive) CyanAccent else NavyBorder, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isActive) CyanAccent else TextMuted
        )
    }
}

@Composable
private fun PivotPointsCard(
    standard: PivotLevels?,
    fibonacci: PivotLevels?
) {
    if (standard == null && fibonacci == null) return

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        if (standard != null) {
            Text(text = "Classic Floor Pivots:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            PivotRow("R3 / Resistance 3", standard.r3, IndicatorSell)
            PivotRow("R2 / Resistance 2", standard.r2, IndicatorSell)
            PivotRow("R1 / Resistance 1", standard.r1, IndicatorSell)
            PivotRow("Pivot Center (P)", standard.pivot, CyanAccent)
            PivotRow("S1 / Support 1", standard.s1, IndicatorBuy)
            PivotRow("S2 / Support 2", standard.s2, IndicatorBuy)
            PivotRow("S3 / Support 3", standard.s3, IndicatorBuy)
        }

        if (fibonacci != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Fibonacci Pivots (38.2% / 61.8%):", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            PivotRow("Fib R2", fibonacci.r2, IndicatorSell)
            PivotRow("Fib R1", fibonacci.r1, IndicatorSell)
            PivotRow("Fib Center", fibonacci.pivot, CyanAccent)
            PivotRow("Fib S1", fibonacci.s1, IndicatorBuy)
            PivotRow("Fib S2", fibonacci.s2, IndicatorBuy)
        }
    }
}

@Composable
private fun PivotRow(name: String, level: Double, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(name, style = MaterialTheme.typography.bodySmall, color = TextMuted)
        Text(
            String.format(Locale.US, "%.4f", level),
            fontFamily = JetBrainsMonoFont,
            fontSize = 11.sp,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}
