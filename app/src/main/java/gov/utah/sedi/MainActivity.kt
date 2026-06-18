package gov.utah.sedi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.mutableStateOf
import java.util.concurrent.atomic.AtomicBoolean
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal val Ink = SediBrand.Ink
internal val Slate = SediBrand.Slate
internal val Mist = SediBrand.SurfaceWarm
internal val CardWhite = SediBrand.SurfaceElevated
internal val StateBlue = SediBrand.Navy
internal val TrustBlue = SediBrand.Teal
private val Success = SediBrand.Success
private val Warning = Color(0xFF9A5B13)
private val Danger = Color(0xFFB42318)

private const val ONBOARDING_STEPS = 15

private object Route {
    const val UvuInvitation = "uvuInvitation"
    const val CreateWallet = "createWallet"
    const val SecureIdentitySetup = "secureIdentitySetup"
    const val DeviceTrustConfirmation = "deviceTrustConfirmation"
    const val PresenceIntro = "presenceIntro"
    const val CameraPermission = "cameraPermission"
    const val FaceAlignment = "faceAlignment"
    const val PresenceDetected = "presenceDetected"
    const val LivenessCheck = "livenessCheck"
    const val SecureConfirmation = "secureConfirmation"
    const val IdentitySessionReady = "identitySessionReady"
    const val ResidencyProofSelection = "residencyProofSelection"
    const val PrivacyReview = "privacyReview"
    const val SecureWalletActivation = "secureWalletActivation"
    const val WalletReady = "walletReady"
    const val OnboardingCancelled = "onboardingCancelled"
    const val WalletLearnMore = "walletLearnMore"
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
    const val ShareVerificationProcessing = "shareVerificationProcessing"
    const val VerificationSuccess = "verificationSuccess"
    const val PermissionDetail = "permission/{institutionId}"
    const val RevokeConfirmation = "revoke/{institutionId}"
    const val RevokeSuccess = "revokeSuccess/{institutionId}"
    const val Delegation = "delegation"
}

private sealed class BottomDestination(val route: String, val label: String, val tab: TabDestination) {
    data object Wallet : BottomDestination(Route.Wallet, "Wallet", mainTabDestinations[0])
    data object Requests : BottomDestination(Route.Requests, "Requests", mainTabDestinations[1])
    data object Institutions : BottomDestination(Route.Institutions, "Institutions", mainTabDestinations[2])
    data object Activity : BottomDestination(Route.Activity, "Activity", mainTabDestinations[3])
}

private val bottomDestinations = listOf(
    BottomDestination.Wallet,
    BottomDestination.Requests,
    BottomDestination.Institutions,
    BottomDestination.Activity
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val keepSplash = AtomicBoolean(true)
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { keepSplash.get() }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SediTheme {
                val walletViewModel: IdentityWalletViewModel = viewModel()
                IdentityWalletApp(
                    viewModel = walletViewModel,
                    onSplashDismissed = { keepSplash.set(false) }
                )
            }
        }
    }
}

