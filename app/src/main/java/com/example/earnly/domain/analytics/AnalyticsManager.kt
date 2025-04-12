package com.example.earnly.domain.analytics

import android.content.Context
import android.util.Log
import com.example.earnly.EarnlyApplication
import com.example.earnly.R
import com.yandex.metrica.YandexMetrica

object AnalyticsManager {
    
    private const val TAG = "AnalyticsManager"
    private lateinit var appContext: Context
    
    fun init(context: Context) {
        appContext = context.applicationContext
    }
    
    private fun getString(resId: Int): String {
        if (!::appContext.isInitialized) {
            return "" // Возвращаем пустую строку, если контекст не инициализирован
        }
        return appContext.getString(resId)
    }
    
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
        logEvent(getString(R.string.event_screen_view), mapOf(
            getString(R.string.param_screen_name) to screenName
        ))
    }
    
    fun logArticleView(articleId: String, articleTitle: String) {
        logEvent(getString(R.string.event_article_view), mapOf(
            getString(R.string.param_article_id) to articleId,
            getString(R.string.param_article_title) to articleTitle
        ))
    }
    
    fun logAdClick(adId: String, placement: String) {
        logEvent(getString(R.string.event_ad_click), mapOf(
            getString(R.string.param_app_key) to EarnlyApplication.APP_KEY,
            getString(R.string.param_ad_id) to adId,
            getString(R.string.param_placement) to placement
        ))
    }
    
    fun logTabSwitch(tab: String) {
        logEvent(getString(R.string.event_tab_switch), mapOf(
            getString(R.string.param_tab) to tab
        ))
    }
    
    fun logError(source: String, message: String) {
        logEvent(getString(R.string.event_app_error), mapOf(
            getString(R.string.param_source) to source,
            getString(R.string.param_error_message) to message
        ))
    }
    
    // Логирование начала загрузки контента
    fun logContentLoadStart(tab: String) {
        logEvent(getString(R.string.event_content_load_start), mapOf(
            getString(R.string.param_tab) to tab,
            getString(R.string.param_timestamp) to System.currentTimeMillis().toString()
        ))
    }
    
    // Логирование успешной загрузки контента
    fun logContentLoadSuccess(tab: String, itemCount: Int, timeMs: Long) {
        logEvent(getString(R.string.event_content_load_success), mapOf(
            getString(R.string.param_tab) to tab,
            getString(R.string.param_item_count) to itemCount.toString(),
            getString(R.string.param_load_time_ms) to timeMs.toString(),
            getString(R.string.param_timestamp) to System.currentTimeMillis().toString()
        ))
    }
    
    // Логирование загрузки рекламы
    fun logAdLoadStart(placement: String) {
        logEvent(getString(R.string.event_ad_load_start), mapOf(
            getString(R.string.param_app_key) to EarnlyApplication.APP_KEY,
            getString(R.string.param_placement) to placement,
            getString(R.string.param_timestamp) to System.currentTimeMillis().toString()
        ))
    }
    
    // Логирование успешной загрузки рекламы
    fun logAdLoadSuccess(adId: String, placement: String, timeMs: Long) {
        logEvent(getString(R.string.event_ad_load_success), mapOf(
            getString(R.string.param_app_key) to EarnlyApplication.APP_KEY,
            getString(R.string.param_ad_id) to adId,
            getString(R.string.param_placement) to placement,
            getString(R.string.param_load_time_ms) to timeMs.toString(),
            getString(R.string.param_timestamp) to System.currentTimeMillis().toString()
        ))
    }
    
    // Логирование отображения рекламы
    fun logAdImpression(adId: String, placement: String) {
        logEvent(getString(R.string.event_ad_impression), mapOf(
            getString(R.string.param_app_key) to EarnlyApplication.APP_KEY,
            getString(R.string.param_ad_id) to adId,
            getString(R.string.param_placement) to placement,
            getString(R.string.param_timestamp) to System.currentTimeMillis().toString()
        ))
    }
    
    // Логирование времени просмотра статьи
    fun logArticleViewDuration(articleId: String, durationSec: Int) {
        logEvent(getString(R.string.event_article_view_duration), mapOf(
            getString(R.string.param_article_id) to articleId,
            getString(R.string.param_duration_sec) to durationSec.toString(),
            getString(R.string.param_timestamp) to System.currentTimeMillis().toString()
        ))
    }
    
    // Логирование прокрутки контента
    fun logScrollDepth(screenName: String, depth: Int) {
        logEvent(getString(R.string.event_scroll_depth), mapOf(
            getString(R.string.param_screen_name) to screenName,
            getString(R.string.param_depth_percent) to depth.toString(),
            getString(R.string.param_timestamp) to System.currentTimeMillis().toString()
        ))
    }
    
    // Логирование закрытия статьи
    fun logArticleClose(articleId: String, timeSpentSec: Int) {
        logEvent(getString(R.string.event_article_close), mapOf(
            getString(R.string.param_article_id) to articleId,
            getString(R.string.param_time_spent_sec) to timeSpentSec.toString(),
            getString(R.string.param_timestamp) to System.currentTimeMillis().toString()
        ))
    }
} 