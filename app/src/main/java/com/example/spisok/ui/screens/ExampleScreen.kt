package com.example.spisok.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.spisok.ui.components.ExampleButton

/**
 * Пример экрана, созданного на основе дизайна из Figma
 * 
 * Инструкция:
 * 1. Откройте ваш экран в Figma
 * 2. Определите структуру:
 *    - Какие элементы на экране (кнопки, тексты, изображения)
 *    - Как они расположены (Column, Row, Box)
 *    - Какие отступы между элементами (padding, spacing)
 * 3. Создайте компоненты для каждого элемента
 * 4. Соберите экран используя Layout компоненты Compose
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExampleScreen(
    onButtonClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Пример экрана") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp), // Замените на отступы из Figma
            verticalArrangement = Arrangement.spacedBy(16.dp), // Замените на spacing из Figma
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Заголовок
            Text(
                text = "Заголовок экрана",
                style = MaterialTheme.typography.headlineMedium
            )
            
            // Описание
            Text(
                text = "Описание или подзаголовок",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Кнопка
            ExampleButton(
                text = "Нажмите меня",
                onClick = onButtonClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

