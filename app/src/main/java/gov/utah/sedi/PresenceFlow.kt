package gov.utah.sedi

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay

private val PresenceWarmMid = Color(0xFFEEF4F9)
private val PresenceGlow = Color(0xFF7EB8D4)
private val PresenceSoftBlue = Color(0xFF2A6F97)
private val PresenceSuccess = Color(0xFF197A56)

@Composable
fun PresenceIntroScreen(
    step: Int,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    PresenceFlowScaffold(
        step = step,
        title = "Confirm your presence",
        subtitle = "A quick check helps protect your wallet.",
        reassurance = SediBrand.Copy.NOT_SHARED,
        primaryLabel = "Start check",
        onPrimary = onContinue,
        showBack = true,
        onBack = onBack
    ) {
        PresenceIntroIllustration()
    }
}

@Composable
fun CameraPermissionScreen(
    step: Int,
    onPermissionGranted: () -> Unit,
    onSkip: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var granted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        granted = isGranted
        if (isGranted) onPermissionGranted()
    }

    PresenceFlowScaffold(
        step = step,
        title = if (granted) "Camera ready" else "Enable camera",
        subtitle = if (granted) "Ready for your presence check." else "Used once during setup.",
        reassurance = SediBrand.Copy.NOT_SHARED,
        primaryLabel = if (granted) "Continue" else "Enable camera",
        onPrimary = {
            if (granted) onPermissionGranted() else launcher.launch(Manifest.permission.CAMERA)
        },
        secondaryLabel = "Not now",
        onSecondary = onSkip,
        showBack = true,
        onBack = onBack,
        immersive = true
    ) {
        CameraPermissionIllustration()
    }
}

@Composable
fun FaceAlignmentScreen(
    step: Int,
    cameraEnabled: Boolean,
    onAligned: () -> Unit,
    onBack: () -> Unit
) {
    var alignmentStage by remember { mutableIntStateOf(0) }
    val guidance = when (alignmentStage) {
        0 -> "Center your face"
        1 -> "Good lighting detected"
        else -> "Face aligned"
    }
    val aligned = alignmentStage >= 2
    val glowStrength by animateFloatAsState(
        targetValue = if (aligned) 1f else 0.55f + alignmentStage * 0.15f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "alignGlow"
    )

    LaunchedEffect(Unit) {
        delay(1600)
        alignmentStage = 1
        delay(1800)
        alignmentStage = 2
        delay(1200)
        onAligned()
    }

    PresenceFlowScaffold(
        step = step,
        title = "Position your face",
        subtitle = "Hold at eye level in good light.",
        primaryLabel = if (aligned) "Continuing…" else "Aligning…",
        onPrimary = { if (aligned) onAligned() },
        showBack = true,
        onBack = onBack,
        primaryEnabled = false,
        immersive = true,
        fullBleed = true
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            FaceAlignmentPreview(
                aligned = aligned,
                glowStrength = glowStrength,
                cameraEnabled = cameraEnabled,
                fullBleed = true
            )
            Spacer(Modifier.height(20.dp))
            AnimatedContent(
                targetState = guidance,
                transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(300)) },
                label = "guidance",
                modifier = Modifier.padding(horizontal = 24.dp)
            ) { text ->
                Text(
                    text,
                    color = Color.White.copy(alpha = 0.92f),
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun PresenceDetectedScreen(
    step: Int,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    var revealed by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(500)
        revealed = true
    }

    PresenceFlowScaffold(
        step = step,
        title = "Presence detected",
        subtitle = "Only you can approve from this wallet.",
        reassurance = SediBrand.Copy.IN_CONTROL,
        primaryLabel = if (revealed) "Continue" else "Confirming…",
        onPrimary = onContinue,
        showBack = true,
        onBack = onBack,
        primaryEnabled = revealed,
        immersive = true
    ) {
        PresenceConfirmationPulse(visible = revealed)
    }
}

@Composable
fun LivenessCheckScreen(
    step: Int,
    onComplete: () -> Unit,
    onBack: () -> Unit
) {
    var livenessStep by remember { mutableIntStateOf(0) }
    var completed by remember { mutableStateOf(false) }
    val instructions = listOf(
        "Look slightly left",
        "Look slightly right",
        "Blink naturally"
    )

    LaunchedEffect(Unit) {
        delay(1400)
        livenessStep = 1
        delay(2000)
        livenessStep = 2
        delay(2000)
        livenessStep = 3
        delay(1800)
        completed = true
    }

    val currentInstruction = when {
        completed -> "Presence confirmed"
        livenessStep == 0 -> "Follow the gentle prompts"
        livenessStep <= instructions.size -> instructions[livenessStep - 1]
        else -> "Presence confirmed"
    }

    PresenceFlowScaffold(
        step = step,
        title = "Confirm it's you",
        subtitle = "Follow the gentle prompts.",
        primaryLabel = if (completed) "Continue" else "Follow prompts",
        onPrimary = { if (completed) onComplete() },
        showBack = true,
        onBack = onBack,
        primaryEnabled = completed,
        immersive = true,
        nextStepHint = currentInstruction
    ) {
        LivenessInteractionFrame(
            activeStep = livenessStep,
            totalSteps = instructions.size,
            instruction = currentInstruction,
            completed = completed
        )
    }
}

@Composable
fun SecureConfirmationScreen(
    step: Int,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    PresenceFlowScaffold(
        step = step,
        title = "Presence confirmed",
        subtitle = "Your wallet is protected on this device.",
        reassurance = SediBrand.Copy.NOT_SHARED,
        primaryLabel = "Continue",
        onPrimary = onContinue,
        showBack = true,
        onBack = onBack
    ) {
        SecureSessionStatusCard()
    }
}

@Composable
fun IdentitySessionReadyScreen(
    step: Int,
    onContinue: () -> Unit
) {
    PresenceFlowScaffold(
        step = step,
        title = "You're ready",
        subtitle = "Your wallet is protected for the UVU request.",
        reassurance = SediBrand.Copy.REVIEW_BEFORE,
        primaryLabel = "Continue",
        onPrimary = onContinue
    ) {}
}

@Composable
private fun PresenceFlowScaffold(
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
    immersive: Boolean = false,
    fullBleed: Boolean = false,
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
        immersive = immersive,
        focusCentered = true,
        mood = if (immersive) SceneMood.Biometric else SceneMood.Approval,
        focusFullBleed = fullBleed,
        content = content
    )
}

