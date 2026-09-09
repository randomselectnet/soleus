package com.soleus.office.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.soleus.office.data.db.SessionLog
import com.soleus.office.domain.calcStreak
import com.soleus.office.ui.theme.Quicksand
import com.soleus.office.ui.theme.SereneCard
import com.soleus.office.ui.theme.SereneOnTertiaryContainer
import com.soleus.office.ui.theme.SerenePrimary
import com.soleus.office.ui.theme.SereneTertiaryContainer
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

/** Log listesinden bugüne biten son 7 günün günlük tamamlanma sayıları (en eski -> en yeni). */
fun last7DayCounts(
    logs: List<SessionLog>,
    today: LocalDate,
    zone: ZoneId = ZoneId.systemDefault()
): List<Int> {
    val days = (6 downTo 0).map { today.minusDays(it.toLong()) }
    val counts = IntArray(7)
    for (log in logs) {
        val day = Instant.ofEpochMilli(log.timestampMillis).atZone(zone).toLocalDate()
        val idx = days.indexOf(day)
        if (idx >= 0) counts[idx]++
    }
    return counts.toList()
}

/** Log listesinden streak (ardışık gün serisi). */
fun streakFromLogs(
    logs: List<SessionLog>,
    today: LocalDate,
    zone: ZoneId = ZoneId.systemDefault()
): Int {
    val days = logs.map {
        Instant.ofEpochMilli(it.timestampMillis).atZone(zone).toLocalDate().toString()
    }.toSet()
    return calcStreak(days, today.toString())
}

/** Ay toplamı: verilen aydaki log sayısı. */
fun ayToplami(
    logs: List<SessionLog>,
    ay: YearMonth,
    zone: ZoneId = ZoneId.systemDefault()
): Int = logs.count {
    YearMonth.from(Instant.ofEpochMilli(it.timestampMillis).atZone(zone).toLocalDate()) == ay
}

enum class GunDurumu { TAMAM, KISMI, DINLENME }

/** Günlük tamamlanma sayısından durum: 0 dinlenme, 1 kısmi, 2+ tamam. */
fun gunDurumu(sayi: Int): GunDurumu = when {
    sayi >= 2 -> GunDurumu.TAMAM
    sayi == 1 -> GunDurumu.KISMI
    else -> GunDurumu.DINLENME
}

/** Takvim hücresi: null = öndeki boşluk (ayın ilk günü Pazartesi değilse). */
data class GunHucresi(val gun: Int, val durum: GunDurumu)

/**
 * Ay takvimi hücreleri: Pazartesi-başlangıçlı ızgara için öndeki boşluklar (null)
 * + gün hücreleri. Gelecek günler dinlenme renginde gösterilir.
 */
fun ayHucreleri(
    logs: List<SessionLog>,
    ay: YearMonth,
    today: LocalDate,
    zone: ZoneId = ZoneId.systemDefault()
): List<GunHucresi?> {
    val sayilar = mutableMapOf<LocalDate, Int>()
    for (log in logs) {
        val gun = Instant.ofEpochMilli(log.timestampMillis).atZone(zone).toLocalDate()
        if (YearMonth.from(gun) == ay) sayilar[gun] = (sayilar[gun] ?: 0) + 1
    }
    val hucreler = mutableListOf<GunHucresi?>()
    // Pazartesi = 0 ön boşluk (DayOfWeek.MONDAY.value == 1).
    val onBosluk = (ay.atDay(1).dayOfWeek.value - DayOfWeek.MONDAY.value + 7) % 7
    repeat(onBosluk) { hucreler.add(null) }
    for (gun in 1..ay.lengthOfMonth()) {
        val tarih = ay.atDay(gun)
        val durum = if (tarih > today) GunDurumu.DINLENME
        else gunDurumu(sayilar[tarih] ?: 0)
        hucreler.add(GunHucresi(gun, durum))
    }
    return hucreler
}

fun dayShortTr(day: DayOfWeek): String = when (day) {
    DayOfWeek.MONDAY -> "Pzt"
    DayOfWeek.TUESDAY -> "Sal"
    DayOfWeek.WEDNESDAY -> "Çar"
    DayOfWeek.THURSDAY -> "Per"
    DayOfWeek.FRIDAY -> "Cum"
    DayOfWeek.SATURDAY -> "Cmt"
    DayOfWeek.SUNDAY -> "Paz"
}

private val GUN_BASLIKLARI = listOf("P", "S", "Ç", "P", "C", "C", "P")

@Composable
private fun GunDairesi(hucre: GunHucresi?) {
    if (hucre == null) {
        Spacer(modifier = Modifier.size(40.dp))
        return
    }
    val zemin = when (hucre.durum) {
        GunDurumu.TAMAM -> SerenePrimary
        GunDurumu.KISMI -> SereneTertiaryContainer
        GunDurumu.DINLENME -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    }
    val yazi = when (hucre.durum) {
        GunDurumu.TAMAM -> Color.White
        GunDurumu.KISMI -> SereneOnTertiaryContainer
        GunDurumu.DINLENME -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(zemin),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "${hucre.gun}",
            style = MaterialTheme.typography.labelMedium,
            color = yazi
        )
    }
}

@Composable
private fun LegendNoktasi(renk: Color, etiket: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(renk)
        )
        Text(
            text = etiket,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Geçmiş ekranı (your_journey): başlık + ay özeti + gün-daire takvimi + alıntı.
 * Gün verisi LogDao'dan gelir (yeşil = tamamlandı, terracotta = kısmi, gri = dinlenme).
 */
@Composable
fun StatsScreen(
    monthTitle: String,
    monthCount: Int,
    monthCells: List<GunHucresi?>,
    streak: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Yolculuğun",
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = Quicksand
            )
            Text(
                text = "Her küçük adım sayılır. Her gün sessiz bir ivme biriktiriyorsun.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        SereneCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = 24.dp
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(SerenePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "BU AY",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$monthCount nazik hatırlatma",
                            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 22.sp),
                            fontFamily = Quicksand
                        )
                    }
                }
                Text(
                    text = "Bu ay kendine öncelik verdiğin $monthCount an birikti. " +
                        "Güzel bir özen birikimi.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = monthTitle,
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 22.sp),
                fontFamily = Quicksand,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "$streak gündür devam ›",
                style = MaterialTheme.typography.labelMedium,
                color = SerenePrimary
            )
        }

        SereneCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = 20.dp
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    GUN_BASLIKLARI.forEach { baslik ->
                        Text(
                            text = baslik,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((((monthCells.size + 6) / 7).coerceAtLeast(1) * 48).dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    userScrollEnabled = false
                ) {
                    itemsIndexed(monthCells, key = { index, hucre ->
                        "$index-${hucre?.gun ?: 0}-${hucre?.durum}"
                    }) { _, hucre ->
                        Box(
                            modifier = Modifier.aspectRatio(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            GunDairesi(hucre)
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        16.dp,
                        Alignment.CenterHorizontally
                    )
                ) {
                    LegendNoktasi(SerenePrimary, "Tamamlandı")
                    LegendNoktasi(SereneTertiaryContainer, "Kısmi")
                    LegendNoktasi(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        "Dinlenme"
                    )
                }
            }
        }

        SereneCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = 24.dp
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(
                    imageVector = Icons.Filled.Spa,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(28.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Nazik bir hatırlatma",
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp),
                        fontFamily = Quicksand
                    )
                    Text(
                        text = "Tutarlılık mükemmel olmak değil; ara versen bile " +
                            "kendine dönmektir. Boş günler sadece nefes alacak yerdir.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
