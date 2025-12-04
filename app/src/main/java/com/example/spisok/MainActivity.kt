package com.example.spisok

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.spisok.data.DataRepository
import com.example.spisok.data.Notification
import com.example.spisok.data.Participant
import com.example.spisok.data.ProductList
import com.example.spisok.data.ProductItem
import com.example.spisok.data.Category
import com.example.spisok.data.Template
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import com.example.spisok.ui.components.CreateListDialog
import com.example.spisok.ui.screens.CategoriesScreen
import com.example.spisok.ui.screens.NotificationsScreen
import com.example.spisok.ui.screens.ProductListDetailScreen
import com.example.spisok.ui.screens.ProductListsScreen
import com.example.spisok.ui.screens.SettingsScreen
import com.example.spisok.ui.screens.TemplatesScreen
import com.example.spisok.ui.theme.SpisokTheme
import com.example.spisok.notifications.NotificationScheduler
import com.example.spisok.firebase.FirebaseService
import com.example.spisok.ui.components.AuthDialog
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import java.util.UUID

/**
 * Перечисление экранов приложения
 */
sealed class Screen {
    data object ProductLists : Screen()
    data object Settings : Screen()
    data object Notifications : Screen()
    data object Categories : Screen()
    data object Templates : Screen()
    data class ProductListDetail(val listId: String) : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpisokTheme {
                ProductListsApp()
            }
        }
    }
}

