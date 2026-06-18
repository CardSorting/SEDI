package gov.utah.sedi

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.sin

private data class AmbientMote(val angle: Float, val radius: Float, val size: Float, val speed: Float)

@Composable
fun SplashAmbientDepth(
    ambient: Float,
    modifier: Modifier = Modifier
) {
  val motes = remember {
        List(14) { i ->
            AmbientMote(
                angle = i * 0.45f,
                radius = 0.22f + (i % 5) * 0.08f,
                size = 1.2f + (i % 3) * 0.6f,
                speed = 0.15f + (i % 4) * 0.05f
            )
        }
    }
    val drift = ambient * 18f

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val cx = w * 0.5f
        val cy = h * 0.48f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    SediBrand.Teal.copy(alpha = 0.07f * ambient),
                    Color.Transparent
                ),
                center = Offset(cx + drift * 0.3f, cy - drift * 0.2f),
                radius = w * 0.55f
            ),
            radius = w * 0.55f,
            center = Offset(cx, cy)
        )

        motes.forEach { mote ->
            val t = ambient * mote.speed * 6.28f
            val px = cx + cos(mote.angle + t) * w * mote.radius
            val py = cy + sin(mote.angle + t * 0.7f) * h * mote.radius
            drawCircle(
                color = SediBrand.MistText.copy(alpha = 0.08f * ambient),
                radius = mote.size,
                center = Offset(px, py)
            )
        }
    }
}

@Composable
fun SplashPresenceField(
    presence: Float,
    field: Float,
    sessionGlow: Float,
    ringCompress: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.minDimension
        val cx = size.width * 0.5f
        val cy = size.height * 0.48f
        val breathe = 1f + sin(presence * 3.1415f) * 0.012f
        val compress = 1.12f - ringCompress * 0.12f

        if (presence > 0.02f) {
            val pulseR = w * 0.14f * breathe * (0.4f + presence * 0.6f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        SediBrand.TealSoft.copy(alpha = 0.22f * presence),
                        SediBrand.Teal.copy(alpha = 0.06f * presence),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy),
                    radius = pulseR
                ),
                radius = pulseR,
                center = Offset(cx, cy)
            )
        }

        if (field > 0.02f) {
            val rings = listOf(
                Triple(0.50f, 0.028f, 0.38f),
                Triple(0.42f, 0.016f, 0.24f),
                Triple(0.34f, 0.011f, 0.16f)
            )
            rings.forEachIndexed { index, (radiusFrac, strokeFrac, alphaMul) ->
                val stagger = (index * 0.08f).coerceAtMost(field)
                val ringAlpha = field * alphaMul * (1f - ringCompress * 0.75f)
                val r = w * radiusFrac * compress * breathe * (0.92f + stagger)
                drawCircle(
                    color = SediBrand.TealSoft.copy(alpha = ringAlpha),
                    radius = r,
                    center = Offset(cx, cy),
                    style = Stroke(width = w * strokeFrac, cap = StrokeCap.Round)
                )
            }
        }

        if (sessionGlow > 0.02f) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        SediBrand.SuccessSoft.copy(alpha = 0.12f * sessionGlow),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy),
                    radius = w * 0.22f
                ),
                radius = w * 0.22f,
                center = Offset(cx, cy)
            )
        }
    }
}
