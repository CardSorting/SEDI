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
                subtitle = "Utah Driver License •••• 2048",
                verifiedLabel = "State identity verified",
                lastVerified = "Today",
                expires = "May 18, 2030"
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
                id = "residency",
                type = CredentialType.UtahResidency,
                title = "Utah Residency",
                subtitle = "Resident status only",
                verifiedLabel = "Residency verified",
                lastVerified = "Today",
                expires = "June 18, 2027"
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
                title = "Proof of Utah residency requested",
                purpose = "Confirm in-state tuition eligibility for the upcoming academic term.",
                requestedProof = "Utah residency verification",
                sharedData = listOf("Utah residency verified", "Verification issued by State of Utah", "Valid through June 18, 2027"),
                hiddenData = listOf("Full address", "Birthdate", "State ID number", "Document image"),
                receivedAt = "2 min ago"
            ),
            VerificationRequest(
                id = "bank-age",
                institutionName = "Beehive Community Bank",
                title = "Age verification requested",
                purpose = "Open an age-restricted financial product.",
                requestedProof = "Over 18 status",
                sharedData = listOf("Over 18 verified", "Verification issued by State of Utah"),
                hiddenData = listOf("Birthdate", "State ID number", "Home address"),
                receivedAt = "18 min ago"
            )
        ),
        institutions = listOf(
            ConnectedInstitution(
                id = "health",
                name = "Department of Health",
                category = "State agency",
                accessScope = "Immunization eligibility confirmation",
                allowedData = listOf("Identity match confirmed", "Utah residency verified"),
                hiddenData = listOf("Full ID number", "Document image", "Financial data"),
                lastUsed = "Yesterday",
                expiration = "Sep 30, 2026",
                status = PermissionStatus.Active
            ),
            ConnectedInstitution(
                id = "licensing",
                name = "Professional Licensing Board",
                category = "State board",
                accessScope = "Professional license standing",
                allowedData = listOf("License active", "Expiration date", "Disciplinary status: none"),
                hiddenData = listOf("Birthdate", "Address", "State ID number"),
                lastUsed = "May 28, 2026",
                expiration = "Dec 31, 2026",
                status = PermissionStatus.Active
            ),
            ConnectedInstitution(
                id = "bank",
                name = "Beehive Community Bank",
                category = "Financial institution",
                accessScope = "Age and identity confirmation",
                allowedData = listOf("Identity verified", "Over 18 verified"),
                hiddenData = listOf("Full address", "Document image", "License number"),
                lastUsed = "Apr 14, 2026",
                expiration = "Apr 14, 2027",
                status = PermissionStatus.Active
            )
        ),
        activity = listOf(
            ActivityEvent(
                id = "seed-identity",
                kind = ActivityKind.IdentityVerified,
                title = "State ID ready for verification",
                description = "Your wallet is prepared to verify a State of Utah identity.",
                timestamp = "Before launch"
            )
        )
    )
}