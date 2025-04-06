package com.example.earnly.data.api

import com.example.earnly.data.model.ApiRequest
import com.example.earnly.data.model.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("getdata.php")
    suspend fun getData(@Body request: ApiRequest): Response<ApiResponse>
} 