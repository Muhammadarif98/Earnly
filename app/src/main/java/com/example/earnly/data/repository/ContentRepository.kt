package com.example.earnly.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.earnly.EarnlyApplication
import com.example.earnly.data.api.NetworkModule
import com.example.earnly.data.model.ApiRequest
import com.example.earnly.data.model.ApiResponse
import com.example.earnly.domain.analytics.AnalyticsManager
import com.example.earnly.domain.util.Resource
import java.util.UUID

class ContentRepository(private val context: Context) {
    private val TAG = "ContentRepository"
    
    // Generate or get a stored UUID for the user
    private val userId = getOrCreateUserId()
    
    private fun getOrCreateUserId(): String {
        val sharedPrefs = context.getSharedPreferences("earnly_prefs", Context.MODE_PRIVATE)
        var userId = sharedPrefs.getString("user_id", null)
        
        if (userId == null) {
            userId = UUID.randomUUID().toString()
            sharedPrefs.edit().putString("user_id", userId).apply()
        }
        
        return userId
    }
    
    suspend fun getContentForTab(tab: String): Resource<ApiResponse> {
        // Проверяем подключение к интернету
        if (!isNetworkAvailable()) {
            AnalyticsManager.logError("network", "Network not available for tab: $tab")
            return Resource.Error("Отсутствует подключение к интернету. Проверьте интернет-соединение и повторите попытку.")
        }
        
        // Если есть подключение, пытаемся загрузить данные
        return try {
            val startTime = System.currentTimeMillis()
            
            val request = ApiRequest(
                user = userId,
                key = EarnlyApplication.APP_KEY,
                tab = tab
            )
            
            android.util.Log.d(TAG, "=================== API REQUEST INFO ===================")
            android.util.Log.d(TAG, "Base URL: ${EarnlyApplication.BASE_URL}")
            android.util.Log.d(TAG, "App Key: ${EarnlyApplication.APP_KEY}")
            android.util.Log.d(TAG, "Sending API request for tab: $tab with user: $userId")
            android.util.Log.d(TAG, "Full request data: $request")
            android.util.Log.d(TAG, "=========================================================")
            
            // Логируем начало запроса
            AnalyticsManager.logEvent("api_request_start", mapOf(
                "tab" to tab,
                "timestamp" to System.currentTimeMillis().toString()
            ))
            
            val response = NetworkModule.apiService.getData(request)
            val responseTime = System.currentTimeMillis() - startTime
            
            // Логируем время ответа API
            AnalyticsManager.logEvent("api_response_time", mapOf(
                "tab" to tab,
                "response_time_ms" to responseTime.toString()
            ))
            
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    // Логируем успешную загрузку
                    val articleCount = apiResponse.recyclerItems.count { it.contentType == "article" }
                    val adCount = apiResponse.recyclerItems.count { it.contentType.contains("ad") || it.contentType == "banner" }
                    
                    android.util.Log.d(TAG, "=================== API RESPONSE INFO ===================")
                    android.util.Log.d(TAG, "API response success for tab: $tab, status: ${response.code()}")
                    android.util.Log.d(TAG, "Received: ${apiResponse.recyclerItems.size} items (Articles: $articleCount, Ads: $adCount)")
                    
                    // Добавляем детальное логирование типов контента
                    if (articleCount == 0) {
                        android.util.Log.w(TAG, "⚠️ NO ARTICLES received in API response for tab: $tab")
                    }
                    
                    // Подробная информация о полученных элементах
                    apiResponse.recyclerItems.forEachIndexed { index, item ->
                        android.util.Log.d(TAG, "Item $index: Type=${item.contentType}, Title=${item.title}, ImageUrl=${item.imageUrl}")
                    }
                    android.util.Log.d(TAG, "===========================================================")
                    
                    // Детальное логирование успешной загрузки
                    AnalyticsManager.logEvent("content_fetched_success", mapOf(
                        "tab" to tab, 
                        "items_count" to apiResponse.recyclerItems.size.toString(),
                        "articles_count" to articleCount.toString(),
                        "ads_count" to adCount.toString(),
                        "response_time_ms" to responseTime.toString()
                    ))
                    
                    Resource.Success(apiResponse)
                } ?: run {
                    // Логируем ошибку пустого тела ответа
                    AnalyticsManager.logError("api_response", "Empty response body for tab: $tab")
                    Resource.Error("Ошибка получения данных с сервера. Проверьте интернет-соединение и повторите попытку.")
                }
            } else {
                // Логируем ошибку API
                android.util.Log.e(TAG, "API error: ${response.code()} - ${response.message()}")
                AnalyticsManager.logError("api_response", "API error: ${response.code()} - ${response.message()} for tab: $tab")
                Resource.Error("Ошибка получения данных с сервера. Проверьте интернет-соединение и повторите попытку.")
            }
        } catch (e: Exception) {
            // Логируем ошибку
            android.util.Log.e(TAG, "Error fetching content: ${e.message}", e)
            AnalyticsManager.logError("api_exception", "Exception loading content for tab: $tab: ${e.message}")
            Resource.Error("Ошибка получения данных с сервера. Проверьте интернет-соединение и повторите попытку.")
        }
    }
    
    private fun isNetworkAvailable(): Boolean {
        try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            
            // Проверяем наличие активного соединения
            val network = connectivityManager.activeNetwork
            if (network == null) {
                android.util.Log.d(TAG, "Нет активной сети")
                return false
            }
            
            // Проверяем возможности сети
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            if (capabilities == null) {
                android.util.Log.d(TAG, "Сеть без возможностей")
                return false
            }
            
            // Проверяем наличие интернета и валидацию
            val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            val isValidated = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            
            android.util.Log.d(TAG, "Состояние сети: интернет=$hasInternet, валидация=$isValidated")
            
            return hasInternet && isValidated
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Ошибка при проверке сети", e)
            return false
        }
    }
    
    fun logContentView(articleId: String) {
        AnalyticsManager.logArticleView(articleId, "Unknown title")
    }
    
    fun logAdClick(bannerId: String, placement: String) {
        AnalyticsManager.logAdClick(bannerId, placement)
    }
} 