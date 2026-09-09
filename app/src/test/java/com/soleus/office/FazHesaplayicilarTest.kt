package com.soleus.office

import com.soleus.office.ui.motion.ankleCircleFaz
import com.soleus.office.ui.motion.easeInOutCubic
import com.soleus.office.ui.motion.easeInOutSine
import com.soleus.office.ui.motion.easeOutCubic
import com.soleus.office.ui.motion.eyeBreathFaz
import com.soleus.office.ui.motion.fazOzeti
import com.soleus.office.ui.motion.hipSqueezeFaz
import com.soleus.office.ui.motion.legExtensionFaz
import com.soleus.office.ui.motion.neckStretchFaz
import com.soleus.office.ui.motion.scapularSqueezeFaz
import com.soleus.office.ui.motion.shoulderShrugFaz
import com.soleus.office.ui.motion.soleusPushupFaz
import com.soleus.office.ui.motion.standupMiniSetFaz
import com.soleus.office.ui.motion.trunkRotationFaz
import com.soleus.office.ui.motion.wristStretchFaz
import org.junit.Assert.*
import org.junit.Test
import kotlin.math.abs

class FazHesaplayicilarTest {

    @Test fun easing_sinirlar() {
        assertEquals(0f, easeOutCubic(0f), 0f)
        assertEquals(1f, easeOutCubic(1f), 0.001f)
        assertEquals(0f, easeInOutSine(0f), 0f)
        assertEquals(1f, easeInOutSine(1f), 0.001f)
        assertEquals(0f, easeInOutCubic(0f), 0f)
        assertEquals(1f, easeInOutCubic(1f), 0.001f)
        assertEquals(0.5f, easeInOutSine(0.5f), 0.001f)
    }

    @Test fun soleus_fazGecisleri() {
        assertEquals("Hazır", soleusPushupFaz(0).fazAdi)
        assertEquals("Kaldır…", soleusPushupFaz(500).fazAdi)
        assertEquals("Tut", soleusPushupFaz(2500).fazAdi)
        assertEquals("İndir", soleusPushupFaz(3500).fazAdi)
        // Döngü sonu başa sarar.
        assertEquals("Hazır", soleusPushupFaz(6000).fazAdi)
        assertEquals(1, soleusPushupFaz(6000).tekrar)
        // Kilitler: topukH ve tekrarIci 0..1.
        listOf(0L, 1500L, 3000L, 5000L, 12000L).forEach { t ->
            val f = soleusPushupFaz(t)
            assertTrue("topukH $t", f.topukH in 0f..1f)
            assertTrue("tekrarIci $t", f.tekrarIci in 0f..1f)
        }
        assertEquals(0f, soleusPushupFaz(0).topukH, 0f)
        assertEquals(1f, soleusPushupFaz(3000).topukH, 0.001f)
    }

    @Test fun ankle_fazGecisleri() {
        assertEquals("SAG_SAAT", ankleCircleFaz(0).altFaz)
        assertEquals("SAG_TERS", ankleCircleFaz(30000).altFaz)
        assertEquals("SOL_SAAT", ankleCircleFaz(60000).altFaz)
        assertEquals("SOL_TERS", ankleCircleFaz(75000).altFaz)
        assertEquals("CIFT_TOPUK", ankleCircleFaz(90000).altFaz)
        assertEquals("SAG_SAAT", ankleCircleFaz(120000).altFaz)
        assertEquals(1, ankleCircleFaz(0).yonIsareti)
        assertEquals(-1, ankleCircleFaz(30000).yonIsareti)
    }

    @Test fun hip_fazGecisleri() {
        assertEquals("Nefes al", hipSqueezeFaz(0).fazAdi)
        assertEquals("Sık-tut", hipSqueezeFaz(2000).fazAdi)
        assertEquals("Gevşe", hipSqueezeFaz(7000).fazAdi)
        assertEquals("Nefes al", hipSqueezeFaz(10000).fazAdi)
        assertEquals(5, hipSqueezeFaz(2000).tutKalanSn)
        assertEquals(1, hipSqueezeFaz(6500).tutKalanSn)
        assertTrue(hipSqueezeFaz(3000).yariCapCarpani in 0f..1f)
    }

    @Test fun leg_fazGecisleri() {
        val sol = legExtensionFaz(0)
        assertEquals("SOL", sol.taraf)
        assertEquals("Uzat", sol.fazAdi)
        assertEquals(90f, sol.dizeAcisi, 0.001f)
        assertEquals("Tut", legExtensionFaz(2000).fazAdi)
        assertEquals(0f, legExtensionFaz(3000).dizeAcisi, 0.001f)
        assertEquals("İndir", legExtensionFaz(4500).fazAdi)
        // Sağ taraf ikinci yarıda.
        assertEquals("SAG", legExtensionFaz(7500).taraf)
        assertTrue(legExtensionFaz(1000).dizeAcisi in 0f..90f)
    }

    @Test fun neck_aciKilidi() {
        listOf(0L, 1000L, 5000L, 21000L, 23000L, 24000L, 30000L, 48000L).forEach { t ->
            val f = neckStretchFaz(t)
            assertTrue("basAcisi $t=${f.basAcisi}", abs(f.basAcisi) <= 20f + 0.001f)
            assertTrue("tutKalan $t", f.tutKalanSn >= 0)
        }
        assertEquals("SAG", neckStretchFaz(0).taraf)
        assertEquals("SOL", neckStretchFaz(24000).taraf)
        assertEquals("Tut-nefes-al", neckStretchFaz(3000).fazAdi)
        assertEquals(19, neckStretchFaz(3000).tutKalanSn)
    }

