package com.soleus.office

import com.soleus.office.domain.calcStreak
import com.soleus.office.domain.dueExercise
import com.soleus.office.domain.enabledIds
import com.soleus.office.domain.shouldRemind
import com.soleus.office.ui.home.greetingForHour
import com.soleus.office.ui.stats.GunDurumu
import com.soleus.office.ui.stats.gunDurumu
import org.junit.Assert.*
import org.junit.Test

class DomainTest {
    @Test fun rotation_picksNextUncompleted() {
        assertEquals("b", dueExercise(listOf("a", "b", "c"), listOf("a")))
    }
    @Test fun rotation_wrapsWhenAllDone() {
        assertEquals("a", dueExercise(listOf("a", "b"), listOf("a", "b")))
    }
    @Test(expected = IllegalArgumentException::class)
    fun rotation_emptyList_throws() {
        dueExercise(emptyList(), emptyList())
    }
    @Test fun enabledFilter_removesDisabled() {
        assertEquals(listOf("a", "c"), enabledIds(listOf("a", "b", "c"), setOf("b")))
    }
    @Test fun enabledFilter_emptyDisabled_returnsAll() {
        assertEquals(listOf("a", "b"), enabledIds(listOf("a", "b"), emptySet()))
    }
    @Test fun enabledFilter_allDisabled_failsafeReturnsAll() {
        assertEquals(listOf("a", "b"), enabledIds(listOf("a", "b"), setOf("a", "b")))
    }
    @Test fun greeting_byHour() {
        assertEquals("Günaydın.", greetingForHour(5))
        assertEquals("Günaydın.", greetingForHour(11))
        assertEquals("Tünaydın.", greetingForHour(12))
        assertEquals("Tünaydın.", greetingForHour(17))
        assertEquals("İyi akşamlar.", greetingForHour(18))
        assertEquals("İyi akşamlar.", greetingForHour(4))
        assertEquals("İyi akşamlar.", greetingForHour(23))
    }
    @Test fun gunDurumu_thresholds() {
        assertEquals(GunDurumu.DINLENME, gunDurumu(0))
        assertEquals(GunDurumu.KISMI, gunDurumu(1))
        assertEquals(GunDurumu.TAMAM, gunDurumu(2))
        assertEquals(GunDurumu.TAMAM, gunDurumu(5))
    }
    @Test fun streak_countsConsecutive() {
        assertEquals(2, calcStreak(setOf("2026-09-07", "2026-09-08"), "2026-09-08"))
    }
    @Test fun streak_breaksOnGap() {
        assertEquals(1, calcStreak(setOf("2026-09-06", "2026-09-08"), "2026-09-08"))
    }
    @Test fun shouldRemind_insideHours() {
        assertTrue(shouldRemind(600, 540, 1080))
        assertFalse(shouldRemind(1200, 540, 1080))
    }
}
