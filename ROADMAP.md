# ROADMAP.md

## 1. Project Center of Gravity

**Core Purpose:**  
SEDI is a native Android stakeholder demo for a State-Endorsed Digital Identity wallet. Its center of gravity is a believable mobile walkthrough of secure sharing approvals for verified state identity credentials.

**Primary Users / Operators:**  
Stakeholders reviewing the identity-wallet interaction model, plus developers/operators maintaining the Kotlin/Jetpack Compose prototype.

**Canonical Architecture:**  
Kotlin Android app with Jetpack Compose UI, Material 3, Navigation Compose, a ViewModel state holder, pure domain models, and an in-memory demo data repository.

**Canonical Workflows:**  
Build and validate with `gradle :app:assembleDebug --no-daemon --console=plain`. Demo workflow: onboarding welcome → state identity verification → Wallet → Request Detail → Share Review → Verification Success → Institutions → Permission Detail → Revoke Confirmation → Revoke Success → Activity timeline.

**Primary Runtime / Operational Center:**  
Workspace project root — ROADMAP.md lives beside source, not in plugin install trees.

**What This Project Must Not Become:**  
A speculative infrastructure dashboard, web prototype, crypto wallet visual clone, or production backend project. The prototype must stay focused on calm, mobile-native identity permissions and revocation.

## 2. Roadmap Health

**Status:** Coherent

**Summary:**  
SEDI now has a native Kotlin/Compose Android prototype structure with real local wallet state transitions, required Wallet/Requests/Institutions/Activity bottom tabs, separated stack/detail screens, and a validated debug build.

**Why This Status:**  
- ROADMAP.md created from gathered evidence
- Schema established for long-horizon steering
- Android project files, Compose screens, ViewModel transitions, and demo data repository have been added
- User-facing navigation was restructured into required bottom tabs and focused stack/detail screens instead of a large scrolling dashboard
- `gradle :app:assembleDebug --no-daemon --console=plain` completed successfully

**Primary Risk:**  
The prototype is intentionally in-memory and demo-oriented; future expansion should not imply production infrastructure without a separate decision.

**Primary Opportunity:**  
Use the native app to record a stakeholder walkthrough where the user verifies identity, receives a request, reviews what will be shared, approves it, sees the institution connected, revokes access, and reviews the activity log.

## 3. Strategic Narrative

SEDI demonstrates “secure sharing permissions for verified state identity” as a native Android identity wallet. The product story is intentionally concrete: a user verifies state identity, lands on the Wallet tab, opens a request detail screen, reviews an OAuth-style share screen, approves a narrowly scoped credential share, sees the institution connected, confirms revocation, and reviews the activity trail. The app should remain mobile-native, institutional, calm, and wallet-like, with real local state transitions rather than static screenshots or speculative infrastructure UI.

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
| MainActivity.kt | The app now contains many focused Compose route screens in one file | Medium | If prototype work continues, split route screens/components into separate UI files while preserving ViewModel-owned state transitions |
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

### 2026-06-18 — Native mobile journey restructure

**Decision:**  
Replace the large dashboard-style home flow with a mobile-native journey using required bottom tabs (`Wallet`, `Requests`, `Institutions`, `Activity`) and stack/detail screens for onboarding, request detail, share review, verification success, permission detail, revoke confirmation, revoke success, credential detail, and optional delegation.

**Reason:**  
The demo needs to feel like a real mobile product flow: one screen per user task, summary tabs for main destinations, and detail/confirmation/success screens for workflow steps.

**Impact:**  
Strengthens the center of gravity by making the walkthrough match the intended stakeholder story: verify identity, receive a request, review sharing, approve, inspect institution access, revoke, and audit activity.

**Follow-up:**  
If development continues beyond the recording/demo pass, split `MainActivity.kt` into focused screen/component files while keeping state mutation centralized in `IdentityWalletViewModel`.

### 2026-06-18 — Familiar navigation and ergonomics pass

