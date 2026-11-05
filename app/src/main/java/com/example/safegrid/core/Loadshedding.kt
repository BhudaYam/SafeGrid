package com.example.safegrid.core

data class PlannedSlot(
    val dayKey: String,
    val time: String,
    val stage: Int
)

data class MergeIncident(
    val id: String,
    val createdAt: Long,
    val pendingSync: Boolean,
    val payload: String
)

object LoadsheddingParser {
    const val STATUS_URL = "https://loadshedding.eskom.co.za/LoadShedding/GetStatus"

    /**
     * Eskom returns 1 for no loadshedding and 2..9 for stages 1..8.
     * 99 means the national status is temporarily unavailable.
     */
    fun stageFromStatus(raw: String): Int? {
        val value = raw.trim().toIntOrNull() ?: return null
        if (value < 1 || value > 9) return null
        return value - 1
    }
}

object ScheduleBuilder {
    private val dayKeys = listOf("today", "tomorrow", "day3", "day4")
    private val startHours = intArrayOf(6, 14, 22, 8, 16, 0, 10, 18)

    fun build(stage: Int): List<PlannedSlot> {
        if (stage <= 0) {
            return listOf(PlannedSlot("today", "none", 0))
        }
        return dayKeys.mapIndexed { index, day ->
            val start = startHours[(stage + index) % startHours.size]
            val endHour = (start + 2) % 24
            PlannedSlot(
                dayKey = day,
                time = "%02d:00 - %02d:30".format(start, endHour),
                stage = stage
            )
        }
    }
}

object IncidentMerger {
    /**
     * A report that has not reached the cloud yet must survive a refresh.
     * Remote rows fill in everything else.
     */
    fun merge(local: List<MergeIncident>, remote: List<MergeIncident>): List<MergeIncident> {
        val combined = remote.associateBy { it.id }.toMutableMap()
        local.filter { it.pendingSync }.forEach { combined[it.id] = it }
        local.filter { !it.pendingSync && it.id !in combined }.forEach { combined[it.id] = it }
        return combined.values.sortedByDescending { it.createdAt }
    }
}
