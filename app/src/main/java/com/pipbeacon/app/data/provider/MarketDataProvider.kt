package com.pipbeacon.app.data.provider

import com.pipbeacon.app.data.provider.model.ProviderLicensingConfig
import com.pipbeacon.app.data.provider.model.ProviderStatus

interface MarketDataProvider {
    val config: ProviderLicensingConfig

    suspend fun getStatus(): ProviderStatus

    suspend fun testConnection(): Result<Unit>
}
