package com.pipbeacon.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pipbeacon.app.ui.theme.CyanAccent
import com.pipbeacon.app.ui.theme.GoldAccent
import com.pipbeacon.app.ui.theme.GoldAccentBright
import com.pipbeacon.app.ui.theme.GoldAccentDark
import com.pipbeacon.app.ui.theme.IndicatorBuy
import com.pipbeacon.app.ui.theme.IndicatorSell
import com.pipbeacon.app.ui.theme.LocalThemeIsDark
import com.pipbeacon.app.ui.theme.NavyBackground
import com.pipbeacon.app.ui.theme.NavyBackgroundBottom
import com.pipbeacon.app.ui.theme.NavyBorder
import com.pipbeacon.app.ui.theme.NavyCard
import com.pipbeacon.app.ui.theme.NavyCardElevated
import com.pipbeacon.app.ui.theme.NavySurfaceVariant
import com.pipbeacon.app.ui.theme.TextMuted
import com.pipbeacon.app.ui.theme.TextPrimary
import com.pipbeacon.app.ui.theme.TextSecondary
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ShowcaseBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(NavyBackground, NavyBackgroundBottom)
                )
            ),
        content = content
    )
}

@Composable
fun ShowcaseGoldButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 50.dp,
    enabled: Boolean = true,
    trailingArrow: Boolean = false
) {
    val gradient = if (enabled) {
        Brush.horizontalGradient(
            colors = listOf(
                Color(0xFFFACC15),
                Color(0xFFF5B731),
                Color(0xFFE09F1F)
            )
        )
    } else {
        Brush.horizontalGradient(
            colors = listOf(Color(0xFF475569), Color(0xFF334155))
        )
    }

    Box(
        modifier = modifier
            .heightIn(min = 48.dp)
            .height(height)
            .clip(RoundedCornerShape(26.dp))
            .background(gradient)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 22.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (trailingArrow) "$text  →" else text,
            color = if (enabled) Color(0xFF060D1A) else Color(0xFFCBD5E1),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
fun ShowcaseSurfaceCard(
    modifier: Modifier = Modifier,
    elevated: Boolean = false,
    borderColor: Color? = null,
    cornerRadius: Dp = 16.dp,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val bgColor = if (elevated) NavyCardElevated else NavyCard
    val strokeColor = borderColor ?: NavyBorder
    val baseModifier = modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(cornerRadius))
        .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(cornerRadius),
        border = BorderStroke(1.dp, strokeColor),
        modifier = baseModifier
    ) {
        Box(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}

@Composable
fun ShowcaseSubScreenHeader(
    title: String,
    onBack: (() -> Unit)? = null,
    subtitle: String? = null,
    trailingContent: @Composable RowScope.() -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            if (onBack != null) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(2.dp))
            }
            Column {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            content = trailingContent
        )
    }
}

