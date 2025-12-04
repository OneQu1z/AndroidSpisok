package com.example.spisok.data

/**
 * Модель данных для товара в списке
 */
data class ProductItem(
    val id: String,
    val name: String,
    val category: String, // ID категории или название (для обратной совместимости)
    val isPurchased: Boolean = false
)

