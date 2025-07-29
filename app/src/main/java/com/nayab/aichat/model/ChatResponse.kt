package com.nayab.aichat.model

data class ChatResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: MessageRequest
)
