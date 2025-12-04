package com.example.spisok.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.spisok.data.Template
import com.example.spisok.ui.components.DeleteConfirmationDialog
import com.example.spisok.ui.components.TemplateCard
import com.example.spisok.ui.theme.Dimens

/**
 * Экран шаблонов
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplatesScreen(
    templates: List<Template>,
    onBackClick: () -> Unit = {},
    onUseTemplate: (Template) -> Unit = {},
    onDeleteTemplates: (Set<String>) -> Unit = {}
) {
    var isDeleteMode by remember { mutableStateOf(false) }
    var markedForDeletion by remember { mutableStateOf<Set<String>>(emptySet()) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Шаблоны",
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
                                contentDescription = "Удалить шаблоны"
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
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Dimens.Spacing16),
            verticalArrangement = Arrangement.spacedBy(Dimens.Spacing12),
            contentPadding = PaddingValues(vertical = Dimens.Spacing16)
        ) {
            items(templates) { template ->
                TemplateCard(
                    templateName = template.name,
                    isInUse = false, // Можно добавить логику отслеживания используемых шаблонов
                    isDeleteMode = isDeleteMode,
                    isMarkedForDeletion = markedForDeletion.contains(template.id),
                    onUseClick = { onUseTemplate(template) },
                    onMarkForDeletion = {
                        markedForDeletion = if (markedForDeletion.contains(template.id)) {
                            markedForDeletion - template.id
                        } else {
                            markedForDeletion + template.id
                        }
                    }
                )
            }
        }
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
                onDeleteTemplates(markedForDeletion)
                markedForDeletion = emptySet()
                isDeleteMode = false
                showDeleteDialog = false
            },
            title = "Удаление шаблонов",
            message = "Вы уверены, что хотите удалить эти шаблоны?"
        )
    }
}

