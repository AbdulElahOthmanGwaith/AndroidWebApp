package com.gwaith.base.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.gwaith.base.MainActivity
import com.gwaith.base.R

/**
 * خدمة الإشعارات - تدير إنشاء وعرض الإشعارات
 * Notification Service - Manages notification creation and display
 */
class NotificationService(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "gwaithbase_notifications"
        const val CHANNEL_NAME = "Gwaith Base Notifications"
        const val CHANNEL_DESCRIPTION = "Notifications from Gwaith Base app"
    }

    init {
        createNotificationChannel()
    }

    /**
     * إنشاء قناة الإشعارات للأنظمة الحديثة
     * Create notification channel for modern systems
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                importance
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                enableLights(true)
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * عرض إشعار بسيث
     * Show simple notification
     */
    fun showNotification(
        title: String,
        message: String,
        notificationId: Int = 1000,
        deepLink: String? = null
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            deepLink?.let { putExtra("deeplink", it) }
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (e: SecurityException) {
            // Handle missing notification permission
            e.printStackTrace()
        }
    }

    /**
     * عرض إشعار مع إجراءات
     * Show notification with actions
     */
    fun showNotificationWithActions(
        title: String,
        message: String,
        actionText: String,
        actionUrl: String,
        notificationId: Int = 1001
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("deeplink", actionUrl)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(R.drawable.ic_launcher_foreground, actionText, pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    /**
     * إلغاء إشعار
     * Cancel notification
     */
    fun cancelNotification(notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }

    /**
     * إلغاء جميع الإشعارات
     * Cancel all notifications
     */
    fun cancelAllNotifications() {
        NotificationManagerCompat.from(context).cancelAll()
    }
}
