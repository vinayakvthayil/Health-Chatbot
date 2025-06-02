package com.example.healthi.utils


import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OnlineSearchHelper {
    private val client = HttpClient()

    suspend fun search(queries: List<String>): String = withContext(Dispatchers.IO) {
        val combined = queries.joinToString(" ")
        val apiKey = "YOUR_BRAVE_SEARCH_API_KEY"
        val url = "https://api.search.brave.com/res/v1/web/search?q=${combined}&limit=3"
        val response: Map<String, Any> = client.get(url) {
            header("Accept", "application/json")
            header("X-Beam-API-Key", apiKey)
        }
        return@withContext response["data"].toString()
    }
}
