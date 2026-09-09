package com.soleus.office.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.soleus.office.ui.info.GuvenlikDipnotu
import com.soleus.office.ui.theme.Quicksand
import com.soleus.office.ui.theme.SereneCard
import com.soleus.office.ui.theme.SereneError
import com.soleus.office.ui.theme.SereneOnTertiaryContainer
import com.soleus.office.ui.theme.SerenePillButton
import com.soleus.office.ui.theme.SerenePrimary
import com.soleus.office.ui.theme.SerenePrimaryContainer
import com.soleus.office.ui.theme.SereneTertiary
import com.soleus.office.ui.theme.SereneTertiaryContainer

/** Hap biçimli rozet (SerenePrimaryContainer zemin). */
@Composable
fun DozajRozeti(etiket: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(SerenePrimaryContainer)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = etiket,
            style = MaterialTheme.typography.labelMedium,
            color = SerenePrimary
        )
    }
}

/** Zorluk rozeti: 1→Kolay, 2→Orta, 3→Zor + 3 nokta göstergesi. */
@Composable
fun ZorlukRozeti(zorluk: Int, modifier: Modifier = Modifier) {
    val etiket = when (zorluk) {
        1 -> "Kolay"
        2 -> "Orta"
        else -> "Zor"
    }
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(SerenePrimaryContainer)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = etiket,
            style = MaterialTheme.typography.labelMedium,
            color = SerenePrimary
        )
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            repeat(3) { i ->
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(
                            if (i < zorluk.coerceIn(1, 3)) SerenePrimary
                            else SerenePrimary.copy(alpha = 0.25f)
                        )
                )
            }
        }
    }
}

/** Sayaç kartı: faz adı + sayaç metni + tekrar halkası + toplam kalan + BAŞLAT/DURAKLAT + mini dikkat şeridi. */
@Composable
fun SayacKarti(
    fazAdi: String,
    sayacMetni: String,
    tekrarIci: Float,
    toplamKalanMetin: String,
    calisiyor: Boolean,
    dikkatKisa: String,
    onBaslatDuraklat: () -> Unit,
    modifier: Modifier = Modifier
) {
    SereneCard(modifier = modifier.fillMaxWidth()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            MotionRing(progress = tekrarIci)
            Text(
                text = fazAdi,
                style = MaterialTheme.typography.headlineMedium,
                fontFamily = Quicksand
            )
            Text(
                text = sayacMetni,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = toplamKalanMetin,
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = Quicksand
            )
            SerenePillButton(
                text = if (calisiyor) "DURAKLAT" else "BAŞLAT",
                onClick = onBaslatDuraklat,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SereneTertiaryContainer.copy(alpha = 0.35f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = null,
                    tint = SereneOnTertiaryContainer,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = dikkatKisa,
                    style = MaterialTheme.typography.bodySmall,
                    color = SereneOnTertiaryContainer,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/** Numaralı adım satırı: adaçayı numaralı daire + adım metni. */
@Composable
fun NumaraliAdimSatiri(sira: Int, metin: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(SerenePrimary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$sira",
                style = MaterialTheme.typography.labelMedium,
                color = androidx.compose.ui.graphics.Color.White
            )
        }
        Text(
            text = metin,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .weight(1f)
                .padding(top = 2.dp)
        )
    }
}

/** Nasıl yapılır kartı: başlık + numaralı adımlar. */
@Composable
fun NasilYapilirKarti(adimlar: List<String>, modifier: Modifier = Modifier) {
    SereneCard(modifier = modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Nasıl yapılır",
                style = MaterialTheme.typography.headlineMedium,
                fontFamily = Quicksand
            )
            adimlar.forEachIndexed { i, adim ->
                NumaraliAdimSatiri(sira = i + 1, metin = adim)
            }
        }
    }
}

/** Fayda kartı (uzun form) + "Neden işe yarıyor?" bilgi linki. */
@Composable
fun FaydaKarti(
    benefit: String,
    onOpenBilgi: () -> Unit,
    modifier: Modifier = Modifier
) {
    SereneCard(modifier = modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Faydası",
                style = MaterialTheme.typography.headlineMedium,
                fontFamily = Quicksand
            )
            Text(
                text = benefit,
                style = MaterialTheme.typography.bodyMedium
            )
            TextButton(
                onClick = onOpenBilgi,
                modifier = Modifier.align(Alignment.Start)
            ) {
                Text(
                    text = "Neden işe yarıyor?",
                    style = MaterialTheme.typography.labelLarge,
                    color = SereneTertiary
                )
            }
        }
    }
}

/** Dikkat kartı: uyarı stili başlık + uzun form kontrendikasyonlar. */
@Composable
fun DikkatKarti(caution: String, modifier: Modifier = Modifier) {
    SereneCard(modifier = modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = null,
                    tint = SereneError,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = "Dikkat",
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = Quicksand,
                    color = SereneError
                )
            }
            Text(
                text = caution,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/** Detay kapanışı sabit güvenlik dipnotu (InfoBilesenleri'ndeki ortak metin). */
@Composable
fun DetayGuvenlikDipnotu(modifier: Modifier = Modifier) {
    GuvenlikDipnotu(modifier = modifier)
}
