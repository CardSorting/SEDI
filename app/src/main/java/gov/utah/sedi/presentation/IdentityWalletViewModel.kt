package gov.utah.sedi.presentation

import androidx.lifecycle.ViewModel
import gov.utah.sedi.data.DemoIdentityRepository
import gov.utah.sedi.domain.ActivityEvent
import gov.utah.sedi.domain.ActivityKind
import gov.utah.sedi.domain.ConnectedInstitution
import gov.utah.sedi.domain.DelegationDraft
import gov.utah.sedi.domain.IdentityWalletState
import gov.utah.sedi.domain.PermissionStatus
import gov.utah.sedi.domain.RequestStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class IdentityWalletViewModel(
    repository: DemoIdentityRepository = DemoIdentityRepository()
) : ViewModel() {
    private var eventCounter = 0
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
                        description = "State ID check, residency check, and identity match check completed.",
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
            val universityPermission = ConnectedInstitution(
                id = "university-of-utah",
                name = "University of Utah",
                category = "Higher education",
                accessScope = "Utah residency verification and student eligibility status",
                allowedData = listOf("Utah residency verification", "Student eligibility status"),
                hiddenData = listOf("Full address", "Birthdate", "ID number", "Unrelated credentials"),
                lastUsed = "Today",
                expiration = "Expires in 30 days",
                status = PermissionStatus.Active
            )
            val institutions = current.institutions
                .filterNot { it.id == universityPermission.id }
                .let { listOf(universityPermission) + it }
            current.copy(
                requests = current.requests.map {
                    if (it.id == requestId) it.copy(status = RequestStatus.Approved) else it
                },
                institutions = institutions,
                activity = listOf(
                    newActivity(
                        kind = ActivityKind.PermissionCreated,
                        title = "University access created",
                        description = "University of Utah can verify residency for the approved purpose.",
                        timestamp = "Just now",
                        institutionName = request.institutionName,
                        result = "Permission active",
                        institutionId = universityPermission.id
                    ),
                    newActivity(
                        kind = ActivityKind.CredentialShared,
                        title = "Residency shared with University of Utah",
                        description = "University of Utah received residency verification. No full address was shared.",
                        timestamp = "Just now",
                        institutionName = request.institutionName,
                        result = "Shared",
                        institutionId = universityPermission.id
                    )
                ) + current.activity
            )
        }
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
                        title = "University access revoked",
                        description = "${institution.name} can no longer verify residency. Previous verification remains in history.",
                        timestamp = "Just now",
                        institutionName = institution.name,
                        result = "Revoked",
                        institutionId = institutionId
                    )
                ) + current.activity
            )
        }
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