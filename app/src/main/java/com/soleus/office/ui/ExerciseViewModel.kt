package com.soleus.office.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.soleus.office.data.ContentLoader
import com.soleus.office.data.db.AppDb
import com.soleus.office.data.db.ExercisePref
import com.soleus.office.data.db.SessionLog
import com.soleus.office.data.model.Exercise
import com.soleus.office.ui.stats.streakFromLogs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * Egzersiz listesi + streak + tercih + bugünkü sayı için tek veri kaynağı.
 * ContentLoader.load bir kez çalışır; tercihler Room exercise_prefs'te tutulur
 * (satır yokluğu = açık).
 */
class ExerciseViewModel(application: Application) : AndroidViewModel(application) {

    private val appCtx = application.applicationContext
    private val db by lazy { AppDb.get(appCtx) }

    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val exercises: StateFlow<List<Exercise>> = _exercises.asStateFlow()

    private val _streak = MutableStateFlow(0)
    val streak: StateFlow<Int> = _streak.asStateFlow()

    private val _disabledIds = MutableStateFlow<Set<String>>(emptySet())
    /** Kapalı egzersiz id'leri (boş = hepsi açık). */
    val disabledIds: StateFlow<Set<String>> = _disabledIds.asStateFlow()

    private val _recentIds = MutableStateFlow<List<String>>(emptyList())
    /** Rotasyon için son tamamlanan id'ler (yeniden eskiye). */
    val recentIds: StateFlow<List<String>> = _recentIds.asStateFlow()

    private val _todayCount = MutableStateFlow(0)
    /** Bugünkü tamamlanma sayısı. */
    val todayCount: StateFlow<Int> = _todayCount.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _exercises.value = runCatching { ContentLoader.load(appCtx) }
                .getOrDefault(emptyList())
            refreshInternal()
        }
    }

    private suspend fun refreshInternal() {
        val logs = runCatching { db.logDao().recent(365) }.getOrDefault(emptyList())
        val today = LocalDate.now()
        _streak.value = streakFromLogs(logs, today)
        _recentIds.value = logs.map { it.exerciseId }
        _todayCount.value = logs.count {
            Instant.ofEpochMilli(it.timestampMillis).atZone(ZoneId.systemDefault())
                .toLocalDate() == today
        }
        _disabledIds.value = runCatching { db.prefDao().disabledIds().toSet() }
            .getOrDefault(emptySet())
    }

    fun logCompletion(exerciseId: String, durationSec: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                db.logDao().insert(
                    SessionLog(
                        exerciseId = exerciseId,
                        timestampMillis = System.currentTimeMillis(),
                        durationDoneSec = durationSec
                    )
                )
            }
            refreshInternal()
        }
    }

    /** Egzersiz tercihini kaydet (toggle). */
    fun setExerciseEnabled(exerciseId: String, enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching { db.prefDao().upsert(ExercisePref(exerciseId, enabled)) }
            _disabledIds.value = runCatching { db.prefDao().disabledIds().toSet() }
                .getOrDefault(_disabledIds.value)
        }
    }

    /** Streak + bugün + tercihleri manuel yenile (örn. ekrana dönüşte). */
    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) { refreshInternal() }
    }
}
