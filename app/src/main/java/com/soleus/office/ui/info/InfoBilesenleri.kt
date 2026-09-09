package com.soleus.office.ui.info

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.soleus.office.data.model.InfoPage
import com.soleus.office.ui.theme.Quicksand
import com.soleus.office.ui.theme.SereneCard
import com.soleus.office.ui.theme.SerenePrimary
import com.soleus.office.ui.theme.SerenePrimaryContainer

/** Bilgi sayfası kimlikleri (metin-destesi A/1–4 sırasıyla). */
val BILGI_SAYFA_IDS = listOf(
    "neden-oturuyoruz",
    "oturmanin-bedeli",
    "mikro-hareketler",
    "soleus-nasil-kullanilir"
)

/** Rotasyonlu "Öğren" kartı + Ayarlar listesi için sayfa başlıkları. */
val BILGI_SAYFA_BASLIKLARI = mapOf(
    "neden-oturuyoruz" to "Neden oturuyoruz?",
    "oturmanin-bedeli" to "Oturmanın bedeli",
    "mikro-hareketler" to "Mikro hareketler neden işe yarıyor?",
    "soleus-nasil-kullanilir" to "Soleus nasıl kullanılır?"
)

/** Sayı headline stat olarak büyütülürse önüne zorunlu "~" chip'i. */
@Composable
fun YaklasikRozet(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(SerenePrimaryContainer)
            .padding(horizontal = 8.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "~",
            style = MaterialTheme.typography.labelMedium,
            color = SerenePrimary
        )
    }
}

/** Sayfa 1–2 altı kaynakça satırı: jenerik + dürüst ifade. */
@Composable
fun KaynakcaSatiri(modifier: Modifier = Modifier) {
    Text(
        text = "Yaklaşık değerler; farklı araştırmalarda aralıklar değişir. Tıbbi ölçüm değildir.",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.fillMaxWidth()
    )
}

/** Her detay + bilgi sayfasında sabit güvenlik dipnotu. */
@Composable
fun GuvenlikDipnotu(modifier: Modifier = Modifier) {
    Text(
        text = "Bu bilgiler genel esenlik amaçlıdır, tıbbi öneri değildir. " +
            "Keskin ağrı, uyuşma artışı, baş dönmesi hissedersen dur ve " +
            "bir sağlık profesyoneline danış.",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.fillMaxWidth()
    )
}

/**
 * Bilgi sayfası (Serene): başlık + paragraf kartları + kapanış +
 * KaynakçaSatiri (yalnızca sayfa 1–2) + GuvenlikDipnotu.
 */
@Composable
fun InfoScreen(page: InfoPage, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = page.baslik,
            style = MaterialTheme.typography.headlineLarge,
            fontFamily = Quicksand,
            modifier = Modifier.fillMaxWidth()
        )
        page.paragraflar.forEach { paragraf ->
            SereneCard(modifier = Modifier.fillMaxWidth(), contentPadding = 20.dp) {
                Text(
                    text = paragraf,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        SereneCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = 20.dp
        ) {
            Text(
                text = page.kapanis,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = Quicksand,
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (page.id == "neden-oturuyoruz" || page.id == "oturmanin-bedeli") {
            KaynakcaSatiri()
        }
        GuvenlikDipnotu()
    }
}
