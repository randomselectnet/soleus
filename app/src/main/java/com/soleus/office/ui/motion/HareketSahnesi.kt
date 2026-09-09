package com.soleus.office.ui.motion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.soleus.office.ui.motion.sahneler.AnkleCircleSahnesi
import com.soleus.office.ui.motion.sahneler.EyeBreathSahnesi
import com.soleus.office.ui.motion.sahneler.HipSqueezeSahnesi
import com.soleus.office.ui.motion.sahneler.LegExtensionSahnesi
import com.soleus.office.ui.motion.sahneler.NeckStretchSahnesi
import com.soleus.office.ui.motion.sahneler.ScapularSqueezeSahnesi
import com.soleus.office.ui.motion.sahneler.ShoulderShrugSahnesi
import com.soleus.office.ui.motion.sahneler.SoleusPushupSahnesi
import com.soleus.office.ui.motion.sahneler.StandupMiniSetSahnesi
import com.soleus.office.ui.motion.sahneler.TrunkRotationSahnesi
import com.soleus.office.ui.motion.sahneler.WristStretchSahnesi
import com.soleus.office.ui.theme.SereneBackground
import com.soleus.office.ui.theme.SereneCard

/** Dispatcher'ın tanıdığı 11 sahne id'si (saf, unit-test edilir). */
val BILINEN_SAHNE_IDLERI: Set<String> = setOf(
    "soleus-pushup",
    "ankle-circle-calf-raise",
    "hip-squeeze",
    "seated-leg-extension",
    "neck-side-stretch",
    "shoulder-shrug-roll",
    "scapular-squeeze",
    "seated-trunk-rotation",
    "wrist-forearm-stretch",
    "eye-202020-breath-444",
    "standup-mini-set"
)

private val SAHNE_ADLARI: Map<String, String> = mapOf(
    "soleus-pushup" to "Soleus Push-Up",
    "ankle-circle-calf-raise" to "Ayak Bileği Çevirme + Calf Raise",
    "hip-squeeze" to "Kalça Sıkma",
    "seated-leg-extension" to "Oturarak Diz Uzatma",
    "neck-side-stretch" to "Boyun Yan Esnetme",
    "shoulder-shrug-roll" to "Omuz Silkme + Çevirme",
    "scapular-squeeze" to "Kürek Kemiği Yaklaştırma / Göğüs Açma",
    "seated-trunk-rotation" to "Oturarak Gövde Rotasyonu",
    "wrist-forearm-stretch" to "Bilek + Önkol Esnetme",
    "eye-202020-breath-444" to "20-20-20 Göz + 4-4-4 Nefes",
    "standup-mini-set" to "Kalkınca Mini Set"
)

/**
 * id → prosedürel sahne dispatcher. elapsedMs SADECE [rememberSahneSaati] çıktısıdır
 * (çift saat yasak). Bilinmeyen id ilk sahneye DÜŞMEZ: boş panel + contentDescription.
 */
@Composable
fun HareketSahnesi(
    id: String,
    elapsedMs: Long,
    modifier: Modifier = Modifier
) {
    SereneCard(modifier = modifier.fillMaxWidth()) {
        val ad = SAHNE_ADLARI[id]
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(SereneBackground)
                .semantics { contentDescription = ad ?: "Bilinmeyen hareket" },
            contentAlignment = Alignment.Center
        ) {
            val sahneModifier = Modifier.matchParentSize()
            when (id) {
                "soleus-pushup" -> SoleusPushupSahnesi(elapsedMs, sahneModifier)
                "ankle-circle-calf-raise" -> AnkleCircleSahnesi(elapsedMs, sahneModifier)
                "hip-squeeze" -> HipSqueezeSahnesi(elapsedMs, sahneModifier)
                "seated-leg-extension" -> LegExtensionSahnesi(elapsedMs, sahneModifier)
                "neck-side-stretch" -> NeckStretchSahnesi(elapsedMs, sahneModifier)
                "shoulder-shrug-roll" -> ShoulderShrugSahnesi(elapsedMs, sahneModifier)
                "scapular-squeeze" -> ScapularSqueezeSahnesi(elapsedMs, sahneModifier)
                "seated-trunk-rotation" -> TrunkRotationSahnesi(elapsedMs, sahneModifier)
                "wrist-forearm-stretch" -> WristStretchSahnesi(elapsedMs, sahneModifier)
                "eye-202020-breath-444" -> EyeBreathSahnesi(elapsedMs, sahneModifier)
                "standup-mini-set" -> StandupMiniSetSahnesi(elapsedMs, sahneModifier)
                else -> Box(modifier = sahneModifier)
            }
        }
    }
}
