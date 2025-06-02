package com.example.healthi.chat

import java.util.Date

data class Message(
    val id: Long,
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Date
)
