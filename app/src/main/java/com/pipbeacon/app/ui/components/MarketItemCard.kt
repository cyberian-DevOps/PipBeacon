package com.pipbeacon.app.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pipbeacon.app.core.MarketType
import com.pipbeacon.app.data.MarketItem
import com.pipbeacon.app.ui.theme.CyanAccent
import com.pipbeacon.app.ui.theme.GoldAccent
import com.pipbeacon.app.ui.theme.GoldAccentBright
import com.pipbeacon.app.ui.theme.IndicatorBuy
import com.pipbeacon.app.ui.theme.IndicatorBuyBackground
import com.pipbeacon.app.ui.theme.IndicatorNeutral
import com.pipbeacon.app.ui.theme.IndicatorSell
import com.pipbeacon.app.ui.theme.IndicatorSellBackground
import com.pipbeacon.app.ui.theme.JetBrainsMonoFont
import com.pipbeacon.app.ui.theme.NavyBorder
import com.pipbeacon.app.ui.theme.NavyCard
import com.pipbeacon.app.ui.theme.NavySurfaceVariant
import com.pipbeacon.app.ui.theme.TextMuted
import com.pipbeacon.app.ui.theme.TextPrimary
import com.pipbeacon.app.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun MarketItemCard(
    item: MarketItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCrypto = item.marketType == MarketType.CRYPTO
    val badgeTint = if (isCrypto) GoldAccentBright else CyanAccent
    val changePct = item.changePercent24h ?: 0.0
    val isUp = changePct >= 0.0
    val badgeColor = if (isUp) IndicatorBuy else IndicatorSell
    val sign = if (isUp) "+" else ""

    Surface(
        color = NavyCard,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, NavyBorder),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("market_item_${item.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1.1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(badgeTint.copy(alpha = 0.15f))
                            .border(1.dp, badgeTint.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isCrypto) Icons.Default.CurrencyBitcoin else Icons.Default.CurrencyExchange,
                            contentDescription = null,
                            tint = badgeTint,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = item.symbol,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(NavySurfaceVariant)
                                    .border(1.dp, NavyBorder, RoundedCornerShape(5.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = item.category,
                                    color = CyanAccent,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                SignalBadge(signal = item.signalSummary)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Price section
                Column {
                    if (item.isProviderLive && item.currentPrice != null) {
                        val formattedPrice = when {
                            item.currentPrice < 10 -> String.format(Locale.US, "%.4f", item.currentPrice)
                            else -> String.format(Locale.US, "%,.2f", item.currentPrice)
                        }
                        Text(
                            text = formattedPrice,
                            fontFamily = JetBrainsMonoFont,
                            fontSize = 17.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        if (item.pipSpread != null) {
                            Text(
                                text = "Spread: ${String.format(Locale.US, "%.1f", item.pipSpread)} pips",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NavySurfaceVariant)
                                .border(1.dp, NavyBorder, RoundedCornerShape(6.dp))
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
                        ShowcaseSparkline(
                            points = item.sparkline,
                            isPositive = isUp,
                            modifier = Modifier
                                .width(72.dp)
                                .height(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isUp) IndicatorBuyBackground else IndicatorSellBackground)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "$sign${String.format(Locale.US, "%.2f", changePct)}%",
                                color = badgeColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                fontFamily = JetBrainsMonoFont
                            )
                        }
                    }
                }
            }

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
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .background(if (item.isStale) IndicatorSellBackground else IndicatorBuyBackground)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
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