    @Test fun shoulder_fazGecisleri() {
        assertEquals("SILKME", shoulderShrugFaz(0).altFaz)
        assertEquals("ONE_DAIRE", shoulderShrugFaz(48000).altFaz)
        assertEquals("GERI_DAIRE", shoulderShrugFaz(72000).altFaz)
        assertEquals("SILKME", shoulderShrugFaz(96000).altFaz)
        assertEquals(1, shoulderShrugFaz(0).tekrar)
        assertEquals(8, shoulderShrugFaz(47000).tekrar)
        assertTrue(shoulderShrugFaz(1000).omuzY in 0f..1f)
    }

    @Test fun scapular_tutmaKilidi() {
        assertEquals("Yaklaştır", scapularSqueezeFaz(0).fazAdi)
        assertEquals("Tut-nefes-ver", scapularSqueezeFaz(2500).fazAdi)
        assertEquals(7, scapularSqueezeFaz(2500).tutKalanSn)
        assertEquals("Bırak", scapularSqueezeFaz(9500).fazAdi)
        listOf(0L, 1200L, 5000L, 11000L).forEach { t ->
            assertTrue("yaklasma $t", scapularSqueezeFaz(t).kurekYaklasma in 0f..1f)
        }
    }

    @Test fun trunk_aciKilidi() {
        listOf(0L, 1500L, 5000L, 19000L, 21000L, 30000L).forEach { t ->
            val f = trunkRotationFaz(t)
            assertTrue("omuzAcisi $t=${f.omuzAcisi}", abs(f.omuzAcisi) <= 30f + 0.001f)
        }
        assertEquals("Dön", trunkRotationFaz(0).fazAdi)
        assertEquals("Tut-nefes-al", trunkRotationFaz(3000).fazAdi)
        assertEquals(15, trunkRotationFaz(3000).tutKalanSn)
    }

    @Test fun wrist_aciKilidi() {
        assertEquals("SOL_YUKARI", wristStretchFaz(0).altFaz)
        assertEquals("SOL_ASAGI", wristStretchFaz(17000).altFaz)
        assertEquals("SAG_YUKARI", wristStretchFaz(34000).altFaz)
        assertEquals("SAG_ASAGI", wristStretchFaz(51000).altFaz)
        assertEquals("POMPA", wristStretchFaz(68000).altFaz)
        assertEquals("SOL_YUKARI", wristStretchFaz(80000).altFaz)
        listOf(0L, 1000L, 5000L, 20000L, 60000L).forEach { t ->
            assertTrue("elAcisi $t", abs(wristStretchFaz(t).elAcisi) <= 30f + 0.001f)
        }
        assertEquals(1, wristStretchFaz(68000).pompaSayaci)
        assertEquals(12, wristStretchFaz(79000).pompaSayaci)
    }

    @Test fun eye_perdeGecisleri() {
        assertEquals("BAKIS", eyeBreathFaz(0).perde)
        assertEquals(20, eyeBreathFaz(0).bakisKalanSn)
        assertEquals("NEFES", eyeBreathFaz(20000).perde)
        assertEquals("AL", eyeBreathFaz(20000).nefesFaz)
        assertEquals("TUT", eyeBreathFaz(24000).nefesFaz)
        assertEquals("VER", eyeBreathFaz(28000).nefesFaz)
        assertEquals(1, eyeBreathFaz(20000).nefesTur)
        assertEquals(4, eyeBreathFaz(56000).nefesTur)
        assertTrue(eyeBreathFaz(22000).nefesYaricap in 0f..1f)
    }

    @Test fun standup_fazGecisleri() {
        assertEquals("KALK", standupMiniSetFaz(0).altFaz)
        assertEquals("SQUAT", standupMiniSetFaz(2000).altFaz)
        assertEquals(1, standupMiniSetFaz(2000).squatSayaci)
        assertEquals(10, standupMiniSetFaz(47000).squatSayaci)
        assertEquals("YURUYUS", standupMiniSetFaz(52000).altFaz)
        assertEquals("OTUR_SU", standupMiniSetFaz(102000).altFaz)
        assertEquals("PUSHUP", standupMiniSetFaz(52000, pushupYerine = true).altFaz)
        assertTrue(standupMiniSetFaz(3000).cokmeOrani in 0f..1f)
    }

    @Test fun ozet_tekrarIciKilitli() {
        val idler = listOf(
            "soleus-pushup", "ankle-circle-calf-raise", "hip-squeeze",
            "seated-leg-extension", "neck-side-stretch", "shoulder-shrug-roll",
            "scapular-squeeze", "seated-trunk-rotation", "wrist-forearm-stretch",
            "eye-202020-breath-444", "standup-mini-set"
        )
        listOf(0L, 1000L, 30000L, 90000L).forEach { t ->
            idler.forEach { id ->
                val o = fazOzeti(id, t)
                assertTrue("$id@$t fazAdi boş", o.fazAdi.isNotBlank())
                assertTrue("$id@$t sayac boş", o.sayacMetni.isNotBlank())
                assertTrue("$id@$t tekrarIci=${o.tekrarIci}", o.tekrarIci in 0f..1f)
            }
        }
    }
}
