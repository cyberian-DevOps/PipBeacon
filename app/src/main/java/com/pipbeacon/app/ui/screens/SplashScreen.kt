package com.pipbeacon.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import com.pipbeacon.app.ui.components.PipBeaconLighthouseArt
import com.pipbeacon.app.ui.components.ShowcaseBackground
import com.pipbeacon.app.ui.theme.CyanAccent
import com.pipbeacon.app.ui.theme.GoldAccent
import com.pipbeacon.app.ui.theme.GoldAccentBright
import com.pipbeacon.app.ui.theme.IndicatorBuy
import com.pipbeacon.app.ui.theme.NavyBorder
import com.pipbeacon.app.ui.theme.TextMuted
import com.pipbeacon.app.ui.theme.TextPrimary
import com.pipbeacon.app.ui.theme.TextSecondary
import kotlinx.coroutines.delay

/**
 * Showcase Splash Screen for PipBeacon: Forex & Crypto Signals
 * Tagline: "Learn. Analyze. Trade Smarter."
 */
@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var startAnim by remember { mutableStateOf(false) }

    val contentAlpha by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "splash_alpha"
    )
    val artScale by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0.88f,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "splash_scale"
    )

    LaunchedEffect(Unit) {
        startAnim = true
        delay(1500)
        onSplashComplete()
    }

    ShowcaseBackground(
        modifier = modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .clickable { onSplashComplete() }
            .testTag("splash_screen")
    ) {
        // Ambient radial spotlight matching showcase
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFF5B731).copy(alpha = 0.12f),
                        Color(0xFF38BDF8).copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.5f, size.height * 0.36f),
                    radius = size.minDimension * 0.55f
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
                .alpha(contentAlpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(0.9f))

            // Golden Lighthouse Beacon Art
            PipBeaconLighthouseArt(
                modifier = Modifier
                    .size(width = 230.dp, height = 190.dp)
                    .scale(artScale),
                showCandles = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "PipBeacon",
                color = GoldAccentBright,
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Forex & Crypto Signals",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Learn. Analyze.\nTrade Smarter.",
                color = GoldAccent,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            // Gold Circular Loading Spinner & Educational Status
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = GoldAccent,
                    trackColor = NavyBorder,
                    strokeWidth = 3.dp
                )
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(IndicatorBuy)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Offline Educational Mode • Tap to Skip",
                        color = TextMuted,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
