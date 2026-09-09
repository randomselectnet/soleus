package com.soleus.office.ui.motion.sahneler

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.soleus.office.ui.motion.scapularSqueezeFaz
import com.soleus.office.ui.motion.zeminSeridi
import com.soleus.office.ui.theme.SereneOnBackground
import com.soleus.office.ui.theme.SerenePrimary
import com.soleus.office.ui.theme.SereneTertiary
import kotlin.math.PI
import kotlin.math.sin

/** 7. scapular-squeeze — ÜST kuşbakışı: simetrik yaklaşan kürek noktaları + çift-ok + nefes yayı. */
@Composable
fun ScapularSqueezeSahnesi(elapsedMs: Long, modifier: Modifier = Modifier) {
    val faz = scapularSqueezeFaz(elapsedMs)
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        zeminSeridi(y = h * 0.92f, genislik = w)
        // Omurga dikey kılavuz + üstte küçük baş.
        drawLine(
            color = SereneOnBackground, start = Offset(w * 0.5f, h * 0.28f),
            end = Offset(w * 0.5f, h * 0.85f), strokeWidth = 9f, cap = StrokeCap.Round
        )
        drawCircle(color = SereneOnBackground, radius = 18f, center = Offset(w * 0.5f, h * 0.16f), style = Stroke(width = 10f))
        // Bel kilit işareti (sabit): küçük yatay çizgi.
        drawLine(
            color = SereneOnBackground, start = Offset(w * 0.44f, h * 0.78f),
            end = Offset(w * 0.56f, h * 0.78f), strokeWidth = 9f, cap = StrokeCap.Round
        )
        // Kürek noktaları: merkeze simetrik yaklaşır.
        val x0 = w * 0.22f
        val d = w * 0.12f * faz.kurekYaklasma
        val sol = Offset(w * 0.5f - x0 + d, h * 0.45f)
        val sag = Offset(w * 0.5f + x0 - d, h * 0.45f)
        drawCircle(color = SereneOnBackground, radius = 13f, center = sol)
        drawCircle(color = SereneOnBackground, radius = 13f, center = sag)
        // Omuz çizgileri (dışa).
        drawLine(color = SereneOnBackground, start = sol, end = Offset(sol.x - 60f, sol.y + 30f), strokeWidth = 11f, cap = StrokeCap.Round)
        drawLine(color = SereneOnBackground, start = sag, end = Offset(sag.x + 60f, sag.y + 30f), strokeWidth = 11f, cap = StrokeCap.Round)
        if (faz.fazAdi == "Tut-nefes-ver") {
            // Terracotta çift-ok (birbirine bakan oklar).
            val okY = h * 0.45f
            drawLine(color = SereneTertiary, start = Offset(sol.x + 18f, okY), end = Offset(w * 0.5f - 14f, okY), strokeWidth = 8f, cap = StrokeCap.Round)
            drawLine(color = SereneTertiary, start = Offset(sag.x - 18f, okY), end = Offset(w * 0.5f + 14f, okY), strokeWidth = 8f, cap = StrokeCap.Round)
            // Göğüs yayı: nefes sinüsüyle genişler (±3 dp eşdeğeri, 4 sn periyot).
            val nefes = sin(2f * PI.toFloat() * ((elapsedMs % 4000L) / 4000f)) * 8f
            drawArc(
                color = SerenePrimary, startAngle = 60f, sweepAngle = 60f, useCenter = false,
                topLeft = Offset(w * 0.5f - 90f - nefes, h * 0.52f),
                size = Size(180f + nefes * 2f, 120f),
                style = Stroke(width = 8f, cap = StrokeCap.Round)
            )
        }
    }
}
