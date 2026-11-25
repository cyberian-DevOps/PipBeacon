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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.pipbeacon.app.core.Constants
import com.pipbeacon.app.data.PermittedProviderMode
import com.pipbeacon.app.data.ProviderConfig
import com.pipbeacon.app.data.provider.model.CommercialPermissionStatus
import com.pipbeacon.app.data.provider.model.ProviderLicensingConfig
import com.pipbeacon.app.data.provider.model.ProviderStatus
import com.pipbeacon.app.data.provider.model.WrittenPermissionStatus
import com.pipbeacon.app.ui.theme.IndicatorBuy
import com.pipbeacon.app.ui.theme.IndicatorNeutral
import com.pipbeacon.app.ui.theme.IndicatorSell
import com.pipbeacon.app.ui.theme.IndicatorSellBackground
import com.pipbeacon.app.ui.theme.NavyBackground
import com.pipbeacon.app.ui.theme.NavyBorder
import com.pipbeacon.app.ui.theme.NavyCard
import com.pipbeacon.app.ui.theme.NavyCardElevated
import com.pipbeacon.app.ui.theme.NavySurface
import com.pipbeacon.app.ui.theme.NavySurfaceVariant
import com.pipbeacon.app.ui.theme.TealAccent
import com.pipbeacon.app.ui.theme.TextMuted
import com.pipbeacon.app.ui.theme.TextPrimary
import com.pipbeacon.app.ui.theme.TextSecondary

