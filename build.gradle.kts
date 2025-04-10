// Корневой файл сборки проекта
plugins {
    id("com.android.application") version "8.1.4" apply false // Версия Android Gradle Plugin
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false // Версия Kotlin плагина
}

// Добавляем задачу для очистки проекта
tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}