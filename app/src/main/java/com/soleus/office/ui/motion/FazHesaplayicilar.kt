package com.soleus.office.ui.motion

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

// ── Saf easing fonksiyonları (fizyo-hareket.md §6 zaman fonksiyonları) ──

fun easeOutCubic(x: Float): Float {
    val t = x.coerceIn(0f, 1f)
    return 1f - (1f - t).pow(3)
}

fun easeInOutSine(x: Float): Float {
    val t = x.coerceIn(0f, 1f)
    return (-(cos(PI.toFloat() * t) - 1f) / 2f)
}

fun easeInOutCubic(x: Float): Float {
    val t = x.coerceIn(0f, 1f)
    return if (t < 0.5f) 4f * t * t * t else 1f - (-2f * t + 2f).pow(3) / 2f
}

/** Detay ekranı sayaç kartının tek kaynaktan beslendiği özet: faz adı + sayaç metni + tekrar halkası. */
data class SahneFazOzeti(
    val fazAdi: String,
    val sayacMetni: String,
    val tekrarIci: Float
)

private fun oran(x: Float): Float = x.coerceIn(0f, 1f)

private fun kalanSn(localMs: Float, bitisMs: Float): Int =
    (((bitisMs - localMs) / 1000f).coerceAtLeast(0f).let {
        kotlin.math.ceil(it.toDouble()).toInt()
    })

// ── 1. soleus-pushup: hazır 0.5 + yüksel 2 + tut 1 + indir 2.5 = 6 sn/tekrar ──

data class SoleusPushupFaz(
    val fazAdi: String,
    val tekrar: Int,
    val tekrarIci: Float,
    val topukH: Float
)

fun soleusPushupFaz(elapsedMs: Long): SoleusPushupFaz {
    val t = elapsedMs.coerceAtLeast(0L)
    val dongu = 6000L
    val local = (t % dongu).toFloat()
    val (ad, h) = when {
        local < 500f -> "Hazır" to 0f
        local < 2500f -> "Kaldır…" to easeOutCubic((local - 500f) / 2000f)
        local < 3500f -> "Tut" to 1f
        else -> "İndir" to (1f - easeInOutSine((local - 3500f) / 2500f))
    }
    return SoleusPushupFaz(ad, (t / dongu).toInt(), oran(local / dongu), oran(h))
}

fun SoleusPushupFaz.ozet(): SahneFazOzeti =
    SahneFazOzeti(fazAdi, "Tekrar ${tekrar + 1}", tekrarIci)

// ── 2. ankle-circle-calf-raise: 30+30+15+15 daire + 30 çift-topuk = 120 sn ──

data class AnkleCircleFaz(
    val altFaz: String,
    val daireSayaci: Int,
    val yonIsareti: Int,
    val ayakucuAcisi: Float,
    val topukH: Float
)

fun ankleCircleFaz(elapsedMs: Long): AnkleCircleFaz {
    val t = (elapsedMs.coerceAtLeast(0L) % 120000L).toFloat()
    val sinirlar = floatArrayOf(0f, 30000f, 60000f, 75000f, 90000f, 120000f)
    val adlar = arrayOf("SAG_SAAT", "SAG_TERS", "SOL_SAAT", "SOL_TERS", "CIFT_TOPUK")
    val yonler = intArrayOf(1, -1, 1, -1, 1)
    var idx = 0
    for (i in 0 until 5) if (t >= sinirlar[i + 1]) idx = i + 1
    if (t >= 120000f) idx = 0
    val baslangic = sinirlar[idx]
    val local = t - baslangic
    // Faz geçişlerinde 1 sn duraklama (ilk faz hariç): açı donar.
    val eff = if (idx == 0) local else (local - 1000f).coerceAtLeast(0f)
    val turMs = 3000f
    val sayac = (eff / turMs).toInt()
    val aci = (2f * PI.toFloat() * ((eff % turMs) / turMs)) * yonler[idx]
    val topuk = if (adlar[idx] == "CIFT_TOPUK") {
        val sub = (local % 6000f)
        when {
            sub < 500f -> 0f
            sub < 2500f -> easeOutCubic((sub - 500f) / 2000f)
            sub < 3500f -> 1f
            else -> 1f - easeInOutSine((sub - 3500f) / 2500f)
        }
    } else 0f
    return AnkleCircleFaz(adlar[idx], sayac, yonler[idx], aci, oran(topuk))
}

