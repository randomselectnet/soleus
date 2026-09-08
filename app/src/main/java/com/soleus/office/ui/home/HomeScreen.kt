package com.soleus.office.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.soleus.office.ui.theme.Kagit
import com.soleus.office.ui.theme.Murekkep

/**
 * Bugün ekranı: sıradaki hareket kartı + Başla + tüm hareketler.
 * Tüm metinler Türkçe, dokunma hedefleri en az 44dp.
 */
@Composable
fun HomeScreen(
    nextName: String,
    nextDurationSec: Int,
    streak: Int = 0,
    onStart: () -> Unit,
    onOpenList: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Bugün",
            style = MaterialTheme.typography.displayLarge
        )
        Text(
            text = "Seri: $streak gün",
            style = MaterialTheme.typography.bodyLarge
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Kagit)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Sıradaki hareket",
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = nextName,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Süre: ${nextDurationSec / 60} dk",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Murekkep
                )
            }
        }
        Button(
            onClick = onStart,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
        ) {
            Text(text = "Başla")
        }
        OutlinedButton(
            onClick = onOpenList,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
        ) {
            Text(text = "Tüm hareketler")
        }
    }
}
