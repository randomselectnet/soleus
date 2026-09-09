package com.soleus.office.ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.soleus.office.R

// İndirilebilir font sağlayıcısı (Google Fonts / GMS). Cihaz çevrimdışıyken ya da
// Play Hizmetleri yokken indirme yapılamaz; Compose yüklenemeyen fontta sistem
// fontuna düşer, uygulama çökmez. Ek güvence: aile kurulamazsa SansSerif yedeği.
private val SereneFontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

private fun indirilebilirAile(font: GoogleFont, agirliklar: List<FontWeight>): FontFamily =
    try {
        FontFamily(agirliklar.map { Font(googleFont = font, fontProvider = SereneFontProvider, weight = it) })
    } catch (_: Exception) {
        FontFamily.SansSerif
    }

// Başlıklar: Quicksand 600 (+ 500, DESIGN.md headline-md için).
val Quicksand: FontFamily = indirilebilirAile(
    GoogleFont("Quicksand"),
    listOf(FontWeight.Medium, FontWeight.SemiBold)
)

// Gövde/etiket: Inter 400/500/600.
val Inter: FontFamily = indirilebilirAile(
    GoogleFont("Inter"),
    listOf(FontWeight.Normal, FontWeight.Medium, FontWeight.SemiBold)
)
