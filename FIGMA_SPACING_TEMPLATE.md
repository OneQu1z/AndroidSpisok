# Шаблон для переноса отступов и размеров из Figma

## Инструкция:
1. Откройте ваш дизайн в Figma
2. Найдите все используемые отступы (padding, margin, gap)
3. Запишите значения в пикселях
4. Используйте эти значения в ваших компонентах Compose

## Создание файла с константами отступов:

Создайте файл `app/src/main/java/com/example/spisok/ui/theme/Dimens.kt`:

```kotlin
package com.example.spisok.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Константы отступов и размеров из Figma дизайна
 * 
 * Инструкция:
 * 1. Найдите все отступы в вашем Figma дизайне
 * 2. Замените значения ниже на значения из Figma
 * 3. Используйте эти константы в ваших компонентах
 */

// Базовые отступы
val Spacing4 = 4.dp   // Замените на значение из Figma
val Spacing8 = 8.dp   // Замените на значение из Figma
val Spacing12 = 12.dp // Замените на значение из Figma
val Spacing16 = 16.dp // Замените на значение из Figma
val Spacing24 = 24.dp // Замените на значение из Figma
val Spacing32 = 32.dp // Замените на значение из Figma
val Spacing48 = 48.dp // Замените на значение из Figma

// Отступы для экранов
val ScreenPadding = 16.dp // Замените на значение из Figma
val ScreenPaddingHorizontal = 16.dp
val ScreenPaddingVertical = 16.dp

// Отступы для карточек
val CardPadding = 16.dp
val CardSpacing = 12.dp

// Размеры элементов
val ButtonHeight = 56.dp // Замените на высоту кнопки из Figma
val IconSize = 24.dp     // Замените на размер иконки из Figma
val AvatarSize = 40.dp   // Замените на размер аватара из Figma

// Радиусы скругления
val CornerRadiusSmall = 8.dp   // Замените на значение из Figma
val CornerRadiusMedium = 12.dp // Замените на значение из Figma
val CornerRadiusLarge = 16.dp  // Замените на значение из Figма
```

## Как получить отступы в Figma:

### Padding (внутренние отступы):
1. Выберите элемент (Frame, Component)
2. В правой панели найдите секцию "Layout"
3. Найдите "Padding" или используйте Auto Layout
4. Запишите значения для всех сторон (Top, Right, Bottom, Left)

### Margin (внешние отступы):
1. Выберите элемент
2. Посмотрите расстояние до соседних элементов
3. Запишите значения в пикселях

### Gap (расстояние между элементами в Auto Layout):
1. Выберите Frame с Auto Layout
2. В правой панели найдите "Gap"
3. Запишите значение в пикселях

## Конвертация единиц:
- **px → dp**: В Android используйте dp для размеров (обычно 1px = 1dp на mdpi экранах)
- Для точности: 1dp = 1px на mdpi, 1.5px на hdpi, 2px на xhdpi, 3px на xxhdpi

## Использование в Compose:

```kotlin
// Padding
Column(
    modifier = Modifier.padding(ScreenPadding)
) { }

// Spacing между элементами
Column(
    verticalArrangement = Arrangement.spacedBy(Spacing16)
) { }

// Размеры
Box(
    modifier = Modifier
        .width(IconSize)
        .height(IconSize)
) { }
```

