package com.example.spisok.notifications

import android.content.Context
import androidx.work.*
import com.example.spisok.data.Notification
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Сервис для планирования уведомлений
 */
class NotificationScheduler(private val context: Context) {

    private val workManager = WorkManager.getInstance(context)

    /**
     * Запланировать уведомление для всех указанных дней недели
     */
    fun scheduleNotification(notification: Notification) {
        if (!notification.isEnabled) {
            return
        }

        // Парсим время
        val timeParts = notification.time.split(":")
        val hour = timeParts[0].toIntOrNull() ?: return
        val minute = timeParts[1].toIntOrNull() ?: return

        // Планируем для каждого дня недели
        notification.daysOfWeek.forEach { dayOfWeek ->
            scheduleNotificationForDay(notification, dayOfWeek, hour, minute)
        }
    }

    /**
     * Запланировать уведомление для конкретного дня недели
     */
    private fun scheduleNotificationForDay(
        notification: Notification,
        dayOfWeek: Int,
        hour: Int,
        minute: Int
    ) {
        val calendar = Calendar.getInstance()
        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        
        // Конвертируем Calendar.DAY_OF_WEEK (1=Sunday, 2=Monday, ...) 
        // в наш формат (1=Monday, 2=Tuesday, ..., 7=Sunday)
        val calendarDayOfWeek = when (dayOfWeek) {
            1 -> Calendar.MONDAY
            2 -> Calendar.TUESDAY
            3 -> Calendar.WEDNESDAY
            4 -> Calendar.THURSDAY
            5 -> Calendar.FRIDAY
            6 -> Calendar.SATURDAY
            7 -> Calendar.SUNDAY
            else -> return
        }

        // Устанавливаем время
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.set(Calendar.DAY_OF_WEEK, calendarDayOfWeek)

        // Если время уже прошло сегодня, планируем на следующую неделю
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1)
        }

        // Создаем уникальный тег для работы
        val workTag = "${notification.id}_$dayOfWeek"

        // Создаем данные для Worker
        val inputData = Data.Builder()
            .putString(NotificationWorker.NOTIFICATION_ID_KEY, notification.id)
            .putString(NotificationWorker.MESSAGE_KEY, notification.message)
            .putInt(NotificationWorker.DAY_OF_WEEK_KEY, dayOfWeek)
            .build()

        // Создаем одноразовую работу с задержкой до нужного времени
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val delay = calculateInitialDelay(calendar)
        
        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .setInputData(inputData)
            .addTag(workTag)
            .build()

        workManager.enqueue(workRequest)
    }

    /**
     * Вычислить задержку до первого уведомления
     */
    private fun calculateInitialDelay(targetCalendar: Calendar): Long {
        val now = System.currentTimeMillis()
        val targetTime = targetCalendar.timeInMillis
        return maxOf(0, targetTime - now)
    }
    
    /**
     * Публичный метод для планирования уведомления на конкретный день
     * (используется Worker для перепланирования на следующую неделю)
     */
    fun scheduleNotificationForDay(notification: Notification, dayOfWeek: Int) {
        val timeParts = notification.time.split(":")
        val hour = timeParts[0].toIntOrNull() ?: return
        val minute = timeParts[1].toIntOrNull() ?: return
        
        val calendar = Calendar.getInstance()
        
        // Конвертируем наш формат в Calendar.DAY_OF_WEEK
        val calendarDayOfWeek = when (dayOfWeek) {
            1 -> Calendar.MONDAY
            2 -> Calendar.TUESDAY
            3 -> Calendar.WEDNESDAY
            4 -> Calendar.THURSDAY
            5 -> Calendar.FRIDAY
            6 -> Calendar.SATURDAY
            7 -> Calendar.SUNDAY
            else -> return
        }

        // Устанавливаем время и день следующей недели
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.set(Calendar.DAY_OF_WEEK, calendarDayOfWeek)
        
        // Всегда планируем на следующую неделю (так как это перепланирование после выполнения)
        calendar.add(Calendar.WEEK_OF_YEAR, 1)

        val workTag = "${notification.id}_$dayOfWeek"
        val inputData = Data.Builder()
            .putString(NotificationWorker.NOTIFICATION_ID_KEY, notification.id)
            .putString(NotificationWorker.MESSAGE_KEY, notification.message)
            .putInt(NotificationWorker.DAY_OF_WEEK_KEY, dayOfWeek)
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val delay = calculateInitialDelay(calendar)
        
        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .setInputData(inputData)
            .addTag(workTag)
            .build()

        workManager.enqueue(workRequest)
    }

    /**
     * Отменить все запланированные уведомления для данного уведомления
     */
    fun cancelNotification(notificationId: String) {
        // Отменяем все работы с тегами, начинающимися с notificationId
        val workManager = WorkManager.getInstance(context)
        workManager.cancelAllWorkByTag("${notificationId}_1")
        workManager.cancelAllWorkByTag("${notificationId}_2")
        workManager.cancelAllWorkByTag("${notificationId}_3")
        workManager.cancelAllWorkByTag("${notificationId}_4")
        workManager.cancelAllWorkByTag("${notificationId}_5")
        workManager.cancelAllWorkByTag("${notificationId}_6")
        workManager.cancelAllWorkByTag("${notificationId}_7")
    }

    /**
     * Перепланировать все уведомления (используется при изменении настроек)
     */
    fun rescheduleAllNotifications(notifications: List<Notification>, globalEnabled: Boolean) {
        // Отменяем все существующие уведомления
        workManager.cancelAllWork()

        // Планируем заново только включенные уведомления
        if (globalEnabled) {
            notifications.forEach { notification ->
                if (notification.isEnabled) {
                    scheduleNotification(notification)
                }
            }
        }
    }
}

