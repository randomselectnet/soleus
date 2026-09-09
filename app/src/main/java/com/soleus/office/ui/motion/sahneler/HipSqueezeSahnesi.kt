package com.soleus.office.ui.motion.sahneler

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import com.soleus.office.ui.motion.hipSqueezeFaz
import com.soleus.office.ui.motion.sandalyeCiz
import com.soleus.office.ui.motion.zeminSeridi
import com.soleus.office.ui.theme.SereneOnBackground
import com.soleus.office.ui.theme.SerenePrimary
import kotlin.math.PI
import kotlin.math.sin

/** 3. hip-squeeze — TAM-BOY arka-yan: daralan kalça yayları + nefes halkası (sandalye titremez). */
@Composable
fun HipSqueezeSahnesi(elapsedMs: Long, modifier: Modifier = Modifier) {
    val faz = hipSqueezeFaz(elapsedMs)
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val zeminY = h * 0.85f
        zeminSeridi(y = zeminY, genislik = w)
        // Sandalye (sabit, titremez).
        sandalyeCiz(oturakY = h * 0.55f, solX = w * 0.30f, genislik = w * 0.34f, yukseklik = h * 0.25f)
        // Oturmuş iskelet (hareketsiz: kalça noktası sabit).
        val kalca = Offset(w * 0.47f, h * 0.50f)
        val bas = Offset(w * 0.47f, h * 0.16f)
        drawCircle(color = SereneOnBackground, radius = 22f, center = bas, style = Stroke(width = 11f))
        drawLine(
            color = SereneOnBackground, start = Offset(bas.x, bas.y + 24f), end = kalca,
            strokeWidth = 13f, cap = androidx.compose.ui.graphics.StrokeCap.Round
        )
        drawLine(
            color = SereneOnBackground, start = kalca, end = Offset(w * 0.62f, zeminY),
            strokeWidth = 13f, cap = androidx.compose.ui.graphics.StrokeCap.Round
        )
        // Kalça eşmerkezli 2 yay: kasılmayla içe daralır.
        val r0 = 64f
        val r = r0 * faz.yariCapCarpani
        drawArc(
            color = SerenePrimary, startAngle = 20f, sweepAngle = 140f, useCenter = false,
            topLeft = Offset(kalca.x - r, kalca.y - r),
            size = androidx.compose.ui.geometry.Size(r * 2f, r * 2f),
            style = Stroke(width = 8f)
        )
        drawArc(
            color = SerenePrimary.copy(alpha = 0.5f), startAngle = 20f, sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(kalca.x - r - 18f, kalca.y - r - 18f),
            size = androidx.compose.ui.geometry.Size((r + 18f) * 2f, (r + 18f) * 2f),
            style = Stroke(width = 7f)
        )
        // Göğüs yanı nefes halkası (4 sn döngü).
        val nefes = 0.5f + 0.5f * sin(2f * PI.toFloat() * ((elapsedMs % 4000L) / 4000f))
        val gogus = Offset(w * 0.60f, h * 0.32f)
        drawCircle(
            color = SerenePrimary.copy(alpha = 0.45f),
            radius = 16f + 10f * nefes, center = gogus, style = Stroke(width = 7f)
        )
    }
}
