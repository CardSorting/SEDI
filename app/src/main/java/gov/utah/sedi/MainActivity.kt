package gov.utah.sedi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import gov.utah.sedi.domain.ActivityEvent
import gov.utah.sedi.domain.ConnectedInstitution
import gov.utah.sedi.domain.Credential
import gov.utah.sedi.domain.IdentityWalletState
import gov.utah.sedi.domain.PermissionStatus
import gov.utah.sedi.domain.RequestStatus
import gov.utah.sedi.domain.VerificationRequest
import gov.utah.sedi.presentation.IdentityWalletViewModel

private val Ink = Color(0xFF102A43)
private val Slate = Color(0xFF52616F)
private val Mist = Color(0xFFF6F8FB)
private val CardWhite = Color(0xFFFFFFFF)
private val StateBlue = Color(0xFF12324A)
private val TrustBlue = Color(0xFF1F6F9E)
private val Success = Color(0xFF197A56)
private val Warning = Color(0xFF9A5B13)
private val Danger = Color(0xFFB42318)
private val SafetyTint = Color(0xFFEAF7F1)

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
    if (!state.onboardingComplete) {
        OnboardingScreen(onFinished = viewModel::completeOnboarding)
    } else {
        val navController = rememberNavController()
        WalletScaffold(
            navController = navController,
            state = state,
            onDenyRequest = viewModel::denyRequest,
            onApproveShare = viewModel::approveRequestAndShare,
            onUpdateDelegationRecipient = { viewModel.updateDelegationDraft(recipient = it) },
            onUpdateDelegationAction = { viewModel.updateDelegationDraft(allowedAction = it) },
            onUpdateDelegationDuration = { viewModel.updateDelegationDraft(duration = it) },
            onGrantDelegation = viewModel::grantDelegation,
            onRevokeAccess = viewModel::revokeAccess
        )
    }
}

