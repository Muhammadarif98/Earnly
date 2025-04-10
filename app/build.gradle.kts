plugins {
    id("com.android.application") 
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize") // Плагин для автоматической реализации Parcelable
}

android {
    namespace = "com.example.earnly"
    compileSdk = 34 // Используем стабильную версию SDK

    defaultConfig {
        applicationId = "com.example.earnly"
        minSdk = 28 // Минимальная поддерживаемая версия Android 9 (Pie)
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true // Включаем минификацию для релизной версии
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            isDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true // Используем ViewBinding для безопасного доступа к элементам UI
    }
}

dependencies {
    // Основные компоненты AndroidX
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    
    // Библиотеки для работы с жизненным циклом
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    
    // Компоненты UI
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    
    // Сетевые библиотеки
    implementation("com.squareup.retrofit2:retrofit:2.9.0") // Для API запросов
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0") // Конвертер JSON
    implementation("com.squareup.okhttp3:okhttp:4.11.0") // HTTP клиент
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0") // Для логирования запросов
    
    // Библиотеки для работы с JSON
    implementation("com.squareup.moshi:moshi:1.14.0") // Парсер JSON
    implementation("com.squareup.moshi:moshi-kotlin:1.14.0") // Поддержка Kotlin для Moshi
    
    // Загрузка и кэширование изображений
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation("com.github.bumptech.glide:okhttp3-integration:4.15.1") // Интеграция с OkHttp
    
    // Аналитика
    implementation("com.yandex.android:mobmetricalib:5.2.0") // AppMetrica для отслеживания событий
    
    // Корутины для асинхронного кода
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
    
    // Библиотеки для тестирования
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}