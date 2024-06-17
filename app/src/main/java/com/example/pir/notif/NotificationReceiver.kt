package com.example.pir.notif

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("NotificationReceiver", "Received broadcast")

        if (intent?.action == "ACTION_SHOW_NOTIFICATION") {
            val notificationId = intent.getIntExtra("notificationId", 0)
            val title = intent.getStringExtra("title")
            val message = intent.getStringExtra("message")
            Log.d("NotificationReceiver", "Showing notification $notificationId with title: $title and message: $message")
            context?.let {
                NotificationUtils.createNotification(
                    context,
                    notificationId,
                    title ?: "",
                    message ?: ""
                )
            }
        }
    }
}
