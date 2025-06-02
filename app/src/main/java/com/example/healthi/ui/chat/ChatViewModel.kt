package com.example.healthi.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthi.model.MediaPipeLLMHelper
import com.example.healthi.model.OnlineSearchHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<String>>(emptyList())
    val messages: StateFlow<List<String>> = _messages

    private val onDeviceHelper = MediaPipeLLMHelper()
    private val searchHelper = OnlineSearchHelper()

    fun onUserMessage(input: String) {
        _messages.value = _messages.value + "User: $input"
        viewModelScope.launch {
            val subQueries = onDeviceHelper.generateSubQueries(input)
            val searchContext = if (subQueries.isNotEmpty()) {
                searchHelper.search(subQueries)
            } else ""
            val answer = onDeviceHelper.generateAnswer(input, searchContext)
            _messages.value = _messages.value + "Assistant: $answer"
            onDeviceHelper.exportSummary(answer)
        }
    }
}
