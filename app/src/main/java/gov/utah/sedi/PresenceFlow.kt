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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

private val PresenceWarmTop = Color(0xFFF8F4EF)
private val PresenceWarmMid = Color(0xFFEEF4F9)
private val PresenceWarmBottom = Color(0xFFE4EDF6)
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
        onBack = onBack
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
        nextStepHint = guidance
    ) {
        FaceAlignmentPreview(
            guidance = guidance,
            aligned = aligned,
            glowStrength = glowStrength,
            cameraEnabled = cameraEnabled
        )
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
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            Surface(color = CardWhite, tonalElevation = 4.dp, shadowElevation = 8.dp) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (nextStepHint != null) {
                        Text(
                            nextStepHint,
                            color = Slate,
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Button(
                        onClick = onPrimary,
                        enabled = primaryEnabled,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StateBlue)
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
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        listOf(PresenceWarmTop, PresenceWarmMid, PresenceWarmBottom)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = if (immersive) Alignment.CenterHorizontally else Alignment.Start
            ) {
                OnboardingProgress(step)
                OnboardingPhaseStrip(step)
                if (showBack && onBack != null) {
                    TextButton(
                        onClick = onBack,
                        modifier = Modifier.padding(top = 2.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 0.dp, vertical = 4.dp)
                    ) {
                        Text("Back", style = MaterialTheme.typography.labelLarge)
                    }
                }
                Spacer(Modifier.height(if (immersive) 6.dp else 8.dp))
                if (!immersive) {
                    Text(
                        title,
                        style = MaterialTheme.typography.titleLarge,
                        color = Ink,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate
                    )
                    if (reassurance != null) {
                        Spacer(Modifier.height(6.dp))
                        ReassuranceLine(reassurance)
                    }
                    Spacer(Modifier.height(14.dp))
                } else {
                    Text(
                        title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Ink,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (reassurance != null) {
                        Spacer(Modifier.height(6.dp))
                        ReassuranceLine(
                            reassurance,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                }
                content()
                Spacer(Modifier.height(16.dp))
            }
        }
    }
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
            .height(180.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.9f),
                        PresenceWarmMid,
                        PresenceGlow.copy(alpha = 0.2f)
                    )
                )
            ),
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
            .height(160.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(listOf(Color(0xFFF5F9FC), Color(0xFFE8F1F8)))
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Color.White)
                .border(1.5.dp, TrustBlue.copy(alpha = 0.25f), RoundedCornerShape(22.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("◎", style = MaterialTheme.typography.headlineMedium, color = TrustBlue)
        }
    }
}

@Composable
private fun FaceAlignmentPreview(
    guidance: String,
    aligned: Boolean,
    glowStrength: Float,
    cameraEnabled: Boolean
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
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1A2E3B).copy(alpha = 0.85f), Color(0xFF2A4A5E).copy(alpha = 0.75f))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        if (cameraEnabled) {
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

        AnimatedContent(
            targetState = guidance,
            transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(300)) },
            label = "guidance",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 28.dp)
        ) { text ->
            Surface(
                color = Color.Black.copy(alpha = 0.35f),
                shape = RoundedCornerShape(999.dp)
            ) {
                Text(
                    text,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
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
            .height(220.dp),
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
            .height(240.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size((200 * breath).dp)) {
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
                .size(140.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(Color.White, PresenceWarmMid)
                    )
                )
                .border(2.dp, PresenceSoftBlue.copy(alpha = 0.3f), CircleShape),
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
                        color = Ink,
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
    CalmPresencePanel {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(PresenceSuccess.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text("✓", color = PresenceSuccess, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text("Session active", fontWeight = FontWeight.SemiBold, color = Ink, style = MaterialTheme.typography.bodyMedium)
                Text("Wallet protected", color = Slate, style = MaterialTheme.typography.labelSmall)
            }
        }
        Spacer(Modifier.height(10.dp))
        LinearProgressIndicator(
            progress = { 1f },
            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
            color = PresenceSuccess,
            trackColor = Color(0xFFE2E8F0),
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
private fun CalmPresencePanel(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        color = CardWhite.copy(alpha = 0.92f),
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), content = content)
    }
}
