# Как проверить, что вы в базе данных Firebase

## Способ 1: Через Firebase Console (Рекомендуется)

1. **Откройте Firebase Console**
   - Перейдите на https://console.firebase.google.com/
   - Выберите ваш проект "androidspisok"

2. **Проверьте Authentication**
   - В левом меню выберите "Authentication"
   - Перейдите на вкладку "Users"
   - Вы должны увидеть список пользователей (анонимных или с email)
   - Скопируйте User UID - это ваш ID пользователя

3. **Проверьте Realtime Database**
   - В левом меню выберите "Realtime Database"
   - Вы должны увидеть структуру данных:
     ```
     users/
       {ваш_user_id}/
         created_at: timestamp
     ```
   - Если вы отправляли списки, также будет:
     ```
     shared_lists/
       {user_id}/
         {list_id}/
           ...
     ```

## Способ 2: Через Logcat в Android Studio

1. **Откройте Logcat**
   - В Android Studio внизу найдите вкладку "Logcat"
   - Или View → Tool Windows → Logcat

2. **Отфильтруйте логи**
   - В поиске введите: `FirebaseService` или `MainActivity`
   - Вы должны увидеть сообщения типа:
     ```
     FirebaseService: Анонимный вход успешен. User ID: abc123...
     MainActivity: Авторизация успешна. User ID: abc123...
     ```

3. **Скопируйте User ID**
   - Это ваш уникальный идентификатор в Firebase

## Способ 3: Через настройки приложения

1. **Откройте приложение**
2. **Перейдите в Настройки** (иконка шестеренки)
3. **В разделе "Совместный доступ"** вы увидите "Ваш ID пользователя"
4. **Скопируйте этот ID**

## Важно!

- **Прямой доступ через URL не работает** - база данных защищена правилами безопасности
- Используйте только Firebase Console для просмотра данных
- User ID появляется сразу после авторизации
- Данные в Realtime Database появляются при:
  - Авторизации (создается запись в `users/{userId}`)
  - Отправке списка (создается запись в `shared_lists/{userId}/{listId}`)

## Структура данных в Firebase:

```
users/
  {user_id}/
    created_at: timestamp
    email: string (если регистрация через email)
    last_login: timestamp

shared_lists/
  {user_id}/
    {list_id}/
      listId: string
      listName: string
      items: array
      fromUserId: string
      timestamp: timestamp
```

