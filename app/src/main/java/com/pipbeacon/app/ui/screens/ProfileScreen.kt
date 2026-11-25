package com.pipbeacon.app.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pipbeacon.app.core.Constants
import com.pipbeacon.app.data.PermittedProviderMode
import com.pipbeacon.app.data.ProviderConfig
import com.pipbeacon.app.ui.components.ShowcaseBackground
import com.pipbeacon.app.ui.components.ShowcaseChevronRow
import com.pipbeacon.app.ui.theme.CyanAccent
import com.pipbeacon.app.ui.theme.GoldAccent
import com.pipbeacon.app.ui.theme.GoldAccentBackground
import com.pipbeacon.app.ui.theme.GoldAccentBright
import com.pipbeacon.app.ui.theme.IndicatorBuy
import com.pipbeacon.app.ui.theme.IndicatorNeutral
import com.pipbeacon.app.ui.theme.IndicatorNeutralBackground
import com.pipbeacon.app.ui.theme.JetBrainsMonoFont
import com.pipbeacon.app.ui.theme.NavyBorder
import com.pipbeacon.app.ui.theme.NavyCard
import com.pipbeacon.app.ui.theme.NavyCardElevated
import com.pipbeacon.app.ui.theme.NavySurface
import com.pipbeacon.app.ui.theme.NavySurfaceVariant
import com.pipbeacon.app.ui.theme.TextMuted
import com.pipbeacon.app.ui.theme.TextPrimary
import com.pipbeacon.app.ui.theme.TextSecondary
import com.pipbeacon.app.viewmodel.AcademyUiState

