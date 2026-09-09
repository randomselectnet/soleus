package com.soleus.office.ui.motion.sahneler

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.soleus.office.ui.motion.legExtensionFaz
import com.soleus.office.ui.motion.sandalyeCiz
import com.soleus.office.ui.motion.zeminSeridi
import com.soleus.office.ui.theme.SereneOnBackground
import com.soleus.office.ui.theme.SerenePrimary
import kotlin.math.cos
import kotlin.math.sin

/** 4. seated-leg-extension — TAM-BOY yan: diz menteşesinde dönen baldır + kuadriseps çizgileri. */
@Composable
fun LegExtensionSahnesi(elapsedMs: Long, modifier: Modifier = Modifier) {
    val faz = legExtensionFaz(elapsedMs)
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val zeminY = h * 0.85f
        zeminSeridi(y = zeminY, genislik = w)
        sandalyeCiz(oturakY = h * 0.52f, solX = w * 0.18f, genislik = w * 0.30f, yukseklik = h * 0.24f)
        // Baş + gövde (sabit).
        val bas = Offset(w * 0.30f, h * 0.14f)
        drawCircle(color = SereneOnBackground, radius = 22f, center = bas, style = Stroke(width = 11f))
        val kalca = Offset(w * 0.30f, h * 0.50f)
        drawLine(color = SereneOnBackground, start = Offset(bas.x, bas.y + 24f), end = kalca, strokeWidth = 13f, cap = StrokeCap.Round)
        // Uyluk (yatay sabit).
        val diz = Offset(w * 0.54f, h * 0.50f)
        drawLine(color = SereneOnBackground, start = kalca, end = diz, strokeWidth = 13f, cap = StrokeCap.Round)
        // Pasif bacak (soluk, sarkık).
        drawLine(
            color = SereneOnBackground.copy(alpha = 0.35f),
            start = diz, end = Offset(diz.x + 10f, zeminY),
            strokeWidth = 12f, cap = StrokeCap.Round
        )
        // Aktif baldır: dizeAcisi 90° (aşağı) → 0° (yatay).
        val rad = Math.toRadians(faz.dizeAcisi.toDouble())
        val boy = h * 0.32f
        val bilek = Offset(
            (diz.x + boy * sin(rad)).toFloat(),
            (diz.y + boy * cos(rad)).toFloat()
        )
        drawLine(color = SereneOnBackground, start = diz, end = bilek, strokeWidth = 13f, cap = StrokeCap.Round)
        // Ayak: baldıra dik 90° sabit (kısa çizgi).
        drawLine(
            color = SereneOnBackground,
            start = Offset(bilek.x - 26f, bilek.y), end = Offset(bilek.x + 6f, bilek.y),
            strokeWidth = 11f, cap = StrokeCap.Round
        )
        // Tutma: 3 terracotta kuadriseps çizgisi (uyluk üstü).
        if (faz.fazAdi == "Tut") {
            val ortaX = (kalca.x + diz.x) / 2f
            repeat(3) { i ->
                val x = ortaX - 28f + i * 28f
                drawLine(
                    color = SerenePrimary, start = Offset(x, kalca.y - 36f),
                    end = Offset(x, kalca.y - 8f), strokeWidth = 8f, cap = StrokeCap.Round
                )
            }
        }
        // Aktif taraf işareti.
        drawCircle(
            color = SerenePrimary, radius = 10f,
            center = if (faz.taraf == "SOL") Offset(w * 0.10f, h * 0.20f) else Offset(w * 0.90f, h * 0.20f)
        )
    }
}
