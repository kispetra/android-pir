package com.example.pir.notif

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.pir.view.MainActivity
import com.example.pir.R

object NotificationUtils {

    private const val TAG = "NotificationUtils"

    fun scheduleNotifications(context: Context, weddingDateMillis: Long) {
        val triggerTime10DaysBefore = calculateTriggerTimeForNotification(weddingDateMillis, -10)
        val triggerTime1DayBefore = calculateTriggerTimeForNotification(weddingDateMillis, -1)
        val triggerTimeOnWeddingDay = weddingDateMillis

        Log.d(TAG, "Scheduling notifications: 10 days before: $triggerTime10DaysBefore, 24h before: $triggerTime1DayBefore, on wedding day: $triggerTimeOnWeddingDay")

        if (canScheduleExactAlarms(context)) {
            scheduleNotification(context, 1, triggerTime10DaysBefore, "Reminder", "Wedding is in 10 days! Fingers crossed. ")
            scheduleNotification(context, 2, triggerTime1DayBefore, "Reminder", "Wedding is in 1 day! :)")
            scheduleNotification(context, 3, triggerTimeOnWeddingDay, "Reminder", "Wedding is today! Goooood luck <3")
        } else {
            // Request the exact alarm permission if not already granted
            requestExactAlarmPermission(context)
        }
    }

    private fun canScheduleExactAlarms(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    private fun requestExactAlarmPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent().apply {
                action = android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                data = Uri.parse("package:${context.packageName}")
            }
            context.startActivity(intent)
        }
    }

    private fun scheduleNotification(context: Context, notificationId: Int, triggerTimeMillis: Long, title: String, message: String) {
        Log.d(TAG, "Scheduling notification $notificationId at $triggerTimeMillis with title: $title and message: $message")

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = "ACTION_SHOW_NOTIFICATION"
            putExtra("notificationId", notificationId)
            putExtra("title", title)
            putExtra("message", message)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTimeMillis, pendingIntent)
    }

    fun createNotification(context: Context, notificationId: Int, title: String, message: String) {
        val channelId = "wedding_notification_channel"
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification) // Ensure this drawable exists
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Ensure high priority
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(context)

        // Ensure VIBRATE permission is granted
        if (context.checkSelfPermission(android.Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Sending notification $notificationId")
            notificationManager.notify(notificationId, builder.build())
        } else {
            Log.d(TAG, "Missing VIBRATE permission for notification $notificationId")
            // Request VIBRATE permission or handle the lack of it
        }
    }


    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "wedding_notification_channel"
            val channelName = "Wedding Notifications"
            val channelDescription = "Notifications for wedding reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun calculateTriggerTimeForNotification(weddingDateMillis: Long, daysBefore: Int): Long {
        return weddingDateMillis - daysBefore * 24 * 60 * 60 * 1000
    }
}
