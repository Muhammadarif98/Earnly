package com.example.earnly

import android.app.Application
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig

/**
 * Главный класс приложения, инициализирует необходимые компоненты
 * и содержит глобальные константы
 */
class EarnlyApplication : Application() {

    companion object {
        // Базовый URL для API запросов
        const val BASE_URL = "https://dohodinfor.ru/apiv2test/"
        
        // Ключ приложения для API
        const val APP_KEY = "com.myapp1"
        
        // Ключ AppMetrica для аналитики
        const val APPMETRICA_API_KEY = "83750483-ef87-4e81-a688-4b9c233cfb81"
    }

    override fun onCreate() {
        super.onCreate()
        
        // Инициализируем AppMetrica только если задан ключ
        if (APPMETRICA_API_KEY.isNotEmpty()) {
            // Настраиваем и активируем аналитику
            val config = YandexMetricaConfig.newConfigBuilder(APPMETRICA_API_KEY)
                .withLogs() // Включаем логи для отладки
                .build()
            
            // Активируем сервис аналитики
            YandexMetrica.activate(applicationContext, config)
            
            // Включаем автоматическое отслеживание активностей
            YandexMetrica.enableActivityAutoTracking(this)
        }
    }
} 