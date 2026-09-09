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

    @Test fun expertFields_areNonBlank() {
        val exercises = ContentLoader.parseJson(contentJson())
        assertEquals(11, exercises.size)
        exercises.forEach { e ->
            assertTrue("${e.id}: faydaKisa boş", e.faydaKisa.isNotBlank())
            assertTrue("${e.id}: dikkatKisa boş", e.dikkatKisa.isNotBlank())
            assertTrue("${e.id}: dozajEtiket boş", e.dozajEtiket.isNotBlank())
            assertTrue("${e.id}: benefit boş", e.benefit.isNotBlank())
            assertTrue("${e.id}: caution boş", e.caution.isNotBlank())
        }
    }

    @Test fun faydaKisa_fitsNinetyChars() {
        val exercises = ContentLoader.parseJson(contentJson())
        exercises.forEach { e ->
            assertTrue(
                "${e.id}: faydaKisa ${e.faydaKisa.length} karakter (sınır 90)",
                e.faydaKisa.length <= 90
            )
        }
    }

    @Test fun dikkatKisa_fitsSeventyChars() {
        val exercises = ContentLoader.parseJson(contentJson())
        exercises.forEach { e ->
            assertTrue(
                "${e.id}: dikkatKisa ${e.dikkatKisa.length} karakter (sınır 70)",
                e.dikkatKisa.length <= 70
            )
        }
    }

    @Test fun zorluk_inOneToThree() {
        val exercises = ContentLoader.parseJson(contentJson())
        exercises.forEach { e ->
            assertTrue("${e.id}: zorluk ${e.zorluk} aralık dışı", e.zorluk in 1..3)
        }
    }

    @Test fun sema_animationAssetYok() {
        // Prosedürel sahne tasfiyesi sonrası: ham JSON'da animationAsset anahtarı kalmamalı.
        assertFalse(
            "exercises_tr.json animationAsset içeriyor",
            contentJson().contains("animationAsset")
        )
        // Şema yine de 11 egzersiz parse etmeli.
        assertEquals(11, ContentLoader.parseJson(contentJson()).size)
    }
}
