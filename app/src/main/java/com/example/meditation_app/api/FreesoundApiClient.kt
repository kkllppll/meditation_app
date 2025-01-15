package com.example.meditation_app.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// модель для збереження деталей звуку
data class SoundDetails(
    val id: Int,
    val name: String,
    val previews: Map<String, String> // мапа із доступними URL
)

// інтерфейс для роботи з Freesound API
interface FreesoundApi {
    @GET("sounds/{id}/")
    suspend fun getSoundDetails(
        @Path("id") soundId: Int, // передаємо ID звуку
        @Query("token") token: String // API-ключ
    ): SoundDetails
}

// Об'єкт для роботи з Freesound API
object FreesoundApiClient {
    private const val BASE_URL = "https://freesound.org/apiv2/" // базова URL для API Freesound

    // налаштування OkHttpClient
    private val client = OkHttpClient.Builder().build()

    // ініціалізація Retrofit
    val api: FreesoundApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // використовуємо налаштований OkHttpClient
            .addConverterFactory(GsonConverterFactory.create()) // конвертер для JSON
            .build()
            .create(FreesoundApi::class.java) // створюємо API-клієнт
    }
}
