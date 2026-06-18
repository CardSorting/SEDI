package gov.utah.sedi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal object SediVerticalRhythm {
    val screenHorizontal = 20.dp
    val atmosphereBottom = 6.dp
    val focusBreathing = 12.dp
    val actionSurfaceTop = 10.dp
    val tabTopBreathing = 20.dp
    val detailTopBreathing = 16.dp
}

@Composable
fun SediScreenBackdrop(
    modifier: Modifier = Modifier,
    warm: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    EnvironmentalBackground(
        mood = if (warm) SceneMood.Approval else SceneMood.Neutral,
        modifier = modifier,
        content = content
    )
}

@Composable
fun CompactOnboardingHeader(
    step: Int,
    total: Int = 15,
    showPhaseStrip: Boolean = true
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = SediVerticalRhythm.atmosphereBottom)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Step $step of $total",
                color = TrustBlue,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                OnboardingPhase.forStep(step).chipLabel,
                color = Slate,
                style = MaterialTheme.typography.labelSmall
            )
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { step.toFloat() / total },
            modifier = Modifier.fillMaxWidth().height(3.dp).clip(RoundedCornerShape(2.dp)),
            color = StateBlue,
            trackColor = Color(0xFFE2E8F0),
            strokeCap = StrokeCap.Round
        )
        if (showPhaseStrip) {
            Spacer(Modifier.height(6.dp))
            OnboardingPhaseStrip(step)
        }
    }
}

@Composable
fun SediFlowTitle(
    title: String,
    subtitle: String,
    immersive: Boolean = false,
    reassurance: String? = null,
    centerAligned: Boolean = false
) {
    val align = if (immersive || centerAligned) TextAlign.Center else TextAlign.Start
    val subtitleStyle = if (immersive) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium
    Text(
        title,
        style = MaterialTheme.typography.titleMedium,
        color = Ink,
        fontWeight = FontWeight.Bold,
        textAlign = align,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(if (immersive) 3.dp else 4.dp))
    Text(
        subtitle,
        style = subtitleStyle,
        color = Slate,
        textAlign = align,
        modifier = Modifier.fillMaxWidth()
    )
    if (reassurance != null) {
        Spacer(Modifier.height(5.dp))
        ReassuranceLine(reassurance, modifier = Modifier.fillMaxWidth())
    }
    Spacer(Modifier.height(if (immersive) 8.dp else 10.dp))
}

@Composable
fun SediActionSurface(
    primaryLabel: String,
    onPrimary: () -> Unit,
    primaryEnabled: Boolean = true,
    secondaryLabel: String? = null,
    onSecondary: (() -> Unit)? = null,
    nextStepHint: String? = null,
    elevated: Boolean = true
) {
    IntegratedActionLayer(
        primaryLabel = primaryLabel,
        onPrimary = onPrimary,
        primaryEnabled = primaryEnabled,
        secondaryLabel = secondaryLabel,
        onSecondary = onSecondary,
        nextStepHint = nextStepHint,
        mood = SceneMood.Neutral
    )
}

@Composable
fun BalancedFlowScaffold(
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
    focusCentered: Boolean = true,
    warmBackdrop: Boolean = false,
    showPhaseStrip: Boolean = !immersive,
    mood: SceneMood = when {
        warmBackdrop -> SceneMood.Approval
        immersive -> SceneMood.Biometric
        else -> SceneMood.Neutral
    },
    focusFullBleed: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    SceneFlowScaffold(
        mood = mood,
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
        focusFullBleed = focusFullBleed,
        content = content
    )
}

@Composable
fun CenteredResultLayout(
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
    ExpansiveSuccessScene(
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
fun SuccessGlyph(size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(SediBrand.Success.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "✓",
            color = SediBrand.Success,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineSmall
        )
    }
}