@Composable
private fun PresenceIntroIllustration() {
    val infiniteTransition = rememberInfiniteTransition(label = "introBreath")
    val breath by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(tween(2800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "breath"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(tween(3200, easing = LinearEasing), RepeatMode.Reverse),
        label = "glow"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size((180 * breath).dp)
                .border(1.5.dp, PresenceGlow.copy(alpha = glowAlpha), CircleShape)
        )
        Box(
            modifier = Modifier
                .size((140 * breath).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(Color.White, PresenceWarmMid.copy(alpha = 0.8f))
                    )
                )
                .border(2.dp, PresenceSoftBlue.copy(alpha = 0.35f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(PresenceSoftBlue.copy(alpha = 0.12f))
                )
                Spacer(Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .width(64.dp)
                        .height(72.dp)
                        .clip(RoundedCornerShape(40.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(PresenceSoftBlue.copy(alpha = 0.08f), PresenceGlow.copy(alpha = 0.15f))
                            )
                        )
                        .border(1.dp, PresenceSoftBlue.copy(alpha = 0.2f), RoundedCornerShape(40.dp))
                )
            }
        }
    }
}

@Composable
private fun CameraPermissionIllustration() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.12f))
                .border(1.5.dp, TrustBlue.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("◎", style = MaterialTheme.typography.headlineMedium, color = Color(0xFF9EC9DC))
        }
    }
}