@Composable
private fun SediTheme(content: @Composable () -> Unit) {
    val sediTypography = Typography(
        headlineMedium = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            lineHeight = 34.sp,
            letterSpacing = (-0.25).sp
        ),
        headlineSmall = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            lineHeight = 30.sp
        ),
        titleLarge = TextStyle(
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            lineHeight = 28.sp
        ),
        titleMedium = TextStyle(
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 24.sp
        ),
        bodyLarge = TextStyle(
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp
        ),
        bodyMedium = TextStyle(
            fontSize = 14.sp,
            lineHeight = 21.sp,
            letterSpacing = 0.1.sp
        ),
        labelLarge = TextStyle(
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    )
    MaterialTheme(
        typography = sediTypography,
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
private fun IdentityWalletApp(
    viewModel: IdentityWalletViewModel,
    onSplashDismissed: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showSplash by remember { mutableStateOf(true) }
    val contentAlpha = remember { Animatable(if (showSplash) 0f else 1f) }
    val contentOffsetY = remember { Animatable(if (showSplash) 36f else 0f) }
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val showBottomBar = !showSplash && state.onboardingComplete && currentRoute in bottomDestinations.map { it.route }

    LaunchedEffect(showSplash) {
        if (!showSplash) onSplashDismissed()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Mist,
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar(
                        containerColor = CardWhite,
                        tonalElevation = 3.dp
                    ) {
                        bottomDestinations.forEach { destination ->
                            val selected = currentRoute == destination.route
                            NavigationBarItem(
                                selected = selected,
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
                                        selected = selected,
                                        pendingCount = state.requests.count { it.status == RequestStatus.Pending }
                                    )
                                },
                                label = { Text(destination.label) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = StateBlue,
                                    selectedTextColor = StateBlue,
                                    indicatorColor = StateBlue.copy(alpha = 0.12f)
                                )
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = if (state.onboardingComplete) Route.Wallet else Route.UvuInvitation,
                modifier = Modifier
                    .padding(innerPadding)
                    .graphicsLayer {
                        alpha = contentAlpha.value
                        translationY = contentOffsetY.value
                    }
            ) {
            composable(Route.UvuInvitation) {
                UvuInvitationScreen(
                    onBeginVerification = { navController.navigate(Route.CreateWallet) },
                    onCancel = { navController.navigate(Route.OnboardingCancelled) }
                )
            }
            composable(Route.OnboardingCancelled) {
                OnboardingCancelledScreen(onReturn = { navController.navigate(Route.UvuInvitation) })
            }
            composable(Route.CreateWallet) {
                CreateWalletScreen(
                    walletCreated = state.walletCreated,
                    onCreateWallet = {
                        viewModel.createWallet()
                    },
                    onContinue = { navController.navigate(Route.SecureIdentitySetup) },
                    onLearnMore = { navController.navigate(Route.WalletLearnMore) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Route.WalletLearnMore) {
                WalletLearnMoreScreen(onBack = { navController.popBackStack() })
            }
            composable(Route.SecureIdentitySetup) {
                SecureIdentitySetupScreen(
                    onStartSetup = {
                        viewModel.startIdentityVerification()
                        navController.navigate(Route.DeviceTrustConfirmation)
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Route.DeviceTrustConfirmation) {
                DeviceTrustConfirmationScreen(
                    onContinue = { navController.navigate(Route.PresenceIntro) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Route.PresenceIntro) {
                PresenceIntroScreen(
                    step = 5,
                    onContinue = { navController.navigate(Route.CameraPermission) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Route.CameraPermission) {
                CameraPermissionScreen(
                    step = 6,
                    onPermissionGranted = { navController.navigate(Route.FaceAlignment) },
                    onSkip = { navController.navigate(Route.FaceAlignment) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Route.FaceAlignment) {
                FaceAlignmentScreen(
                    step = 7,
                    cameraEnabled = true,
                    onAligned = { navController.navigate(Route.PresenceDetected) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Route.PresenceDetected) {
                PresenceDetectedScreen(
                    step = 8,
                    onContinue = { navController.navigate(Route.LivenessCheck) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Route.LivenessCheck) {
                LivenessCheckScreen(
                    step = 9,
                    onComplete = { navController.navigate(Route.SecureConfirmation) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Route.SecureConfirmation) {
                SecureConfirmationScreen(
                    step = 10,
                    onContinue = { navController.navigate(Route.IdentitySessionReady) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Route.IdentitySessionReady) {
                IdentitySessionReadyScreen(
                    step = 11,
                    onContinue = { navController.navigate(Route.ResidencyProofSelection) }
                )
            }
            composable(Route.ResidencyProofSelection) {
                ResidencyProofSelectionScreen(
                    onContinue = { navController.navigate(Route.PrivacyReview) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Route.PrivacyReview) {
                PrivacyReviewScreen(
                    onActivateWallet = { navController.navigate(Route.SecureWalletActivation) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Route.SecureWalletActivation) {
                SecureWalletActivationScreen(
                    onFinished = {
                        viewModel.completeIdentityVerification()
                        navController.navigate(Route.WalletReady) {
                            popUpTo(Route.UvuInvitation) { inclusive = false }
                        }
                    }
                )
            }
            composable(Route.WalletReady) {
                WalletReadyScreen(
                    onReviewUvuRequest = { navController.navigate("request/uvu-residency") }
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
                        if (state.onboardingComplete) {
                            navController.navigate(Route.Requests) { popUpTo(Route.Requests) { inclusive = true } }
                        } else {
                            viewModel.finishOnboarding()
                            navController.navigate(Route.Wallet) {
                                popUpTo(Route.UvuInvitation) { inclusive = true }
                            }
                        }
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
                        navController.navigate(Route.ShareVerificationProcessing)
                    },
                    onCancel = {
                        viewModel.denyRequest(request.id)
                        if (state.onboardingComplete) {
                            navController.navigate(Route.Requests) { popUpTo(Route.Requests) { inclusive = true } }
                        } else {
                            viewModel.finishOnboarding()
                            navController.navigate(Route.Wallet) {
                                popUpTo(Route.UvuInvitation) { inclusive = true }
                            }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Route.ShareVerificationProcessing) {
                ShareVerificationProcessingScreen(
                    onFinished = {
                        navController.navigate(Route.VerificationSuccess) {
                            popUpTo(Route.ShareVerificationProcessing) { inclusive = true }
                        }
                    }
                )
            }
            composable(Route.VerificationSuccess) {
                VerificationSuccessScreen(
                    onViewUvuAccess = {
                        viewModel.finishOnboarding()
                        navController.navigate("permission/uvu")
                    },
                    onBackToWallet = {
                        viewModel.finishOnboarding()
                        navController.navigate(Route.Wallet) {
                            popUpTo(Route.UvuInvitation) { inclusive = true }
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
        AnimatedVisibility(
            visible = showSplash,
            enter = fadeIn(),
            exit = fadeOut(tween(400))
        ) {
            CinematicSplashScreen(
                onHandoff = {
                    scope.launch {
                        launch {
                            contentAlpha.animateTo(1f, tween(680, easing = FastOutSlowInEasing))
                        }
                        launch {
                            contentOffsetY.animateTo(0f, tween(720, easing = FastOutSlowInEasing))
                        }
                    }
                },
                onFinished = { showSplash = false }
            )
        }
    }
}

@Composable
private fun NavIconWithBadge(
    destination: BottomDestination,
    selected: Boolean,
    pendingCount: Int
) {
    val icon = if (selected) destination.tab.selectedIcon else destination.tab.unselectedIcon
    if (destination == BottomDestination.Requests && pendingCount > 0) {
        BadgedBox(badge = { Badge { Text(pendingCount.toString()) } }) {
            Icon(icon, contentDescription = destination.label)
        }
    } else {
        Icon(icon, contentDescription = destination.label)
    }
}

@Composable
private fun UvuInvitationScreen(onBeginVerification: () -> Unit, onCancel: () -> Unit) {
    VerificationFlowScreen(
        step = 1,
        title = "UVU requests residency proof",
        subtitle = "Review and approve what Utah Valley University receives.",
        reassurance = SediBrand.Copy.APPROVED_ONLY,
        primaryLabel = "Begin",
        onPrimary = onBeginVerification,
        secondaryLabel = "Not Now",
        onSecondary = onCancel
    ) {
        InvitationHeroCard(
            institutionName = "Utah Valley University",
            headline = "Utah residency proof",
            detail = "Enrollment eligibility · Expires in 7 days"
        )
    }
}

@Composable
private fun OnboardingCancelledScreen(onReturn: () -> Unit) {
    SediScreenBackdrop {
        Box(modifier = Modifier.fillMaxSize().padding(SediVerticalRhythm.screenHorizontal)) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(bottom = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Verification paused",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Ink,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Return when you're ready to finish UVU's residency request.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(10.dp))
                ReassuranceLine(SediBrand.Copy.IN_CONTROL)
            }
            Button(
                onClick = onReturn,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Return to Request", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun CreateWalletScreen(
    walletCreated: Boolean,
    onCreateWallet: () -> Unit,
    onContinue: () -> Unit,
    onLearnMore: () -> Unit,
    onBack: () -> Unit
) {
    var showCreated by remember(walletCreated) { mutableStateOf(walletCreated) }
    VerificationFlowScreen(
        step = 2,
        title = "Create your identity wallet",
        subtitle = "Use it to approve requests and manage access.",
        reassurance = SediBrand.Copy.IN_CONTROL,
        primaryLabel = if (showCreated) "Continue" else "Create Wallet",
        onPrimary = {
            if (showCreated) {
                onContinue()
            } else {
                onCreateWallet()
                showCreated = true
            }
        },
        secondaryLabel = if (showCreated) null else "Learn More",
        onSecondary = if (showCreated) null else onLearnMore,
        showBack = true,
        onBack = onBack,
        primaryEnabled = true
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            SediLogoMark(
                size = 64.dp,
                variant = SediLogoVariant.OnLight,
                animated = showCreated
            )
        }
        if (showCreated) {
            Spacer(Modifier.height(12.dp))
            VerificationStatusBanner(
                label = "Wallet created",
                detail = "Ready for setup.",
                complete = true
            )
        }
    }
}

@Composable
private fun SecureIdentitySetupScreen(onStartSetup: () -> Unit, onBack: () -> Unit) {
    VerificationFlowScreen(
        step = 3,
        title = "Secure identity",
        subtitle = "Confirm your device and presence first.",
        primaryLabel = "Start Setup",
        onPrimary = onStartSetup,
        showBack = true,
        onBack = onBack
    ) {
        CalmPanel {
            VerificationChecklistRow("Confirm device", pending = true)
            VerificationChecklistRow("Verify presence", pending = true)
            VerificationChecklistRow("Enable sharing", pending = true)
        }
    }
}

@Composable
private fun DeviceTrustConfirmationScreen(onContinue: () -> Unit, onBack: () -> Unit) {
    var stage by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        delay(800)
        stage = 1
        delay(900)
        stage = 2
        delay(900)
        stage = 3
        delay(800)
        stage = 4
    }
    VerificationFlowScreen(
        step = 4,
        title = "Trust this device",
        subtitle = "It will approve requests on your behalf.",
        primaryLabel = "Continue",
        onPrimary = onContinue,
        showBack = true,
        onBack = onBack,
        primaryEnabled = stage >= 4
    ) {
        DeviceTrustHandshake(active = stage in 1..3, complete = stage >= 4)
        if (stage >= 4) {
            Spacer(Modifier.height(10.dp))
            VerificationStatusBanner(
                label = "Device trusted",
                detail = "Ready to approve.",
                complete = true
            )
        }
    }
}

@Composable
private fun ResidencyProofSelectionScreen(onContinue: () -> Unit, onBack: () -> Unit) {
    var preparing by remember { mutableStateOf(true) }
    var ready by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(1400)
        preparing = false
        ready = true
    }
    VerificationFlowScreen(
        step = 12,
        title = "Residency proof",
        subtitle = "Prepared for UVU when you approve.",
        reassurance = SediBrand.Copy.APPROVED_ONLY,
        primaryLabel = if (ready) "Continue" else "Preparing…",
        onPrimary = onContinue,
        showBack = true,
        onBack = onBack,
        primaryEnabled = ready
    ) {
        if (preparing) {
            CalmPanel {
                Text("Preparing proof…", color = TrustBlue, style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            VerificationStatusBanner(
                label = "Ready for UVU",
                detail = "Residency proof prepared.",
                complete = true
            )
        }
    }
}

@Composable
private fun PrivacyReviewScreen(onActivateWallet: () -> Unit, onBack: () -> Unit) {
    VerificationFlowScreen(
        step = 13,
        title = "Review your privacy",
        subtitle = "UVU receives residency verification only.",
        reassurance = SediBrand.Copy.PRIVATE_DETAILS,
        primaryLabel = "Continue",
        onPrimary = onActivateWallet,
        showBack = true,
        onBack = onBack
    ) {
        PrivacySplitPanel(
            shared = listOf("Residency verified"),
            hidden = listOf("Address", "Birthdate", "ID number")
        )
    }
}

@Composable
private fun SecureWalletActivationScreen(onFinished: () -> Unit) {
    OperationalProcessingScreen(
        title = "Activating wallet",
        subtitle = "Just a moment",
        steps = listOf("Securing wallet", "Enabling approvals"),
        step = 14,
        onFinished = onFinished,
        stepDelayMs = 850
    )
}

@Composable
private fun WalletReadyScreen(onReviewUvuRequest: () -> Unit) {
    VerificationFlowScreen(
        step = 15,
        title = "Your wallet is ready",
        subtitle = "Review UVU's request next.",
        reassurance = SediBrand.Copy.REVIEW_BEFORE,
        primaryLabel = "Review Request",
        onPrimary = onReviewUvuRequest
    ) {
        VerificationStatusBanner(
            label = "All set",
            detail = "Proof sharing and access controls are on.",
            complete = true
        )
    }
}

@Composable
private fun WalletLearnMoreScreen(onBack: () -> Unit) {
    DetailScreen(
        title = "About your wallet",
        onBack = onBack,
        subtitle = "A secure place to approve identity sharing.",
        reassurance = SediBrand.Copy.IN_CONTROL
    ) {
        PrivacySplitPanel(
            shared = listOf("Proofs you approve", "Institution access you allow"),
            hidden = listOf("Full address", "Birthdate", "ID number")
        )
    }
}

@Composable
private fun VerificationFlowScreen(
    step: Int,
    title: String,
    subtitle: String,
    primaryLabel: String,
    onPrimary: () -> Unit,
    secondaryLabel: String? = null,
    onSecondary: (() -> Unit)? = null,
    showBack: Boolean = false,
    onBack: (() -> Unit)? = null,
    primaryEnabled: Boolean = true,
    nextStepHint: String? = null,
    reassurance: String? = null,
    focusCentered: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    BalancedFlowScaffold(
        step = step,
        title = title,
        subtitle = subtitle,
        primaryLabel = primaryLabel,
        onPrimary = onPrimary,
        secondaryLabel = secondaryLabel,
        onSecondary = onSecondary,
        showBack = showBack,
        onBack = onBack,
        primaryEnabled = primaryEnabled,
        nextStepHint = nextStepHint,
        reassurance = reassurance,
        focusCentered = focusCentered,
        content = content
    )
}

@Composable
internal fun OnboardingProgress(step: Int, total: Int = ONBOARDING_STEPS) {
    CompactOnboardingHeader(step = step, total = total, showPhaseStrip = true)
}

@Composable
private fun VerificationChecklistRow(label: String, pending: Boolean) {
    Row(modifier = Modifier.padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
        StatusCircle(
            text = if (pending) "○" else "✓",
            color = if (pending) TrustBlue.copy(alpha = 0.12f) else Success.copy(alpha = 0.12f),
            textColor = if (pending) TrustBlue else Success,
            size = 36.dp
        )
        Spacer(Modifier.width(10.dp))
        Text(label, color = Ink, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun VerificationStageRow(label: String, complete: Boolean, inProgress: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatusCircle(
            text = when {
                complete -> "✓"
                inProgress -> "…"
                else -> "○"
            },
            color = when {
                complete -> Success.copy(alpha = 0.12f)
                inProgress -> TrustBlue.copy(alpha = 0.12f)
                else -> Color(0xFFE8EDF2)
            },
            textColor = when {
                complete -> Success
                inProgress -> TrustBlue
                else -> Slate
            },
            size = 36.dp
        )
        Spacer(Modifier.width(12.dp))
        Text(
            label,
            color = if (complete) Ink else Slate,
            fontWeight = if (complete) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun VerificationStatusBanner(label: String, detail: String, complete: Boolean) {
    CalmPanel {
        Row(verticalAlignment = Alignment.CenterVertically) {
            StatusCircle(
                text = if (complete) "✓" else "…",
                color = if (complete) Success.copy(alpha = 0.12f) else TrustBlue.copy(alpha = 0.12f),
                textColor = if (complete) Success else TrustBlue,
                size = 36.dp
            )
            Spacer(Modifier.width(10.dp))
            Column {
                Text(label, fontWeight = FontWeight.SemiBold, color = Ink, style = MaterialTheme.typography.bodyMedium)
                Text(detail, color = Slate, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun DeviceTrustHandshake(active: Boolean, complete: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "deviceTrust")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Reverse),
        label = "trustPulse"
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFF0F7FC), Color(0xFFE3EFF8))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        if (active) {
            Box(
                modifier = Modifier
                    .size((120 * pulse).dp)
                    .border(2.dp, TrustBlue.copy(alpha = 0.25f), CircleShape)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            StatusCircle("▣", TrustBlue.copy(alpha = 0.15f), textColor = TrustBlue)
            if (active) {
                Text("···", color = TrustBlue.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
            } else if (complete) {
                Text("✓", color = Success, fontWeight = FontWeight.Bold)
            } else {
                Text("—", color = Slate)
            }
            StatusCircle(if (complete) "✓" else "◆", if (complete) Success.copy(alpha = 0.15f) else TrustBlue.copy(alpha = 0.12f), textColor = if (complete) Success else TrustBlue)
        }
    }
}

@Composable
private fun ProofSelectionCard(
    title: String,
    detail: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val clickableModifier = if (enabled) Modifier.clickable(onClick = onClick) else Modifier
    Box(modifier = Modifier.fillMaxWidth().then(clickableModifier)) {
        CalmPanel {
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusCircle(
                    text = if (selected) "✓" else "○",
                    color = if (selected) TrustBlue.copy(alpha = 0.12f) else Color(0xFFE8EDF2),
                    textColor = if (selected) TrustBlue else Slate
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, fontWeight = FontWeight.SemiBold, color = Ink)
                    Text(detail, color = Slate, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun OperationalProcessingScreen(
    title: String,
    steps: List<String>,
    onFinished: () -> Unit,
    subtitle: String? = null,
    step: Int? = null,
    stepDelayMs: Long = 850
) {
    var visibleSteps by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        steps.indices.forEach { index ->
            delay(stepDelayMs)
            visibleSteps = index + 1
        }
        delay(700)
        onFinished()
    }
    SediScreenBackdrop {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(SediVerticalRhythm.screenHorizontal),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (step != null) {
                CompactOnboardingHeader(step = step, showPhaseStrip = true)
                Spacer(Modifier.height(16.dp))
            }
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Ink,
                textAlign = TextAlign.Center
            )
            if (subtitle != null) {
                Spacer(Modifier.height(4.dp))
                Text(subtitle, color = Slate, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
            }
            Spacer(Modifier.height(20.dp))
            CalmPanel {
                steps.forEachIndexed { index, stepLabel ->
                    val complete = index < visibleSteps
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusCircle(
                            text = if (complete) "✓" else "…",
                            color = if (complete) Success.copy(alpha = 0.12f) else TrustBlue.copy(alpha = 0.12f),
                            textColor = if (complete) Success else TrustBlue,
                            size = 36.dp
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            stepLabel,
                            color = if (complete) Ink else Slate,
                            fontWeight = if (complete) FontWeight.SemiBold else FontWeight.Normal,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
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
    MainTabScaffold(
        title = "Wallet",
        subtitle = if (state.walletActive) "Protected and ready" else "Setting up"
    ) {
        if (state.walletActive) {
            StateIdentityVerifiedCard(onViewIdentity = onViewIdentity)
        }
        if (pendingRequest != null) {
            UvuRequestPreviewCard(
                request = pendingRequest,
                onReviewRequest = { onOpenRequest(pendingRequest.id) }
            )
        }
        WalletQuickAction(label = "Share a proof", onClick = onShareProof)
        WalletQuickAction(label = "Manage access", onClick = onManageAccess)
        WalletQuickAction(label = "View activity", onClick = onViewActivity)
        ReassuranceLine(SediBrand.Copy.IN_CONTROL)
    }
}

@Composable
private fun StateIdentityVerifiedCard(onViewIdentity: () -> Unit) {
    CalmPanel {
        Row(verticalAlignment = Alignment.CenterVertically) {
            StatusCircle("✓", Success.copy(alpha = 0.12f), textColor = Success, size = 40.dp)
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("State identity verified", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text("Presence confirmed", color = Slate, style = MaterialTheme.typography.bodySmall)
            }
        }
        Spacer(Modifier.height(10.dp))
        OutlinedButton(onClick = onViewIdentity, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
            Text("View details")
        }
    }
}

@Composable
private fun UvuRequestPreviewCard(request: VerificationRequest, onReviewRequest: () -> Unit) {
    CalmPanel {
        Row(verticalAlignment = Alignment.CenterVertically) {
            InstitutionAvatar(request.institutionName)
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("UVU residency request", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleSmall)
                Text("Expires ${request.expires.lowercase()}", color = Slate, style = MaterialTheme.typography.bodySmall)
            }
            RequestChip(request.status)
        }
        Spacer(Modifier.height(10.dp))
        Button(onClick = onReviewRequest, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
            Text("Review request", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun WalletQuickAction(label: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(46.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Text(label, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun ShareProofTypeScreen(
    onChooseResidency: () -> Unit,
    onChooseAge: () -> Unit,
    onChooseLicense: () -> Unit,
    onBack: () -> Unit
) {
    SediScreenBackdrop {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                horizontal = SediVerticalRhythm.screenHorizontal,
                vertical = SediVerticalRhythm.tabTopBreathing
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
        item { TopBackRow(title = "Share a proof", onBack = onBack) }
        item {
            Text(
                "Choose what to share when an institution asks.",
                style = MaterialTheme.typography.bodyMedium,
                color = Slate
            )
        }
        item {
            ProofTypeOptionCard(
                title = "Residency",
                description = "Utah residency without your full address.",
                onClick = onChooseResidency
            )
        }
        item {
            ProofTypeOptionCard(
                title = "Age",
                description = "Age eligibility without your birthdate.",
                onClick = onChooseAge
            )
        }
        item {
            ProofTypeOptionCard(
                title = "License",
                description = "License status without unrelated details.",
                onClick = onChooseLicense
            )
        }
        item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun ProofTypeOptionCard(title: String, description: String, onClick: () -> Unit) {
    CalmPanel {
        Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(4.dp))
        Text(description, color = Slate, style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(10.dp))
        Button(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
            Text("Share $title", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun StateIdentityDetailScreen(credential: Credential, onBack: () -> Unit) {
    DetailScreen(
        title = "Verified identity",
        onBack = onBack,
        subtitle = "Your state identity on this device.",
        reassurance = SediBrand.Copy.IN_CONTROL
    ) {
        CalmPanel {
            InfoRow("Status", "Verified")
            InfoRow("Verified on", credential.lastVerified)
            InfoRow("Wallet", "Active")
        }
    }
}

@Composable
private fun ResidencyProofDetailScreen(onShareProof: () -> Unit, onBack: () -> Unit) {
    DetailScreen(
        title = "Residency proof",
        onBack = onBack,
        subtitle = "Prove Utah residency without sharing your address.",
        reassurance = SediBrand.Copy.PRIVATE_DETAILS
    ) {
        Button(onClick = onShareProof, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(14.dp)) {
            Text("Share residency proof", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun AgeProofDetailScreen(onShareProof: () -> Unit, onBack: () -> Unit) {
    DetailScreen(
        title = "Age proof",
        onBack = onBack,
        subtitle = "Prove age eligibility without sharing your birthdate.",
        reassurance = SediBrand.Copy.PRIVATE_DETAILS
    ) {
        Button(onClick = onShareProof, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(14.dp)) {
            Text("Share age proof", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun LicenseProofDetailScreen(onShareProof: () -> Unit, onBack: () -> Unit) {
    DetailScreen(
        title = "License proof",
        onBack = onBack,
        subtitle = "Prove active license status only.",
        reassurance = SediBrand.Copy.PRIVATE_DETAILS
    ) {
        Button(onClick = onShareProof, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(14.dp)) {
            Text("Share license proof", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun ShareResidencyReviewScreen(onApprove: () -> Unit, onCancel: () -> Unit) {
    DetailScreen(
        title = "Share residency proof?",
        onBack = onCancel,
        subtitle = "Review what will be shared.",
        reassurance = SediBrand.Copy.APPROVED_ONLY
    ) {
        PrivacySplitPanel(
            shared = listOf("Utah residency verified"),
            hidden = listOf("Full address", "Birthdate", "ID number")
        )
        DetailActions(primaryLabel = "Approve share", onPrimary = onApprove, secondaryLabel = "Cancel", onSecondary = onCancel)
    }
}

@Composable
private fun ShareAgeReviewScreen(onApprove: () -> Unit, onCancel: () -> Unit) {
    DetailScreen(
        title = "Share age proof?",
        onBack = onCancel,
        subtitle = "Only the age threshold is shared.",
        reassurance = SediBrand.Copy.PRIVATE_DETAILS
    ) {
        PrivacySplitPanel(
            shared = listOf("21+ verified"),
            hidden = listOf("Birthdate", "ID number")
        )
        DetailActions(primaryLabel = "Approve share", onPrimary = onApprove, secondaryLabel = "Cancel", onSecondary = onCancel)
    }
}

@Composable
private fun ShareLicenseReviewScreen(onApprove: () -> Unit, onCancel: () -> Unit) {
    DetailScreen(
        title = "Share license proof?",
        onBack = onCancel,
        subtitle = "Only license status is shared.",
        reassurance = SediBrand.Copy.PRIVATE_DETAILS
    ) {
        PrivacySplitPanel(
            shared = listOf("License active"),
            hidden = listOf("Unrelated credentials", "Personal ID details")
        )
        DetailActions(primaryLabel = "Approve share", onPrimary = onApprove, secondaryLabel = "Cancel", onSecondary = onCancel)
    }
}

@Composable
private fun CredentialShareSuccessScreen(
    shareResult: ShareResult,
    onViewActivity: () -> Unit,
    onBackToWallet: () -> Unit,
    onViewInstitution: (() -> Unit)?
) {
    ResultScreen(
        title = "Proof shared",
        body = "${shareResult.credentialTitle} was shared with ${shareResult.recipient}.",
        reassurance = SediBrand.Copy.PRIVATE_DETAILS,
        primaryLabel = "View activity",
        onPrimary = onViewActivity,
        secondaryLabel = "Back to wallet",
        onSecondary = onBackToWallet,
        extraActionLabel = if (onViewInstitution != null) "View access" else null,
        onExtraAction = onViewInstitution
    )
}

@Composable
private fun CredentialShareHistoryScreen(
    credentialTitle: String,
    history: List<CredentialShareRecord>,
    onBack: () -> Unit
) {
    DetailScreen(
        title = "Share history",
        onBack = onBack,
        subtitle = credentialTitle
    ) {
        if (history.isEmpty()) {
            EmptyCard("No shares yet")
        } else {
            history.forEach { record ->
                CalmPanel {
                    InfoRow("Shared with", record.recipient)
                    InfoRow("Date", record.timestamp)
                    InfoRow("Result", record.result)
                }
            }
        }
    }
}

@Composable
private fun RequestsListScreen(requests: List<VerificationRequest>, onOpenRequest: (String) -> Unit) {
    MainTabScaffold(title = "Requests", subtitle = "Review before sharing") {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            color = CardWhite.copy(alpha = 0.94f),
            tonalElevation = 1.dp,
            shadowElevation = 3.dp
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                if (requests.isEmpty()) {
                    EmptyStateCard(
                        title = "No requests",
                        body = "Institution requests will appear here."
                    )
                } else {
                    requests.forEach { request ->
                        RequestCard(request = request, onOpenRequest = { onOpenRequest(request.id) })
                    }
                }
            }
        }
        ReassuranceLine(SediBrand.Copy.REVIEW_BEFORE)
    }
}

@Composable
private fun EmptyStateCard(title: String, body: String) {
    CalmPanel {
        Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(4.dp))
        Text(body, color = Slate, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun RequestDetailScreen(
    request: VerificationRequest,
    onReviewSharedData: () -> Unit,
    onDeny: () -> Unit,
    onBack: () -> Unit
) {
    DetailScreen(
        title = "UVU request",
        onBack = onBack,
        subtitle = "Residency proof for enrollment eligibility.",
        reassurance = SediBrand.Copy.REVIEW_BEFORE
    ) {
        CalmPanel {
            Row(verticalAlignment = Alignment.CenterVertically) {
                InstitutionAvatar(request.institutionName)
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(request.institutionName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text("Residency verification", color = Slate, style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(Modifier.height(10.dp))
            InfoRow("Expires", request.expires)
            InfoRow("Purpose", request.purpose)
        }
        DetailActions(
            primaryLabel = "Review sharing",
            onPrimary = onReviewSharedData,
            secondaryLabel = "Deny",
            onSecondary = onDeny
        )
    }
}

@Composable
private fun SharedDataPreviewScreen(request: VerificationRequest, onContinueToApproval: () -> Unit, onBack: () -> Unit) {
    DetailScreen(
        title = "What's shared",
        onBack = onBack,
        subtitle = "Residency proof for Utah Valley University.",
        reassurance = SediBrand.Copy.PRIVATE_DETAILS
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            PrivacySplitPanel(
                shared = request.sharedData,
                hidden = request.hiddenData
            )
            DetailActions(primaryLabel = "Continue", onPrimary = onContinueToApproval)
        }
    }
}

@Composable
private fun ApprovalConsentScreen(request: VerificationRequest, onApprove: () -> Unit, onCancel: () -> Unit, onBack: () -> Unit) {
    DetailScreen(
        title = "Approve residency proof?",
        onBack = onBack,
        subtitle = "UVU will receive verified residency for enrollment eligibility.",
        reassurance = SediBrand.Copy.REVOKE_ANYTIME
    ) {
        Spacer(Modifier.height(8.dp))
        DetailActions(
            primaryLabel = "Approve share",
            onPrimary = onApprove,
            secondaryLabel = "Cancel",
            onSecondary = onCancel
        )
    }
}

@Composable
private fun ShareVerificationProcessingScreen(onFinished: () -> Unit) {
    OperationalProcessingScreen(
        title = "Sharing proof",
        subtitle = "Just a moment",
        steps = listOf("Preparing proof", "Sending to UVU"),
        onFinished = onFinished,
        stepDelayMs = 700
    )
}

@Composable
private fun VerificationSuccessScreen(onViewUvuAccess: () -> Unit, onBackToWallet: () -> Unit) {
    ResultScreen(
        title = "Proof shared",
        body = "UVU received residency verification. Your address stayed private.",
        reassurance = SediBrand.Copy.REVOKE_ANYTIME,
        primaryLabel = "View access",
        onPrimary = onViewUvuAccess,
        secondaryLabel = "Go to wallet",
        onSecondary = onBackToWallet
    )
}

@Composable
private fun InstitutionsListScreen(institutions: List<ConnectedInstitution>, onOpenInstitution: (String) -> Unit) {
    MainTabScaffold(title = "Institutions", subtitle = "Manage connected access") {
        institutions.forEach { institution ->
            InstitutionCard(institution = institution, onOpenInstitution = { onOpenInstitution(institution.id) })
        }
        ReassuranceLine(SediBrand.Copy.REVOKE_ANYTIME)
    }
}

@Composable
private fun PermissionDetailScreen(
    institution: ConnectedInstitution,
    onRevokeAccess: () -> Unit,
    onViewActivity: () -> Unit,
    onBack: () -> Unit
) {
    DetailScreen(
        title = institution.name,
        onBack = onBack,
        subtitle = if (institution.status == PermissionStatus.Active) institution.accessScope else "No active access",
        reassurance = if (institution.status == PermissionStatus.Active) SediBrand.Copy.REVOKE_ANYTIME else null
    ) {
        if (institution.status == PermissionStatus.Active) {
            PrivacySplitPanel(
                shared = institution.allowedData,
                hidden = institution.hiddenData
            )
            CalmPanel {
                InfoRow("Expires", institution.expiration)
                InfoRow("Last used", institution.lastUsed)
            }
            Button(
                onClick = onRevokeAccess,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Danger)
            ) { Text("Remove access", fontWeight = FontWeight.SemiBold) }
        } else {
            EmptyCard("No active access")
        }
        OutlinedButton(onClick = onViewActivity, modifier = Modifier.fillMaxWidth().height(46.dp), shape = RoundedCornerShape(14.dp)) {
            Text("View activity")
        }
    }
}

@Composable
private fun RevokeConfirmationScreen(institution: ConnectedInstitution, onConfirmRevoke: () -> Unit, onCancel: () -> Unit) {
    DetailScreen(
        title = "Remove UVU access?",
        onBack = onCancel,
        subtitle = "UVU will no longer verify your residency through this permission.",
        reassurance = SediBrand.Copy.IN_CONTROL
    ) {
        DetailActions(
            primaryLabel = "Remove access",
            onPrimary = onConfirmRevoke,
            secondaryLabel = "Cancel",
            onSecondary = onCancel,
            primaryColors = ButtonDefaults.buttonColors(containerColor = Danger)
        )
    }
}

@Composable
private fun RevokeSuccessScreen(institutionName: String, onViewActivity: () -> Unit, onBackToInstitutions: () -> Unit) {
    ResultScreen(
        title = "Access removed",
        body = "$institutionName no longer has active access.",
        reassurance = SediBrand.Copy.IN_CONTROL,
        primaryLabel = "View activity",
        onPrimary = onViewActivity,
        secondaryLabel = "Back to institutions",
        onSecondary = onBackToInstitutions
    )
}

@Composable
private fun ActivityScreen(events: List<ActivityEvent>) {
    MainTabScaffold(title = "Activity", subtitle = "What you've shared and when") {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            color = CardWhite.copy(alpha = 0.92f),
            tonalElevation = 1.dp,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 18.dp)) {
                events.forEachIndexed { index, event ->
                    ActivityTimelineCard(event = event, showConnector = index < events.lastIndex)
                }
            }
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
    DetailScreen(
        title = "Delegation",
        onBack = onCancel,
        subtitle = "Optional temporary sharing.",
        reassurance = SediBrand.Copy.IN_CONTROL
    ) {
        OutlinedTextField(value = draft.recipient, onValueChange = onRecipientChanged, modifier = Modifier.fillMaxWidth(), label = { Text("Recipient") }, shape = RoundedCornerShape(14.dp))
        OutlinedTextField(value = draft.allowedAction, onValueChange = onActionChanged, modifier = Modifier.fillMaxWidth(), label = { Text("Scope") }, shape = RoundedCornerShape(14.dp))
        OutlinedTextField(value = draft.duration, onValueChange = onDurationChanged, modifier = Modifier.fillMaxWidth(), label = { Text("Duration") }, shape = RoundedCornerShape(14.dp))
        DetailActions(primaryLabel = "Create delegation", onPrimary = onConfirm)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailScreen(
    title: String,
    onBack: () -> Unit,
    subtitle: String? = null,
    reassurance: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        containerColor = Mist,
        topBar = {
            androidx.compose.material3.CenterAlignedTopAppBar(
                title = { Text(title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Ink
                )
            )
        }
    ) { padding ->
        SediScreenBackdrop {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = SediVerticalRhythm.screenHorizontal),
                ) {
                    Spacer(Modifier.height(SediVerticalRhythm.detailTopBreathing + 8.dp))
                    if (subtitle != null) {
                        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = Slate)
                    }
                    if (reassurance != null) {
                        if (subtitle != null) Spacer(Modifier.height(6.dp))
                        ReassuranceLine(reassurance)
                    }
                    if (subtitle != null || reassurance != null) {
                        Spacer(Modifier.height(14.dp))
                    }
                    content()
                    Spacer(Modifier.height(48.dp))
                }
            }
        }
    }
}

@Composable
private fun DetailActions(
    primaryLabel: String,
    onPrimary: () -> Unit,
    secondaryLabel: String? = null,
    onSecondary: (() -> Unit)? = null,
    primaryColors: ButtonColors = ButtonDefaults.buttonColors()
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = CardWhite,
        tonalElevation = 2.dp,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onPrimary,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = primaryColors
            ) {
                Text(primaryLabel, fontWeight = FontWeight.SemiBold)
            }
            if (secondaryLabel != null && onSecondary != null) {
                OutlinedButton(
                    onClick = onSecondary,
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(secondaryLabel)
                }
            }
        }
    }
}

@Composable
private fun ResultScreen(
    title: String,
    body: String,
    primaryLabel: String,
    onPrimary: () -> Unit,
    secondaryLabel: String,
    onSecondary: () -> Unit,
    reassurance: String? = null,
    extraActionLabel: String? = null,
    onExtraAction: (() -> Unit)? = null
) {
    CenteredResultLayout(
        title = title,
        body = body,
        primaryLabel = primaryLabel,
        onPrimary = onPrimary,
        secondaryLabel = secondaryLabel,
        onSecondary = onSecondary,
        reassurance = reassurance,
        extraActionLabel = extraActionLabel,
        onExtraAction = onExtraAction
    )
}

@Composable
private fun RequestCard(request: VerificationRequest, onOpenRequest: () -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            InstitutionAvatar(request.institutionName)
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(request.institutionName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(request.requestedProof, color = Slate, style = MaterialTheme.typography.bodySmall)
            }
            RequestChip(request.status)
        }
        Spacer(Modifier.height(10.dp))
        InfoRow("Expires", request.expires)
        Button(onClick = onOpenRequest, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
            Text("Review request")
        }
    }
}

@Composable
private fun InstitutionCard(institution: ConnectedInstitution, onOpenInstitution: () -> Unit) {
    val isActive = institution.status == PermissionStatus.Active
    CalmPanel {
        Row(verticalAlignment = Alignment.CenterVertically) {
            InstitutionAvatar(institution.name)
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(institution.name, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleSmall)
                Text(
                    if (isActive) institution.allowedData.firstOrNull() ?: institution.accessScope else "No active access",
                    color = Slate,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            PermissionChip(institution.status)
        }
        if (isActive) {
            Spacer(Modifier.height(8.dp))
            InfoRow("Expires", institution.expiration)
        }
        Spacer(Modifier.height(10.dp))
        Button(onClick = onOpenInstitution, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
            Text(if (isActive) "View access" else "View details")
        }
    }
}

@Composable
private fun ActivityTimelineCard(event: ActivityEvent, showConnector: Boolean) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(24.dp)) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(TrustBlue.copy(alpha = 0.85f), CircleShape)
            )
            if (showConnector) {
                Box(
                    modifier = Modifier
                        .width(1.5.dp)
                        .height(72.dp)
                        .background(Slate.copy(alpha = 0.12f))
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = if (showConnector) 14.dp else 4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    event.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                ActivityResultChip(event.result)
            }
            Spacer(Modifier.height(4.dp))
            Text(
                event.description,
                color = Slate,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(6.dp))
            Text(event.timestamp, color = Slate, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun ActivityResultChip(result: String) {
    Surface(
        color = when (result.lowercase()) {
            "revoked", "denied" -> Danger.copy(alpha = 0.12f)
            "shared", "active", "confirmed", "ready", "created" -> Success.copy(alpha = 0.12f)
            else -> TrustBlue.copy(alpha = 0.12f)
        },
        shape = RoundedCornerShape(999.dp)
    ) {
        Text(
            result,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = when (result.lowercase()) {
                "revoked", "denied" -> Danger
                "shared", "active", "confirmed", "ready", "created" -> Success
                else -> TrustBlue
            }
        )
    }
}

@Composable
private fun CalmPanel(content: @Composable ColumnScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) { content() }
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
    Surface(color = CardWhite, tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
        ) {
            TextButton(onClick = onBack) { Text("Back") }
            Text(
                title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun WalletMark() {
    SediLogoMark(size = 68.dp, variant = SediLogoVariant.OnLight, animated = false)
}

@Composable
private fun StatusCircle(text: String, color: Color, textColor: Color = Color.White, size: Dp = 48.dp) {
    Box(modifier = Modifier.size(size).clip(CircleShape).background(color), contentAlignment = Alignment.Center) {
        Text(text, color = textColor, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
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
