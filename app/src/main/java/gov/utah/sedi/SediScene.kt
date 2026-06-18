package gov.utah.sedi

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class SceneMood {
    Neutral,
    Biometric,
    Institutional,
    Privacy,
    Approval,
    Success
}

@Composable
fun EnvironmentalBackground(
    mood: SceneMood = SceneMood.Neutral,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sceneAmbient")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(4200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "scenePulse"
    )
    val drift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing), RepeatMode.Reverse),
        label = "sceneDrift"
    )

    Box(modifier = modifier.fillMaxSize()) {
        when (mood) {
            SceneMood.Biometric -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF0D1822), Color(0xFF152A38), Color(0xFF1A3344))
                            )
                        )
                )
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF4A9BB5).copy(alpha = 0.22f * pulse),
                                    Color(0xFF2A6F97).copy(alpha = 0.08f),
                                    Color.Transparent
                                ),
                                radius = 680f + drift * 80f
                            )
                        )
                )
                Box(
                    Modifier
                        .size((320 * pulse).dp)
                        .align(Alignment.Center)
                        .border(1.dp, Color(0xFF7EB8D4).copy(alpha = 0.12f), CircleShape)
                )
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color(0xFF0D1822).copy(alpha = 0.85f))
                            )
                        )
                )
            }
            SceneMood.Institutional -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF1A2E42), Color(0xFF243D56), Color(0xFF2E4D6A))
                            )
                        )
                )
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .align(Alignment.TopCenter)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF4A7A9A).copy(alpha = 0.35f),
                                    Color.Transparent
                                ),
                                radius = 500f
                            )
                        )
                )
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.06f),
                                    Color.Transparent
                                ),
                                radius = 900f
                            )
                        )
                )
            }
            SceneMood.Privacy -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFFE8F0F6), Color(0xFFDCE9F2), Color(0xFFD2E3EE))
                            )
                        )
                )
                Box(
                    Modifier
                        .fillMaxWidth(0.55f)
                        .fillMaxHeight(0.45f)
                        .align(Alignment.TopStart)
                        .background(
                            Brush.radialGradient(
                                listOf(SediBrand.Success.copy(alpha = 0.12f), Color.Transparent)
                            )
                        )
                )
                Box(
                    Modifier
                        .fillMaxWidth(0.55f)
                        .fillMaxHeight(0.45f)
                        .align(Alignment.BottomEnd)
                        .background(
                            Brush.radialGradient(
                                listOf(StateBlue.copy(alpha = 0.1f), Color.Transparent)
                            )
                        )
                )
            }
            SceneMood.Approval -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFFF2F6F9), Color(0xFFE8EFF5), Color(0xFFDEE8F0))
                            )
                        )
                )
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    TrustBlue.copy(alpha = 0.14f * pulse),
                                    Color.Transparent
                                ),
                                radius = 750f
                            )
                        )
                )
            }
            SceneMood.Success -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFFF0F8F4), Color(0xFFE4F2EA), Color(0xFFD8EBE2))
                            )
                        )
                )
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    SediBrand.Success.copy(alpha = 0.2f * pulse),
                                    Color.Transparent
                                ),
                                radius = 600f + drift * 100f
                            )
                        )
                )
            }
            SceneMood.Neutral -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Mist, Color(0xFFF3F1EE), Color(0xFFECE8E4))
                            )
                        )
                )
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color.White.copy(alpha = 0.3f), Color.Transparent),
                                radius = 900f
                            )
                        )
                )
            }
        }
        content()
    }
}

@Composable
fun SceneContextLayer(
    step: Int? = null,
    total: Int = 15,
    sectionLabel: String? = null,
    showBack: Boolean = false,
    onBack: (() -> Unit)? = null,
    mood: SceneMood = SceneMood.Neutral,
    lightText: Boolean = false
) {
    val labelColor = when {
        lightText -> Color.White.copy(alpha = 0.72f)
        mood == SceneMood.Biometric || mood == SceneMood.Institutional -> Color.White.copy(alpha = 0.7f)
        else -> Slate
    }
    val accentColor = when {
        lightText || mood == SceneMood.Biometric || mood == SceneMood.Institutional -> Color(0xFF9EC9DC)
        else -> TrustBlue
    }
    val titleColor = when {
        lightText || mood == SceneMood.Biometric || mood == SceneMood.Institutional -> Color.White
        else -> Ink
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SediVerticalRhythm.screenHorizontal, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showBack && onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = titleColor
                    )
                }
            } else {
                Spacer(Modifier.width(4.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                if (step != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Step $step of $total",
                            color = accentColor,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            sectionLabel ?: OnboardingPhase.forStep(step).chipLabel,
                            color = labelColor,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { step.toFloat() / total },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = if (lightText || mood == SceneMood.Institutional) Color.White.copy(alpha = 0.85f) else StateBlue,
                        trackColor = if (lightText || mood == SceneMood.Institutional) Color.White.copy(alpha = 0.18f) else Color(0xFFE2E8F0),
                        strokeCap = StrokeCap.Round
                    )
                }
            }
        }
    }
}

