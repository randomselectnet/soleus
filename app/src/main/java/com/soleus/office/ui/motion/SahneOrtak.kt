package com.soleus.office.ui.motion

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.soleus.office.ui.theme.SereneOnBackground
import com.soleus.office.ui.theme.SerenePrimaryContainer
import com.soleus.office.ui.theme.SereneTertiary

/** Figür oran birliği: baş yarıçapı birim (r); tüm sahneler bunu kullanır. */
object FigurOranlari {
    const val BAS_R = 1f
    const val GOVDE = 3f
    const val UYLUK = 2f
    const val BALDIR = 2f
    const val KOL = 2.2f
}

/** Kadraj sözlüğü (fizyo-hareket.md §7 kamera sözlüğü). */
enum class SahneKadro {
    YAKIN_DIZ_ALTI,
    YAKIN_BILEK,
    TAM_BOY_YAN,
    TAM_BOY_ARKA_YAN,
    YAKIN_BAS_OMUZ,
    YAKIN_ONDEN,
    UST_KUSBBAKISI,
    YAKIN_EL_BILEK,
    YAKIN_YUZ,
    TAM_BOY
}

/** Adaçayı zemin şeridi: yatay kalın çizgi. */
fun DrawScope.zeminSeridi(
    y: Float,
    genislik: Float,
    baslangicX: Float = 0f,
    renk: Color = SerenePrimaryContainer,
    kalinlik: Float = 20f
) {
    drawLine(
        color = renk,
        start = Offset(baslangicX, y),
        end = Offset(baslangicX + genislik, y),
        strokeWidth = kalinlik
    )
}

/** Terracotta sandalye: oturak + sırt + 2 ayak (4 çizgi). */
fun DrawScope.sandalyeCiz(
    oturakY: Float,
    solX: Float,
    genislik: Float,
    yukseklik: Float,
    renk: Color = SereneTertiary
) {
    val kalem = 10f
    // Oturak.
    drawLine(renk, Offset(solX, oturakY), Offset(solX + genislik, oturakY), kalem)
    // Sırt (sol kenardan yukarı).
    drawLine(renk, Offset(solX, oturakY), Offset(solX, oturakY - yukseklik), kalem)
    // 2 ayak.
    drawLine(renk, Offset(solX + 8f, oturakY), Offset(solX + 8f, oturakY + yukseklik * 0.6f), kalem)
    drawLine(
        renk,
        Offset(solX + genislik - 8f, oturakY),
        Offset(solX + genislik - 8f, oturakY + yukseklik * 0.6f),
        kalem
    )
}

/** Mürekkep figür çizgisi: yuvarlak başlıklı kalın çizgi. */
fun DrawScope.figurCizgisi(
    baslangic: Offset,
    bitis: Offset,
    renk: Color = SereneOnBackground,
    kalinlik: Float = 13f
) {
    drawLine(
        color = renk,
        start = baslangic,
        end = bitis,
        strokeWidth = kalinlik,
        cap = androidx.compose.ui.graphics.StrokeCap.Round
    )
}

/** Baş dairesi: içi boş mürekkep halka. */
fun DrawScope.figurBasi(
    merkez: Offset,
    yaricap: Float,
    renk: Color = SereneOnBackground,
    kalinlik: Float = 11f
) {
    drawCircle(
        color = renk,
        radius = yaricap,
        center = merkez,
        style = Stroke(width = kalinlik)
    )
}

/** İnce yardımcı yay (gerilme/yön işareti): 3 dp eşdeğeri ince çizgi. */
fun DrawScope.yardimciYay(
    merkez: Offset,
    yaricap: Float,
    baslangicAci: Float,
    supurmeAci: Float,
    renk: Color,
    kalinlik: Float = 7f,
    alfa: Float = 1f
) {
    drawArc(
        color = renk.copy(alpha = alfa),
        startAngle = baslangicAci,
        sweepAngle = supurmeAci,
        useCenter = false,
        topLeft = Offset(merkez.x - yaricap, merkez.y - yaricap),
        size = androidx.compose.ui.geometry.Size(yaricap * 2f, yaricap * 2f),
        style = Stroke(width = kalinlik, cap = androidx.compose.ui.graphics.StrokeCap.Round)
    )
}
