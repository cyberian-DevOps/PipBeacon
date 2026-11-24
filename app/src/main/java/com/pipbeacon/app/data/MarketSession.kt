package com.pipbeacon.app.data

data class MarketSession(
    val name: String,
    val city: String,
    val openUtcHour: Int,
    val closeUtcHour: Int,
    val isOpen: Boolean,
    val statusLabel: String
)
