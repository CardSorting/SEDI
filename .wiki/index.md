# SEDI Knowledge Ledger

## Current System State

SEDI is a native Android Kotlin prototype for a State-Endorsed Digital Identity wallet demo. The implementation is intentionally local and demo-oriented: it uses Jetpack Compose, Material 3, Navigation Compose, a ViewModel state holder, pure domain models, and an in-memory demo repository. No backend, persistence layer, blockchain, or cryptographic infrastructure is implemented.

The primary walkthrough now uses Utah Valley University (UVU). A citizen creates a state-backed identity wallet, verifies state identity, reviews a UVU proof-of-Utah-residency request for enrollment eligibility, previews exactly what will and will not be shared, approves the share, sees UVU added as a connected institution, revokes UVU access, and reviews Activity History.

The app is structured as a mobile product journey rather than a large scrolling home dashboard. The primary destinations are bottom tabs: `Wallet`, `Requests`, `Institutions`, and `Activity`. Onboarding, request detail, shared data preview, approval consent, verification processing, verification success, permission detail, revoke confirmation, revoke success, credential detail, and delegation are stack/detail screens outside the primary tab destinations.

## Verified Source Structure

- `settings.gradle.kts` defines the `SEDI` Gradle root project and includes the `:app` module.
- `build.gradle.kts` defines Android application, Kotlin Android, and Kotlin Compose plugin versions.
- `gradle.properties` enables AndroidX and sets JVM arguments.
- `.gitignore` excludes Gradle/build/IDE generated artifacts.
- `app/build.gradle.kts` configures the Android application module with package namespace `gov.utah.sedi`, SDK levels, Compose, Java 17 compatibility, Kotlin JVM toolchain 17, and dependencies for Activity Compose, Material 3, Compose UI, Lifecycle Compose/ViewModel Compose, and Navigation Compose.
- `app/src/main/AndroidManifest.xml` declares `gov.utah.sedi.MainActivity` as the launcher activity and labels the app `Utah Identity Wallet`.
- `app/src/main/res/values/styles.xml` provides the Android no-action-bar base theme used by the Compose app.
- `app/src/main/java/gov/utah/sedi/domain/Models.kt` contains pure Kotlin domain models and enums for credentials, requests, permissions, activity events, delegation draft state, and aggregate wallet state.
- `app/src/main/java/gov/utah/sedi/data/DemoIdentityRepository.kt` seeds local demo data for verified State Identity, Utah Residency, Age Verification, Professional License, one pending Utah Valley University residency request, non-active Department of Health/Licensing Board/Bank institution entries, and seed Activity History entries.
- `app/src/main/java/gov/utah/sedi/presentation/IdentityWalletViewModel.kt` owns the live `StateFlow<IdentityWalletState>` and local state transition functions for identity verification, onboarding completion, request denial, UVU request approval/share, delegation grant, and access revocation.
- `app/src/main/java/gov/utah/sedi/MainActivity.kt` contains the Compose UI, app theme, navigation graph, onboarding stack, bottom navigation, tab summary screens, detail screens, confirmation/success screens, verification processing screen, optional delegation screen, and shared UI components.

## Verified UX Navigation State

- Bottom navigation uses exactly four main destinations: `Wallet`, `Requests`, `Institutions`, and `Activity`.
- `OnboardingWelcomeScreen` introduces the app and uses the `Create Identity Wallet` action.
- `OnboardingIdentitySetupScreen` begins wallet setup and uses the `Verify with State Identity` action.
- `StateVerificationChecksScreen` shows `State ID matched`, `Utah residency confirmed`, and `Identity status verified`; tapping `Continue` calls `verifyIdentity()`.
- `OnboardingCompleteScreen` confirms setup and routes to Wallet through `Go to Wallet`.
- `WalletScreen` shows Verified State Identity, Utah Residency, Age Verification, Professional License, and a recent Utah Valley University request preview.
- `RequestsListScreen` shows the pending UVU proof-of-Utah-residency request with institution, request, purpose, pending status, expiration, and `Review Request` action.
- `RequestDetailScreen` explains the UVU request and routes to `SharedDataPreviewScreen` through `Review What Will Be Shared`.
- `SharedDataPreviewScreen` lists what UVU will receive and what UVU will not receive, then routes to approval through `Continue to Approval`.
- `ApprovalConsentScreen` provides the OAuth-style approval step with `Approve Share` and `Deny` actions.
- `VerificationProcessingScreen` auto-advances after showing preparation, credential confirmation, and verified residency sending steps.
- `VerificationSuccessScreen` confirms UVU received residency verification, full address was not shared, and UVU was added to Connected Institutions.
- `InstitutionsListScreen` shows Utah Valley University as `Active` after approval plus Department of Health, Licensing Board, and Bank as `No active access`.
- `PermissionDetailScreen` shows exactly what UVU can and cannot access, last-used/expiration/status details, and `Revoke Access`.
- `RevokeConfirmationScreen` confirms revocation with consequences and a `Confirm Revoke` action.
- `RevokeSuccessScreen` confirms access removal and routes to `Activity` through `View Activity`.
- `ActivityScreen` renders Activity History from live state.
- `DelegationScreen` remains separate from the main UVU walkthrough.

## Verified State Changes

- `verifyIdentity()` sets `identityVerified = true` and prepends `State Identity Verified` to Activity History.
- `completeOnboarding()` sets `onboardingComplete = true` without creating duplicate verification activity.
- `approveRequestAndShare("uvu-residency")` marks the UVU request `Approved`, creates/replaces an active `uvu` connected institution permission, and prepends `UVU connected permission created` and `Residency verification shared with Utah Valley University` activity events.
- `revokeAccess("uvu")` marks the UVU permission `Revoked`, changes `lastUsed` to `Revoked today`, and prepends `UVU access revoked` to Activity History.
- `ActivityScreen` reflects the current `state.activity` list, including seed entries and the live approval/revocation events.

## Run the Demo

**Automatic launcher** (emulator + install + app + on-screen mirror):

```bash
bash scripts/start-emulator-ui.sh
```

macOS: double-click `scripts/start-emulator-ui.command` in Finder.

Full setup, `--fresh` reset, and troubleshooting: `.wiki/android-emulator-troubleshooting.md`.

## Developer Troubleshooting

- `.wiki/android-emulator-troubleshooting.md` — one-command demo launcher, first-time setup, emulator crashes, stale data, and `Error type 3` (activity not found).

## Verified Build

The debug build was validated with:

```bash
gradle :app:assembleDebug --no-daemon --console=plain
```

The latest run completed successfully after the UVU walkthrough revision. Build output reported Gradle deprecated-feature warnings and an unstripped `libandroidx.graphics.path.so` packaging notice; neither blocked debug APK assembly.
