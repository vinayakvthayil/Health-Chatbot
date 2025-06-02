package com.example.healthi.Activity

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mentalhealthchatbot.R
import com.example.mentalhealthchatbot.adapters.ChatAdapter
import com.example.mentalhealthchatbot.database.DatabaseHelper
import com.example.mentalhealthchatbot.models.Message
import com.example.mentalhealthchatbot.utils.GeminiManager

import com.example.mentalhealthchatbot.R
import com.example.mentalhealthchatbot.adapters.ChatAdapter
import com.example.mentalhealthchatbot.models.Message
import com.example.mentalhealthchatbot.database.DatabaseHelper
import com.example.mentalhealthchatbot.utils.GeminiManager

class ChatActivity : AppCompatActivity() {
    private var chatRecyclerView: RecyclerView? = null
    private var messageInput: EditText? = null
    private var sendButton: ImageButton? = null
    private var chatAdapter: ChatAdapter? = null
    private var databaseHelper: DatabaseHelper? = null
    private var geminiManager: GeminiManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        initializeViews()
        setupDatabase()
        setupRecyclerView()
        setupGeminiManager()
        setupSendButton()
    }

    private fun initializeViews() {
        chatRecyclerView = findViewById<RecyclerView>(R.id.chatRecyclerView)
        messageInput = findViewById<EditText>(R.id.messageInput)
        sendButton = findViewById<ImageButton>(R.id.sendButton)
    }

    private fun setupDatabase() {
        databaseHelper = DatabaseHelper(this)
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter()
        chatRecyclerView!!.layoutManager = LinearLayoutManager(this)
        chatRecyclerView!!.adapter = chatAdapter
    }

    private fun setupGeminiManager() {
        // Pass the context to GeminiManager
        geminiManager = GeminiManager(this)
    }

    private fun setupSendButton() {
        sendButton!!.setOnClickListener { v: View? -> sendMessage() }
    }

    private fun sendMessage() {
        val messageText = messageInput!!.text.toString().trim { it <= ' ' }
        if (!messageText.isEmpty()) {
            // Add and save user message
            val userMessage = Message(messageText, true)
            chatAdapter.addMessage(userMessage)
            messageInput!!.setText("")

            // Generate bot response using Gemini
            geminiManager.generateResponse(messageText, object : ResponseCallback() {
                fun onResponse(response: String?) {
                    runOnUiThread {
                        val botMessage = Message(response, false)
                        chatAdapter.addMessage(botMessage)
                        chatRecyclerView!!.smoothScrollToPosition(chatAdapter.getItemCount() - 1)
                    }
                }

                fun onError(error: String?) {
                    runOnUiThread {
                        Toast.makeText(this@ChatActivity, error, Toast.LENGTH_SHORT).show()
                        val errorMessage = Message(
                            "Sorry, I'm having trouble responding right now.", false
                        )
                        chatAdapter.addMessage(errorMessage)
                    }
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (databaseHelper != null) {
            databaseHelper.close()
        }
    }
}