package com.example.spisok.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Диалог авторизации/регистрации
 */
@Composable
fun AuthDialog(
    isSignUp: Boolean = false,
    onDismiss: () -> Unit,
    onSignIn: (String, String) -> Unit,
    onSignUp: (String, String) -> Unit,
    onSignInAnonymously: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = if (isSignUp) "Регистрация" else "Вход")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Пароль") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                )
                
                TextButton(
                    onClick = onSignInAnonymously,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Войти анонимно")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        if (isSignUp) {
                            onSignUp(email, password)
                        } else {
                            onSignIn(email, password)
                        }
                    }
                },
                enabled = email.isNotBlank() && password.isNotBlank()
            ) {
                Text(if (isSignUp) "Зарегистрироваться" else "Войти")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

