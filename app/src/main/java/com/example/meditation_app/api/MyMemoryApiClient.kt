package com.example.meditation_app.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject

object MyMemoryApiClient {
    private const val BASE_URL = "https://api.mymemory.translated.net/get"

    // Логування HTTP-запитів для налагодження
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging) // Додаємо логування
        .build()

    // Метод перекладу
    suspend fun translate(text: String, source: String = "en", target: String = "uk"): String {
        val url = "$BASE_URL?q=${text}&langpair=${source}|${target}"
        println("Request URL: $url")

        return withContext(Dispatchers.IO) { // Виконання запиту в IO-потоці
            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                println("Response Body: $responseBody")

                if (response.isSuccessful && responseBody != null) {
                    val json = JSONObject(responseBody)
                    if (json.has("responseData")) {
                        json.getJSONObject("responseData").getString("translatedText")
                    } else {
                        "Помилка: responseData відсутній у відповіді"
                    }
                } else {
                    "Помилка: HTTP ${response.code}"
                }
            } catch (e: Exception) {
                "Помилка: ${e.message}"
            }
        }
    }
}
