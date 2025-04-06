package com.example.earnly.domain.analytics

import android.util.Log
import com.example.earnly.EarnlyApplication
import com.yandex.metrica.YandexMetrica

object AnalyticsManager {
    
    private const val TAG = "AnalyticsManager"
    
    fun logEvent(eventName: String, params: Map<String, String> = emptyMap()) {
        try {
            // Для тестирования будем выводить события в лог
            Log.d(TAG, "Event: $eventName, Params: $params")
            
            // Аналитика может не работать если ключ не задан
            if (EarnlyApplication.APPMETRICA_API_KEY.isNotEmpty()) {
                YandexMetrica.reportEvent(eventName, params)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error logging event: $eventName", e)
        }
    }
    
    fun logScreenView(screenName: String) {
        logEvent("screen_view", mapOf("screen_name" to screenName))
    }
    
    fun logArticleView(articleId: String, articleTitle: String) {
        logEvent("article_view", mapOf(
            "article_id" to articleId,
            "article_title" to articleTitle
        ))
    }
    
    fun logAdClick(adId: String, placement: String) {
        logEvent("ad_click", mapOf(
            "app_key" to EarnlyApplication.APP_KEY,
            "ad_id" to adId,
            "placement" to placement
        ))
    }
    
    fun logTabSwitch(tab: String) {
        logEvent("tab_switch", mapOf("tab" to tab))
    }
    
    fun logError(source: String, message: String) {
        logEvent("app_error", mapOf(
            "source" to source,
            "error_message" to message
        ))
    }
} 