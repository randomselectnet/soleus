package com.soleus.office.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/** v1 -> v2: egzersiz tercih tablosu (Hareketler ekranındaki toggle'lar). */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `exercise_prefs` " +
                "(`exerciseId` TEXT NOT NULL, `enabled` INTEGER NOT NULL, " +
                "PRIMARY KEY(`exerciseId`))"
        )
    }
}

@Database(
    entities = [SessionLog::class, ReminderSettings::class, ExercisePref::class],
    version = 2,
    exportSchema = false
)
abstract class AppDb : RoomDatabase() {
    abstract fun logDao(): LogDao
    abstract fun settingsDao(): SettingsDao
    abstract fun prefDao(): PrefDao

    companion object {
        @Volatile
        private var instance: AppDb? = null

        fun get(context: Context): AppDb {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDb::class.java,
                    "soleus.db"
                ).addMigrations(MIGRATION_1_2).build().also { instance = it }
            }
        }
    }
}
