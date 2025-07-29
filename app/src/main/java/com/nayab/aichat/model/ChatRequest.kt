package com.nayab.aichat.model

data class ChatRequest(
    val model: String,
    val messages: List<MessageRequest>
)

data class MessageRequest(
    val role: String,
    val content: String
)
