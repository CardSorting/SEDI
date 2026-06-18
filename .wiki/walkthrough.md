# SEDI Stakeholder Walkthrough

## Primary Demo Path Implemented

1. Launch the Android app.
2. On `OnboardingWelcome`, introduce `Utah Identity Wallet` and the message `State-backed identity, controlled by you.`
3. Tap `Create Identity Wallet`.
4. On `OnboardingVerify`, show `Verify your State Identity` with State ID check, Residency check, and Identity match check.
5. Tap `Verify Identity`.
6. On `OnboardingComplete`, show `State Identity Verified`, the checkmark, and `Your identity wallet is ready.`
7. Tap `Go to Wallet`.
8. On the `Wallet` tab, show the verified State Identity card, credential cards for Utah Residency, Age Verification, and Professional License, and the recent University of Utah request preview.
9. Open the University of Utah request from the Wallet preview or the `Requests` tab.
10. On `Request Detail`, review requester name, purpose, requested proof, expiration, what University of Utah will receive, and what stays private.
11. Tap `Review Share`.
12. On `Share Review`, confirm `Share Utah Residency Verification`, shared `Utah residency verified`, and hidden full address, birthdate, and ID number.
13. Tap `Approve Share`.
14. On `Verification Success`, show `Verification Complete`, University of Utah received residency verification, no full address was shared, and permission was added to Connected Institutions.
15. Tap `View Institution Access` or open the `Institutions` tab.
16. On `Permission Detail`, show University of Utah can access Utah residency verification and student eligibility status, cannot access full address/birthdate/ID number/unrelated credentials, and has active access expiring in 30 days.
17. Tap `Revoke Access`.
18. On `Revoke Confirmation`, confirm that University of Utah will no longer verify residency, previous verification remains in activity history, and a new request can be approved later.
19. Tap `Confirm Revoke`.
20. On `Revoke Success`, show `Access Revoked`, University of Utah access removed, and Activity log updated.
21. Tap `View Activity`.
22. On `Activity`, show the audit trail entries for State Identity Verified, Residency request received, Residency shared with University of Utah, University access created, and University access revoked.

## Product Framing Preserved

- One screen has one primary job.
- Main tabs are summary destinations only: Wallet, Requests, Institutions, and Activity.
- Workflow-specific content lives in detail, confirmation, success, or optional flow screens.
- Wallet no longer mixes full permissions, full institutions, full activity, and delegation controls.
- The prototype avoids crypto/blockchain/trust-graph terminology and visuals in app source.
- The design remains calm, institutional, card-based, and mobile-native.
