package com.soleus.office.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.soleus.office.data.model.Exercise
import com.soleus.office.ui.theme.IconBubble
import com.soleus.office.ui.theme.Quicksand
import com.soleus.office.ui.theme.SereneCard
import com.soleus.office.ui.theme.SerenePrimary
import com.soleus.office.ui.theme.SerenePrimaryFixedDim
import com.soleus.office.ui.theme.SereneSurfaceContainerLow
import com.soleus.office.ui.theme.exerciseIcon

/**
 * Hareketler ekranı (manage_habits): başlık + alt açıklama + 11 beyaz kart.
 * Her kart: ikon balonu + ad + alt açıklama + toggle (açık = adaçayı).
 * Toggle durumu Room exercise_prefs'te tutulur; satır yokluğu = açık.
 * Kapalı hareket rotasyona girmez (fail-safe: tümü kapalıysa tüm liste).
 * Karta dokunma detay ekranını açar (animasyon + adımlar); toggle bağımsızdır.
 */
@Composable
fun ExerciseListScreen(
    exercises: List<Exercise>,
    disabledIds: Set<String> = emptySet(),
    onToggle: (String, Boolean) -> Unit = { _, _ -> },
    onOpen: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 8.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Hareketler",
            style = MaterialTheme.typography.headlineLarge,
            fontFamily = Quicksand
        )
        Text(
            text = "Odaklanmak istediğin küçük rutinleri seç.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(exercises, key = { it.id }) { exercise ->
                val acik = exercise.id !in disabledIds
                SereneCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            role = Role.Button,
                            onClickLabel = "Hareket detayı",
                            onClick = { onOpen(exercise.id) }
                        ),
                    contentPadding = 16.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        IconBubble(
                            icon = exerciseIcon(exercise.id),
                            size = 48.dp,
                            container = SereneSurfaceContainerLow,
                            content = SerenePrimary,
                            contentDescription = null
                        )
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = exercise.trName,
                                style = MaterialTheme.typography.titleMedium,
                                fontFamily = Quicksand,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = exercise.faydaKisa,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Switch(
                            checked = acik,
                            onCheckedChange = { onToggle(exercise.id, it) },
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = SerenePrimaryFixedDim,
                                checkedThumbColor = Color.White,
                                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                                uncheckedThumbColor = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}
