package com.soleus.office.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.soleus.office.ui.theme.IconBubble
import com.soleus.office.ui.theme.Quicksand
import com.soleus.office.ui.theme.SereneCard
import com.soleus.office.ui.theme.SereneOnTertiaryContainer
import com.soleus.office.ui.theme.SerenePillButton
import com.soleus.office.ui.theme.SerenePrimary
import com.soleus.office.ui.theme.SereneTertiary
import com.soleus.office.ui.theme.exerciseIcon
import java.time.LocalTime

/**
 * Saate göre selamlama: 05-12 Günaydın, 12-18 Tünaydın, diğer İyi akşamlar.
 * Sınırlar [05,12) ve [12,18) aralıklarıdır.
 */
fun greetingForHour(hour: Int): String = when (hour) {
    in 5..11 -> "Günaydın."
    in 12..17 -> "Tünaydın."
    else -> "İyi akşamlar."
}

/**
 * Bugün ekranı (today_s_nudge): selamlama + odak hareket kartı + günün ilerlemesi.
 * Tüm metinler Türkçe; başlıklar Quicksand, kartlar SereneCard, buton hap.
 */
@Composable
fun HomeScreen(
    nextId: String,
    nextName: String,
    nextDesc: String,
    streak: Int = 0,
    doneCount: Int = 0,
    totalCount: Int = 1,
    currentHour: Int = LocalTime.now().hour,
    onStart: () -> Unit = {},
    onDone: () -> Unit
) {
    var tamamlandi by remember(nextId) { mutableStateOf(false) }
    val ilerleme = (doneCount.coerceAtLeast(0).toFloat() /
        totalCount.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = greetingForHour(currentHour),
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = Quicksand
            )
            Text(
                text = "Bu saatin odağı.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        SereneCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                IconBubble(
                    icon = exerciseIcon(nextId),
                    size = 64.dp,
                    contentDescription = nextName
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = nextName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = Quicksand
                )
                Text(
                    text = nextDesc,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = onStart) {
                    Text(
                        text = "Hareketi izle",
                        style = MaterialTheme.typography.labelLarge,
                        color = SereneTertiary
                    )
                }
                SerenePillButton(
                    text = if (tamamlandi) "TAMAMLANDI" else "YAPILDI",
                    icon = Icons.Filled.Check,
                    enabled = !tamamlandi,
                    onClick = {
                        tamamlandi = true
                        onDone()
                    }
                )
            }
        }

        SereneCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = 24.dp
        ) {
            // İkinci kart zemin tonu: açık gri kapsayıcı (tasarım).
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "BUGÜNÜN İLERLEMESİ",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "$doneCount / $totalCount",
                        style = MaterialTheme.typography.headlineMedium,
                        fontFamily = Quicksand
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.LocalFireDepartment,
                            contentDescription = null,
                            tint = SereneTertiary,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            text = "$streak gündür devam",
                            style = MaterialTheme.typography.labelMedium,
                            color = SereneOnTertiaryContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { ilerleme },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(CircleShape),
                    color = SerenePrimary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    strokeCap = StrokeCap.Round
                )
            }
        }
    }
}
