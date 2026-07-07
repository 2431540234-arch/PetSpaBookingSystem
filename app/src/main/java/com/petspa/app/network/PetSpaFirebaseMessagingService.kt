package com.petspa.app.network

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.petspa.app.MainActivity

class PetSpaFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Refreshed token: $token")
        // Token will be synced via ViewModel/Repository on app start or login
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM", "From: ${remoteMessage.from}")

        // Handle notification and data payload
        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "PetSpa"
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: ""
        
        sendNotification(title, body, remoteMessage.data)
    }

    private fun sendNotification(title: String, body: String, data: Map<String, String>) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        // Handle Deep Linking from data payload
        val type = data["type"] ?: ""
        val bookingId = data["bookingId"] ?: ""

        val deepLinkUri = when (type) {
            "booking_new", "booking_confirmed", "booking_rescheduled", "booking_cancelled",
            "service_in_progress", "service_completed" -> 
                Uri.parse("petspa://customer/appointment/$bookingId")
            "payment_success", "payment_failed" -> 
                Uri.parse("petspa://payment/callback?bookingId=$bookingId")
            else -> Uri.parse("petspa://home")
        }
        intent.data = deepLinkUri

        val pendingIntent = PendingIntent.getActivity(
            this, System.currentTimeMillis().toInt(), intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = when {
            type.startsWith("booking") -> "booking_channel"
            type.startsWith("payment") -> "payment_channel"
            type.startsWith("service") -> "service_channel"
            else -> "system_channel"
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannels(notificationManager)
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    private fun createNotificationChannels(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel("booking_channel", "Lịch hẹn", NotificationManager.IMPORTANCE_HIGH),
                NotificationChannel("payment_channel", "Thanh toán", NotificationManager.IMPORTANCE_HIGH),
                NotificationChannel("service_channel", "Dịch vụ", NotificationManager.IMPORTANCE_DEFAULT),
                NotificationChannel("system_channel", "Hệ thống", NotificationManager.IMPORTANCE_LOW)
            )
            notificationManager.createNotificationChannels(channels)
        }
    }
}
