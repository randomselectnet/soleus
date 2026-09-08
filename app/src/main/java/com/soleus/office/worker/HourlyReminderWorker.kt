package com.soleus.office.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.soleus.office.data.ContentLoader
import com.soleus.office.domain.dueExercise
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

        val recentIds = inputData.getStringArray(KEY_RECENT_IDS)?.toList().orEmpty()
        val ids = runCatching { ContentLoader.load(applicationContext).map { it.id } }
            .getOrDefault(emptyList())
        if (ids.isEmpty()) return Result.success()

        val nextId = dueExercise(ids, recentIds)
        val name = runCatching {
            ContentLoader.load(applicationContext).firstOrNull { it.id == nextId }?.trName
        }.getOrNull() ?: nextId
        NotificationHelper.show(applicationContext, name, nextId)
        return Result.success()
    }

    companion object {
        const val WORK_NAME = "hourly_reminder"
        const val KEY_START = "startMin"
        const val KEY_END = "endMin"
        const val KEY_RECENT_IDS = "recentIds"

        fun scheduleHourly(context: Context, intervalMin: Long = 60) {
            val safeInterval = intervalMin.coerceAtLeast(15)
            val request = PeriodicWorkRequestBuilder<HourlyReminderWorker>(safeInterval, TimeUnit.MINUTES)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}