@Composable
fun SceneFocusTitle(
    title: String,
    subtitle: String,
    mood: SceneMood = SceneMood.Neutral,
    reassurance: String? = null,
    centerAligned: Boolean = true
) {
    val titleColor = when (mood) {
        SceneMood.Biometric, SceneMood.Institutional -> Color.White
        else -> Ink
    }
    val subtitleColor = when (mood) {
        SceneMood.Biometric, SceneMood.Institutional -> Color.White.copy(alpha = 0.78f)
        else -> Slate
    }
    val align = if (centerAligned) TextAlign.Center else TextAlign.Start

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SediVerticalRhythm.screenHorizontal)
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            color = titleColor,
            fontWeight = FontWeight.Bold,
            textAlign = align,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(4.dp))
        Text(
            subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = subtitleColor,
            textAlign = align,
            modifier = Modifier.fillMaxWidth()
        )
        if (reassurance != null) {
            Spacer(Modifier.height(6.dp))
            Text(
                reassurance,
                style = MaterialTheme.typography.labelMedium,
                color = if (mood == SceneMood.Biometric || mood == SceneMood.Institutional) Color(0xFF9EC9DC) else TrustBlue,
                fontWeight = FontWeight.Medium,
                textAlign = align,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
fun IntegratedActionLayer(
    primaryLabel: String,
    onPrimary: () -> Unit,
    primaryEnabled: Boolean = true,
    secondaryLabel: String? = null,
    onSecondary: (() -> Unit)? = null,
    nextStepHint: String? = null,
    mood: SceneMood = SceneMood.Neutral
) {
    val barTop = when (mood) {
        SceneMood.Biometric -> Color(0xFF0D1822).copy(alpha = 0.55f)
        SceneMood.Institutional -> Color(0xFF1A2E42).copy(alpha = 0.6f)
        else -> Color.White.copy(alpha = 0.72f)
    }
    val hintColor = when (mood) {
        SceneMood.Biometric, SceneMood.Institutional -> Color.White.copy(alpha = 0.7f)
        else -> Slate
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(Color.Transparent, barTop)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SediVerticalRhythm.screenHorizontal, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (nextStepHint != null) {
                Text(
                    nextStepHint,
                    color = hintColor,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Button(
                onClick = onPrimary,
                enabled = primaryEnabled,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StateBlue)
            ) {
                Text(primaryLabel, fontWeight = FontWeight.SemiBold)
            }
            if (secondaryLabel != null && onSecondary != null) {
                OutlinedButton(
                    onClick = onSecondary,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (mood == SceneMood.Biometric || mood == SceneMood.Institutional) {
                            Color.White.copy(alpha = 0.35f)
                        } else {
                            Slate.copy(alpha = 0.3f)
                        }
                    )
                ) {
                    Text(
                        secondaryLabel,
                        color = if (mood == SceneMood.Biometric || mood == SceneMood.Institutional) Color.White else Ink
                    )
                }
            }
        }
    }
}

@Composable
fun SceneFlowScaffold(
    mood: SceneMood = SceneMood.Neutral,
    step: Int? = null,
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
    focusFullBleed: Boolean = false,
    suppressHeader: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val lightMood = mood == SceneMood.Biometric || mood == SceneMood.Institutional

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            IntegratedActionLayer(
                primaryLabel = primaryLabel,
                onPrimary = onPrimary,
                primaryEnabled = primaryEnabled,
                secondaryLabel = secondaryLabel,
                onSecondary = onSecondary,
                nextStepHint = nextStepHint,
                mood = mood
            )
        }
    ) { innerPadding ->
        EnvironmentalBackground(mood = mood) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                SceneContextLayer(
                    step = step,
                    showBack = showBack,
                    onBack = onBack,
                    mood = mood,
                    lightText = lightMood
                )
                if (!suppressHeader) {
                    if (!immersive) {
                        SceneFocusTitle(
                            title = title,
                            subtitle = subtitle,
                            mood = mood,
                            reassurance = reassurance,
                            centerAligned = immersive || mood == SceneMood.Institutional
                        )
                    } else {
                        SceneFocusTitle(
                            title = title,
                            subtitle = subtitle,
                            mood = mood,
                            reassurance = null,
                            centerAligned = true
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .then(
                            if (!focusFullBleed) {
                                Modifier.padding(horizontal = SediVerticalRhythm.screenHorizontal)
                            } else {
                                Modifier
                            }
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    content = content
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PrivacyEnvironmentSplit(
    shared: List<String>,
    hidden: List<String>,
    sharedLabel: String = "UVU receives",
    hiddenLabel: String = "Stays private"
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            SediBrand.Success.copy(alpha = 0.14f),
                            SediBrand.Success.copy(alpha = 0.06f)
                        )
                    )
                )
                .padding(horizontal = 20.dp, vertical = 22.dp)
        ) {
            Column {
                Text(
                    sharedLabel,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = SediBrand.Success
                )
                Spacer(Modifier.height(10.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    shared.forEach { item ->
                        ScenePrivacyChip(item, positive = true)
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            TrustBlue.copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            StateBlue.copy(alpha = 0.08f),
                            StateBlue.copy(alpha = 0.14f)
                        )
                    )
                )
                .padding(horizontal = 20.dp, vertical = 22.dp)
        ) {
            Column {
                Text(
                    hiddenLabel,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = StateBlue
                )
                Spacer(Modifier.height(10.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    hidden.forEach { item ->
                        ScenePrivacyChip(item, positive = false)
                    }
                }
            }
        }
    }
}

@Composable
private fun ScenePrivacyChip(label: String, positive: Boolean) {
    val tone = if (positive) SediBrand.Success else StateBlue
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(tone.copy(alpha = if (positive) 0.16f else 0.1f))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            if (positive) "✓" else "—",
            color = tone,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelSmall
        )
        Spacer(Modifier.width(6.dp))
        Text(label, color = Ink, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun InstitutionalRequestPresentation(
    institutionName: String,
    headline: String,
    detail: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF2A4D6A).copy(alpha = 0.95f),
                            Color(0xFF1E3A52).copy(alpha = 0.98f)
                        )
                    )
                )
                .padding(horizontal = 24.dp, vertical = 28.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.14f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            institutionName.split(" ").take(2).joinToString("") { it.first().uppercase() },
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(
                            institutionName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "Identity verification request",
                            color = Color.White.copy(alpha = 0.68f),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
                Text(
                    headline,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 26.sp, lineHeight = 32.sp),
                    fontWeight = FontWeight.Bold
                )
                if (detail.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        detail,
                        color = Color.White.copy(alpha = 0.78f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.White.copy(alpha = 0.15f))
                )
                Spacer(Modifier.height(14.dp))
                Text(
                    SediBrand.Copy.REVIEW_BEFORE,
                    color = Color(0xFF9EC9DC),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ExpansiveSuccessScene(
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
    EnvironmentalBackground(mood = SceneMood.Success) {
        Box(modifier = Modifier.fillMaxSize()) {
            val infiniteTransition = rememberInfiniteTransition(label = "successPulse")
            val ripple by infiniteTransition.animateFloat(
                initialValue = 0.88f,
                targetValue = 1.12f,
                animationSpec = infiniteRepeatable(tween(2800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
                label = "successRipple"
            )
            Box(
                modifier = Modifier
                    .size((280 * ripple).dp)
                    .align(Alignment.Center)
                    .background(
                        Brush.radialGradient(
                            listOf(SediBrand.Success.copy(alpha = 0.12f), Color.Transparent)
                        ),
                        shape = CircleShape
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 28.dp)
                    .padding(bottom = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SuccessGlyph(size = 80.dp)
                Spacer(Modifier.height(28.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Ink,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    body,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Slate,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
                if (reassurance != null) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        reassurance,
                        style = MaterialTheme.typography.labelLarge,
                        color = SediBrand.Success,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                if (extraActionLabel != null && onExtraAction != null) {
                    OutlinedButton(
                        onClick = onExtraAction,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = SediVerticalRhythm.screenHorizontal)
                            .height(48.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(extraActionLabel)
                    }
                    Spacer(Modifier.height(4.dp))
                }
                IntegratedActionLayer(
                    primaryLabel = primaryLabel,
                    onPrimary = onPrimary,
                    secondaryLabel = secondaryLabel,
                    onSecondary = onSecondary,
                    mood = SceneMood.Success
                )
            }
        }
    }
}

@Composable
fun SceneBlendRegion(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.45f),
                        Color.White.copy(alpha = 0.28f)
                    )
                )
            )
            .padding(horizontal = 20.dp, vertical = 18.dp),
        content = content
    )
}
