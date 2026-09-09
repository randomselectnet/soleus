package com.soleus.office

import com.soleus.office.data.ContentLoader
import org.junit.Assert.*
import org.junit.Test

class InfoContentTest {
    private fun infoJson(): String {
        val candidates = listOf(
            java.io.File("app/src/main/assets/content/info_tr.json"),
            java.io.File("src/main/assets/content/info_tr.json")
        )
        return candidates.firstOrNull { it.exists() }?.readText()
            ?: throw AssertionError("info_tr.json bulunamadı: $candidates (cwd=${java.io.File(".").absolutePath})")
    }

    @Test fun infoPageCount_isFour() {
        val pages = ContentLoader.parseInfoJson(infoJson())
        assertEquals(4, pages.size)
    }

    @Test fun infoPageIds_matchContract() {
        val pages = ContentLoader.parseInfoJson(infoJson())
        assertEquals(
            listOf(
                "neden-oturuyoruz",
                "oturmanin-bedeli",
                "mikro-hareketler",
                "soleus-nasil-kullanilir"
            ),
            pages.map { it.id }
        )
    }

    @Test fun infoPages_haveBodyAndClosing() {
        val pages = ContentLoader.parseInfoJson(infoJson())
        pages.forEach { p ->
            assertTrue("${p.id}: başlık boş", p.baslik.isNotBlank())
            assertTrue("${p.id}: paragraf yok", p.paragraflar.isNotEmpty())
            p.paragraflar.forEach { par ->
                assertTrue("${p.id}: boş paragraf", par.isNotBlank())
            }
            assertTrue("${p.id}: kapanış boş", p.kapanis.isNotBlank())
        }
    }
}
