package com.soleus.office.ui.motion.sahneler

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.soleus.office.ui.motion.standupMiniSetFaz
import com.soleus.office.ui.motion.zeminSeridi
import com.soleus.office.ui.theme.SereneOnBackground
import com.soleus.office.ui.theme.SerenePrimary
import com.soleus.office.ui.theme.SereneTertiary
import kotlin.math.PI
import kotlin.math.sin

/** 11. standup-mini-set — TAM-BOY: yarım-squat + yürüyüş loop (+ masa push-up ikamesi). */
@Composable
fun StandupMiniSetSahnesi(elapsedMs: Long, modifier: Modifier = Modifier) {
    val faz = standupMiniSetFaz(elapsedMs)
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val zeminY = h * 0.86f
        zeminSeridi(y = zeminY, genislik = w)
        // Sandalye (yanda) + masa çizgisi.
        drawLine(color = SereneTertiary.copy(alpha = 0.7f), start = Offset(w * 0.12f, h * 0.45f), end = Offset(w * 0.26f, h * 0.45f), strokeWidth = 10f, cap = StrokeCap.Round)
        drawLine(color = SereneTertiary.copy(alpha = 0.7f), start = Offset(w * 0.12f, h * 0.45f), end = Offset(w * 0.12f, zeminY), strokeWidth = 10f)
        drawLine(color = SereneOnBackground.copy(alpha = 0.5f), start = Offset(w * 0.70f, h * 0.40f), end = Offset(w * 0.92f, h * 0.40f), strokeWidth = 9f, cap = StrokeCap.Round)
        drawLine(color = SereneOnBackground.copy(alpha = 0.5f), start = Offset(w * 0.72f, h * 0.40f), end = Offset(w * 0.72f, zeminY), strokeWidth = 8f)
        drawLine(color = SereneOnBackground.copy(alpha = 0.5f), start = Offset(w * 0.90f, h * 0.40f), end = Offset(w * 0.90f, zeminY), strokeWidth = 8f)
        // Yarım-squat hiza çizgisi (kesik terracotta).
        val hizaY = h * 0.58f
        drawLine(
            color = SereneTertiary, start = Offset(w * 0.34f, hizaY), end = Offset(w * 0.62f, hizaY),
            strokeWidth = 6f, cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f))
        )
        when (faz.altFaz) {
            "SQUAT", "KALK", "OTUR_SU" -> {
                // Ayakta figür: diz/kalça Dmax kadar çöker (Dmax = uyluk/3 eşdeğeri).
                val cokme = if (faz.altFaz == "SQUAT") faz.cokmeOrani else 0f
                val dmax = h * 0.10f
                val bas = Offset(w * 0.48f, h * 0.16f + cokme * dmax)
                val kalca = Offset(w * 0.48f, h * 0.50f + cokme * dmax)
                val dizX = w * 0.48f // diz x sabit kilitli + dikey kılavuz.
                drawLine(
                    color = SereneOnBackground.copy(alpha = 0.35f),
                    start = Offset(dizX, h * 0.40f), end = Offset(dizX, zeminY), strokeWidth = 5f
                )
                drawCircle(color = SereneOnBackground, radius = 20f, center = bas, style = Stroke(width = 10f))
                drawLine(color = SereneOnBackground, start = Offset(bas.x, bas.y + 22f), end = kalca, strokeWidth = 12f, cap = StrokeCap.Round)
                val diz = Offset(dizX, h * 0.62f + cokme * dmax * 0.4f)
                drawLine(color = SereneOnBackground, start = kalca, end = diz, strokeWidth = 12f, cap = StrokeCap.Round)
                drawLine(color = SereneOnBackground, start = diz, end = Offset(dizX + 6f, zeminY), strokeWidth = 12f, cap = StrokeCap.Round)
                drawLine(color = SereneOnBackground, start = Offset(bas.x, bas.y + 30f), end = Offset(bas.x - 30f, bas.y + 90f), strokeWidth = 11f, cap = StrokeCap.Round)
                drawLine(color = SereneOnBackground, start = Offset(bas.x, bas.y + 30f), end = Offset(bas.x + 30f, bas.y + 90f), strokeWidth = 11f, cap = StrokeCap.Round)
            }
            "PUSHUP" -> {
                // Push-up ikamesi: eğik doğru + dirsek 90°↔160° sinüsü (~4 sn).
                val dirsek = 125f + 35f * sin(2f * PI.toFloat() * ((elapsedMs % 4000L) / 4000f))
                val bas = Offset(w * 0.72f, h * 0.50f)
                val ayak = Offset(w * 0.34f, zeminY - 8f)
                drawLine(color = SereneOnBackground, start = ayak, end = bas, strokeWidth = 12f, cap = StrokeCap.Round)
                drawCircle(color = SereneOnBackground, radius = 18f, center = Offset(bas.x + 26f, bas.y - 14f), style = Stroke(width = 10f))
                drawLine(color = SerenePrimary, start = Offset(w * 0.55f, h * 0.55f), end = Offset(w * 0.55f + dirsek * 0.4f, h * 0.62f), strokeWidth = 10f, cap = StrokeCap.Round)
            }
            else -> {
                // Yürüyüş: yatay lineer loop (2 tur eşdeğeri), bacaklar zıt-faz (1 adım/sn).
                val ilerleme = ((elapsedMs % 25000L) / 25000f)
                val x = w * 0.30f + ilerleme * w * 0.35f
                val adim = sin(2f * PI.toFloat() * ((elapsedMs % 1000L) / 1000f))
                val bas = Offset(x, h * 0.18f)
                val kalca = Offset(x, h * 0.50f)
                drawCircle(color = SereneOnBackground, radius = 20f, center = bas, style = Stroke(width = 10f))
                drawLine(color = SereneOnBackground, start = Offset(bas.x, bas.y + 22f), end = kalca, strokeWidth = 12f, cap = StrokeCap.Round)
                drawLine(color = SereneOnBackground, start = kalca, end = Offset(x - 26f, zeminY - 20f * adim), strokeWidth = 12f, cap = StrokeCap.Round)
                drawLine(color = SereneOnBackground, start = kalca, end = Offset(x + 26f, zeminY + 20f * adim), strokeWidth = 12f, cap = StrokeCap.Round)
                drawLine(color = SereneOnBackground, start = Offset(bas.x, bas.y + 30f), end = Offset(x + 24f * adim.toFloat(), bas.y + 90f), strokeWidth = 11f, cap = StrokeCap.Round)
                // "Sessiz" dalga işareti.
                drawArc(
                    color = SerenePrimary, startAngle = -40f, sweepAngle = 80f, useCenter = false,
                    topLeft = Offset(x - 60f, zeminY - 40f),
                    size = androidx.compose.ui.geometry.Size(40f, 30f),
                    style = Stroke(width = 6f, cap = StrokeCap.Round)
                )
            }
        }
    }
}
