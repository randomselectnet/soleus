package com.soleus.office

import org.junit.Assert.*
import org.junit.Test

class AssetTest {
    @Test fun lottie_valid() {
        val candidates = listOf(
            java.io.File("app/src/main/assets/lottie/soleus-pushup.json"),
            java.io.File("src/main/assets/lottie/soleus-pushup.json")
        )
        val f = candidates.firstOrNull { it.exists() }
            ?: throw AssertionError("soleus-pushup.json bulunamadı: $candidates (cwd=${java.io.File(".").absolutePath})")
        assertTrue(f.exists())
        assertTrue(f.readText().contains("\"v\""))
    }
}