@Composable
fun ShowcaseChevronRow(
    title: String,
    icon: ImageVector,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailingBadge: String? = null,
    trailingBadgeColor: Color = GoldAccentBright
) {
    Surface(
        color = NavyCard,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, NavyBorder),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconTint.copy(alpha = 0.14f))
                        .border(1.dp, iconTint.copy(alpha = 0.28f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!subtitle.isNullOrBlank()) {
                        Text(
                            text = subtitle,
                            color = TextSecondary,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!trailingBadge.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(trailingBadgeColor.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = trailingBadge,
                            color = trailingBadgeColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Open $title",
                    tint = TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Custom Canvas Golden Lighthouse Beacon art matching the PipBeacon Showcase brand emblem.
 */
@Composable
fun PipBeaconLighthouseArt(
    modifier: Modifier = Modifier,
    showCandles: Boolean = true
) {
    val isDark = LocalThemeIsDark.current
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w * 0.50f
        val lanternY = h * 0.28f

        // Radial warm gold glow behind lantern
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFACC15).copy(alpha = 0.45f),
                    Color(0xFF38BDF8).copy(alpha = if (isDark) 0.14f else 0.08f),
                    Color.Transparent
                ),
                center = Offset(cx, lanternY),
                radius = w * 0.45f
            ),
            center = Offset(cx, lanternY),
            radius = w * 0.45f
        )

        // Glowing light beams sweeping left and right
        val leftBeam = Path().apply {
            moveTo(cx, lanternY)
            lineTo(0f, lanternY - h * 0.16f)
            lineTo(0f, lanternY + h * 0.14f)
            close()
        }
        drawPath(
            path = leftBeam,
            brush = Brush.horizontalGradient(
                colors = listOf(Color.Transparent, Color(0xFFFDE047).copy(alpha = 0.28f)),
                startX = 0f,
                endX = cx
            )
        )

        val rightBeam = Path().apply {
            moveTo(cx, lanternY)
            lineTo(w, lanternY - h * 0.18f)
            lineTo(w, lanternY + h * 0.12f)
            close()
        }
        drawPath(
            path = rightBeam,
            brush = Brush.horizontalGradient(
                colors = listOf(Color(0xFFFDE047).copy(alpha = 0.32f), Color.Transparent),
                startX = cx,
                endX = w
            )
        )

        // Subtle candlestick silhouettes flanking the lighthouse
        if (showCandles) {
            val candleData = listOf(
                Triple(0.16f, 0.56f, 0.16f),
                Triple(0.26f, 0.48f, 0.20f),
                Triple(0.75f, 0.50f, 0.18f),
                Triple(0.85f, 0.42f, 0.22f)
            )
            candleData.forEachIndexed { idx, (xFrac, topFrac, bodyFrac) ->
                val x = w * xFrac
                val topY = h * topFrac
                val botY = topY + h * bodyFrac
                val cColor = if (idx % 2 == 0) Color(0xFF10B981).copy(alpha = 0.35f) else Color(0xFF38BDF8).copy(alpha = 0.30f)
                drawLine(
                    color = cColor,
                    start = Offset(x, topY - h * 0.06f),
                    end = Offset(x, botY + h * 0.05f),
                    strokeWidth = 2.dp.toPx()
                )
                drawRoundRect(
                    color = cColor,
                    topLeft = Offset(x - w * 0.025f, topY),
                    size = Size(w * 0.05f, h * bodyFrac),
                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                )
            }
        }

        // Lighthouse Tower (tapered gold & navy bands)
        val baseY = h * 0.84f
        val galleryY = h * 0.36f
        val towerPath = Path().apply {
            moveTo(cx - w * 0.065f, galleryY)
            lineTo(cx + w * 0.065f, galleryY)
            lineTo(cx + w * 0.125f, baseY)
            lineTo(cx - w * 0.125f, baseY)
            close()
        }
        drawPath(
            path = towerPath,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFFACC15), Color(0xFFF5B731), Color(0xFFB45309))
            )
        )

        // Navy architectural stripes on tower
        for (band in listOf(0.49f to 0.56f, 0.65f to 0.72f)) {
            val yTop = h * band.first
            val yBot = h * band.second
            val tTop = (yTop - galleryY) / (baseY - galleryY)
            val tBot = (yBot - galleryY) / (baseY - galleryY)
            val hwTop = w * (0.065f + 0.060f * tTop)
            val hwBot = w * (0.065f + 0.060f * tBot)
            val stripePath = Path().apply {
                moveTo(cx - hwTop, yTop)
                lineTo(cx + hwTop, yTop)
                lineTo(cx + hwBot, yBot)
                lineTo(cx - hwBot, yBot)
                close()
            }
            drawPath(stripePath, color = Color(0xFF0A162B).copy(alpha = 0.85f))
        }

        // Gallery deck
        drawRoundRect(
            color = Color(0xFFFDE047),
            topLeft = Offset(cx - w * 0.095f, galleryY - h * 0.028f),
            size = Size(w * 0.19f, h * 0.032f),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
        )

        // Lantern room
        drawRoundRect(
            color = Color(0xFFFEF08A),
            topLeft = Offset(cx - w * 0.055f, lanternY - h * 0.045f),
            size = Size(w * 0.11f, h * 0.085f),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
        )

        // Dome cap
        val domePath = Path().apply {
            moveTo(cx - w * 0.07f, lanternY - h * 0.045f)
            lineTo(cx, lanternY - h * 0.11f)
            lineTo(cx + w * 0.07f, lanternY - h * 0.045f)
            close()
        }
        drawPath(domePath, color = Color(0xFFD97706))

        // Rocky cliff / horizon base
        val cliffPath = Path().apply {
            moveTo(w * 0.18f, h * 0.92f)
            lineTo(w * 0.34f, baseY - h * 0.01f)
            lineTo(w * 0.66f, baseY)
            lineTo(w * 0.84f, h * 0.92f)
            close()
        }
        drawPath(
            cliffPath,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF1E3454), Color(0xFF091324))
            )
        )
    }
}

