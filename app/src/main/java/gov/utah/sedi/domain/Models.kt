package gov.utah.sedi.domain

enum class CredentialType {
    StateIdentity,
    AgeVerification,
    UtahResidency,
    ProfessionalLicense
}

data class Credential(
    val id: String,
    val type: CredentialType,
    val title: String,
    val subtitle: String,
    val verifiedLabel: String,
    val lastVerified: String,
    val expires: String
)

enum class RequestStatus {
    Pending,
    Approved,
    Denied
}

data class VerificationRequest(
    val id: String,
    val institutionName: String,
    val title: String,
    val purpose: String,
    val requestedProof: String,
    val sharedData: List<String>,
    val hiddenData: List<String>,
    val receivedAt: String,
    val expires: String,
    val status: RequestStatus = RequestStatus.Pending
)

enum class PermissionStatus {
    Active,
    Revoked,
    Expired
}

data class ConnectedInstitution(
    val id: String,
    val name: String,
    val category: String,
    val accessScope: String,
    val allowedData: List<String>,
    val hiddenData: List<String>,
    val lastUsed: String,
    val expiration: String,
    val status: PermissionStatus
)

enum class ActivityKind {
    IdentityVerified,
    RequestReceived,
    CredentialShared,
    PermissionCreated,
    RequestApproved,
    RequestDenied,
    TemporaryAccessGranted,
    AccessRevoked
}

data class ActivityEvent(
    val id: String,
    val kind: ActivityKind,
    val title: String,
    val description: String,
    val timestamp: String,
    val institutionName: String = "Wallet",
    val result: String,
    val institutionId: String? = null
)

data class DelegationDraft(
    val recipient: String = "",
    val allowedAction: String = "Confirm Utah residency status",
    val duration: String = "72 hours"
)

data class IdentityWalletState(
    val identityVerified: Boolean = false,
    val onboardingComplete: Boolean = false,
    val credentials: List<Credential> = emptyList(),
    val requests: List<VerificationRequest> = emptyList(),
    val institutions: List<ConnectedInstitution> = emptyList(),
    val activity: List<ActivityEvent> = emptyList(),
    val delegationDraft: DelegationDraft = DelegationDraft()
)
