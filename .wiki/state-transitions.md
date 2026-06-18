# SEDI State Transitions

This ledger records the implemented local state transitions in `IdentityWalletViewModel` after the Utah Valley University walkthrough revision.

## Verify State Identity

- Function: `verifyIdentity()`
- Effect: sets `identityVerified` to `true` when it is not already verified.
- Activity effect: prepends an `IdentityVerified` activity event titled `State Identity Verified` with institution `State of Utah` and result `Verified`.
- User-facing trigger: `StateVerificationChecksScreen` primary action `Continue`.

## Complete Onboarding

- Function: `completeOnboarding()`
- Effect: sets `onboardingComplete` to `true`.
- Activity effect: none. Verification activity is owned by `verifyIdentity()` to avoid duplicate setup entries.

## Deny Verification Request

- Function: `denyRequest(requestId)`
- Effect: updates the matching `VerificationRequest.status` to `Denied`.
- Activity effect: prepends a `RequestDenied` activity event naming the requesting institution and requested proof with result `Denied`.

## Approve UVU Request and Share Residency Verification

- Function: `approveRequestAndShare(requestId)`
- Primary demo request id: `uvu-residency`.
- Request effect: updates the matching `VerificationRequest.status` to `Approved`.
- Institution effect: creates or replaces the `uvu` connected institution permission for `Utah Valley University`.
- Permission data: allowed data is `Utah Residency Verification` and `Enrollment eligibility confirmation`; hidden data is `Full address`, `Birthdate`, `State ID number`, `Age Verification`, `Professional License`, and `Activity History`; expiration is `30 days`; last used is `Today`; status is `Active`.
- Activity effect: prepends `PermissionCreated` (`UVU connected permission created`) and `CredentialShared` (`Residency verification shared with Utah Valley University`) events.

## Grant Delegation

- Function: `grantDelegation()`
- Main demo status: separate optional flow, not part of the UVU primary walkthrough.
- Effect: creates a new active connected institution entry using the current delegation draft recipient/action/duration.
- Draft effect: resets `delegationDraft` to defaults.
- Activity effect: prepends a `TemporaryAccessGranted` activity event with institution name set to the recipient and result `Temporary access active`.

## Revoke UVU Access

- Function: `revokeAccess(institutionId)`
- Primary demo institution id: `uvu`.
- Effect: updates the matching `ConnectedInstitution.status` to `Revoked` and changes `lastUsed` to `Revoked today`.
- Activity effect: prepends an `AccessRevoked` activity event titled `UVU access revoked` with result `Revoked`.

## Activity Read Model

- Screen: `Activity` tab in `MainActivity.kt`.
- Source: live `state.activity` from `IdentityWalletViewModel`.
- Display fields: timestamp, action/title, institution, and result.
- Effect: reflects local state changes caused by identity verification, UVU request receipt seed data, UVU approval/share, UVU permission creation, denial, delegation, and revocation actions.