@Composable
private fun OnboardingScreen(onFinished: () -> Unit) {
    var step by remember { mutableIntStateOf(0) }
    val steps = listOf(
        OnboardingStep(
            eyebrow = "State-Endorsed Digital Identity",
            title = "Your identity wallet",
            body = "Approve verified identity requests with the same clarity you expect from connected apps — but for state-backed credentials.",
            action = "Create Identity Wallet"
        ),
        OnboardingStep(
            eyebrow = "Verify once",
            title = "Create Identity Wallet",
            body = "Your wallet will hold verified credentials such as Utah residency, age verification, and professional license status.",
            action = "Verify State Identity"
        ),
        OnboardingStep(
            eyebrow = "State identity check",
            title = "Verify State Identity",
            body = "Confirm your State of Utah identity so institutions can request only the proof they need — never the full document.",
            action = "Complete Verification"
        ),
        OnboardingStep(
            eyebrow = "Success",
            title = "State Identity Verified",
            body = "Your wallet is ready. You control approvals, expiration dates, connected institutions, and revocation.",
            action = "Go to Wallet"
        )
    )
    val current = steps[step]
    Surface(color = Mist, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(Modifier.height(24.dp))
            Column {
                WalletMark()
                Spacer(Modifier.height(32.dp))
                Text(current.eyebrow, color = TrustBlue, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(12.dp))
                Text(current.title, style = MaterialTheme.typography.headlineLarge, color = Ink, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                Text(current.body, style = MaterialTheme.typography.bodyLarge, color = Slate)
                Spacer(Modifier.height(28.dp))
                DemoProgress(step = step, total = steps.size)
            }
            Column {
                CalmPanel {
                    Text("What institutions see", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(12.dp))
                    BulletRow("Verified answer, not raw identity documents")
                    BulletRow("Permission scopes and expiration dates")
                    BulletRow("User-controlled revocation at any time")
                }
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = {
                        if (step == steps.lastIndex) onFinished() else step += 1
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(current.action, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

private data class OnboardingStep(
    val eyebrow: String,
    val title: String,
    val body: String,
    val action: String
)

private sealed class BottomDestination(val route: String, val label: String, val icon: String) {
    data object Wallet : BottomDestination("wallet", "Home", "▣")
    data object Requests : BottomDestination("requests", "Requests", "↗")
    data object Institutions : BottomDestination("institutions", "Access", "◫")
    data object Activity : BottomDestination("activity", "Activity", "≡")
}

private val bottomDestinations = listOf(
    BottomDestination.Wallet,
    BottomDestination.Requests,
    BottomDestination.Institutions,
    BottomDestination.Activity
)

@Composable
private fun WalletScaffold(
    navController: NavHostController,
    state: IdentityWalletState,
    onDenyRequest: (String) -> Unit,
    onApproveShare: (String) -> Unit,
    onUpdateDelegationRecipient: (String) -> Unit,
    onUpdateDelegationAction: (String) -> Unit,
    onUpdateDelegationDuration: (String) -> Unit,
    onGrantDelegation: () -> Unit,
    onRevokeAccess: (String) -> Unit
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in bottomDestinations.map { it.route }

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
            startDestination = BottomDestination.Wallet.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomDestination.Wallet.route) {
                WalletScreen(
                    state = state,
                    onOpenRequest = { navController.navigate("share/$it") },
                    onOpenRequests = { navController.navigate(BottomDestination.Requests.route) },
                    onOpenInstitutions = { navController.navigate(BottomDestination.Institutions.route) },
                    onOpenDelegation = { navController.navigate("delegation") }
                )
            }
            composable(BottomDestination.Requests.route) {
                RequestsScreen(
                    requests = state.requests,
                    onReview = { navController.navigate("share/$it") },
                    onDeny = onDenyRequest
                )
            }
            composable(BottomDestination.Institutions.route) {
                InstitutionsScreen(
                    institutions = state.institutions,
                    onOpenInstitution = { navController.navigate("permission/$it") },
                    onOpenDelegation = { navController.navigate("delegation") }
                )
            }
            composable(BottomDestination.Activity.route) {
                ActivityScreen(events = state.activity)
            }
            composable("share/{requestId}") { entry ->
                val requestId = entry.arguments?.getString("requestId").orEmpty()
                val request = state.requests.firstOrNull { it.id == requestId }
                if (request == null) {
                    MissingScreen(title = "Request unavailable", onBack = { navController.popBackStack() })
                } else {
                    ShareCredentialScreen(
                        request = request,
                        onApprove = {
                            onApproveShare(request.id)
                            navController.navigate("shareComplete/${request.institutionName}") {
                                popUpTo(BottomDestination.Wallet.route)
                            }
                        },
                        onCancel = { navController.popBackStack() }
                    )
                }
            }
            composable("shareComplete/{institution}") { entry ->
                ShareCompleteScreen(
                    institution = entry.arguments?.getString("institution").orEmpty(),
                    onViewInstitutions = {
                        navController.navigate(BottomDestination.Institutions.route) {
                            popUpTo(BottomDestination.Wallet.route)
                        }
                    },
                    onViewActivity = {
                        navController.navigate(BottomDestination.Activity.route) {
                            popUpTo(BottomDestination.Wallet.route)
                        }
                    }
                )
            }
            composable("permission/{institutionId}") { entry ->
                val institutionId = entry.arguments?.getString("institutionId").orEmpty()
                val institution = state.institutions.firstOrNull { it.id == institutionId }
                if (institution == null) {
                    MissingScreen(title = "Permission unavailable", onBack = { navController.popBackStack() })
                } else {
                    PermissionDetailScreen(
                        institution = institution,
                        events = state.activity.filter { it.institutionId == institution.id },
                        onBack = { navController.popBackStack() },
                        onRevoke = onRevokeAccess
                    )
                }
            }
            composable("delegation") {
                DelegationScreen(
                    state = state,
                    onRecipientChanged = onUpdateDelegationRecipient,
                    onActionChanged = onUpdateDelegationAction,
                    onDurationChanged = onUpdateDelegationDuration,
                    onConfirm = {
                        onGrantDelegation()
                        navController.navigate(BottomDestination.Institutions.route) {
                            popUpTo(BottomDestination.Wallet.route)
                        }
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
private fun WalletScreen(
    state: IdentityWalletState,
    onOpenRequest: (String) -> Unit,
    onOpenRequests: () -> Unit,
    onOpenInstitutions: () -> Unit,
    onOpenDelegation: () -> Unit
) {
    val pendingRequest = state.requests.firstOrNull { it.status == RequestStatus.Pending }
    val pendingCount = state.requests.count { it.status == RequestStatus.Pending }
    val activeAccessCount = state.institutions.count { it.status == PermissionStatus.Active }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { ScreenHeader("Home", "Your verified ID, requests, and access controls") }
        item {
            HeroIdentityCard()
        }
        item {
            TrustBanner(
                title = "You approve every share",
                body = "Organizations get a verified answer only after you review it. Your full ID, document image, and private details stay in your wallet."
            )
        }
        item {
            NextBestActionCard(
                pendingRequest = pendingRequest,
                onOpenRequest = onOpenRequest,
                onOpenAccess = onOpenInstitutions
            )
        }
        item {
            WalletAtAGlance(
                credentialCount = state.credentials.size,
                pendingRequestCount = pendingCount,
                activeAccessCount = activeAccessCount
            )
        }
        item {
            QuickActionsPanel(
                onOpenRequests = onOpenRequests,
                onOpenAccess = onOpenInstitutions,
                onShareTemporarily = onOpenDelegation
            )
        }
        item {
            HowItWorksPanel()
        }
        item { SectionTitle("Verified information") }
        items(state.credentials) { credential ->
            CredentialCard(credential)
        }
        item { SectionTitle("Needs your review") }
        item {
            if (pendingRequest != null) {
                RequestPreviewCard(request = pendingRequest, onClick = { onOpenRequest(pendingRequest.id) })
            } else {
                EmptyCard("No pending verification requests")
            }
        }
        item { SectionTitle("Who has access") }
        item {
            val activeInstitution = state.institutions.firstOrNull { it.status == PermissionStatus.Active }
            if (activeInstitution != null) {
                InstitutionMiniCard(institution = activeInstitution, onClick = onOpenInstitutions)
            } else {
                EmptyCard("No active institution connections")
            }
        }
        item {
            OutlinedButton(onClick = onOpenDelegation, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Text("Share temporarily")
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun RequestsScreen(
    requests: List<VerificationRequest>,
    onReview: (String) -> Unit,
    onDeny: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { ScreenHeader("Requests", "Choose what gets shared before you allow access") }
        item {
            TrustBanner(
                title = "Review first, share second",
                body = "A request works like a connected-app permission screen: check who is asking, what they need, what stays private, and when access ends."
            )
        }
        item {
            RequestSummaryPanel(requests)
        }
        items(requests) { request ->
            VerificationRequestCard(request = request, onReview = { onReview(request.id) }, onDeny = { onDeny(request.id) })
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun ShareCredentialScreen(
    request: VerificationRequest,
    onApprove: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().background(Mist).verticalScroll(rememberScrollState()).padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        TopBackRow(title = "Review request", onBack = onCancel)
        ConsentHeader(request)
        ReviewChecklistPanel()
        CalmPanel {
            DetailLabel("Purpose")
            Text(request.purpose, color = Ink, style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(16.dp))
            DetailLabel("Requested proof")
            Text(request.requestedProof, color = Ink, fontWeight = FontWeight.SemiBold)
        }
        DataListPanel(title = "They will receive", tone = Success, rows = request.sharedData)
        DataListPanel(title = "Kept private", tone = Slate, rows = request.hiddenData)
        WhatHappensNextPanel()
        CalmPanel {
            Text("Your control", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            BulletRow("Access expires June 18, 2027")
            BulletRow("You can revoke this access any time")
            BulletRow("Every future use appears in Activity")
        }
        Button(onClick = onApprove, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(16.dp)) {
            Text("Allow secure sharing", fontWeight = FontWeight.SemiBold)
        }
        OutlinedButton(onClick = onCancel, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp)) {
            Text("Not now")
        }
    }
}

@Composable
private fun ShareCompleteScreen(
    institution: String,
    onViewInstitutions: () -> Unit,
    onViewActivity: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().background(Mist).padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(Modifier.height(20.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            StatusCircle("✓", Success)
            Spacer(Modifier.height(24.dp))
            Text("Sharing allowed", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Ink)
            Spacer(Modifier.height(12.dp))
            Text(
                "Utah residency verification was shared with $institution. You can manage or revoke this access at any time.",
                style = MaterialTheme.typography.bodyLarge,
                color = Slate
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onViewInstitutions, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(16.dp)) {
                Text("Manage access")
            }
            OutlinedButton(onClick = onViewActivity, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp)) {
                Text("See activity")
            }
        }
    }
}

@Composable
private fun InstitutionsScreen(
    institutions: List<ConnectedInstitution>,
    onOpenInstitution: (String) -> Unit,
    onOpenDelegation: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { ScreenHeader("Access", "See and revoke who can use your verified info") }
        item {
            AccessOverviewPanel(institutions)
        }
        item {
            Button(onClick = onOpenDelegation, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(16.dp)) {
                Text("Share temporarily")
            }
        }
        items(institutions) { institution ->
            InstitutionCard(institution = institution, onClick = { onOpenInstitution(institution.id) })
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun PermissionDetailScreen(
    institution: ConnectedInstitution,
    events: List<ActivityEvent>,
    onBack: () -> Unit,
    onRevoke: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().background(Mist).verticalScroll(rememberScrollState()).padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        TopBackRow(title = "Access details", onBack = onBack)
        CalmPanel {
            Row(verticalAlignment = Alignment.CenterVertically) {
                InstitutionAvatar(institution.name)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(institution.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(institution.category, color = Slate)
                }
                PermissionChip(institution.status)
            }
            Spacer(Modifier.height(18.dp))
            DetailLabel("What they can use")
            Text(institution.accessScope, color = Ink, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))
            DetailLabel("Ends")
            Text(institution.expiration, color = Ink)
        }
        DataListPanel(title = "They can see", tone = Success, rows = institution.allowedData)
        DataListPanel(title = "Kept private", tone = Slate, rows = institution.hiddenData)
        CalmPanel {
            Text("Access controls", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            BulletRow("Revoke access immediately from this screen")
            BulletRow("Review every use in Activity")
            BulletRow("Keep sharing narrow: only the approved proof is available")
        }
        CalmPanel {
            Text("Recent activity", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))
            if (events.isEmpty()) {
                Text("No recent activity for this institution.", color = Slate)
            } else {
                events.forEach { event -> ActivityInline(event) }
            }
        }
        if (institution.status == PermissionStatus.Active) {
            Button(
                onClick = { onRevoke(institution.id) },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Danger)
            ) {
                Text("Revoke access", fontWeight = FontWeight.SemiBold)
            }
        } else {
            EmptyCard("This access is already revoked")
        }
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
    Column(
        modifier = Modifier.fillMaxSize().background(Mist).verticalScroll(rememberScrollState()).padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        TopBackRow(title = "Share temporarily", onBack = onCancel)
        CalmPanel {
            Text("Set up temporary sharing", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Choose what a person or organization can confirm, and for how long. You can revoke it from Access.", color = Slate)
        }
        TrustBanner(
            title = "Best for one-time situations",
            body = "Use temporary sharing for a landlord, employer, school office, or service counter that only needs a short-lived confirmation."
        )
        OutlinedTextField(
            value = draft.recipient,
            onValueChange = onRecipientChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Who needs access?") },
            shape = RoundedCornerShape(16.dp)
        )
        OutlinedTextField(
            value = draft.allowedAction,
            onValueChange = onActionChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("What can they confirm?") },
            shape = RoundedCornerShape(16.dp)
        )
        OutlinedTextField(
            value = draft.duration,
            onValueChange = onDurationChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("When should access end?") },
            shape = RoundedCornerShape(16.dp)
        )
        DataListPanel(
            title = "Kept private",
            tone = Slate,
            rows = listOf("Full address", "Birthdate", "State ID number", "Document image")
        )
        Button(onClick = onConfirm, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(16.dp)) {
            Text("Allow temporary sharing", fontWeight = FontWeight.SemiBold)
        }
        OutlinedButton(onClick = onCancel, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp)) {
            Text("Cancel")
        }
    }
}

@Composable
private fun ActivityScreen(events: List<ActivityEvent>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { ScreenHeader("Activity", "A timeline of requests, sharing, and revocations") }
        item {
            TrustBanner(
                title = "Your receipt trail",
                body = "Activity shows what happened in plain language, including approvals, denials, temporary sharing, and revocations."
            )
        }
        items(events) { event ->
            ActivityCard(event)
        }
        item { Spacer(Modifier.height(16.dp)) }
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
private fun TrustBanner(title: String, body: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SafetyTint),
        shape = RoundedCornerShape(22.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            StatusCircle("✓", Success.copy(alpha = 0.12f), textColor = Success)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = Ink)
                Spacer(Modifier.height(4.dp))
                Text(body, color = Slate, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun HowItWorksPanel() {
    CalmPanel {
        Text("How sharing works", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(12.dp))
        NumberedStep("1", "Review the request", "See who is asking and why before anything leaves your wallet.")
        NumberedStep("2", "Share a verified answer", "Send only the approved proof, not your full document.")
        NumberedStep("3", "Manage access anytime", "Check Activity, see connected organizations, or revoke access.")
    }
}

@Composable
private fun RequestSummaryPanel(requests: List<VerificationRequest>) {
    val pending = requests.count { it.status == RequestStatus.Pending }
    CalmPanel {
        Text("Request inbox", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            GlanceMetric(value = pending.toString(), label = "Need review", modifier = Modifier.weight(1f))
            GlanceMetric(value = requests.count { it.status == RequestStatus.Approved }.toString(), label = "Allowed", modifier = Modifier.weight(1f))
            GlanceMetric(value = requests.count { it.status == RequestStatus.Denied }.toString(), label = "Denied", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun ReviewChecklistPanel() {
    CalmPanel {
        Text("Before you allow", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        BulletRow("Confirm the organization name looks right")
        BulletRow("Check the exact proof they will receive")
        BulletRow("Review what remains private")
    }
}

@Composable
private fun WhatHappensNextPanel() {
    CalmPanel {
        Text("What happens next", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        BulletRow("The organization receives a verified answer")
        BulletRow("A connection appears under Access")
        BulletRow("This action is saved to Activity")
    }
}

@Composable
private fun AccessOverviewPanel(institutions: List<ConnectedInstitution>) {
    val active = institutions.count { it.status == PermissionStatus.Active }
    val inactive = institutions.size - active
    CalmPanel {
        Text("Connected organizations", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            GlanceMetric(value = active.toString(), label = "Active", modifier = Modifier.weight(1f))
            GlanceMetric(value = inactive.toString(), label = "Ended", modifier = Modifier.weight(1f))
            GlanceMetric(value = institutions.size.toString(), label = "Total", modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Text("Tap any organization to see what it can use, when access ends, and the revoke button.", color = Slate)
    }
}

@Composable
private fun NumberedStep(number: String, title: String, body: String) {
    Row(modifier = Modifier.padding(vertical = 7.dp), verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier.size(30.dp).clip(CircleShape).background(TrustBlue.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(number, color = TrustBlue, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, color = Ink)
            Text(body, color = Slate, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun HeroIdentityCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = StateBlue),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusCircle("UT", Color(0xFFEAF3F8))
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("State of Utah", color = Color.White.copy(alpha = 0.78f), style = MaterialTheme.typography.labelLarge)
                    Text("Verified State Identity", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(24.dp))
            Text("Wallet holder", color = Color.White.copy(alpha = 0.72f), style = MaterialTheme.typography.labelMedium)
            Text("Alex Morgan", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(18.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                HeroMeta("Status", "Verified")
                HeroMeta("Expires", "May 2030")
                HeroMeta("Control", "Revocable")
            }
        }
    }
}

@Composable
private fun NextBestActionCard(
    pendingRequest: VerificationRequest?,
    onOpenRequest: (String) -> Unit,
    onOpenAccess: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = TrustBlue.copy(alpha = 0.10f)),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text("Recommended next step", style = MaterialTheme.typography.labelLarge, color = TrustBlue, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            if (pendingRequest != null) {
                Text("Review ${pendingRequest.institutionName}'s request", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Ink)
                Spacer(Modifier.height(8.dp))
                Text("See exactly what they will receive before you allow sharing.", color = Slate)
                Spacer(Modifier.height(14.dp))
                Button(onClick = { onOpenRequest(pendingRequest.id) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                    Text("Review request")
                }
            } else {
                Text("Your wallet is up to date", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Ink)
                Spacer(Modifier.height(8.dp))
                Text("No one is waiting for approval. You can still check who has access at any time.", color = Slate)
                Spacer(Modifier.height(14.dp))
                OutlinedButton(onClick = onOpenAccess, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                    Text("Manage access")
                }
            }
        }
    }
}

@Composable
private fun WalletAtAGlance(
    credentialCount: Int,
    pendingRequestCount: Int,
    activeAccessCount: Int
) {
    CalmPanel {
        Text("At a glance", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            GlanceMetric(value = credentialCount.toString(), label = "Verified", modifier = Modifier.weight(1f))
            GlanceMetric(value = pendingRequestCount.toString(), label = "Requests", modifier = Modifier.weight(1f))
            GlanceMetric(value = activeAccessCount.toString(), label = "Have access", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun GlanceMetric(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(16.dp)).background(Mist).padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, style = MaterialTheme.typography.titleLarge, color = StateBlue, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(2.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = Slate, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun QuickActionsPanel(
    onOpenRequests: () -> Unit,
    onOpenAccess: () -> Unit,
    onShareTemporarily: () -> Unit
) {
    CalmPanel {
        Text("Common actions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(12.dp))
        QuickActionRow(title = "Review requests", body = "Approve or deny before anything is shared", onClick = onOpenRequests)
        QuickActionRow(title = "Manage access", body = "See connected organizations and revoke access", onClick = onOpenAccess)
        QuickActionRow(title = "Share temporarily", body = "Create a short-lived permission", onClick = onShareTemporarily)
    }
}

@Composable
private fun QuickActionRow(title: String, body: String, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, color = Ink)
            Text(body, color = Slate, style = MaterialTheme.typography.bodySmall)
        }
        TextButton(onClick = onClick) { Text("Open") }
    }
}

@Composable
private fun CredentialCard(credential: Credential) {
    CalmPanel {
        Row(verticalAlignment = Alignment.CenterVertically) {
            StatusCircle("✓", Success.copy(alpha = 0.12f), textColor = Success)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(credential.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(credential.subtitle, style = MaterialTheme.typography.bodyMedium, color = Slate)
            }
            Text(credential.lastVerified, style = MaterialTheme.typography.labelMedium, color = Slate)
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SoftChip(credential.verifiedLabel, Success)
            SoftChip("Expires ${credential.expires}", Slate)
        }
    }
}

@Composable
private fun RequestPreviewCard(request: VerificationRequest, onClick: () -> Unit) {
    CalmPanel {
        Row(verticalAlignment = Alignment.CenterVertically) {
            InstitutionAvatar(request.institutionName)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(request.institutionName, fontWeight = FontWeight.SemiBold)
                Text(request.title, color = Slate, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            SoftChip(request.receivedAt, TrustBlue)
        }
        Spacer(Modifier.height(14.dp))
        Button(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
            Text("Review what will be shared")
        }
    }
}

@Composable
private fun VerificationRequestCard(request: VerificationRequest, onReview: () -> Unit, onDeny: () -> Unit) {
    CalmPanel {
        Row(verticalAlignment = Alignment.CenterVertically) {
            InstitutionAvatar(request.institutionName)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(request.institutionName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(request.receivedAt, color = Slate, style = MaterialTheme.typography.labelMedium)
            }
            RequestChip(request.status)
        }
        Spacer(Modifier.height(14.dp))
        Text(request.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Text(request.purpose, color = Slate)
        Spacer(Modifier.height(12.dp))
        DetailLabel("Requested proof")
        Text(request.requestedProof, color = Ink)
        if (request.status == RequestStatus.Pending) {
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = onDeny, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp)) { Text("Don't allow") }
                Button(onClick = onReview, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp)) { Text("Review details") }
            }
        }
    }
}

@Composable
private fun ConsentHeader(request: VerificationRequest) {
    CalmPanel {
        Text("Secure sharing request", style = MaterialTheme.typography.labelLarge, color = TrustBlue, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Text("Allow ${request.requestedProof}?", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            InstitutionAvatar(request.institutionName)
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Who is asking", color = Slate, style = MaterialTheme.typography.labelMedium)
                Text(request.institutionName, fontWeight = FontWeight.SemiBold)
                Text("Only receives the approved proof", color = Slate)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InstitutionMiniCard(institution: ConnectedInstitution, onClick: () -> Unit) {
    Card(onClick = onClick, colors = CardDefaults.cardColors(containerColor = CardWhite), shape = RoundedCornerShape(22.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            InstitutionAvatar(institution.name)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(institution.name, fontWeight = FontWeight.SemiBold)
                Text(institution.accessScope, color = Slate, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            PermissionChip(institution.status)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InstitutionCard(institution: ConnectedInstitution, onClick: () -> Unit) {
    Card(onClick = onClick, colors = CardDefaults.cardColors(containerColor = CardWhite), shape = RoundedCornerShape(22.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                InstitutionAvatar(institution.name)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(institution.name, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                    Text(institution.category, color = Slate, style = MaterialTheme.typography.bodyMedium)
                }
                PermissionChip(institution.status)
            }
            Spacer(Modifier.height(14.dp))
            Text(institution.accessScope, color = Ink, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SoftChip("Last used ${institution.lastUsed}", Slate)
                SoftChip("Expires ${institution.expiration}", TrustBlue)
            }
        }
    }
}

@Composable
private fun ActivityCard(event: ActivityEvent) {
    CalmPanel {
        Row(verticalAlignment = Alignment.Top) {
            StatusCircle("•", TrustBlue.copy(alpha = 0.12f), textColor = TrustBlue)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(event.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text(event.description, color = Slate)
                Spacer(Modifier.height(8.dp))
                SoftChip(event.timestamp, Slate)
            }
        }
    }
}

@Composable
private fun ActivityInline(event: ActivityEvent) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(event.title, fontWeight = FontWeight.SemiBold)
        Text(event.description, color = Slate, style = MaterialTheme.typography.bodyMedium)
        Text(event.timestamp, color = TrustBlue, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun CalmPanel(content: @Composable () -> Unit) {
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
private fun TopBackRow(title: String, onBack: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 12.dp)) {
        TextButton(onClick = onBack) { Text("Back") }
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 6.dp))
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Ink, modifier = Modifier.padding(top = 6.dp))
}

@Composable
private fun DetailLabel(text: String) {
    Text(text.uppercase(), style = MaterialTheme.typography.labelSmall, color = Slate, fontWeight = FontWeight.Bold)
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
private fun DemoProgress(step: Int, total: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(total) { index ->
            Box(
                modifier = Modifier
                    .height(6.dp)
                    .width(if (index == step) 34.dp else 16.dp)
                    .clip(CircleShape)
                    .background(if (index <= step) TrustBlue else Color(0xFFD7DEE8))
            )
        }
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