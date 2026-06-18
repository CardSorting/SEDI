package gov.utah.sedi

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val CalmEase = CubicBezierEasing(0.22f, 0.61f, 0.36f, 1f)
private val SettleEase = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)

@Composable
fun CinematicSplashScreen(
    onHandoff: () -> Unit = {},
    onFinished: () -> Unit
) {
    val ambient = remember { Animatable(0f) }
    val depthAlpha = remember { Animatable(0f) }
    val presence = remember { Animatable(0f) }
    val trustField = remember { Animatable(0f) }
    val logoReveal = remember { Animatable(0f) }
    val ringCompress = remember { Animatable(0f) }
    val markProgress = remember { Animatable(0f) }
    val centerAlpha = remember { Animatable(0f) }
    val logoSettle = remember { Animatable(1.06f) }
    val textAlpha = remember { Animatable(0f) }
    val textRise = remember { Animatable(18f) }
    val textSharpness = remember { Animatable(6f) }
    val sessionReady = remember { Animatable(0f) }
    val fieldFade = remember { Animatable(1f) }
    val logoLift = remember { Animatable(0f) }
    val exitAlpha = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        // Phase 1 — Ambient darkness (~550ms): space only, no mark
        launch { depthAlpha.animateTo(1f, tween(550, easing = CalmEase)) }
        ambient.animateTo(1f, tween(550, easing = LinearEasing))
        delay(80)

        // Phase 2 — Presence pulse (~750ms): human breathing rhythm
        presence.animateTo(1f, tween(750, easing = CalmEase))
        delay(60)

        // Phase 3 — Trust field formation (~850ms)
        trustField.animateTo(1f, tween(850, easing = CalmEase))
        delay(80)

        // Phase 4 — SEDI mark emergence (~900ms): discovered, not dropped
        launch { logoReveal.animateTo(1f, tween(900, easing = SettleEase)) }
        launch { ringCompress.animateTo(1f, tween(900, easing = CalmEase)) }
        launch { centerAlpha.animateTo(1f, tween(700, easing = CalmEase)) }
        markProgress.animateTo(1f, tween(880, easing = CalmEase))
        logoSettle.animateTo(1f, tween(920, easing = SettleEase))
        delay(100)

        // Phase 5 — Typography reveal (~600ms): gentle upward fade
        launch { textAlpha.animateTo(1f, tween(580, easing = CalmEase)) }
        launch { textRise.animateTo(0f, tween(620, easing = SettleEase)) }
        textSharpness.animateTo(0f, tween(640, easing = CalmEase))
        delay(120)

        // Phase 6 — Secure session ready (~500ms)
        sessionReady.animateTo(1f, tween(480, easing = CalmEase))
        delay(180)

        // Transition — dissolve into UVU request (~700ms)
        onHandoff()
        launch { fieldFade.animateTo(0.15f, tween(650, easing = CalmEase)) }
        launch { logoLift.animateTo(-36f, tween(680, easing = CalmEase)) }
        launch { textAlpha.animateTo(0f, tween(520, easing = CalmEase)) }
        launch { logoReveal.animateTo(0.85f, tween(600, easing = CalmEase)) }
        exitAlpha.animateTo(0f, tween(680, easing = CalmEase))
        onFinished()
    }

    val drift = ambient.value
    val gradientTop = lerpColor(SediBrand.Midnight, SediBrand.Charcoal, drift * 0.4f)
    val gradientMid = lerpColor(SediBrand.Charcoal, SediBrand.Graphite, drift * 0.3f)
    val gradientBottom = lerpColor(SediBrand.Graphite, SediBrand.Navy, drift * 0.2f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(exitAlpha.value * depthAlpha.value)
            .background(Brush.verticalGradient(listOf(gradientTop, gradientMid, gradientBottom))),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            SediBrand.Teal.copy(alpha = 0.08f + drift * 0.04f),
                            Color.Transparent
                        ),
                        center = Offset(300f + drift * 14f, 240f - drift * 10f),
                        radius = 500f + drift * 20f
                    )
                )
        )

        SplashAmbientDepth(
            ambient = ambient.value * fieldFade.value,
            modifier = Modifier.alpha(fieldFade.value)
        )

        SplashPresenceField(
            presence = presence.value * fieldFade.value,
            field = trustField.value * fieldFade.value,
            sessionGlow = sessionReady.value,
            ringCompress = ringCompress.value,
            modifier = Modifier.alpha(fieldFade.value * (1f - logoReveal.value * 0.35f))
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(horizontal = 48.dp)
                .offset(y = logoLift.value.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (logoReveal.value > 0.02f) {
                    SediLogoMark(
                        size = 132.dp,
                        variant = SediLogoVariant.OnDark,
                        animated = sessionReady.value > 0.2f && logoReveal.value > 0.7f,
                        ringAlpha = logoReveal.value * trustField.value.coerceAtLeast(0.4f),
                        ringScale = logoSettle.value,
                        markProgress = markProgress.value,
                        centerAlpha = centerAlpha.value,
                        pulseStrength = sessionReady.value * 0.6f,
                        shimmerAngle = sessionReady.value,
                        modifier = Modifier.alpha(logoReveal.value)
                    )
                }
            }

            Spacer(Modifier.height(38.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .offset(y = textRise.value.dp)
                    .alpha(textAlpha.value)
            ) {
                Text(
                    SediBrand.APP_NAME,
                    modifier = Modifier.blur(textSharpness.value.dp),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 36.sp,
                        letterSpacing = 7.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = SediBrand.MistText,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    SediBrand.TAGLINE_PRIMARY,
                    modifier = Modifier.blur((textSharpness.value * 0.65f).dp),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 17.sp,
                        lineHeight = 26.sp,
                        letterSpacing = 0.25.sp
                    ),
                    color = SediBrand.MistText.copy(alpha = 0.84f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

private fun lerpColor(start: Color, end: Color, fraction: Float): Color {
    val f = fraction.coerceIn(0f, 1f)
    return Color(
        red = start.red + (end.red - start.red) * f,
        green = start.green + (end.green - start.green) * f,
        blue = start.blue + (end.blue - start.blue) * f,
        alpha = start.alpha + (end.alpha - start.alpha) * f
    )
}
