# SEDI UX Ergonomics Audit

## Audit Date

2026-06-18

## Files Verified

- `app/src/main/java/gov/utah/sedi/MainActivity.kt`
- `app/src/main/java/gov/utah/sedi/domain/Models.kt`
- `app/src/main/java/gov/utah/sedi/data/DemoIdentityRepository.kt`
- `app/src/main/java/gov/utah/sedi/presentation/IdentityWalletViewModel.kt`
- `.wiki/index.md`
- `.wiki/changelog.md`
- `.wiki/state-transitions.md`
- `.wiki/walkthrough.md`

## Ergonomic Goal

Revise the native Android stakeholder demo so it feels like a real mobile product journey, not a scrolling dashboard. Each screen should perform one user task, with tabs reserved for primary destinations and stack screens handling detail workflows.

## Implemented UI Structure

### Primary Navigation

- Bottom tabs are `Wallet`, `Requests`, `Institutions`, and `Activity`.
- These tabs are summary destinations only.
- Detail work happens in stack routes outside the tab bar.

### Stack Screens

- Onboarding: `OnboardingWelcomeScreen`, `OnboardingVerifyScreen`, `OnboardingCompleteScreen`.
- Credential detail: `CredentialDetailScreen`.
- Request flow: `RequestDetailScreen`, `ShareReviewScreen`, `VerificationSuccessScreen`.
- Institution/revocation flow: `PermissionDetailScreen`, `RevokeConfirmationScreen`, `RevokeSuccessScreen`.
- Optional delegation: `DelegationScreen`.

### Wallet Scope Control

- Wallet shows a verified State Identity card.
- Wallet shows credential cards for Utah Residency, Age Verification, and Professional License.
- Wallet shows one recent request preview.
- Wallet does not show the full permissions list, full activity feed, all institutions, or delegation controls.

### Request and Consent Flow

- Requests list shows incoming request cards with requester, proof, purpose, and status.
- Request detail explains one request with purpose, requested proof, expiration, shared data, and hidden data.
- Share review uses consent-screen framing and actions `Approve Share` and `Cancel`.
- Approval creates University permission and activity events before showing success.

### Institution and Revocation Flow

- Institutions list shows connected institutions only.
- Permission detail shows can-access, cannot-access, expiration, last-used, and active/revoked status.
- Revoke confirmation is a separate confirmation step.
- Revoke success is a separate result step that routes to Activity or Institutions.

### Activity Transparency

- Activity shows audit entries with timestamp, action/title, institution, and result.
- The activity trail is fed by the ViewModel state transitions and initial request seed data.

## Layer Boundaries Preserved

- Domain layer: pure Kotlin data/enums only.
- Data/demo repository: in-memory seed data only.
- Presentation ViewModel: owns local state transitions.
- UI layer: Compose screens render state and dispatch user intentions.
- Build/infrastructure configuration: unchanged.

## Verification

`gradle :app:assembleDebug --no-daemon --console=plain` completed successfully after the native journey restructure.

Build notes observed:

- Gradle reported deprecated features that will be incompatible with Gradle 10.
- Android packaging reported that `libandroidx.graphics.path.so` could not be stripped and was packaged as-is.
- Neither note blocked `:app:assembleDebug`.
