package com.pipbeacon.app.ui.screens

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pipbeacon.app.ui.components.OnboardingAnalyzeArt
import com.pipbeacon.app.ui.components.OnboardingLearnArt
import com.pipbeacon.app.ui.components.OnboardingSignalsArt
import com.pipbeacon.app.ui.components.ShowcaseBackground
import com.pipbeacon.app.ui.components.ShowcaseGoldButton
import com.pipbeacon.app.ui.theme.CyanAccent
import com.pipbeacon.app.ui.theme.GoldAccentBright
import com.pipbeacon.app.ui.theme.NavyBorder
import com.pipbeacon.app.ui.theme.TextMuted
import com.pipbeacon.app.ui.theme.TextPrimary
import com.pipbeacon.app.ui.theme.TextSecondary
import kotlinx.coroutines.launch

private data class OnboardingPage(
    val title: String,
    val subtitle: String,
    val badge: String
)

@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pages = listOf(
        OnboardingPage(
            title = "Learn Trading\nfrom Basics",
            subtitle = "Master Forex & Crypto with step-by-step offline lessons.",
            badge = "EDUCATION"
        ),
        OnboardingPage(
            title = "Analyze Markets",
            subtitle = "Explore technical chart analysis and indicators for Forex & Crypto.",
            badge = "ANALYSIS"
        ),
        OnboardingPage(
            title = "Get Trading Signals",
            subtitle = "Study structured setups with Entry, TP and SL educational targets.",
            badge = "SIGNALS"
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    ShowcaseBackground(
        modifier = modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("onboarding_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 560.dp)
                .align(Alignment.Center)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar with Skip button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PipBeacon",
                    color = GoldAccentBright,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                TextButton(
                    onClick = onFinishOnboarding,
                    modifier = Modifier.testTag("onboarding_skip_button")
                ) {
                    Text(
                        text = "Skip",
                        color = CyanAccent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { pageIndex ->
                val page = pages[pageIndex]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    when (pageIndex) {
                        0 -> OnboardingLearnArt(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(230.dp)
                        )
                        1 -> OnboardingAnalyzeArt(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(230.dp)
                        )
                        else -> OnboardingSignalsArt(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(230.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Text(
                        text = page.title,
                        color = TextPrimary,
                        fontSize = 27.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 33.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = page.subtitle,
                        color = TextSecondary,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }

            // Pager Indicator Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 18.dp)
            ) {
                repeat(pages.size) { index ->
                    val isCurrent = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .size(width = if (isCurrent) 24.dp else 8.dp, height = 8.dp)
                            .clip(if (isCurrent) RoundedCornerShape(4.dp) else CircleShape)
                            .background(if (isCurrent) GoldAccentBright else NavyBorder)
                            .clickable {
                                scope.launch { pagerState.animateScrollToPage(index) }
                            }
                    )
                }
            }

            // Primary Gold Pill Button ("Next" or "Get Started")
            val isLastPage = pagerState.currentPage == pages.lastIndex
            ShowcaseGoldButton(
                text = if (isLastPage) "Get Started" else "Next",
                onClick = {
                    if (isLastPage) {
                        onFinishOnboarding()
                    } else {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .testTag(if (isLastPage) "onboarding_get_started_button" else "onboarding_next_button")
            )

            Text(
                text = "Educational platform • Demonstration market data • No broker execution",
                color = TextMuted,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}
