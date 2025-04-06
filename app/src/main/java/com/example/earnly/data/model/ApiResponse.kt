package com.example.earnly.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class ApiResponse(
    @Json(name = "ad_top_item")
    val topAdItems: List<ContentItem> = emptyList(),
    
    @Json(name = "ad_down_item")
    val bottomAdItems: List<ContentItem> = emptyList(),
    
    @Json(name = "recycler_item")
    val recyclerItems: List<ContentItem> = emptyList()
)

// API request model
@JsonClass(generateAdapter = true)
data class ApiRequest(
    @Json(name = "user")
    val user: String,
    
    @Json(name = "key")
    val key: String,
    
    @Json(name = "tab")
    val tab: String
) 