@Composable
fun ProductListsApp() {
    val context = LocalContext.current
    val dataRepository = remember { DataRepository(context) }
    val notificationScheduler = remember { NotificationScheduler(context) }
    val firebaseService = remember { FirebaseService() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    // Состояние текущего экрана
    var currentScreen by remember { mutableStateOf<Screen>(Screen.ProductLists) }
    
    // Состояние для списков продуктов - загружаем из сохраненных данных
    var productLists by remember { 
        mutableStateOf<List<ProductList>>(dataRepository.loadProductLists())
    }
    
    // Состояние для участников - загружаем из сохраненных данных
    var participants by remember { 
        mutableStateOf<List<Participant>>(dataRepository.loadParticipants())
    }
    
    // Состояние для уведомлений - загружаем из сохраненных данных
    var notifications by remember { 
        mutableStateOf<List<Notification>>(dataRepository.loadNotifications())
    }
    
    // Состояние глобального переключателя уведомлений
    var notificationsEnabled by remember { 
        mutableStateOf(dataRepository.loadNotificationsEnabled())
    }
    
    // Состояние для категорий - загружаем из сохраненных данных
    var categories by remember { 
        mutableStateOf<List<Category>>(dataRepository.loadCategories())
    }
    
    // Состояние для шаблонов - загружаем из сохраненных данных
    var templates by remember { 
        mutableStateOf<List<Template>>(dataRepository.loadTemplates())
    }
    
    // Состояние авторизации
    var isAuthenticated by remember { 
        mutableStateOf(firebaseService.getCurrentUserId() != null)
    }
    var showAuthDialog by remember { mutableStateOf(!isAuthenticated) }
    var isSignUp by remember { mutableStateOf(false) }
    
    // Состояние для диалога создания списка
    var showCreateDialog by remember { mutableStateOf(false) }
    
    // Авторизация при первом запуске
    LaunchedEffect(Unit) {
        if (!isAuthenticated) {
            val result = firebaseService.signInAnonymously()
            result.onSuccess { userId ->
                Log.d("MainActivity", "Авторизация успешна. User ID: $userId")
                isAuthenticated = true
                showAuthDialog = false
            }.onFailure { error ->
                Log.e("MainActivity", "Ошибка авторизации: ${error.message}", error)
                // Оставляем диалог авторизации открытым
            }
        }
    }
    
    // Получаем списки, отправленные для текущего пользователя
    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            Log.d("MainActivity", "Начинаем получать shared lists")
            firebaseService.getSharedLists()
                .onEach { sharedLists ->
                    Log.d("MainActivity", "Получено ${sharedLists.size} shared lists")
                    // Добавляем новые списки, которых еще нет
                    sharedLists.forEach { sharedList ->
                        if (productLists.none { it.id == sharedList.id }) {
                            Log.d("MainActivity", "Добавляем новый список: ${sharedList.name} (${sharedList.id})")
                            
                            // Создаем категории из полученного списка и обновляем ID товаров
                            val updatedItems = sharedList.items.map { item ->
                                if (item.category.isNotBlank()) {
                                    // Ищем категорию по имени (в новом формате category содержит название)
                                    val existingCategory = categories.find { it.name == item.category }
                                    
                                    if (existingCategory != null) {
                                        // Категория уже есть, используем её ID
                                        item.copy(category = existingCategory.id)
                                    } else {
                                        // Категории нет, создаем новую
                                        val newCategory = Category(
                                            id = UUID.randomUUID().toString(),
                                            name = item.category
                                        )
                                        categories = categories + newCategory
                                        Log.d("MainActivity", "Создана новая категория: ${item.category} (${newCategory.id})")
                                        item.copy(category = newCategory.id)
                                    }
                                } else {
                                    item
                                }
                            }
                            
                            val updatedList = sharedList.copy(items = updatedItems)
                            productLists = productLists + updatedList
                        } else {
                            Log.d("MainActivity", "Список ${sharedList.name} уже существует, пропускаем")
                        }
                    }
                }
                .collect()
        }
    }
    
    // Планируем все уведомления при первом запуске
    LaunchedEffect(Unit) {
        if (notificationsEnabled) {
            notificationScheduler.rescheduleAllNotifications(notifications, notificationsEnabled)
        }
    }
    
    // Сохраняем списки продуктов при изменении
    LaunchedEffect(productLists) {
        dataRepository.saveProductLists(productLists)
    }
    
    // Сохраняем участников при изменении
    LaunchedEffect(participants) {
        dataRepository.saveParticipants(participants)
    }
    
    // Сохраняем уведомления при изменении
    LaunchedEffect(notifications) {
        dataRepository.saveNotifications(notifications)
    }
    
    // Сохраняем состояние глобального переключателя уведомлений
    LaunchedEffect(notificationsEnabled) {
        dataRepository.saveNotificationsEnabled(notificationsEnabled)
    }
    
    // Сохраняем категории при изменении
    LaunchedEffect(categories) {
        dataRepository.saveCategories(categories)
    }
    
    // Сохраняем шаблоны при изменении
    LaunchedEffect(templates) {
        dataRepository.saveTemplates(templates)
    }
    
    // Обработчики событий
    val onCreateListClick = { showCreateDialog = true }
    val onListClick = { listId: String ->
        currentScreen = Screen.ProductListDetail(listId)
    }
    val onMenuClick = {
        scope.launch {
            drawerState.open()
        }
        Unit
    }
    val onSettingsClick = {
        currentScreen = Screen.Settings
    }
    val onBackFromSettings = {
        currentScreen = Screen.ProductLists
    }
    val onBackFromDetail = {
        currentScreen = Screen.ProductLists
    }
    val onDeleteList = { listId: String ->
        productLists = productLists.filter { it.id != listId }
        currentScreen = Screen.ProductLists
    }
    val onUpdateListItems = { listId: String, items: List<ProductItem> ->
        productLists = productLists.map { list ->
            if (list.id == listId) {
                list.copy(items = items)
            } else {
                list
            }
        }
    }
    val onConfirmCreate = { listName: String ->
        val newList = ProductList(
            id = UUID.randomUUID().toString(),
            name = listName
        )
        productLists = productLists + newList
    }
    val onAddParticipant = { userId: String, name: String ->
        val newParticipant = Participant(
            id = userId,
            name = name
        )
        participants = participants + newParticipant
    }
    val onRemoveParticipant = { participantId: String ->
        participants = participants.filter { it.id != participantId }
    }
    val onNotificationsToggle = { enabled: Boolean ->
        notificationsEnabled = enabled
        // Перепланируем все уведомления при изменении глобального переключателя
        notificationScheduler.rescheduleAllNotifications(notifications, enabled)
    }
    val onNotificationsClick = {
        scope.launch {
            drawerState.close()
        }
        currentScreen = Screen.Notifications
    }
    val onCategoriesClick = {
        scope.launch {
            drawerState.close()
        }
        currentScreen = Screen.Categories
    }
    val onTemplatesClick = {
        scope.launch {
            drawerState.close()
        }
        currentScreen = Screen.Templates
    }
    val onBackFromNotifications = {
        currentScreen = Screen.ProductLists
    }
    val onBackFromCategories = {
        currentScreen = Screen.ProductLists
    }
    val onBackFromTemplates = {
        currentScreen = Screen.ProductLists
    }
    val onAddCategory = { name: String ->
        val newCategory = Category(
            id = UUID.randomUUID().toString(),
            name = name
        )
        categories = categories + newCategory
    }
    val onUpdateCategory = { categoryId: String, newName: String ->
        categories = categories.map {
            if (it.id == categoryId) {
                it.copy(name = newName)
            } else {
                it
            }
        }
    }
    val onDeleteCategories = { categoryIds: Set<String> ->
        categories = categories.filter { it.id !in categoryIds }
    }
    val onUseTemplate = { template: Template ->
        // Создаем новый список на основе шаблона
        val newList = ProductList(
            id = UUID.randomUUID().toString(),
            name = template.name,
            items = template.items.map { item ->
                // Создаем новые ID для товаров, чтобы они были независимыми
                item.copy(id = UUID.randomUUID().toString(), isPurchased = false)
            }
        )
        productLists = productLists + newList
        // Переходим на главный экран
        currentScreen = Screen.ProductLists
    }
    val onDeleteTemplates = { templateIds: Set<String> ->
        templates = templates.filter { it.id !in templateIds }
    }
    val onSaveAsTemplate = { templateName: String, items: List<ProductItem> ->
        val newTemplate = Template(
            id = UUID.randomUUID().toString(),
            name = templateName,
            items = items.map { item ->
                // Создаем новые ID для товаров в шаблоне
                item.copy(id = UUID.randomUUID().toString(), isPurchased = false)
            }
        )
        templates = templates + newTemplate
    }
    val onShareList = { listId: String, participantId: String ->
        val list = productLists.find { it.id == listId }
        if (list != null) {
            scope.launch {
                Log.d("MainActivity", "Отправка списка $listId участнику $participantId")
                val result = firebaseService.shareList(list, participantId, categories)
                result.onSuccess {
                    Log.d("MainActivity", "Список успешно отправлен")
                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                        Toast.makeText(context, "Список отправлен", Toast.LENGTH_SHORT).show()
                    }
                }.onFailure { error ->
                    Log.e("MainActivity", "Ошибка отправки списка: ${error.message}", error)
                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                        Toast.makeText(context, "Ошибка отправки: ${error.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            Log.w("MainActivity", "Список с ID $listId не найден")
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                Toast.makeText(context, "Список не найден", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    val onSignIn = { email: String, password: String ->
        scope.launch {
            val result = firebaseService.signInWithEmail(email, password)
            result.onSuccess {
                isAuthenticated = true
                showAuthDialog = false
            }.onFailure { error ->
                println("Ошибка входа: ${error.message}")
            }
        }
        Unit
    }
    
    val onSignUp = { email: String, password: String ->
        scope.launch {
            val result = firebaseService.signUpWithEmail(email, password)
            result.onSuccess {
                isAuthenticated = true
                showAuthDialog = false
            }.onFailure { error ->
                println("Ошибка регистрации: ${error.message}")
            }
        }
        Unit
    }
    
    val onSignInAnonymously = {
        scope.launch {
            val result = firebaseService.signInAnonymously()
            result.onSuccess {
                isAuthenticated = true
                showAuthDialog = false
            }.onFailure { error ->
                println("Ошибка анонимного входа: ${error.message}")
            }
        }
        Unit
    }
    val onAddNotification = { message: String, time: String, daysOfWeek: Set<Int> ->
        val newNotification = Notification(
            id = UUID.randomUUID().toString(),
            message = message,
            time = time,
            daysOfWeek = daysOfWeek,
            // Уведомление включается только если глобальный переключатель включен
            isEnabled = notificationsEnabled
        )
        notifications = notifications + newNotification
        // Запланировать уведомление (только если notificationsEnabled == true)
        if (notificationsEnabled && newNotification.isEnabled) {
            notificationScheduler.scheduleNotification(newNotification)
        }
    }
    val onToggleNotification = { notificationId: String, enabled: Boolean ->
        val updatedNotifications = notifications.map {
            if (it.id == notificationId) {
                val updated = it.copy(isEnabled = enabled && notificationsEnabled)
                // Обновляем планирование уведомления
                notificationScheduler.cancelNotification(notificationId)
                if (updated.isEnabled && notificationsEnabled) {
                    notificationScheduler.scheduleNotification(updated)
                }
                updated
            } else {
                it
            }
        }
        notifications = updatedNotifications
    }
    val onDeleteNotifications = { notificationIds: Set<String> ->
        // Отменяем запланированные уведомления перед удалением
        notificationIds.forEach { notificationId ->
            notificationScheduler.cancelNotification(notificationId)
        }
        notifications = notifications.filter { it.id !in notificationIds }
    }
    
    // Отслеживаем состояние drawer
    var isDrawerOpen by remember { mutableStateOf(drawerState.currentValue == DrawerValue.Open) }
    LaunchedEffect(Unit) {
        snapshotFlow { drawerState.currentValue }.collect { value ->
            isDrawerOpen = value == DrawerValue.Open
        }
    }
    
    // Обработка системной кнопки "Назад"
    BackHandler(enabled = currentScreen != Screen.ProductLists || isDrawerOpen) {
        if (isDrawerOpen) {
            // Если открыт drawer, закрываем его
            scope.launch {
                drawerState.close()
            }
        } else {
            // Если не на главном экране, переходим на главный
            currentScreen = Screen.ProductLists
        }
    }
    
    // Отображаем текущий экран
    when (val screen = currentScreen) {
        is Screen.ProductLists -> {
                ProductListsScreen(
                onMenuClick = onMenuClick,
                onSettingsClick = onSettingsClick,
                onCreateListClick = onCreateListClick,
                onListClick = onListClick,
                productLists = productLists,
                drawerState = drawerState,
                onNotificationsClick = onNotificationsClick,
                onCategoriesClick = onCategoriesClick,
                onTemplatesClick = onTemplatesClick
            )
            
            // Показываем диалог создания списка
            if (showCreateDialog) {
                CreateListDialog(
                    onDismiss = { showCreateDialog = false },
                    onConfirm = onConfirmCreate
                )
            }
        }
        is Screen.Settings -> {
            SettingsScreen(
                onBackClick = onBackFromSettings,
                participants = participants,
                onAddParticipant = onAddParticipant,
                onRemoveParticipant = onRemoveParticipant,
                onNotificationsToggle = onNotificationsToggle,
                notificationsEnabled = notificationsEnabled,
                currentUserId = firebaseService.getCurrentUserId() ?: "",
                onSignOut = {
                    firebaseService.signOut()
                    isAuthenticated = false
                    showAuthDialog = true
                }
            )
        }
        is Screen.ProductListDetail -> {
            val currentList = productLists.find { it.id == screen.listId }
            if (currentList != null) {
                ProductListDetailScreen(
                    listName = currentList.name,
                    items = currentList.items,
                    categories = categories,
                    participants = participants,
                    onBackClick = onBackFromDetail,
                    onItemsChange = { items ->
                        onUpdateListItems(screen.listId, items)
                    },
                    onSaveAsTemplate = onSaveAsTemplate,
                    onDeleteList = {
                        onDeleteList(screen.listId)
                    },
                    onShareList = { participantId ->
                        onShareList(screen.listId, participantId)
                    }
                )
            }
        }
        is Screen.Notifications -> {
            NotificationsScreen(
                onBackClick = onBackFromNotifications,
                notifications = notifications,
                onAddNotification = onAddNotification,
                onToggleNotification = onToggleNotification,
                onDeleteNotifications = onDeleteNotifications,
                globalNotificationsEnabled = notificationsEnabled
            )
        }
        is Screen.Categories -> {
            CategoriesScreen(
                categories = categories,
                onBackClick = onBackFromCategories,
                onAddCategory = onAddCategory,
                onUpdateCategory = onUpdateCategory,
                onDeleteCategories = onDeleteCategories
            )
        }
        is Screen.Templates -> {
            TemplatesScreen(
                templates = templates,
                onBackClick = onBackFromTemplates,
                onUseTemplate = onUseTemplate,
                onDeleteTemplates = onDeleteTemplates
            )
        }
    }
    
    // Диалог авторизации
    if (showAuthDialog) {
        AuthDialog(
            isSignUp = isSignUp,
            onDismiss = { showAuthDialog = false },
            onSignIn = onSignIn,
            onSignUp = onSignUp,
            onSignInAnonymously = onSignInAnonymously
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProductListsAppPreview() {
    SpisokTheme {
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        ProductListsScreen(
            drawerState = drawerState
        )
    }
}