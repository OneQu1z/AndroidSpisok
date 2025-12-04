package com.example.spisok.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.spisok.data.Notification
import com.example.spisok.ui.components.AddNotificationDialog
import com.example.spisok.ui.components.DeleteConfirmationDialog
import com.example.spisok.ui.components.NotificationCard
import com.example.spisok.ui.theme.Dimens
import java.util.Calendar

/**
 * Экран уведомлений
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBackClick: () -> Unit = {},
    notifications: List<Notification> = emptyList(),
    onAddNotification: (String, String, Set<Int>) -> Unit = { _, _, _ -> },
    onToggleNotification: (String, Boolean) -> Unit = { _, _ -> },
    onDeleteNotifications: (Set<String>) -> Unit = { },
    globalNotificationsEnabled: Boolean = true
) {
    var isDeleteMode by remember { mutableStateOf(false) }
    var markedForDeletion by remember { mutableStateOf<Set<String>>(emptySet()) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    // Функция для форматирования дней недели
    fun formatDays(days: Set<Int>): String {
        val dayNames = mapOf(
            Calendar.MONDAY to "пн",
            Calendar.TUESDAY to "вт",
            Calendar.WEDNESDAY to "ср",
            Calendar.THURSDAY to "чт",
            Calendar.FRIDAY to "пт",
            Calendar.SATURDAY to "сб",
            Calendar.SUNDAY to "вс"
        )
        return days.sorted().joinToString(", ") { dayNames[it] ?: "" }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Уведомления",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    if (isDeleteMode) {
                        IconButton(
                            onClick = {
                                if (markedForDeletion.isNotEmpty()) {
                                    showDeleteDialog = true
                                } else {
                                    isDeleteMode = false
                                }
                            },
                            enabled = markedForDeletion.isNotEmpty()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Подтвердить удаление",
                                tint = if (markedForDeletion.isNotEmpty()) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                }
                            )
                        }
                    } else {
                        IconButton(onClick = { isDeleteMode = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Удалить уведомления"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.Spacing16)
                    .padding(top = Dimens.Spacing8)
                    .offset(y = (-64).dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Button(
                    onClick = { showAddDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dimens.ButtonHeight),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Добавить",
                            modifier = Modifier.size(24.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(Dimens.Spacing8))
                        
                        Text(
                            text = "Добавить новое уведомление",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Dimens.Spacing16),
            verticalArrangement = Arrangement.spacedBy(Dimens.Spacing8),
            contentPadding = PaddingValues(vertical = Dimens.Spacing16)
        ) {
            items(notifications) { notification ->
                NotificationCard(
                    time = notification.time,
                    daysText = formatDays(notification.daysOfWeek),
                    isEnabled = notification.isEnabled && globalNotificationsEnabled,
                    isDeleteMode = isDeleteMode,
                    isMarkedForDeletion = markedForDeletion.contains(notification.id),
                    onToggle = { enabled ->
                        // Учитываем глобальный переключатель: если он выключен, нельзя включить отдельное уведомление
                        if (globalNotificationsEnabled) {
                            onToggleNotification(notification.id, enabled)
                        }
                    },
                    onMarkForDeletion = {
                        markedForDeletion = if (markedForDeletion.contains(notification.id)) {
                            markedForDeletion - notification.id
                        } else {
                            markedForDeletion + notification.id
                        }
                    }
                )
            }
        }
    }
    
    // Диалог добавления уведомления
    if (showAddDialog) {
        AddNotificationDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { message, time, days ->
                onAddNotification(message, time, days)
            }
        )
    }
    
    // Диалог подтверждения удаления
    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            itemCount = markedForDeletion.size,
            onDismiss = {
                showDeleteDialog = false
                isDeleteMode = false
                markedForDeletion = emptySet()
            },
            onConfirm = {
                onDeleteNotifications(markedForDeletion)
                markedForDeletion = emptySet()
                isDeleteMode = false
            }
        )
    }
}

