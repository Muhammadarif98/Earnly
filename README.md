# Earnly

Android приложение для просмотра статей и рекламных баннеров.

## Требования

- Android Studio Hedgehog | 2023.1.1
- Android SDK 34 (Android 14)
- Gradle 8.2.2
- Kotlin 1.9.0
- JDK 17

## Зависимости

### Инструменты сборки
- Android Gradle Plugin: 8.2.2
- Kotlin Gradle Plugin: 1.9.0
- Gradle: 8.2.2

### Основные библиотеки
- AndroidX Core: 1.12.0
- AndroidX AppCompat: 1.6.1
- Material Design: 1.11.0
- ConstraintLayout: 2.1.4
- Navigation: 2.7.7
- SwipeRefreshLayout: 1.1.0
- Lifecycle: 2.7.0 (ViewModel, LiveData)

### Сетевые библиотеки
- Retrofit: 2.9.0
- Moshi: 1.14.0
- OkHttp: 4.12.0
- OkHttp Logging Interceptor: 4.12.0

### Дополнительные библиотеки
- Glide: 4.16.0
- Yandex Metrica: 5.3.0

## Логирование и аналитика

### Аналитика AppMetrica

В приложении используется Яндекс AppMetrica для отслеживания активности пользователей. API ключ: `83750483-ef87-4e81-a688-4b9c233cfb81`

### Отслеживаемые события

#### Загрузка контента
- Начало загрузки контента
- Успешная загрузка контента
- Ошибки загрузки контента
- Время загрузки контента

#### Просмотр контента
- Открытие статьи
- Просмотр времени на статье
- Прокрутка контента
- Закрытие статьи

#### Рекламные блоки
- Загрузка рекламных блоков
- Отображение рекламы
- Клики по рекламе
- Ошибки загрузки рекламы

#### Навигация
- Переключение между табами
- Открытие экранов
- Возврат на предыдущий экран

### Параметры событий
- `app_key` - идентификатор приложения
- `ad_id` - идентификатор рекламного блока
- `placement` - место размещения рекламы
- `content_id` - идентификатор контента
- `content_type` - тип контента
- `error_message` - сообщение об ошибке
- `duration` - длительность просмотра
- `scroll_depth` - глубина прокрутки

## Настройка и запуск проекта

### Подготовка окружения
1. Установите Android Studio Hedgehog (2023.1.1) или новее
2. Установите JDK 17
3. Установите Android SDK 34 (Android 14)
4. Убедитесь, что в Android Studio настроены:
   - Android SDK
   - Gradle 8.2.2
   - Kotlin 1.9.0

### Импорт и сборка проекта
1. Клонируйте репозиторий:
   ```bash
   git clone <repository-url>
   ```

2. Откройте проект в Android Studio:
   - File -> Open -> выберите папку с проектом
   - Дождитесь завершения индексации и синхронизации Gradle

3. Синхронизация зависимостей:
   - File -> Sync Project with Gradle Files
   - Или нажмите кнопку "Sync Now" в уведомлении Gradle

4. Сборка проекта:
   - Build -> Make Project (Ctrl+F9)
   - Или используйте командную строку:
     ```bash
     ./gradlew assembleDebug
     ```

### Запуск приложения
1. Подключите Android устройство или запустите эмулятор
2. В Android Studio:
   - Выберите устройство в выпадающем списке
   - Нажмите Run (Shift+F10)
3. Или используйте командную строку:
   ```bash
   ./gradlew installDebug
   ```

## Структура проекта

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/earnly/
│   │   │   ├── data/
│   │   │   │   ├── api/
│   │   │   │   ├── model/
│   │   │   │   └── repository/
│   │   │   ├── domain/
│   │   │   │   ├── analytics/
│   │   │   │   └── usecase/
│   │   │   └── presentation/
│   │   │       ├── article/
│   │   │       └── main/
│   │   └── res/
│   └── test/
└── build.gradle.kts
```

## Особенности

- Поддержка Android 9.0 (API 28) и выше
- Адаптивные иконки
- ViewBinding для работы с макетами
- Навигация через Navigation Component
- Аналитика через Yandex Metrica
- Кэширование изображений через Glide
- Pull-to-refresh функционал
- Полное логирование всех пользовательских действий
- Отслеживание производительности загрузки контента

## Тестирование

```bash
# Запуск всех тестов
./gradlew test

# Запуск тестов с отчетом о покрытии
./gradlew testDebugUnitTestCoverage
```

## Лицензия

MIT License 