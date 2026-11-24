package com.pipbeacon.app.data

enum class PermittedProviderMode(val displayName: String, val description: String) {
    UNCONFIGURED(
        "Unconfigured (Safe Compliance)",
        "Shows 'Waiting for permitted data provider'. No external network calls are made."
    ),
    FREE_PUBLIC_RATES(
        "Free Public Feeds (Zero Cost)",
        "Connects to free, non-billable open rate APIs without API keys or subscriptions."
    ),
    CUSTOM_ENDPOINT(
        "Custom REST Endpoint",
        "Connects to your self-hosted market data bridge or private webhook."
    )
}

data class ProviderConfig(
    val mode: PermittedProviderMode = PermittedProviderMode.FREE_PUBLIC_RATES,
    val customUrl: String = "",
    val refreshIntervalSec: Int = 30,
    val autoSync: Boolean = true,
    val riskTolerance: String = "Moderate",
    val defaultLotSize: Double = 0.1,
    val accountBalance: Double = 10000.0,
    val themeMode: String = "SYSTEM", // "SYSTEM", "DARK", "LIGHT"
    val notificationsEnabled: Boolean = true,
    val backgroundRefreshIntervalMinutes: Int = 15 // 15, 30, 60, 0 = manual
)
