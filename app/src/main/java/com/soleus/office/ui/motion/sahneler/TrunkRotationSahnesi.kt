package com.soleus.office.ui.motion.sahneler

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.soleus.office.ui.motion.trunkRotationFaz
import com.soleus.office.ui.motion.zeminSeridi
import com.soleus.office.ui.theme.SereneOnBackground
import com.soleus.office.ui.theme.SereneTertiary

/** 8. seated-trunk-rotation — ÜST kuşbakışı: dönen omuz çizgisi, kalça çizgisi sabit. */
@Composable
fun TrunkRotationSahnesi(elapsedMs: Long, modifier: Modifier = Modifier) {
    val faz = trunkRotationFaz(elapsedMs)
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        zeminSeridi(y = h * 0.90f, genislik = w)
        val merkez = Offset(w * 0.5f, h * 0.48f)
        // Sandalye dairesi + 4 ayak.
        drawCircle(color = SereneTertiary.copy(alpha = 0.7f), radius = w * 0.26f, center = merkez, style = Stroke(width = 10f))
        listOf(-40f, -13f, 13f, 40f).forEach { dx ->
            drawLine(
                color = SereneTertiary.copy(alpha = 0.7f),
                start = Offset(merkez.x + dx * w / 400f, merkez.y + w * 0.24f),
                end = Offset(merkez.x + dx * w / 400f, merkez.y + w * 0.30f),
                strokeWidth = 8f, cap = StrokeCap.Round
            )
        }
        // Kalça çizgisi (kalın, sabit — hiç kımıldamaz).
        drawLine(
            color = SereneOnBackground, start = Offset(merkez.x - w * 0.16f, merkez.y + 26f),
            end = Offset(merkez.x + w * 0.16f, merkez.y + 26f), strokeWidth = 16f, cap = StrokeCap.Round
        )
        // Omuz çizgisi + baş + kavuşmuş kollar birlikte döner (α ≤ 30°).
        rotate(degrees = faz.omuzAcisi, pivot = merkez) {
            drawLine(
                color = SereneOnBackground, start = Offset(merkez.x - w * 0.20f, merkez.y - 20f),
                end = Offset(merkez.x + w * 0.20f, merkez.y - 20f), strokeWidth = 13f, cap = StrokeCap.Round
            )
            drawCircle(
                color = SereneOnBackground, radius = 20f,
                center = Offset(merkez.x, merkez.y - 62f), style = Stroke(width = 10f)
            )
            // Kavuşmuş kollar: 2 çapraz çizgi.
            drawLine(
                color = SereneOnBackground, start = Offset(merkez.x - 44f, merkez.y - 34f),
                end = Offset(merkez.x + 44f, merkez.y - 6f), strokeWidth = 10f, cap = StrokeCap.Round
            )
            drawLine(
                color = SereneOnBackground, start = Offset(merkez.x + 44f, merkez.y - 34f),
                end = Offset(merkez.x - 44f, merkez.y - 6f), strokeWidth = 10f, cap = StrokeCap.Round
            )
        }
        // Merkez noktası.
        drawCircle(color = SereneOnBackground, radius = 8f, center = merkez)
        // Tutma: dönen tarafta 2 gerilme yayı (alfa nabzı).
        if (faz.fazAdi == "Tut-nefes-al") {
            val nabiz = 0.5f + 0.5f * kotlin.math.sin(2f * Math.PI.toFloat() * ((elapsedMs % 2000L) / 2000f))
            val isaret = if (faz.taraf == "SAG") 1f else -1f
            repeat(2) { i ->
                drawArc(
                    color = SereneTertiary.copy(alpha = 0.4f + 0.5f * nabiz),
                    startAngle = if (isaret > 0) -60f else 120f, sweepAngle = 60f,
                    useCenter = false,
                    topLeft = Offset(merkez.x + isaret * (40f + i * 26f) - 40f, merkez.y - 120f),
                    size = Size(80f, 160f),
                    style = Stroke(width = 7f, cap = StrokeCap.Round)
                )
            }
        }
    }
}
