package com.sanwashoseki.bookskozuchi.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sanwashoseki.bookskozuchi.R


class SWFirebaseInstanceService : FirebaseMessagingService() {

    private val NOTIFICATION_CHANNEL_ID = "com.sanwashoseki.bookskozuchi"
    private val NOTIFICATION_CHANNEL_NAME = "SanWa"
    private var notificationID = 0

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        sendNotification(message.notification?.title.toString(), message.notification?.body.toString())
    }

    private fun sendNotification(title: String, messageBody: String) {
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_logo)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .build()
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.setShowBadge(true)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(notificationID++, notificationBuilder)
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.d("SWFirebaseInstanceService", "onNewToken:\n$p0")
    }
}