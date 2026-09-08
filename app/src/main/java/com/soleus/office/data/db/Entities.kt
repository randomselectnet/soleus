package com.soleus.office.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Exercise(
    val id: String,
    val trName: String,
    val steps: List<String>,
    val durationSec: Int,
    val benefit: String,
    val caution: String,
    val animationAsset: String
)

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
