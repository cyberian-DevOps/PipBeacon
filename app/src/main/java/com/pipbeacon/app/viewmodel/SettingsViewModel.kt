package com.pipbeacon.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pipbeacon.app.data.MarketRepository
import com.pipbeacon.app.data.PermittedProviderMode
import com.pipbeacon.app.data.ProviderConfig
import com.pipbeacon.app.data.SettingsRepository
import com.pipbeacon.app.data.provider.model.ProviderLicensingConfig
import com.pipbeacon.app.data.provider.model.ProviderStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val diagnosticMessage: String = "",
    val isTestingConnection: Boolean = false
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val marketRepository: MarketRepository
) : ViewModel() {

    val config: StateFlow<ProviderConfig> = settingsRepository.config
        .stateIn(viewModelScope, SharingStarted.Lazily, settingsRepository.getConfig())

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    val candidateConfigs: StateFlow<List<ProviderLicensingConfig>> =
        marketRepository.marketDataRepository.candidateConfigsFlow
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val providerStatuses: StateFlow<List<ProviderStatus>> =
        marketRepository.marketDataRepository.providerStatusesFlow
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setProviderMode(mode: PermittedProviderMode) {
        settingsRepository.updateMode(mode)
        viewModelScope.launch {
            marketRepository.refreshPrices()
        }
    }

    fun setCustomUrl(url: String) {
        settingsRepository.updateCustomUrl(url)
        marketRepository.marketDataRepository.updateCustomBridgeUrl(url)
    }

    fun setRefreshInterval(seconds: Int) {
        settingsRepository.updateRefreshInterval(seconds)
    }

    fun setAutoSync(enabled: Boolean) {
        settingsRepository.updateAutoSync(enabled)
    }

    fun setRiskTolerance(tolerance: String) {
        settingsRepository.updateRiskTolerance(tolerance)
    }

    fun setThemeMode(theme: String) {
        settingsRepository.updateThemeMode(theme)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        settingsRepository.updateNotificationsEnabled(enabled)
    }

    fun setBackgroundRefreshInterval(minutes: Int) {
        settingsRepository.updateBackgroundRefreshInterval(minutes)
    }

    fun completeOnboarding() {
        settingsRepository.updateOnboardingCompleted(true)
    }

    fun resetOnboarding() {
        settingsRepository.updateOnboardingCompleted(false)
    }

    fun toggleCandidateProvider(providerId: String, enable: Boolean) {
        marketRepository.marketDataRepository.toggleProvider(providerId, enable)
        viewModelScope.launch {
            marketRepository.refreshPrices()
        }
    }

    fun verifyCandidateProvider(providerId: String, verified: Boolean) {
        marketRepository.marketDataRepository.verifyProvider(providerId, verified)
        viewModelScope.launch {
            marketRepository.refreshPrices()
        }
    }

    fun testConnection() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isTestingConnection = true,
                diagnosticMessage = "Testing network bridge and provider compliance..."
            )
            marketRepository.refreshPrices()
            val hasActive = marketRepository.marketDataRepository.hasAnyActivePermittedProvider()
            val msg = if (hasActive) {
                "Provider verification active. Live rates synchronized."
            } else {
                "Waiting for permitted data provider. No unverified endpoints enabled."
            }
            _uiState.value = _uiState.value.copy(
                isTestingConnection = false,
                diagnosticMessage = msg
            )
        }
    }

    fun resetToDefaults() {
        settingsRepository.updateMode(PermittedProviderMode.UNCONFIGURED)
        settingsRepository.updateCustomUrl("")
        settingsRepository.updateRefreshInterval(30)
        settingsRepository.updateAutoSync(false)
        settingsRepository.updateRiskTolerance("Moderate")
        marketRepository.marketDataRepository.toggleProvider(marketRepository.marketDataRepository.ecbProvider.config.id, false)
        marketRepository.marketDataRepository.toggleProvider(marketRepository.marketDataRepository.coinCapProvider.config.id, false)
        marketRepository.marketDataRepository.toggleProvider(marketRepository.marketDataRepository.customBridgeProvider.config.id, false)
        viewModelScope.launch {
            marketRepository.refreshPrices()
        }
    }
}
