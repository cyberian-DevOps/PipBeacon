package com.pipbeacon.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pipbeacon.app.core.SignalType
import com.pipbeacon.app.ui.theme.IndicatorBuy
import com.pipbeacon.app.ui.theme.IndicatorNeutral
import com.pipbeacon.app.ui.theme.IndicatorSell
import com.pipbeacon.app.ui.theme.IndicatorStrongBuy
import com.pipbeacon.app.ui.theme.IndicatorStrongSell
import com.pipbeacon.app.ui.theme.NavyBorder
import com.pipbeacon.app.ui.theme.NavyCard
import com.pipbeacon.app.ui.theme.TextMuted
import com.pipbeacon.app.ui.theme.TextPrimary

@Composable
fun TechnicalGauge(
    overallSignal: SignalType,
    buyCount: Int,
    neutralCount: Int,
    sellCount: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NavyCard)
            .border(1.dp, NavyBorder, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "SUMMARY SENTIMENT GAUGE",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Arc Gauge Canvas
            val needleFraction = when (overallSignal) {
                SignalType.STRONG_SELL -> 0.1f
                SignalType.SELL -> 0.3f
                SignalType.NEUTRAL -> 0.5f
                SignalType.BUY -> 0.7f
                SignalType.STRONG_BUY -> 0.9f
            }

            Canvas(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(90.dp)
            ) {
                val w = size.width
                val h = size.height
                val center = Offset(w / 2f, h)
                val radius = w * 0.42f
                val strokeWidth = 14.dp.toPx()

                // Draw background arc segments
                // Strong Sell
                drawArc(
                    color = IndicatorStrongSell,
                    startAngle = 180f,
                    sweepAngle = 36f,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth)
                )
                // Sell
                drawArc(
                    color = IndicatorSell,
                    startAngle = 216f,
                    sweepAngle = 36f,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth)
                )
                // Neutral
                drawArc(
                    color = IndicatorNeutral,
                    startAngle = 252f,
                    sweepAngle = 36f,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth)
                )
                // Buy
                drawArc(
                    color = IndicatorBuy,
                    startAngle = 288f,
                    sweepAngle = 36f,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth)
                )
                // Strong Buy
                drawArc(
                    color = IndicatorStrongBuy,
                    startAngle = 324f,
                    sweepAngle = 36f,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth)
                )

                // Needle
                val needleAngleRad = Math.toRadians((180.0 + (needleFraction * 180.0)))
                val needleLength = radius * 0.85f
                val needleEnd = Offset(
                    (center.x + needleLength * Math.cos(needleAngleRad)).toFloat(),
                    (center.y + needleLength * Math.sin(needleAngleRad)).toFloat()
                )

                drawLine(
                    color = Color.White,
                    start = center,
                    end = needleEnd,
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawCircle(color = Color.White, radius = 6.dp.toPx(), center = center)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Overall signal text
            SignalBadge(signal = overallSignal)

            Spacer(modifier = Modifier.height(12.dp))

            // Breakdown counters: Sell | Neutral | Buy
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BreakdownPill(label = "SELL", count = sellCount, color = IndicatorSell)
                BreakdownPill(label = "NEUTRAL", count = neutralCount, color = IndicatorNeutral)
                BreakdownPill(label = "BUY", count = buyCount, color = IndicatorBuy)
            }
        }
    }
}

@Composable
private fun BreakdownPill(
    label: String,
    count: Int,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.SemiBold)
        Text(
            text = "$count",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            style = MaterialTheme.typography.labelMedium
        )
    }
}
