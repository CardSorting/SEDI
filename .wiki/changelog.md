# SEDI Changelog

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