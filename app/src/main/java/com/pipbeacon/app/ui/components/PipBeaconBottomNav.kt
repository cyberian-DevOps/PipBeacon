package com.pipbeacon.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CandlestickChart
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pipbeacon.app.ui.theme.GoldAccentBright
import com.pipbeacon.app.ui.theme.NavyBorder
import com.pipbeacon.app.ui.theme.NavySurface
import com.pipbeacon.app.ui.theme.NavySurfaceVariant
import com.pipbeacon.app.ui.theme.TealAccent
import com.pipbeacon.app.ui.theme.TextMuted
import com.pipbeacon.app.ui.theme.TextPrimary

enum class NavigationDestination(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val isBottomBarItem: Boolean = true
) {
    HOME("home", "Pulse", Icons.Default.Dashboard, true),
    RATES("rates", "Markets", Icons.AutoMirrored.Filled.TrendingUp, true),
    SIGNALS("signals", "Signals", Icons.Default.CrisisAlert, true),
    NEWS("news", "News", Icons.Default.Newspaper, true),
    PROFILE("profile", "Profile", Icons.Default.Person, true),

    // Additional modules accessible from dashboard / profile shortcuts
    FOREX_ACADEMY("forex_academy", "Forex Academy", Icons.Default.CurrencyExchange, false),
    CRYPTO_ACADEMY("crypto_academy", "Crypto Academy", Icons.Default.CurrencyBitcoin, false),
    ANALYSIS("analysis", "Analysis", Icons.Default.CandlestickChart, false),
    SIGNAL_ENGINE("signal_engine", "Forex Engine", Icons.Default.Radar, false),
    SETTINGS("settings", "Settings", Icons.Default.Settings, false),
    AI_TOOLS("ai_tools", "AI Tools", Icons.Default.AutoAwesome, false),
    CALCULATORS("calculators", "Calculators", Icons.Default.Calculate, false),
    LESSONS("lessons", "Academy", Icons.AutoMirrored.Filled.MenuBook, false),
    FOREX("forex", "Forex", Icons.Default.CurrencyExchange, false),
    CRYPTO("crypto", "Crypto", Icons.Default.CurrencyBitcoin, false)
}

@Composable
fun PipBeaconBottomNav(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .background(NavySurface)
            .windowInsetsPadding(WindowInsets.navigationBars),
        containerColor = NavySurface,
        tonalElevation = 6.dp
    ) {
        val bottomItems = NavigationDestination.entries.filter { it.isBottomBarItem }
        bottomItems.forEach { destination ->
            val isSelected = currentRoute == destination.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(destination.route) },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.title
                    )
                },
                label = {
                    Text(
                        text = destination.title,
                        fontSize = 9.sp,
                        maxLines = 1,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    selectedTextColor = GoldAccentBright,
                    indicatorColor = GoldAccentBright,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted
                ),
                modifier = Modifier.testTag("nav_item_${destination.route}")
            )
        }
    }
}
