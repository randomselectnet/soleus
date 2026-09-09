package com.soleus.office.ui.detail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.soleus.office.ui.theme.Aksan
import com.soleus.office.ui.theme.Murekkep

/** Timer ilerlemesini 0..1 aralığına sıkıştırır. */
fun clampProgress(p: Float): Float = p.coerceIn(0f, 1f)

/**
 * Saf tekrar halkası: turuncu ilerleme yayı. Ölçek/nefes efekti YOK —
 * faz fonksiyonundaki sinüs overlay'inden gelir (tek animasyon kaynağı: elapsedMs).
 */
@Composable
fun MotionRing(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val p = clampProgress(progress)
    Canvas(modifier = modifier.size(192.dp)) {
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