@Composable
private fun FaceAlignmentPreview(
    aligned: Boolean,
    glowStrength: Float,
    cameraEnabled: Boolean,
    fullBleed: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "alignPulse")
    val ringScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(tween(2400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "ring"
    )
    val depthAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Reverse),
        label = "depth"
    )

    Box(
        modifier = Modifier
            .then(
                if (fullBleed) Modifier.fillMaxWidth().height(300.dp)
                else Modifier.fillMaxWidth().height(260.dp).clip(RoundedCornerShape(28.dp))
            ),
        contentAlignment = Alignment.Center
    ) {
        if (cameraEnabled && !fullBleed) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Color(0xFF4A6B7C).copy(alpha = 0.4f),
                                Color(0xFF1E3340).copy(alpha = 0.9f)
                            )
                        )
                    )
            )
        }

        Box(
            modifier = Modifier
                .size((260 * ringScale).dp)
                .border(
                    width = 2.dp,
                    brush = Brush.sweepGradient(
                        listOf(
                            PresenceGlow.copy(alpha = depthAlpha * glowStrength),
                            Color.Transparent,
                            PresenceGlow.copy(alpha = depthAlpha * 0.5f * glowStrength)
                        )
                    ),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size((220 * ringScale).dp)
                .border(
                    1.5.dp,
                    Color.White.copy(alpha = if (aligned) 0.55f else 0.25f),
                    CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color.White.copy(alpha = 0.18f),
                            Color.White.copy(alpha = 0.05f)
                        )
                    )
                )
                .border(
                    width = if (aligned) 3.dp else 2.dp,
                    color = if (aligned) PresenceSuccess.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.35f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            SoftFaceSilhouette(aligned = aligned)
        }
    }
}

@Composable
private fun SoftFaceSilhouette(aligned: Boolean) {
    val tint = if (aligned) PresenceSuccess.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.15f)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(tint)
        )
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .width(72.dp)
                .height(88.dp)
                .clip(RoundedCornerShape(44.dp))
                .background(tint)
        )
    }
}

@Composable
private fun PresenceConfirmationPulse(visible: Boolean) {
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.85f,
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = "pulseScale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0.4f,
        animationSpec = tween(600),
        label = "pulseAlpha"
    )
    val infiniteTransition = rememberInfiniteTransition(label = "sessionPulse")
    val ripple by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "ripple"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp),
        contentAlignment = Alignment.Center
    ) {
        if (visible) {
            Box(
                modifier = Modifier
                    .size((200 * ripple).dp)
                    .border(1.dp, PresenceSuccess.copy(alpha = 0.2f), CircleShape)
            )
        }
        Box(
            modifier = Modifier
                .size((140 * scale).dp)
                .clip(CircleShape)
                .background(PresenceSuccess.copy(alpha = 0.12f * alpha))
                .border(2.dp, PresenceSuccess.copy(alpha = 0.5f * alpha), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                if (visible) "✓" else "…",
                style = MaterialTheme.typography.displaySmall,
                color = PresenceSuccess.copy(alpha = alpha),
                fontWeight = FontWeight.Light
            )
        }
    }
}

@Composable
private fun LivenessInteractionFrame(
    activeStep: Int,
    totalSteps: Int,
    instruction: String,
    completed: Boolean
) {
    val progress = when {
        completed -> 1f
        activeStep == 0 -> 0.08f
        else -> activeStep.toFloat() / (totalSteps + 1)
    }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "livenessProgress"
    )
    val infiniteTransition = rememberInfiniteTransition(label = "livenessBreath")
    val breath by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "livenessBreath"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size((220 * breath).dp)) {
            val stroke = 6.dp.toPx()
            drawArc(
                color = Color(0xFFE2E8F0),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            drawArc(
                brush = Brush.sweepGradient(listOf(PresenceSoftBlue, PresenceGlow, PresenceSoftBlue)),
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(Color.White.copy(alpha = 0.22f), Color.White.copy(alpha = 0.06f))
                    )
                )
                .border(2.dp, PresenceSoftBlue.copy(alpha = 0.35f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AnimatedContent(
                    targetState = instruction,
                    transitionSpec = { fadeIn(tween(350)) togetherWith fadeOut(tween(250)) },
                    label = "livenessInstruction"
                ) { text ->
                    Text(
                        text,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SecureSessionStatusCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(PresenceSuccess.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Text("✓", color = PresenceSuccess, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Session active", fontWeight = FontWeight.SemiBold, color = Ink, style = MaterialTheme.typography.bodyMedium)
                Text("Wallet protected", color = Slate, style = MaterialTheme.typography.labelSmall)
            }
        }
        Spacer(Modifier.height(14.dp))
        LinearProgressIndicator(
            progress = { 1f },
            modifier = Modifier.fillMaxWidth().height(3.dp).clip(RoundedCornerShape(2.dp)),
            color = PresenceSuccess,
            trackColor = Color(0xFFE2E8F0),
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
private fun CalmPresencePanel(content: @Composable ColumnScope.() -> Unit) {
    SceneBlendRegion(content = content)
}
