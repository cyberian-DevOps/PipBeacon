package com.pipbeacon.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Showcase Midnight Navy backgrounds & elevated cards in Dark mode, Crisp Daylight Slate in Light mode
val NavyBackground: Color
    @Composable
    get() = if (LocalThemeIsDark.current) Color(0xFF060D1A) else Color(0xFFF4F7FB)

val NavyBackgroundBottom: Color
    @Composable
    get() = if (LocalThemeIsDark.current) Color(0xFF0B1528) else Color(0xFFEAF0F8)

val NavySurface: Color
    @Composable
    get() = if (LocalThemeIsDark.current) Color(0xFF0A1426) else Color(0xFFFFFFFF)

val NavySurfaceVariant: Color
    @Composable
    get() = if (LocalThemeIsDark.current) Color(0xFF091324) else Color(0xFFF1F5F9)

val NavyCard: Color
    @Composable
    get() = if (LocalThemeIsDark.current) Color(0xFF0F1C33) else Color(0xFFFFFFFF)

val NavyCardElevated: Color
    @Composable
    get() = if (LocalThemeIsDark.current) Color(0xFF13223B) else Color(0xFFF8FAFC)

val NavyBorder: Color
    @Composable
    get() = if (LocalThemeIsDark.current) Color(0xFF1E3454) else Color(0xFFD6E0EC)

val NavyBorderSubtle: Color
    @Composable
    get() = if (LocalThemeIsDark.current) Color(0xFF152540) else Color(0xFFE2E8F0)

// Metallic Gold Accents (Pill CTAs, Lighthouse Beacon, Active Tabs, Badges)
val GoldAccent = Color(0xFFF5B731)
val GoldAccentBright = Color(0xFFFACC15)
val GoldAccentDark = Color(0xFFD97706)
val GoldAccentBackground: Color
    @Composable
    get() = if (LocalThemeIsDark.current) Color(0xFF2B210B) else Color(0xFFFEF3C7)

// Electric Cyan / Sky Accents (Quick Access Rings, Chart Indicators, Play Badges)
val CyanAccent: Color
    @Composable
    get() = if (LocalThemeIsDark.current) Color(0xFF38BDF8) else Color(0xFF0284C7)

val TealAccent: Color
    @Composable
    get() = if (LocalThemeIsDark.current) Color(0xFF38BDF8) else Color(0xFF0284C7)

val TealAccentSecondary = Color(0xFF0EA5E9)
val TealDark = Color(0xFF075985)

// Emerald Profit / BUY Indicators
val IndicatorBuy = Color(0xFF10B981)
val IndicatorStrongBuy = Color(0xFF059669)
val IndicatorBuyBackground: Color
    @Composable
    get() = if (LocalThemeIsDark.current) Color(0xFF072A22) else Color(0xFFDCFCE7)
val IndicatorBuyBorder = Color(0xFF10B981).copy(alpha = 0.4f)

// Ruby Loss / SELL / High Impact Indicators
val IndicatorSell = Color(0xFFEF4444)
val IndicatorStrongSell = Color(0xFFDC2626)
val IndicatorSellBackground: Color
    @Composable
    get() = if (LocalThemeIsDark.current) Color(0xFF33141A) else Color(0xFFFEE2E2)
val IndicatorSellBorder = Color(0xFFEF4444).copy(alpha = 0.4f)

// Neutral / Medium Impact Indicators
val IndicatorNeutral = Color(0xFFF5B731)
val IndicatorNeutralBackground: Color
    @Composable
    get() = if (LocalThemeIsDark.current) Color(0xFF2A210B) else Color(0xFFFEF3C7)

// Text Colors
val TextPrimary: Color
    @Composable
    get() = if (LocalThemeIsDark.current) Color(0xFFF8FAFC) else Color(0xFF0F172A)

val TextSecondary: Color
    @Composable
    get() = if (LocalThemeIsDark.current) Color(0xFF9FB0C7) else Color(0xFF475569)

val TextMuted: Color
    @Composable
    get() = if (LocalThemeIsDark.current) Color(0xFF6B7F9E) else Color(0xFF64748B)

// Candlestick Colors
val CandleBullish = Color(0xFF10B981)
val CandleBearish = Color(0xFFEF4444)
