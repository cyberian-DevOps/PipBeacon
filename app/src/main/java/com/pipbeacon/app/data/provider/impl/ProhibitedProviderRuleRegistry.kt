package com.pipbeacon.app.data.provider.impl

import com.pipbeacon.app.data.provider.model.AuthRequirement
import com.pipbeacon.app.data.provider.model.CommercialPermissionStatus
import com.pipbeacon.app.data.provider.model.ProviderLicensingConfig
import com.pipbeacon.app.data.provider.model.WrittenPermissionStatus

object ProhibitedProviderRuleRegistry {

    /**
     * Catalog of explicitly rejected candidate providers that violate PipBeacon's strict data rules.
     * Enforces auditability and compliance with Terms of Service.
     */
    val prohibitedCandidates: List<ProviderLicensingConfig> = listOf(
        ProviderLicensingConfig(
            id = "prohibited_tradingview_feed",
            name = "TradingView Unofficial Scraped/Widget Feed",
            baseUrl = "https://www.tradingview.com",
            authRequirement = AuthRequirement.PAID_SUBSCRIPTION_PROHIBITED,
            dataRefreshLimits = "N/A - Prohibited",
            attributionRequirement = "TradingView data is restricted to native interactive charts.",
            commercialDisplayPermission = CommercialPermissionStatus.PROHIBITED,
            writtenPermissionStatus = WrittenPermissionStatus.NOT_REQUESTED,
            isEnabled = false,
            isVerifiedPermitted = false,
            complianceRuleJustification = "REJECTED under Rule 5: 'Do not use TradingView data as an automated signal-engine input' & Rule 3: 'Do not scrape websites'.",
            documentationUrl = "https://www.tradingview.com/policies/"
        ),
        ProviderLicensingConfig(
            id = "prohibited_personal_feed_relay",
            name = "Personal-Tier API Key Public Relay",
            baseUrl = "N/A - Personal credentials",
            authRequirement = AuthRequirement.PAID_SUBSCRIPTION_PROHIBITED,
            dataRefreshLimits = "N/A - Prohibited",
            attributionRequirement = "Prohibited from public distribution.",
            commercialDisplayPermission = CommercialPermissionStatus.PROHIBITED,
            writtenPermissionStatus = WrittenPermissionStatus.NOT_REQUESTED,
            isEnabled = false,
            isVerifiedPermitted = false,
            complianceRuleJustification = "REJECTED under Rule 2: 'Do not use personal-only API access as a public application feed' & Rule 4: 'Do not bypass API keys or restrictions'.",
            documentationUrl = "https://pipbeacon.app/governance"
        ),
        ProviderLicensingConfig(
            id = "prohibited_html_scraping_feed",
            name = "Direct Broker Webpage Scraper",
            baseUrl = "N/A - Scraping forbidden",
            authRequirement = AuthRequirement.NONE_OPEN,
            dataRefreshLimits = "N/A - Prohibited",
            attributionRequirement = "N/A",
            commercialDisplayPermission = CommercialPermissionStatus.PROHIBITED,
            writtenPermissionStatus = WrittenPermissionStatus.NOT_REQUESTED,
            isEnabled = false,
            isVerifiedPermitted = false,
            complianceRuleJustification = "REJECTED under Rule 3: 'Do not scrape websites'. Scraping violates computer fraud and misuse statutes and provider terms.",
            documentationUrl = "https://pipbeacon.app/governance"
        )
    )
}
