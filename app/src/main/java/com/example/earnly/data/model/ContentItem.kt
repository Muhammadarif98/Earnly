package com.example.earnly.data.model

import com.example.earnly.EarnlyApplication
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

/**
 * Модель данных для контента, получаемого с сервера.
 * Может представлять как статьи, так и рекламные блоки.
 */
@JsonClass(generateAdapter = true)
data class ContentItem(
    // Основное поле, определяющее тип контента
    @Json(name = "contentType")
    val contentType: String, // "article", "banner", "inline_ad", "item_ad"

    // Заголовок контента
    @Json(name = "heading")
    val title: String? = null,

    // Детальное описание или содержание статьи
    @Json(name = "details")
    val description: String? = null,

    // Путь к изображению (относительный, требует добавления BASE_URL)
    @Json(name = "imgPath")
    val imageUrl: String? = null,

    // Поля для статей
    @Json(name = "recordId")
    val articleId: String? = null,

    // Флаги для показа рекламы в статье
    @Json(name = "showTopAd")
    val showTopAd: String? = null,

    @Json(name = "showBottomAd")
    val showBottomAd: String? = null,

    // Поля для рекламы
    @Json(name = "bannerId")
    val bannerId: String? = null,

    // Флаг показа метки "Реклама"
    @Json(name = "showAdLabel")
    val showAdLabel: String? = null,

    // Текст кнопки действия в рекламе
    @Json(name = "buttonCaption")
    val buttonCaption: String? = null,

    // Информация о рекламодателе
    @Json(name = "orgInfo")
    val orgInfo: String? = null
) : Serializable {
    // Проверяет, является ли контент статьей
    fun isArticle() = contentType == "article"

    // Проверяет, является ли контент рекламой любого типа
    fun isAd(): Boolean {
        val result = contentType.contains("ad", ignoreCase = true) || contentType == "banner"
        android.util.Log.d("ContentItem", "isAd check for type '$contentType': $result")
        return result
    }

    // Вспомогательные методы для проверки флагов
    fun shouldShowAdLabel() = showAdLabel == "1"
    fun shouldShowTopAd() = showTopAd == "1"
    fun shouldShowBottomAd() = showBottomAd == "1"

    /**
     * Формирует полный URL для изображения, добавляя BASE_URL
     * @return Полный URL изображения или null, если путь отсутствует
     */
    fun getFullImageUrl(): String? {
        return if (!imageUrl.isNullOrEmpty()) {
            val baseUrl = EarnlyApplication.BASE_URL.trimEnd('/')
            val fullUrl = "$baseUrl/$imageUrl"
            android.util.Log.d("ContentItem", "Generated image URL: $fullUrl")
            fullUrl
        } else {
            android.util.Log.w("ContentItem", "No image URL for item: $title")
            null
        }
    }
}