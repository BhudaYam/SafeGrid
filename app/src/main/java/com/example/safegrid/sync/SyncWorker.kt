package com.example.safegrid.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.safegrid.data.SafeGridRepository

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            SafeGridRepository.get(applicationContext).syncPending()
            Result.success()
        } catch (error: Exception) {
            Result.retry()
        }
    }
}