@Composable
fun ProfileScreen(
    academyUiState: AcademyUiState,
    config: ProviderConfig,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onRiskToleranceChange: (String) -> Unit,
    onOfflineDemoToggle: (Boolean) -> Unit,
    onOpenForexAcademy: () -> Unit,
    onOpenCryptoAcademy: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenJournal: () -> Unit = {},
    onReplayOnboarding: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isOfflineDemo = config.mode == PermittedProviderMode.UNCONFIGURED
    val studentLevel = 1 + (academyUiState.totalCompletedCount / 2)

    ShowcaseBackground(modifier = modifier.testTag("profile_screen")) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Centered Showcase Trader Profile Hero Card
            item {
                Surface(
                    color = NavyCard,
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(
                        1.dp,
                        Brush.linearGradient(
                            listOf(GoldAccentBright.copy(alpha = 0.55f), CyanAccent.copy(alpha = 0.45f))
                        )
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(NavyCardElevated, NavyCard)
                                )
                            )
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(GoldAccent.copy(alpha = 0.16f))
                                .border(2.dp, GoldAccentBright, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Trader Profile Avatar",
                                tint = GoldAccentBright,
                                modifier = Modifier.size(38.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "PipBeacon Student",
                            color = TextPrimary,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(GoldAccentBackground)
                                    .border(1.dp, GoldAccent.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 3.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = GoldAccentBright, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Level $studentLevel",
                                        color = GoldAccentBright,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CyanAccent.copy(alpha = 0.14f))
                                    .padding(horizontal = 10.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = if (isOfflineDemo) "OFFLINE DEMO MODE" else "FREE OPEN DATA",
                                    color = CyanAccent,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Learn. Analyze. Trade Smarter.",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // 2. Showcase Menu Rows
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ShowcaseChevronRow(
                        title = "My Progress",
                        subtitle = "${academyUiState.totalCompletedCount}/${academyUiState.totalLessonsCount} Academy lessons completed",
                        icon = Icons.Default.School,
                        iconTint = GoldAccentBright,
                        trailingBadge = "Level $studentLevel",
                        onClick = onOpenForexAcademy
                    )

                    ShowcaseChevronRow(
                        title = "Trading Journal",
                        subtitle = "Review & log offline practice setups",
                        icon = Icons.Default.Book,
                        iconTint = IndicatorBuy,
                        onClick = onOpenJournal
                    )

                    ShowcaseChevronRow(
                        title = "Replay Intro Tour",
                        subtitle = "View the 3-step PipBeacon showcase onboarding",
                        icon = Icons.Default.Explore,
                        iconTint = CyanAccent,
                        onClick = onReplayOnboarding
                    )

                    ShowcaseChevronRow(
                        title = "Data Sources & App Settings",
                        subtitle = "Configure free public feeds, diagnostics & alerts",
                        icon = Icons.Default.Policy,
                        iconTint = CyanAccent,
                        onClick = onOpenSettings,
                        modifier = Modifier.testTag("profile_open_settings_button")
                    )
                }
            }

            // 3. Learning Mastery Progress Card
            item {
                Surface(
                    color = NavyCard,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, NavyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ACADEMY MASTERY",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyanAccent,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "${academyUiState.totalCompletedCount}/${academyUiState.totalLessonsCount} Modules",
                                color = GoldAccentBright,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = JetBrainsMonoFont
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        AcademyProgressRow(
                            title = "Forex Academy",
                            completed = academyUiState.forexCompletedCount,
                            total = academyUiState.forexTotalCount,
                            accent = CyanAccent,
                            icon = Icons.Default.CurrencyExchange,
                            onClick = onOpenForexAcademy,
                            testTag = "profile_open_forex_academy"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        AcademyProgressRow(
                            title = "Crypto Academy",
                            completed = academyUiState.cryptoCompletedCount,
                            total = academyUiState.cryptoTotalCount,
                            accent = GoldAccentBright,
                            icon = Icons.Default.CurrencyBitcoin,
                            onClick = onOpenCryptoAcademy,
                            testTag = "profile_open_crypto_academy"
                        )
                    }
                }
            }

            // 4. Preferences & Workspace Toggles
            item {
                Surface(
                    color = NavyCard,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, NavyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Tune, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "WORKSPACE & STUDY PREFERENCES",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Study Risk Profile",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Conservative", "Moderate", "Aggressive").forEach { tol ->
                                val isSel = config.riskTolerance == tol
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSel) CyanAccent.copy(alpha = 0.16f) else NavySurfaceVariant)
                                        .border(1.dp, if (isSel) CyanAccent else NavyBorder, RoundedCornerShape(10.dp))
                                        .clickable { onRiskToleranceChange(tol) }
                                        .padding(vertical = 9.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = tol,
                                        color = if (isSel) CyanAccent else TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Appearance Mode Switch
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(NavySurfaceVariant)
                                .clickable(onClick = onToggleTheme)
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                                    contentDescription = null,
                                    tint = GoldAccentBright,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Appearance Mode", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = if (isDarkTheme) "Midnight Navy Dark" else "Daylight Slate Light",
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                            Switch(
                                checked = isDarkTheme,
                                onCheckedChange = { onToggleTheme() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF060D1A),
                                    checkedTrackColor = GoldAccentBright,
                                    uncheckedThumbColor = TextSecondary,
                                    uncheckedTrackColor = NavySurface
                                ),
                                modifier = Modifier.testTag("profile_theme_switch")
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Offline Demo Switch
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(NavySurfaceVariant)
                                .clickable { onOfflineDemoToggle(!isOfflineDemo) }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudOff,
                                    contentDescription = null,
                                    tint = CyanAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Offline Demonstration Mode", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = "Operate strictly offline using bundled sample market data",
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                            Switch(
                                checked = isOfflineDemo,
                                onCheckedChange = onOfflineDemoToggle,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF060D1A),
                                    checkedTrackColor = CyanAccent,
                                    uncheckedThumbColor = TextSecondary,
                                    uncheckedTrackColor = NavySurface
                                ),
                                modifier = Modifier.testTag("profile_offline_demo_switch")
                            )
                        }
                    }
                }
            }

            // 5. Educational Platform Notice Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(IndicatorNeutralBackground)
                        .border(1.dp, IndicatorNeutral.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = IndicatorNeutral, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "EDUCATIONAL PLATFORM NOTICE",
                                color = IndicatorNeutral,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${Constants.RISK_DISCLAIMER} PipBeacon executes zero live broker trades and stores no paid API secrets.",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AcademyProgressRow(
    title: String,
    completed: Int,
    total: Int,
    accent: Color,
    icon: ImageVector,
    onClick: () -> Unit,
    testTag: String
) {
    val pct = if (total > 0) completed.toFloat() / total.toFloat() else 0f
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NavySurfaceVariant)
            .border(1.dp, NavyBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$completed/$total",
                    color = accent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = JetBrainsMonoFont
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Open $title",
                    tint = TextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { pct },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = accent,
            trackColor = NavySurface
        )
    }
}
