package com.soleus.office.ui.motion.sahneler

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import com.soleus.office.ui.motion.ankleCircleFaz
import com.soleus.office.ui.motion.yardimciYay
import com.soleus.office.ui.motion.zeminSeridi
import com.soleus.office.ui.theme.SereneOnBackground
import com.soleus.office.ui.theme.SerenePrimary
import kotlin.math.cos
import kotlin.math.sin

/** 2. ankle-circle-calf-raise — YAKIN bilek: dairesel ayakucu + hayalet iz + yön oku. */
@Composable
fun AnkleCircleSahnesi(elapsedMs: Long, modifier: Modifier = Modifier) {
    val faz = ankleCircleFaz(elapsedMs)
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val zeminY = h * 0.85f
        zeminSeridi(y = zeminY, genislik = w)
        val merkez = Offset(w * 0.5f, h * 0.42f)
        // Bilek merkezi.
        drawCircle(color = SereneOnBackground, radius = 12f, center = merkez)
        // Bacak çizgisi (sabit, yukarı).
        drawLine(
            color = SereneOnBackground,
            start = Offset(merkez.x, merkez.y - h * 0.28f),
            end = merkez,
            strokeWidth = 13f,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )
        if (faz.altFaz == "CIFT_TOPUK") {
            // Kaldırma fazı: topuk salınımı (sahne-1 temposu).
            val kaldir = faz.topukH * h * 0.12f
            val ayak = Offset(merkez.x, merkez.y + h * 0.20f - kaldir)
            drawLine(
                color = SereneOnBackground, start = merkez, end = ayak,
                strokeWidth = 13f, cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            drawLine(
                color = SerenePrimary, start = Offset(ayak.x - 40f, ayak.y),
                end = Offset(ayak.x + 40f, ayak.y), strokeWidth = 11f,
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        } else {
            // Daire yarıçapı: ilk 2 tur 0.6R ısınma, sonra R.
            val tur = faz.daireSayaci
            val r = (if (tur < 2) 0.6f else 1f) * (w * 0.22f)
            val uc = Offset(
                merkez.x + r * cos(faz.ayakucuAcisi),
                merkez.y + r * sin(faz.ayakucuAcisi)
            )
            // Hayalet iz yayı (son turun izi, alfa 0.25).
            drawArc(
                color = SerenePrimary.copy(alpha = 0.25f),
                startAngle = 0f, sweepAngle = 300f, useCenter = false,
                topLeft = Offset(merkez.x - r, merkez.y - r),
                size = Size(r * 2f, r * 2f),
                style = Stroke(width = 7f)
            )
            // Ayak çizgisi merkez → P(t).
            drawLine(
                color = SereneOnBackground, start = merkez, end = uc,
                strokeWidth = 13f, cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            drawCircle(color = SerenePrimary, radius = 11f, center = uc)
            // Yön oku (yay + ok başı).
            yardimciYay(
                merkez = merkez, yaricap = r + 26f,
                baslangicAci = if (faz.yonIsareti > 0) 200f else -20f,
                supurmeAci = if (faz.yonIsareti > 0) 120f else -120f,
                renk = SereneOnBackground, alfa = 0.7f
            )
        }
        // Pasif ayak (soluk, bekler).
        drawCircle(
            color = SereneOnBackground.copy(alpha = 0.3f),
            radius = 10f,
            center = Offset(w * 0.16f, zeminY - 14f)
        )
    }
}
