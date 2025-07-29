package com.nayab.aichat.model

data class Message(
    val role: String,
    val content: String,
    val isTyping: Boolean = false
)
