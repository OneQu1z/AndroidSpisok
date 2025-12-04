package com.example.spisok.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.example.spisok.R
import com.example.spisok.data.DataRepository
import com.example.spisok.data.Notification
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Worker для отправки уведомлений
 */
class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val notificationIdString = inputData.getString(NOTIFICATION_ID_KEY) ?: return Result.failure()
        val dayOfWeek = inputData.getInt(DAY_OF_WEEK_KEY, -1)
        val message = inputData.getString(MESSAGE_KEY) ?: return Result.failure()
        
        if (dayOfWeek == -1) {
            return Result.failure()
        }

        createNotificationChannel()
        val notificationId = notificationIdString.hashCode()
        sendNotification(notificationId, message)

        // Перепланируем следующее уведомление на следующую неделю
        rescheduleNextNotification(notificationIdString, dayOfWeek)

        return Result.success()
    }
    
    private fun rescheduleNextNotification(notificationId: String, dayOfWeek: Int) {
        val dataRepository = DataRepository(applicationContext)
        val notifications = dataRepository.loadNotifications()
        val notification = notifications.find { it.id == notificationId } ?: return
        
        if (!notification.isEnabled || !dataRepository.loadNotificationsEnabled()) {
            return
        }
        
        if (!notification.daysOfWeek.contains(dayOfWeek)) {
            return
        }
        
        val scheduler = NotificationScheduler(applicationContext)
        scheduler.scheduleNotificationForDay(notification, dayOfWeek)
    }

    private fun createNotificationChannel() {
        val channelId = "spisok_notifications"
        val channelName = "Уведомления списка"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        
        val channel = NotificationChannel(channelId, channelName, importance)
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun sendNotification(notificationId: Int, message: String) {
        val channelId = "spisok_notifications"
        
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Напоминание")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }

    companion object {
        const val NOTIFICATION_ID_KEY = "notification_id"
        const val MESSAGE_KEY = "message"
        const val DAY_OF_WEEK_KEY = "day_of_week"
    }
}

