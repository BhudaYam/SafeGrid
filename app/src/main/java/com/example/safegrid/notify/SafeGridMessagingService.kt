package com.example.safegrid.notify

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class SafeGridMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title
            ?: message.data["title"]
            ?: "SafeGrid"
        val body = message.notification?.body
            ?: message.data["body"]
            ?: "New safety update"
        NotificationHelper.show(this, title, body)
    }
}
