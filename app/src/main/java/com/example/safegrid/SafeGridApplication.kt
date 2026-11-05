package com.example.safegrid

import android.app.Application
import com.example.safegrid.data.SafeGridRepository
import com.example.safegrid.notify.NotificationHelper

class SafeGridApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.ensureChannel(this)
        SafeGridRepository.get(this).enqueueSync()
    }
}
