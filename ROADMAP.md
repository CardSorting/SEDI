# ROADMAP.md

## 1. Project Center of Gravity

**Core Purpose:**  
SEDI is a native Android stakeholder demo for a State-Endorsed Digital Identity wallet. Its center of gravity is a believable mobile walkthrough of secure sharing approvals for verified state identity credentials.

**Primary Users / Operators:**  
Stakeholders reviewing the identity-wallet interaction model, plus developers/operators maintaining the Kotlin/Jetpack Compose prototype.

**Canonical Architecture:**  
Kotlin Android app with Jetpack Compose UI, Material 3, Navigation Compose, a ViewModel state holder, pure domain models, and an in-memory demo data repository.

**Canonical Workflows:**  
Build and validate with `gradle :app:assembleDebug --no-daemon --console=plain`. Demo workflow: onboarding → Home → residency verification request → secure sharing approval → Access details → revoke access → Activity timeline.

**Primary Runtime / Operational Center:**  
Workspace project root — ROADMAP.md lives beside source, not in plugin install trees.

**What This Project Must Not Become:**  
A speculative infrastructure dashboard, web prototype, crypto wallet visual clone, or production backend project. The prototype must stay focused on calm, mobile-native identity permissions and revocation.

## 2. Roadmap Health

**Status:** Coherent

**Summary:**  
SEDI now has a native Kotlin/Compose Android prototype structure with real local wallet state transitions, a more familiar Home/Requests/Access/Activity navigation model, and a validated debug build.

**Why This Status:**  
- ROADMAP.md created from gathered evidence
- Schema established for long-horizon steering
- Android project files, Compose screens, ViewModel transitions, and demo data repository have been added
- User-facing navigation and consent copy were updated toward non-technical, familiar connected-app/access-management patterns
- `gradle :app:assembleDebug --no-daemon --console=plain` completed successfully

**Primary Risk:**  
The prototype is intentionally in-memory and demo-oriented; future expansion should not imply production infrastructure without a separate decision.

**Primary Opportunity:**  
Use the native app to record a 60–90 second stakeholder walkthrough that makes state identity permissions feel familiar, revocable, and user-controlled through Home, Requests, Access, and Activity.

## 3. Strategic Narrative

SEDI demonstrates “secure sharing permissions for verified state identity” as a native Android identity wallet. The product story is intentionally concrete: a user verifies state identity, lands on a familiar Home dashboard, reviews an institution’s requested proof, allows a narrowly scoped credential share, manages who has access, revokes access, and then reviews the activity trail. The app should remain mobile-native, institutional, calm, and wallet-like, with real local state transitions rather than static screenshots or speculative infrastructure UI.

## 4. Now

### 1. Complete ROADMAP bootstrap fill

**Goal:**  
Keep ROADMAP.md aligned with the native Android identity-wallet prototype as implementation details evolve.

**Evidence:**  
ROADMAP.md, Gradle Android project files, app/src/main Kotlin source

**Center-of-Gravity Impact:**  
Strengthens

### 2. Address centralization recommendation

**Goal:**  
Preserve the current canonical structure: Gradle project root, `app` Android module, `domain` models, `data` demo repository, `presentation` ViewModel, and Compose UI entry point.

**Evidence:**  
settings.gradle.kts, app/build.gradle.kts, app/src/main/java/gov/utah/sedi

**Center-of-Gravity Impact:**  
Strengthens

## 5. Next

## 6. Later

## 7. Discovery

## 8. Maintenance Gravity

### Hotspots

| Area | Symptom | Risk | Recommended Action |
|---|---|---|---|
| MainActivity.kt | Large single-file Compose UI created for rapid prototype speed | Medium | Split into screen/component files if the prototype continues beyond walkthrough recording |
| MainActivity.kt | More Home dashboard and UX helper components now live in the same file | Medium | If additional UX passes continue, extract screen/component files while preserving ViewModel-owned state transitions |
| DemoIdentityRepository.kt | In-memory demo data only | Low | Keep explicit that no backend or persistence is provided |

### Repeated Friction

### Documentation Gaps

- No README yet documents Android Studio import, debug build, or walkthrough script.