fun AnkleCircleFaz.ozet(): SahneFazOzeti {
    val (ad, sayac) = when (altFaz) {
        "SAG_SAAT" -> "Sağ ayak • saat yönü" to "Daire ${(daireSayaci + 1).coerceAtMost(10)}/10"
        "SAG_TERS" -> "Sağ ayak • ters yön" to "Daire ${(daireSayaci + 1).coerceAtMost(10)}/10"
        "SOL_SAAT" -> "Sol ayak • saat yönü" to "Daire ${(daireSayaci + 1).coerceAtMost(5)}/5"
        "SOL_TERS" -> "Sol ayak • ters yön" to "Daire ${(daireSayaci + 1).coerceAtMost(5)}/5"
        else -> "Çift topuk kaldırma" to "Tekrar ${daireSayaci / 2 + 1}"
    }
    val ici = if (altFaz == "CIFT_TOPUK") topukH else oran((ayakucuAcisi / (2f * PI.toFloat())) * yonIsareti)
    return SahneFazOzeti(ad, sayac, ici)
}

// ── 3. hip-squeeze: nefes-al 2 + sık-tut 5 + gevşe 3 = 10 sn/tekrar ──

data class HipSqueezeFaz(
    val fazAdi: String,
    val tutKalanSn: Int,
    val tekrar: Int,
    val yariCapCarpani: Float
)

fun hipSqueezeFaz(elapsedMs: Long): HipSqueezeFaz {
    val t = elapsedMs.coerceAtLeast(0L)
    val dongu = 10000L
    val local = (t % dongu).toFloat()
    return when {
        local < 2000f -> HipSqueezeFaz(
            "Nefes al", 5, (t / dongu).toInt(),
            oran(1f - 0.25f * easeOutCubic(local / 2000f))
        )
        local < 7000f -> HipSqueezeFaz(
            "Sık-tut", kalanSn(local, 7000f), (t / dongu).toInt(),
            // Tutma sabit + 1 Hz nefes titreşimi overlay'i.
            oran(0.75f + 0.015f * sin(2f * PI.toFloat() * local / 1000f))
        )
        else -> HipSqueezeFaz(
            "Gevşe", 0, (t / dongu).toInt(),
            oran(0.75f + 0.25f * easeInOutSine((local - 7000f) / 3000f))
        )
    }
}

fun HipSqueezeFaz.ozet(): SahneFazOzeti =
    SahneFazOzeti(
        fazAdi,
        if (fazAdi == "Sık-tut") "Tut: $tutKalanSn" else "Tekrar ${tekrar + 1}",
        if (fazAdi == "Sık-tut") 1f - tutKalanSn / 5f else oran(1f - yariCapCarpani)
    )

// ── 4. seated-leg-extension: uzat 2 + tut 2.5 + indir 3 = 7.5 sn/taraf ──

data class LegExtensionFaz(
    val taraf: String,
    val fazAdi: String,
    val dizeAcisi: Float,
    val tutKalanSn: Int,
    val tarafSayaci: Int
)

fun legExtensionFaz(elapsedMs: Long): LegExtensionFaz {
    val t = elapsedMs.coerceAtLeast(0L)
    val repMs = 7500L
    val rep = (t / repMs).toInt()
    val taraf = if (rep % 2 == 0) "SOL" else "SAG"
    val local = (t % repMs).toFloat()
    val (ad, aci) = when {
        local < 2000f -> "Uzat" to 90f * (1f - easeOutCubic(local / 2000f))
        local < 4500f -> "Tut" to 0f
        else -> "İndir" to 90f * easeInOutSine((local - 4500f) / 3000f)
    }
    val tutKalan = if (ad == "Tut") kalanSn(local, 4500f) else 0
    return LegExtensionFaz(taraf, ad, aci.coerceIn(0f, 90f), tutKalan, rep / 2 + 1)
}

