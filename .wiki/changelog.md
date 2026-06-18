# SEDI Changelog

## 2026-06-18 — Native mobile journey restructure

### Changed

- Reworked `MainActivity.kt` from a large Home/Access dashboard pattern into a stack-and-tabs mobile journey.
- Bottom tabs now use the required primary destinations: `Wallet`, `Requests`, `Institutions`, and `Activity`.
- Added explicit navigation routes for `OnboardingWelcome`, `OnboardingVerify`, `OnboardingComplete`, `CredentialDetail`, `RequestDetail`, `ShareReview`, `VerificationSuccess`, `PermissionDetail`, `RevokeConfirmation`, `RevokeSuccess`, and `Delegation`.
- Replaced the old multi-step onboarding state inside one composable with separate onboarding screens and a distinct `verifyIdentity()` ViewModel transition.
- Simplified Wallet into a focused overview: verified identity card, credential cards, and a small recent request preview only.
- Added request detail, OAuth-style share review, verification success, permission detail, revoke confirmation, and revoke success screens as focused task screens.
- Kept delegation separate from Wallet as an optional detail flow.
- Updated `DemoIdentityRepository.kt` seed data to include the three requested incoming request examples and institution cards aligned to University of Utah, Department of Health, Licensing Board, and Bank journey content after approval.
- Updated `Models.kt` with `identityVerified`, request `expires`, `RequestReceived`, `PermissionCreated`, and activity audit fields `institutionName` and `result`.
- Updated `IdentityWalletViewModel.kt` so approval creates University permission and share/activity events, while revoke updates permission status and adds a revocation activity event.

### Validated

- `gradle :app:assembleDebug --no-daemon --console=plain` completed successfully after the restructure.
- Latest build output included Gradle deprecated-feature warnings and an unstripped `libandroidx.graphics.path.so` packaging notice; debug APK assembly was not blocked.

### Architectural Notes

- Domain models remain pure Kotlin data/enums.
- Demo data remains in the in-memory repository.
- Local state mutation remains centralized in `IdentityWalletViewModel`.
- Compose UI renders focused screens and dispatches intentions to the ViewModel.


## 2026-06-18 — Third navigation clarity and reassurance pass

### Changed

- Added `WalletNavigationMap` to the Home screen in `MainActivity.kt` so non-technical users can map `Requests`, `Access`, and `Activity` to familiar inbox, connected-app, and receipt-history patterns.
- Added `NavigationGuideRow` as a reusable Home orientation row with plain-language destinations, status chips, and direct open actions for Requests and Access.
- Added `ScreenGuidePanel` to provide short contextual guidance on Requests and Activity without adding new state or navigation routes.
- Added a Requests guide explaining that nothing is shared until the user opens a request and chooses Allow, and that denials are recorded in Activity.
- Added `PrivacySnapshotPanel` and `PrivacySnapshotRow` to the request-review flow so users see who is asking, what they get, what they do not get, and what choices remain available.
- Added an Access trust banner framing connected organizations as familiar connected-app-style permissions for verified ID.
- Added `AccessJourneyPanel` to access detail screens to show the allowed proof, visible receipts, and revocation/end state as a three-step status summary.
- Added an Activity guide explaining how the receipt trail answers who asked, what was shared, what was denied, and when access changed.

### Unchanged

- Domain models in `app/src/main/java/gov/utah/sedi/domain/Models.kt` were not changed.
- Demo seed data in `app/src/main/java/gov/utah/sedi/data/DemoIdentityRepository.kt` was not changed.
- State transitions in `app/src/main/java/gov/utah/sedi/presentation/IdentityWalletViewModel.kt` were not changed.
- Gradle files, Android manifest, and Android resources were not changed.

### Validated

- `gradle :app:assembleDebug --no-daemon --console=plain` completed successfully after the third navigation clarity and reassurance pass.
- Latest build output included Gradle deprecated-feature warnings; debug APK assembly was not blocked.

## 2026-06-18 — Second UX ergonomics audit pass

### Changed

