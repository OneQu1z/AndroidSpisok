package com.example.spisok.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Репозиторий для сохранения и загрузки данных приложения
 */
class DataRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "spisok_prefs",
        Context.MODE_PRIVATE
    )
    private val gson = Gson()

    companion object {
        private const val KEY_PRODUCT_LISTS = "product_lists"
        private const val KEY_PARTICIPANTS = "participants"
        private const val KEY_NOTIFICATIONS = "notifications"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_CATEGORIES = "categories"
        private const val KEY_TEMPLATES = "templates"
    }

    /**
     * Сохранить списки продуктов
     */
    fun saveProductLists(lists: List<ProductList>) {
        val json = gson.toJson(lists)
        prefs.edit().putString(KEY_PRODUCT_LISTS, json).apply()
    }

    /**
     * Загрузить списки продуктов
     */
    fun loadProductLists(): List<ProductList> {
        val json = prefs.getString(KEY_PRODUCT_LISTS, null) ?: return emptyList()
        val type = object : TypeToken<List<ProductList>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Сохранить участников
     */
    fun saveParticipants(participants: List<Participant>) {
        val json = gson.toJson(participants)
        prefs.edit().putString(KEY_PARTICIPANTS, json).apply()
    }

    /**
     * Загрузить участников
     */
    fun loadParticipants(): List<Participant> {
        val json = prefs.getString(KEY_PARTICIPANTS, null) ?: return emptyList()
        val type = object : TypeToken<List<Participant>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Сохранить уведомления
     */
    fun saveNotifications(notifications: List<com.example.spisok.data.Notification>) {
        val json = gson.toJson(notifications)
        prefs.edit().putString(KEY_NOTIFICATIONS, json).apply()
    }

    /**
     * Загрузить уведомления
     */
    fun loadNotifications(): List<com.example.spisok.data.Notification> {
        val json = prefs.getString(KEY_NOTIFICATIONS, null) ?: return emptyList()
        val type = object : TypeToken<List<com.example.spisok.data.Notification>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Сохранить состояние глобального переключателя уведомлений
     */
    fun saveNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
    }

    /**
     * Загрузить состояние глобального переключателя уведомлений
     */
    fun loadNotificationsEnabled(): Boolean {
        return prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true) // По умолчанию включено
    }

    /**
     * Сохранить категории
     */
    fun saveCategories(categories: List<Category>) {
        val json = gson.toJson(categories)
        prefs.edit().putString(KEY_CATEGORIES, json).apply()
    }

    /**
     * Загрузить категории
     */
    fun loadCategories(): List<Category> {
        val json = prefs.getString(KEY_CATEGORIES, null) ?: return emptyList()
        val type = object : TypeToken<List<Category>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Сохранить шаблоны
     */
    fun saveTemplates(templates: List<Template>) {
        val json = gson.toJson(templates)
        prefs.edit().putString(KEY_TEMPLATES, json).apply()
    }

    /**
     * Загрузить шаблоны
     */
    fun loadTemplates(): List<Template> {
        val json = prefs.getString(KEY_TEMPLATES, null) ?: return getDefaultTemplates()
        val type = object : TypeToken<List<Template>>() {}.type
        return try {
            gson.fromJson(json, type) ?: getDefaultTemplates()
        } catch (e: Exception) {
            getDefaultTemplates()
        }
    }

    /**
     * Получить шаблоны по умолчанию
     */
    private fun getDefaultTemplates(): List<Template> {
        return listOf(
            Template(
                id = "template_weekly",
                name = "Еженедельные продукты",
                items = listOf(
                    ProductItem(id = "item1", name = "Хлеб", category = "Продукты", isPurchased = false),
                    ProductItem(id = "item2", name = "Молоко", category = "Продукты", isPurchased = false),
                    ProductItem(id = "item3", name = "Яйца", category = "Продукты", isPurchased = false),
                    ProductItem(id = "item4", name = "Масло", category = "Продукты", isPurchased = false)
                )
            ),
            Template(
                id = "template_barbecue",
                name = "Для шашлыка",
                items = listOf(
                    ProductItem(id = "item5", name = "Мясо", category = "Продукты", isPurchased = false),
                    ProductItem(id = "item6", name = "Уголь", category = "Бытовое", isPurchased = false),
                    ProductItem(id = "item7", name = "Овощи", category = "Продукты", isPurchased = false),
                    ProductItem(id = "item8", name = "Соус", category = "Продукты", isPurchased = false)
                )
            )
        )
    }
}

