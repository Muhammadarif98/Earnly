package com.example.earnly.domain.usecase

import com.example.earnly.data.model.ApiResponse
import com.example.earnly.data.repository.ContentRepository
import com.example.earnly.domain.util.Resource

class GetContentUseCase(private val repository: ContentRepository) {
    suspend operator fun invoke(tab: String): Resource<ApiResponse> {
        return repository.getContentForTab(tab)
    }
} 