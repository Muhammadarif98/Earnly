// Корневой файл сборки проекта
plugins {
    id("com.android.application") version "8.2.2" apply false // Версия Android Gradle Plugin
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false // Версия Kotlin плагина
}

buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.2.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.21")
        classpath("com.google.gms:google-services:4.4.1")
    }
}

// Добавляем задачу для очистки проекта
tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}