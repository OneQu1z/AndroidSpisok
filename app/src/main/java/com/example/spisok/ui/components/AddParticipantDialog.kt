package com.example.spisok.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.spisok.ui.theme.Dimens

/**
 * Диалог для добавления участника
 */
@Composable
fun AddParticipantDialog(
    onDismiss: () -> Unit,
    onConfirm: (userId: String, name: String) -> Unit
) {
    var userId by remember { mutableStateOf("") }
    var participantName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Добавить участника")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Dimens.Spacing16)
            ) {
                OutlinedTextField(
                    value = userId,
                    onValueChange = { userId = it },
                    label = { Text("ID пользователя") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = participantName,
                    onValueChange = { participantName = it },
                    label = { Text("Имя участника") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("Например: Мама") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (userId.isNotBlank() && participantName.isNotBlank()) {
                        onConfirm(userId, participantName)
                        onDismiss()
                    }
                },
                enabled = userId.isNotBlank() && participantName.isNotBlank()
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

