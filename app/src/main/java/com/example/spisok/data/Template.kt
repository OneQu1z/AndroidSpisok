package com.example.spisok.data

/**
 * Модель данных для шаблона списка продуктов
 */
data class Template(
    val id: String,
    val name: String,
    val items: List<ProductItem> = emptyList()
)

