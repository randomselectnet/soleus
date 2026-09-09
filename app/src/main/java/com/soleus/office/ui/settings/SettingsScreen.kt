package com.soleus.office.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.soleus.office.data.QuietPrefs
import com.soleus.office.ui.info.BILGI_SAYFA_BASLIKLARI
import com.soleus.office.ui.info.BILGI_SAYFA_IDS
import com.soleus.office.ui.info.GuvenlikDipnotu
import com.soleus.office.ui.theme.Quicksand
import com.soleus.office.ui.theme.SereneCard
import com.soleus.office.ui.theme.SerenePillButton
import com.soleus.office.ui.theme.SerenePrimary
import com.soleus.office.ui.theme.SereneSurfaceContainerLow

/** İzin verilen hatırlatma sıklıkları (dk). Saatlik / 2 saatte bir / özel ritim. */
val ALLOWED_INTERVALS = setOf(30, 45, 60, 90, 120)

/** Saatlik ve 2 saatte bir dışındaki özel ritim seçenekleri. */
val CUSTOM_INTERVALS = listOf(30, 45, 60, 90, 120)

/** Mesai aralığı geçerli mi: ikisi de gün içinde ve başlangıç < bitiş. */
fun isWorkRangeValid(startMin: Int, endMin: Int): Boolean =
    startMin in 0..1439 && endMin in 0..1439 && startMin < endMin

fun formatMinutes(min: Int): String {
    val m = min.coerceIn(0, 1439)
    return "%02d:%02d".format(m / 60, m % 60)
}

private enum class Ritim { SAATLIK, IKI_SAAT, OZEL }

private fun ritimOf(interval: Int): Ritim = when (interval) {
    60 -> Ritim.SAATLIK
    120 -> Ritim.IKI_SAAT
    else -> Ritim.OZEL
}

@Composable
private fun RitimSatiri(
    ikon: ImageVector,
    baslik: String,
    alt: String,
    secili: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(8.dp)
            .heightIn(min = 48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (secili) SerenePrimary.copy(alpha = 0.12f)
                    else SereneSurfaceContainerLow
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = ikon,
                contentDescription = null,
                tint = if (secili) SerenePrimary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = baslik, style = MaterialTheme.typography.titleSmall)
            Text(
                text = alt,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
        if (secili) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = "Seçili",
                tint = SerenePrimary,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Spacer(modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
private fun HapSecici(
    etiket: String,
    deger: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF5F3ED))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp)
            .heightIn(min = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = etiket,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = deger,
            style = MaterialTheme.typography.titleMedium,
            color = SerenePrimary
        )
    }
}

