package com.example.spisok.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.spisok.data.Participant
import com.example.spisok.ui.components.AddParticipantButton
import com.example.spisok.ui.components.AddParticipantDialog
import com.example.spisok.ui.components.ParticipantCard
import com.example.spisok.ui.theme.Dimens

/**
 * Экран настроек
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit = {},
    participants: List<Participant> = emptyList(),
    onAddParticipant: (userId: String, name: String) -> Unit = { _, _ -> },
    onRemoveParticipant: (String) -> Unit = {},
    onNotificationsToggle: (Boolean) -> Unit = {},
    notificationsEnabled: Boolean = true
) {
    var localNotificationsEnabled by remember(notificationsEnabled) { mutableStateOf(notificationsEnabled) }
    var showAddParticipantDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Настройки",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Dimens.Spacing16),
            verticalArrangement = Arrangement.spacedBy(Dimens.Spacing24),
            contentPadding = PaddingValues(vertical = Dimens.Spacing16)
        ) {
            // Секция "Уведомления"
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Dimens.Spacing12)
                ) {
                    Text(
                        text = "Уведомления",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Получать уведомления о покупках",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Switch(
                            checked = localNotificationsEnabled,
                            onCheckedChange = { checked ->
                                localNotificationsEnabled = checked
                                onNotificationsToggle(checked)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                checkedTrackColor = MaterialTheme.colorScheme.primary,
                                uncheckedThumbColor = MaterialTheme.colorScheme.surface,
                                uncheckedTrackColor = MaterialTheme.colorScheme.outline
                            )
                        )
                    }
                }
            }
            
            // Секция "Совместный доступ"
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Dimens.Spacing12)
                ) {
                    Text(
                        text = "Совместный доступ",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    // Список участников
                    if (participants.isNotEmpty()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(Dimens.Spacing8)
                        ) {
                            participants.forEach { participant ->
                                ParticipantCard(
                                    participantName = participant.name,
                                    onRemoveClick = { onRemoveParticipant(participant.id) }
                                )
                            }
                        }
                    }
                    
                    // Кнопка добавления участника
                    AddParticipantButton(
                        onClick = { showAddParticipantDialog = true }
                    )
                }
            }
        }
    }
    
    // Диалог добавления участника
    if (showAddParticipantDialog) {
        AddParticipantDialog(
            onDismiss = { showAddParticipantDialog = false },
            onConfirm = { userId, name ->
                onAddParticipant(userId, name)
            }
        )
    }
}