- Added Material 3 `Badge` and `BadgedBox` usage in `MainActivity.kt` so the `Requests` bottom navigation item displays the pending request count when pending requests exist.
- Added `SafetyTint` and a reusable `TrustBanner` component for plain-language reassurance panels.
- Added a Home trust banner explaining that the user approves every share and that full ID/document details stay in the wallet.
- Added `HowItWorksPanel` and `NumberedStep` to explain the sharing model in three familiar steps: review the request, share a verified answer, and manage access anytime.
- Added `RequestSummaryPanel` on the Requests screen with counts for requests that need review, were allowed, or were denied.
- Added `ReviewChecklistPanel` to the request review screen with pre-approval checks for organization name, exact proof, and private data.
- Added `WhatHappensNextPanel` to the request review screen explaining that approval creates a verified answer, adds an Access connection, and records Activity.
- Added `AccessOverviewPanel` on the Access screen with active, ended, and total connection counts.
- Added an `Access controls` panel to the access detail screen explaining revocation, Activity review, and narrow proof sharing.
- Added a temporary-sharing trust banner explaining one-time scenarios for landlord, employer, school office, or service-counter verification.
- Added an Activity trust banner framing Activity as a plain-language receipt trail.

### Unchanged

- Domain models in `app/src/main/java/gov/utah/sedi/domain/Models.kt` were not changed.
- Demo seed data in `app/src/main/java/gov/utah/sedi/data/DemoIdentityRepository.kt` was not changed.
- State transitions in `app/src/main/java/gov/utah/sedi/presentation/IdentityWalletViewModel.kt` were not changed.
- Gradle files, Android manifest, and Android resources were not changed.

### Validated

- `gradle :app:assembleDebug --no-daemon --console=plain` completed successfully after the second UX ergonomics pass.
- Latest build output included Gradle deprecated-feature warnings; debug APK assembly was not blocked.

## 2026-06-18 — User ergonomics and familiar navigation pass

### Changed

- Renamed the primary bottom navigation from `Wallet`, `Requests`, `Institutions`, `Activity` to `Home`, `Requests`, `Access`, `Activity` in `MainActivity.kt`.
- Reworked the wallet landing screen into a Home dashboard with:
  - `NextBestActionCard` for a recommended request-review or access-management next step.
  - `WalletAtAGlance` and `GlanceMetric` for verified credential, pending request, and active access counts.
  - `QuickActionsPanel` and `QuickActionRow` for common actions: review requests, manage access, and share temporarily.
- Updated request and consent language from technical/OAuth framing toward familiar secure-sharing language, including `Review request`, `Secure sharing request`, `They will receive`, `Kept private`, `Your control`, and `Allow secure sharing`.
- Updated connected-institution language toward access-management language, including `Access`, `Access details`, `Who has access`, `They can see`, and `Manage access`.
- Updated delegation language toward temporary sharing language, including `Share temporarily`, `Set up temporary sharing`, and `Allow temporary sharing`.

### Unchanged

- Domain models in `app/src/main/java/gov/utah/sedi/domain/Models.kt` were not changed.
- Demo seed data in `app/src/main/java/gov/utah/sedi/data/DemoIdentityRepository.kt` was not changed.
- State transitions in `app/src/main/java/gov/utah/sedi/presentation/IdentityWalletViewModel.kt` were not changed.
- Gradle files, Android manifest, and Android resources were not changed.

### Validated

- `gradle :app:assembleDebug --no-daemon --console=plain` completed successfully after the ergonomics changes.
- Build output included a Gradle deprecated-feature warning and an unstripped `libandroidx.graphics.path.so` notice; neither blocked debug APK assembly.

## 2026-06-18 — Native Android identity wallet prototype

### Added

- Created a native Android Gradle project with a single `:app` module.
- Added Kotlin + Jetpack Compose + Material 3 + Navigation Compose dependencies.
- Added Android launcher manifest and app theme resources.
- Added pure domain models for verified credentials, verification requests, connected institutions, permission status, activity events, delegation draft state, and aggregate wallet state.
- Added an in-memory demo repository with State Identity, Age Verification, Utah Residency, Professional License, University of Utah request, Beehive Community Bank request, and connected institution examples.
- Added `IdentityWalletViewModel` with real local state transitions for onboarding completion, request denial, request approval/share, delegation grant, and access revocation.
- Added Compose screens for onboarding, wallet, requests, share credential consent, share completion, connected institutions, permission detail, delegation, and activity.
- Added bottom navigation for Wallet, Requests, Institutions, and Activity.
- Added `.gitignore` for generated Android/Gradle/IDE artifacts.

### Validated

- `gradle :app:assembleDebug --no-daemon --console=plain` completed successfully.
- A source scan of `app/src/main` found no occurrences of prohibited terms: `crypto`, `blockchain`, `trust graph`, `trust graphs`, or `abstract graph`.

### Steering

- Updated `ROADMAP.md` to reflect the native Android prototype center of gravity and validated it with `roadmap(action='validate')`.