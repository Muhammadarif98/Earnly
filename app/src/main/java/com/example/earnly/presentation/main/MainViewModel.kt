package com.example.earnly.presentation.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.earnly.data.model.ApiResponse
import com.example.earnly.data.model.ContentItem
import com.example.earnly.data.repository.ContentRepository
import com.example.earnly.domain.analytics.AnalyticsManager
import com.example.earnly.domain.util.Resource
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = ContentRepository(application.applicationContext)
    
    private val _contentState = MutableLiveData<ContentState>()
    val contentState: LiveData<ContentState> = _contentState
    
    private val _selectedContent = MutableLiveData<ContentItem?>()
    val selectedContent: LiveData<ContentItem?> = _selectedContent
    
    // Используем корректные значения вкладок из ТЗ - ДОЛЖНО СОВПАДАТЬ с MainActivity
    private val tabs = listOf("surveys","main")
    private var currentTab = tabs[0]
    
    // Кеширование результатов по вкладкам
    private val cachedResults = mutableMapOf<String, ApiResponse>()
    
    init {
        loadContent(currentTab)
    }
    
    fun loadContent(tab: String) {
        android.util.Log.d("MainViewModel", "Загрузка контента для вкладки: $tab (текущая: $currentTab)")
        
        if (tab != currentTab) {
            currentTab = tab
            AnalyticsManager.logTabSwitch(tab)
            android.util.Log.d("MainViewModel", "Вкладка изменена на: $currentTab")
        }
        
        // Проверяем, есть ли кешированный результат для этой вкладки
        if (cachedResults.containsKey(tab)) {
            android.util.Log.d("MainViewModel", "Используем кешированные данные для вкладки: $tab")
            val cachedResponse = cachedResults[tab]
            _contentState.value = ContentState.Success(cachedResponse!!)
            return
        }
        
        _contentState.value = ContentState.Loading
        
        // Логируем начало загрузки контента
        AnalyticsManager.logContentLoadStart(tab)
        val startTime = System.currentTimeMillis()

        viewModelScope.launch {
            android.util.Log.d("MainViewModel", "Запрос данных в репозиторий для вкладки: $tab")
            when (val result = repository.getContentForTab(tab)) {
                is Resource.Success -> {
                    val loadTime = System.currentTimeMillis() - startTime
                    
                    // Анализируем результат
                    val items = result.data?.recyclerItems ?: emptyList()
                    val articleCount = items.count { it.contentType == "article" }
                    val adCount = items.count { it.isAd() }
                    
                    android.util.Log.d("MainViewModel", "Получены данные: всего ${items.size} элементов, статей: $articleCount, рекламы: $adCount")
                    
                    // Логируем успешную загрузку
                    AnalyticsManager.logContentLoadSuccess(tab, items.size, loadTime)
                    
                    // Для каждого рекламного блока логируем отображение
                    items.filter { it.isAd() }.forEach { adItem ->
                        adItem.bannerId?.let { bannerId ->
                            val placement = when {
                                adItem.contentType == "inline_ad" -> "inline"
                                adItem.contentType == "banner" -> "banner"
                                else -> "item_ad"
                            }
                            AnalyticsManager.logAdImpression(bannerId, placement)
                        }
                    }
                    
                    // Кешируем результат
                    result.data?.let { cachedResults[tab] = it }
                    
                    _contentState.value = result.data?.let { ContentState.Success(it) } 
                        ?: ContentState.Error("Empty response")
                }
                is Resource.Error -> {
                    android.util.Log.e("MainViewModel", "Ошибка загрузки: ${result.message}")
                    
                    // Логируем ошибку
                    AnalyticsManager.logError("content_loading", "Failed to load content for tab: $tab, ${result.message}")
                    
                    _contentState.value = ContentState.Error(result.message ?: "Unknown error")
                }
                is Resource.Loading -> {
                    _contentState.value = ContentState.Loading
                }
            }
        }
    }
    
    // Принудительная загрузка свежих данных (например, при свайпе для обновления)
    fun refreshContent() {
        android.util.Log.d("MainViewModel", "Принудительное обновление контента для вкладки: $currentTab")
        
        // Логируем событие обновления
        AnalyticsManager.logEvent("content_refresh", mapOf("tab" to currentTab))
        
        // Очищаем кеш для текущей вкладки
        cachedResults.remove(currentTab)
        
        // Загружаем свежие данные
        loadContent(currentTab)
    }
    
    fun onContentItemClicked(item: ContentItem) {
        if (item.isArticle()) {
            // Log article click
            item.articleId?.let { articleId ->
                repository.logContentView(articleId)
                _selectedContent.value = item
            }
        } else if (item.isAd()) {
            // Log ad click
            item.bannerId?.let { bannerId ->
                val placement = when {
                    item.contentType == "inline_ad" -> "inline"
                    item.contentType == "banner" -> "banner"
                    else -> "item_ad"
                }
                repository.logAdClick(bannerId, placement)
            }
        }
    }
    
    fun clearSelectedContent() {
        _selectedContent.value = null
    }
    
    sealed class ContentState {
        object Loading : ContentState()
        data class Success(val data: ApiResponse) : ContentState()
        data class Error(val message: String) : ContentState()
    }
} 