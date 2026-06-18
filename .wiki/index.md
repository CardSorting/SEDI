# SEDI Knowledge Ledger

## Current System State

SEDI is a native Android Kotlin prototype for a State-Endorsed Digital Identity wallet demo. The implementation is intentionally local and demo-oriented: it uses Jetpack Compose, Material 3, Navigation Compose, a ViewModel state holder, pure domain models, and an in-memory demo repository. No backend, persistence layer, blockchain, or cryptographic infrastructure is implemented.

The app is now structured as a mobile product journey rather than a large scrolling home dashboard. The primary destinations are bottom tabs: `Wallet`, `Requests`, `Institutions`, and `Activity`. Onboarding, request review, share consent, verification success, permission details, revoke confirmation, revoke success, credential detail, and delegation are stack/detail screens outside the primary tab destinations.

## Verified Source Structure

- `settings.gradle.kts` defines the `SEDI` Gradle root project and includes the `:app` module.
- `build.gradle.kts` defines Android application, Kotlin Android, and Kotlin Compose plugin versions.
- `gradle.properties` enables AndroidX and sets JVM arguments.
- `.gitignore` excludes Gradle/build/IDE generated artifacts.
- `app/build.gradle.kts` configures the Android application module with package namespace `gov.utah.sedi`, SDK levels, Compose, Java 17 compatibility, Kotlin JVM toolchain 17, and dependencies for Activity Compose, Material 3, Compose UI, Lifecycle Compose/ViewModel Compose, and Navigation Compose.
- `app/src/main/AndroidManifest.xml` declares `gov.utah.sedi.MainActivity` as the launcher activity and labels the app `Utah Identity Wallet`.
- `app/src/main/res/values/styles.xml` provides the Android no-action-bar base theme used by the Compose app.
- `app/src/main/java/gov/utah/sedi/domain/Models.kt` contains pure Kotlin domain models and enums for credentials, requests, permissions, activity events, delegation draft state, and aggregate wallet state. It now includes `identityVerified`, request expiration text, `RequestReceived`, `PermissionCreated`, and audit-trail fields `institutionName` and `result` on `ActivityEvent`.
- `app/src/main/java/gov/utah/sedi/data/DemoIdentityRepository.kt` seeds local demo data for credentials, three incoming verification requests, three pre-existing connected institutions, and the initial University residency request receipt.
- `app/src/main/java/gov/utah/sedi/presentation/IdentityWalletViewModel.kt` owns the live `StateFlow<IdentityWalletState>` and local state transition functions for identity verification, onboarding completion, request denial, request approval/share, delegation grant, and access revocation.
- `app/src/main/java/gov/utah/sedi/MainActivity.kt` contains the Compose UI, app theme, navigation graph, onboarding stack, bottom navigation, tab summary screens, detail screens, confirmation/success screens, optional delegation screen, and shared UI components.

## Verified UX Navigation State

- Bottom navigation uses exactly four main destinations: `Wallet`, `Requests`, `Institutions`, and `Activity`.
- `OnboardingWelcomeScreen` introduces the app with `Utah Identity Wallet`, `State-backed identity, controlled by you.`, and a `Create Identity Wallet` action.
- `OnboardingVerifyScreen` simulates state identity verification with `State ID check`, `Residency check`, `Identity match check`, and a `Verify Identity` action.
- `OnboardingCompleteScreen` confirms `State Identity Verified` and routes to the Wallet tab through `Go to Wallet`.
- `WalletScreen` is a focused overview containing a verified State Identity card, credential cards for Utah Residency, Age Verification, and Professional License, and one recent request preview. It does not render the full institutions list, full activity feed, full permissions list, or delegation controls.
- `RequestsListScreen` renders incoming request cards for University of Utah residency, Department of Health identity confirmation, and Bank age verification.
- `RequestDetailScreen` explains one request with requester, purpose, requested proof, expiration, what the institution receives, what stays private, and actions `Review Share` and `Deny`.
- `ShareReviewScreen` is the consent screen for `Share Utah Residency Verification`, listing shared Utah residency verification and hidden full address, birthdate, and ID number.
- `VerificationSuccessScreen` confirms `Verification Complete`, states that University of Utah received residency verification, states no full address was shared, and routes to institution access or Wallet.
- `InstitutionsListScreen` shows connected institutions only with cards for access status, shared proof, expiration, last used, and `Open Institution` action.
- `PermissionDetailScreen` shows what one institution can and cannot access, status, expiration/last-used metadata, and actions `Revoke Access`, `Extend Access`, and `View Activity`.
- `RevokeConfirmationScreen` asks `Revoke University of Utah access?` and explains future verification loss, history retention, and future request approval.
- `RevokeSuccessScreen` confirms `Access Revoked`, notes the activity log update, and routes to Activity or Institutions.
- `ActivityScreen` renders a transparent audit trail with timestamp, action/title, institution, and result fields.
- `DelegationScreen` remains separate from Wallet as an optional flow with recipient, scope, duration, review, and creation action.

## Verified State Changes

- `verifyIdentity()` sets `identityVerified = true` and prepends a `State Identity Verified` activity event.
- `completeOnboarding()` sets `onboardingComplete = true` without creating a duplicate verification event.
- `approveRequestAndShare("uofu-residency")` marks the University request approved, creates/replaces the `University of Utah` active permission, and prepends `University access created` and `Residency shared with University of Utah` activity events.
- `revokeAccess("university-of-utah")` marks the University permission revoked, changes `lastUsed` to `Revoked today`, and prepends a `University access revoked` activity event.

## Verified Build

The debug build was validated with:

```bash
gradle :app:assembleDebug --no-daemon --console=plain
```

The latest run completed successfully after the native journey restructure. Build output reported Gradle deprecated-feature warnings and an unstripped `libandroidx.graphics.path.so` packaging notice; neither blocked debug APK assembly.
