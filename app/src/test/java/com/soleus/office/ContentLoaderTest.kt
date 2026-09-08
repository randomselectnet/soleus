package com.soleus.office

import org.junit.Assert.*
import org.junit.Test

class ContentLoaderTest {
    private fun contentJson(): String {
        val candidates = listOf(
            java.io.File("app/src/main/assets/content/exercises_tr.json"),
            java.io.File("src/main/assets/content/exercises_tr.json")
        )
        return candidates.firstOrNull { it.exists() }?.readText()
            ?: throw AssertionError("exercises_tr.json bulunamadı: $candidates (cwd=${java.io.File(".").absolutePath})")
    }

    @Test fun soleus_firstExercise_hasTwoMinutes() {
        val json = contentJson()
        assertTrue(json.contains("soleus-pushup"))
        assertTrue(json.contains("Soleus Push-Up"))
    }

    @Test fun exerciseCount_isEleven() {
        val json = contentJson()
        val count = "\"id\"".toRegex().findAll(json).count()
        assertEquals(11, count)
    }
}
