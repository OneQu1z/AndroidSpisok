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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.spisok.data.Category
import com.example.spisok.ui.components.CategoryCard
import com.example.spisok.ui.components.DeleteConfirmationDialog
import com.example.spisok.ui.components.EditCategoryDialog
import com.example.spisok.ui.theme.Dimens

/**
 * Экран категорий
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    categories: List<Category>,
    onBackClick: () -> Unit = {},
    onAddCategory: (String) -> Unit = {},
    onUpdateCategory: (String, String) -> Unit = { _, _ -> },
    onDeleteCategories: (Set<String>) -> Unit = {}
) {
    var isDeleteMode by remember { mutableStateOf(false) }
    var markedForDeletion by remember { mutableStateOf<Set<String>>(emptySet()) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<Category?>(null) }
    var newCategoryName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Категории",
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
                                contentDescription = "Удалить категории"
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
                    .padding(bottom = Dimens.Spacing16)
                    .offset(y = (-64).dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing8),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newCategoryName,
                        onValueChange = { newCategoryName = it },
                        label = { Text("Добавить категорию") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    FloatingActionButton(
                        onClick = {
                            if (newCategoryName.isNotBlank()) {
                                onAddCategory(newCategoryName.trim())
                                newCategoryName = ""
                            }
                        },
                        modifier = Modifier.size(56.dp),
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Добавить",
                            tint = MaterialTheme.colorScheme.onPrimary
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
            verticalArrangement = Arrangement.spacedBy(Dimens.Spacing12),
            contentPadding = PaddingValues(vertical = Dimens.Spacing16)
        ) {
            items(categories) { category ->
                CategoryCard(
                    categoryName = category.name,
                    isDeleteMode = isDeleteMode,
                    isMarkedForDeletion = markedForDeletion.contains(category.id),
                    onEditClick = {
                        editingCategory = category
                    },
                    onMarkForDeletion = {
                        markedForDeletion = if (markedForDeletion.contains(category.id)) {
                            markedForDeletion - category.id
                        } else {
                            markedForDeletion + category.id
                        }
                    }
                )
                }
            }
        }
    
    // Диалог редактирования категории
    editingCategory?.let { category ->
        EditCategoryDialog(
            currentName = category.name,
            onDismiss = { editingCategory = null },
            onConfirm = { newName ->
                onUpdateCategory(category.id, newName)
                editingCategory = null
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
                onDeleteCategories(markedForDeletion)
                markedForDeletion = emptySet()
                isDeleteMode = false
                showDeleteDialog = false
            },
            title = "Удаление категорий",
            message = "Вы уверены, что хотите удалить эти категории?"
        )
    }
}
