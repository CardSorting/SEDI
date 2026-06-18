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
    val top = if (warm) Color(0xFFF8F4EF) else Mist
    val mid = if (warm) Color(0xFFEEF4F9) else Color(0xFFF3F1EE)
    val bottom = if (warm) Color(0xFFE4EDF6) else Color(0xFFECE8E4)
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(top, mid, bottom)))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = if (warm) 0.42f else 0.28f),
                            Color.Transparent
                        ),
                        radius = 900f
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, bottom.copy(alpha = 0.55f))
                    )
                )
        )
        content()
    }
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
    Surface(
        color = CardWhite,
        tonalElevation = if (elevated) 4.dp else 2.dp,
        shadowElevation = if (elevated) 6.dp else 0.dp,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SediVerticalRhythm.screenHorizontal, vertical = SediVerticalRhythm.actionSurfaceTop),
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
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            SediActionSurface(
                primaryLabel = primaryLabel,
                onPrimary = onPrimary,
                primaryEnabled = primaryEnabled,
                secondaryLabel = secondaryLabel,
                onSecondary = onSecondary,
                nextStepHint = nextStepHint
            )
        }
    ) { innerPadding ->
        SediScreenBackdrop(warm = warmBackdrop) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = SediVerticalRhythm.screenHorizontal)
            ) {
                Spacer(Modifier.height(if (immersive) 4.dp else 8.dp))
                if (step != null) {
                    CompactOnboardingHeader(step = step, showPhaseStrip = showPhaseStrip)
                }
                if (showBack && onBack != null) {
                    TextButton(
                        onClick = onBack,
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 0.dp, vertical = 2.dp)
                    ) {
                        Text("Back", style = MaterialTheme.typography.labelLarge)
                    }
                }
                SediFlowTitle(
                    title = title,
                    subtitle = subtitle,
                    immersive = immersive,
                    reassurance = reassurance,
                    centerAligned = immersive || focusCentered
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalAlignment = if (immersive || focusCentered) Alignment.CenterHorizontally else Alignment.Start,
                    verticalArrangement = if (focusCentered) Arrangement.Center else Arrangement.Top,
                    content = content
                )
                Spacer(Modifier.height(SediVerticalRhythm.focusBreathing))
            }
        }
    }
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
    SediScreenBackdrop {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = SediVerticalRhythm.screenHorizontal)
                    .padding(bottom = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .height(120.dp)
                            .fillMaxWidth(0.7f)
                            .background(
                                Brush.radialGradient(
                                    listOf(SediBrand.Success.copy(alpha = 0.14f), Color.Transparent)
                                )
                            )
                    )
                    SuccessGlyph(size = 64.dp)
                }
                Spacer(Modifier.height(20.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Ink,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate,
                    textAlign = TextAlign.Center
                )
                if (reassurance != null) {
                    Spacer(Modifier.height(12.dp))
                    ReassuranceLine(reassurance)
                }
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = SediVerticalRhythm.screenHorizontal, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onPrimary,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(primaryLabel, fontWeight = FontWeight.SemiBold)
                }
                if (extraActionLabel != null && onExtraAction != null) {
                    OutlinedButton(
                        onClick = onExtraAction,
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(extraActionLabel)
                    }
                }
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
