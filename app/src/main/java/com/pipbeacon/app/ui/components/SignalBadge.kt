package com.pipbeacon.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pipbeacon.app.core.SignalType
import com.pipbeacon.app.ui.theme.IndicatorBuy
import com.pipbeacon.app.ui.theme.IndicatorBuyBackground
import com.pipbeacon.app.ui.theme.IndicatorNeutral
import com.pipbeacon.app.ui.theme.IndicatorNeutralBackground
import com.pipbeacon.app.ui.theme.IndicatorSell
import com.pipbeacon.app.ui.theme.IndicatorSellBackground
import com.pipbeacon.app.ui.theme.IndicatorStrongBuy
import com.pipbeacon.app.ui.theme.IndicatorStrongSell

@Composable
fun SignalBadge(
    signal: SignalType,
    modifier: Modifier = Modifier
) {
    val (bgColor, fgColor, borderColor) = when (signal) {
        SignalType.STRONG_BUY -> Triple(IndicatorBuyBackground, IndicatorStrongBuy, IndicatorStrongBuy)
        SignalType.BUY -> Triple(IndicatorBuyBackground, IndicatorBuy, IndicatorBuy)
        SignalType.NEUTRAL -> Triple(IndicatorNeutralBackground, IndicatorNeutral, IndicatorNeutral)
        SignalType.SELL -> Triple(IndicatorSellBackground, IndicatorSell, IndicatorSell)
        SignalType.STRONG_SELL -> Triple(IndicatorSellBackground, IndicatorStrongSell, IndicatorStrongSell)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(1.dp, borderColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = signal.label,
            color = fgColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelSmall
        )
    }
}
