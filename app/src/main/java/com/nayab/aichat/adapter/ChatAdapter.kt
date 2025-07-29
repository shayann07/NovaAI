package com.nayab.aichat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nayab.aichat.R
import com.nayab.aichat.model.Message

class ChatAdapter(private val messages: MutableList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_BOT = 2
    }

    override fun getItemViewType(position: Int): Int =
        if (messages[position].role == "user") VIEW_TYPE_USER else VIEW_TYPE_BOT

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        if (viewType == VIEW_TYPE_USER) {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_user, parent, false)
                .let(::UserViewHolder)
        } else {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_bot, parent, false)
                .let(::BotViewHolder)
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = messages[position]
        if (holder is UserViewHolder) holder.bind(msg) else (holder as BotViewHolder).bind(msg)
    }

    override fun getItemCount(): Int = messages.size

    fun addMessage(message: Message) {
        messages.add(message)
        notifyItemInserted(messages.lastIndex)
    }

    // ────────────── ViewHolders ──────────────

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.messageText)
        fun bind(message: Message) {
            messageText.text = message.content
        }
    }

    class BotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val inputContainer: View = itemView.findViewById(R.id.input)
        private val loadingContainer: View = itemView.findViewById(R.id.loading)
        private val messageText: TextView = itemView.findViewById(R.id.messageText)

        fun bind(message: Message) {
            if (message.isTyping) {
                // assistant is “thinking”
                loadingContainer.visibility = View.VISIBLE
                inputContainer.visibility = View.GONE
            } else {
                loadingContainer.visibility = View.GONE
                inputContainer.visibility = View.VISIBLE
                messageText.text = message.content
            }
        }
    }
}