package com.soleus.office.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.soleus.office.data.model.Exercise
import com.soleus.office.ui.motion.HareketSahnesi
import com.soleus.office.ui.motion.fazOzeti
import com.soleus.office.ui.motion.rememberSahneSaati
import com.soleus.office.ui.theme.Quicksand

/** Saniyeyi mm:ss biçimine çevirir. */
fun formatSure(toplamSaniye: Int): String {
    val s = toplamSaniye.coerceAtLeast(0)
    return "%02d:%02d".format(s / 60, s % 60)
}

/**
 * Hareket detayı (Serene, bölüm kartları): başlık + rozetler → sahne →
 * sayaç (mini dikkat şeridi) → nasıl yapılır → fayda (+bilgi linki) →
 * dikkat → BİTİR + güvenlik dipnotu.
 *
 * Animasyon + metrik + sayaç TEK [rememberSahneSaati] saatinden beslenir (çift saat yok).
 */
@Composable
fun ExerciseDetailScreen(
    exercise: Exercise,
    onDone: () -> Unit,
    onOpenBilgi: (String) -> Unit = {}
) {
    val toplam = exercise.durationSec.coerceAtLeast(1)
    var calisiyor by remember(exercise.id) { mutableStateOf(false) }
    var sifirlama by remember(exercise.id) { mutableIntStateOf(0) }
    // TEK saat: key değişince (bitiş sonrası yeniden başlatma) sıfırlanır.
    val elapsedMs = key(exercise.id, sifirlama) { rememberSahneSaati(calisiyor = calisiyor) }
    val toplamKalanSn = (toplam - (elapsedMs / 1000).toInt()).coerceAtLeast(0)
    LaunchedEffect(elapsedMs, toplam) {
        if (elapsedMs >= toplam * 1000L) calisiyor = false
    }
    val faz = remember(exercise.id, elapsedMs) { fazOzeti(exercise.id, elapsedMs) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Başlık bloğu (kart değil): ad + rozet satırı.
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = exercise.trName,
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = Quicksand,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DozajRozeti(etiket = exercise.dozajEtiket)
                ZorlukRozeti(zorluk = exercise.zorluk)
            }
        }
        // 2. Sahne (prosedürel, tek saatten).
        HareketSahnesi(
            id = exercise.id,
            elapsedMs = elapsedMs
        )
        // 3. Sayaç (aynı fazdan: faz adı + sayaç metni + tekrar halkası + toplam kalan).
        SayacKarti(
            fazAdi = faz.fazAdi,
            sayacMetni = faz.sayacMetni,
            tekrarIci = faz.tekrarIci,
            toplamKalanMetin = formatSure(toplamKalanSn),
            calisiyor = calisiyor,
            dikkatKisa = exercise.dikkatKisa,
            onBaslatDuraklat = {
                if (toplamKalanSn == 0) {
                    sifirlama++
                    calisiyor = true
                } else {
                    calisiyor = !calisiyor
                }
            }
        )
        // 4. Nasıl yapılır.
        NasilYapilirKarti(adimlar = exercise.steps)
        // 5. Fayda (+bilgi linki).
        FaydaKarti(
            benefit = exercise.benefit,
            onOpenBilgi = { onOpenBilgi("mikro-hareketler") }
        )
        // 6. Dikkat.
        DikkatKarti(caution = exercise.caution)
        // 7. Kapanış.
        OutlinedButton(
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp),
            shape = CircleShape
        ) {
            Text(text = "BİTİR")
        }
        DetayGuvenlikDipnotu()
    }
}
