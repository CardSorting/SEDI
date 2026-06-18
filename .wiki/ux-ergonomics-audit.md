# SEDI UX Ergonomics Audit

## Audit Date

2026-06-18

## Files Verified

- `app/src/main/java/gov/utah/sedi/MainActivity.kt`
- `.wiki/index.md`
- `.wiki/changelog.md`
- `.wiki/walkthrough.md`

## Ergonomic Goal

Improve the native Android stakeholder demo so non-technical users can understand the wallet with familiar consumer patterns: a Home landing page, requests awaiting review, access management, temporary sharing, and an activity timeline.

## Implemented UI Patterns

### Familiar Navigation

- Bottom tab labels are `Home`, `Requests`, `Access`, and `Activity`.
- `Institutions` remains the internal route name, but the user-facing label is `Access`.

### Guided Home Dashboard

- `NextBestActionCard` surfaces the next recommended action.
- `WalletAtAGlance` summarizes verified credentials, pending requests, and active access counts.
- `QuickActionsPanel` offers direct routes to review requests, manage access, and share temporarily.

### Plain-Language Consent

- Consent review is framed as a `Secure sharing request`.
- The approval screen identifies who is asking, what proof is requested, what they receive, what is kept private, and how the user keeps control.
- Primary approval copy is `Allow secure sharing`; the cancel copy is `Not now`.

### Access Management

- Connected institutions are presented as access holders.
- Detail copy uses `What they can use`, `They can see`, `Kept private`, `Ends`, and `Recent activity`.
- Revocation remains available through `Revoke access`.

### Temporary Sharing

- Delegation is framed as temporary sharing.
- Input labels ask `Who needs access?`, `What can they confirm?`, and `When should access end?`.

## Layer Boundaries Preserved

- Domain layer: unchanged.
- Data/demo repository: unchanged.
- Presentation ViewModel state transitions: unchanged.
- UI layer: changed in `MainActivity.kt` only.
- Build/infrastructure configuration: unchanged.

## Verification

`gradle :app:assembleDebug --no-daemon --console=plain` completed successfully after the UX changes.

Build notes observed:

- Gradle reported deprecated features that will be incompatible with Gradle 10.
- Android packaging reported that `libandroidx.graphics.path.so` could not be stripped and was packaged as-is.
- Neither note blocked `:app:assembleDebug`.