/**
 * Ayarlar ekranı (reminders): "Hatırlatma Ritmi" kartı (saatlik / 2 saatte bir /
 * özel ritim + seçili tik) + "Sessiz Saatler" kartı (açma-kapama + başlangıç-bitiş
 * hap seçiciler; mesai time picker'ları bu kartta korunur).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    workStartMin: Int = 540,
    workEndMin: Int = 1080,
    intervalMin: Int = 60,
    quiet: QuietPrefs = QuietPrefs(),
    onSave: (Int, Int, Int, QuietPrefs, (Boolean) -> Unit) -> Unit =
        { _, _, _, _, done -> done(true) },
    saveError: String? = null,
    onSaved: () -> Unit = {},
    onOpenBilgi: (String) -> Unit = {}
) {
    var start by remember(workStartMin) { mutableIntStateOf(workStartMin) }
    var end by remember(workEndMin) { mutableIntStateOf(workEndMin) }
    var interval by remember(intervalMin) { mutableIntStateOf(intervalMin) }
    var quietEnabled by remember(quiet) { mutableStateOf(quiet.enabled) }
    var quietStart by remember(quiet) { mutableIntStateOf(quiet.startMin) }
    var quietEnd by remember(quiet) { mutableIntStateOf(quiet.endMin) }
    var ritim by remember(intervalMin) { mutableStateOf(ritimOf(intervalMin)) }

    var pickerTarget by remember { mutableStateOf<String?>(null) }
    var saved by remember { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }

    val valid = isWorkRangeValid(start, end)

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
                text = "Hatırlatmalar",
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = Quicksand
            )
            Text(
                text = "Gün içinde seni dengede tutacak nazik dürtüler ayarla.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Hatırlatma Ritmi",
                style = MaterialTheme.typography.headlineMedium,
                fontFamily = Quicksand
            )
            SereneCard(modifier = Modifier.fillMaxWidth(), contentPadding = 16.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    RitimSatiri(
                        ikon = Icons.Filled.HourglassEmpty,
                        baslik = "Saatlik",
                        alt = "Nefes alacak bir an",
                        secili = ritim == Ritim.SAATLIK,
                        onClick = { ritim = Ritim.SAATLIK; interval = 60 }
                    )
                    RitimSatiri(
                        ikon = Icons.Filled.Update,
                        baslik = "2 saatte bir",
                        alt = "Biraz daha seyrek",
                        secili = ritim == Ritim.IKI_SAAT,
                        onClick = { ritim = Ritim.IKI_SAAT; interval = 120 }
                    )
                    RitimSatiri(
                        ikon = Icons.Filled.Tune,
                        baslik = "Özel ritim",
                        alt = "Kendi temponu ayarla",
                        secili = ritim == Ritim.OZEL,
                        onClick = {
                            ritim = Ritim.OZEL
                            if (interval == 60 || interval == 120) interval = 45
                        }
                    )
                    if (ritim == Ritim.OZEL) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CUSTOM_INTERVALS.forEach { secenek ->
                                val seciliHap = interval == secenek
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .heightIn(min = 48.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (seciliHap) SerenePrimary
                                            else SerenePrimary.copy(alpha = 0.12f)
                                        )
                                        .clickable(
                                            role = Role.Button,
                                            onClick = { interval = secenek }
                                        )
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$secenek dk",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (seciliHap) Color.White else SerenePrimary,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Sessiz Saatler",
                style = MaterialTheme.typography.headlineMedium,
                fontFamily = Quicksand
            )
            SereneCard(modifier = Modifier.fillMaxWidth(), contentPadding = 20.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Sessiz saatler",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = "Tüm bildirimleri duraklat",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                    .copy(alpha = 0.7f)
                            )
                        }
                        Switch(
                            checked = quietEnabled,
                            onCheckedChange = { quietEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = SerenePrimary,
                                checkedThumbColor = Color.White
                            )
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HapSecici(
                            etiket = "Başlangıç",
                            deger = formatMinutes(quietStart),
                            onClick = { pickerTarget = "quietStart" },
                            modifier = Modifier.weight(1f)
                        )
                        HapSecici(
                            etiket = "Bitiş",
                            deger = formatMinutes(quietEnd),
                            onClick = { pickerTarget = "quietEnd" },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Text(
                        text = "Mesai saatleri",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HapSecici(
                            etiket = "Başlangıç",
                            deger = formatMinutes(start),
                            onClick = { pickerTarget = "workStart" },
                            modifier = Modifier.weight(1f)
                        )
                        HapSecici(
                            etiket = "Bitiş",
                            deger = formatMinutes(end),
                            onClick = { pickerTarget = "workEnd" },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (!valid) {
                        Text(
                            text = "Başlangıç saati bitişten önce olmalı.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Bilgi & Güvenlik",
                style = MaterialTheme.typography.headlineMedium,
                fontFamily = Quicksand
            )
            SereneCard(modifier = Modifier.fillMaxWidth(), contentPadding = 16.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    BILGI_SAYFA_IDS.forEach { sayfaId ->
                        RitimSatiri(
                            ikon = Icons.Filled.Info,
                            baslik = BILGI_SAYFA_BASLIKLARI[sayfaId].orEmpty(),
                            alt = "Oku",
                            secili = false,
                            onClick = { onOpenBilgi(sayfaId) }
                        )
                    }
                }
            }
            SereneCard(modifier = Modifier.fillMaxWidth(), contentPadding = 20.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Güvenlik notu",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "Bu hareketler genel esenlik amaçlıdır, tedavi değildir.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    GuvenlikDipnotu()
                }
            }
        }

        SerenePillButton(
            text = if (saving) "Kaydediliyor…" else "Kaydet",
            onClick = {
                if (!valid || saving) return@SerenePillButton
                if (interval !in ALLOWED_INTERVALS) return@SerenePillButton
                saving = true
                onSave(
                    start, end, interval,
                    QuietPrefs(quietEnabled, quietStart, quietEnd)
                ) { ok ->
                    saving = false
                    if (ok) {
                        saved = true
                        onSaved()
                    }
                }
            },
            enabled = valid && !saving && interval in ALLOWED_INTERVALS,
            modifier = Modifier.fillMaxWidth()
        )
        if (saved) {
            Text(
                text = "Kaydedildi. Hatırlatmalar güncellendi.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        if (saveError != null) {
            Text(
                text = saveError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    val hedef = pickerTarget
    if (hedef != null) {
        val (baslik, mevcut) = when (hedef) {
            "quietStart" -> "Sessiz saat başlangıcı" to quietStart
            "quietEnd" -> "Sessiz saat bitişi" to quietEnd
            "workStart" -> "Mesai başlangıcı" to start
            "workEnd" -> "Mesai bitişi" to end
            else -> null to null
        }
        if (baslik != null && mevcut != null) {
            val state = rememberTimePickerState(
                initialHour = mevcut / 60,
                initialMinute = mevcut % 60
            )
            AlertDialog(
                onDismissRequest = { pickerTarget = null },
                title = { Text(text = baslik) },
                text = { TimePicker(state = state) },
                confirmButton = {
                    TextButton(onClick = {
                        val secilen = state.hour * 60 + state.minute
                        when (hedef) {
                            "quietStart" -> quietStart = secilen
                            "quietEnd" -> quietEnd = secilen
                            "workStart" -> start = secilen
                            "workEnd" -> end = secilen
                        }
                        pickerTarget = null
                    }) { Text(text = "Tamam") }
                },
                dismissButton = {
                    TextButton(onClick = { pickerTarget = null }) { Text(text = "Vazgeç") }
                }
            )
        }
    }
}
