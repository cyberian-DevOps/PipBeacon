package com.pipbeacon.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Obsidian Deep Navy backgrounds & cards in Dark mode, Crisp clean slate surfaces in Light mode
val NavyBackground: Color
    @Composable
    get() = if (LocalThemeIsDark.current) Color(0xFF0B0F17) else Color(0xFFF1F5F9)

val NavySurface: Color
    @Composable
    get() = if (LocalThemeIsDark.current) Color(0xFF1E293B) else Color(0xFFFFFFFF)

val NavySurfaceVariant: Color
    @Composable
    get() = if (LocalThemeIsDark.current) Color(0xFF141D2C) else Color(0xFFF8FAFC)

val NavyCard: Color
    @Composable
    get() = if (LocalThemeIsDark.current) Color(0xFF1E293B) else Color(0xFFFFFFFF)

val NavyCardElevated: Color
    @Composable
    get() = if (LocalThemeIsDark.current) Color(0xFF242C3D) else Color(0xFFF1F5F9)

val NavyBorder: Color
    @Composable
    get() = if (LocalThemeIsDark.current) Color(0xFF334155) else Color(0xFFCBD5E1)

val NavyBorderSubtle: Color
    @Composable
    get() = if (LocalThemeIsDark.current) Color(0xFF1E2433) else Color(0xFFE2E8F0)

// Luxury Golden Amber Accents (from reference images: buttons, badges, tutor icon)
val GoldAccent = Color(0xFFF59E0B)
val GoldAccentBright = Color(0xFFFBBF24)
val GoldAccentDark = Color(0xFFD97706)
val GoldAccentBackground = Color(0xFF332205)

// Cyan / Teal Accents
val TealAccent = Color(0xFF00D09C)
val TealAccentSecondary = Color(0xFF00B4D8)
val TealDark = Color(0xFF006B7D)

// Neon Emerald & Forest Green Indicators (BUY, TP hit, Win Rate, Positive Pips)
val IndicatorBuy = Color(0xFF00D09C)
val IndicatorStrongBuy = Color(0xFF059669)
val IndicatorBuyBackground: Color
    @Composable
    get() = if (LocalThemeIsDark.current) Color(0xFF062B1F) else Color(0xFFDCFCE7)
val IndicatorBuyBorder = Color(0xFF00D09C).copy(alpha = 0.35f)

// Ruby Coral Indicators (SELL, Stop Loss)
val IndicatorSell = Color(0xFFFF4B4B)
val IndicatorStrongSell = Color(0xFFDC2626)
val IndicatorSellBackground: Color
    @Composable
    get() = if (LocalThemeIsDark.current) Color(0xFF321216) else Color(0xFFFEE2E2)
val IndicatorSellBorder = Color(0xFFFF4B4B).copy(alpha = 0.35f)

// Neutral Indicators
val IndicatorNeutral = Color(0xFFFBBF24)
val IndicatorNeutralBackground: Color
    @Composable
    get() = if (LocalThemeIsDark.current) Color(0xFF332A0F) else Color(0xFFFEF3C7)

// Text Colors
val TextPrimary: Color
    @Composable
    get() = if (LocalThemeIsDark.current) Color(0xFFFFFFFF) else Color(0xFF0F172A)

val TextSecondary: Color
    @Composable
    get() = if (LocalThemeIsDark.current) Color(0xFF94A3B8) else Color(0xFF475569)

val TextMuted: Color
    @Composable
    get() = if (LocalThemeIsDark.current) Color(0xFF64748B) else Color(0xFF64748B)

// Candlestick Colors
val CandleBullish = Color(0xFF00D09C)
val CandleBearish = Color(0xFFFF4B4B)
