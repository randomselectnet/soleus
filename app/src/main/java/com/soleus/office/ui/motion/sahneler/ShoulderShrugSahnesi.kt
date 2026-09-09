package com.soleus.office.ui.motion.sahneler

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.soleus.office.ui.motion.shoulderShrugFaz
import com.soleus.office.ui.motion.zeminSeridi
import com.soleus.office.ui.theme.SereneOnBackground
import com.soleus.office.ui.theme.SerenePrimary
import kotlin.math.cos
import kotlin.math.sin

/** 6. shoulder-shrug-roll — YAKIN önden: dikey silkme + basık elips daireler. */
@Composable
fun ShoulderShrugSahnesi(elapsedMs: Long, modifier: Modifier = Modifier) {
    val faz = shoulderShrugFaz(elapsedMs)
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        zeminSeridi(y = h * 0.92f, genislik = w)
        // Baş (sabit).
        val bas = Offset(w * 0.5f, h * 0.22f)
        drawCircle(color = SereneOnBackground, radius = w * 0.11f, center = bas, style = Stroke(width = 11f))
        // Merkez kılavuz.
        drawLine(
            color = SereneOnBackground.copy(alpha = 0.3f),
            start = Offset(w * 0.5f, h * 0.10f), end = Offset(w * 0.5f, h * 0.85f), strokeWidth = 6f
        )
        val tabanY = h * 0.55f
        val kaldir = if (faz.altFaz == "SILKME") faz.omuzY * h * 0.10f else 0f
        val sol = Offset(w * 0.30f, tabanY - kaldir)
        val sag = Offset(w * 0.70f, tabanY - kaldir)
        if (faz.altFaz == "SILKME") {
            // Omuz noktaları + tutmada yatay vurgu çizgisi.
            drawCircle(color = SereneOnBackground, radius = 12f, center = sol)
            drawCircle(color = SereneOnBackground, radius = 12f, center = sag)
            if (faz.omuzY > 0.95f) {
                drawLine(
                    color = SerenePrimary, start = Offset(sol.x - 30f, sol.y - 22f),
                    end = Offset(sag.x + 30f, sag.y - 22f), strokeWidth = 8f, cap = StrokeCap.Round
                )
            }
            // Kollar pasif sarkar.
            drawLine(color = SereneOnBackground, start = sol, end = Offset(sol.x - 14f, sol.y + 90f), strokeWidth = 11f, cap = StrokeCap.Round)
            drawLine(color = SereneOnBackground, start = sag, end = Offset(sag.x + 14f, sag.y + 90f), strokeWidth = 11f, cap = StrokeCap.Round)
        } else {
            // Daire: basık elips (Rx > Ry), ~3 sn/tur; soluk tam-elips iz.
            val rx = w * 0.10f
            val ry = h * 0.06f
            listOf(sol, sag).forEach { m ->
                drawArc(
                    color = SereneOnBackground.copy(alpha = 0.2f),
                    startAngle = 0f, sweepAngle = 360f, useCenter = false,
                    topLeft = Offset(m.x - rx, tabanY - ry),
                    size = Size(rx * 2f, ry * 2f),
                    style = Stroke(width = 6f)
                )
                val p = Offset(
                    m.x + rx * cos(faz.daireAcisi),
                    tabanY + ry * sin(faz.daireAcisi)
                )
                drawCircle(color = SerenePrimary, radius = 12f, center = p)
                drawLine(color = SereneOnBackground, start = p, end = Offset(p.x, p.y + 80f), strokeWidth = 11f, cap = StrokeCap.Round)
            }
        }
    }
}
