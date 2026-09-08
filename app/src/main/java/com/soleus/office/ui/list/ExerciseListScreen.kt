package com.soleus.office.ui.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.soleus.office.data.db.Exercise
import com.soleus.office.domain.durationLabel
import kotlinx.coroutines.delay

/**
 * Hareket listesi: 11 kart, kademeli (staggered) giriş animasyonu.
 */
@Composable
fun ExerciseListScreen(
    exercises: List<Exercise>,
    onOpen: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text(
            text = "Hareketler",
            style = MaterialTheme.typography.displayLarge
        )
        LazyColumn(
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(exercises, key = { _, e -> e.id }) { index, exercise ->
                var gorunur by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    delay(index * 45L)
                    gorunur = true
                }
                AnimatedVisibility(
                    visible = gorunur,
                    enter = fadeIn() + slideInVertically { it / 4 }
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 64.dp)
                            .clickable { onOpen(exercise.id) }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = exercise.trName,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "Süre: ${durationLabel(exercise.durationSec)}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}
