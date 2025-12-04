# Шаблон для переноса цветов из Figma

## Инструкция:
1. Откройте ваш дизайн в Figma
2. Найдите все используемые цвета
3. Замените значения ниже на цвета из Figma
4. Скопируйте обновленные цвета в файл `app/src/main/java/com/example/spisok/ui/theme/Color.kt`

## Цвета из Figma:

### Основные цвета:
```kotlin
// Primary (основной цвет)
val PrimaryColor = Color(0xFF000000) // Замените на ваш цвет

// Secondary (вторичный цвет)
val SecondaryColor = Color(0xFF000000) // Замените на ваш цвет

// Background (фон)
val BackgroundColor = Color(0xFFFFFFFF) // Замените на ваш цвет

// Surface (поверхность)
val SurfaceColor = Color(0xFFFFFFFF) // Замените на ваш цвет

// Error (ошибка)
val ErrorColor = Color(0xFFB3261E) // Замените на ваш цвет
```

### Дополнительные цвета:
```kotlin
// Добавьте сюда дополнительные цвета из вашего дизайна
val AccentColor = Color(0xFF000000)
val TextPrimary = Color(0xFF000000)
val TextSecondary = Color(0xFF666666)
```

## Как получить HEX код цвета в Figma:
1. Выберите элемент с нужным цветом
2. В правой панели найдите свойство Fill или Stroke
3. Нажмите на цветной квадрат
4. В открывшемся окне выберите формат HEX
5. Скопируйте значение (например: #FF5733)
6. Конвертируйте в формат Color: замените # на 0xFF (например: 0xFFFF5733)

## Пример конвертации:
- Figma: `#FF5733` → Kotlin: `Color(0xFFFF5733)`
- Figma: `#000000` → Kotlin: `Color(0xFF000000)`
- Figma: `#FFFFFF` → Kotlin: `Color(0xFFFFFFFF)`

