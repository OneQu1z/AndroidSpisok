package com.example.spisok.ui.components

import android.content.Context
import android.view.LayoutInflater
import android.widget.TimePicker
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.spisok.R
import com.example.spisok.ui.theme.Dimens
import java.util.Calendar

/**
 * Диалог для добавления уведомления
 */
@Composable
fun AddNotificationDialog(
    onDismiss: () -> Unit,
    onConfirm: (message: String, time: String, daysOfWeek: Set<Int>) -> Unit
) {
    var message by remember { mutableStateOf("") }
    var selectedDays by remember { mutableStateOf<Set<Int>>(emptySet()) }
    
    val context = LocalContext.current
    val currentTime = Calendar.getInstance()
    var selectedHour by remember { mutableStateOf(currentTime.get(Calendar.HOUR_OF_DAY)) }
    var selectedMinute by remember { mutableStateOf(currentTime.get(Calendar.MINUTE)) }
    
    val daysOfWeek = listOf(
        "Пн" to Calendar.MONDAY,
        "Вт" to Calendar.TUESDAY,
        "Ср" to Calendar.WEDNESDAY,
        "Чт" to Calendar.THURSDAY,
        "Пт" to Calendar.FRIDAY,
        "Сб" to Calendar.SATURDAY,
        "Вс" to Calendar.SUNDAY
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Добавить уведомление")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Dimens.Spacing16),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Поле для текста уведомления
                OutlinedTextField(
                    value = message,
                    onValueChange = { 
                        if (it.length <= 50) {
                            message = it
                        }
                    },
                    label = { Text("Текст уведомления") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    maxLines = 2,
                    supportingText = {
                        Text(
                            text = "${message.length}/50",
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    isError = message.length > 50
                )
                
                // Выбор времени с помощью нативного TimePicker в spinner режиме
                AndroidView(
                    factory = { ctx ->
                        createSpinnerTimePicker(ctx, selectedHour, selectedMinute) { hour, minute ->
                            selectedHour = hour
                            selectedMinute = minute
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Dimens.Spacing8)
                        .height(200.dp),
                    update = { timePicker ->
                        timePicker.hour = selectedHour
                        timePicker.minute = selectedMinute
                    }
                )
                
                // Выбор дней недели
                Text(
                    text = "Дни недели",
                    style = MaterialTheme.typography.labelMedium
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing4)
                ) {
                    daysOfWeek.forEach { (label, day) ->
                        FilterChip(
                            selected = selectedDays.contains(day),
                            onClick = {
                                selectedDays = if (selectedDays.contains(day)) {
                                    selectedDays - day
                                } else {
                                    selectedDays + day
                                }
                            },
                            label = { 
                                Text(
                                    text = label
                                ) 
                            },
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(minWidth = 60.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (message.isNotBlank() && selectedDays.isNotEmpty()) {
                        val timeString = "${selectedHour.toString().padStart(2, '0')}:${selectedMinute.toString().padStart(2, '0')}"
                        onConfirm(message, timeString, selectedDays)
                        onDismiss()
                    }
                },
                enabled = message.isNotBlank() && selectedDays.isNotEmpty() && message.length <= 50
            ) {
                Text("Добавить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

/**
 * Создает TimePicker в spinner режиме используя XML layout
 */
private fun createSpinnerTimePicker(
    context: Context,
    initialHour: Int,
    initialMinute: Int,
    onTimeChanged: (Int, Int) -> Unit
): TimePicker {
    // Загружаем TimePicker из XML с атрибутом android:timePickerMode="spinner"
    val inflater = LayoutInflater.from(context)
    val timePicker = inflater.inflate(R.layout.time_picker_spinner, null, false) as TimePicker
    
    timePicker.apply {
        hour = initialHour
        minute = initialMinute
        setIs24HourView(true)
        setOnTimeChangedListener { _, hour, minute ->
            onTimeChanged(hour, minute)
        }
    }
    
    return timePicker
}