/**
 * Onboarding 1: Candlestick chart + Graduation Cap Canvas illustration
 */
@Composable
fun OnboardingLearnArt(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Subtle cyan glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF38BDF8).copy(alpha = 0.22f), Color.Transparent),
                center = Offset(w * 0.5f, h * 0.5f),
                radius = w * 0.45f
            )
        )

        // Candlestick bars background
        val candles = listOf(
            Triple(0.16f, 0.52f, true),
            Triple(0.28f, 0.62f, false),
            Triple(0.40f, 0.46f, true),
            Triple(0.52f, 0.55f, false),
            Triple(0.64f, 0.40f, true),
            Triple(0.76f, 0.33f, true),
            Triple(0.86f, 0.26f, true)
        )
        candles.forEach { (xf, yf, bullish) ->
            val x = w * xf
            val yTop = h * yf
            val barH = h * 0.22f
            val color = if (bullish) Color(0xFF10B981) else Color(0xFFEF4444)
            drawLine(color, Offset(x, yTop - h * 0.06f), Offset(x, yTop + barH + h * 0.06f), strokeWidth = 2.5.dp.toPx())
            drawRoundRect(
                color = color,
                topLeft = Offset(x - w * 0.032f, yTop),
                size = Size(w * 0.064f, barH),
                cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
            )
        }

        // Graduation cap floating above chart
        val capCx = w * 0.54f
        val capCy = h * 0.30f
        val diamond = Path().apply {
            moveTo(capCx, capCy - h * 0.13f)
            lineTo(capCx + w * 0.24f, capCy)
            lineTo(capCx, capCy + h * 0.11f)
            lineTo(capCx - w * 0.24f, capCy)
            close()
        }
        drawPath(
            diamond,
            brush = Brush.linearGradient(listOf(Color(0xFF38BDF8), Color(0xFF1D4ED8)))
        )
        drawPath(diamond, color = Color(0xFF93C5FD), style = Stroke(width = 1.5.dp.toPx()))

        // Cap base crown
        drawRoundRect(
            brush = Brush.verticalGradient(listOf(Color(0xFF1E40AF), Color(0xFF0F1C33))),
            topLeft = Offset(capCx - w * 0.11f, capCy + h * 0.06f),
            size = Size(w * 0.22f, h * 0.12f),
            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
        )
        // Gold tassel
        drawLine(
            color = Color(0xFFFACC15),
            start = Offset(capCx + w * 0.21f, capCy + h * 0.01f),
            end = Offset(capCx + w * 0.21f, capCy + h * 0.18f),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawCircle(Color(0xFFFACC15), radius = 4.dp.toPx(), center = Offset(capCx + w * 0.21f, capCy + h * 0.19f))
    }
}

/**
 * Onboarding 2: Metallic Gold BTC & USD coins with curved Cyan swap arrows
 */
