package com.soleus.office

import org.junit.Assert.*
import org.junit.Test

class AssetTest {
    private fun assetFile(rel: String): java.io.File {
        val candidates = listOf(
            java.io.File("app/src/main/assets/$rel"),
            java.io.File("src/main/assets/$rel")
        )
        return candidates.firstOrNull { it.exists() }
            ?: throw AssertionError("$rel bulunamadı: $candidates (cwd=${java.io.File(".").absolutePath})")
    }

    /** Parça 1 (alt-vücut): exercises_tr.json'daki animationAsset yollarıyla birebir aynı adlar. */
    private val part1LowerBody = listOf(
        "lottie/soleus-pushup.json",
        "lottie/ankle-circle-calf-raise.json",
        "lottie/hip-squeeze.json",
        "lottie/seated-leg-extension.json",
        "lottie/standup-mini-set.json"
    )

    @Test fun lottie_valid() {
        val f = assetFile("lottie/soleus-pushup.json")
        assertTrue(f.exists())
        assertTrue(f.readText().contains("\"v\""))
    }

    @Test fun lottie_part1_lowerBody_exists() {
        for (rel in part1LowerBody) {
            assertTrue("$rel yok", assetFile(rel).exists())
        }
    }

    @Test fun lottie_part1_validStructure() {
        val layersNonEmpty = Regex("\"layers\"\\s*:\\s*\\[\\s*\\{")
        for (rel in part1LowerBody) {
            val text = assetFile(rel).readText()
            assertTrue("$rel: \"v\" yok", text.contains("\"v\""))
            assertTrue("$rel: layers boş", layersNonEmpty.containsMatchIn(text))
            assertTrue("$rel: 100KB üstü (${assetFile(rel).length()})", assetFile(rel).length() < 100 * 1024)
        }
    }

    @Test fun lottie_part1_referencedByContent() {
        val content = assetFile("content/exercises_tr.json").readText()
        val referenced = Regex("\"animationAsset\"\\s*:\\s*\"([^\"]+)\"")
            .findAll(content).map { it.groupValues[1] }.toSet()
        for (rel in part1LowerBody) {
            assertTrue("$rel exercises_tr.json'da referanslı değil", rel in referenced)
        }
    }

    /** Parça 2 (üst-vücut + göz/nefes): exercises_tr.json'daki animationAsset yollarıyla birebir aynı adlar. */
    private val part2UpperBody = listOf(
        "lottie/neck-side-stretch.json",
        "lottie/shoulder-shrug-roll.json",
        "lottie/scapular-squeeze.json",
        "lottie/seated-trunk-rotation.json",
        "lottie/wrist-forearm-stretch.json",
        "lottie/eye-202020-breath-444.json"
    )

    @Test fun lottie_part2_upperBody_exists() {
        for (rel in part2UpperBody) {
            assertTrue("$rel yok", assetFile(rel).exists())
        }
    }

    @Test fun lottie_part2_validStructure() {
        val layersNonEmpty = Regex("\"layers\"\\s*:\\s*\\[\\s*\\{")
        for (rel in part2UpperBody) {
            val text = assetFile(rel).readText()
            assertTrue("$rel: \"v\" yok", text.contains("\"v\""))
            assertTrue("$rel: layers boş", layersNonEmpty.containsMatchIn(text))
            assertTrue("$rel: 100KB üstü (${assetFile(rel).length()})", assetFile(rel).length() < 100 * 1024)
        }
    }

    @Test fun lottie_part2_referencedByContent() {
        val content = assetFile("content/exercises_tr.json").readText()
        val referenced = Regex("\"animationAsset\"\\s*:\\s*\"([^\"]+)\"")
            .findAll(content).map { it.groupValues[1] }.toSet()
        for (rel in part2UpperBody) {
            assertTrue("$rel exercises_tr.json'da referanslı değil", rel in referenced)
        }
    }
}
