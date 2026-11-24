package com.pipbeacon.app.data.provider.model

enum class ProviderConnectionState(val displayName: String) {
    DISCONNECTED("Disconnected"),
    CONNECTING("Connecting"),
    CONNECTED("Connected (Live)"),
    STALE("Stale Data"),
    RATE_LIMITED("Rate Limited (Backing Off)"),
    DISABLED_PENDING_VERIFICATION("Disabled (Pending License Verification)"),
    ERROR("Connection Error")
}

data class ProviderStatus(
    val providerId: String,
    val providerName: String,
    val state: ProviderConnectionState,
    val lastSuccessfulSync: Long? = null,
    val lastErrorMessage: String? = null,
    val quoteCount: Int = 0,
    val isVerifiedPermitted: Boolean = false,
    val attributionText: String = "",
    val licensingNotice: String = "",
    val rateLimitNotice: String = ""
)