@Composable
fun OnboardingAnalyzeArt(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val r = w * 0.19f
        val leftC = Offset(w * 0.33f, h * 0.45f)
        val rightC = Offset(w * 0.67f, h * 0.56f)

        // Ambient gold & cyan glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFF5B731).copy(alpha = 0.24f), Color.Transparent),
                center = leftC,
                radius = r * 1.8f
            ),
            center = leftC,
            radius = r * 1.8f
        )

        // Top & bottom curved cyan exchange arcs
        drawArc(
            color = Color(0xFF38BDF8),
            startAngle = 205f,
            sweepAngle = 115f,
            useCenter = false,
            topLeft = Offset(w * 0.26f, h * 0.18f),
            size = Size(w * 0.48f, h * 0.48f),
            style = Stroke(width = 4.5.dp.toPx(), cap = StrokeCap.Round)
        )
        drawArc(
            color = Color(0xFF38BDF8),
            startAngle = 25f,
            sweepAngle = 115f,
            useCenter = false,
            topLeft = Offset(w * 0.26f, h * 0.34f),
            size = Size(w * 0.48f, h * 0.48f),
            style = Stroke(width = 4.5.dp.toPx(), cap = StrokeCap.Round)
        )

        // Left Gold Coin (BTC style)
        drawCircle(
            brush = Brush.linearGradient(listOf(Color(0xFFFDE047), Color(0xFFD97706))),
            radius = r,
            center = leftC
        )
        drawCircle(color = Color(0xFFFEF08A), radius = r * 0.82f, center = leftC, style = Stroke(width = 2.dp.toPx()))
        // Stylized B bars inside left coin
        drawLine(Color(0xFF0A1426), Offset(leftC.x - r * 0.12f, leftC.y - r * 0.48f), Offset(leftC.x - r * 0.12f, leftC.y + r * 0.48f), strokeWidth = 3.5.dp.toPx(), cap = StrokeCap.Round)
        drawLine(Color(0xFF0A1426), Offset(leftC.x + r * 0.08f, leftC.y - r * 0.48f), Offset(leftC.x + r * 0.08f, leftC.y + r * 0.48f), strokeWidth = 3.5.dp.toPx(), cap = StrokeCap.Round)
        drawArc(
            color = Color(0xFF0A1426),
            startAngle = -90f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(leftC.x - r * 0.14f, leftC.y - r * 0.36f),
            size = Size(r * 0.56f, r * 0.36f),
            style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
        )
        drawArc(
            color = Color(0xFF0A1426),
            startAngle = -90f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(leftC.x - r * 0.14f, leftC.y),
            size = Size(r * 0.62f, r * 0.38f),
            style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
        )

        // Right Gold Coin (USD style)
        drawCircle(
            brush = Brush.linearGradient(listOf(Color(0xFFFACC15), Color(0xFFB45309))),
            radius = r,
            center = rightC
        )
        drawCircle(color = Color(0xFFFEF08A), radius = r * 0.82f, center = rightC, style = Stroke(width = 2.dp.toPx()))
        // Stylized $ glyph
        drawLine(Color(0xFF0A1426), Offset(rightC.x, rightC.y - r * 0.48f), Offset(rightC.x, rightC.y + r * 0.48f), strokeWidth = 3.5.dp.toPx(), cap = StrokeCap.Round)
        val sPath = Path().apply {
            moveTo(rightC.x + r * 0.24f, rightC.y - r * 0.24f)
            cubicTo(rightC.x - r * 0.32f, rightC.y - r * 0.38f, rightC.x - r * 0.30f, rightC.y - r * 0.02f, rightC.x, rightC.y)
            cubicTo(rightC.x + r * 0.32f, rightC.y + r * 0.02f, rightC.x + r * 0.32f, rightC.y + r * 0.38f, rightC.x - r * 0.24f, rightC.y + r * 0.26f)
        }
        drawPath(sPath, color = Color(0xFF0A1426), style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round))
    }
}

/**
 * Onboarding 3: Ascending Emerald-to-Gold Bar Chart with sweeping upward trend arrow
 */
