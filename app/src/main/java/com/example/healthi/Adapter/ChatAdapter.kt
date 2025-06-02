package com.example.healthi.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.healthchatbot.R
import com.example.healthchatbot.models.Message


class ChatAdapter : RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {
    private val messages: MutableList<Message> = ArrayList<Message>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_message_item, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message: Message = messages[position]
        holder.messageText.setText(message.getMessageText())

        // Align messages based on sender
        if (message.isUser()) {
            holder.messageText.setBackgroundResource(R.drawable.message_bubble_user)
            holder.itemView.setPadding(100, 8, 8, 8)
        } else {
            holder.messageText.setBackgroundResource(R.drawable.message_bubble)
            holder.itemView.setPadding(8, 8, 100, 8)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    fun addMessage(message: Message) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    internal class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var messageText: TextView

        init {
            messageText = itemView.findViewById<TextView>(R.id.messageText)
        }
    }
}