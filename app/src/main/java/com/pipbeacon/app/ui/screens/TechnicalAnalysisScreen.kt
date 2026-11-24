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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.pipbeacon.app.ui.components.SignalBadge
import com.pipbeacon.app.ui.components.TechnicalGauge
import com.pipbeacon.app.ui.theme.IndicatorBuy
import com.pipbeacon.app.ui.theme.IndicatorSell
import com.pipbeacon.app.ui.theme.NavyBackground
import com.pipbeacon.app.ui.theme.NavyBorder
import com.pipbeacon.app.ui.theme.NavyCard
import com.pipbeacon.app.ui.theme.NavySurface
import com.pipbeacon.app.ui.theme.TealAccent
import com.pipbeacon.app.ui.theme.TextMuted
import com.pipbeacon.app.ui.theme.TextPrimary
import com.pipbeacon.app.ui.theme.TextSecondary

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

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .testTag("technical_analysis_screen"),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onOpenCalculator(true) },
                containerColor = TealAccent,
                contentColor = Color.Black,
                modifier = Modifier.testTag("floating_pip_calculator_button")
            ) {
                Icon(Icons.Default.Calculate, contentDescription = "Pip Calculator")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(NavyBackground)
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Instrument Selector Chips
            item {
                Column {
                    Text(
                        text = "SELECT ASSET",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(symbols) { sym ->
                            val isSelected = selectedSymbol == sym
                            FilterChip(
                                selected = isSelected,
                                onClick = { onSelectSymbol(sym) },
                                label = { Text(sym, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
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
                }
            }

            // Timeframe Selector & Chart Controls
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Timeframe chips
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        TimeFrame.entries.forEach { tf ->
                            val isSelected = selectedTimeFrame == tf
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) TealAccent else NavySurface)
                                    .clickable { onSelectTimeFrame(tf) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = tf.label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else TextSecondary
                                )
                            }
                        }
                    }

                    // Chart Mode toggle: Candle vs Area
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OverlayChip(
                            label = if (isCandleChart) "Candles" else "Line",
                            isActive = isCandleChart,
                            onClick = onToggleChartType
                        )
                    }
                }
            }

            // Overlay controls row: SMA 20, EMA 50, Bollinger Bands
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

            // Candlestick Chart View
            item {
                InteractiveCandlestickChart(
                    candles = candles,
                    isCandleMode = isCandleChart,
                    showSma = showSmaOverlay,
                    showEma = showEmaOverlay,
                    showBollinger = showBollingerOverlay
                )
            }

            // Summary Gauge
            if (report != null) {
                item {
                    TechnicalGauge(
                        overallSignal = report.overallSignal,
                        buyCount = report.buyCount,
                        neutralCount = report.neutralCount,
                        sellCount = report.sellCount
                    )
                }

                // Oscillators Card
                item {
                    IndicatorsTableCard(
                        title = "OSCILLATORS & MOMENTUM",
                        items = report.oscillators
                    )
                }

                // Moving Averages Card
                item {
                    IndicatorsTableCard(
                        title = "MOVING AVERAGES",
                        items = report.movingAverages
                    )
                }

                // Pivot Points
                item {
                    PivotPointsCard(
                        standard = report.standardPivots,
                        fibonacci = report.fibonacciPivots
                    )
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
private fun OverlayChip(
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isActive) Color(0xFF1E293B) else NavySurface)
            .border(1.dp, if (isActive) TealAccent else NavyBorder, RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = if (isActive) TealAccent else TextMuted
        )
    }
}

@Composable
private fun IndicatorsTableCard(
    title: String,
    items: List<IndicatorItem>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NavyCard)
            .border(1.dp, NavyBorder, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = TealAccent,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            items.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = item.name, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                        Text(text = item.value, style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    }
                    SignalBadge(signal = item.signal)
                }
                if (index < items.size - 1) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0x1AFFFFFF))
                    )
                }
            }
        }
    }
}

@Composable
private fun PivotPointsCard(
    standard: PivotLevels?,
    fibonacci: PivotLevels?
) {
    if (standard == null && fibonacci == null) return

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NavyCard)
            .border(1.dp, NavyBorder, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "PIVOT LEVELS (FLOOR / FIBONACCI)",
                style = MaterialTheme.typography.labelSmall,
                color = TealAccent,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            if (standard != null) {
                Text(text = "Classic Floor Pivots:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                PivotRow("R3 / Resistance 3", standard.r3, IndicatorSell)
                PivotRow("R2 / Resistance 2", standard.r2, IndicatorSell)
                PivotRow("R1 / Resistance 1", standard.r1, IndicatorSell)
                PivotRow("Pivot Center (P)", standard.pivot, TealAccent)
                PivotRow("S1 / Support 1", standard.s1, IndicatorBuy)
                PivotRow("S2 / Support 2", standard.s2, IndicatorBuy)
                PivotRow("S3 / Support 3", standard.s3, IndicatorBuy)
            }

            if (fibonacci != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "Fibonacci Pivots (38.2% / 61.8%):", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                PivotRow("Fib R2", fibonacci.r2, IndicatorSell)
                PivotRow("Fib R1", fibonacci.r1, IndicatorSell)
                PivotRow("Fib Center", fibonacci.pivot, TealAccent)
                PivotRow("Fib S1", fibonacci.s1, IndicatorBuy)
                PivotRow("Fib S2", fibonacci.s2, IndicatorBuy)
            }
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
        Text(String.format("%.4f", level), style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold)
    }
}