**Decision:**  
Shift user-facing navigation and consent language toward familiar consumer patterns: Home, Requests, Access, Activity, secure sharing request, what they receive, kept private, manage access, and revoke access.

**Reason:**  
Non-technical stakeholders need to understand the identity-wallet model through familiar connected-app and mobile-wallet patterns rather than technical permission terminology.

**Impact:**  
Strengthens the center of gravity by making the native Android walkthrough more approachable without changing domain models, demo repository data, or ViewModel state transitions.

**Follow-up:**  
If the UI grows further, split `MainActivity.kt` into screen/component files after preserving the current navigation and state-transition boundaries.

### 2026-06-18 — Second non-technical UX audit pass

**Decision:**  
Add clearer orientation and reassurance patterns to the existing native Android wallet flow: pending request badge, trust banners, request inbox counts, before-you-allow checklist, what-happens-next guidance, connected-organization overview, access-control explanation, temporary-sharing use-case copy, and Activity receipt-trail framing.

**Reason:**  
Non-technical stakeholders need the wallet to mirror familiar mobile permission, connected-app, and receipt-history patterns so the demo explains itself without technical narration.

**Impact:**  
Strengthens the center of gravity by making the existing local-state Android walkthrough clearer and more approachable while preserving domain models, demo repository data, ViewModel transitions, Gradle configuration, manifest, and resources.

**Follow-up:**  
MainActivity.kt remains the primary prototype-speed hotspot; if more UX passes are added, split Compose screens/components while keeping state mutation centralized in `IdentityWalletViewModel`.

### 2026-06-18 — Third navigation clarity and reassurance pass

**Decision:**
Add familiar destination mapping and decision reassurance to the existing Android wallet UI: Home `Where to go` orientation, Requests inbox guidance, request privacy snapshot, connected-app Access banner, access status steps, and Activity receipt guidance.

**Reason:**
Non-technical users benefit from repeated, familiar mental models at the exact decision points: inbox before sharing, connected apps for access, and receipts for accountability.

**Impact:**
Strengthens the center of gravity by making the demo more self-explanatory while preserving domain models, demo data, ViewModel transitions, Gradle configuration, manifest, and resources.

**Follow-up:**
MainActivity.kt is now a clearer but larger prototype UI surface; the next maintainability improvement should split Compose screens/components if additional UX expansion continues.

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
Completed the native mobile journey restructure for SEDI’s Android identity wallet: bottom tabs are now Wallet, Requests, Institutions, and Activity, and the demo path is implemented through focused onboarding, request detail, share review, verification success, permission detail, revoke confirmation, revoke success, and activity screens.

**Moved:**  
- Wallet responsibilities moved away from dashboard stuffing and into focused tab summaries plus stack/detail workflow screens.

**Added:**  
- `identityVerified` state and explicit `verifyIdentity()` transition.
- Request expiration and activity audit fields for institution/result display.
- RequestReceived and PermissionCreated activity kinds.
- Separate Compose screens/routes for onboarding welcome, onboarding verify, onboarding complete, request detail, share review, verification success, permission detail, revoke confirmation, revoke success, credential detail, and delegation.

**Updated:**  
- `MainActivity.kt` now implements the required bottom tabs and one-task-per-screen journey.
- `DemoIdentityRepository.kt` now seeds the required requests and institution/access copy for the walkthrough.
- `IdentityWalletViewModel.kt` now creates University permission/activity entries on approval and revocation activity on revoke.
- `.wiki/` ledger now documents the restructured navigation, state transitions, walkthrough, UX audit, changelog, and validated build.

**Archived:**  
- None

**Code Soup Risk:** Low  
Code soup risk remains Low overall because canonical source locations are unchanged, but `MainActivity.kt` is now a medium maintainability hotspot due to multiple focused route screens living in one prototype file.

**Recommended Next Move:**  
Record or manually review the primary walkthrough on device/emulator; if additional UI expansion continues, split `MainActivity.kt` into focused screen/component files without moving state transitions out of `IdentityWalletViewModel`.

## 12. Archive