fun LegExtensionFaz.ozet(): SahneFazOzeti {
    val tarafAd = if (taraf == "SOL") "Sol" else "Sağ"
    val sayac = if (fazAdi == "Tut") "Tut: $tutKalanSn" else "$tarafAd $tarafSayaci/8"
    val ici = when (fazAdi) {
        "Uzat" -> oran(1f - dizeAcisi / 90f)
        "Tut" -> 1f
        else -> oran(dizeAcisi / 90f)
    }
    return SahneFazOzeti("$tarafAd • $fazAdi", sayac, ici)
}

// ── 5. neck-side-stretch: eğ 2 + tut 20 + merkez 2 = 24 sn/taraf ──

data class NeckStretchFaz(
    val taraf: String,
    val tur: Int,
    val fazAdi: String,
    val basAcisi: Float,
    val tutKalanSn: Int
)

fun neckStretchFaz(elapsedMs: Long): NeckStretchFaz {
    val t = elapsedMs.coerceAtLeast(0L)
    val yariMs = 24000L
    val yari = (t / yariMs).toInt()
    val taraf = if (yari % 2 == 0) "SAG" else "SOL"
    val isaret = if (taraf == "SAG") 1f else -1f
    val local = (t % yariMs).toFloat()
    val (ad, aci) = when {
        local < 2000f -> "Eğil" to isaret * 20f * easeInOutSine(local / 2000f)
        local < 22000f -> "Tut-nefes-al" to isaret * 20f
        else -> "Ortaya-dön" to isaret * 20f * (1f - easeInOutSine((local - 22000f) / 2000f))
    }
    val tutKalan = if (ad == "Tut-nefes-al") kalanSn(local, 22000f) else 0
    return NeckStretchFaz(taraf, (t / 48000L).toInt(), ad, aci.coerceIn(-20f, 20f), tutKalan)
}

fun NeckStretchFaz.ozet(): SahneFazOzeti {
    val tarafAd = if (taraf == "SAG") "Sağ" else "Sol"
    val sayac = if (fazAdi == "Tut-nefes-al") "Tut: $tutKalanSn" else "$tarafAd • ${tur + 1}/2"
    val ici = if (fazAdi == "Tut-nefes-al") oran(1f - tutKalanSn / 20f) else oran(kotlin.math.abs(basAcisi) / 20f)
    return SahneFazOzeti("$tarafAd • $fazAdi", sayac, ici)
}

// ── 6. shoulder-shrug-roll: silkme 8×6 + öne 8×3 + geri 8×3 = 96 sn ──

data class ShoulderShrugFaz(
    val altFaz: String,
    val tekrar: Int,
    val omuzY: Float,
    val daireAcisi: Float
)

fun shoulderShrugFaz(elapsedMs: Long): ShoulderShrugFaz {
    val t = (elapsedMs.coerceAtLeast(0L) % 96000L).toFloat()
    return when {
        t < 48000f -> {
            val rep = (t / 6000f).toInt()
            val local = t % 6000f
            val y = when {
                local < 2000f -> easeOutCubic(local / 2000f)
                local < 4000f -> 1f
                else -> 1f - easeInOutSine((local - 4000f) / 2000f)
            }
            ShoulderShrugFaz("SILKME", rep + 1, oran(y), 0f)
        }
        t < 72000f -> {
            val local2 = t - 48000f
            val rep = (local2 / 3000f).toInt()
            ShoulderShrugFaz("ONE_DAIRE", rep + 1, 0f, 2f * PI.toFloat() * ((local2 % 3000f) / 3000f))
        }
        else -> {
            val local3 = t - 72000f
            val rep = (local3 / 3000f).toInt()
            ShoulderShrugFaz("GERI_DAIRE", rep + 1, 0f, 2f * PI.toFloat() * ((local3 % 3000f) / 3000f))
        }
    }
}

