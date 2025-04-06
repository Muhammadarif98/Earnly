    package com.example.earnly.data.model

    import com.example.earnly.EarnlyApplication
    import com.squareup.moshi.Json
    import com.squareup.moshi.JsonClass
    import java.io.Serializable

    @JsonClass(generateAdapter = true)
    data class ContentItem(
        // Common fields for all content types
        @Json(name = "contentType")
        val contentType: String, // "article", "banner", "inline_ad", "item_ad"

        @Json(name = "heading")
        val title: String? = null,

        @Json(name = "details")
        val description: String? = null,

        @Json(name = "imgPath")
        val imageUrl: String? = null,

        // Fields for articles
        @Json(name = "recordId")
        val articleId: String? = null,

        @Json(name = "showTopAd")
        val showTopAd: String? = null,

        @Json(name = "showBottomAd")
        val showBottomAd: String? = null,

        // Fields for ads
        @Json(name = "bannerId")
        val bannerId: String? = null,

        @Json(name = "showAdLabel")
        val showAdLabel: String? = null,

        @Json(name = "buttonCaption")
        val buttonCaption: String? = null,

        @Json(name = "orgInfo")
        val orgInfo: String? = null
    ) : Serializable {
        // Helper functions
        fun isArticle() = contentType == "article"

        fun isAd(): Boolean {
            val result = contentType.contains("ad", ignoreCase = true) || contentType == "banner"
            android.util.Log.d("ContentItem", "isAd check for type '$contentType': $result")
            return result
        }

        fun shouldShowAdLabel() = showAdLabel == "1"
        fun shouldShowTopAd() = showTopAd == "1"
        fun shouldShowBottomAd() = showBottomAd == "1"

        // Get full image URL
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