@Composable
fun OnboardingSignalsArt(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val barHeights = listOf(0.24f, 0.34f, 0.46f, 0.58f, 0.70f)
        val startX = w * 0.16f
        val gap = w * 0.145f
        val barW = w * 0.105f
        val baseY = h * 0.84f

        barHeights.forEachIndexed { i, hf ->
            val bh = h * hf * 0.72f
            val x = startX + i * gap
            val y = baseY - bh
            val barBrush = Brush.verticalGradient(
                colors = if (i >= 3) {
                    listOf(Color(0xFFFACC15), Color(0xFF10B981))
                } else {
                    listOf(Color(0xFF10B981), Color(0xFF065F46))
                }
            )
            drawRoundRect(
                brush = barBrush,
                topLeft = Offset(x, y),
                size = Size(barW, bh),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
        }

        // Curved sweeping gold arrow above bars
        val arrowPath = Path().apply {
            moveTo(w * 0.14f, h * 0.58f)
            quadraticTo(w * 0.46f, h * 0.42f, w * 0.80f, h * 0.16f)
        }
        drawPath(
            path = arrowPath,
            brush = Brush.horizontalGradient(listOf(Color(0xFF38BDF8), Color(0xFFFACC15))),
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
        )
        // Arrowhead
        val tip = Offset(w * 0.81f, h * 0.15f)
        val head = Path().apply {
            moveTo(tip.x, tip.y)
            lineTo(tip.x - w * 0.08f, tip.y + h * 0.01f)
            lineTo(tip.x - w * 0.02f, tip.y + h * 0.08f)
            close()
        }
        drawPath(head, color = Color(0xFFFACC15))
    }
}

/**
 * Academy Hero Banner Canvas Graphic (Euro / Dollar for Forex, Glowing Gold Coin for Crypto)
 */
