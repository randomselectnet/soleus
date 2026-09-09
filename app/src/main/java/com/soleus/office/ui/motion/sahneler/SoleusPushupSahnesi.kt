package com.soleus.office.ui.motion.sahneler

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import com.soleus.office.ui.motion.soleusPushupFaz
import com.soleus.office.ui.motion.zeminSeridi
import com.soleus.office.ui.theme.SereneOnBackground
import com.soleus.office.ui.theme.SereneTertiary
import kotlin.math.cos
import kotlin.math.sin

/** 1. soleus-pushup — YAKIN diz-altı: pivot etrafında dönen ayak tabanı + topuk vurgusu. */
@Composable
fun SoleusPushupSahnesi(elapsedMs: Long, modifier: Modifier = Modifier) {
    val faz = soleusPushupFaz(elapsedMs)
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val zeminY = h * 0.82f
        zeminSeridi(y = zeminY, genislik = w)
        // Diz menteşesi (sabit).
        val diz = Offset(w * 0.32f, h * 0.30f)
        drawCircle(color = SereneOnBackground, radius = 12f, center = diz)
        // Parmak-ucu pivotu (sabit üçgen işaret).
        val pivot = Offset(w * 0.55f, zeminY)
        val ucgen = listOf(
            pivot,
            Offset(pivot.x - 14f, pivot.y - 20f),
            Offset(pivot.x + 14f, pivot.y - 20f)
        )
        drawPath(
            path = androidx.compose.ui.graphics.Path().apply {
                moveTo(ucgen[0].x, ucgen[0].y)
                lineTo(ucgen[1].x, ucgen[1].y)
                lineTo(ucgen[2].x, ucgen[2].y)
                close()
            },
            color = SereneOnBackground
        )
        // Ayak tabanı: pivot etrafında döner (maks ~35°).
        val aci = Math.toRadians((faz.topukH * 35.0)).toFloat()
        val tabanBoy = w * 0.30f
        val topuk = Offset(
            pivot.x - tabanBoy * cos(aci),
            pivot.y - tabanBoy * sin(aci) - 6f
        )
        drawLine(
            color = SereneOnBackground,
            start = pivot,
            end = topuk,
            strokeWidth = 13f,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )
        // Baldır: diz → topuk (kısalıp dikleşir).
        drawLine(
            color = SereneOnBackground,
            start = diz,
            end = topuk,
            strokeWidth = 13f,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )
        // Terracotta mini sandalye ayağı (arka plan).
        drawLine(
            color = SereneTertiary.copy(alpha = 0.55f),
            start = Offset(w * 0.12f, h * 0.35f),
            end = Offset(w * 0.12f, zeminY),
            strokeWidth = 10f
        )
        // Tutma vurgusu: üst noktada terracotta nabız.
        if (faz.fazAdi == "Tut") {
            drawCircle(color = SereneTertiary, radius = 14f, center = topuk)
        }
    }
}
