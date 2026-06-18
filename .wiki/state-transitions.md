# SEDI State Transitions

This ledger records the implemented local state transitions in `IdentityWalletViewModel` after the native mobile journey restructure.

## Verify State Identity

- Function: `verifyIdentity()`
- Effect: sets `identityVerified` to `true` when it is not already verified.
- Activity effect: prepends an `IdentityVerified` activity event titled `State Identity Verified` with institution `State of Utah` and result `Verified`.

## Complete Onboarding

- Function: `completeOnboarding()`
- Effect: sets `onboardingComplete` to `true`.
- Activity effect: none. Verification activity is owned by `verifyIdentity()` to avoid duplicate setup entries.

## Deny Verification Request

- Function: `denyRequest(requestId)`
- Effect: updates the matching `VerificationRequest.status` to `Denied`.
- Activity effect: prepends a `RequestDenied` activity event naming the requesting institution and requested proof with result `Denied`.

## Approve Request and Share Credential

- Function: `approveRequestAndShare(requestId)`
- Effect: updates the matching `VerificationRequest.status` to `Approved`.
- Institution effect: creates or replaces the `University of Utah` connected institution permission with active Utah residency verification and student eligibility access.
- Permission data: allowed data is `Utah residency verification` and `Student eligibility status`; hidden data is `Full address`, `Birthdate`, `ID number`, and `Unrelated credentials`; expiration is `Expires in 30 days`; last used is `Today`.
- Activity effect: prepends `PermissionCreated` (`University access created`) and `CredentialShared` (`Residency shared with University of Utah`) events.

## Grant Delegation

- Function: `grantDelegation()`
- Effect: creates a new active connected institution entry using the current delegation draft recipient/action/duration.
- Draft effect: resets `delegationDraft` to defaults.
- Activity effect: prepends a `TemporaryAccessGranted` activity event with institution name set to the recipient and result `Temporary access active`.

## Revoke Access

- Function: `revokeAccess(institutionId)`
- Effect: updates the matching `ConnectedInstitution.status` to `Revoked` and changes `lastUsed` to `Revoked today`.
- Activity effect: prepends an `AccessRevoked` activity event titled `University access revoked` with result `Revoked`.

## Activity Read Model

- Screen: `Activity` tab in `MainActivity.kt`.
- Source: live `state.activity` from `IdentityWalletViewModel`.
- Display fields: timestamp, action/title, institution, and result.
- Effect: reflects local state changes caused by identity verification, request receipt seed data, approval/share, permission creation, denial, delegation, and revocation actions.
