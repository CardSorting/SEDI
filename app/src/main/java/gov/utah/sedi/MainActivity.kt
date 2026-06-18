package gov.utah.sedi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import gov.utah.sedi.domain.ActivityEvent
import gov.utah.sedi.domain.ActivityKind
import gov.utah.sedi.domain.ConnectedInstitution
import gov.utah.sedi.domain.Credential
import gov.utah.sedi.domain.CredentialType
import gov.utah.sedi.domain.CredentialShareRecord
import gov.utah.sedi.domain.IdentityWalletState
import gov.utah.sedi.domain.PermissionStatus
import gov.utah.sedi.domain.RequestStatus
import gov.utah.sedi.domain.ShareResult
import gov.utah.sedi.domain.VerificationRequest
import gov.utah.sedi.presentation.IdentityWalletViewModel
import kotlinx.coroutines.delay

private val Ink = Color(0xFF102A43)
private val Slate = Color(0xFF52616F)
private val Mist = Color(0xFFF6F8FB)
private val CardWhite = Color.White
private val StateBlue = Color(0xFF12324A)
private val TrustBlue = Color(0xFF1F6F9E)
private val Success = Color(0xFF197A56)
private val Warning = Color(0xFF9A5B13)
private val Danger = Color(0xFFB42318)

private object Route {
    const val OnboardingWelcome = "onboardingWelcome"
    const val OnboardingIdentitySetup = "onboardingIdentitySetup"
    const val StateVerificationChecks = "stateVerificationChecks"
    const val OnboardingComplete = "onboardingComplete"
    const val Wallet = "wallet"
    const val StateIdentity = "stateIdentity"
    const val ShareProof = "shareProof"
    const val ResidencyProof = "residencyProof"
    const val AgeProof = "ageProof"
    const val LicenseProof = "licenseProof"
    const val Requests = "requests"
    const val Institutions = "institutions"
    const val Activity = "activity"
    const val CredentialShareHistory = "credentialShareHistory/{credentialId}"
    const val ShareResidencyReview = "shareResidencyReview"
    const val ShareAgeReview = "shareAgeReview"
    const val ShareLicenseReview = "shareLicenseReview"
    const val ShareSuccess = "shareSuccess"
    const val RequestDetail = "request/{requestId}"
    const val SharedDataPreview = "sharedDataPreview/{requestId}"
    const val ApprovalConsent = "approvalConsent/{requestId}"
    const val VerificationProcessing = "verificationProcessing"
    const val VerificationSuccess = "verificationSuccess"
    const val PermissionDetail = "permission/{institutionId}"
    const val RevokeConfirmation = "revoke/{institutionId}"
    const val RevokeSuccess = "revokeSuccess/{institutionId}"
    const val Delegation = "delegation"
}

private sealed class BottomDestination(val route: String, val label: String, val icon: String) {
    data object Wallet : BottomDestination(Route.Wallet, "Wallet", "▣")
    data object Requests : BottomDestination(Route.Requests, "Requests", "↗")
    data object Institutions : BottomDestination(Route.Institutions, "Institutions", "◫")
    data object Activity : BottomDestination(Route.Activity, "Activity", "≡")
}

private val bottomDestinations = listOf(
    BottomDestination.Wallet,
    BottomDestination.Requests,
    BottomDestination.Institutions,
    BottomDestination.Activity
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SediTheme {
                val walletViewModel: IdentityWalletViewModel = viewModel()
                IdentityWalletApp(walletViewModel)
            }
        }
    }
}

@Composable
private fun SediTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = StateBlue,
            secondary = TrustBlue,
            background = Mist,
            surface = CardWhite,
            onPrimary = Color.White,
            onSecondary = Color.White,
            onBackground = Ink,
            onSurface = Ink
        ),
        content = content
    )
}

