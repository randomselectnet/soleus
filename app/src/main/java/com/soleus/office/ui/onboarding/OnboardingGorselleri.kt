package com.soleus.office.ui.onboarding

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.soleus.office.ui.theme.SereneOnBackground
import com.soleus.office.ui.theme.SereneOutline
import com.soleus.office.ui.theme.SerenePrimary
import com.soleus.office.ui.theme.SerenePrimaryContainer
import com.soleus.office.ui.theme.SereneTertiary
import com.soleus.office.ui.theme.SereneTertiaryContainer

/** Sayfa-1: alçalan enerji çubuğu + sandalye silueti (statik; kırmızı alarm yok). */
@Composable
fun EnerjiCubuguGorseli(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxWidth().height(170.dp)) {
        val w = size.width
        val h = size.height
        // Giderek kısalan yatay bar (gri-mavi değil: nötr adaçayı → soluk).
        val barSayisi = 5
        repeat(barSayisi) { i ->
            val oran = 1f - i * 0.16f
            drawLine(
                color = SereneOutline.copy(alpha = 0.35f + 0.1f * (barSayisi - i).toFloat()),
                start = Offset(w * 0.12f, h * 0.22f + i * h * 0.13f),
                end = Offset(w * 0.12f + (w * 0.60f) * oran, h * 0.22f + i * h * 0.13f),
                strokeWidth = 22f,
                cap = StrokeCap.Round
            )
        }
        // Sandalye silueti (terracotta, soluk).
        val sx = w * 0.80f
        drawLine(color = SereneTertiary.copy(alpha = 0.6f), start = Offset(sx - 30f, h * 0.45f), end = Offset(sx + 30f, h * 0.45f), strokeWidth = 10f, cap = StrokeCap.Round)
        drawLine(color = SereneTertiary.copy(alpha = 0.6f), start = Offset(sx - 30f, h * 0.45f), end = Offset(sx - 30f, h * 0.15f), strokeWidth = 10f, cap = StrokeCap.Round)
        drawLine(color = SereneTertiary.copy(alpha = 0.6f), start = Offset(sx - 20f, h * 0.45f), end = Offset(sx - 20f, h * 0.85f), strokeWidth = 10f)
        drawLine(color = SereneTertiary.copy(alpha = 0.6f), start = Offset(sx + 20f, h * 0.45f), end = Offset(sx + 20f, h * 0.85f), strokeWidth = 10f)
    }
}

/**
 * Sayfa-2: canlı nefes teaser'ı — 4 sn büyü-küçül halka + ortada "1 dk".
 * Detay saatine bağlı değildir (bağımsız teaser; motion tek-saat kuralının tek istisnası).
 */
@Composable
fun NefesHalkasiTeaser(modifier: Modifier = Modifier) {
    val sonsuz = rememberInfiniteTransition(label = "nefes-teaser")
    val olcek by sonsuz.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "nefes-halka"
    )
    Canvas(modifier = modifier.fillMaxWidth().height(170.dp)) {
        val m = Offset(size.width / 2f, size.height / 2f)
        drawCircle(color = SerenePrimaryContainer, radius = 62f * olcek, center = m)
        drawCircle(color = SerenePrimary, radius = 62f * olcek, center = m, style = Stroke(width = 8f))
    }
}

/** Sayfa-3: zil → kart/tik → filiz sırası (alev değil yaprak; statik, streak baskısı yok). */
@Composable
fun UcIkonSirasi(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxWidth().height(170.dp)) {
        val h = size.height
        val w = size.width
        val y = h / 2f
        val xs = listOf(w * 0.22f, w * 0.50f, w * 0.78f)
        // Bağlantı çizgisi.
        drawLine(color = SerenePrimaryContainer, start = Offset(xs[0], y), end = Offset(xs[2], y), strokeWidth = 8f)
        // 1. Zil (hatırlatma): daire + küçük çan.
        drawCircle(color = SerenePrimaryContainer, radius = 34f, center = Offset(xs[0], y))
        drawArc(
            color = SerenePrimary, startAngle = 200f, sweepAngle = 140f, useCenter = false,
            topLeft = Offset(xs[0] - 16f, y - 18f),
            size = androidx.compose.ui.geometry.Size(32f, 30f),
            style = Stroke(width = 7f, cap = StrokeCap.Round)
        )
        drawCircle(color = SerenePrimary, radius = 5f, center = Offset(xs[0], y + 16f))
        // 2. Kart/tik (yap-geç): kart + tik.
        drawCircle(color = SerenePrimaryContainer, radius = 34f, center = Offset(xs[1], y))
        drawLine(color = SerenePrimary, start = Offset(xs[1] - 12f, y + 1f), end = Offset(xs[1] - 3f, y + 10f), strokeWidth = 8f, cap = StrokeCap.Round)
        drawLine(color = SerenePrimary, start = Offset(xs[1] - 3f, y + 10f), end = Offset(xs[1] + 14f, y - 10f), strokeWidth = 8f, cap = StrokeCap.Round)
        // 3. Filiz (streak): yumuşak yaprak.
        drawCircle(color = SerenePrimaryContainer, radius = 34f, center = Offset(xs[2], y))
        drawLine(color = SerenePrimary, start = Offset(xs[2], y + 14f), end = Offset(xs[2], y - 12f), strokeWidth = 7f, cap = StrokeCap.Round)
        drawArc(
            color = SerenePrimary, startAngle = 250f, sweepAngle = 110f, useCenter = false,
            topLeft = Offset(xs[2] - 4f, y - 22f),
            size = androidx.compose.ui.geometry.Size(30f, 22f),
            style = Stroke(width = 7f, cap = StrokeCap.Round)
        )
    }
}

/** İzin adımı: yumuşak zil + tek bildirim baloncuğu ("1 dk'lık mola?"). */
@Composable
fun ZilBaloncukGorseli(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxWidth().height(170.dp)) {
        val m = Offset(size.width * 0.42f, size.height / 2f)
        // Zil gövdesi (terracotta açık ton).
        drawCircle(color = SereneTertiaryContainer.copy(alpha = 0.4f), radius = 44f, center = m)
        drawArc(
            color = SereneTertiary, startAngle = 200f, sweepAngle = 140f, useCenter = false,
            topLeft = Offset(m.x - 22f, m.y - 24f),
            size = androidx.compose.ui.geometry.Size(44f, 40f),
            style = Stroke(width = 9f, cap = StrokeCap.Round)
        )
        drawCircle(color = SereneTertiary, radius = 7f, center = Offset(m.x, m.y + 22f))
        // Titreşim yayları (yumuşak, statik).
        drawArc(
            color = SereneOnBackground.copy(alpha = 0.3f), startAngle = -50f, sweepAngle = 100f,
            useCenter = false,
            topLeft = Offset(m.x - 52f, m.y - 44f),
            size = androidx.compose.ui.geometry.Size(104f, 88f),
            style = Stroke(width = 6f, cap = StrokeCap.Round)
        )
        // Bildirim baloncuğu.
        val b = Offset(size.width * 0.74f, size.height * 0.30f)
        drawCircle(color = SerenePrimaryContainer, radius = 40f, center = b)
        drawCircle(color = SerenePrimary, radius = 6f, center = Offset(b.x - 46f, b.y + 30f))
    }
}
