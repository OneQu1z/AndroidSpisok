package com.example.spisok.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.spisok.data.ProductItem
import com.example.spisok.data.Category
import com.example.spisok.data.ProductList
import com.example.spisok.ui.components.DeleteConfirmationDialog
import com.example.spisok.ui.components.ProductItemRow
import com.example.spisok.ui.components.SaveAsTemplateDialog
import com.example.spisok.ui.components.ShareListDialog
import com.example.spisok.ui.theme.Dimens
import java.util.UUID

/**
 * Экран деталей списка продуктов
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListDetailScreen(
    listName: String,
    items: List<ProductItem>,
    categories: List<Category>,
    participants: List<com.example.spisok.data.Participant>,
    onBackClick: () -> Unit,
    onItemsChange: (List<ProductItem>) -> Unit,
    onSaveAsTemplate: (String, List<ProductItem>) -> Unit = { _, _ -> },
    onDeleteList: () -> Unit = {},
    onShareList: (String) -> Unit = {} // participantId
) {
    var currentItems by remember(items) { mutableStateOf(items) }
    var isDeleteMode by remember { mutableStateOf(false) }
    var markedForDeletion by remember { mutableStateOf<Set<String>>(emptySet()) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDeleteListDialog by remember { mutableStateOf(false) }
    var showSaveTemplateDialog by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }
    var newItemText by remember { mutableStateOf("") }
    var showCategoryDropdown by remember { mutableStateOf(false) }

    // Создаем карту категорий для быстрого доступа
    val categoryMap = remember(categories) {
        categories.associateBy { it.id }
    }
    
    // Группируем товары по категориям
    val itemsByCategory = remember(currentItems, categoryMap, categories) {
        currentItems.groupBy { item ->
            // Используем ID категории, если он есть в списке
            if (categoryMap.containsKey(item.category)) {
                item.category
            } else {
                // Для обратной совместимости: если category - это старое название, ищем по имени
                categories.find { it.name == item.category }?.id ?: item.category
            }
        }.toList().sortedBy { (categoryId, _) ->
            // Сортируем категории: сначала известные категории по имени, потом неизвестные
            categoryMap[categoryId]?.name ?: categoryId
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = listName,
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
                        // Кнопка отправки списка
                        IconButton(
                            onClick = { showShareDialog = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Отправить список"
                            )
                        }
                        // Кнопка "Сохранить как шаблон"
                        IconButton(
                            onClick = { showSaveTemplateDialog = true },
                            enabled = currentItems.isNotEmpty()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = "Сохранить как шаблон"
                            )
                        }
                        // Кнопка удаления товаров
                        IconButton(onClick = { isDeleteMode = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Удалить товары"
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
                Column(
                    verticalArrangement = Arrangement.spacedBy(Dimens.Spacing12)
                ) {
                    // Поле ввода для нового товара с выбором категории
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing8),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newItemText,
                            onValueChange = { newItemText = it },
                            placeholder = { Text("Добавить товар") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        
                        // Кнопка "+" с выпадающим списком категорий
                        Box {
                            FloatingActionButton(
                                onClick = {
                                    if (newItemText.isNotBlank() && categories.isNotEmpty()) {
                                        showCategoryDropdown = true
                                    }
                                },
                                containerColor = if (categories.isNotEmpty() && newItemText.isNotBlank()) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                },
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(56.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Добавить"
                                )
                            }
                            
                            // Выпадающий список категорий (появляется выше кнопки)
                            DropdownMenu(
                                expanded = showCategoryDropdown && categories.isNotEmpty(),
                                onDismissRequest = { showCategoryDropdown = false },
                                modifier = Modifier.widthIn(min = 150.dp, max = 250.dp)
                            ) {
                                categories.forEach { category ->
                                    DropdownMenuItem(
                                        text = { 
                                            Text(
                                                text = category.name,
                                                maxLines = 1
                                            ) 
                                        },
                                        onClick = {
                                            if (newItemText.isNotBlank()) {
                                                val newItem = ProductItem(
                                                    id = UUID.randomUUID().toString(),
                                                    name = newItemText,
                                                    category = category.id,
                                                    isPurchased = false
                                                )
                                                val updatedItems = currentItems + newItem
                                                currentItems = updatedItems
                                                onItemsChange(updatedItems)
                                                newItemText = ""
                                                showCategoryDropdown = false
                                            }
                                        }
                                    )
                                }
                            }
                        }
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
            verticalArrangement = Arrangement.spacedBy(Dimens.Spacing16),
            contentPadding = PaddingValues(vertical = Dimens.Spacing16)
        ) {
            // Отображаем товары по категориям
            items(
                items = itemsByCategory,
                key = { (categoryId, _) -> "category_$categoryId" }
            ) { (categoryId, categoryItems) ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(Dimens.Spacing4)
                ) {
                    // Заголовок категории (используем название из списка категорий)
                    val categoryName = categoryMap[categoryId]?.name ?: categoryId
                    Text(
                        text = categoryName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = Dimens.Spacing4)
                    )
                    
                    // Товары в категории
                    categoryItems.forEach { item ->
                        ProductItemRow(
                            itemName = item.name,
                            isPurchased = item.isPurchased,
                            isDeleteMode = isDeleteMode,
                            isMarkedForDeletion = markedForDeletion.contains(item.id),
                            onPurchasedChange = { isChecked ->
                                val updatedItems = currentItems.map {
                                    if (it.id == item.id) {
                                        it.copy(isPurchased = isChecked)
                                    } else {
                                        it
                                    }
                                }
                                currentItems = updatedItems
                                onItemsChange(updatedItems)
                            },
                            onMarkForDeletion = {
                                markedForDeletion = if (markedForDeletion.contains(item.id)) {
                                    markedForDeletion - item.id
                                } else {
                                    markedForDeletion + item.id
                                }
                            }
                        )
                    }
                }
            }
        }
    }
    
    // Диалог подтверждения удаления товаров
    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            itemCount = markedForDeletion.size,
            onDismiss = {
                showDeleteDialog = false
                isDeleteMode = false
                markedForDeletion = emptySet()
            },
            onConfirm = {
                val updatedItems = currentItems.filter { it.id !in markedForDeletion }
                currentItems = updatedItems
                onItemsChange(updatedItems)
                markedForDeletion = emptySet()
                isDeleteMode = false
                
                // Если список стал пустым, предлагаем удалить весь список
                if (updatedItems.isEmpty()) {
                    showDeleteListDialog = true
                }
            }
        )
    }
    
    // Диалог подтверждения удаления списка
    if (showDeleteListDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteListDialog = false
            },
            title = {
                Text(text = "Удалить список?")
            },
            text = {
                Text(text = "Список \"$listName\" пуст. Хотите удалить его полностью?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteListDialog = false
                        onDeleteList()
                    }
                ) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteListDialog = false }
                ) {
                    Text("Отмена")
                }
            }
        )
    }
    
    // Диалог сохранения как шаблона
    if (showSaveTemplateDialog) {
        SaveAsTemplateDialog(
            defaultName = listName,
            onDismiss = { showSaveTemplateDialog = false },
            onConfirm = { templateName ->
                onSaveAsTemplate(templateName, currentItems)
                showSaveTemplateDialog = false
            }
        )
    }
    
    // Диалог отправки списка
    if (showShareDialog) {
        ShareListDialog(
            participants = participants,
            onDismiss = { showShareDialog = false },
            onShare = { participantId ->
                onShareList(participantId)
                showShareDialog = false
            }
        )
    }
}

