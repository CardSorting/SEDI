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

Revise the native Android stakeholder demo so Utah Valley University is the primary institution and the product story feels like a sequence of small, focused mobile screens rather than a scroll-heavy dashboard. Each screen should perform one decision or show one result, with tabs reserved for primary destinations and stack screens handling workflow steps.

## Implemented UI Structure

### Primary Navigation

- Bottom tabs are exactly `Wallet`, `Requests`, `Institutions`, and `Activity`.
- These tabs are summary destinations only.
- Detail work happens in stack routes outside the tab bar.

### Stack Screens

- Onboarding: `OnboardingWelcomeScreen`, `OnboardingIdentitySetupScreen`, `StateVerificationChecksScreen`, `OnboardingCompleteScreen`.
- Credential detail: `CredentialDetailScreen`.
- UVU request flow: `RequestDetailScreen`, `SharedDataPreviewScreen`, `ApprovalConsentScreen`, `VerificationProcessingScreen`, `VerificationSuccessScreen`.
- Institution/revocation flow: `PermissionDetailScreen`, `RevokeConfirmationScreen`, `RevokeSuccessScreen`.
- Optional delegation: `DelegationScreen`, kept separate from the main UVU walkthrough.

### Wallet Scope Control

- Wallet shows a verified State Identity card.
- Wallet shows credential cards for Utah Residency, Age Verification, and Professional License.
- Wallet shows one recent Utah Valley University request preview.
- Wallet does not show the full permissions list, full activity feed, all institutions, or delegation controls.

### Request and Consent Flow

- Requests list shows the UVU incoming request with institution, proof, purpose, status, expiration, and `Review Request` action.
- Request detail explains one UVU request and routes to the shared-data preview through `Review What Will Be Shared`.
- Shared data preview separates `UVU will receive` from `UVU will NOT receive`.
- Approval consent uses product language: `Share Verification`, `Approve Share`, and `Deny`.
- Approval creates real ViewModel state changes before the app shows processing and success screens.

### Institution and Revocation Flow

- Institutions list shows Utah Valley University as Active after approval and Department of Health, Licensing Board, and Bank as No active access.
- UVU permission detail shows can-access, cannot-access, purpose, last-used, expiration, and status metadata.
- Revoke confirmation is a separate confirmation step.
- Revoke success is a separate result step that routes to Activity or Institutions.

### Activity Transparency

- Activity shows Activity History entries with timestamp, action/title, institution, and result.
- The activity trail is fed by the ViewModel state transitions and initial seed data.
- Approval and revocation entries are not static text; they are created by state mutation functions.

## Layer Boundaries Preserved

- Domain layer: pure Kotlin data/enums only.
- Data/demo repository: in-memory seed data only.
- Presentation ViewModel: owns local state transitions.
- UI layer: Compose screens render state and dispatch user intentions.
- Build/infrastructure configuration: unchanged.

## Verification

`gradle :app:assembleDebug --no-daemon --console=plain` completed successfully after the UVU walkthrough revision.

Build notes observed:

- Gradle reported deprecated features that will be incompatible with Gradle 10.
- Android packaging reported that `libandroidx.graphics.path.so` could not be stripped and was packaged as-is.
- Neither note blocked `:app:assembleDebug`.