### Agent Confusion Points

- This is now a native Android prototype, not the abandoned responsive web prototype.
- State transitions are local ViewModel transitions, not backend or production identity infrastructure.

## 9. Centralization & Code Soup Audit

**Overall Code Soup Risk:** Low

### Canonical Path Integrity

**Assessment:**  
Code soup risk: Low. The new Android project has a single Gradle app module and clear Kotlin package structure, with one known prototype-speed concentration in MainActivity.kt.

### Authority Boundaries

**Assessment:**  
Runtime authority is Android app-local: Gradle manifests configure the app, demo repository supplies data, and the ViewModel owns local state mutation. ROADMAP.md stays in the project workspace only.

### Structural Drift

**Assessment:**  
Recent changes from git evidence:
- Added native Android Gradle project and app module
- Added Kotlin domain models, demo repository, ViewModel, and Compose UI
- Added gradle.properties for AndroidX and JVM settings

### Agent Coherence

**Assessment:**  
Current source structure and validation command are explicit. Agents should avoid reintroducing web-prototype or infrastructure-dashboard scope.

### Centralization Recommendation

Keep feature mutations centralized through `IdentityWalletViewModel` for this prototype. If the UI grows, split Compose screens while keeping state transitions in presentation/domain layers.

## 10. Decision Log

### 2026-06-18 — Familiar navigation and ergonomics pass

**Decision:**  
Shift user-facing navigation and consent language toward familiar consumer patterns: Home, Requests, Access, Activity, secure sharing request, what they receive, kept private, manage access, and revoke access.

**Reason:**  
Non-technical stakeholders need to understand the identity-wallet model through familiar connected-app and mobile-wallet patterns rather than technical permission terminology.

**Impact:**  
Strengthens the center of gravity by making the native Android walkthrough more approachable without changing domain models, demo repository data, or ViewModel state transitions.

**Follow-up:**  
If the UI grows further, split `MainActivity.kt` into screen/component files after preserving the current navigation and state-transition boundaries.

### 2026-06-18 — Native Android prototype direction

**Decision:**  
Build SEDI as a native Kotlin Android app using Jetpack Compose, Material 3, Navigation Compose, ViewModel state, and local demo data.

**Reason:**  
The stakeholder demo requires a believable mobile identity wallet walkthrough rather than a responsive web prototype.

**Impact:**  
Project center of gravity moves to Android-native wallet interactions, real local state transitions, verified credentials, consent approvals, connected institutions, revocation, and activity audit trails.

**Follow-up:**  
Record the primary walkthrough and split large Compose UI files only if continued development needs maintainability beyond the prototype.

### 2026-06-18 — Initial roadmap bootstrap

**Decision:**  
Adopt ROADMAP.md at workspace root as the steering surface for SEDI.

**Reason:**  
Adopt ROADMAP.md as the long-horizon steering surface for SEDI.

**Impact:**  
Route SEDI strategic work through Now/Next/Later — max 5 Now items.

**Follow-up:**  
Run roadmap checkpoints after meaningful direction changes.

## 11. Recent Checkpoint

**Date:** 2026-06-18

**Checkpoint Summary:**  
Completed a user ergonomics pass for SEDI’s Android identity wallet, preserving the native prototype center of gravity while making navigation and consent review more familiar for non-technical users.

**Moved:**  
- None

**Added:**  
- Full 12-section schema

**Updated:**  
- `MainActivity.kt` user-facing navigation and copy now emphasize Home, Requests, Access, Activity, secure sharing, access management, and temporary sharing.
- `.wiki/` ledger now documents the UX ergonomics audit, updated walkthrough, changelog, and verified build.

**Archived:**  
- None

**Code Soup Risk:** Low  
Code soup risk remains Low overall, with one medium hotspot: `MainActivity.kt` now contains more Compose dashboard and helper components for prototype speed.

**Recommended Next Move:**  
Record or manually review the stakeholder walkthrough on device/emulator; if further UX expansion continues, split `MainActivity.kt` into focused screen/component files without moving state transitions out of `IdentityWalletViewModel`.

## 12. Archive