fun ShoulderShrugFaz.ozet(): SahneFazOzeti {
    val ad = when (altFaz) {
        "SILKME" -> "Silkme $tekrar/8"
        "ONE_DAIRE" -> "Öne daire $tekrar/8"
        else -> "Geriye daire $tekrar/8"
    }
    val ici = if (altFaz == "SILKME") omuzY else oran(daireAcisi / (2f * PI.toFloat()))
    return SahneFazOzeti(ad, ad, ici)
}

// ── 7. scapular-squeeze: aç 2.5 + tut 7 + bırak 2.5 = 12 sn/tekrar ──

data class ScapularSqueezeFaz(
    val fazAdi: String,
    val tekrar: Int,
    val tutKalanSn: Int,
    val kurekYaklasma: Float
)

fun scapularSqueezeFaz(elapsedMs: Long): ScapularSqueezeFaz {
    val t = elapsedMs.coerceAtLeast(0L)
    val dongu = 12000L
    val local = (t % dongu).toFloat()
    return when {
        local < 2500f -> ScapularSqueezeFaz(
            "Yaklaştır", (t / dongu).toInt(), 7, oran(easeInOutCubic(local / 2500f))
        )
        local < 9500f -> ScapularSqueezeFaz(
            "Tut-nefes-ver", (t / dongu).toInt(), kalanSn(local, 9500f), 1f
        )
        else -> ScapularSqueezeFaz(
            "Bırak", (t / dongu).toInt(), 0, oran(1f - easeInOutSine((local - 9500f) / 2500f))
        )
    }
}

fun ScapularSqueezeFaz.ozet(): SahneFazOzeti =
    SahneFazOzeti(
        fazAdi,
        if (fazAdi == "Tut-nefes-ver") "Tut: $tutKalanSn" else "${tekrar + 1}/6",
        if (fazAdi == "Tut-nefes-ver") oran(1f - tutKalanSn / 7f) else kurekYaklasma
    )

// ── 8. seated-trunk-rotation: dön 3 + tut 15 + merkez 3 = 21 sn/taraf ──

data class TrunkRotationFaz(
    val taraf: String,
    val tur: Int,
    val fazAdi: String,
    val omuzAcisi: Float,
    val tutKalanSn: Int
)

fun trunkRotationFaz(elapsedMs: Long): TrunkRotationFaz {
    val t = elapsedMs.coerceAtLeast(0L)
    val yariMs = 21000L
    val yari = (t / yariMs).toInt()
    val taraf = if (yari % 2 == 0) "SAG" else "SOL"
    val isaret = if (taraf == "SAG") 1f else -1f
    val local = (t % yariMs).toFloat()
    val (ad, aci) = when {
        local < 3000f -> "Dön" to isaret * 30f * easeInOutCubic(local / 3000f)
        local < 18000f -> "Tut-nefes-al" to isaret * 30f
        else -> "Merkeze" to isaret * 30f * (1f - easeInOutCubic((local - 18000f) / 3000f))
    }
    val tutKalan = if (ad == "Tut-nefes-al") kalanSn(local, 18000f) else 0
    return TrunkRotationFaz(taraf, (t / 42000L).toInt(), ad, aci.coerceIn(-30f, 30f), tutKalan)
}

fun TrunkRotationFaz.ozet(): SahneFazOzeti {
    val tarafAd = if (taraf == "SAG") "Sağa" else "Sola"
    val sayac = if (fazAdi == "Tut-nefes-al") "Tut: $tutKalanSn" else "$tarafAd • ${tur + 1}/2"
    val ici = if (fazAdi == "Tut-nefes-al") oran(1f - tutKalanSn / 15f) else oran(kotlin.math.abs(omuzAcisi) / 30f)
    return SahneFazOzeti("$tarafAd • $fazAdi", sayac, ici)
}

