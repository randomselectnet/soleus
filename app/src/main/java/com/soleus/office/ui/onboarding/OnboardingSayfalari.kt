package com.soleus.office.ui.onboarding

/** Karşılama sayfası veri modeli: başlık + gövde + geçiş cümlesi (metinler spec'ten birebir). */
data class KarsilamaSayfasi(
    val baslik: String,
    val govde: List<String>,
    val gecis: String
)

/** 3 karşılama sayfası (onboarding-metin.md §1–3, kelimesi kelimesine). */
val KARSILAMA_SAYFALARI: List<KarsilamaSayfasi> = listOf(
    KarsilamaSayfasi(
        baslik = "Oturmak sessizce birikiyor",
        govde = listOf(
            "Masa başı çalışanların günde yaklaşık 9–11 saat oturabildiği bildiriliyor.",
            "Akşamki sırt ağırlığı, bacaklardaki uyuşukluk, dağılan dikkat — çoğu ofis çalışanının tanıdığı hisler."
        ),
        gecis = "Ama mesele oturmak değil; mesele hiç kalkmamak. →"
    ),
    KarsilamaSayfasi(
        baslik = "Çözüm: 1 dakikalık molalar",
        govde = listOf(
            "Saat başı 1 hareket, 1–2 dakika — ter atmadan, toplantıyı bölmeden.",
            "Birçok kişi kısa aradan sonra bacaklarının canlandığını, zihninin tazelendiğini söylüyor."
        ),
        gecis = "Küçük doz, düzenli ritim — beden tekrarı sever. →"
    ),
    KarsilamaSayfasi(
        baslik = "Soleus gününe eşlik eder",
        govde = listOf(
            "Saat başı nazikçe seslenir; erteleyebilirsin, ceza yok.",
            "Kartı aç, 1–2 dakikada yap ya da \"geç\" de — karar senin.",
            "Yaptıkça küçük serin büyür; bir gün kaçsa bile kaldığın yerden devam."
        ),
        gecis = ""
    )
)

/** Rota içi adım durumu (NavGraph'a yeni rota eklenmez). */
enum class KarsilamaAdimi { SAYFA_1, SAYFA_2, SAYFA_3, IZIN }

// ── İzin adımı metinleri (onboarding-metin.md §4, birebir) ──
const val IZIN_BASLIK = "Hatırlatmalar için bir iznin gerek"
const val IZIN_GEREKCE = "Soleus saat başı tek bir nazik hatırlatma gönderir, bunun dışında sessiz kalır."
const val IZIN_BUTON = "Hatırlatmaları aç"
const val IZIN_RET_BUTON = "Şimdilik sessiz başla"

/** 3. sayfa CTA metni (butona sığmazsa kısa versiyon). */
const val CTA_UZUN = "İlk molana hazır mısın?"
const val CTA_KISA = "Hadi başlayalım"