@Composable
fun SettingsScreen(
    config: ProviderConfig,
    candidateConfigs: List<ProviderLicensingConfig>,
    providerStatuses: List<ProviderStatus>,
    diagnosticMessage: String,
    isTesting: Boolean,
    onModeChange: (PermittedProviderMode) -> Unit,
    onCustomUrlChange: (String) -> Unit,
    onRefreshIntervalChange: (Int) -> Unit,
    onAutoSyncChange: (Boolean) -> Unit,
    onRiskToleranceChange: (String) -> Unit,
    onToggleCandidate: (String, Boolean) -> Unit,
    onVerifyCandidate: (String, Boolean) -> Unit,
    onTestConnection: () -> Unit,
    onResetDefaults: () -> Unit,
    onThemeModeChange: (String) -> Unit = {},
    onNotificationsEnabledChange: (Boolean) -> Unit = {},
    onBackgroundRefreshIntervalChange: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    com.pipbeacon.app.ui.components.ShowcaseBackground(
        modifier = modifier.testTag("settings_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        // Section: Permitted Data Provider Governance
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(NavyCard)
                    .border(1.dp, NavyBorder, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Policy, contentDescription = null, tint = TealAccent, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PROVIDER GOVERNANCE MODE",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "PipBeacon strictly adheres to provider terms of service. Endpoints remain disabled until verified as legally permitted.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    PermittedProviderMode.entries.forEach { mode ->
                        val isSelected = config.mode == mode
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) NavySurfaceVariant else Color.Transparent)
                                .clickable { onModeChange(mode) }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { onModeChange(mode) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = TealAccent,
                                    unselectedColor = TextMuted
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = mode.displayName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) TealAccent else TextPrimary
                                )
                                Text(
                                    text = mode.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    if (config.mode == PermittedProviderMode.CUSTOM_ENDPOINT) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = config.customUrl,
                            onValueChange = onCustomUrlChange,
                            label = { Text("Custom REST URL", fontSize = 11.sp) },
                            placeholder = { Text("https://my-feed.internal/rates.json", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TealAccent,
                                unfocusedBorderColor = NavyBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = NavySurface,
                                unfocusedContainerColor = NavySurface
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onTestConnection,
                        enabled = !isTesting,
                        colors = ButtonDefaults.buttonColors(containerColor = NavyCardElevated, contentColor = TealAccent),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("test_provider_button")
                    ) {
                        if (isTesting) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = TealAccent, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text("Test Provider Sync & Diagnostics", style = MaterialTheme.typography.labelMedium)
                    }

                    if (diagnosticMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = diagnosticMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (diagnosticMessage.contains("error", ignoreCase = true)) IndicatorSell else IndicatorBuy,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Section: Candidate Provider Registry & Licensing Audit
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Gavel, contentDescription = null, tint = TealAccent, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CANDIDATE PROVIDER REGISTRY & LICENSING",
                        style = MaterialTheme.typography.labelSmall,
                        color = TealAccent,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "A provider must remain disabled for public distribution until its intended usage has been verified as permitted. Never invent a quote when an endpoint fails.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }

        items(candidateConfigs) { candidate ->
            CandidateProviderCard(
                candidate = candidate,
                onToggle = { enabled -> onToggleCandidate(candidate.id, enabled) },
                onVerify = { verified -> onVerifyCandidate(candidate.id, verified) }
            )
        }

        // Section: Sync & Refresh Intervals
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(NavyCard)
                    .border(1.dp, NavyBorder, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "REFRESH INTERVAL",
                        style = MaterialTheme.typography.labelSmall,
                        color = TealAccent,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val intervals = listOf(15, 30, 60, 300)
                        intervals.forEach { sec ->
                            val isSel = config.refreshIntervalSec == sec
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) TealAccent else NavySurface)
                                    .clickable { onRefreshIntervalChange(sec) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (sec < 60) "${sec}s" else "${sec / 60}m",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.Black else TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Background Auto-Polling", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                            Text("Poll only when app is in foreground", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                        }
                        Switch(
                            checked = config.autoSync,
                            onCheckedChange = onAutoSyncChange,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = TealAccent,
                                uncheckedThumbColor = TextSecondary,
                                uncheckedTrackColor = NavyBackground
                            )
                        )
                    }
                }
            }
        }

        // Section: Risk Tolerance Management
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(NavyCard)
                    .border(1.dp, NavyBorder, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "RISK PROFILE & LOT SIZING",
                        style = MaterialTheme.typography.labelSmall,
                        color = TealAccent,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val tolerances = listOf("Conservative", "Moderate", "Aggressive")
                        tolerances.forEach { tol ->
                            val isSel = config.riskTolerance == tol
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) NavySurfaceVariant else NavySurface)
                                    .border(1.dp, if (isSel) TealAccent else NavyBorder, RoundedCornerShape(6.dp))
                                    .clickable { onRiskToleranceChange(tol) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tol,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isSel) TealAccent else TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Architecture & Zero-Budget Disclosure
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(NavyCard)
                    .border(1.dp, NavyBorder, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = IndicatorBuy, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "STRICT DATA RULES & ZERO BUDGET",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "1. Only endpoints with permitted terms are integrated.\n" +
                                "2. No personal-only keys in public application feeds.\n" +
                                "3. No website scraping.\n" +
                                "4. No bypassing API keys, rate limits or restrictions.\n" +
                                "5. No TradingView data as automated signal engine input.\n" +
                                "6. Never invent a quote when an endpoint fails.\n" +
                                "7. Always display provider name, timestamp, and data status.\n" +
                                "8. All provider integrations are replaceable.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = Constants.ZERO_BUDGET_DISCLAIMER,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }

        // Section: Appearance & Theme
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(NavyCard)
                    .border(1.dp, NavyBorder, RoundedCornerShape(14.dp))
                    .padding(16.dp)
                    .testTag("theme_settings_card")
            ) {
                Column {
                    Text(
                        text = "THEME & APPEARANCE",
                        style = MaterialTheme.typography.labelSmall,
                        color = TealAccent,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val themes = listOf(
                        "SYSTEM" to "System Default",
                        "DARK" to "Dark Navy (Default)",
                        "LIGHT" to "Light Mode"
                    )

                    themes.forEach { (code, label) ->
                        val isSelected = config.themeMode == code
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) NavySurfaceVariant else Color.Transparent)
                                .clickable { onThemeModeChange(code) }
                                .padding(vertical = 6.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { onThemeModeChange(code) },
                                colors = RadioButtonDefaults.colors(selectedColor = TealAccent, unselectedColor = TextMuted)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isSelected) TealAccent else TextPrimary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // Section: Refresh Preferences & Background Sync (WorkManager)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(NavyCard)
                    .border(1.dp, NavyBorder, RoundedCornerShape(14.dp))
                    .padding(16.dp)
                    .testTag("refresh_preferences_card")
            ) {
                Column {
                    Text(
                        text = "BACKGROUND REFRESH PREFERENCES",
                        style = MaterialTheme.typography.labelSmall,
                        color = TealAccent,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "On-device WorkManager periodic task scheduled when network is connected. Zero billable cloud infrastructure.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val intervals = listOf(
                            15 to "15 Min",
                            30 to "30 Min",
                            60 to "1 Hour",
                            0 to "Manual"
                        )
                        intervals.forEach { (mins, label) ->
                            val isSel = config.backgroundRefreshIntervalMinutes == mins
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) NavySurfaceVariant else NavySurface)
                                    .border(1.dp, if (isSel) TealAccent else NavyBorder, RoundedCornerShape(8.dp))
                                    .clickable { onBackgroundRefreshIntervalChange(mins) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isSel) TealAccent else TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Auto-sync on Startup",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Refreshes quotes automatically when app launches",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = config.autoSync,
                            onCheckedChange = onAutoSyncChange,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = TealAccent,
                                uncheckedThumbColor = TextSecondary,
                                uncheckedTrackColor = NavySurface
                            )
                        )
                    }
                }
            }
        }

        // Section: Local Android Notifications
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(NavyCard)
                    .border(1.dp, NavyBorder, RoundedCornerShape(14.dp))
                    .padding(16.dp)
                    .testTag("notifications_settings_card")
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "LOCAL NOTIFICATIONS",
                                style = MaterialTheme.typography.labelSmall,
                                color = TealAccent,
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Target TP/SL & Signal Alerts",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Switch(
                            checked = config.notificationsEnabled,
                            onCheckedChange = onNotificationsEnabledChange,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = TealAccent,
                                uncheckedThumbColor = TextSecondary,
                                uncheckedTrackColor = NavySurface
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Dispatches local Android notifications exclusively on this device when validated setups reach Take Profit (TP1/2/3) or Stop Loss targets. Operates 100% locally with zero external push servers, Firebase Cloud Messaging, or third-party telemetry.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Section: Mandatory Risk Disclaimer
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(com.pipbeacon.app.ui.theme.IndicatorNeutralBackground)
                    .border(1.dp, com.pipbeacon.app.ui.theme.GoldAccent.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .padding(16.dp)
                    .testTag("risk_disclaimer_card")
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Warning",
                            tint = IndicatorNeutral,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "MANDATORY FINANCIAL RISK DISCLAIMER",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = IndicatorNeutral
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Trading foreign exchange (Forex) and cryptocurrency contracts carries substantial risk of financial loss and is not appropriate for all investors. " +
                                "All signals, indicators, and analytics presented in PipBeacon are strictly educational, informational, and experimental rule-based mathematical outputs. " +
                                "PipBeacon never provides personalized financial advice, guarantees no profits or win rates, does not execute trades, and never connects to brokerage accounts.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Section: About PipBeacon
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(NavyCard)
                    .border(1.dp, NavyBorder, RoundedCornerShape(14.dp))
                    .padding(16.dp)
                    .testTag("about_pipbeacon_card")
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = TealAccent, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ABOUT PIPBEACON",
                            style = MaterialTheme.typography.labelSmall,
                            color = TealAccent,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "PipBeacon: Forex & Crypto",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Version 1.2.0 • Package: com.pipbeacon.app\n" +
                                "Architecture: 100% Zero-Cost Native Android\n" +
                                "• Kotlin & Jetpack Compose Material 3\n" +
                                "• Pure Mathematical Technical Indicators (EMA, RSI, MACD, ATR)\n" +
                                "• Room SQLite Local Persistence\n" +
                                "• WorkManager Local Job Scheduling\n" +
                                "• Free Permitted Rates (ECB & CoinCap Open APIs)\n" +
                                "• Zero Paid APIs & Zero Billable Cloud Services",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // Reset Settings Button
        item {
            OutlinedButton(
                onClick = onResetDefaults,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reset_defaults_button"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = IndicatorSell)
            ) {
                Text("Reset All Configurations to Factory Defaults")
            }
        }
    }
    }
}

