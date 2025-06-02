package com.example.healthi.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.healthi.R
import java.text.SimpleDateFormat
import java.util.Locale

class ChatAdapter : ListAdapter<Message, ChatAdapter.VH>(Diff()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val layout = if (viewType == VIEW_TYPE_USER)
            R.layout.item_message_user else R.layout.item_message_assistant
        val view = LayoutInflater.from(parent.context)
            .inflate(layout, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(getItem(position))

    override fun getItemViewType(position: Int) =
        if (getItem(position).isFromUser) VIEW_TYPE_USER else VIEW_TYPE_ASSISTANT

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView =
            itemView.findViewById(R.id.messageTextView)
        private val timeText: TextView =
            itemView.findViewById(R.id.timeTextView)
        private val fmt = SimpleDateFormat("h:mm a", Locale.getDefault())

        fun bind(m: Message) {
            messageText.text = m.text
            timeText.text = fmt.format(m.timestamp)
        }
    }

    private class Diff : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(a: Message, b: Message) = a.id == b.id
        override fun areContentsTheSame(a: Message, b: Message) = a == b
    }

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_ASSISTANT = 2
    }
}
