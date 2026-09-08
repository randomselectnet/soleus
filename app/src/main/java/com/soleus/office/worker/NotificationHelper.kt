package com.soleus.office.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.soleus.office.MainActivity

object NotificationHelper {
    const val CHANNEL_ID = "soleus_reminder"
    private const val NOTIFICATION_ID = 1001

    fun show(context: Context, exerciseName: String, exerciseId: String) {
        ensureChannel(context)
        if (Build.VERSION.SDK_INT >= 33 &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        // Bildirime dokununca ilgili hareketin detay ekranı açılır.
        val deepLink = Intent(
            Intent.ACTION_VIEW,
            "soleus://detail/$exerciseId".toUri(),
            context,
            MainActivity::class.java
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val tap = PendingIntent.getActivity(
            context,
            exerciseId.hashCode(),
            deepLink,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Hareket zamanı")
            .setContentText("$exerciseName — 2 dk")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(tap)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(
            exerciseId.hashCode().takeIf { it != 0 } ?: NOTIFICATION_ID,
            notification
        )
    }

    private fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (manager.getNotificationChannel(CHANNEL_ID) == null) {
            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    "Saatlik hatırlatıcı",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }
    }
}
