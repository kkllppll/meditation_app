package com.example.meditation_app.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

// Дані для окремої цитати
data class Quote(
    val body: String,
    val author: String
)

// Відповідь від API (список цитат)
data class FavQsQuoteResponse(
    val quotes: List<Quote>
)

// Інтерфейс для роботи з FavQs API
interface FavQsApi {
    @Headers("Authorization: Token token=\"62efa4634f78d0360c697b2a004283f8\"")
    @GET("quotes")
    suspend fun getQuotesByCategory(@Query("filter") category: String): FavQsQuoteResponse
}

// Клієнт для роботи з Retrofit
object FavQsApiClient {
    private const val BASE_URL = "https://favqs.com/api/"

    val api: FavQsApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FavQsApi::class.java)
    }
}
