package com.pipbeacon.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pipbeacon.app.indicators.BollingerBandsCalculator
import com.pipbeacon.app.indicators.Candle
import com.pipbeacon.app.indicators.MovingAveragesCalculator
import com.pipbeacon.app.ui.theme.CandleBearish
import com.pipbeacon.app.ui.theme.CandleBullish
import com.pipbeacon.app.ui.theme.NavyBorder
import com.pipbeacon.app.ui.theme.NavyCard
import com.pipbeacon.app.ui.theme.TealAccent
import com.pipbeacon.app.ui.theme.TextMuted

@Composable
fun InteractiveCandlestickChart(
    candles: List<Candle>,
    isCandleMode: Boolean = true,
    showSma: Boolean = true,
    showEma: Boolean = true,
    showBollinger: Boolean = false,
    modifier: Modifier = Modifier
) {
    if (candles.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(NavyCard)
                .border(1.dp, NavyBorder, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No candle data available",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
        }
        return
    }

    val closes = candles.map { it.close }
    val smaSeries = if (showSma) MovingAveragesCalculator.calculateSma(closes, 20) else emptyList()
    val emaSeries = if (showEma) MovingAveragesCalculator.calculateEma(closes, 50) else emptyList()
    val bbSeries = if (showBollinger) BollingerBandsCalculator.calculate(closes, 20, 2.0) else emptyList()

    val minPrice = candles.minOf { it.low }
    val maxPrice = candles.maxOf { it.high }
    val priceRange = if (maxPrice - minPrice == 0.0) 1.0 else (maxPrice - minPrice) * 1.05

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(NavyCard)
            .border(1.dp, NavyBorder, RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val candleCount = candles.size
            val candleWidth = (width / candleCount) * 0.7f
            val candleStep = width / candleCount

            fun priceToY(price: Double): Float {
                val norm = ((price - minPrice) / priceRange).toFloat()
                return height - (norm * height)
            }

            // Draw grid lines
            val gridLines = 4
            for (g in 0..gridLines) {
                val y = height * (g.toFloat() / gridLines)
                drawLine(
                    color = Color(0x1FFFFFFF),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f
                )
            }

            // Draw Bollinger Bands channel if enabled
            if (showBollinger && bbSeries.isNotEmpty()) {
                val upperPath = Path()
                val lowerPath = Path()
                val offsetIdx = candleCount - bbSeries.size

                bbSeries.forEachIndexed { i, bb ->
                    val x = (offsetIdx + i) * candleStep + (candleStep / 2f)
                    val yUp = priceToY(bb.upperBand)
                    val yLow = priceToY(bb.lowerBand)
                    if (i == 0) {
                        upperPath.moveTo(x, yUp)
                        lowerPath.moveTo(x, yLow)
                    } else {
                        upperPath.lineTo(x, yUp)
                        lowerPath.lineTo(x, yLow)
                    }
                }
                drawPath(
                    path = upperPath,
                    color = Color(0xFF64B5F6).copy(alpha = 0.6f),
                    style = Stroke(width = 1.5.dp.toPx())
                )
                drawPath(
                    path = lowerPath,
                    color = Color(0xFF64B5F6).copy(alpha = 0.6f),
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }

            // Draw Candlesticks or Area Line
            if (isCandleMode) {
                candles.forEachIndexed { index, c ->
                    val x = index * candleStep + (candleStep / 2f)
                    val isUp = c.close >= c.open
                    val color = if (isUp) CandleBullish else CandleBearish

                    val yHigh = priceToY(c.high)
                    val yLow = priceToY(c.low)
                    val yOpen = priceToY(c.open)
                    val yClose = priceToY(c.close)

                    // Wick
                    drawLine(
                        color = color,
                        start = Offset(x, yHigh),
                        end = Offset(x, yLow),
                        strokeWidth = 1.5.dp.toPx()
                    )

                    // Body
                    val bodyTop = minOf(yOpen, yClose)
                    val bodyHeight = maxOf(Math.abs(yClose - yOpen), 2.5f)
                    drawRect(
                        color = color,
                        topLeft = Offset(x - (candleWidth / 2f), bodyTop),
                        size = Size(candleWidth, bodyHeight)
                    )
                }
            } else {
                val linePath = Path()
                candles.forEachIndexed { index, c ->
                    val x = index * candleStep + (candleStep / 2f)
                    val y = priceToY(c.close)
                    if (index == 0) linePath.moveTo(x, y) else linePath.lineTo(x, y)
                }
                drawPath(
                    path = linePath,
                    color = TealAccent,
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            // Draw SMA 20 Overlay (Teal)
            if (showSma && smaSeries.isNotEmpty()) {
                val smaPath = Path()
                var started = false
                smaSeries.forEachIndexed { i, valSma ->
                    if (!valSma.isNaN()) {
                        val x = i * candleStep + (candleStep / 2f)
                        val y = priceToY(valSma)
                        if (!started) {
                            smaPath.moveTo(x, y)
                            started = true
                        } else {
                            smaPath.lineTo(x, y)
                        }
                    }
                }
                drawPath(
                    path = smaPath,
                    color = TealAccent,
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            // Draw EMA 50 Overlay (Amber/Orange)
            if (showEma && emaSeries.isNotEmpty()) {
                val emaPath = Path()
                var started = false
                emaSeries.forEachIndexed { i, valEma ->
                    if (!valEma.isNaN()) {
                        val x = i * candleStep + (candleStep / 2f)
                        val y = priceToY(valEma)
                        if (!started) {
                            emaPath.moveTo(x, y)
                            started = true
                        } else {
                            emaPath.lineTo(x, y)
                        }
                    }
                }
                drawPath(
                    path = emaPath,
                    color = Color(0xFFFFB300),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }

        // Price Axis Badges
        Text(
            text = String.format("%.4f", maxPrice),
            color = TextMuted,
            fontSize = 10.sp,
            modifier = Modifier.align(Alignment.TopEnd)
        )
        Text(
            text = String.format("%.4f", minPrice),
            color = TextMuted,
            fontSize = 10.sp,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}
