# SEDI State Transitions

This ledger records the implemented local state transitions in `IdentityWalletViewModel`.

## Onboarding Completion

- Function: `completeOnboarding()`
- Effect: sets `onboardingComplete` to `true`.
- Activity effect: prepends an `IdentityVerified` activity event titled `State ID verified`.

## Deny Verification Request

- Function: `denyRequest(requestId)`
- Effect: updates the matching `VerificationRequest.status` to `Denied`.
- Activity effect: prepends a `RequestDenied` activity event naming the requesting institution and requested proof.

## Approve Request and Share Credential

- Function: `approveRequestAndShare(requestId)`
- Effect: updates the matching `VerificationRequest.status` to `Approved`.
- Institution effect: creates or replaces the `University of Utah` connected institution permission with active residency verification access, allowed shared data from the request, hidden data from the request, and expiration `June 18, 2027`.
- Activity effect: prepends `CredentialShared` and `RequestApproved` activity events.

## Grant Delegation

- Function: `grantDelegation()`
- Effect: creates a new active connected institution entry using the current delegation draft recipient/action/duration.
- Draft effect: resets `delegationDraft` to defaults.
- Activity effect: prepends a `TemporaryAccessGranted` activity event.

## Revoke Access

- Function: `revokeAccess(institutionId)`
- Effect: updates the matching `ConnectedInstitution.status` to `Revoked` and changes `lastUsed` to `Revoked just now`.
- Activity effect: prepends an `AccessRevoked` activity event naming the revoked institution and access scope.

## Activity Read Model

- Screen: Activity tab in `MainActivity.kt`.
- Source: live `state.activity` from `IdentityWalletViewModel`.
- Effect: reflects local state changes caused by onboarding, denial, approval/share, delegation, and revocation actions.