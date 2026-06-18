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

    fun completeOnboarding() {
        _state.update { current ->
            current.copy(
                onboardingComplete = true,
                activity = listOf(
                    newActivity(
                        kind = ActivityKind.IdentityVerified,
                        title = "State ID verified",
                        description = "State of Utah identity verification completed in wallet.",
                        timestamp = "Just now"
                    )
                ) + current.activity
            )
        }
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
                        timestamp = "Just now"
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
                accessScope = "Residency verification for tuition eligibility",
                allowedData = request.sharedData,
                hiddenData = request.hiddenData,
                lastUsed = "Just now",
                expiration = "June 18, 2027",
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
                        kind = ActivityKind.CredentialShared,
                        title = "Residency shared",
                        description = "Shared Utah residency verification with ${request.institutionName}.",
                        timestamp = "Just now",
                        institutionId = universityPermission.id
                    ),
                    newActivity(
                        kind = ActivityKind.RequestApproved,
                        title = "Request approved",
                        description = "Approved ${request.institutionName}'s verification request.",
                        timestamp = "Just now",
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
                hiddenData = listOf("Birthdate", "State ID number", "Full address", "Document image"),
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
                    if (it.id == institutionId) it.copy(status = PermissionStatus.Revoked, lastUsed = "Revoked just now") else it
                },
                activity = listOf(
                    newActivity(
                        kind = ActivityKind.AccessRevoked,
                        title = "Access revoked",
                        description = "${institution.name} can no longer use ${institution.accessScope}.",
                        timestamp = "Just now",
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
        institutionId: String? = null
    ): ActivityEvent {
        eventCounter += 1
        return ActivityEvent(
            id = "event-$eventCounter",
            kind = kind,
            title = title,
            description = description,
            timestamp = timestamp,
            institutionId = institutionId
        )
    }
}