@Composable
private fun IdentityWalletApp(viewModel: IdentityWalletViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val showBottomBar = state.onboardingComplete && currentRoute in bottomDestinations.map { it.route }

    Scaffold(
        containerColor = Mist,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = CardWhite) {
                    bottomDestinations.forEach { destination ->
                        NavigationBarItem(
                            selected = currentRoute == destination.route,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                NavIconWithBadge(
                                    destination = destination,
                                    pendingCount = state.requests.count { it.status == RequestStatus.Pending }
                                )
                            },
                            label = { Text(destination.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (state.onboardingComplete) Route.Wallet else Route.OnboardingWelcome,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Route.OnboardingWelcome) {
                OnboardingWelcomeScreen(onCreateWallet = { navController.navigate(Route.OnboardingIdentitySetup) })
            }
            composable(Route.OnboardingIdentitySetup) {
                OnboardingIdentitySetupScreen(
                    onVerifyWithStateIdentity = { navController.navigate(Route.StateVerificationChecks) }
                )
            }
            composable(Route.StateVerificationChecks) {
                StateVerificationChecksScreen(
                    onContinue = {
                        viewModel.verifyIdentity()
                        navController.navigate(Route.OnboardingComplete)
                    }
                )
            }
            composable(Route.OnboardingComplete) {
                OnboardingCompleteScreen(
                    onGoToWallet = {
                        viewModel.completeOnboarding()
                        navController.navigate(Route.Wallet) {
                            popUpTo(Route.OnboardingWelcome) { inclusive = true }
                        }
                    }
                )
            }
            composable(Route.Wallet) {
                WalletScreen(
                    state = state,
                    onViewIdentity = { navController.navigate(Route.StateIdentity) },
                    onShareProof = { navController.navigate(Route.ShareProof) },
                    onManageAccess = { navController.navigate(Route.Institutions) },
                    onViewActivity = { navController.navigate(Route.Activity) },
                    onOpenRequest = { navController.navigate("request/$it") }
                )
            }
            composable(Route.StateIdentity) {
                val credential = state.credentials.firstOrNull { it.type == CredentialType.StateIdentity }
                if (credential == null) {
                    MissingScreen("Identity unavailable", onBack = { navController.popBackStack() })
                } else {
                    StateIdentityDetailScreen(
                        credential = credential,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
            composable(Route.ShareProof) {
                ShareProofTypeScreen(
                    onChooseResidency = { navController.navigate(Route.ResidencyProof) },
                    onChooseAge = { navController.navigate(Route.AgeProof) },
                    onChooseLicense = { navController.navigate(Route.LicenseProof) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Route.ResidencyProof) {
                ResidencyProofDetailScreen(
                    onShareProof = { navController.navigate(Route.ShareResidencyReview) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Route.AgeProof) {
                AgeProofDetailScreen(
                    onShareProof = { navController.navigate(Route.ShareAgeReview) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Route.LicenseProof) {
                LicenseProofDetailScreen(
                    onShareProof = { navController.navigate(Route.ShareLicenseReview) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Route.Requests) {
                RequestsListScreen(
                    requests = state.requests,
                    onOpenRequest = { navController.navigate("request/$it") }
                )
            }
            composable(Route.Institutions) {
                InstitutionsListScreen(
                    institutions = state.institutions,
                    onOpenInstitution = { navController.navigate("permission/$it") }
                )
            }
            composable(Route.Activity) {
                ActivityScreen(events = state.activity)
            }
            composable(Route.CredentialShareHistory) { entry ->
                val credentialId = entry.arguments?.getString("credentialId").orEmpty()
                val credential = state.credentials.firstOrNull { it.id == credentialId }
                val history = state.credentialShareHistory.filter { it.credentialId == credentialId }
                if (credential == null) {
                    MissingScreen("Share history unavailable", onBack = { navController.popBackStack() })
                } else {
                    CredentialShareHistoryScreen(
                        credentialTitle = credential.title,
                        history = history,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
            composable(Route.ShareResidencyReview) {
                ShareResidencyReviewScreen(
                    onApprove = {
                        viewModel.approveResidencyShare()
                        navController.navigate(Route.ShareSuccess)
                    },
                    onCancel = { navController.popBackStack() }
                )
            }
            composable(Route.ShareAgeReview) {
                ShareAgeReviewScreen(
                    onApprove = {
                        viewModel.approveAgeShare()
                        navController.navigate(Route.ShareSuccess)
                    },
                    onCancel = { navController.popBackStack() }
                )
            }
            composable(Route.ShareLicenseReview) {
                ShareLicenseReviewScreen(
                    onApprove = {
                        viewModel.approveLicenseShare()
                        navController.navigate(Route.ShareSuccess)
                    },
                    onCancel = { navController.popBackStack() }
                )
            }
            composable(Route.ShareSuccess) {
                val shareResult = state.lastShareResult
                if (shareResult == null) {
                    MissingScreen("Share result unavailable", onBack = { navController.popBackStack() })
                } else {
                    CredentialShareSuccessScreen(
                        shareResult = shareResult,
                        onViewActivity = { navController.navigate(Route.Activity) },
                        onBackToWallet = {
                            viewModel.clearLastShareResult()
                            navController.navigate(Route.Wallet) {
                                popUpTo(navController.graph.findStartDestination().id)
                                launchSingleTop = true
                            }
                        },
                        onViewInstitution = shareResult.institutionId?.let { institutionId ->
                            { navController.navigate("permission/$institutionId") }
                        }
                    )
                }
            }
            composable(Route.RequestDetail) { entry ->
                val request = state.requests.firstOrNull { it.id == entry.arguments?.getString("requestId") }
                if (request == null) MissingScreen("Request unavailable", onBack = { navController.popBackStack() })
                else RequestDetailScreen(
                    request = request,
                    onReviewSharedData = { navController.navigate("sharedDataPreview/${request.id}") },
                    onDeny = {
                        viewModel.denyRequest(request.id)
                        navController.navigate(Route.Requests) { popUpTo(Route.Requests) { inclusive = true } }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Route.SharedDataPreview) { entry ->
                val request = state.requests.firstOrNull { it.id == entry.arguments?.getString("requestId") }
                if (request == null) MissingScreen("Shared data preview unavailable", onBack = { navController.popBackStack() })
                else SharedDataPreviewScreen(
                    request = request,
                    onContinueToApproval = { navController.navigate("approvalConsent/${request.id}") },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Route.ApprovalConsent) { entry ->
                val request = state.requests.firstOrNull { it.id == entry.arguments?.getString("requestId") }
                if (request == null) MissingScreen("Approval unavailable", onBack = { navController.popBackStack() })
                else ApprovalConsentScreen(
                    request = request,
                    onApprove = {
                        viewModel.approveRequestAndShare(request.id)
                        navController.navigate(Route.VerificationProcessing)
                    },
                    onDeny = {
                        viewModel.denyRequest(request.id)
                        navController.navigate(Route.Requests) { popUpTo(Route.Requests) { inclusive = true } }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Route.VerificationProcessing) {
                VerificationProcessingScreen(
                    onFinished = {
                        navController.navigate(Route.VerificationSuccess) {
                            popUpTo(Route.VerificationProcessing) { inclusive = true }
                        }
                    }
                )
            }
            composable(Route.VerificationSuccess) {
                VerificationSuccessScreen(
                    onViewUvuAccess = { navController.navigate("permission/uvu") },
                    onBackToWallet = {
                        navController.navigate(Route.Wallet) {
                            popUpTo(navController.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(Route.PermissionDetail) { entry ->
                val institution = state.institutions.firstOrNull { it.id == entry.arguments?.getString("institutionId") }
                if (institution == null) MissingScreen("Permission unavailable", onBack = { navController.popBackStack() })
                else PermissionDetailScreen(
                    institution = institution,
                    onRevokeAccess = { navController.navigate("revoke/${institution.id}") },
                    onViewActivity = { navController.navigate(Route.Activity) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Route.RevokeConfirmation) { entry ->
                val institution = state.institutions.firstOrNull { it.id == entry.arguments?.getString("institutionId") }
                if (institution == null) MissingScreen("Access unavailable", onBack = { navController.popBackStack() })
                else RevokeConfirmationScreen(
                    institution = institution,
                    onConfirmRevoke = {
                        viewModel.revokeAccess(institution.id)
                        navController.navigate("revokeSuccess/${institution.id}")
                    },
                    onCancel = { navController.popBackStack() }
                )
            }
            composable(Route.RevokeSuccess) { entry ->
                val institutionId = entry.arguments?.getString("institutionId").orEmpty()
                val institution = state.institutions.firstOrNull { it.id == institutionId }
                RevokeSuccessScreen(
                    institutionName = institution?.name ?: "Institution",
                    onViewActivity = { navController.navigate(Route.Activity) },
                    onBackToInstitutions = { navController.navigate(Route.Institutions) }
                )
            }
            composable(Route.Delegation) {
                DelegationScreen(
                    state = state,
                    onRecipientChanged = { viewModel.updateDelegationDraft(recipient = it) },
                    onActionChanged = { viewModel.updateDelegationDraft(allowedAction = it) },
                    onDurationChanged = { viewModel.updateDelegationDraft(duration = it) },
                    onConfirm = {
                        viewModel.grantDelegation()
                        navController.navigate(Route.Institutions)
                    },
                    onCancel = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
private fun NavIconWithBadge(destination: BottomDestination, pendingCount: Int) {
    if (destination == BottomDestination.Requests && pendingCount > 0) {
        BadgedBox(badge = { Badge { Text(pendingCount.toString()) } }) {
            Text(destination.icon, fontWeight = FontWeight.Bold)
        }
    } else {
        Text(destination.icon, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun OnboardingWelcomeScreen(onCreateWallet: () -> Unit) {
    FullScreenStep(
        eyebrow = "Utah Identity Wallet",
        title = "State-backed identity, controlled by you.",
        body = "Create a wallet for verified state credentials and approve exactly what each institution can receive.",
        actionLabel = "Create Identity Wallet",
        onAction = onCreateWallet
    ) {
        WalletMark()
        Spacer(Modifier.height(24.dp))
        DataListPanel(
            title = "What this wallet does",
            tone = TrustBlue,
            rows = listOf("Holds verified state credentials", "Shows requests before sharing", "Lets you revoke access later")
        )
    }
}

@Composable
private fun OnboardingIdentitySetupScreen(onVerifyWithStateIdentity: () -> Unit) {
    FullScreenStep(
        eyebrow = "Identity setup",
        title = "Connect to State Identity",
        body = "Begin wallet setup by verifying your identity with the State of Utah.",
        actionLabel = "Verify with State Identity",
        onAction = onVerifyWithStateIdentity
    ) {
        StatusCircle("ID", TrustBlue.copy(alpha = 0.12f), textColor = TrustBlue)
        Spacer(Modifier.height(24.dp))
        DataListPanel(
            title = "Setup will confirm",
            tone = TrustBlue,
            rows = listOf("State identity record", "Utah residency credential", "Wallet eligibility")
        )
    }
}

@Composable
private fun StateVerificationChecksScreen(onContinue: () -> Unit) {
    FullScreenStep(
        eyebrow = "State verification",
        title = "Verification checks complete",
        body = "Your state-backed identity wallet is ready to activate.",
        actionLabel = "Continue",
        onAction = onContinue
    ) {
        StatusCircle("✓", Success.copy(alpha = 0.12f), textColor = Success)
        Spacer(Modifier.height(24.dp))
        CheckPanel(rows = listOf("State ID matched", "Utah residency confirmed", "Identity status verified"))
    }
}

@Composable
private fun OnboardingCompleteScreen(onGoToWallet: () -> Unit) {
    FullScreenStep(
        eyebrow = "Setup complete",
        title = "State Identity Verified",
        body = "Your identity wallet is ready.",
        actionLabel = "Go to Wallet",
        onAction = onGoToWallet
    ) {
        StatusCircle("✓", Success.copy(alpha = 0.12f), textColor = Success)
        Spacer(Modifier.height(24.dp))
        CalmPanel {
            Text("Your wallet is ready.", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("You can now review requests, share verified proofs, manage institution access, and audit activity.", color = Slate)
        }
    }
}

@Composable
private fun FullScreenStep(
    eyebrow: String,
    title: String,
    body: String,
    actionLabel: String,
    onAction: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(color = Mist, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Spacer(Modifier.height(36.dp))
                content()
                Spacer(Modifier.height(32.dp))
                Text(eyebrow, color = TrustBlue, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(10.dp))
                Text(title, style = MaterialTheme.typography.headlineLarge, color = Ink, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(14.dp))
                Text(body, style = MaterialTheme.typography.bodyLarge, color = Slate)
            }
            Button(onClick = onAction, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(16.dp)) {
                Text(actionLabel, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun WalletScreen(
    state: IdentityWalletState,
    onViewIdentity: () -> Unit,
    onShareProof: () -> Unit,
    onManageAccess: () -> Unit,
    onViewActivity: () -> Unit,
    onOpenRequest: (String) -> Unit
) {
    val pendingRequest = state.requests.firstOrNull { it.status == RequestStatus.Pending }
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ScreenHeader("Wallet", "Your identity home")
        IdentityStatusCard(onViewIdentity = onViewIdentity)
        if (pendingRequest != null) {
            PrimaryActionCard(
                title = pendingRequest.title,
                subtitle = "Review what UVU will receive before sharing.",
                onReviewRequest = { onOpenRequest(pendingRequest.id) }
            )
        }
        SectionTitle("Quick actions")
        WalletQuickAction(label = "Share a Proof", onClick = onShareProof)
        WalletQuickAction(label = "Manage Access", onClick = onManageAccess)
        WalletQuickAction(label = "View Activity", onClick = onViewActivity)
        PrivacySummaryCard()
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun IdentityStatusCard(onViewIdentity: () -> Unit) {
    CalmPanel {
        Text("State Identity Verified", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Your Utah identity wallet is active.", color = Slate, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(14.dp))
        Button(onClick = onViewIdentity, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
            Text("View Identity")
        }
    }
}

@Composable
private fun PrimaryActionCard(title: String, subtitle: String, onReviewRequest: () -> Unit) {
    CalmPanel {
        Text(title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Text(subtitle, color = Slate, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(14.dp))
        Button(onClick = onReviewRequest, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
            Text("Review Request")
        }
    }
}

@Composable
private fun WalletQuickAction(label: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(label, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun PrivacySummaryCard() {
    CalmPanel {
        Text("You choose what to share", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Text("Institutions receive only the proof you approve.", color = Slate, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun ShareProofTypeScreen(
    onChooseResidency: () -> Unit,
    onChooseAge: () -> Unit,
    onChooseLicense: () -> Unit,
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { TopBackRow(title = "Share a Proof", onBack = onBack) }
        item {
            Text(
                "Choose what you need to verify.",
                style = MaterialTheme.typography.bodyMedium,
                color = Slate,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        item {
            ProofTypeOptionCard(
                title = "Residency",
                description = "Verify Utah residency without sharing your full address.",
                onClick = onChooseResidency
            )
        }
        item {
            ProofTypeOptionCard(
                title = "Age",
                description = "Verify age eligibility without sharing your birthdate.",
                onClick = onChooseAge
            )
        }
        item {
            ProofTypeOptionCard(
                title = "License Status",
                description = "Verify license status without sharing unrelated information.",
                onClick = onChooseLicense
            )
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun ProofTypeOptionCard(title: String, description: String, onClick: () -> Unit) {
    CalmPanel {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Text(description, color = Slate, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(14.dp))
        Button(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
            Text("Choose $title")
        }
    }
}

@Composable
private fun StateIdentityDetailScreen(credential: Credential, onBack: () -> Unit) {
    DetailScreen(title = "State Identity", onBack = onBack) {
        CalmPanel {
            Text("State Identity Verified", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(14.dp))
            InfoRow("Issuer", "State of Utah")
            InfoRow("Status", "Active")
            InfoRow("Verified on", credential.lastVerified)
            InfoRow("Wallet", "Identity wallet active")
        }
    }
}

@Composable
private fun ResidencyProofDetailScreen(onShareProof: () -> Unit, onBack: () -> Unit) {
    DetailScreen(title = "Residency Proof", onBack = onBack) {
        CalmPanel {
            Text("Utah Residency: Verified", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(14.dp))
            InfoRow("Can prove", "Utah residency")
            InfoRow("Hidden by default", "Full address, birthdate, ID number")
        }
        Button(onClick = onShareProof, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(16.dp)) {
            Text("Share Residency Proof", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun AgeProofDetailScreen(onShareProof: () -> Unit, onBack: () -> Unit) {
    DetailScreen(title = "Age Proof", onBack = onBack) {
        CalmPanel {
            Text("Age Eligibility: Verified", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(14.dp))
            InfoRow("Can prove", "18+ or 21+")
            InfoRow("Hidden by default", "Birthdate, ID number")
        }
        Button(onClick = onShareProof, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(16.dp)) {
            Text("Share Age Proof", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun LicenseProofDetailScreen(onShareProof: () -> Unit, onBack: () -> Unit) {
    DetailScreen(title = "License Proof", onBack = onBack) {
        CalmPanel {
            Text("License Status: Verified", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(14.dp))
            InfoRow("Can prove", "Active license status")
            InfoRow("Hidden by default", "Unrelated credentials")
        }
        Button(onClick = onShareProof, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(16.dp)) {
            Text("Share License Proof", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun ShareResidencyReviewScreen(onApprove: () -> Unit, onCancel: () -> Unit) {
    DetailScreen(title = "Share Residency Review", onBack = onCancel) {
        CalmPanel {
            Text("Review residency proof", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Confirm what will be shared before sending verification.", color = Slate)
        }
        DataListPanel("Shared", Success, listOf("Utah residency verified", "Issuer: State of Utah", "Status: Active"))
        DataListPanel("Hidden", Slate, listOf("Full address", "Birthdate", "State ID number", "Unrelated credentials"))
        Button(onClick = onApprove, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(16.dp)) {
            Text("Approve Share", fontWeight = FontWeight.SemiBold)
        }
        OutlinedButton(onClick = onCancel, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp)) {
            Text("Cancel")
        }
    }
}

@Composable
private fun ShareAgeReviewScreen(onApprove: () -> Unit, onCancel: () -> Unit) {
    DetailScreen(title = "Share Age Review", onBack = onCancel) {
        CalmPanel {
            Text("Review age proof", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Only the age threshold is shared, not your full record.", color = Slate)
        }
        DataListPanel("Shared", Success, listOf("21+ verified"))
        DataListPanel("Hidden", Slate, listOf("Exact birthdate", "ID number", "Full legal record"))
        Button(onClick = onApprove, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(16.dp)) {
            Text("Approve Share", fontWeight = FontWeight.SemiBold)
        }
        OutlinedButton(onClick = onCancel, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp)) {
            Text("Cancel")
        }
    }
}

@Composable
private fun ShareLicenseReviewScreen(onApprove: () -> Unit, onCancel: () -> Unit) {
    DetailScreen(title = "Share License Review", onBack = onCancel) {
        CalmPanel {
            Text("Review license verification", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Only license status and issuer details are shared.", color = Slate)
        }
        DataListPanel("Shared", Success, listOf("License active", "Issuer: Utah Division of Professional Licensing", "License class if applicable", "Expiration"))
        DataListPanel("Hidden", Slate, listOf("Unrelated credentials", "Personal identity details not needed"))
        Button(onClick = onApprove, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(16.dp)) {
            Text("Approve Share", fontWeight = FontWeight.SemiBold)
        }
        OutlinedButton(onClick = onCancel, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp)) {
            Text("Cancel")
        }
    }
}

@Composable
private fun CredentialShareSuccessScreen(
    shareResult: ShareResult,
    onViewActivity: () -> Unit,
    onBackToWallet: () -> Unit,
    onViewInstitution: (() -> Unit)?
) {
    Column(
        modifier = Modifier.fillMaxSize().background(Mist).padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(Modifier.height(20.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            StatusCircle("✓", Success.copy(alpha = 0.12f), textColor = Success)
            Spacer(Modifier.height(24.dp))
            Text("Verification Shared", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Ink)
            Spacer(Modifier.height(12.dp))
            Text(
                "${shareResult.credentialTitle} was shared with ${shareResult.recipient}.",
                style = MaterialTheme.typography.bodyLarge,
                color = Slate
            )
            Spacer(Modifier.height(20.dp))
            DataListPanel("Shared", Success, shareResult.sharedItems)
            Spacer(Modifier.height(12.dp))
            DataListPanel("Stayed private", Slate, shareResult.hiddenItems)
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onViewActivity, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(16.dp)) {
                Text("View Activity")
            }
            if (onViewInstitution != null) {
                OutlinedButton(onClick = onViewInstitution, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp)) {
                    Text("View Connected Institution")
                }
            }
            OutlinedButton(onClick = onBackToWallet, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp)) {
                Text("Back to Wallet")
            }
        }
    }
}

@Composable
private fun CredentialShareHistoryScreen(
    credentialTitle: String,
    history: List<CredentialShareRecord>,
    onBack: () -> Unit
) {
    DetailScreen(title = "Share History", onBack = onBack) {
        CalmPanel {
            Text(credentialTitle, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Past shares for this credential.", color = Slate)
        }
        if (history.isEmpty()) {
            EmptyCard("No share history yet")
        } else {
            history.forEach { record ->
                CalmPanel {
                    InfoRow("Shared with", record.recipient)
                    InfoRow("Date", record.timestamp)
                    InfoRow("Purpose", record.purpose)
                    InfoRow("Result", record.result)
                }
            }
        }
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp)) {
            Text("Back")
        }
    }
}

@Composable
private fun RequestsListScreen(requests: List<VerificationRequest>, onOpenRequest: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { ScreenHeader("Requests", "Incoming verification requests") }
        items(requests) { request ->
            RequestCard(request = request, onOpenRequest = { onOpenRequest(request.id) })
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun RequestDetailScreen(
    request: VerificationRequest,
    onReviewSharedData: () -> Unit,
    onDeny: () -> Unit,
    onBack: () -> Unit
) {
    DetailScreen(title = "Request Detail", onBack = onBack) {
        CalmPanel {
            Text(request.institutionName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Requests proof of Utah residency", color = Slate)
            Spacer(Modifier.height(14.dp))
            InfoRow("Purpose", request.purpose)
            InfoRow("Requested proof", request.requestedProof)
            InfoRow("Expires in", request.expires)
        }
        Button(onClick = onReviewSharedData, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(16.dp)) {
            Text("Review What Will Be Shared", fontWeight = FontWeight.SemiBold)
        }
        OutlinedButton(onClick = onDeny, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp)) {
            Text("Deny")
        }
    }
}

@Composable
private fun SharedDataPreviewScreen(request: VerificationRequest, onContinueToApproval: () -> Unit, onBack: () -> Unit) {
    DetailScreen(title = "Shared Data Preview", onBack = onBack) {
        CalmPanel {
            Text("What Utah Valley University receives", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Only the required residency proof is prepared for this request.", color = Slate)
        }
        DataListPanel("UVU will receive", Success, request.sharedData)
        DataListPanel("UVU will NOT receive", Slate, request.hiddenData)
        Button(onClick = onContinueToApproval, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(16.dp)) {
            Text("Continue to Approval", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun ApprovalConsentScreen(request: VerificationRequest, onApprove: () -> Unit, onDeny: () -> Unit, onBack: () -> Unit) {
    DetailScreen(title = "Share Verification", onBack = onBack) {
        CalmPanel {
            Text("Approve sharing residency verification with Utah Valley University?", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Review and approve this one-time proof for enrollment eligibility.", color = Slate)
        }
        DataListPanel(
            "Permission summary",
            TrustBlue,
            listOf(
                "Share once",
                "For ${request.purpose.lowercase()}",
                "No full address shared",
                "UVU added to connected institutions after approval"
            )
        )
        Button(onClick = onApprove, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(16.dp)) {
            Text("Approve Share", fontWeight = FontWeight.SemiBold)
        }
        OutlinedButton(onClick = onDeny, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp)) {
            Text("Deny")
        }
    }
}

@Composable
private fun VerificationProcessingScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1400)
        onFinished()
    }
    Column(
        modifier = Modifier.fillMaxSize().background(Mist).padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StatusCircle("…", TrustBlue.copy(alpha = 0.12f), textColor = TrustBlue)
        Spacer(Modifier.height(24.dp))
        Text("Sending Verification", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Ink)
        Spacer(Modifier.height(16.dp))
        DataListPanel(
            title = "In progress",
            tone = TrustBlue,
            rows = listOf("Preparing proof", "Confirming state-issued credential", "Sending verified residency status to UVU")
        )
    }
}

@Composable
private fun VerificationSuccessScreen(onViewUvuAccess: () -> Unit, onBackToWallet: () -> Unit) {
    ResultScreen(
        title = "Verification Complete",
        body = "Utah Valley University received residency verification. Your full address was not shared. UVU has been added to Connected Institutions.",
        primaryLabel = "View UVU Access",
        onPrimary = onViewUvuAccess,
        secondaryLabel = "Back to Wallet",
        onSecondary = onBackToWallet
    )
}

@Composable
private fun InstitutionsListScreen(institutions: List<ConnectedInstitution>, onOpenInstitution: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { ScreenHeader("Institutions", "Connected Institutions") }
        items(institutions) { institution ->
            InstitutionCard(institution = institution, onOpenInstitution = { onOpenInstitution(institution.id) })
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun PermissionDetailScreen(
    institution: ConnectedInstitution,
    onRevokeAccess: () -> Unit,
    onViewActivity: () -> Unit,
    onBack: () -> Unit
) {
    DetailScreen(title = if (institution.id == "uvu") "UVU Permission Detail" else "Permission Detail", onBack = onBack) {
        CalmPanel {
            Row(verticalAlignment = Alignment.CenterVertically) {
                InstitutionAvatar(institution.name)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(institution.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(institution.category, color = Slate)
                }
                PermissionChip(institution.status)
            }
            Spacer(Modifier.height(16.dp))
            InfoRow("Purpose", institution.accessScope)
            InfoRow("Last used", institution.lastUsed)
            InfoRow("Expires", institution.expiration)
            InfoRow("Status", institution.status.name.lowercase().replaceFirstChar { it.uppercase() })
        }
        DataListPanel("${institution.name} can access", Success, institution.allowedData)
        DataListPanel("${institution.name} cannot access", Slate, institution.hiddenData)
        if (institution.status == PermissionStatus.Active) {
            Button(
                onClick = onRevokeAccess,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Danger)
            ) { Text("Revoke Access", fontWeight = FontWeight.SemiBold) }
        } else {
            EmptyCard("No active access")
        }
        OutlinedButton(onClick = onViewActivity, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp)) {
            Text("View Activity")
        }
    }
}

@Composable
private fun RevokeConfirmationScreen(institution: ConnectedInstitution, onConfirmRevoke: () -> Unit, onCancel: () -> Unit) {
    DetailScreen(title = "Revoke Confirmation", onBack = onCancel) {
        CalmPanel {
            Text("Revoke ${institution.name} access?", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            BulletRow("UVU will no longer be able to verify residency through this permission.")
            BulletRow("Past verification remains visible in Activity.")
            BulletRow("UVU can send a new request later.")
        }
        Button(
            onClick = onConfirmRevoke,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Danger)
        ) { Text("Confirm Revoke", fontWeight = FontWeight.SemiBold) }
        OutlinedButton(onClick = onCancel, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp)) {
            Text("Cancel")
        }
    }
}

@Composable
private fun RevokeSuccessScreen(institutionName: String, onViewActivity: () -> Unit, onBackToInstitutions: () -> Unit) {
    ResultScreen(
        title = "Access Revoked",
        body = "$institutionName no longer has active access. Activity history has been updated.",
        primaryLabel = "View Activity",
        onPrimary = onViewActivity,
        secondaryLabel = "Back to Institutions",
        onSecondary = onBackToInstitutions
    )
}

@Composable
private fun ActivityScreen(events: List<ActivityEvent>) {
    val orderedEvents = events.sortedBy { event ->
        when (event.kind) {
            ActivityKind.IdentityVerified -> 1
            ActivityKind.RequestReceived -> 2
            ActivityKind.CredentialShared -> 3
            ActivityKind.PermissionCreated -> 4
            ActivityKind.AccessRevoked -> 5
            ActivityKind.RequestApproved -> 6
            ActivityKind.RequestDenied -> 7
            ActivityKind.TemporaryAccessGranted -> 8
        }
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { ScreenHeader("Activity", "Activity History") }
        items(orderedEvents) { event -> ActivityTimelineCard(event) }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun DelegationScreen(
    state: IdentityWalletState,
    onRecipientChanged: (String) -> Unit,
    onActionChanged: (String) -> Unit,
    onDurationChanged: (String) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    val draft = state.delegationDraft
    DetailScreen(title = "Delegation", onBack = onCancel) {
        CalmPanel {
            Text("Delegation Intro", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Separate optional flow for temporary sharing after the primary demo journey works.", color = Slate)
        }
        OutlinedTextField(value = draft.recipient, onValueChange = onRecipientChanged, modifier = Modifier.fillMaxWidth(), label = { Text("Choose Recipient") }, shape = RoundedCornerShape(16.dp))
        OutlinedTextField(value = draft.allowedAction, onValueChange = onActionChanged, modifier = Modifier.fillMaxWidth(), label = { Text("Choose Scope") }, shape = RoundedCornerShape(16.dp))
        OutlinedTextField(value = draft.duration, onValueChange = onDurationChanged, modifier = Modifier.fillMaxWidth(), label = { Text("Set Duration") }, shape = RoundedCornerShape(16.dp))
        DataListPanel("Review Delegation", TrustBlue, listOf(draft.recipient.ifBlank { "Recipient not set" }, draft.allowedAction, draft.duration))
        Button(onClick = onConfirm, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(16.dp)) { Text("Create Delegation") }
    }
}

@Composable
private fun DetailScreen(title: String, onBack: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(Mist).verticalScroll(rememberScrollState()).padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        TopBackRow(title = title, onBack = onBack)
        content()
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun ResultScreen(
    title: String,
    body: String,
    primaryLabel: String,
    onPrimary: () -> Unit,
    secondaryLabel: String,
    onSecondary: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().background(Mist).padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(Modifier.height(20.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            StatusCircle("✓", Success.copy(alpha = 0.12f), textColor = Success)
            Spacer(Modifier.height(24.dp))
            Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Ink)
            Spacer(Modifier.height(12.dp))
            Text(body, style = MaterialTheme.typography.bodyLarge, color = Slate)
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onPrimary, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(16.dp)) { Text(primaryLabel) }
            OutlinedButton(onClick = onSecondary, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp)) { Text(secondaryLabel) }
        }
    }
}

@Composable
private fun RequestCard(request: VerificationRequest, onOpenRequest: () -> Unit) {
    CalmPanel {
        Row(verticalAlignment = Alignment.CenterVertically) {
            InstitutionAvatar(request.institutionName)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(request.institutionName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(request.requestedProof, color = Slate)
            }
            RequestChip(request.status)
        }
        Spacer(Modifier.height(12.dp))
        Text(request.purpose, color = Slate)
        Spacer(Modifier.height(10.dp))
        InfoRow("Request", "Proof of Utah residency")
        InfoRow("Purpose", request.purpose)
        InfoRow("Status", request.status.name)
        InfoRow("Expires", request.expires)
        Button(onClick = onOpenRequest, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) { Text("Review Request") }
    }
}

@Composable
private fun InstitutionCard(institution: ConnectedInstitution, onOpenInstitution: () -> Unit) {
    CalmPanel {
        Row(verticalAlignment = Alignment.CenterVertically) {
            InstitutionAvatar(institution.name)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(institution.name, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                Text(institution.accessScope, color = Slate, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            InstitutionAccessChip(institution)
        }
        Spacer(Modifier.height(12.dp))
        InfoRow("Access", institution.accessScope)
        InfoRow("Expires", institution.expiration)
        InfoRow("Last used", institution.lastUsed)
        Button(onClick = onOpenInstitution, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) { Text("View Access") }
    }
}

@Composable
private fun ActivityTimelineCard(event: ActivityEvent) {
    CalmPanel {
        Row(verticalAlignment = Alignment.Top) {
            StatusCircle("•", TrustBlue.copy(alpha = 0.12f), textColor = TrustBlue)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(event.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text(event.description, color = Slate)
                Spacer(Modifier.height(8.dp))
                InfoRow("Timestamp", event.timestamp)
                InfoRow("Institution", event.institutionName)
                InfoRow("Result", event.result)
            }
        }
    }
}

@Composable
private fun CheckPanel(rows: List<String>) {
    CalmPanel {
        rows.forEach { row ->
            Row(modifier = Modifier.padding(vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
                StatusCircle("✓", Success.copy(alpha = 0.12f), textColor = Success)
                Spacer(Modifier.width(12.dp))
                Text(row, color = Ink, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun CalmPanel(content: @Composable ColumnScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) { content() }
    }
}

@Composable
private fun DataListPanel(title: String, tone: Color, rows: List<String>) {
    CalmPanel {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(10.dp))
        rows.forEach { row ->
            Row(modifier = Modifier.padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(tone))
                Spacer(Modifier.width(10.dp))
                Text(row, color = Ink)
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.Top) {
        Text(label, color = Slate, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(112.dp))
        Text(value, color = Ink, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun TopBackRow(title: String, onBack: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 12.dp)) {
        TextButton(onClick = onBack) { Text("Back") }
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 6.dp))
    }
}

@Composable
private fun ScreenHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(top = 26.dp, bottom = 4.dp)) {
        Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Ink)
        Spacer(Modifier.height(6.dp))
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = Slate)
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Ink, modifier = Modifier.padding(top = 6.dp))
}

@Composable
private fun BulletRow(text: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
        Text("•", color = TrustBlue, fontWeight = FontWeight.Bold)
        Spacer(Modifier.width(8.dp))
        Text(text, color = Slate, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun WalletMark() {
    Box(
        modifier = Modifier.size(68.dp).clip(RoundedCornerShape(22.dp)).background(StateBlue),
        contentAlignment = Alignment.Center
    ) {
        Text("ID", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun StatusCircle(text: String, color: Color, textColor: Color = Color.White) {
    Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(color), contentAlignment = Alignment.Center) {
        Text(text, color = textColor, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun InstitutionAvatar(name: String) {
    val initials = name.split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }
    Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFFEAF3F8)), contentAlignment = Alignment.Center) {
        Text(initials, color = StateBlue, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun HeroMeta(label: String, value: String) {
    Column {
        Text(label, color = Color.White.copy(alpha = 0.62f), style = MaterialTheme.typography.labelSmall)
        Text(value, color = Color.White, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SoftChip(label: String, color: Color) {
    Box(modifier = Modifier.clip(CircleShape).background(color.copy(alpha = 0.10f)).padding(horizontal = 10.dp, vertical = 6.dp)) {
        Text(label, color = color, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun PermissionChip(status: PermissionStatus) {
    val color = when (status) {
        PermissionStatus.Active -> Success
        PermissionStatus.Revoked -> Danger
        PermissionStatus.Expired -> Warning
    }
    SoftChip(status.name.lowercase().replaceFirstChar { it.uppercase() }, color)
}

@Composable
private fun InstitutionAccessChip(institution: ConnectedInstitution) {
    if (institution.status == PermissionStatus.Active) {
        SoftChip("Active", Success)
    } else {
        SoftChip("No active access", Slate)
    }
}

@Composable
private fun RequestChip(status: RequestStatus) {
    val color = when (status) {
        RequestStatus.Pending -> Warning
        RequestStatus.Approved -> Success
        RequestStatus.Denied -> Danger
    }
    SoftChip(status.name.lowercase().replaceFirstChar { it.uppercase() }, color)
}

@Composable
private fun EmptyCard(message: String) {
    CalmPanel { Text(message, color = Slate) }
}

@Composable
private fun MissingScreen(title: String, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(Mist).padding(24.dp), verticalArrangement = Arrangement.Center) {
        Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Button(onClick = onBack) { Text("Back") }
    }
}
