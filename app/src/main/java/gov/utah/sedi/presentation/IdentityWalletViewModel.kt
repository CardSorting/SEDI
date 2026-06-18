package gov.utah.sedi.presentation

import androidx.lifecycle.ViewModel
import gov.utah.sedi.data.DemoIdentityRepository
import gov.utah.sedi.domain.ActivityEvent
import gov.utah.sedi.domain.ActivityKind
import gov.utah.sedi.domain.ConnectedInstitution
import gov.utah.sedi.domain.CredentialShareRecord
import gov.utah.sedi.domain.DelegationDraft
import gov.utah.sedi.domain.IdentityWalletState
import gov.utah.sedi.domain.PermissionStatus
import gov.utah.sedi.domain.RequestStatus
import gov.utah.sedi.domain.ShareResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class IdentityWalletViewModel(
    repository: DemoIdentityRepository = DemoIdentityRepository()
) : ViewModel() {
    private var eventCounter = 0
    private var shareCounter = 0
    private val _state = MutableStateFlow(repository.initialState())
    val state: StateFlow<IdentityWalletState> = _state.asStateFlow()

    fun verifyIdentity() {
        _state.update { current ->
            if (current.identityVerified) return@update current
            current.copy(
                identityVerified = true,
                activity = listOf(
                    newActivity(
                        kind = ActivityKind.IdentityVerified,
                        title = "State Identity Verified",
                        description = "State ID matched, Utah residency confirmed, and identity status verified.",
                        timestamp = "Just now",
                        institutionName = "State of Utah",
                        result = "Verified"
                    )
                ) + current.activity
            )
        }
    }

    fun completeOnboarding() {
        _state.update { current -> current.copy(onboardingComplete = true) }
    }

    fun denyRequest(requestId: String) {
        _state.update { current ->
            val request = current.requests.firstOrNull { it.id == requestId } ?: return@update current
            current.copy(
                requests = current.requests.map {
                    if (it.id == requestId) it.copy(status = RequestStatus.Denied) else it
                },
                activity = listOf(
                    newActivity(
                        kind = ActivityKind.RequestDenied,
                        title = "Request denied",
                        description = "${request.institutionName} was not granted access to ${request.requestedProof}.",
                        timestamp = "Just now",
                        institutionName = request.institutionName,
                        result = "Denied"
                    )
                ) + current.activity
            )
        }
    }

    fun approveRequestAndShare(requestId: String) {
        _state.update { current ->
            val request = current.requests.firstOrNull { it.id == requestId } ?: return@update current
            val uvuPermission = ConnectedInstitution(
                id = "uvu",
                name = "Utah Valley University",
                category = "Higher education",
                accessScope = "Enrollment eligibility",
                allowedData = listOf("Utah Residency Verification", "Enrollment eligibility confirmation"),
                hiddenData = listOf("Full address", "Birthdate", "State ID number", "Age Verification", "Professional License", "Activity History"),
                lastUsed = "Today",
                expiration = "30 days",
                status = PermissionStatus.Active
            )
            val institutions = current.institutions
                .filterNot { it.id == uvuPermission.id }
                .let { listOf(uvuPermission) + it }
            current.copy(
                requests = current.requests.map {
                    if (it.id == requestId) it.copy(status = RequestStatus.Approved) else it
                },
                institutions = institutions,
                credentialShareHistory = listOf(
                    newShareRecord(
                        credentialId = "residency",
                        recipient = uvuPermission.name,
                        purpose = request.purpose,
                        result = "Verified"
                    )
                ) + current.credentialShareHistory,
                lastShareResult = ShareResult(
                    credentialId = "residency",
                    credentialTitle = "Utah Residency",
                    recipient = uvuPermission.name,
                    sharedItems = request.sharedData,
                    hiddenItems = request.hiddenData,
                    institutionId = uvuPermission.id
                ),
                activity = listOf(
                    newActivity(
                        kind = ActivityKind.PermissionCreated,
                        title = "UVU connected permission created",
                        description = "Utah Valley University was added to Connected Institutions for enrollment eligibility.",
                        timestamp = "Just now",
                        institutionName = uvuPermission.name,
                        result = "Permission active",
                        institutionId = uvuPermission.id
                    ),
                    newActivity(
                        kind = ActivityKind.CredentialShared,
                        title = "Residency verification shared with Utah Valley University",
                        description = "Utah Valley University received verified Utah residency status. No full address was shared.",
                        timestamp = "Just now",
                        institutionName = uvuPermission.name,
                        result = "Shared",
                        institutionId = uvuPermission.id
                    )
                ) + current.activity
            )
        }
    }

    fun approveResidencyShare(recipient: String = "Requested institution") {
        val sharedItems = listOf("Utah residency verified", "Issuer: State of Utah", "Status: Active")
        val hiddenItems = listOf("Full address", "Birthdate", "State ID number", "Unrelated credentials")
        _state.update { current ->
            current.copy(
                credentialShareHistory = listOf(
                    newShareRecord(
                        credentialId = "residency",
                        recipient = recipient,
                        purpose = "Residency verification",
                        result = "Verified"
                    )
                ) + current.credentialShareHistory,
                lastShareResult = ShareResult(
                    credentialId = "residency",
                    credentialTitle = "Utah Residency",
                    recipient = recipient,
                    sharedItems = sharedItems,
                    hiddenItems = hiddenItems
                ),
                activity = listOf(
                    newActivity(
                        kind = ActivityKind.CredentialShared,
                        title = "Residency verification shared",
                        description = "$recipient received verified Utah residency status. No full address was shared.",
                        timestamp = "Just now",
                        institutionName = recipient,
                        result = "Shared"
                    )
                ) + current.activity
            )
        }
    }

    fun approveAgeShare(ageProof: String = "21+ verified") {
        val sharedItems = listOf(ageProof)
        val hiddenItems = listOf("Exact birthdate", "ID number", "Full legal record")
        _state.update { current ->
            current.copy(
                credentialShareHistory = listOf(
                    newShareRecord(
                        credentialId = "age",
                        recipient = "Requested institution",
                        purpose = "Age verification",
                        result = "Verified"
                    )
                ) + current.credentialShareHistory,
                lastShareResult = ShareResult(
                    credentialId = "age",
                    credentialTitle = "Age Verification",
                    recipient = "Requested institution",
                    sharedItems = sharedItems,
                    hiddenItems = hiddenItems
                ),
                activity = listOf(
                    newActivity(
                        kind = ActivityKind.CredentialShared,
                        title = "Age proof shared",
                        description = "Shared $ageProof without exact birthdate or ID number.",
                        timestamp = "Just now",
                        institutionName = "Requested institution",
                        result = "Shared"
                    )
                ) + current.activity
            )
        }
    }

    fun approveLicenseShare(recipient: String = "Requested institution") {
        val sharedItems = listOf("License active", "Issuer: Utah Division of Professional Licensing", "Expiration: December 31, 2026")
        val hiddenItems = listOf("Unrelated credentials", "Personal identity details not needed")
        _state.update { current ->
            current.copy(
                credentialShareHistory = listOf(
                    newShareRecord(
                        credentialId = "license",
                        recipient = recipient,
                        purpose = "License verification",
                        result = "Verified"
                    )
                ) + current.credentialShareHistory,
                lastShareResult = ShareResult(
                    credentialId = "license",
                    credentialTitle = "Professional License",
                    recipient = recipient,
                    sharedItems = sharedItems,
                    hiddenItems = hiddenItems
                ),
                activity = listOf(
                    newActivity(
                        kind = ActivityKind.CredentialShared,
                        title = "License verification shared",
                        description = "$recipient received active license verification without unrelated identity details.",
                        timestamp = "Just now",
                        institutionName = recipient,
                        result = "Shared"
                    )
                ) + current.activity
            )
        }
    }

    fun clearLastShareResult() {
        _state.update { current -> current.copy(lastShareResult = null) }
    }

    fun updateDelegationDraft(recipient: String? = null, allowedAction: String? = null, duration: String? = null) {
        _state.update { current ->
            current.copy(
                delegationDraft = current.delegationDraft.copy(
                    recipient = recipient ?: current.delegationDraft.recipient,
                    allowedAction = allowedAction ?: current.delegationDraft.allowedAction,
                    duration = duration ?: current.delegationDraft.duration
                )
            )
        }
    }

    fun grantDelegation() {
        _state.update { current ->
            val draft = current.delegationDraft
            val recipientName = draft.recipient.ifBlank { "Temporary delegate" }
            val delegationId = "delegation-${eventCounter + 1}"
            val permission = ConnectedInstitution(
                id = delegationId,
                name = recipientName,
                category = "Temporary delegate",
                accessScope = draft.allowedAction,
                allowedData = listOf(draft.allowedAction, "Time-limited wallet approval"),
                hiddenData = listOf("Birthdate", "ID number", "Full address", "Document image"),
                lastUsed = "Just now",
                expiration = draft.duration,
                status = PermissionStatus.Active
            )
            current.copy(
                institutions = listOf(permission) + current.institutions,
                delegationDraft = DelegationDraft(),
                activity = listOf(
                    newActivity(
                        kind = ActivityKind.TemporaryAccessGranted,
                        title = "Temporary access granted",
                        description = "$recipientName can ${draft.allowedAction.lowercase()} for ${draft.duration}.",
                        timestamp = "Just now",
                        institutionName = recipientName,
                        result = "Temporary access active",
                        institutionId = delegationId
                    )
                ) + current.activity
            )
        }
    }

    fun revokeAccess(institutionId: String) {
        _state.update { current ->
            val institution = current.institutions.firstOrNull { it.id == institutionId } ?: return@update current
            if (institution.status == PermissionStatus.Revoked) return@update current
            current.copy(
                institutions = current.institutions.map {
                    if (it.id == institutionId) it.copy(status = PermissionStatus.Revoked, lastUsed = "Revoked today") else it
                },
                activity = listOf(
                    newActivity(
                        kind = ActivityKind.AccessRevoked,
                        title = if (institution.id == "uvu") "UVU access revoked" else "Access revoked",
                        description = "${institution.name} can no longer verify residency through this permission. Past verification remains visible in Activity.",
                        timestamp = "Just now",
                        institutionName = institution.name,
                        result = "Revoked",
                        institutionId = institutionId
                    )
                ) + current.activity
            )
        }
    }

    private fun newShareRecord(
        credentialId: String,
        recipient: String,
        purpose: String,
        result: String
    ): CredentialShareRecord {
        shareCounter += 1
        return CredentialShareRecord(
            id = "share-$shareCounter",
            credentialId = credentialId,
            recipient = recipient,
            timestamp = "Just now",
            purpose = purpose,
            result = result
        )
    }

    private fun newActivity(
        kind: ActivityKind,
        title: String,
        description: String,
        timestamp: String,
        institutionName: String,
        result: String,
        institutionId: String? = null
    ): ActivityEvent {
        eventCounter += 1
        return ActivityEvent(
            id = "event-$eventCounter",
            kind = kind,
            title = title,
            description = description,
            timestamp = timestamp,
            institutionName = institutionName,
            result = result,
            institutionId = institutionId
        )
    }
}