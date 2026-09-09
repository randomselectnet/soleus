package com.soleus.office.ui.motion

import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay

/**
 * Tek global sahne saati: BAŞLAT'tan beri biriken elapsedMs; duraklatınca donar.
 * Sade-gösterimde (animatör ölçeği kapalı) 1 fps storyboard'a kuantalanır.
 */
@Composable
fun rememberSahneSaati(
    calisiyor: Boolean,
    hizCarpani: Float = 1f,
    storyboardAdimMs: Long = 1000L
): Long {
    var elapsedMs by remember { mutableLongStateOf(0L) }
    val sade = sadeGosterimIsteniyor()
    LaunchedEffect(calisiyor, sade) {
        val adim = if (sade) storyboardAdimMs else 16L
        while (calisiyor) {
            delay(adim)
            val yeni = elapsedMs + (adim * hizCarpani).toLong()
            elapsedMs = if (sade) (yeni / storyboardAdimMs) * storyboardAdimMs else yeni
        }
    }
    return elapsedMs
}

/**
 * ANIMATOR_DURATION_SCALE == 0f ise sade gösterim istenir:
 * sahne 1 fps storyboard karesi gösterir, sayaç metni aynı kuantalanmış saatten akar.
 */
@Composable
fun sadeGosterimIsteniyor(): Boolean {
    val context = LocalContext.current
    return remember {
        try {
            Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1f
            ) == 0f
        } catch (_: Exception) {
            false
        }
    }
}
