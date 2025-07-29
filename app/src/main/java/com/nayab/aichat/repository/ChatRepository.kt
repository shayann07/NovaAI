package com.nayab.aichat.repository

import android.util.Log
import com.google.gson.Gson
import com.nayab.aichat.api.RetrofitClient
import com.nayab.aichat.model.ChatRequest
import com.nayab.aichat.model.ChatResponse
import com.nayab.aichat.model.MessageRequest
import retrofit2.Response

class ChatRepository {

    companion object {
        private const val DEFAULT_MODEL = "mistralai/Mistral-7B-Instruct-v0.2"
    }

    suspend fun getChatReply(messages: List<MessageRequest>): Response<ChatResponse> {
        val request = ChatRequest(
            model = DEFAULT_MODEL,          // <- use constant
            messages = messages
        )
        Log.d("ChatRequest", Gson().toJson(request))
        return RetrofitClient.api.getChatCompletion(request)
    }
}