package com.soleus.office.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.soleus.office.data.model.Exercise
import com.soleus.office.ui.theme.Quicksand
import com.soleus.office.ui.theme.SereneBackground
import com.soleus.office.ui.theme.SereneCard
import com.soleus.office.ui.theme.SerenePillButton
import com.soleus.office.ui.theme.SerenePrimary
import kotlinx.coroutines.delay

/** Saniyeyi mm:ss biçimine çevirir. */
fun formatSure(toplamSaniye: Int): String {
    val s = toplamSaniye.coerceAtLeast(0)
    return "%02d:%02d".format(s / 60, s % 60)
}

/**
 * Hareket detayı (Serene): beyaz kart içinde krem sahne panelinde Lottie
 * (reduce-motion'da statik ilk kare), Quicksand başlık, adaçayı noktalı adım
 * listesi, hap BAŞLAT butonu, sayaç + MotionRing, hap BİTİR.
 */
@Composable
fun ExerciseDetailScreen(
    exercise: Exercise,
    onDone: () -> Unit
) {
    val toplam = exercise.durationSec.coerceAtLeast(1)
    var kalan by remember(exercise.id) { mutableIntStateOf(exercise.durationSec) }
    var calisiyor by remember(exercise.id) { mutableStateOf(false) }
    LaunchedEffect(calisiyor, exercise.id) {
        while (calisiyor && kalan > 0) {
            delay(1000)
            kalan--
        }
        if (kalan == 0) calisiyor = false
    }
    val ilerleme = 1f - kalan.toFloat() / toplam

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = exercise.trName,
            style = MaterialTheme.typography.headlineLarge,
            fontFamily = Quicksand,
            modifier = Modifier.fillMaxWidth()
        )
        SereneCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Sahne: krem panel üstünde GIF-stili çizgi figür.
                val context = LocalContext.current
                val animasyonuAzalt = remember {
                    try {
                        android.provider.Settings.Global.getFloat(
                            context.contentResolver,
                            android.provider.Settings.Global.ANIMATOR_DURATION_SCALE,
                            1f
                        ) == 0f
                    } catch (_: Exception) {
                        false
                    }
                }
                val composition by rememberLottieComposition(
                    LottieCompositionSpec.Asset(exercise.animationAsset)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(SereneBackground)
                        .semantics { contentDescription = exercise.trName },
                    contentAlignment = Alignment.Center
                ) {
                    if (composition != null) {
                        if (animasyonuAzalt) {
                            LottieAnimation(
                                composition = composition,
                                progress = { 0f },
                                modifier = Modifier.size(200.dp)
                            )
                        } else {
                            LottieAnimation(
                                composition = composition,
                                iterations = LottieConstants.IterateForever,
                                modifier = Modifier.size(200.dp)
                            )
                        }
                    } else {
                        Text(
                            text = "Animasyon yükleniyor\n${exercise.animationAsset}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    exercise.steps.forEach { adim ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 10.dp)
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(SerenePrimary)
                            )
                            Text(
                                text = adim,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                Text(
                    text = "Fayda: ${exercise.benefit}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Dikkat: ${exercise.caution}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
                MotionRing(progress = ilerleme)
                Text(
                    text = formatSure(kalan),
                    style = MaterialTheme.typography.headlineLarge,
                    fontFamily = Quicksand
                )
                SerenePillButton(
                    text = if (calisiyor) "DURAKLAT" else "BAŞLAT",
                    onClick = {
                        if (kalan == 0) kalan = toplam
                        calisiyor = !calisiyor
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedButton(
                    onClick = onDone,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp),
                    shape = CircleShape
                ) {
                    Text(text = "BİTİR")
                }
            }
        }
    }
}
