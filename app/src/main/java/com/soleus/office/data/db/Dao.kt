package com.soleus.office.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface LogDao {
    @Insert
    suspend fun insert(log: SessionLog): Long

    @Query("SELECT * FROM session_log WHERE timestampMillis >= :since ORDER BY timestampMillis DESC")
    suspend fun logsSince(since: Long): List<SessionLog>

    @Query("SELECT * FROM session_log ORDER BY timestampMillis DESC LIMIT :limit")
    suspend fun recent(limit: Int): List<SessionLog>
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM reminder_settings WHERE `key` = :key")
    suspend fun get(key: String = "main"): ReminderSettings?

    @Upsert
    suspend fun upsert(settings: ReminderSettings)
}

@Dao
interface PrefDao {
    @Query("SELECT * FROM exercise_prefs")
    suspend fun all(): List<ExercisePref>

    @Query("SELECT exerciseId FROM exercise_prefs WHERE enabled = 0")
    suspend fun disabledIds(): List<String>

    @Upsert
    suspend fun upsert(pref: ExercisePref)
}
