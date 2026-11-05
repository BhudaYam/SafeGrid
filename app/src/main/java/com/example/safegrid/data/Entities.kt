package com.example.safegrid.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "incidents")
data class IncidentEntity(
    @PrimaryKey val id: String,
    val category: String,
    val description: String,
    val area: String,
    val reporter: String,
    val createdAt: Long,
    val photoLocal: String?,
    val photoCloudUrl: String?,
    val pendingSync: Boolean
)

@Entity(tableName = "schedule")
data class ScheduleEntity(
    @PrimaryKey val id: String,
    val dayKey: String,
    val time: String,
    val stage: Int,
    val area: String,
    val syncedAt: Long
)

@Entity(tableName = "sos_events")
data class SosEntity(
    @PrimaryKey val id: String,
    val area: String,
    val location: String,
    val createdAt: Long,
    val pendingSync: Boolean
)
