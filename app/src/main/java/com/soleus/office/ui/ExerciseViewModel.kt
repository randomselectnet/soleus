package com.soleus.office.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.soleus.office.data.ContentLoader
import com.soleus.office.data.db.AppDb
import com.soleus.office.data.db.SessionLog
import com.soleus.office.data.model.Exercise
import com.soleus.office.ui.stats.streakFromLogs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

/**
 * Egzersiz listesi + streak için tek veri kaynağı.
 * ContentLoader.load bir kez çalışır; streak LogDao.recent üzerinden hesaplanır.
 */
class ExerciseViewModel(application: Application) : AndroidViewModel(application) {

    private val appCtx = application.applicationContext
    private val logDao by lazy { AppDb.get(appCtx).logDao() }

    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val exercises: StateFlow<List<Exercise>> = _exercises.asStateFlow()

    private val _streak = MutableStateFlow(0)
    val streak: StateFlow<Int> = _streak.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _exercises.value = runCatching { ContentLoader.load(appCtx) }
                .getOrDefault(emptyList())
            refreshStreak()
        }
    }

    private suspend fun refreshStreak() {
        val logs = runCatching { logDao.recent(365) }.getOrDefault(emptyList())
        _streak.value = streakFromLogs(logs, LocalDate.now())
    }

    fun logCompletion(exerciseId: String, durationSec: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                logDao.insert(
                    SessionLog(
                        exerciseId = exerciseId,
                        timestampMillis = System.currentTimeMillis(),
                        durationDoneSec = durationSec
                    )
                )
            }
            refreshStreak()
        }
    }

    /** Streak'i manuel yenile (örn. ekrana dönüşte). Test edilebilirlik için public. */
    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) { refreshStreak() }
    }
}
