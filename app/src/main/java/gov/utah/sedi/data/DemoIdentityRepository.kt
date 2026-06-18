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
                id = "uofu-residency",
                institutionName = "University of Utah",
                title = "University of Utah requests proof of residency",
                purpose = "Confirm in-state tuition eligibility for the upcoming academic term.",
                requestedProof = "Utah residency verification",
                sharedData = listOf("Utah residency verified"),
                hiddenData = listOf("Full address", "Birthdate", "ID number"),
                receivedAt = "2 min ago",
                expires = "Expires in 7 days"
            ),
            VerificationRequest(
                id = "health-identity",
                institutionName = "Department of Health",
                title = "Department of Health requests identity confirmation",
                purpose = "Match your state identity for a health services application.",
                requestedProof = "Identity confirmation",
                sharedData = listOf("Identity match confirmed"),
                hiddenData = listOf("Full address", "Birthdate", "ID number"),
                receivedAt = "12 min ago",
                expires = "Expires in 14 days"
            ),
            VerificationRequest(
                id = "bank-age",
                institutionName = "Bank",
                title = "Bank requests age verification",
                purpose = "Confirm eligibility for an age-restricted financial product.",
                requestedProof = "Age verification",
                sharedData = listOf("Over 18 verified"),
                hiddenData = listOf("Birthdate", "Full address", "ID number"),
                receivedAt = "18 min ago",
                expires = "Expires in 10 days"
            )
        ),
        institutions = listOf(
            ConnectedInstitution(
                id = "health",
                name = "Department of Health",
                category = "State agency",
                accessScope = "Identity confirmation",
                allowedData = listOf("Identity match confirmed", "Utah residency verified"),
                hiddenData = listOf("Full address", "Birthdate", "ID number", "Unrelated credentials"),
                lastUsed = "Yesterday",
                expiration = "Sep 30, 2026",
                status = PermissionStatus.Active
            ),
            ConnectedInstitution(
                id = "licensing",
                name = "Licensing Board",
                category = "State board",
                accessScope = "Professional license standing",
                allowedData = listOf("License active", "Expiration date", "Disciplinary status: none"),
                hiddenData = listOf("Full address", "Birthdate", "ID number", "Unrelated credentials"),
                lastUsed = "May 28, 2026",
                expiration = "Dec 31, 2026",
                status = PermissionStatus.Active
            ),
            ConnectedInstitution(
                id = "bank",
                name = "Bank",
                category = "Financial institution",
                accessScope = "Age verification",
                allowedData = listOf("Over 18 verified"),
                hiddenData = listOf("Full address", "Birthdate", "ID number", "Unrelated credentials"),
                lastUsed = "Apr 14, 2026",
                expiration = "Apr 14, 2027",
                status = PermissionStatus.Active
            )
        ),
        activity = listOf(
            ActivityEvent(
                id = "seed-request",
                kind = ActivityKind.RequestReceived,
                title = "Residency request received",
                description = "University of Utah requested proof of Utah residency.",
                timestamp = "2 min ago",
                institutionName = "University of Utah",
                result = "Pending review",
                institutionId = "university-of-utah"
            )
        )
    )
}