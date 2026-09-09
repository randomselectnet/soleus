package com.soleus.office.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** İzin verilen hatırlatma sıklıkları (dk). Aralık: 30/45/60/90. */
val ALLOWED_INTERVALS = setOf(30, 45, 60, 90)

/** Mesai aralığı geçerli mi: ikisi de gün içinde ve başlangıç < bitiş. */
fun isWorkRangeValid(startMin: Int, endMin: Int): Boolean =
    startMin in 0..1439 && endMin in 0..1439 && startMin < endMin

fun formatMinutes(min: Int): String {
    val m = min.coerceIn(0, 1439)
    return "%02d:%02d".format(m / 60, m % 60)
}

/**
 * Ayarlar ekranı: mesai başlangıç/bitiş (TimePicker) + sıklık (30/45/60/90)
 * + kaydet -> [onSave] (üretimde [com.soleus.office.ui.SettingsViewModel.save]:
 * Room upsert + HourlyReminderWorker.scheduleHourly).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    workStartMin: Int = 540,
    workEndMin: Int = 1080,
    intervalMin: Int = 60,
    onSave: (Int, Int, Int, (Boolean) -> Unit) -> Unit = { _, _, _, done -> done(true) },
    saveError: String? = null,
    onSaved: () -> Unit = {}
) {
    var start by remember(workStartMin) { mutableIntStateOf(workStartMin) }
    var end by remember(workEndMin) { mutableIntStateOf(workEndMin) }
    var interval by remember(intervalMin) { mutableIntStateOf(intervalMin) }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }
    var saved by remember { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }

    val valid = isWorkRangeValid(start, end)
    val options = listOf(30, 45, 60, 90)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Ayarlar",
            style = MaterialTheme.typography.displayLarge
        )
        Text(
            text = "Mesai saatleri",
            style = MaterialTheme.typography.titleLarge
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { showStartPicker = true },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
            ) {
                Text(text = "Başlangıç ${formatMinutes(start)}")
            }
            Button(
                onClick = { showEndPicker = true },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
            ) {
                Text(text = "Bitiş ${formatMinutes(end)}")
            }
        }
        if (!valid) {
            Text(
                text = "Başlangıç saati bitişten önce olmalı.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Text(
            text = "Hatırlatma sıklığı",
            style = MaterialTheme.typography.titleLarge
        )
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEachIndexed { i, option ->
                SegmentedButton(
                    selected = interval == option,
                    onClick = { interval = option },
                    shape = SegmentedButtonDefaults.itemShape(i, options.size),
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Text(text = "$option dk")
                }
            }
        }
        Button(
            onClick = {
                if (!valid || saving) return@Button
                saving = true
                onSave(start, end, interval) { ok ->
                    saving = false
                    if (ok) {
                        saved = true
                        onSaved()
                    }
                }
            },
            enabled = valid && !saving,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
        ) {
            Text(text = if (saving) "Kaydediliyor…" else "Kaydet")
        }
        if (saved) {
            Text(
                text = "Kaydedildi. Hatırlatmalar güncellendi.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        if (saveError != null) {
            Text(
                text = saveError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }

    if (showStartPicker) {
        val state = rememberTimePickerState(
            initialHour = start / 60,
            initialMinute = start % 60
        )
        AlertDialog(
            onDismissRequest = { showStartPicker = false },
            title = { Text(text = "Mesai başlangıcı") },
            text = { TimePicker(state = state) },
            confirmButton = {
                TextButton(onClick = {
                    start = state.hour * 60 + state.minute
                    showStartPicker = false
                }) { Text(text = "Tamam") }
            },
            dismissButton = {
                TextButton(onClick = { showStartPicker = false }) { Text(text = "Vazgeç") }
            }
        )
    }
    if (showEndPicker) {
        val state = rememberTimePickerState(
            initialHour = end / 60,
            initialMinute = end % 60
        )
        AlertDialog(
            onDismissRequest = { showEndPicker = false },
            title = { Text(text = "Mesai bitişi") },
            text = { TimePicker(state = state) },
            confirmButton = {
                TextButton(onClick = {
                    end = state.hour * 60 + state.minute
                    showEndPicker = false
                }) { Text(text = "Tamam") }
            },
            dismissButton = {
                TextButton(onClick = { showEndPicker = false }) { Text(text = "Vazgeç") }
            }
        )
    }
}
