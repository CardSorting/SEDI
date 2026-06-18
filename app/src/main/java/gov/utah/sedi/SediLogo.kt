package gov.utah.sedi

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.RepeatMode

enum class SediLogoVariant {
    OnDark,
    OnLight
}

/**
 * Soft Approval Ring — iconic trust symbol.
 * Circular presence ring, grounded center point, resolving approval check.
 */
@Composable
fun SediLogoMark(
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    variant: SediLogoVariant = SediLogoVariant.OnDark,
    animated: Boolean = false,
    ringAlpha: Float = 1f,
    ringScale: Float = 1f,
    markProgress: Float = 1f,
    centerAlpha: Float = 1f,
    pulseStrength: Float = 0f,
    shimmerAngle: Float = 0f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sediAmbient")
    val breath by infiniteTransition.animateFloat(
        initialValue = 0.992f,
        targetValue = 1.008f,
        animationSpec = infiniteRepeatable(tween(3600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "breath"
    )
    val scale = if (animated) breath else 1f
    val ringPulse = ringScale * (1f + pulseStrength * 0.035f)

    val ringPrimary = when (variant) {
        SediLogoVariant.OnDark -> SediBrand.TealSoft
        SediLogoVariant.OnLight -> SediBrand.Teal
    }
    val ringSecondary = when (variant) {
        SediLogoVariant.OnDark -> SediBrand.TealGlow
        SediLogoVariant.OnLight -> SediBrand.TealSoft
    }
    val portalFill = when (variant) {
        SediLogoVariant.OnDark -> Color.White.copy(alpha = 0.06f)
        SediLogoVariant.OnLight -> SediBrand.Teal.copy(alpha = 0.07f)
    }
    val checkColor = SediBrand.SuccessSoft
    val anchorColor = when (variant) {
        SediLogoVariant.OnDark -> SediBrand.MistText
        SediLogoVariant.OnLight -> SediBrand.Teal
    }

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size * scale)) {
            val w = this.size.width
            val cx = w * 0.5f
            val cy = w * 0.5f
            val outerR = w * 0.46f * ringPulse
            val midR = w * 0.38f * ringPulse
            val innerR = w * 0.28f * ringPulse

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(portalFill, Color.Transparent),
                    center = Offset(cx, cy),
                    radius = innerR * 1.15f
                ),
                radius = innerR * 1.15f,
                center = Offset(cx, cy),
                alpha = ringAlpha * centerAlpha
            )

            drawCircle(
                color = ringPrimary.copy(alpha = ringAlpha * 0.14f),
                radius = outerR,
                center = Offset(cx, cy),
                style = Stroke(width = w * 0.008f)
            )
            drawCircle(
                color = ringPrimary.copy(alpha = ringAlpha * 0.42f),
                radius = outerR * 0.96f,
                center = Offset(cx, cy),
                style = Stroke(width = w * 0.028f, cap = StrokeCap.Round)
            )
            drawCircle(
                color = ringSecondary.copy(alpha = ringAlpha * 0.22f),
                radius = midR,
                center = Offset(cx, cy),
                style = Stroke(width = w * 0.014f, cap = StrokeCap.Round)
            )

            if (shimmerAngle > 0f && ringAlpha > 0.4f) {
                drawCircle(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color.Transparent,
                            SediBrand.TealGlow.copy(alpha = 0.2f * shimmerAngle),
                            Color.Transparent
                        ),
                        center = Offset(cx, cy)
                    ),
                    radius = outerR * 0.98f,
                    center = Offset(cx, cy),
                    style = Stroke(width = w * 0.018f, cap = StrokeCap.Round)
                )
            }

            val anchorY = cy + innerR * 0.22f
            drawCircle(
                color = anchorColor.copy(alpha = centerAlpha * 0.35f),
                radius = w * 0.07f,
                center = Offset(cx, anchorY)
            )
            drawCircle(
                color = anchorColor.copy(alpha = centerAlpha * 0.95f),
                radius = w * 0.024f,
                center = Offset(cx, anchorY)
            )

            val check = Path().apply {
                moveTo(cx - innerR * 0.42f, cy - innerR * 0.02f)
                lineTo(cx - innerR * 0.12f, cy + innerR * 0.32f)
                lineTo(cx + innerR * 0.48f, cy - innerR * 0.36f)
            }
            drawPath(
                path = trimPath(check, markProgress),
                color = checkColor.copy(alpha = 0.95f),
                style = Stroke(width = w * 0.038f, cap = StrokeCap.Round)
            )

            if (markProgress > 0.55f) {
                val stemAlpha = ((markProgress - 0.55f) / 0.45f).coerceIn(0f, 1f) * centerAlpha
                drawLine(
                    color = ringSecondary.copy(alpha = stemAlpha * 0.35f),
                    start = Offset(cx, anchorY - w * 0.03f),
                    end = Offset(cx, cy + innerR * 0.08f),
                    strokeWidth = w * 0.012f,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

private fun trimPath(path: Path, progress: Float): Path {
    if (progress >= 1f) return path
    if (progress <= 0f) return Path()
    val measure = PathMeasure()
    measure.setPath(path, false)
    val trimmed = Path()
    measure.getSegment(0f, measure.length * progress.coerceIn(0f, 1f), trimmed, true)
    return trimmed
}
