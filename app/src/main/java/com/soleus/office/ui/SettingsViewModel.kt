package com.soleus.office.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.soleus.office.data.db.AppDb
import com.soleus.office.data.db.ReminderSettings
import com.soleus.office.worker.HourlyReminderWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Ayarlar ekranının veri kaynağı.
 * Kayıtlı hatırlatma ayarını yayınlar; kaydetme Room upsert +
 * [HourlyReminderWorker.scheduleHourly] yapar.
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val appCtx = application.applicationContext

    private val _settings = MutableStateFlow<ReminderSettings?>(null)
    val settings: StateFlow<ReminderSettings?> = _settings.asStateFlow()

    init {
        refresh()
    }

    /** Kayıtlı ayarı yeniden oku (null = henüz yüklenmedi). */
    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            _settings.value = runCatching {
                AppDb.get(appCtx).settingsDao().get()
            }.getOrNull()
        }
    }

    /**
     * Ayarı kaydet: Room upsert + saatlik hatırlatıcıyı güncelle.
     * [onSaved] ana thread'te çağrılır.
     */
    fun save(workStartMin: Int, workEndMin: Int, intervalMin: Int, onSaved: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = ReminderSettings(
                workStartMin = workStartMin,
                workEndMin = workEndMin,
                intervalMin = intervalMin
            )
            runCatching { AppDb.get(appCtx).settingsDao().upsert(updated) }
            _settings.value = updated
            HourlyReminderWorker.scheduleHourly(
                appCtx,
                intervalMin.toLong(),
                workStartMin,
                workEndMin
            )
            withContext(Dispatchers.Main) { onSaved() }
        }
    }
}
