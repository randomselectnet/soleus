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
    @Query("SELECT * FROM reminder_settings WHERE `key` = 'main'")
    suspend fun get(): ReminderSettings?

    @Upsert
    suspend fun upsert(settings: ReminderSettings)
}
