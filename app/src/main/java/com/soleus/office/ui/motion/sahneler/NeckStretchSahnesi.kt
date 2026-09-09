package com.soleus.office.ui.motion.sahneler

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.soleus.office.ui.motion.neckStretchFaz
import com.soleus.office.ui.motion.zeminSeridi
import com.soleus.office.ui.theme.SereneOnBackground
import com.soleus.office.ui.theme.SereneTertiary
import kotlin.math.cos
import kotlin.math.sin

/** 5. neck-side-stretch — YAKIN baş-omuz: ±20° eğilen baş + gerilme yayları (el yok). */
@Composable
fun NeckStretchSahnesi(elapsedMs: Long, modifier: Modifier = Modifier) {
    val faz = neckStretchFaz(elapsedMs)
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        zeminSeridi(y = h * 0.92f, genislik = w)
        // Omuz çizgisi + uç daireler (sabit).
        val omuzY = h * 0.72f
        drawLine(
            color = SereneOnBackground, start = Offset(w * 0.22f, omuzY),
            end = Offset(w * 0.78f, omuzY), strokeWidth = 13f, cap = StrokeCap.Round
        )
        drawCircle(color = SereneOnBackground, radius = 10f, center = Offset(w * 0.22f, omuzY))
        drawCircle(color = SereneOnBackground, radius = 10f, center = Offset(w * 0.78f, omuzY))
        // Merkez dikey kılavuz (kesik, alfa 0.3).
        var ky = h * 0.10f
        while (ky < omuzY) {
            drawLine(
                color = SereneOnBackground.copy(alpha = 0.3f),
                start = Offset(w * 0.5f, ky), end = Offset(w * 0.5f, ky + 12f), strokeWidth = 6f
            )
            ky += 24f
        }
        // Baş: merkez etrafında ±20° yay boyunca eğilir.
        val boyun = Offset(w * 0.5f, omuzY)
        val rad = Math.toRadians(faz.basAcisi.toDouble())
        val basR = w * 0.13f
        val basMerkez = Offset(
            (boyun.x + h * 0.30f * sin(rad)).toFloat(),
            (boyun.y - h * 0.30f * cos(rad)).toFloat()
        )
        drawLine(color = SereneOnBackground, start = boyun, end = basMerkez, strokeWidth = 12f, cap = StrokeCap.Round)
        drawCircle(color = SereneOnBackground, radius = basR, center = basMerkez, style = Stroke(width = 11f))
        // Gözler (başla birlikte döner).
        val gozDx = basR * 0.35f * cos(rad).toFloat()
        val gozDy = basR * 0.35f * sin(rad).toFloat()
        drawCircle(color = SereneOnBackground, radius = 6f, center = Offset(basMerkez.x - basR * 0.3f + gozDx, basMerkez.y - 6f + gozDy))
        drawCircle(color = SereneOnBackground, radius = 6f, center = Offset(basMerkez.x + basR * 0.3f + gozDx, basMerkez.y - 6f + gozDy))
        // Tutma: gerilen tarafta 3 terracotta yay (0.5 Hz alfa nabzı).
        if (faz.fazAdi == "Tut-nefes-al") {
            val nabiz = 0.5f + 0.5f * kotlin.math.sin(2f * Math.PI.toFloat() * ((elapsedMs % 2000L) / 2000f))
            val tarafX = if (faz.taraf == "SAG") w * 0.68f else w * 0.32f
            repeat(3) { i ->
                drawArc(
                    color = SereneTertiary.copy(alpha = 0.4f + 0.5f * nabiz),
                    startAngle = if (faz.taraf == "SAG") 200f else -20f, sweepAngle = 100f,
                    useCenter = false,
                    topLeft = Offset(tarafX - 30f - i * 14f, omuzY - 130f),
                    size = Size(60f + i * 28f, 110f),
                    style = Stroke(width = 7f, cap = StrokeCap.Round)
                )
            }
        }
        // Eller yanda sabit (kısa çizgiler).
        drawLine(color = SereneOnBackground, start = Offset(w * 0.16f, omuzY), end = Offset(w * 0.16f, omuzY + 50f), strokeWidth = 11f, cap = StrokeCap.Round)
        drawLine(color = SereneOnBackground, start = Offset(w * 0.84f, omuzY), end = Offset(w * 0.84f, omuzY + 50f), strokeWidth = 11f, cap = StrokeCap.Round)
    }
}
