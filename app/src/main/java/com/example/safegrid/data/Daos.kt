package com.example.safegrid.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface IncidentDao {
    @Query("SELECT * FROM incidents ORDER BY createdAt DESC")
    fun observe(): Flow<List<IncidentEntity>>

    @Query("SELECT * FROM incidents WHERE pendingSync = 1")
    suspend fun pending(): List<IncidentEntity>

    @Query("SELECT * FROM incidents WHERE id = :id LIMIT 1")
    suspend fun get(id: String): IncidentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: IncidentEntity)

    @Query("UPDATE incidents SET pendingSync = 0, photoCloudUrl = :photo WHERE id = :id")
    suspend fun markSynced(id: String, photo: String?)
}

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedule ORDER BY id")
    suspend fun all(): List<ScheduleEntity>

    @Query("DELETE FROM schedule")
    suspend fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ScheduleEntity>)
}

@Dao
interface SosDao {
    @Query("SELECT * FROM sos_events WHERE pendingSync = 1")
    suspend fun pending(): List<SosEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SosEntity)

    @Query("UPDATE sos_events SET pendingSync = 0 WHERE id = :id")
    suspend fun markSynced(id: String)
}
