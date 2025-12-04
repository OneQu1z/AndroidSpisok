# Настройка Firebase для совместного доступа

## Шаги настройки:

1. **Создайте проект в Firebase Console**
   - Перейдите на https://console.firebase.google.com/
   - Создайте новый проект или выберите существующий

2. **Добавьте Android приложение**
   - Нажмите "Add app" → выберите Android
   - Введите package name: `com.example.spisok`
   - Скачайте файл `google-services.json`

3. **Добавьте google-services.json в проект**
   - Скопируйте файл `google-services.json` в папку `app/`

4. **Включите Realtime Database**
   - В Firebase Console перейдите в "Realtime Database"
   - Создайте базу данных в режиме "Test mode" (для разработки)
   - Скопируйте URL базы данных
https://androidspisok-default-rtdb.firebaseio.com/:null
5. **Включите Authentication**
   - В Firebase Console перейдите в "Authentication"
   - Включите "Anonymous" и "Email/Password" методы входа

6. **Настройте правила безопасности (Realtime Database)**
   ```json
   {
     "rules": {
       "users": {
         "$userId": {
           ".read": "$userId === auth.uid",
           ".write": "$userId === auth.uid"
         }
       },
       "shared_lists": {
         "$userId": {
           ".read": "$userId === auth.uid",
           ".write": "auth != null"
         }
       },
       "lists": {
         "$listId": {
           ".read": "auth != null",
           ".write": "auth != null"
         }
       }
     }
   }
   ```
   
   **Важно**: Правило `.write` для `shared_lists` должно быть `"auth != null"` (без проверки `$userId === auth.uid`), чтобы пользователи могли отправлять списки друг другу!

## Структура данных в Firebase:

```
shared_lists/
  {userId}/
    {listId}/
      listId: string
      listName: string
      items: array
      fromUserId: string
      timestamp: timestamp

lists/
  {listId}/
    listName: string
    items: array
```

## Использование:

1. При первом запуске приложение автоматически войдет анонимно
2. Можно зарегистрироваться/войти через email в настройках
3. Для отправки списка выберите участника (по его Firebase User ID)
4. Полученные списки автоматически синхронизируются в реальном времени

