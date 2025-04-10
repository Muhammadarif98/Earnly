# Earnly

Приложение для чтения статей с интегрированными рекламными блоками.

## Требования

- Android Studio Giraffe (2022.3.1) или новее
- JDK 8 или новее
- Android SDK API Level 28+
- Gradle 8.2

## Как запустить проект

1. Клонируйте репозиторий:
```
git clone <repository-url>
```

2. Откройте проект в Android Studio:
   - Выберите "Open an existing Android Studio project"
   - Укажите путь к папке с клонированным проектом
   - Нажмите "OK"

3. Дождитесь завершения инициализации Gradle (автоматическая синхронизация)
   - В случае ошибок синхронизации, нажмите "Sync Project with Gradle Files" в верхней панели

4. Для запуска проекта:
   - Выберите устройство (эмулятор или подключенное физическое устройство)
   - Нажмите "Run" (зеленый треугольник) или используйте комбинацию Shift+F10
   - Дождитесь установки и запуска приложения

## Сборка APK

Для сборки APK-файла:

1. В Android Studio выберите меню "Build" -> "Build Bundle(s) / APK(s)" -> "Build APK(s)"
2. После завершения сборки нажмите на ссылку "locate" для открытия папки с APK
3. APK-файл будет находиться в директории `app/build/outputs/apk/debug/app-debug.apk`

Альтернативно, можно использовать командную строку:
```
./gradlew assembleDebug
```

## Особенности проекта

- Использует Kotlin в качестве основного языка программирования
- Retrofit для работы с API (POST запросы)
- AppMetrica для аналитики (логирование просмотров контента, кликов по рекламе)
- Material Design компоненты для современного пользовательского интерфейса
- Поддержка просмотра статей и различных типов рекламных блоков

## Структура проекта

- `app/src/main/java/com/example/earnly/data`: Классы для работы с данными и API
- `app/src/main/java/com/example/earnly/domain`: Доменный слой (аналитика, утилиты)
- `app/src/main/java/com/example/earnly/presentation`: Экраны приложения (активности, адаптеры)
- `app/src/main/res`: Ресурсы приложения (макеты, стили, изображения)

## Зависимости и их версии

### Инструменты сборки
- Gradle: 8.2
- Android Gradle Plugin: 8.1.4
- Kotlin: 1.9.22

### AndroidX
- Core KTX: 1.12.0
- AppCompat: 1.6.1
- Material: 1.11.0
- Activity KTX: 1.8.2
- ConstraintLayout: 2.1.4
- Lifecycle (ViewModel, LiveData, Runtime): 2.6.2
- ViewPager2: 1.0.0
- Fragment KTX: 1.6.2
- SwipeRefreshLayout: 1.1.0

### Сетевое взаимодействие
- Retrofit: 2.9.0
- Retrofit Moshi Converter: 2.9.0
- OkHttp: 4.11.0
- OkHttp Logging Interceptor: 4.11.0

### Работа с JSON
- Moshi: 1.14.0
- Moshi Kotlin: 1.14.0

### Загрузка изображений
- Glide: 4.15.1
- Glide OkHttp Integration: 4.15.1

### Аналитика
- AppMetrica: 5.2.0

### Асинхронное программирование
- Coroutines Android: 1.7.1

### Тестирование
- JUnit: 4.13.2
- AndroidX Test JUnit: 1.1.5
- Espresso Core: 3.5.1

## Лицензия

Проект распространяется под лицензией MIT License. 