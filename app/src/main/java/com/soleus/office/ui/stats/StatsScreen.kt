package com.soleus.office.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.soleus.office.data.db.SessionLog
import com.soleus.office.domain.calcStreak
import com.soleus.office.ui.theme.Aksan
import com.soleus.office.ui.theme.Murekkep
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
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

fun dayShortTr(day: DayOfWeek): String = when (day) {
    DayOfWeek.MONDAY -> "Pzt"
    DayOfWeek.TUESDAY -> "Sal"
    DayOfWeek.WEDNESDAY -> "Çar"
    DayOfWeek.THURSDAY -> "Per"
    DayOfWeek.FRIDAY -> "Cum"
    DayOfWeek.SATURDAY -> "Cmt"
    DayOfWeek.SUNDAY -> "Paz"
}

/**
 * İstatistik ekranı: büyük serif streak sayısı + haftalık 7 bar.
 * Veri LogDao'dan NavGraph üzerinden [streak] ve [weeklyCounts] olarak alınır.
 */
@Composable
fun StatsScreen(
    streak: Int,
    weeklyCounts: List<Int>,
    weekLabels: List<String> = (6 downTo 0).map {
        dayShortTr(LocalDate.now().minusDays(it.toLong()).dayOfWeek)
    }
) {
    val counts = (weeklyCounts + List(7) { 0 }).take(7)
    val labels = (weekLabels + List(7) { "" }).take(7)
    val max = counts.maxOrNull()?.coerceAtLeast(1) ?: 1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "İstatistik",
            style = MaterialTheme.typography.displayLarge
        )
        Text(
            text = "$streak",
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 72.sp)
        )
        Text(
            text = "günlük seri",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Son 7 gün",
            style = MaterialTheme.typography.titleLarge
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            counts.forEachIndexed { i, count ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "$count",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Box(
                        modifier = Modifier
                            .height(160.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .width(28.dp)
                                .height((24 + 136 * count / max).dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (count > 0) Aksan
                                    else Murekkep.copy(alpha = 0.15f)
                                )
                        )
                    }
                    Text(
                        text = labels[i],
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Her gün küçük bir hareket, uzun bir seri.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
