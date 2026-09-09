package com.soleus.office

import com.soleus.office.data.ContentLoader
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
        val exercises = ContentLoader.parseJson(contentJson())
        assertEquals("soleus-pushup", exercises.first().id)
        assertEquals(120, exercises.first().durationSec)
    }

    @Test fun exerciseCount_isEleven() {
        val exercises = ContentLoader.parseJson(contentJson())
        assertEquals(11, exercises.size)
    }
}
