package com.example.earnly

import android.app.Application
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig

class EarnlyApplication : Application() {

    companion object {
        const val BASE_URL = "https://dohodinfor.ru/apiv2test/"
            const val APP_KEY = "com.myapp1"
        // Оставляем пустой ключ для тестирования - при релизе заменить на реальный
        const val APPMETRICA_API_KEY = ""
    }

    override fun onCreate() {
        super.onCreate()
        
        if (APPMETRICA_API_KEY.isNotEmpty()) {
            // Initialize AppMetrica only if key is provided
            val config = YandexMetricaConfig.newConfigBuilder(APPMETRICA_API_KEY)
                .withLogs()
                .build()
            YandexMetrica.activate(applicationContext, config)
            YandexMetrica.enableActivityAutoTracking(this)
        }
    }
} 