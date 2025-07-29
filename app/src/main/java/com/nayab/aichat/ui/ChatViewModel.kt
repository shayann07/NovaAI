package com.nayab.aichat.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nayab.aichat.model.Message
import com.nayab.aichat.model.MessageRequest
import com.nayab.aichat.repository.ChatRepository
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val repository = ChatRepository()

    private val _messages = MutableLiveData<List<Message>>(mutableListOf())
    val messages: LiveData<List<Message>> = _messages

    // For animated typing dots
    private var dotHandler: Handler? = null
    private var dotRunnable: Runnable? = null
    private var dotIndex = 0
    private val dotStates = listOf(".", "..", "...")

    fun sendMessage(userMsg: String) {
        val currentList = _messages.value?.toMutableList() ?: mutableListOf()

        // Step 1: Add user message
        currentList.add(Message(role = "user", content = userMsg))
        _messages.value = currentList

        viewModelScope.launch {
            try {
                // Step 2: Add animated typing indicator (start with ".")
                val typingIndicator = Message(role = "assistant", content = ".", isTyping = true)
                currentList.add(typingIndicator)
                _messages.value = currentList

                // Start animation
                startTypingDotsAnimation()

                // Step 3: Prepare messages for API (skip typing indicator)
                val apiMessages = currentList
                    .filter { !it.isTyping }
                    .map { MessageRequest(it.role, it.content) }

                val response = repository.getChatReply(apiMessages)

                // Step 4: Remove typing indicator and stop animation
                stopTypingDotsAnimation()
                currentList.removeLastOrNull()

                // Step 5: Handle API response
                if (response.isSuccessful && response.body()?.choices?.isNotEmpty() == true) {
                    val reply = response.body()!!
                        .choices.first().message.content.trim()
                    currentList.add(Message(role = "assistant", content = reply))
                } else {
                    val errorText = response.errorBody()?.string()?.trim() ?: "Unknown error"
                    currentList.add(
                        Message(role = "assistant", content = "Error: $errorText")
                    )
                }

                _messages.value = currentList

            } catch (e: Exception) {
                stopTypingDotsAnimation()
                currentList.removeLastOrNull()
                currentList.add(
                    Message(
                        role = "assistant",
                        content = "Exception: ${e.localizedMessage ?: "Unknown error"}"
                    )
                )
                _messages.value = currentList
            }
        }
    }

    private fun startTypingDotsAnimation() {
        dotHandler = Handler(Looper.getMainLooper())
        dotRunnable = object : Runnable {
            override fun run() {
                val currentList = _messages.value?.toMutableList() ?: return
                val lastIndex = currentList.lastIndex
                if (lastIndex >= 0 && currentList[lastIndex].isTyping) {
                    val updated = currentList[lastIndex].copy(content = dotStates[dotIndex])
                    currentList[lastIndex] = updated
                    _messages.value = currentList
                    dotIndex = (dotIndex + 1) % dotStates.size
                    dotHandler?.postDelayed(this, 500)
                }
            }
        }
        dotHandler?.post(dotRunnable!!)
    }

    private fun stopTypingDotsAnimation() {
        dotHandler?.removeCallbacks(dotRunnable!!)
        dotHandler = null
        dotRunnable = null
        dotIndex = 0
    }
}
