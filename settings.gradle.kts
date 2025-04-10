pluginManagement {
    repositories {
        google() // Репозиторий Google для плагинов Android
        mavenCentral() // Центральный репозиторий Maven
        gradlePluginPortal() // Портал плагинов Gradle
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS) // Запрещаем объявление репозиториев в проектах
    repositories {
        google() // Репозиторий Google для Android библиотек
        mavenCentral() // Основной репозиторий Maven
        maven { url = uri("https://maven.google.com") } // Дополнительный репозиторий Google
        maven { url = uri("https://jitpack.io") } // JitPack для GitHub библиотек
    }
}

rootProject.name = "Earnly" // Имя корневого проекта
include(":app") // Подключаем модуль приложения
