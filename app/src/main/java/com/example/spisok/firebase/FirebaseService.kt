package com.example.spisok.firebase

import com.example.spisok.data.ProductList
import com.example.spisok.data.ProductItem
import com.example.spisok.data.Category
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Сервис для работы с Firebase Realtime Database
 */
class FirebaseService {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    
    /**
     * Получить текущего пользователя ID
     */
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
    
    /**
     * Войти анонимно (для быстрого старта)
     */
    suspend fun signInAnonymously(): Result<String> {
        return try {
            val result = auth.signInAnonymously().await()
            val userId = result.user?.uid ?: ""
            
            // Создаем тестовую запись для проверки, что пользователь в базе
            if (userId.isNotBlank()) {
                database.reference
                    .child("users")
                    .child(userId)
                    .child("created_at")
                    .setValue(ServerValue.TIMESTAMP)
                    .await()
            }
            
            android.util.Log.d("FirebaseService", "Анонимный вход успешен. User ID: $userId")
            Result.success(userId)
        } catch (e: Exception) {
            android.util.Log.e("FirebaseService", "Ошибка анонимного входа: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * Войти с email и паролем
     */
    suspend fun signInWithEmail(email: String, password: String): Result<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: ""
            
            // Обновляем информацию о пользователе
            if (userId.isNotBlank()) {
                database.reference
                    .child("users")
                    .child(userId)
                    .child("last_login")
                    .setValue(ServerValue.TIMESTAMP)
                    .await()
            }
            
            android.util.Log.d("FirebaseService", "Вход с email успешен. User ID: $userId")
            Result.success(userId)
        } catch (e: Exception) {
            android.util.Log.e("FirebaseService", "Ошибка входа с email: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * Зарегистрироваться с email и паролем
     */
    suspend fun signUpWithEmail(email: String, password: String): Result<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: ""
            
            // Создаем запись о пользователе
            if (userId.isNotBlank()) {
                database.reference
                    .child("users")
                    .child(userId)
                    .setValue(mapOf(
                        "email" to email,
                        "created_at" to ServerValue.TIMESTAMP
                    ))
                    .await()
            }
            
            android.util.Log.d("FirebaseService", "Регистрация успешна. User ID: $userId")
            Result.success(userId)
        } catch (e: Exception) {
            android.util.Log.e("FirebaseService", "Ошибка регистрации: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * Выйти
     */
    fun signOut() {
        auth.signOut()
    }
    
    /**
     * Поделиться списком с участником
     */
    suspend fun shareList(list: ProductList, participantId: String, categories: List<com.example.spisok.data.Category>): Result<Unit> {
        return try {
            val currentUserId = getCurrentUserId() ?: return Result.failure(Exception("Не авторизован"))
            
            android.util.Log.d("FirebaseService", "Отправка списка: listId=${list.id}, participantId=$participantId, currentUserId=$currentUserId")
            
            val categoryMap = categories.associateBy { it.id }
            
            val shareData = mapOf(
                "listId" to list.id,
                "listName" to list.name,
                "items" to list.items.map { item ->
                    val categoryName = if (item.category.isNotBlank()) {
                        categoryMap[item.category]?.name ?: item.category
                    } else {
                        ""
                    }
                    mapOf(
                        "id" to item.id,
                        "name" to item.name,
                        "categoryId" to (item.category ?: ""),
                        "categoryName" to categoryName,
                        "isPurchased" to item.isPurchased
                    )
                },
                "fromUserId" to currentUserId,
                "timestamp" to ServerValue.TIMESTAMP
            )
            
            database.reference
                .child("shared_lists")
                .child(participantId)
                .child(list.id)
                .setValue(shareData)
                .await()
            
            android.util.Log.d("FirebaseService", "Список успешно отправлен в shared_lists/$participantId/${list.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("FirebaseService", "Ошибка отправки списка: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * Получить списки, отправленные для текущего пользователя
     */
    fun getSharedLists(): Flow<List<ProductList>> = callbackFlow {
        val currentUserId = getCurrentUserId() ?: run {
            android.util.Log.w("FirebaseService", "getSharedLists: пользователь не авторизован")
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        
        android.util.Log.d("FirebaseService", "Начинаем слушать shared_lists для userId: $currentUserId")
        
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                android.util.Log.d("FirebaseService", "Получены данные из shared_lists для userId: $currentUserId, количество: ${snapshot.childrenCount}")
                val lists = mutableListOf<ProductList>()
                
                snapshot.children.forEach { listSnapshot ->
                    try {
                        val listData = listSnapshot.value as? Map<*, *> ?: return@forEach
                        val listId = listSnapshot.key ?: return@forEach
                        val listName = listData["listName"] as? String ?: return@forEach
                        val itemsData = listData["items"] as? List<*> ?: emptyList<Any>()
                        
                        android.util.Log.d("FirebaseService", "Обработка списка: id=$listId, name=$listName, items=${itemsData.size}")
                        
                        // Сначала собираем информацию о категориях из items
                        val categoryInfoMap = mutableMapOf<String, String>() // categoryId -> categoryName
                        itemsData.forEach { itemData ->
                            val itemMap = itemData as? Map<*, *> ?: return@forEach
                            val categoryName = itemMap["categoryName"] as? String
                            val categoryId = itemMap["categoryId"] as? String
                            val oldCategory = itemMap["category"] as? String
                            val finalCategoryId = categoryId ?: oldCategory ?: ""
                            if (finalCategoryId.isNotBlank() && categoryName != null && categoryName.isNotBlank()) {
                                categoryInfoMap[finalCategoryId] = categoryName
                            }
                        }
                        
                        val items = itemsData.mapNotNull { itemData ->
                            val itemMap = itemData as? Map<*, *> ?: return@mapNotNull null
                            // Получаем название категории (новый формат) или ID (старый формат)
                            val categoryName = itemMap["categoryName"] as? String
                            val categoryId = itemMap["categoryId"] as? String
                            val oldCategory = itemMap["category"] as? String // Для обратной совместимости
                            val finalCategoryId = categoryId ?: oldCategory ?: ""
                            
                            // Сохраняем categoryName в поле category для последующей обработки
                            // В MainActivity мы создадим категорию и обновим ID
                            ProductItem(
                                id = itemMap["id"] as? String ?: "",
                                name = itemMap["name"] as? String ?: "",
                                category = categoryName ?: finalCategoryId, // Используем название, если есть
                                isPurchased = itemMap["isPurchased"] as? Boolean ?: false
                            )
                        }
                        
                        lists.add(ProductList(
                            id = listId,
                            name = listName,
                            items = items,
                            participants = emptyList()
                        ))
                    } catch (e: Exception) {
                        android.util.Log.e("FirebaseService", "Ошибка обработки списка: ${e.message}", e)
                    }
                }
                
                android.util.Log.d("FirebaseService", "Отправляем ${lists.size} списков в UI")
                trySend(lists)
            }
            
            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("FirebaseService", "Ошибка получения списков: ${error.message}")
                close(error.toException())
            }
        }
        
        database.reference
            .child("shared_lists")
            .child(currentUserId)
            .addValueEventListener(listener)
        
        awaitClose {
            android.util.Log.d("FirebaseService", "Останавливаем слушатель shared_lists")
            database.reference
                .child("shared_lists")
                .child(currentUserId)
                .removeEventListener(listener)
        }
    }
    
    /**
     * Синхронизировать список в реальном времени
     */
    fun syncList(listId: String): Flow<ProductList?> = callbackFlow {
        val currentUserId = getCurrentUserId() ?: run {
            trySend(null)
            close()
            return@callbackFlow
        }
        
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val listData = snapshot.value as? Map<*, *> ?: run {
                        trySend(null)
                        return
                    }
                    
                    val listName = listData["listName"] as? String ?: ""
                    val itemsData = listData["items"] as? List<*> ?: emptyList<Any>()
                    
                    val items = itemsData.mapNotNull { itemData ->
                        val itemMap = itemData as? Map<*, *> ?: return@mapNotNull null
                        ProductItem(
                            id = itemMap["id"] as? String ?: "",
                            name = itemMap["name"] as? String ?: "",
                            category = itemMap["category"] as? String ?: "",
                            isPurchased = itemMap["isPurchased"] as? Boolean ?: false
                        )
                    }
                    
                    val list = ProductList(
                        id = listId,
                        name = listName,
                        items = items,
                        participants = emptyList()
                    )
                    
                    trySend(list)
                } catch (e: Exception) {
                    trySend(null)
                }
            }
            
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        
        database.reference
            .child("lists")
            .child(listId)
            .addValueEventListener(listener)
        
        awaitClose {
            database.reference
                .child("lists")
                .child(listId)
                .removeEventListener(listener)
        }
    }
    
    /**
     * Обновить список на сервере
     */
    suspend fun updateList(list: ProductList): Result<Unit> {
        return try {
            val listData = mapOf(
                "listName" to list.name,
                "items" to list.items.map { item ->
                    mapOf(
                        "id" to item.id,
                        "name" to item.name,
                        "category" to (item.category ?: ""),
                        "isPurchased" to item.isPurchased
                    )
                }
            )
            
            database.reference
                .child("lists")
                .child(list.id)
                .setValue(listData)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

