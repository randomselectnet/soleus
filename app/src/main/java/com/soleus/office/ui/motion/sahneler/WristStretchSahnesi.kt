package com.soleus.office.ui.motion.sahneler

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.soleus.office.ui.motion.wristStretchFaz
import com.soleus.office.ui.motion.zeminSeridi
import com.soleus.office.ui.theme.SereneOnBackground
import com.soleus.office.ui.theme.SerenePrimary
import com.soleus.office.ui.theme.SereneTertiary
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** 9. wrist-forearm-stretch — YAKIN el-bilek: ±30° el + destek eli + yumruk↔yıldız pompa. */
@Composable
fun WristStretchSahnesi(elapsedMs: Long, modifier: Modifier = Modifier) {
    val faz = wristStretchFaz(elapsedMs)
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        zeminSeridi(y = h * 0.88f, genislik = w)
        val bilek = Offset(w * 0.42f, h * 0.48f)
        // Önkol (yatay sabit).
        drawLine(
            color = SereneOnBackground, start = Offset(w * 0.10f, bilek.y),
            end = bilek, strokeWidth = 14f, cap = StrokeCap.Round
        )
        // Bilek menteşe dairesi.
        drawCircle(color = SereneOnBackground, radius = 11f, center = bilek)
        if (faz.altFaz == "POMPA") {
            // Pompa: ~1 Hz sinüs — yumruk (daire) ↔ yıldız (5 çizgi).
            val ritim = sin(2f * PI.toFloat() * ((elapsedMs % 1000L) / 1000f))
            if (ritim > 0f) {
                drawCircle(color = SereneOnBackground, radius = 22f, center = Offset(bilek.x + 90f, bilek.y), style = Stroke(width = 11f))
            } else {
                val m = Offset(bilek.x + 90f, bilek.y)
                repeat(5) { i ->
                    val a = (2f * PI.toFloat() * i / 5f) - PI.toFloat() / 2f
                    drawLine(
                        color = SereneOnBackground,
                        start = m, end = Offset(m.x + 34f * cos(a), m.y + 34f * sin(a)),
                        strokeWidth = 9f, cap = StrokeCap.Round
                    )
                }
            }
            // Pompa sayacı işareti: küçük terracotta nokta sırası yerine tek halka.
            drawCircle(color = SereneTertiary, radius = 10f, center = Offset(w * 0.88f, h * 0.20f))
        } else {
            // Germe: el bilek etrafında β açısında.
            rotate(degrees = -faz.elAcisi, pivot = bilek) {
                drawLine(
                    color = SereneOnBackground, start = bilek,
                    end = Offset(bilek.x + w * 0.28f, bilek.y),
                    strokeWidth = 13f, cap = StrokeCap.Round
                )
            }
            // Yardımcı el (destek işareti): parmak ucuna dokunan küçük çizgi.
            val rad = Math.toRadians(faz.elAcisi.toDouble())
            val parmak = Offset(
                (bilek.x + w * 0.28f * cos(rad)).toFloat(),
                (bilek.y - w * 0.28f * sin(rad)).toFloat()
            )
            drawLine(
                color = SereneOnBackground.copy(alpha = 0.6f),
                start = Offset(parmak.x + 30f, parmak.y - 44f), end = parmak,
                strokeWidth = 9f, cap = StrokeCap.Round
            )
            // 2 terracotta gerilme yayı (bilek–önkol arası).
            repeat(2) { i ->
                drawArc(
                    color = SereneTertiary, startAngle = -50f, sweepAngle = 100f,
                    useCenter = false,
                    topLeft = Offset(bilek.x - 44f - i * 16f, bilek.y - 44f - i * 16f),
                    size = Size(88f + i * 32f, 88f + i * 32f),
                    style = Stroke(width = 7f, cap = StrokeCap.Round)
                )
            }
            // Taraf etiketi yerine taraf noktası.
            val solMu = faz.altFaz.startsWith("SOL")
            drawCircle(
                color = SerenePrimary, radius = 10f,
                center = if (solMu) Offset(w * 0.10f, h * 0.20f) else Offset(w * 0.90f, h * 0.20f)
            )
        }
    }
}
