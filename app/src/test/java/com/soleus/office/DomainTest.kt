package com.soleus.office

import com.soleus.office.domain.calcStreak
import com.soleus.office.domain.dueExercise
import com.soleus.office.domain.shouldRemind
import org.junit.Assert.*
import org.junit.Test

class DomainTest {
    @Test fun rotation_picksNextUncompleted() {
        assertEquals("b", dueExercise(listOf("a", "b", "c"), listOf("a")))
    }
    @Test fun rotation_wrapsWhenAllDone() {
        assertEquals("a", dueExercise(listOf("a", "b"), listOf("a", "b")))
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
