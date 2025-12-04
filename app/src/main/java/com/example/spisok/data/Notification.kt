package com.example.spisok.data

/**
 * Модель данных для уведомления
 */
data class Notification(
    val id: String,
    val message: String, // Текст уведомления (до 50 символов)
    val time: String, // Время в формате "HH:mm" (например, "10:00")
    val daysOfWeek: Set<Int>, // Дни недели: 1=Понедельник, 2=Вторник, ..., 7=Воскресенье
    val isEnabled: Boolean = true // Включено/выключено
)

