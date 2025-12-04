# Руководство по переносу дизайна из Figma в Android проект

## Шаг 1: Экспорт ресурсов из Figma

### Цвета (Color Tokens)
1. Откройте ваш дизайн в Figma
2. Найдите все используемые цвета в дизайне
3. Скопируйте HEX-коды цветов
4. Обновите файл `app/src/main/java/com/example/spisok/ui/theme/Color.kt`

### Иконки и изображения
1. Выберите иконку/изображение в Figma
2. Нажмите правой кнопкой → "Export" или используйте панель Export справа
3. Экспортируйте в формате:
   - **SVG** для векторных иконок (рекомендуется)
   - **PNG** для растровых изображений (1x, 2x, 3x для разных плотностей)
4. Сохраните файлы в `app/src/main/res/drawable/` (для SVG) или в соответствующие папки mipmap

### Шрифты и типографика
1. Найдите используемые шрифты в Figma
2. Запишите размеры шрифтов, веса (font-weight), межстрочные интервалы
3. Обновите файл `app/src/main/java/com/example/spisok/ui/theme/Type.kt`

## Шаг 2: Структура компонентов

Создайте структуру папок для ваших UI компонентов:
```
app/src/main/java/com/example/spisok/
├── ui/
│   ├── components/     # Переиспользуемые компоненты
│   ├── screens/        # Экраны приложения
│   └── theme/          # Тема (цвета, типографика)
```

## Шаг 3: Использование Figma плагинов (опционально)

### Плагины для экспорта:
- **Figma to Code** - экспортирует код для различных платформ
- **Figma to Flutter** - если нужно конвертировать в похожий формат
- **Figma Tokens** - для экспорта дизайн-токенов (цвета, шрифты, отступы)

## Шаг 4: Ручная реализация в Compose

1. Создайте экраны в папке `screens/`
2. Создайте переиспользуемые компоненты в `components/`
3. Используйте экспортированные цвета и типографику из темы

## Полезные команды для работы с Figma:

### Экспорт через Figma API (для автоматизации):
```bash
# Установите Figma CLI (если нужно)
npm install -g @figma/cli
```

### Получение токенов через Figma API:
1. Получите Personal Access Token в Figma Settings
2. Используйте API для получения цветов, шрифтов и других токенов

## Примеры:

### Обновление цветов:
Откройте `Color.kt` и замените цвета на те, что из Figma:
```kotlin
val PrimaryColor = Color(0xFF123456) // Ваш цвет из Figma
val SecondaryColor = Color(0xFF789ABC)
```

### Создание компонента:
Создайте файл в `ui/components/Button.kt`:
```kotlin
@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(text = text)
    }
}
```