// ── 9. wrist-forearm-stretch: 4×(giriş 2 + tut 15) + pompa 12 = 80 sn ──

data class WristStretchFaz(
    val altFaz: String,
    val elAcisi: Float,
    val tutKalanSn: Int,
    val pompaSayaci: Int
)

fun wristStretchFaz(elapsedMs: Long): WristStretchFaz {
    val t = (elapsedMs.coerceAtLeast(0L) % 80000L).toFloat()
    val adlar = arrayOf("SOL_YUKARI", "SOL_ASAGI", "SAG_YUKARI", "SAG_ASAGI", "POMPA")
    val hedefler = floatArrayOf(30f, -30f, 30f, -30f, 0f)
    val sinirlar = floatArrayOf(0f, 17000f, 34000f, 51000f, 68000f, 80000f)
    var idx = 4
    for (i in 0 until 5) if (t >= sinirlar[i] && t < sinirlar[i + 1]) idx = i
    val local = t - sinirlar[idx]
    if (adlar[idx] == "POMPA") {
        return WristStretchFaz("POMPA", 0f, 0, ((local / 1000f).toInt() + 1).coerceAtMost(12))
    }
    val hedef = hedefler[idx]
    val aci = if (local < 2000f) hedef * easeInOutSine(local / 2000f) else hedef
    val tutKalan = if (local < 2000f) 15 else kalanSn(local, 17000f)
    return WristStretchFaz(adlar[idx], aci.coerceIn(-30f, 30f), tutKalan, 0)
}

fun WristStretchFaz.ozet(): SahneFazOzeti {
    val ad = when (altFaz) {
        "SOL_YUKARI" -> "Sol kol • avuç yukarı"
        "SOL_ASAGI" -> "Sol kol • avuç aşağı"
        "SAG_YUKARI" -> "Sağ kol • avuç yukarı"
        "SAG_ASAGI" -> "Sağ kol • avuç aşağı"
        else -> "Pompa"
    }
    val sayac = if (altFaz == "POMPA") "Aç-kapa $pompaSayaci/12" else "Tut: $tutKalanSn"
    val ici = if (altFaz == "POMPA") oran(pompaSayaci / 12f) else oran(1f - tutKalanSn / 15f)
    return SahneFazOzeti(ad, sayac, ici)
}

// ── 10. eye-202020-breath-444: bakış 20 + 4×(al 4 + tut 4 + ver 4) = 68 sn ──

data class EyeBreathFaz(
    val perde: String,
    val bakisKalanSn: Int,
    val kirpmaKapali: Boolean,
    val nefesFaz: String,
    val nefesTur: Int,
    val nefesYaricap: Float
)

fun eyeBreathFaz(elapsedMs: Long): EyeBreathFaz {
    val t = (elapsedMs.coerceAtLeast(0L) % 68000L).toFloat()
    if (t < 20000f) {
        return EyeBreathFaz(
            perde = "BAKIS",
            bakisKalanSn = kalanSn(t, 20000f),
            kirpmaKapali = (t % 5000f) < 200f,
            nefesFaz = "AL",
            nefesTur = 0,
            nefesYaricap = 0f
        )
    }
    val local = t - 20000f
    val tur = (local / 12000f).toInt().coerceAtMost(3)
    val turLocal = local % 12000f
    val (nefesAd, yaricap) = when {
        turLocal < 4000f -> "AL" to easeInOutSine(turLocal / 4000f)
        turLocal < 8000f -> "TUT" to 1f
        else -> "VER" to (1f - easeInOutSine((turLocal - 8000f) / 4000f))
    }
    return EyeBreathFaz("NEFES", 0, false, nefesAd, tur + 1, oran(yaricap))
}

