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

    private val _saveError = MutableStateFlow<String?>(null)
    /** Son kaydetme hatası (null = hata yok). */
    val saveError: StateFlow<String?> = _saveError.asStateFlow()

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
     * [onResult] ana thread'te çağrılır: true = kaydedildi, false = hata.
     */
    fun save(
        workStartMin: Int,
        workEndMin: Int,
        intervalMin: Int,
        onResult: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = ReminderSettings(
                workStartMin = workStartMin,
                workEndMin = workEndMin,
                intervalMin = intervalMin
            )
            val ok = runCatching {
                AppDb.get(appCtx).settingsDao().upsert(updated)
            }.isSuccess
            withContext(Dispatchers.Main) {
                if (ok) {
                    _saveError.value = null
                    _settings.value = updated
                    HourlyReminderWorker.scheduleHourly(
                        appCtx,
                        intervalMin.toLong(),
                        workStartMin,
                        workEndMin
                    )
                } else {
                    _saveError.value = "Ayar kaydedilemedi. Tekrar deneyin."
                }
                onResult(ok)
            }
        }
    }
}
