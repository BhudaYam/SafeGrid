package com.example.safegrid.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [IncidentEntity::class, ScheduleEntity::class, SosEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SafeGridDatabase : RoomDatabase() {
    abstract fun incidents(): IncidentDao
    abstract fun schedule(): ScheduleDao
    abstract fun sos(): SosDao

    companion object {
        @Volatile
        private var instance: SafeGridDatabase? = null

        fun get(context: Context): SafeGridDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    SafeGridDatabase::class.java,
                    "safegrid.db"
                ).build().also { instance = it }
            }
        }
    }
}
