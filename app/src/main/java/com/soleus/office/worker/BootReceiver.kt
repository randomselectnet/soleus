package com.soleus.office.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.soleus.office.data.db.AppDb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val saved = AppDb.get(context).settingsDao().get()
                HourlyReminderWorker.scheduleHourly(
                    context,
                    (saved?.intervalMin ?: 60).toLong(),
                    saved?.workStartMin ?: 540,
                    saved?.workEndMin ?: 1080
                )
            } catch (_: Exception) {
                HourlyReminderWorker.scheduleHourly(context, 60)
            } finally {
                pending.finish()
            }
        }
    }
}
