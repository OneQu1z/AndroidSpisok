package com.example.spisok.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.spisok.data.Participant

/**
 * Диалог для отправки списка участнику
 */
@Composable
fun ShareListDialog(
    participants: List<Participant>,
    onDismiss: () -> Unit,
    onShare: (String) -> Unit // participantId
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Отправить список")
        },
        text = {
            if (participants.isEmpty()) {
                Text("Нет доступных участников. Добавьте участников в настройках.")
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                ) {
                    items(participants) { participant ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            onClick = {
                                onShare(participant.id)
                                onDismiss()
                            }
                        ) {
                            Text(
                                text = participant.name,
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        },
        dismissButton = null
    )
}

