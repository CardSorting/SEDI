# SEDI Stakeholder Walkthrough

## Primary UVU Demo Path Implemented

1. Launch the Android app.
2. On `OnboardingWelcome`, introduce the Utah identity wallet.
3. Tap `Create Identity Wallet`.
4. On `OnboardingIdentitySetup`, begin setup.
5. Tap `Verify with State Identity`.
6. On `StateVerificationChecks`, review `State ID matched`, `Utah residency confirmed`, and `Identity status verified`.
7. Tap `Continue`; this sets `identityVerified = true`.
8. On `OnboardingComplete`, confirm setup.
9. Tap `Go to Wallet`.
10. On `Wallet`, see Verified State Identity, Utah Residency, Age Verification, Professional License, and the recent Utah Valley University request preview.
11. Tap the UVU request preview or open the `Requests` bottom tab.
12. On `Requests`, review the pending UVU request: institution `Utah Valley University`, request `Proof of Utah residency`, purpose `Enrollment eligibility`, status `Pending`, and expiration `7 days`.
13. Tap `Review Request`.
14. On `Request Detail`, review that Utah Valley University requests proof of Utah residency for enrollment eligibility, with requested proof `Utah Residency Verified` and expiration `7 days`.
15. Tap `Review What Will Be Shared`.
16. On `Shared Data Preview`, confirm UVU will receive `Utah Residency: Verified`, `Issuer: State of Utah`, and `Verification status: Active`.
17. Confirm UVU will not receive full address, birthdate, State ID number, or unrelated credentials.
18. Tap `Continue to Approval`.
19. On `Share Verification`, review the approval summary: share once, enrollment eligibility purpose, no full address shared, and UVU added to connected institutions after approval.
20. Tap `Approve Share`; this marks the UVU request approved, creates the UVU permission, and adds Activity History entries.
21. On `Verification Processing`, watch proof preparation, state credential confirmation, and verified residency sending steps auto-advance.
22. On `Verification Success`, confirm UVU received residency verification, full address was not shared, and UVU was added to Connected Institutions.
23. Tap `View UVU Access`.
24. On `UVU Permission Detail`, review that UVU can access Utah Residency Verification and enrollment eligibility confirmation.
25. Confirm UVU cannot access full address, birthdate, State ID number, Age Verification, Professional License, or Activity History.
26. Confirm access details: status Active, purpose Enrollment eligibility, last used Today, expires 30 days.
27. Tap `Revoke Access`.
28. On `Revoke Confirmation`, review that UVU will no longer verify residency through this permission, past verification remains visible in Activity, and UVU can send a new request later.
29. Tap `Confirm Revoke`; this marks the UVU permission revoked and adds a revocation Activity History entry.
30. On `Revoke Success`, confirm Utah Valley University no longer has active access and Activity History was updated.
31. Tap `View Activity`.
32. On `Activity`, review the audit trail entries including `State Identity Verified`, `Utah Residency Credential Active`, `UVU residency request received`, `Residency verification shared with Utah Valley University`, `UVU connected permission created`, and `UVU access revoked`.

## Product Framing Preserved

- One screen has one primary job.
- Main tabs are summary destinations only: Wallet, Requests, Institutions, and Activity.
- Workflow-specific content lives in detail, confirmation, processing, success, or optional flow screens.
- Wallet does not mix full permissions, full institutions, full activity, and delegation controls.
- The prototype avoids prohibited technical/futuristic terminology in app source.
- The design remains calm, institutional, card-based, mobile-native, and operationally believable.