@Composable
fun AcademyHeroGraphic(
    isForex: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val bgColors = if (isForex) {
            listOf(Color(0xFF0B2542), Color(0xFF0A1426))
        } else {
            listOf(Color(0xFF281D0A), Color(0xFF0A1426))
        }
        drawRect(Brush.horizontalGradient(bgColors))

        // Decorative glowing arc & dots
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    (if (isForex) Color(0xFF38BDF8) else Color(0xFFFACC15)).copy(alpha = 0.30f),
                    Color.Transparent
                ),
                center = Offset(w * 0.5f, h * 0.5f),
                radius = w * 0.4f
            )
        )

        if (isForex) {
            // Left cyan Euro ring & Right gold Dollar ring
            drawCircle(
                color = Color(0xFF38BDF8).copy(alpha = 0.25f),
                radius = h * 0.34f,
                center = Offset(w * 0.38f, h * 0.50f)
            )
            drawCircle(
                color = Color(0xFF38BDF8),
                radius = h * 0.34f,
                center = Offset(w * 0.38f, h * 0.50f),
                style = Stroke(width = 2.5.dp.toPx())
            )
            drawCircle(
                brush = Brush.linearGradient(listOf(Color(0xFFFACC15), Color(0xFFD97706))),
                radius = h * 0.34f,
                center = Offset(w * 0.62f, h * 0.50f)
            )
            // Subtle candlestick silhouettes at base
            for (i in 0..7) {
                val x = w * (0.10f + i * 0.11f)
                val barH = h * (0.14f + (i % 3) * 0.08f)
                drawRect(
                    color = Color(0xFF38BDF8).copy(alpha = 0.18f),
                    topLeft = Offset(x, h - barH),
                    size = Size(w * 0.03f, barH)
                )
            }
        } else {
            // Glowing Crypto Coin
            val cx = w * 0.5f
            val cy = h * 0.5f
            val r = h * 0.36f
            drawCircle(
                brush = Brush.linearGradient(listOf(Color(0xFFFDE047), Color(0xFFD97706))),
                radius = r,
                center = Offset(cx, cy)
            )
            drawCircle(
                color = Color(0xFFFEF08A),
                radius = r * 0.80f,
                center = Offset(cx, cy),
                style = Stroke(width = 2.dp.toPx())
            )
            // Bitcoin bars
            drawLine(Color(0xFF0A1426), Offset(cx - r * 0.10f, cy - r * 0.45f), Offset(cx - r * 0.10f, cy + r * 0.45f), strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
            drawLine(Color(0xFF0A1426), Offset(cx + r * 0.08f, cy - r * 0.45f), Offset(cx + r * 0.08f, cy + r * 0.45f), strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
            drawArc(
                color = Color(0xFF0A1426),
                startAngle = -90f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(cx - r * 0.12f, cy - r * 0.34f),
                size = Size(r * 0.54f, r * 0.34f),
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = Color(0xFF0A1426),
                startAngle = -90f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(cx - r * 0.12f, cy),
                size = Size(r * 0.60f, r * 0.36f),
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}

/**
 * Smooth Canvas Sparkline with soft gradient area fill for Markets & Home Overview
 */
@Composable
fun ShowcaseSparkline(
    points: List<Double>,
    isPositive: Boolean,
    modifier: Modifier = Modifier
) {
    val lineColor = if (isPositive) IndicatorBuy else IndicatorSell
    val effectivePoints = if (points.size >= 2) {
        points
    } else if (isPositive) {
        listOf(1.0, 1.02, 1.01, 1.04, 1.03, 1.06, 1.08)
    } else {
        listOf(1.08, 1.06, 1.07, 1.03, 1.04, 1.01, 0.99)
    }

    Canvas(modifier = modifier) {
        val min = effectivePoints.minOrNull() ?: 0.0
        val max = effectivePoints.maxOrNull() ?: 1.0
        val range = if (max - min == 0.0) 1.0 else max - min
        val stepX = size.width / (effectivePoints.size - 1)

        val strokePath = Path()
        val fillPath = Path()

        effectivePoints.forEachIndexed { idx, p ->
            val x = idx * stepX
            val normY = ((p - min) / range).toFloat()
            val y = size.height * 0.9f - (normY * size.height * 0.8f)
            if (idx == 0) {
                strokePath.moveTo(x, y)
                fillPath.moveTo(x, size.height)
                fillPath.lineTo(x, y)
            } else {
                strokePath.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
        }
        fillPath.lineTo(size.width, size.height)
        fillPath.close()

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(lineColor.copy(alpha = 0.25f), Color.Transparent)
            )
        )
        drawPath(
            path = strokePath,
            color = lineColor,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

/**
 * Mini Candlestick Strip for Signal Spotlight, Signal Details & Lesson Details banner
 */
@Composable
fun ShowcaseCandlestickStrip(
    isBullishBias: Boolean = true,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val count = 16
        val step = w / count

        // Subtle background grid
        for (g in 1..3) {
            val gy = h * g / 4f
            drawLine(
                color = Color(0xFF1E3454).copy(alpha = 0.45f),
                start = Offset(0f, gy),
                end = Offset(w, gy),
                strokeWidth = 1f
            )
        }

        val linePath = Path()
        for (i in 0 until count) {
            val t = i.toFloat() / (count - 1)
            val wave = sin(t * PI * 2.5).toFloat() * 0.08f
            val trend = if (isBullishBias) (0.72f - t * 0.48f) else (0.28f + t * 0.48f)
            val midY = h * (trend + wave).coerceIn(0.18f, 0.82f)
            val isGreen = if (isBullishBias) (i % 4 != 1) else (i % 4 == 1)
            val cColor = if (isGreen) IndicatorBuy else IndicatorSell
            val x = i * step + step * 0.5f
            val bodyH = h * (0.10f + (i % 3) * 0.04f)
            val topY = (midY - bodyH * 0.5f).coerceIn(h * 0.08f, h * 0.80f)

            drawLine(
                color = cColor,
                start = Offset(x, (topY - h * 0.07f).coerceAtLeast(2f)),
                end = Offset(x, (topY + bodyH + h * 0.07f).coerceAtMost(h - 2f)),
                strokeWidth = 1.5.dp.toPx()
            )
            drawRoundRect(
                color = cColor,
                topLeft = Offset(x - step * 0.24f, topY),
                size = Size(step * 0.48f, bodyH),
                cornerRadius = CornerRadius(1.5.dp.toPx(), 1.5.dp.toPx())
            )
            if (i == 0) linePath.moveTo(x, midY) else linePath.lineTo(x, midY)
        }

        // Smooth trend curve
        drawPath(
            path = linePath,
            color = Color(0xFF38BDF8).copy(alpha = 0.6f),
            style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}
