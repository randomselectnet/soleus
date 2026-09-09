package com.soleus.office.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.soleus.office.data.db.AppDb
import com.soleus.office.ui.stats.GunHucresi
import com.soleus.office.ui.stats.ayHucreleri
import com.soleus.office.ui.stats.ayToplami
import com.soleus.office.ui.stats.last7DayCounts
import com.soleus.office.ui.stats.streakFromLogs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

private val TR = Locale("tr")

/** "Kasım Desenleri" tarzı ay başlığı. */
fun ayBasligi(ay: YearMonth): String {
    val ad = ay.month.getDisplayName(TextStyle.FULL_STANDALONE, TR)
        .replaceFirstChar { it.uppercase(TR) }
    return "$ad Desenleri"
}

/**
 * Geçmiş ekranının veri kaynağı.
 * Son 90 günün loglarını okuyup streak + son 7 gün + ay görünümünü yayınlar.
 */
class StatsViewModel(application: Application) : AndroidViewModel(application) {

    private val appCtx = application.applicationContext

    private val _streak = MutableStateFlow(0)
    val streak: StateFlow<Int> = _streak.asStateFlow()

    private val _weeklyCounts = MutableStateFlow(List(7) { 0 })
    val weeklyCounts: StateFlow<List<Int>> = _weeklyCounts.asStateFlow()

    private val _monthTitle = MutableStateFlow(ayBasligi(YearMonth.now()))
    val monthTitle: StateFlow<String> = _monthTitle.asStateFlow()

    private val _monthCount = MutableStateFlow(0)
    val monthCount: StateFlow<Int> = _monthCount.asStateFlow()

    private val _monthCells = MutableStateFlow<List<GunHucresi?>>(emptyList())
    val monthCells: StateFlow<List<GunHucresi?>> = _monthCells.asStateFlow()

    init {
        refresh()
    }

    /** Loglardan tüm istatistikleri yeniden hesapla. */
    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            val logs = runCatching {
                AppDb.get(appCtx).logDao()
                    .logsSince(System.currentTimeMillis() - 90L * 24 * 60 * 60 * 1000)
            }.getOrDefault(emptyList())
            val today = LocalDate.now()
            val ay = YearMonth.now()
            _streak.value = streakFromLogs(logs, today)
            _weeklyCounts.value = last7DayCounts(logs, today)
            _monthTitle.value = ayBasligi(ay)
            _monthCount.value = ayToplami(logs, ay)
            _monthCells.value = ayHucreleri(logs, ay, today)
        }
    }
}
