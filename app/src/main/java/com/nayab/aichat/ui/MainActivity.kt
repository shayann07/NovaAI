package com.nayab.aichat.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import com.nayab.aichat.adapter.ChatAdapter
import com.nayab.aichat.databinding.ActivityMainBinding
import com.nayab.aichat.model.Message

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val messageList = mutableListOf<Message>()
    private lateinit var chatAdapter: ChatAdapter
    private val viewModel: ChatViewModel by viewModels()
    private var isFirstMessageSent = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeMessages()
        setupInputLogic()

        // ✅ Show keyboard only on first launch
        if (!isFirstMessageSent) {
            binding.messageEditText.requestFocus()
            showKeyboard()
        }
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(messageList)
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.adapter = chatAdapter
        binding.chatRecyclerView.visibility = View.VISIBLE
    }

    private fun observeMessages() {
        viewModel.messages.observe(this) { updatedList ->
            messageList.clear()
            messageList.addAll(updatedList)
            chatAdapter.notifyDataSetChanged()

            // ✅ Smooth scroll to last message after layout updates
            binding.chatRecyclerView.post {
                binding.chatRecyclerView.scrollToPosition(messageList.size - 1)
            }
        }
    }


    private fun setupInputLogic() {
        binding.sendButton.visibility = View.GONE

        binding.messageEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val hasText = !s.isNullOrBlank()
                binding.sendButton.visibility = if (hasText) View.VISIBLE else View.GONE
                binding.filterIcon.visibility = if (hasText) View.GONE else View.VISIBLE
                binding.micIcon.visibility = if (hasText) View.GONE else View.VISIBLE
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.sendButton.setOnClickListener {
            val message = binding.messageEditText.text.toString().trim()
            if (message.isNotEmpty()) {
                viewModel.sendMessage(message)
                binding.messageEditText.setText("")

                // ✅ Hide keyboard after first message
                if (!isFirstMessageSent) {
                    isFirstMessageSent = true
                    hideKeyboard()
                    binding.messageEditText.clearFocus()
                }
            }
        }
    }

    private fun showKeyboard() {
        val imm = getSystemService<InputMethodManager>()
        imm?.showSoftInput(binding.messageEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        val imm = getSystemService<InputMethodManager>()
        imm?.hideSoftInputFromWindow(binding.messageEditText.windowToken, 0)
    }
}