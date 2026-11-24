package com.pipbeacon.app.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository {
    private val _config = MutableStateFlow(ProviderConfig())
    val config: Flow<ProviderConfig> = _config.asStateFlow()

    fun getConfig(): ProviderConfig = _config.value

    fun updateMode(mode: PermittedProviderMode) {
        _config.value = _config.value.copy(mode = mode)
    }

    fun updateCustomUrl(url: String) {
        _config.value = _config.value.copy(customUrl = url)
    }

    fun updateRefreshInterval(seconds: Int) {
        _config.value = _config.value.copy(refreshIntervalSec = seconds)
    }

    fun updateAutoSync(enabled: Boolean) {
        _config.value = _config.value.copy(autoSync = enabled)
    }

    fun updateRiskTolerance(tolerance: String) {
        _config.value = _config.value.copy(riskTolerance = tolerance)
    }

    fun updateLotSize(lotSize: Double) {
        _config.value = _config.value.copy(defaultLotSize = lotSize)
    }

    fun updateAccountBalance(balance: Double) {
        _config.value = _config.value.copy(accountBalance = balance)
    }

    fun updateThemeMode(theme: String) {
        _config.value = _config.value.copy(themeMode = theme)
    }

    fun updateNotificationsEnabled(enabled: Boolean) {
        _config.value = _config.value.copy(notificationsEnabled = enabled)
    }

    fun updateBackgroundRefreshInterval(minutes: Int) {
        _config.value = _config.value.copy(backgroundRefreshIntervalMinutes = minutes)
    }
}