fun EyeBreathFaz.ozet(): SahneFazOzeti =
    if (perde == "BAKIS") SahneFazOzeti("Uzağa bak", "Uzağa bak: $bakisKalanSn", oran(1f - bakisKalanSn / 20f))
    else {
        val ad = when (nefesFaz) {
            "AL" -> "Al"
            "TUT" -> "Tut"
            else -> "Ver"
        }
        SahneFazOzeti("$ad • Nefes $nefesTur/4", "$ad 4… • $nefesTur/4", nefesYaricap)
    }

// ── 11. standup-mini-set: kalk 2 + squat 10×5 + yürüyüş 50 + otur-su 3 = 105 sn ──

data class StandupMiniSetFaz(
    val altFaz: String,
    val squatSayaci: Int,
    val cokmeOrani: Float,
    val yuruyusKalanSn: Int,
    val pushupYerine: Boolean
)

fun standupMiniSetFaz(elapsedMs: Long, pushupYerine: Boolean = false): StandupMiniSetFaz {
    val t = (elapsedMs.coerceAtLeast(0L) % 105000L).toFloat()
    return when {
        t < 2000f -> StandupMiniSetFaz("KALK", 0, 0f, 50, pushupYerine)
        t < 52000f -> {
            val local = t - 2000f
            val rep = (local / 5000f).toInt()
            val repLocal = local % 5000f
            val cokme = when {
                repLocal < 2000f -> easeInOutSine(repLocal / 2000f)
                repLocal < 3000f -> 1f
                else -> 1f - easeInOutSine((repLocal - 3000f) / 2000f)
            }
            StandupMiniSetFaz("SQUAT", rep + 1, oran(cokme), 50, pushupYerine)
        }
        t < 102000f -> {
            val local = t - 52000f
            val yuruyus = if (pushupYerine) "PUSHUP" else "YURUYUS"
            StandupMiniSetFaz(yuruyus, 10, 0f, kalanSn(local, 50000f), pushupYerine)
        }
        else -> StandupMiniSetFaz("OTUR_SU", 10, 0f, 0, pushupYerine)
    }
}

fun StandupMiniSetFaz.ozet(): SahneFazOzeti =
    when (altFaz) {
        "KALK" -> SahneFazOzeti("Kalk", "Kalk", 0f)
        "SQUAT" -> SahneFazOzeti("Squat $squatSayaci/10", "Squat $squatSayaci/10", cokmeOrani)
        "YURUYUS" -> SahneFazOzeti("Yürüyüş", "Yürüyüş 0:${"%02d".format(yuruyusKalanSn)}", oran(1f - yuruyusKalanSn / 50f))
        "PUSHUP" -> SahneFazOzeti("Masa push-up", "Yürüyüş yerine • 0:${"%02d".format(yuruyusKalanSn)}", oran(1f - yuruyusKalanSn / 50f))
        else -> SahneFazOzeti("Otur • su", "Otur • su", 1f)
    }

/** id → tek özet dispatcher (detay ekranı bunu kullanır). */
fun fazOzeti(id: String, elapsedMs: Long): SahneFazOzeti = when (id) {
    "soleus-pushup" -> soleusPushupFaz(elapsedMs).ozet()
    "ankle-circle-calf-raise" -> ankleCircleFaz(elapsedMs).ozet()
    "hip-squeeze" -> hipSqueezeFaz(elapsedMs).ozet()
    "seated-leg-extension" -> legExtensionFaz(elapsedMs).ozet()
    "neck-side-stretch" -> neckStretchFaz(elapsedMs).ozet()
    "shoulder-shrug-roll" -> shoulderShrugFaz(elapsedMs).ozet()
    "scapular-squeeze" -> scapularSqueezeFaz(elapsedMs).ozet()
    "seated-trunk-rotation" -> trunkRotationFaz(elapsedMs).ozet()
    "wrist-forearm-stretch" -> wristStretchFaz(elapsedMs).ozet()
    "eye-202020-breath-444" -> eyeBreathFaz(elapsedMs).ozet()
    "standup-mini-set" -> standupMiniSetFaz(elapsedMs).ozet()
    else -> SahneFazOzeti("Hazır", "Tekrar 1", 0f)
}
