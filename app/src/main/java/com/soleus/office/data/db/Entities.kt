package com.soleus.office.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "session_log")
data class SessionLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val exerciseId: String,
    val timestampMillis: Long,
    val durationDoneSec: Int
)

@Entity(tableName = "reminder_settings")
data class ReminderSettings(
    @PrimaryKey val key: String = "main",
    val workStartMin: Int = 540,
    val workEndMin: Int = 1080,
    val intervalMin: Int = 60
)

/**
 * Egzersiz tercihi: kapalı hareket rotasyona girmez.
 * Satır yokluğu = açık (varsayılan hepsi açık).
 */
@Entity(tableName = "exercise_prefs")
data class ExercisePref(
    @PrimaryKey val exerciseId: String,
    val enabled: Boolean = true
)
