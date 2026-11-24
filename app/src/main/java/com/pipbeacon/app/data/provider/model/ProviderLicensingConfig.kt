package com.pipbeacon.app.data.provider.model

enum class AuthRequirement(val label: String) {
    NONE_OPEN("None (Open Public API)"),
    FREE_API_KEY("Free Non-Billable API Key"),
    OAUTH2("OAuth2 User Grant"),
    PAID_SUBSCRIPTION_PROHIBITED("Paid Subscription (Strictly Prohibited)")
}

enum class CommercialPermissionStatus(val label: String) {
    PENDING_VERIFICATION("Pending Legal Verification"),
    PERMITTED_OPEN_DATA("Permitted (Open Data / Public Domain)"),
    RESTRICTED_NON_COMMERCIAL("Restricted to Non-Commercial Use"),
    PROHIBITED("Prohibited by Provider Terms of Service")
}

enum class WrittenPermissionStatus(val label: String) {
    NOT_REQUESTED("Not Requested"),
    IN_REVIEW("In Formal Review"),
    GRANTED("Explicit Written Permission Granted"),
    NOT_REQUIRED_OPEN_PUBLIC_DOMAIN("Not Required (Open Government / Public Domain License)")
}

data class ProviderLicensingConfig(
    val id: String,
    val name: String,
    val baseUrl: String,
    val authRequirement: AuthRequirement,
    val dataRefreshLimits: String,
    val attributionRequirement: String,
    val commercialDisplayPermission: CommercialPermissionStatus,
    val writtenPermissionStatus: WrittenPermissionStatus,
    val isEnabled: Boolean = false,
    val isVerifiedPermitted: Boolean = false,
    val complianceRuleJustification: String = "",
    val documentationUrl: String = ""
)
