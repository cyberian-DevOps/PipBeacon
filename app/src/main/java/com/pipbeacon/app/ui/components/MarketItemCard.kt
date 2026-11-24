package com.pipbeacon.app.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pipbeacon.app.data.MarketItem
import com.pipbeacon.app.ui.theme.IndicatorBuy
import com.pipbeacon.app.ui.theme.IndicatorNeutral
import com.pipbeacon.app.ui.theme.IndicatorSell
import com.pipbeacon.app.ui.theme.NavyBorder
import com.pipbeacon.app.ui.theme.NavyCard
import com.pipbeacon.app.ui.theme.TealAccent
import com.pipbeacon.app.ui.theme.TextMuted
import com.pipbeacon.app.ui.theme.TextPrimary
import com.pipbeacon.app.ui.theme.TextSecondary

@Composable
fun MarketItemCard(
    item: MarketItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NavyCard)
            .border(1.dp, NavyBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag("market_item_${item.id}")
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Symbol & Category
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.symbol,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF1E293B))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = item.category,
                            color = TealAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Buy / Sell Quick Signal
                SignalBadge(signal = item.signalSummary)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.name,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Price section
                Column {
                    if (item.isProviderLive && item.currentPrice != null) {
                        val formattedPrice = if (item.currentPrice < 10) {
                            String.format("%.4f", item.currentPrice)
                        } else {
                            String.format("%,.2f", item.currentPrice)
                        }
                        Text(
                            text = formattedPrice,
                            style = MaterialTheme.typography.labelMedium,
                            fontSize = 17.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        if (item.pipSpread != null) {
                            Text(
                                text = "Spread: ${String.format("%.1f", item.pipSpread)} pips",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF1B2333))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Waiting for permitted data provider",
                                color = IndicatorNeutral,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // 24h change & Sparkline
                if (item.isProviderLive && item.changePercent24h != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (item.sparkline.isNotEmpty()) {
                            SparklinePreview(
                                points = item.sparkline,
                                isPositive = item.changePercent24h >= 0,
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(26.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        val isUp = item.changePercent24h >= 0
                        val badgeColor = if (isUp) IndicatorBuy else IndicatorSell
                        val sign = if (isUp) "+" else ""

                        Text(
                            text = "$sign${String.format("%.2f", item.changePercent24h)}%",
                            color = badgeColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            // Provider Attribution, Quote Timestamp & Status (Strict Rule 7)
            if (item.providerName != null && item.quoteTimestamp != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Source: ${item.providerName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = 10.sp,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (item.isStale) Color(0xFF381419) else Color(0xFF093322))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = item.statusText,
                            color = if (item.isStale) IndicatorSell else IndicatorBuy,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SparklinePreview(
    points: List<Double>,
    isPositive: Boolean,
    modifier: Modifier = Modifier
) {
    if (points.size < 2) return
    val lineColor = if (isPositive) IndicatorBuy else IndicatorSell

    Canvas(modifier = modifier) {
        val min = points.minOrNull() ?: 0.0
        val max = points.maxOrNull() ?: 1.0
        val range = if (max - min == 0.0) 1.0 else max - min

        val path = Path()
        val stepX = size.width / (points.size - 1)

        points.forEachIndexed { i, p ->
            val x = i * stepX
            val normY = ((p - min) / range).toFloat()
            val y = size.height - (normY * size.height)
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 1.5.dp.toPx())
        )
    }
}
