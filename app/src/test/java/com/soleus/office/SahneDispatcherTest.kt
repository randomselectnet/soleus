package com.soleus.office

import com.soleus.office.ui.motion.BILINEN_SAHNE_IDLERI
import org.junit.Assert.*
import org.junit.Test

class SahneDispatcherTest {
    private val beklenen = setOf(
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

    @Test fun onBirId_bosPaneleDusmez() {
        assertEquals(11, BILINEN_SAHNE_IDLERI.size)
        beklenen.forEach { id ->
            assertTrue("$id dispatcher'da yok", id in BILINEN_SAHNE_IDLERI)
        }
    }

    @Test fun bilinmeyenId_bosPanel() {
        assertFalse("bilinmeyen" in BILINEN_SAHNE_IDLERI)
        assertFalse("" in BILINEN_SAHNE_IDLERI)
    }
}
