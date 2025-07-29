package com.nayab.aichat.api

import com.nayab.aichat.model.ChatRequest
import com.nayab.aichat.model.ChatResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    // ✅  Correct OpenRouter path keeps the /api/ prefix
    @POST("v1/chat/completions")
    suspend fun getChatCompletion(
        @Body request: ChatRequest
    ): Response<ChatResponse>
}