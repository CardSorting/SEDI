# SEDI Knowledge Ledger

## Current System State

SEDI is now a native Android Kotlin prototype for a State-Endorsed Digital Identity wallet demo. The implementation is intentionally local and demo-oriented: it uses Jetpack Compose, Material 3, Navigation Compose, a ViewModel state holder, and an in-memory demo repository. No backend, persistence layer, blockchain, or cryptographic infrastructure is implemented.

## Verified Source Structure

- `settings.gradle.kts` defines the `SEDI` Gradle root project and includes the `:app` module.
- `build.gradle.kts` defines Android application, Kotlin Android, and Kotlin Compose plugin versions.
- `gradle.properties` enables AndroidX and sets JVM arguments.
- `.gitignore` excludes Gradle/build/IDE generated artifacts.
- `app/build.gradle.kts` configures the Android application module with package namespace `gov.utah.sedi`, SDK levels, Compose, Java 17 compatibility, Kotlin JVM toolchain 17, and dependencies for Activity Compose, Material 3, Compose UI, Lifecycle Compose/ViewModel Compose, and Navigation Compose.
- `app/src/main/AndroidManifest.xml` declares `gov.utah.sedi.MainActivity` as the launcher activity and labels the app `Utah Identity Wallet`.
- `app/src/main/res/values/styles.xml` provides the Android no-action-bar base theme used by the Compose app.
- `app/src/main/java/gov/utah/sedi/domain/Models.kt` contains pure Kotlin domain models and enums for credentials, requests, permissions, activity events, delegation draft state, and aggregate wallet state.
- `app/src/main/java/gov/utah/sedi/data/DemoIdentityRepository.kt` seeds local demo data for credentials, incoming verification requests, connected institutions, and initial activity.
- `app/src/main/java/gov/utah/sedi/presentation/IdentityWalletViewModel.kt` owns the live `StateFlow<IdentityWalletState>` and local state transition functions.
- `app/src/main/java/gov/utah/sedi/MainActivity.kt` contains the Compose UI, app theme, navigation graph, onboarding flow, bottom navigation, Home, Requests, Access, Activity, request review, sharing completion, access detail, temporary sharing, and shared component screens.

## Verified UX Navigation State

- Bottom navigation now uses the non-technical labels `Home`, `Requests`, `Access`, and `Activity`.
- The Home screen now includes a recommended next-step card, an at-a-glance metrics panel, and common action shortcuts before the detailed credential/request/access sections.
- Request review copy now uses familiar permission language: `Review request`, `Secure sharing request`, `Allow secure sharing`, `Don't allow`, and `Not now`.
- Access management copy now uses familiar connected-app language: `Access`, `Access details`, `Who has access`, `Manage access`, and `Revoke access`.
- Temporary delegation is presented as temporary sharing through `Share temporarily`, `Set up temporary sharing`, and `Allow temporary sharing`.
- No domain model, demo repository, ViewModel state transition, Gradle configuration, manifest, or Android resource file was changed in this ergonomics pass.

## Verified Build

The debug build was validated with:

```bash
gradle :app:assembleDebug --no-daemon --console=plain
```

The command completed successfully after adding AndroidX Gradle properties and aligning Java/Kotlin JVM targets to 17.

The same command was re-run successfully after the Home/Requests/Access/Activity ergonomics pass. Gradle reported deprecated feature warnings and an unstripped `libandroidx.graphics.path.so` packaging notice, but the debug APK assembly completed successfully.