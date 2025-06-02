package com.example.healthi

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthi.chat.ChatAdapter
import com.example.healthi.chat.Message
import java.util.Date

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rv        = findViewById<RecyclerView>(R.id.chatRecyclerView)
        val et        = findViewById<EditText>(R.id.messageEditText)
        val sendBt    = findViewById<ImageButton>(R.id.sendButton)

        val messages = mutableListOf<Message>()
        val adapter  = ChatAdapter().also {
            rv.layoutManager = LinearLayoutManager(this)
            rv.adapter       = it
        }

        // initial greeting
        messages.add(Message(1L, "Welcome to Health Assistant!", false, Date()))
        adapter.submitList(ArrayList(messages))

        sendBt.setOnClickListener {
            val text = et.text.toString().trim()
            if (text.isNotEmpty()) {
                messages.add(Message(messages.size + 1L, text, true, Date()))
                messages.add(Message(
                    messages.size + 1L,
                    "API limit exceeded. Please try again later.",
                    false,
                    Date()
                ))
                adapter.submitList(ArrayList(messages))
                rv.scrollToPosition(messages.size - 1)
                et.text.clear()
                Toast.makeText(this, "API limit exceeded", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
