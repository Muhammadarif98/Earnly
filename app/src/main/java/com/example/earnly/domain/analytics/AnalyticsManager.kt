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
    
    // Логирование начала загрузки контента
    fun logContentLoadStart(tab: String) {
        logEvent("content_load_start", mapOf(
            "tab" to tab,
            "timestamp" to System.currentTimeMillis().toString()
        ))
    }
    
    // Логирование успешной загрузки контента
    fun logContentLoadSuccess(tab: String, itemCount: Int, timeMs: Long) {
        logEvent("content_load_success", mapOf(
            "tab" to tab,
            "item_count" to itemCount.toString(),
            "load_time_ms" to timeMs.toString(),
            "timestamp" to System.currentTimeMillis().toString()
        ))
    }
    
    // Логирование загрузки рекламы
    fun logAdLoadStart(placement: String) {
        logEvent("ad_load_start", mapOf(
            "app_key" to EarnlyApplication.APP_KEY,
            "placement" to placement,
            "timestamp" to System.currentTimeMillis().toString()
        ))
    }
    
    // Логирование успешной загрузки рекламы
    fun logAdLoadSuccess(adId: String, placement: String, timeMs: Long) {
        logEvent("ad_load_success", mapOf(
            "app_key" to EarnlyApplication.APP_KEY,
            "ad_id" to adId,
            "placement" to placement,
            "load_time_ms" to timeMs.toString(),
            "timestamp" to System.currentTimeMillis().toString()
        ))
    }
    
    // Логирование отображения рекламы
    fun logAdImpression(adId: String, placement: String) {
        logEvent("ad_impression", mapOf(
            "app_key" to EarnlyApplication.APP_KEY,
            "ad_id" to adId,
            "placement" to placement,
            "timestamp" to System.currentTimeMillis().toString()
        ))
    }
    
    // Логирование времени просмотра статьи
    fun logArticleViewDuration(articleId: String, durationSec: Int) {
        logEvent("article_view_duration", mapOf(
            "article_id" to articleId,
            "duration_sec" to durationSec.toString(),
            "timestamp" to System.currentTimeMillis().toString()
        ))
    }
    
    // Логирование прокрутки контента
    fun logScrollDepth(screenName: String, depth: Int) {
        logEvent("scroll_depth", mapOf(
            "screen_name" to screenName,
            "depth_percent" to depth.toString(),
            "timestamp" to System.currentTimeMillis().toString()
        ))
    }
    
    // Логирование закрытия статьи
    fun logArticleClose(articleId: String, timeSpentSec: Int) {
        logEvent("article_close", mapOf(
            "article_id" to articleId,
            "time_spent_sec" to timeSpentSec.toString(),
            "timestamp" to System.currentTimeMillis().toString()
        ))
    }
} 