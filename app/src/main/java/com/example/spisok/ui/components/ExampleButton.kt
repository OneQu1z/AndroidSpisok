package com.example.spisok.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Пример кнопки, созданной на основе дизайна из Figma
 * 
 * Инструкция:
 * 1. Откройте ваш дизайн кнопки в Figma
 * 2. Запишите параметры:
 *    - Цвет фона (Background)
 *    - Цвет текста (Text Color)
 *    - Радиус скругления углов (Border Radius)
 *    - Высота кнопки (Height)
 *    - Отступы (Padding)
 * 3. Обновите значения ниже
 */
@Composable
fun ExampleButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp), // Замените на высоту из Figma
        shape = RoundedCornerShape(12.dp), // Замените на радиус из Figma
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary, // Замените на цвет из Figma
            contentColor = MaterialTheme.colorScheme.onPrimary, // Замените на цвет текста из Figma
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold // Замените на вес шрифта из Figma
            )
        )
    }
}

