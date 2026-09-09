package com.soleus.office.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.soleus.office.data.ContentLoader
import com.soleus.office.data.QuietStore
import com.soleus.office.data.db.AppDb
import com.soleus.office.domain.dueExercise
import com.soleus.office.domain.enabledIds
import com.soleus.office.domain.isQuietTime
import com.soleus.office.domain.shouldRemind
import java.util.Calendar
import java.util.concurrent.TimeUnit

class HourlyReminderWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val start = inputData.getInt(KEY_START, 540)
        val end = inputData.getInt(KEY_END, 1080)

        val cal = Calendar.getInstance()
        val nowMin = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
        if (!shouldRemind(nowMin, start, end)) return Result.success()

        // Sessiz saatler: aralık içindeyse bildirim atma.
        val quiet = runCatching { QuietStore.load(applicationContext) }.getOrNull()
        if (quiet != null && isQuietTime(nowMin, quiet.startMin, quiet.endMin, quiet.enabled)) {
            return Result.success()
        }

        val inputRecent = inputData.getStringArray(KEY_RECENT_IDS)?.toList().orEmpty()
        val egzersizler = runCatching { ContentLoader.load(applicationContext) }
            .getOrDefault(emptyList())
        if (egzersizler.isEmpty()) return Result.success()

        // Kapalı hareketler havuzdan çıkar (tümü kapalıysa fail-safe tüm liste).
        val kapali = runCatching {
            AppDb.get(applicationContext).prefDao().disabledIds()
        }.getOrDefault(emptyList())
        val havuz = enabledIds(egzersizler.map { it.id }, kapali)
        val ids = havuz

        // Rotasyon: son tamamlananları doğrudan DB'den oku (inputData yazılmıyor).
        val dbRecent = runCatching {
            AppDb.get(applicationContext).logDao().recent(ids.size).map { it.exerciseId }
        }.getOrDefault(emptyList())
        val recentIds = (dbRecent + inputRecent).distinct()

        val nextId = dueExercise(ids, recentIds)
        val next = egzersizler.firstOrNull { it.id == nextId }
        NotificationHelper.show(applicationContext, next?.trName ?: nextId, nextId, next?.durationSec ?: 120)
        return Result.success()
    }

    companion object {
        const val WORK_NAME = "hourly_reminder"
        const val KEY_START = "startMin"
        const val KEY_END = "endMin"
        const val KEY_RECENT_IDS = "recentIds"

        fun scheduleHourly(
            context: Context,
            intervalMin: Long = 60,
            workStartMin: Int = 540,
            workEndMin: Int = 1080
        ) {
            val safeInterval = intervalMin.coerceAtLeast(15)
            val request = PeriodicWorkRequestBuilder<HourlyReminderWorker>(safeInterval, TimeUnit.MINUTES)
                .setInputData(
                    workDataOf(
                        KEY_START to workStartMin,
                        KEY_END to workEndMin
                    )
                )
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                request
            )
        }
    }
}
