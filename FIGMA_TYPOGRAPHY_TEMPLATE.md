# Шаблон для переноса типографики из Figma

## Инструкция:
1. Откройте ваш дизайн в Figma
2. Найдите все текстовые стили
3. Запишите параметры каждого стиля (размер, вес, межстрочный интервал)
4. Обновите файл `app/src/main/java/com/example/spisok/ui/theme/Type.kt`

## Параметры для записи из Figma:

### Для каждого текстового стиля запишите:
- **Font Family** (семейство шрифта) - например: Roboto, Inter, etc.
- **Font Size** (размер) - в пикселях (px)
- **Font Weight** (толщина) - например: Regular (400), Medium (500), Bold (700)
- **Line Height** (межстрочный интервал) - в пикселях или процентах
- **Letter Spacing** (межбуквенный интервал) - в пикселях или процентах

## Пример структуры для Type.kt:

```kotlin
val Typography = Typography(
    // Заголовки
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default, // Замените на ваш шрифт
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp, // Из Figma
        lineHeight = 64.sp, // Из Figma
        letterSpacing = (-0.25).sp // Из Figma
    ),
    
    // Заголовок H1
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp, // Из Figma
        lineHeight = 40.sp, // Из Figma
        letterSpacing = 0.sp
    ),
    
    // Заголовок H2
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp, // Из Figma
        lineHeight = 36.sp, // Из Figma
        letterSpacing = 0.sp
    ),
    
    // Обычный текст
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp, // Из Figma
        lineHeight = 24.sp, // Из Figma
        letterSpacing = 0.5.sp
    ),
    
    // Мелкий текст
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp, // Из Figma
        lineHeight = 16.sp, // Из Figma
        letterSpacing = 0.4.sp
    )
)
```

## Как получить параметры в Figma:
1. Выберите текстовый элемент
2. В правой панели найдите секцию "Text"
3. Запишите все параметры:
   - Font: название и вес
   - Size: размер в px
   - Line height: в px или %
   - Letter spacing: в px или %

## Конвертация единиц:
- **px → sp**: В Android используйте sp для текста (обычно 1px = 1sp, но можно масштабировать)
- **% → sp**: Для line height в процентах, умножьте на размер шрифта (например, 150% от 16px = 24sp)

## Добавление кастомных шрифтов:
Если в Figma используются кастомные шрифты:
1. Скачайте файлы шрифтов (.ttf или .otf)
2. Поместите их в `app/src/main/res/font/`
3. Используйте в Type.kt:
```kotlin
val CustomFontFamily = FontFamily(
    Font(R.font.your_font_regular, FontWeight.Normal),
    Font(R.font.your_font_medium, FontWeight.Medium),
    Font(R.font.your_font_bold, FontWeight.Bold)
)
```

