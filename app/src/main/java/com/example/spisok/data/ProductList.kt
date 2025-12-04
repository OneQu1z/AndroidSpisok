package com.example.spisok.data

/**
 * Модель данных для списка продуктов
 */
data class ProductList(
    val id: String,
    val name: String,
    val items: List<ProductItem> = emptyList(),
    val participants: List<String> = emptyList() // Список ID участников, имеющих доступ к списку
)

