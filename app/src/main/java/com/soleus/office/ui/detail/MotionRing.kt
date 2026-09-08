package com.soleus.office.ui.detail

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.soleus.office.ui.theme.Aksan
import com.soleus.office.ui.theme.Murekkep

/** Timer ilerlemesini 0..1 aralığına sıkıştırır. */
fun clampProgress(p: Float): Float = p.coerceIn(0f, 1f)

/**
 * Nefesle büyüyen hareket halkası: turuncu ilerleme yayı + nefes ölçek animasyonu.
 * Lottie entegrasyonu Task 7'de; halka şimdiden timer görselidir.
 */
@Composable
fun MotionRing(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val p = clampProgress(progress)
    val sonsuz = rememberInfiniteTransition(label = "nefes")
    val nefes by sonsuz.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "nefes-olcegi"
    )
    Canvas(
        modifier = modifier
            .size(192.dp)
            .graphicsLayer(scaleX = nefes, scaleY = nefes)
    ) {
        drawArc(
            color = Murekkep.copy(alpha = 0.15f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = 24f, cap = StrokeCap.Round)
        )
        drawArc(
            color = Aksan,
            startAngle = -90f,
            sweepAngle = 360f * p,
            useCenter = false,
            style = Stroke(width = 24f, cap = StrokeCap.Round)
        )
    }
}
