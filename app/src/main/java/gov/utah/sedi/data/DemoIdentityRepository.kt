package gov.utah.sedi.data

import gov.utah.sedi.domain.ActivityEvent
import gov.utah.sedi.domain.ActivityKind
import gov.utah.sedi.domain.ConnectedInstitution
import gov.utah.sedi.domain.Credential
import gov.utah.sedi.domain.CredentialType
import gov.utah.sedi.domain.IdentityWalletState
import gov.utah.sedi.domain.PermissionStatus
import gov.utah.sedi.domain.VerificationRequest

class DemoIdentityRepository {
    fun initialState(): IdentityWalletState = IdentityWalletState(
        credentials = listOf(
            Credential(
                id = "state-id",
                type = CredentialType.StateIdentity,
                title = "Verified State Identity",
                subtitle = "State of Utah identity verified",
                verifiedLabel = "Verified by State of Utah",
                lastVerified = "Today",
                expires = "May 18, 2030"
            ),
            Credential(
                id = "residency",
                type = CredentialType.UtahResidency,
                title = "Utah Residency",
                subtitle = "Resident status only",
                verifiedLabel = "Residency verified",
                lastVerified = "Today",
                expires = "June 18, 2027"
            ),
            Credential(
                id = "age",
                type = CredentialType.AgeVerification,
                title = "Age Verification",
                subtitle = "Over 21 confirmation",
                verifiedLabel = "Age verified",
                lastVerified = "Today",
                expires = "May 18, 2030"
            ),
            Credential(
                id = "license",
                type = CredentialType.ProfessionalLicense,
                title = "Professional License",
                subtitle = "Active license confirmation",
                verifiedLabel = "License in good standing",
                lastVerified = "Yesterday",
                expires = "December 31, 2026"
            )
        ),
        requests = listOf(
            VerificationRequest(
                id = "uvu-residency",
                institutionName = "Utah Valley University",
                title = "Utah Valley University requests proof of Utah residency",
                purpose = "Enrollment eligibility",
                requestedProof = "Utah Residency Verified",
                sharedData = listOf("Utah Residency: Verified", "Issuer: State of Utah", "Verification status: Active"),
                hiddenData = listOf("Full address", "Birthdate", "State ID number", "Unrelated credentials"),
                receivedAt = "2 min ago",
                expires = "7 days"
            )
        ),
        institutions = listOf(
            ConnectedInstitution(
                id = "health",
                name = "Department of Health",
                category = "State agency",
                accessScope = "No active access",
                allowedData = listOf("No active access"),
                hiddenData = listOf("Full address", "Birthdate", "State ID number", "Unrelated credentials"),
                lastUsed = "No active access",
                expiration = "No active access",
                status = PermissionStatus.Revoked
            ),
            ConnectedInstitution(
                id = "licensing",
                name = "Licensing Board",
                category = "State board",
                accessScope = "No active access",
                allowedData = listOf("No active access"),
                hiddenData = listOf("Full address", "Birthdate", "State ID number", "Unrelated credentials"),
                lastUsed = "No active access",
                expiration = "No active access",
                status = PermissionStatus.Revoked
            ),
            ConnectedInstitution(
                id = "bank",
                name = "Bank",
                category = "Financial institution",
                accessScope = "No active access",
                allowedData = listOf("No active access"),
                hiddenData = listOf("Full address", "Birthdate", "State ID number", "Unrelated credentials"),
                lastUsed = "No active access",
                expiration = "No active access",
                status = PermissionStatus.Revoked
            )
        ),
        activity = listOf(
            ActivityEvent(
                id = "seed-residency-active",
                kind = ActivityKind.IdentityVerified,
                title = "Utah Residency Credential Active",
                description = "State of Utah residency credential is active in the wallet.",
                timestamp = "Today",
                institutionName = "State of Utah",
                result = "Active"
            ),
            ActivityEvent(
                id = "seed-request",
                kind = ActivityKind.RequestReceived,
                title = "UVU residency request received",
                description = "Utah Valley University requested proof of Utah residency for enrollment eligibility.",
                timestamp = "2 min ago",
                institutionName = "Utah Valley University",
                result = "Pending review",
                institutionId = "uvu"
            )
        )
    )
}