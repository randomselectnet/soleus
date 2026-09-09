package com.soleus.office.ui.motion.sahneler

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.soleus.office.ui.motion.eyeBreathFaz
import com.soleus.office.ui.motion.zeminSeridi
import com.soleus.office.ui.theme.SereneOnBackground
import com.soleus.office.ui.theme.SerenePrimary
import com.soleus.office.ui.theme.SerenePrimaryContainer
import com.soleus.office.ui.theme.SereneTertiary

/** 10. eye-202020-breath-444 — YAKIN yüz: bakış perdesi + 4-4-4 nefes dairesi. */
@Composable
fun EyeBreathSahnesi(elapsedMs: Long, modifier: Modifier = Modifier) {
    val faz = eyeBreathFaz(elapsedMs)
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        zeminSeridi(y = h * 0.90f, genislik = w)
        val bas = Offset(w * 0.38f, h * 0.42f)
        val basR = w * 0.16f
        if (faz.perde == "BAKIS") {
            // Yüz şeması + pencere/ufuk sembolü (terracotta dikdörtgen).
            drawCircle(color = SereneOnBackground, radius = basR, center = bas, style = Stroke(width = 11f))
            val pencere = Offset(w * 0.78f, h * 0.30f)
            drawRect(
                color = SereneTertiary,
                topLeft = Offset(pencere.x - 44f, pencere.y - 34f),
                size = Size(88f, 68f),
                style = Stroke(width = 8f)
            )
            // Gözler: kırpma anında nokta → yatay çizgi.
            val solGoz = Offset(bas.x - basR * 0.35f, bas.y - 6f)
            val sagGoz = Offset(bas.x + basR * 0.35f, bas.y - 6f)
            if (faz.kirpmaKapali) {
                drawLine(color = SereneOnBackground, start = Offset(solGoz.x - 10f, solGoz.y), end = Offset(solGoz.x + 10f, solGoz.y), strokeWidth = 8f, cap = StrokeCap.Round)
                drawLine(color = SereneOnBackground, start = Offset(sagGoz.x - 10f, sagGoz.y), end = Offset(sagGoz.x + 10f, sagGoz.y), strokeWidth = 8f, cap = StrokeCap.Round)
            } else {
                drawCircle(color = SereneOnBackground, radius = 7f, center = solGoz)
                drawCircle(color = SereneOnBackground, radius = 7f, center = sagGoz)
                // Bakış çizgileri (göz → ufuk).
                drawLine(color = SereneOnBackground.copy(alpha = 0.5f), start = solGoz, end = Offset(pencere.x - 44f, pencere.y), strokeWidth = 5f)
                drawLine(color = SereneOnBackground.copy(alpha = 0.5f), start = sagGoz, end = Offset(pencere.x + 44f, pencere.y), strokeWidth = 5f)
            }
        } else {
            // Nefes perdesi: yüz solar, merkez nefes dairesi belirir.
            drawCircle(color = SereneOnBackground.copy(alpha = 0.4f), radius = basR, center = bas, style = Stroke(width = 11f))
            val m = Offset(w * 0.5f, h * 0.48f)
            val r = 20f + 64f * faz.nefesYaricap
            val renk = when (faz.nefesFaz) {
                "AL" -> SerenePrimaryContainer
                "TUT" -> SerenePrimary
                else -> SereneTertiary
            }
            drawCircle(color = renk, radius = r, center = m)
            drawCircle(color = SereneOnBackground, radius = r, center = m, style = Stroke(width = 8f))
            // Tut-fazında ince dönen halka.
            if (faz.nefesFaz == "TUT") {
                rotate(degrees = (elapsedMs % 4000L) / 4000f * 360f, pivot = m) {
                    drawLine(
                        color = SereneOnBackground, start = Offset(m.x, m.y - r - 14f),
                        end = Offset(m.x, m.y - r - 30f), strokeWidth = 7f, cap = StrokeCap.Round
                    )
                }
            }
        }
    }
}
