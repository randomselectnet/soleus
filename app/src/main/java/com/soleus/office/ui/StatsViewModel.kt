package com.soleus.office.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.soleus.office.data.db.AppDb
import com.soleus.office.ui.stats.last7DayCounts
import com.soleus.office.ui.stats.streakFromLogs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * İstatistik ekranının veri kaynağı.
 * Son 30 günün loglarını okuyup streak + son 7 günün günlük sayılarını yayınlar.
 */
class StatsViewModel(application: Application) : AndroidViewModel(application) {

    private val appCtx = application.applicationContext

    private val _streak = MutableStateFlow(0)
    val streak: StateFlow<Int> = _streak.asStateFlow()

    private val _weeklyCounts = MutableStateFlow(List(7) { 0 })
    val weeklyCounts: StateFlow<List<Int>> = _weeklyCounts.asStateFlow()

    init {
        refresh()
    }

    /** Loglardan streak + haftalık sayıları yeniden hesapla. */
    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            val logs = runCatching {
                AppDb.get(appCtx).logDao()
                    .logsSince(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000)
            }.getOrDefault(emptyList())
            val today = LocalDate.now()
            _streak.value = streakFromLogs(logs, today)
            _weeklyCounts.value = last7DayCounts(logs, today)
        }
    }
}