@Composable
fun CandidateProviderCard(
    candidate: ProviderLicensingConfig,
    onToggle: (Boolean) -> Unit,
    onVerify: (Boolean) -> Unit
) {
    val isProhibited = candidate.commercialDisplayPermission == CommercialPermissionStatus.PROHIBITED

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isProhibited) IndicatorSellBackground else NavyCard)
            .border(1.dp, if (isProhibited) IndicatorSell.copy(alpha = 0.5f) else NavyBorder, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Column {
            // Header: Name & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = candidate.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isProhibited) IndicatorSell else TextPrimary
                    )
                    Text(
                        text = "Base URL: ${candidate.baseUrl}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }

                if (isProhibited) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(IndicatorSellBackground)
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text("PROHIBITED", color = IndicatorSell, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Switch(
                        checked = candidate.isEnabled,
                        onCheckedChange = onToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = TealAccent,
                            uncheckedThumbColor = TextSecondary,
                            uncheckedTrackColor = NavySurface
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Metadata rows
            ConfigMetadataRow("Auth Requirement:", candidate.authRequirement.label)
            ConfigMetadataRow("Refresh Limits:", candidate.dataRefreshLimits)
            ConfigMetadataRow("Attribution:", candidate.attributionRequirement)
            ConfigMetadataRow("Commercial Status:", candidate.commercialDisplayPermission.label,
                color = when (candidate.commercialDisplayPermission) {
                    CommercialPermissionStatus.PERMITTED_OPEN_DATA -> IndicatorBuy
                    CommercialPermissionStatus.PENDING_VERIFICATION -> IndicatorNeutral
                    else -> IndicatorSell
                }
            )
            ConfigMetadataRow("Written Permission:", candidate.writtenPermissionStatus.label)

            Spacer(modifier = Modifier.height(8.dp))

            // Compliance justification / verification note
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(NavySurfaceVariant)
                    .padding(8.dp)
            ) {
                Text(
                    text = candidate.complianceRuleJustification,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isProhibited) IndicatorSell else TextSecondary,
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )
            }

            if (!isProhibited) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (candidate.isVerifiedPermitted) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (candidate.isVerifiedPermitted) IndicatorBuy else IndicatorNeutral,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (candidate.isVerifiedPermitted) "Verified Permitted" else "Pending Verification",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (candidate.isVerifiedPermitted) IndicatorBuy else IndicatorNeutral,
                            fontSize = 10.sp
                        )
                    }

                    OutlinedButton(
                        onClick = { onVerify(!candidate.isVerifiedPermitted) },
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(28.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Text(
                            text = if (candidate.isVerifiedPermitted) "Revoke Verification" else "Mark Verified",
                            fontSize = 10.sp,
                            color = TealAccent
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConfigMetadataRow(label: String, value: String, color: Color = TextSecondary) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = TextMuted, fontSize = 10.sp)
        Text(text = value, style = MaterialTheme.typography.labelSmall, color = color, fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}
